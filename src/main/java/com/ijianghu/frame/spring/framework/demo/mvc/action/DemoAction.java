package com.ijianghu.frame.spring.framework.demo.mvc.action;



import com.ijianghu.frame.spring.framework.Annotation.JHAutowired;
import com.ijianghu.frame.spring.framework.Annotation.JHController;
import com.ijianghu.frame.spring.framework.Annotation.JHRequestMapping;
import com.ijianghu.frame.spring.framework.Annotation.JHRequestParam;
import com.ijianghu.frame.spring.framework.demo.service.IDemoService;
import com.ijianghu.frame.spring.framework.webmvc.servlet.JHModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author kai on
 * @date 2018/5/24 22:23
 */
@JHController
@JHRequestMapping("/demo")
public class DemoAction {

    @JHAutowired
    private IDemoService demoService;

    @JHRequestMapping("/main.json")
    public JHModelAndView main(HttpServletRequest req, HttpServletResponse resp,
                     @JHRequestParam("name") String name){
        System.out.println(name+"开始调用service方法");
        String result = demoService.getNames(name);
        return out(resp,result);

    }

    @JHRequestMapping("/query.shtml")
    public JHModelAndView query(HttpServletRequest req, HttpServletResponse resp,
                                @JHRequestParam("name") String name ,@JHRequestParam("data") String date){
        demoService.getNames(name);
        HashMap<String, String> model = new HashMap<String, String>();
        model.put("name",name);
        model.put("date",date);
        return new JHModelAndView("list.html",model);
    }

    private JHModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
