package com.worksum.android.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataManagerReg {

    String[] actions() default {""};

    RegisterType register() default RegisterType.PART;

    public enum RegisterType { ALL,PART}
}
