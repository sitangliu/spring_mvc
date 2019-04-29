package com.ijianghu.frame.spring.framework.core;

/**
 * @author kai on
 * @date 2018/5/25 0:06
 */
public interface JHBeanFactory {

    /**
     * 根据beanName从IOC容器之中获得一个实例Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
}
