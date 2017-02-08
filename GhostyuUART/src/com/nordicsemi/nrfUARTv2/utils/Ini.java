package com.nordicsemi.nrfUARTv2.utils;

import com.nordicsemi.nrfUARTv2.UART;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author chao.qin
 * @since 2017/1/11
 */

public class Ini {

    private  static IniReader mReader;

    public static final String SECTION_CHANNEL = "channels";
    public static final String SECTION_UNIT = "units";

    public static void load() {
        try {
            InputStream is = UART.getApp().getAssets().open("config.dat");
            mReader = new IniReader(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T getValue(String section,String key, T defaultValue) {
        T t = (T) mReader.getValue(section,key);
        if (t == null) {
            t = defaultValue;
        }
        return t;
    }

    public static <T> T matchValue(String section,String key, T defaultValue) {
        Properties properties = mReader.getProperties(section);
        Enumeration<Object> keys = properties.keys();
        while(keys.hasMoreElements()) {
            Object element = keys.nextElement();
            if (element == null) {
                element = "";
            }
            Pattern pattern = Pattern.compile(element.toString());
            if (pattern.matcher(key).matches()) {
                return (T)properties.get(key);
            }

        }
        return (T)null;
    }
}
