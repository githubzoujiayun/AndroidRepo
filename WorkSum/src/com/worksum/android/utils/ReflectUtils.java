package com.worksum.android.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author chao.qin
 *
 * 2016/03/15
 */
public class ReflectUtils {


    /**
     * 使用反射调用方法
     *
     * @param o 被调用对象
     * @param methodName 被调用对象的方法名称
     * @param args 被调用方法所需传入的参数列表
     */
    public static <T> T callMethod(Object o,String methodName,Object... args) {
        T result = null;
        try {
            Class c = o.getClass();
            Method m = c.getDeclaredMethod(methodName);
            m.setAccessible(true);
            result = (T)m.invoke(o,args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 使用反射设置变量值
     *
     * @param o 被调用对象
     * @param fieldName 被调用对象的字段，一般是成员变量或静态变量，不可是常量！
     * @param value 值
     * @param <T> value类型，泛型
     */
    public static <T> void setField(Object o,String fieldName,T value) {
        try {
            Class c = o.getClass();
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(o, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用反射获取变量值
     *
     * @param o 被调用对象
     * @param fieldName 被调用对象的字段，一般是成员变量或静态变量，也可以是常量
     * @param <T> 返回类型，泛型
     * @return 值
     */
    public static <T> T getField(Object o,String fieldName) {
        T value = null;
        try {
            Class c = o.getClass();
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            value = (T) f.get(o);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }
}
