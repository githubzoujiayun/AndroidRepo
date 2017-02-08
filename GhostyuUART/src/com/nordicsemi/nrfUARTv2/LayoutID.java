package com.nordicsemi.nrfUARTv2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用来注解定义布局文件
 *
 * @since jobpadia 1.0.6 2016/6/1
 *
 * @author chao.qin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LayoutID {
    int value() default 0;
}
