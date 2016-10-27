package com.worksum.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.worksum.android.R;


/**
 * 头像视图
 * chao.qin 2016/1/6
 */
public class HeaderIconView extends View {

    private String mImageUrl;

    private Bitmap mImageBitmap;

    private boolean mWithAdd;

    private int mDefaultHead;

    public HeaderIconView(Context context) {
        this(context,null);
    }

    public HeaderIconView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeaderIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.jobpedia);
        mWithAdd = a.getBoolean(R.styleable.jobpedia_withAdd,false);
        mDefaultHead = a.getResourceId(R.styleable.jobpedia_headIcon, R.drawable.ico_default_head);
        a.recycle();
        initView(context);
    }

    private void initView(Context context) {

    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        setImageBitmap(BitmapFactory.decodeFile(mImageUrl));
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageBitmap = bitmap;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (mImageBitmap == null) {
            mImageBitmap = BitmapFactory.decodeResource(getResources(), mDefaultHead);
        }

        Bitmap meAdd = BitmapFactory.decodeResource(getResources(), R.drawable.me_add);

        int radius = getWidth() / 2;
        if(mWithAdd) {
            radius -= meAdd.getWidth() / 2;
        }
        Bitmap target = drawHeaderIcon(mImageBitmap, radius);

        canvas.drawBitmap(target, 0, 0, paint);

        if (mWithAdd) {
            canvas.drawBitmap(meAdd, radius * 2 - (meAdd.getWidth() / 2), radius, paint);
        }
    }

    private Bitmap drawHeaderIcon(Bitmap source,int radius) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
         /**
         * 首先绘制圆形
         */
        canvas.drawCircle(radius, radius, radius, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source,null,new Rect(0,0,target.getWidth(),target.getHeight()),paint);
        return target;
    }

    public void resetHeadIcon() {
        mImageBitmap = null;
        postInvalidate();
    }
}
