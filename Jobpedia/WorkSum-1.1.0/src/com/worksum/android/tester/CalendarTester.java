package com.worksum.android.tester;

import android.test.AndroidTestCase;

import com.jobs.lib_v1.app.AppUtil;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chao on 2016/11/7.
 */

public class CalendarTester extends TestCase{

    public void testCalender(){
        Calendar c = Calendar.getInstance();


    }

    public void testDateFormat() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.CHINESE);
        Date date = format.parse("2016-11");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        System.out.println(calendar.get(Calendar.YEAR) + ":" + calendar.get(Calendar.MONTH));

    }
}
