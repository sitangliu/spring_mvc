package com.ijianghu.frame.spring.servlet;

import com.ijianghu.frame.spring.framework.Annotation.JHController;
import com.ijianghu.frame.spring.framework.Annotation.JHRequestMapping;
import com.ijianghu.frame.spring.framework.Annotation.JHRequestParam;
import com.ijianghu.frame.spring.framework.content.support.JHApplicationContext;
import com.ijianghu.frame.spring.framework.webmvc.JHHandlerAdapter;
import com.ijianghu.frame.spring.framework.webmvc.JHHandlerMapping;
import com.ijianghu.frame.spring.framework.webmvc.JHViewResolver;
import com.ijianghu.frame.spring.framework.webmvc.servlet.JHModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author kai on
 * @date 2018/5/24 23:43
 */
//Servlet只是作为一个MVC的启动入口
public class JHDispatcherServlet extends HttpServlet{

    private  final String LOCATION = "contextConfigLocation";

    //    private Map<String,GPHandlerMapping> handlerMapping = new HashMap<String,GPHandlerMapping>();

    //课后再去思考一下这样设计的经典之处
    //GPHandlerMapping最核心的设计，也是最经典的
    //它牛B到直接干掉了Struts、Webwork等MVC框架
    private List<JHHandlerMapping> handlerMappings = new ArrayList<JHHandlerMapping>();

    private Map<JHHandlerMapping,JHHandlerAdapter> handlerAdapters = new HashMap<JHHandlerMapping,JHHandlerAdapter>();

    private List<JHViewResolver> viewResolvers = new ArrayList<JHViewResolver>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化容器

        //相当于初始化IOC容器
        JHApplicationContext context = new JHApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //        String url = req.getRequestURI();
//        String contextPath = req.getContextPath();
//        url = url.replace(contextPath,"").replaceAll("/+","/");
//        GPHandlerMapping handler = handlerMapping.get(url);

//        try {
//            GPModelAndView mv = (GPModelAndView)handler.getMethod().invoke(handler.getController());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }


        //对象.方法名才能调用
        //对象要从IOC容器中获取
//        method.invoke(context.);


        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception"+e.getLocalizedMessage());
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

            //根据用户的url来获得一个 handler  (controller method)
            JHHandlerMapping handler = getHandler(req);
            if(handler == null){
                resp.getWriter().write("404 Not Found \r\n @iJiangHuMVC");
            }


            JHHandlerAdapter ha = getHandlerAdapter(handler);

            //这一步只是调用方法，得到返回值
            JHModelAndView mv = ha.handle(req,resp,handler);

            processDispatchResult(resp,mv);
    }

    private void processDispatchResult(HttpServletResponse resp, JHModelAndView mv) throws Exception{

        //调用viewResolver的resolveView方法
        if(null == mv){return;}
        if(this.viewResolvers.isEmpty()){return;}

        for (JHViewResolver viewResolver:this.viewResolvers) {
            if(!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }
            String out = viewResolver.viewResolver(mv);
            if(out != null){
                resp.getWriter().write(out);
                break;
            }else{
                resp.getWriter().write("404 Page Not Found \r\n @iJiangHuMVC");
            }
        }

    }

    private JHHandlerAdapter getHandlerAdapter(JHHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(handler);


    }

    private JHHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings == null){
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for (JHHandlerMapping handlerMapping: this.handlerMappings) {
            if(handlerMapping.getPattern().matcher(url).find()){
                return  handlerMapping;
            }else{
                continue;
            }
        }
        return null;
    }

    protected void initStrategies(JHApplicationContext context) {

        //有九种策略
        // 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
        // 每种策略可以自定义干预，但是最终的结果都是一致
        // ModelAndView

        // =============  这里说的就是传说中的九大组件 ================
        initMultipartResolver(context);//文件上传解析，如果请求类型是multipart将通过MultipartResolver进行文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析

        /** 我们自己会实现 */
        //GPHandlerMapping 用来保存Controller中配置的RequestMapping和Method的一个对应关系
        initHandlerMappings(context);//通过HandlerMapping，将请求映射到处理器
        /** 我们自己会实现 */
        //HandlerAdapters 用来动态匹配Method参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配

        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver来解析
        initRequestToViewNameTranslator(context);//直接解析请求到视图名

        /** 我们自己会实现 */
        //通过ViewResolvers实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver解析逻辑视图到具体视图实现

        initFlashMapManager(context);//flash映射管理器

    }

    //将Controller中配置的RequestMapping和Method进行一一对应
    private void initHandlerMappings(JHApplicationContext context) {

        //按照我们通常的理解应该是一个Map
        //Map<String,Method> map;
        //map.put(url,Method)

        //首先从容器中取到所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            //但不是所有的牛奶都是特仑苏
            if(!clazz.isAnnotationPresent(JHController.class)){continue;}

            String baseUrl = "";

            if(clazz.isAnnotationPresent(JHRequestMapping.class)){
                JHRequestMapping requestMapping = clazz.getAnnotation(JHRequestMapping.class);
                baseUrl = requestMapping.value();

            }

            //扫描所有的public方法
            Method[] methods = clazz.getMethods();
            for (Method method:methods) {
                if(!method.isAnnotationPresent(JHRequestMapping.class)){continue;}

                JHRequestMapping requestMapping = method.getAnnotation(JHRequestMapping.class);

                String regex = (baseUrl+requestMapping.value().replaceAll("\\*",".*"));
                Pattern pattern = Pattern.compile(regex);

                this.handlerMappings.add(new JHHandlerMapping(pattern,controller,method));

                System.out.println("Mapping "+regex+","+method);
            }
        }

    }

    private void initHandlerAdapters(JHApplicationContext context) {
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置 inedx，挨个从数组中添值，这样的话，就和参数的顺序无关了

        for (JHHandlerMapping handlerMapping:this.handlerMappings) {

            //每一个方法有一个参数列表，那么这里保存的是形参列表
            HashMap<String, Integer> paramMapping = new HashMap<String, Integer>();

            //这里只是出来了命名参数
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0; i<pa.length; i++){
                for (Annotation a : pa[i]) {
                    if(a instanceof JHRequestParam){
                        String paramName = ((JHRequestParam) a).value();
                        if(!"".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }

                }
            }

            //接下来，我们处理非命名参数
            //只处理Request和Response
            Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
            for(int i = 0;i<paramTypes.length; i++){
                Class<?> type = paramTypes[i];
                if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }

            this.handlerAdapters.put(handlerMapping,new JHHandlerAdapter(paramMapping));
        }

    }

    private void initViewResolvers(JHApplicationContext context) {

        //在页面敲一个http://localhost/first.html
        //解决页面名字和模板文件相关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File template: templateRootDir.listFiles()) {
            this.viewResolvers.add(new JHViewResolver(template.getName(),template));

        }

    }



    private void initFlashMapManager(JHApplicationContext context) { }

    private void initRequestToViewNameTranslator(JHApplicationContext context) { }

    private void initHandlerExceptionResolvers(JHApplicationContext context) { }

    private void initThemeResolver(JHApplicationContext context) { }

    private void initLocaleResolver(JHApplicationContext context) { }

    private void initMultipartResolver(JHApplicationContext context) { }
}
