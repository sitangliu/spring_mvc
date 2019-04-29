package com.ijianghu.frame.spring.framework.content.support;

import com.ijianghu.frame.spring.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author kai on
 * @date 2018/5/27 12:31
 */
//用来对配置文件进行查找、读取和解析
public class JHBeanDefinitionReader {

    private Properties config = new Properties();

    private List<String>   registerBeanClasses = new ArrayList<String>();

    //在配置文件中，用来获取自动扫描的包名称的key
    private final String SCAN_PACKAGE = "scanPackage";

    public JHBeanDefinitionReader(String ... locations){
        //在Spring中是通过Reader去查找和定位对不对
              //此处location 需要完善成数组
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != is) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //加载资源信息
        doScanner(config.getProperty(this.SCAN_PACKAGE));
    }


    public List<String> loadBeanDefinitions(){
        return this.registerBeanClasses;
    }

    //每注册一个className,就返回一个BeanDefinition，我自己包装
    //只是为了对配置信息进行一个包装
    public BeanDefinition rigisterBean(String className){

        if(this.registerBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
            return  beanDefinition;
        }

        return null;
    }

    private String lowerFirstCase(String key) {
        char[] chars = key.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }

    //递归扫描所有的相关联的class，并且保存到一个list中
    public void doScanner(String packageName){
        //获取到 要扫描的资源路径
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));

        File classFile = new File(url.getFile());

        for (File file: classFile.listFiles()) {
            if(file.isDirectory()){
                //是目录，继续扫描
                doScanner(packageName+"."+file.getName());
            }else{
                //是文件  获取到类的全名称
                this.registerBeanClasses.add(packageName+"."+file.getName().replace(".class",""));
            }

        }
    }


    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }
}
