package com.xiaojm.annotation;

import java.lang.annotation.*;

/**
 * Created by xiaojm
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogger {
    String value() default "";
}
