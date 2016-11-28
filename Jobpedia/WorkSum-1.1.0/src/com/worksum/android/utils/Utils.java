package com.worksum.android.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author chao.qin
 *         <p/>
 *         16/10/13
 */
public final class Utils {

    private static final String PHONE_REGEX = "[5,6,8,9][0-9]{7}";

    public static boolean matchesPhone(String phoneNumber) {
        Pattern p = Pattern.compile(PHONE_REGEX);
        return p.matcher(phoneNumber).matches();
    }

    public static String dateFormat(String dateText) {

        if (TextUtils.isEmpty(dateText)) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINESE);
        Date date;
        try {
            date = dateFormat.parse(dateText);
        } catch (ParseException e) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        return year + "-" + month;
    }

    public static String emptyValue(String mayNullValue) {
        if (mayNullValue == null) {
            return "";
        }
        return mayNullValue;
    }
}
