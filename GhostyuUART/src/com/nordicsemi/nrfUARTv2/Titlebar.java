package com.nordicsemi.nrfUARTv2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Titlebar {
    int titleId() default 0;
    int leftTextId() default 0;
    int leftDrawableId() default 0;
    int rightTextId() default 0;
    int rightDrawableId() default 0;
}
