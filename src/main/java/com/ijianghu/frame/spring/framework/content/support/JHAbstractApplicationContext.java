package com.ijianghu.frame.spring.framework.content.support;

/**
 * @author kai on
 * @date 2018/5/25 0:10
 */
public abstract class JHAbstractApplicationContext {

    //提供给子类重写
    protected void onRefresh(){
        // For subclasses: do nothing by default.
    }

    protected abstract void refreshBeanFactory();
}
