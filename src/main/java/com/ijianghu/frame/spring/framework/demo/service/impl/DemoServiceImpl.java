package com.ijianghu.frame.spring.framework.demo.service.impl;


import com.ijianghu.frame.spring.framework.Annotation.JHService;
import com.ijianghu.frame.spring.framework.demo.service.IDemoService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kai on
 * @date 2018/5/24 22:25
 */
@JHService
public class DemoServiceImpl implements IDemoService {
    @Override
    public String getNames(String name) {
        System.out.println("调用了service方法");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String result = "name = "+name+" time = "+time;

        return result;
    }
}
