package com.ijianghu.frame.spring.framework.Annotation;

import java.lang.annotation.*;

/**
 * @author kai on
 * @date 2018/5/24 20:55
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JHRequestMapping {

    String value() default  "";
}
