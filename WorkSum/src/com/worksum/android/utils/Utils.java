package com.worksum.android.utils;

import java.util.regex.Pattern;

/**
 * @author chao.qin
 *         <p/>
 *         16/10/13
 */
public class Utils {

    private static final String PHONE_REGEX = "[5,6,8,9][0-9]{7}";

    public static boolean matchesPhone(String phoneNumber) {
        Pattern p = Pattern.compile(PHONE_REGEX);
        return p.matcher(phoneNumber).matches();
    }
}
