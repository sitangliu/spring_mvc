package com.ijianghu.frame.spring.framework.webmvc.servlet;

import java.util.Map;

/**
 * @author kai on
 * @date 2018/5/27 23:26
 */
public class JHModelAndView {

    private String viewName;
    private Map<String,?> model;
    public JHModelAndView(String viewName, Map<String,?> model){
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
