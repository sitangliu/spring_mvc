package com.ijianghu.frame.spring.framework.beans;

import com.ijianghu.frame.spring.framework.core.JHFactoryBean;

/**
 * @author kai on
 * @date 2018/5/27 12:34
 */
public class BeanWrapper extends JHFactoryBean{

    //还会用到 观察者 模式
    //1、支持事件响应，还有一个监听
    private BeanPostProcessor beanPostProcessor;
    private Object wrapperInstance;

   //原始的通过反射new出来，要把包装起来，存下来
    private Object originalInstance;

    public BeanWrapper(Object instance){
        this.wrapperInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    //返回代理以后的Class
    //k可能会是这个$Proxy@###
    public Object getOriginalInstance() {
        return originalInstance;
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }
}
