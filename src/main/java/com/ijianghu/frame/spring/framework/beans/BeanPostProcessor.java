package com.ijianghu.frame.spring.framework.beans;

/**
 * @author kai on
 * @date 2018/5/27 16:29
 */

//用做事件监听的
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean,String beanName){

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName){

        return bean;
    }
}
