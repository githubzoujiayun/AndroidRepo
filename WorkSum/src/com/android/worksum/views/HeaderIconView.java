package com.android.worksum.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.android.worksum.R;

/**
 * 头像视图
 * chao.qin 2016/1/6
 */
public class HeaderIconView extends View {

    private String mImageUrl;

    private Bitmap mImageBitmap;

    public HeaderIconView(Context context) {
        super(context);
    }

    public HeaderIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {

    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        mImageBitmap = BitmapFactory.decodeFile(mImageUrl);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (mImageBitmap == null) {
            mImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_self_icon);
        }

        int radius = getWidth() / 2;
        Bitmap target = drawHeaderIcon(mImageBitmap,radius);
        canvas.drawBitmap(target,0,0,paint);

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
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
