package com.ijianghu.frame.spring.framework.beans;

/**
 * @author kai on
 * @date 2018/5/27 12:34
 */

//用来存储配置文件中的信息
    //相当于内存中的配置信息
public class BeanDefinition {

    private String beanClassName;

    private String factoryBeanName;

    private boolean lazyInit = false;

    public void setBeanClassName(String beanClassName){
            this.beanClassName = beanClassName;
    }

    public String getBeanClassName(){

        return beanClassName;
    }

    public  void setFactoryBeanName(String factoryBeanName){
            this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName(){

        return  factoryBeanName;
    }

    public  void setLazyInit(boolean lazyInit){
        this.lazyInit = lazyInit;
    }

    public boolean getLazyInit(){

        return false;
    }

}
