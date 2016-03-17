package com.android.worksum.utils;

import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * @author chao.qin
 *
 * 2016/03/15
 */
public class ReflectUtils {


    public static <T> void setField(Object object,String key,T value) {
        try {
            Field field = object.getClass().getDeclaredField(key);//mOverscrollDistance
            field.setAccessible(true);
            field.set(object,value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
