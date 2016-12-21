package com.worksum.android.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.device.Storage;
import com.worksum.android.R;

import java.io.File;
import java.io.IOException;
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

    public static final String TEMPLATE_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String TEMPLATE_YEAR_MONTH = "yyyy-MM";
    public static final String TEMPLATE_FULL_DATE = "yyyy-MM-dd HH-mm-ss";

    private static final String PHONE_REGEX = "[5,6,8,9][0-9]{7}";


    public static final int REQUEST_CODE_CROP_IMAGE = 1;
    public static final int REQUEST_CODE_PICK_PHOTO = 2;

    public static final int SOUND_NOTIFY_MSG = 1;
    private static final int SOUND_IDS[] = new int[] {R.raw.msg};
    private static SoundPool mSp = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
    static {
        mSp.load(AppMain.getApp(),SOUND_IDS[0],1);
    }

    public static boolean matchesPhone(String phoneNumber) {
        Pattern p = Pattern.compile(PHONE_REGEX);
        return p.matcher(phoneNumber).matches();
    }


    public static boolean matchesEmail(String emailAddress) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    public static int[] stringToTimeArray(String timeText) {
        if (TextUtils.isEmpty(timeText) || !timeText.contains(":")) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new int[]{hour,minute};
        }
        String time[] = timeText.split(":");
        int length = time.length;
        int[] timeInt = new int[length];
        for (int i=0;i<length;i++) {
            timeInt[i] = Integer.parseInt(time[i].trim());
        }
        return timeInt;
    }

    public static int[] stringToDateArray(String dateText) {
        return stringToDateArray(dateText,TEMPLATE_FULL_DATE);
    }

    public static int[] stringToDateArray(String dateText,String template) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(template, Locale.CHINESE);
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
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return new int[]{year, month, day, hour, min, second};
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



    public static Uri startImageZoom(Fragment fragment, Uri uri) {
        return startImageZoom(fragment,uri,1,1);
    }
        /**
         * 系统裁剪
         **/
    public static Uri startImageZoom(Fragment fragment, Uri uri,int aspectX,int aspectY) {
        int dp = 500;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);//输出是X方向的比例
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", dp);//输出X方向的像素
        intent.putExtra("outputY", dp);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        Uri tempUri = null;
        try {
            File tempFile = File.createTempFile("temp",".jpg", new File(Storage.getAppImageCacheDir()));
            tempUri = Uri.fromFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("return-data", true);//设置是否返回数据
        fragment.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        return tempUri;
    }

    public static String formatTimeOrDate(String start, String end) {
        StringBuilder sbuilder = new StringBuilder();
        if (!TextUtils.isEmpty(start)) {
            sbuilder.append(start);
        }
        if (!TextUtils.isEmpty(end)) {
            if (sbuilder.length() > 0) {
                sbuilder.append(" - ");
            }
            sbuilder.append(end);
        }
        return sbuilder.toString();
    }

    public static void playSound(int soundId) {
        AudioManager am = (AudioManager) AppMain.getApp().getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        mSp.play(soundId,
                volumnRatio,// 左声道音量
                volumnRatio,// 右声道音量
                1, // 优先级
                0,// 循环播放次数
                1);// 回放速度，该值在0.5-2.0之间 1为正常速度
        AppUtil.print("playSound succed!");
    }

}
