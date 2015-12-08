package com.jobs.lib_v1.misc;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Build;

import com.jobs.lib_v1.app.AppMain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 压缩Bitmap，Bitmap转bytes，bytes转Bitmap
 * 
 * @author solomon.wen
 * @date 2014-10-20
 */
public class BitmapUtil {
	public static final int DEFAULT_BITMAP_QUALITY = 90; // 默认位图质量，取值范围: 0 -> 100

    /**
     * 从资源中获取Bitmap对象，通过计算当前内存大小，最大限度避免OOM
     *
     * @param resID 图片资源ID
     * @return Bitmap 对应资源ID的Bitmap对象
     */
    public static Bitmap getBitmapForResourceID(int resID) {
    	return getBitmapForResourceID(resID, -1, -1);
    }

    /**
     * 从资源中获取Bitmap对象，通过计算当前内存大小，最大限度避免OOM
     * 
     * @param resID 图片资源ID
     * @param maxHeight 最大解码高度
     * @param maxWidth 最大解码宽度
     * @return Bitmap 对应资源ID的Bitmap对象
     */
    public static Bitmap getBitmapForResourceID(int resID, int maxHeight, int maxWidth) {
        BitmapFactory.Options opts = getBitmapOptionsForResourceID(resID, maxHeight, maxWidth);
        Bitmap bitmap = null;

        if (null == opts){
            return null;
        }

        // 设置图片可以被回收
        opts.inPurgeable = true;

        // 对象引用是否可分享 只有inPurgeable为true时才生效
        opts.inInputShareable = true;

        try {
            InputStream is = AppMain.getApp().getResources().openRawResource(resID);
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (Throwable e) {
            bitmap = null;
        }

        return bitmap;
    }

	/**
	 * 从图片字节数组中获取位图对象
	 * 
	 * @param imageBytes 图片字节数组
	 * @param maxHeight 最大解码高度
	 * @param maxWidth 最大解码宽度
	 * @return 位图对象
	 */
	public static Bitmap getBitmapForBytes(byte[] imageBytes, int maxHeight, int maxWidth) {
		BitmapFactory.Options opts = getBitmapOptionsForBytes(imageBytes, maxHeight, maxWidth);
		if(null == opts){
			return null;
		}

		Bitmap bitmap = getBitmapForBytesWidthOpts(imageBytes, opts);
		if (null != bitmap) {
			return bitmap;
		}

		recycleUnusedMemory();

		// =================
		// 尝试更换解码方式
		// =================
		opts.inSampleSize++; // 增大抽样率，继续降低图片的清晰度

		opts.inJustDecodeBounds = false;
		opts.inDither = false; // 不进行图片抖动处理
		opts.inPreferredConfig = null; // 设置让解码器以最佳方式解码

		bitmap = getBitmapForBytesWidthOpts(imageBytes, opts);
		if (null != bitmap) {
			return bitmap;
		}

		recycleUnusedMemory();

		// =================
		// 尝试在sdcard开辟空间存储内存
		// =================
		opts.inSampleSize++; // 增大抽样率，继续降低图片的清晰度

		int width = opts.outWidth / opts.inSampleSize;
		int height = opts.outHeight / opts.inSampleSize;
		int bitmapSize = (int) Math.ceil((double) getMemorySizeForBitmap(width, height, opts.inPreferredConfig));

		opts.inTempStorage = new byte[bitmapSize];
		opts.inJustDecodeBounds = false;
		opts.inDither = false; // 不进行图片抖动处理
		opts.inPreferredConfig = null; // 设置让解码器以最佳方式解码

		return getBitmapForBytesWidthOpts(imageBytes, opts);
	}

	/**
	 * 获取位图大小字节数
	 */
	public static int getBitmapSize(BitmapDrawable value) {
		Bitmap bitmap = value.getBitmap();
		return getBitmapSize(bitmap);
	}

	/**
	 * 获取位图大小字节数
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static int getBitmapSize(Bitmap value) {
		if (null == value) {
			return 0;
		}

		if (SdkUtil.isApi12Plus()) {
			return value.getByteCount();
		}

		return value.getRowBytes() * value.getHeight();
	}

	/**
	 * 获取一个图片路径对应位图对象的字节数据
	 * 
	 * @param pathName 图片路径
	 * @param maxHeight 最大编码高度
	 * @param maxWidth 最大编码宽度
	 * @param maxBytesSize 目标字节数据最大的大小
	 * @param bitmapQuality 目标JPEG图片的质量 (0-100)
	 * @return byte[] 字节数据
	 */
	public static byte[] getBitmapBytesForPath(String pathName, int maxHeight, int maxWidth, int maxBytesSize, int bitmapQuality) {
		Bitmap srcBitmap = getBitmapForPath(pathName, maxHeight, maxWidth);
		int srcDegree = getBitmapDegree(pathName);
        byte[] retBytes = getBitmapBytes(srcBitmap, srcDegree, maxHeight, maxWidth, maxBytesSize, bitmapQuality);

        if(null != srcBitmap) {
            srcBitmap.recycle();
        }

        return retBytes;
	}

    /**
     * 获取一个图片路径对应位图对象的字节数据(处理图片横屏)
     *
     * @param pathName 图片路径
     * @param maxHeight 最大编码高度
     * @param maxWidth 最大编码宽度
     * @param maxBytesSize 目标字节数据最大的大小
     * @return byte[] 字节数据
     */
    public static byte[] getBitmapBytesForPath(String pathName, int maxHeight, int maxWidth, int maxBytesSize) {
        Bitmap srcBitmap = getBitmapForPath(pathName, maxHeight, maxWidth);
        byte[] retBytes = getBitmapBytes(srcBitmap, maxHeight, maxWidth, maxBytesSize);

        if(null != srcBitmap) {
            srcBitmap.recycle();
        }

        return retBytes;
    }

	/**
     * 获取一个位图对象的字节数据
	 * 
	 * @param srcBitmap 位图对象
	 * @param maxHeight 最大编码高度
	 * @param maxWidth 最大编码宽度
	 * @param maxBytesSize 目标字节数据最大的大小
	 * @param bitmapQuality 目标JPEG图片的质量 (0-100)
	 * @return byte[] 字节数据
     */
	public static byte[] getBitmapBytes(Bitmap srcBitmap, int maxHeight, int maxWidth, int maxBytesSize, int bitmapQuality) {
		return getBitmapBytes(srcBitmap, 0, maxHeight, maxWidth, maxBytesSize, bitmapQuality);
	}

	/**
	 * 获取一个位图对象的字节数据
	 * 
	 * @param srcBitmap 位图对象
	 * @param srcDegree 设置旋转角度
	 * @param maxHeight 最大编码高度
	 * @param maxWidth 最大编码宽度
	 * @param maxBytesSize 目标字节数据最大的大小
	 * @param bitmapQuality 目标JPEG图片的质量 (0-100)
	 * @return 字节数据
	 */
	private static byte[] getBitmapBytes(Bitmap srcBitmap, int srcDegree, int maxHeight, int maxWidth, int maxBytesSize, int bitmapQuality) {
		Bitmap dstBitmap = null;

		if (null == srcBitmap) {
			return null;
		}

		float height = srcBitmap.getHeight();
		float width = srcBitmap.getWidth();
		boolean matchedWidth = (maxWidth < 1) || width < maxWidth;
		boolean matchedHeight = (maxHeight < 1) || height < maxHeight;

		if (srcDegree == 0 && matchedWidth && matchedHeight) {
			dstBitmap = srcBitmap;
		} else {
			// 生成旋转缩放矩阵
			Matrix matrix = new Matrix();
			float scale = 1;

			// 计算基于最大高度的缩放比率
			if ((maxHeight > 0) && height > maxHeight) {
				scale = maxHeight / height;
				width = scale * width;
			}

			// 计算基于最大宽度的缩放比率
			if ((maxWidth > 0) && width > maxWidth) {
				float tmpScale = maxWidth / width;
				height = tmpScale * height;
				scale = scale * tmpScale;
			}

			// 设置矩阵缩放和旋转参数
			matrix.postRotate(srcDegree);
			matrix.postScale(scale, scale);

			try {
				// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
				dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
			} catch (Throwable e) {
				e.printStackTrace();
				dstBitmap = null;
			}

			if (null == dstBitmap) {
				return null;
			}
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] retBytes = null;
		int errorTimes = 0;

		if (bitmapQuality < 1) {
			bitmapQuality = DEFAULT_BITMAP_QUALITY;
		}

		if (bitmapQuality > 100) {
			bitmapQuality = 100;
		}

		while (errorTimes < 3) {
			try {
				baos.reset();
				dstBitmap.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, baos);
				retBytes = baos.toByteArray();
			} catch (Throwable e) {
				e.printStackTrace();
				retBytes = null;
				errorTimes++;
			}

			if (null == retBytes) {
				continue;
			}

			if (maxBytesSize < 1 || retBytes.length <= maxBytesSize) {
				break;
			}

			// 如果生成目标字节数大于额定字节数，则缩放图片至一定比率
			double imageNeedScale = Math.sqrt((double)(maxBytesSize - 1) / retBytes.length);
			retBytes = null;
			Bitmap tmpBitmap = getScaledBitmap(dstBitmap, (float) imageNeedScale);
			if (null == tmpBitmap) {
				break;
			}

			if (dstBitmap != srcBitmap) {
				dstBitmap.recycle();
			}

			dstBitmap = tmpBitmap;
		}

		if (dstBitmap != srcBitmap) {
			dstBitmap.recycle();
		}

		return retBytes;
	}

    /**
     * 获取一个位图对象的字节数据(处理图片横屏时设置旋转角度)
     *
     * @param srcBitmap 位图对象
     * @param maxHeight 最大编码高度
     * @param maxWidth 最大编码宽度
     * @param maxBytesSize 目标字节数据最大的大小
     * @return 字节数据
     */
    private static byte[] getBitmapBytes(Bitmap srcBitmap, int maxHeight, int maxWidth, int maxBytesSize) {
        Bitmap dstBitmap = null;
        int srcDegree = 0;
        int bitmapQuality = -1;

        if (null == srcBitmap) {
            return null;
        }

        float height = srcBitmap.getHeight();
        float width = srcBitmap.getWidth();
        if(width > height){
            srcDegree = 90;
        }
        boolean matchedWidth = (maxWidth < 1) || width < maxWidth;
        boolean matchedHeight = (maxHeight < 1) || height < maxHeight;

        if (srcDegree == 0 && matchedWidth && matchedHeight) {
            dstBitmap = srcBitmap;
        } else {
            // 生成旋转缩放矩阵
            Matrix matrix = new Matrix();
            float scale = 1;

            // 计算基于最大高度的缩放比率
            if ((maxHeight > 0) && height > maxHeight) {
                scale = maxHeight / height;
                width = scale * width;
            }

            // 计算基于最大宽度的缩放比率
            if ((maxWidth > 0) && width > maxWidth) {
                float tmpScale = maxWidth / width;
                height = tmpScale * height;
                scale = scale * tmpScale;
            }

            // 设置矩阵缩放和旋转参数
            matrix.postRotate(srcDegree);
            matrix.postScale(scale, scale);

            try {
                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
                dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            } catch (Throwable e) {
                e.printStackTrace();
                dstBitmap = null;
            }

            if (null == dstBitmap) {
                return null;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] retBytes = null;
        int errorTimes = 0;

        if (bitmapQuality < 1) {
            bitmapQuality = DEFAULT_BITMAP_QUALITY;
        }

        if (bitmapQuality > 100) {
            bitmapQuality = 100;
        }

        while (errorTimes < 3) {
            try {
                baos.reset();
                dstBitmap.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, baos);
                retBytes = baos.toByteArray();
            } catch (Throwable e) {
                e.printStackTrace();
                retBytes = null;
                errorTimes++;
            }

            if (null == retBytes) {
                continue;
            }

            if (maxBytesSize < 1 || retBytes.length <= maxBytesSize) {
                break;
            }

            // 如果生成目标字节数大于额定字节数，则缩放图片至一定比率
            double imageNeedScale = Math.sqrt((double)(maxBytesSize - 1) / retBytes.length);
            retBytes = null;
            Bitmap tmpBitmap = getScaledBitmap(dstBitmap, (float) imageNeedScale);
            if (null == tmpBitmap) {
                break;
            }

            if (dstBitmap != srcBitmap) {
                dstBitmap.recycle();
            }

            dstBitmap = tmpBitmap;
        }

        if (dstBitmap != srcBitmap) {
            dstBitmap.recycle();
        }

        return retBytes;
    }

	 /**
	  * 从原始位图对象指定缩放比率的位图对象
	  * 
	  * @param bitmap 原始位图对象
	  * @param scale 指定的缩放比率
	  * @return Bitmap 指定缩放比率的位图对象
	  */
	 public static Bitmap getScaledBitmap(Bitmap bitmap, float scale){
		 if(null == bitmap || !(bitmap instanceof Bitmap)){
			 return null;
		 }

	     // 生成旋转缩放矩阵
	     Matrix matrix = new Matrix();
	     Bitmap dstBitmap;

	     // 设置矩阵缩放和旋转参数
	     matrix.postScale(scale, scale);

	     try {
	         // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
	    	 dstBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	     } catch (Throwable e) {
	    	 dstBitmap = null;
	     }

	     return dstBitmap;
	 }

	 /**
	  * 获取图片需要旋转的度数
	  * 
	  * @param pathName 图片路径
	  * @return int 图片需要旋转的度数 (0, 90, 270三种情况)
	  */
	 public static int getBitmapDegree(String pathName) {
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(pathName);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			}
		} catch (Throwable e) {
		}

		return 0;
	}

	/**
	 * 从指定路径中载入位图，并根据系统内存、目标最大允许高宽等情况适配工厂参数
	 * 
	 * @param pathName 位图地址
	 * @return Bitmap 载入的位图对象
	 */
	public static Bitmap getBitmapForPath(String pathName) {
		return getBitmapForPath(pathName, -1, -1);
	}

	/**
	 * 从指定路径中载入位图，并根据系统内存、目标最大允许高宽、缩放要求等情况适配工厂参数
	 * 
	 * @param pathName 位图地址
	 * @param maxHeight 最大解码高度
	 * @param maxWidth 最大解码宽度
	 * @return Bitmap 载入的位图对象
	 */
	public static Bitmap getBitmapForPath(String pathName, int maxHeight, int maxWidth) {
		// 尝试获取位图工厂参数，获取失败则返回null
		BitmapFactory.Options opts = getBitmapOptionsForPath(pathName, maxHeight, maxWidth);
		if (null == opts) {
			return null;
		}

		Bitmap bitmap = getBitmapForPathWidthOpts(pathName, opts);
		if (null != bitmap) {
			return bitmap;
		}

		recycleUnusedMemory();

		// =================
		// 尝试更换解码方式
		// =================
		opts.inSampleSize++; // 增大抽样率，继续降低图片的清晰度

		opts.inJustDecodeBounds = false;
		opts.inDither = false; // 不进行图片抖动处理
		opts.inPreferredConfig = null; // 设置让解码器以最佳方式解码

		bitmap = getBitmapForPathWidthOpts(pathName, opts);
		if (null != bitmap) {
			return bitmap;
		}

		recycleUnusedMemory();

		// =================
		// 尝试在sdcard开辟空间存储内存
		// =================
		opts.inSampleSize++; // 增大抽样率，继续降低图片的清晰度

		int width = opts.outWidth / opts.inSampleSize;
		int height = opts.outHeight / opts.inSampleSize;
		int bitmapSize = (int) Math.ceil((double) getMemorySizeForBitmap(width, height, opts.inPreferredConfig));

		opts.inTempStorage = new byte[bitmapSize];
		opts.inJustDecodeBounds = false;
		opts.inDither = false; // 不进行图片抖动处理
		opts.inPreferredConfig = null; // 设置让解码器以最佳方式解码

		return getBitmapForPathWidthOpts(pathName, opts);
	}

	/**
	 * 回收不再用到的对象
	 */
	public static void recycleUnusedMemory() {
		System.runFinalization();

		// 给系统一些时间回收临时对象
		try {
			Thread.sleep(500);
		} catch (Throwable e) {
		}
	}

	/**
	 * 根据图片字节数据和位图工厂参数载入位图
	 * 
	 * @param imageBytes 图片字节数据
	 * @param opts 位图工厂参数
	 * @return Bitmap 位图
	 */
	private static Bitmap getBitmapForBytesWidthOpts(byte[] imageBytes, BitmapFactory.Options opts) {
		Bitmap bitmap = null;

		try {
			bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
		} catch (Throwable e) {
			bitmap = null;
		}

		return bitmap;
	}

	/**
	 * 根据图片路径和位图工厂参数载入位图
	 * 
	 * @param pathName 图片路径
	 * @param opts 位图工厂参数
	 * @return Bitmap 位图
	 */
	private static Bitmap getBitmapForPathWidthOpts(String pathName, BitmapFactory.Options opts) {
		FileInputStream is = null;
		Bitmap bitmap = null;

		try {
			// 用 decodeFileDescriptor() 来生成 bimap比decodeFile() 省内存
			// http://blog.csdn.net/go_to_learn/article/details/9764805
			is = new FileInputStream(pathName);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (Throwable e) {
			e.printStackTrace();
			bitmap = null;
		}

		if (null != is) {
			try {
				is.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	/**
	 * 根据图片字节数据计算出安全读取图片的位图工厂参数，计算失败则返回null 使用该方法最大限度避免OOM
	 * 
	 * @param imageBytes 图片字节数据
	 * @param maxHeight 最大解码高度
	 * @param maxWidth 最大解码宽度
	 * @return BitmapFactory.Options 位图工厂参数
	 */
	public static BitmapFactory.Options getBitmapOptionsForBytes(byte[] imageBytes, int maxHeight, int maxWidth) {
		if (null == imageBytes) {
			return null;
		}

		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 告诉执行函数，只需计算图片高宽等参数
			opts.inJustDecodeBounds = true;

			opts.inPurgeable = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 计算图片的真实高宽
			BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);

			// 计算读取图片时允许的最大抽样率
			opts.inSampleSize = getMaxInSampleSize(opts, maxHeight, maxWidth);

			// 告诉执行函数，后续调用时，真正对图片进行抽样
			opts.inJustDecodeBounds = false;

			return opts;
		} catch (Throwable e) {
			return null;
		}
	}

    /**
     * 根据图片资源ID计算出安全读取图片的位图工厂参数，计算失败则返回null 使用该方法最大限度避免OOM
     *
     * @param resID 图片地址
     * @param maxHeight 最大解码高度
     * @param maxWidth 最大解码宽度
     * @return BitmapFactory.Options 位图工厂参数
     */
    public static BitmapFactory.Options getBitmapOptionsForResourceID(int resID, int maxHeight, int maxWidth) {
        if (0 == resID) {
            return null;
        }

        try {
            InputStream is = AppMain.getApp().getResources().openRawResource(resID);
            BitmapFactory.Options opts = new BitmapFactory.Options();

            // 告诉执行函数，只需计算图片高宽等参数
            opts.inJustDecodeBounds = true;

            opts.inPurgeable = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

            // 计算图片的真实高宽
            BitmapFactory.decodeStream(is, null, opts);

            // 计算读取图片时允许的最大抽样率
            opts.inSampleSize = getMaxInSampleSize(opts, maxHeight, maxWidth);

            // 告诉执行函数，后续调用时，真正对图片进行抽样
            opts.inJustDecodeBounds = false;

            return opts;
        } catch (Throwable e) {
            return null;
        }
    }

	/**
	 * 根据图片地址计算出安全读取图片的位图工厂参数，计算失败则返回null 使用该方法最大限度避免OOM
	 * 
	 * @param pathName 图片地址
	 * @param maxHeight 最大解码高度
	 * @param maxWidth 最大解码宽度
	 * @return BitmapFactory.Options 位图工厂参数
	 */
	public static BitmapFactory.Options getBitmapOptionsForPath(String pathName, int maxHeight, int maxWidth) {
		if (null == pathName) {
			return null;
		}

		try {
			File filePath = new File(pathName);
			if (!filePath.exists() || !filePath.isFile()) {
				return null;
			}

			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 告诉执行函数，只需计算图片高宽等参数
			opts.inJustDecodeBounds = true;

			opts.inPurgeable = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 计算图片的真实高宽
			BitmapFactory.decodeFile(pathName, opts);

			// 计算读取图片时允许的最大抽样率
			opts.inSampleSize = getMaxInSampleSize(opts, maxHeight, maxWidth);

			// 告诉执行函数，后续调用时，真正对图片进行抽样
			opts.inJustDecodeBounds = false;

			return opts;
		} catch (Throwable e) {
			return null;
		}
	}

    /**
     * 将图片圆角化处理
     *
     * @param srcBitmap 原图 Bitmap 对象
     * @param cornerPixels 圆角的像素值
     * @param backgroundColor 圆角背景颜色
     * @return Bitmap 返回圆角处理后的图片对象
     */
    public static Bitmap setRoundCorner(Bitmap srcBitmap, float cornerPixels, int backgroundColor) {
        try {
            Bitmap output = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rectF, cornerPixels, cornerPixels, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(srcBitmap, rect, rect, paint);
            return output;
        } catch (Throwable e) {
            return null;
        }
    }

	/**
	 * 根据位图工厂参数，计算出位图的最大允许抽样率 抽样率N为整数，表示内存中图片的高宽均为原始图片的N分一
	 * 
	 * @param options 位图工厂参数
	 * @return int 最大允许抽样率
	 */
	public static int getMaxInSampleSize(BitmapFactory.Options options) {
		return getMaxInSampleSize(options, -1, -1);
	}

	/**
	 * 根据位图工厂参数等限制条件，计算出位图的最大允许抽样率 抽样率N为整数，表示内存中图片的高宽均为原始图片的N分一
	 * 
	 * @param options 位图工厂参数
	 * @param maxHeight 最大解码高度
	 * @param maxWidth 最大解码宽度
	 * @return int 获取根据限制条件计算得出最大允许抽样率
	 */
	public static int getMaxInSampleSize(BitmapFactory.Options options, int maxHeight, int maxWidth) {
		int inSampleSize = 1;
		int height = options.outHeight;
		int width = options.outWidth;

		// 根据最大允许的高度定制缩放比
		if (maxHeight > 0 && maxHeight < height) {
			double maxScaleHeight = (double)height / maxHeight;
			while (maxScaleHeight > inSampleSize) {
				inSampleSize++;
			}
		}

		// 根据最大允许的宽度定制缩放比
		if (maxWidth > 0 && maxWidth < width) {
			double maxScaleWidth = (double)width / maxWidth;
			while (maxScaleWidth > inSampleSize) {
				inSampleSize++;
			}
		}

		// 根据内存定制缩放比
		while (!checkCanLoadBitmap((int) (width / (double) inSampleSize), (int) (height / (double) inSampleSize), options.inPreferredConfig)) {
			inSampleSize++;
		}

		return inSampleSize;
	}

	/**
	 * 检测当前是否有足够的内存去加载一张指定高宽的位图
	 * 
	 * @param width 位图的宽
	 * @param height 位图的高
	 * @param config 位图参数配置
	 * @return boolean 占用内存字节数
	 */
	public static boolean checkCanLoadBitmap(long width, long height, Bitmap.Config config) {
		return (getMemorySizeForBitmap(width, height, config) < getAvailableMemory());
	}

    /**
     * 获取当前系统还允许App申请的内存
     * 这个函数得到的值随时会变，要求在用的时候调用，无需缓存
     * @return long 系统允许App在增加申请的内存字节数
     */
    public static long getAvailableMemory() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory();
    }

	/**
	 * 获取指定高宽的位图所占内存字节数
	 * 
	 * @param width 位图的宽
	 * @param height 位图的高
	 * @param config 位图参数配置
	 * @return long 占用内存字节数
	 */
	public static long getMemorySizeForBitmap(long width, long height, Bitmap.Config config) {
		return width * height * getMemoryBytesForPixel(config);
	}

	/**
	 * 获取位图中每个像素所占内存的字节数
	 * 
	 * @param config 位图参数配置
	 * @return int 每个像素站的内存字节数
	 */
	public static int getMemoryBytesForPixel(Bitmap.Config config) {
		if (null == config) { // 默认按 Bitmap.Config.ARGB_8888 处理
			return 4;
		}

		switch (config) {
		case RGB_565:
		case ARGB_4444:
			return 2;

		case ALPHA_8:
			return 1;

		case ARGB_8888:
		default:
			return 4;
		}
	}
}
