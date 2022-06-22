package com.iwdael.dblite.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface QuerySet {
    String[] value() default null
}