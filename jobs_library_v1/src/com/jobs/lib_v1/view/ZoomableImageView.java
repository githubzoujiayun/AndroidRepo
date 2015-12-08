package com.jobs.lib_v1.view;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.fs.AssetsLoader;
import com.jobs.lib_v1.misc.ViewUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 用来显示图片缩放的类，原创
 * 
 * @author solomon.wen
 * @date 2012-12-13
 */
public class ZoomableImageView extends ImageView implements View.OnTouchListener {
	static final int TOUCH_MODE_NONE = 0; // 无操作
	static final int TOUCH_MODE_DRAG = 1; // 拖动操作
	static final int TOUCH_MODE_ZOOM = 2; // 缩放操作

	private PointF lastTouchStartPoint = new PointF(); // 拖动操作中第一次触碰的点
	private PointF lastTouchMultiMiddlePoint = new PointF(); // 缩放操作中，第一次触碰的中心点
	private Matrix lastSavedMatrix = new Matrix(); // DOWN 事件中的矩阵值
	private float lastDistanceBetweenFingers = 1f; // 上一次两个手指之间的距离
	private Matrix currentMatrix = new Matrix(); // 当前矩阵值
	private int currentTouchMode = TOUCH_MODE_NONE; // 默认事件
	private Bitmap imageBitMap = null; // 位图对象

	private boolean zoomEnable = false; // 是否启用缩放功能
	private final static float maxScale = 3.0f; // 最大放大倍数
	private final static float minScale = 0.5f; // 最小缩小倍数
	private float minAdjustScale = minScale; // 经过调整后的最小缩放倍数
	private float maxAdjustScale = maxScale; // 经过调整后的最大放大倍数
	private boolean imageViewHasInited = false; // 视图是否已初始化完成
	private int lastWidth = 0; // 上次得到的控件的宽度
	private int lastHeight = 0; // 上次得到的控件的高度
	private float initScale = 1.0f; // 初始化时的缩放比例
	private GestureDetector doubleClickDetector = null; // 双击手势捕获
	private OnClickListener doubleClickListener = null; // 双击事件
	private boolean doubleClickHasCalled = false; // 双击事件是否已调用

	public ZoomableImageView(Context context) {
		super(context);
		initParams();
	}

	public ZoomableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParams();
	}

	public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParams();
	}

	/**
	 * 做一些初始化的操作
	 */
	private void initParams() {
		if(isInEditMode()){
			super.setImageResource(android.R.drawable.picture_frame);
			setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			setScaleType(ImageView.ScaleType.MATRIX);
		}
	}

	/**
	 * 通过位图对象来设置当前图片
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		ViewUtil.setImageAlpha(this, 0);

		super.setImageBitmap(bm);

		imageBitMap = bm;

		post(new Runnable() {
			@Override
			public void run() {
				autoInitImagePosition();
			}
		});
	}

	/**
	 * 通过资源ID来设置当期图片
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	@Override
	public void setImageResource(int resId) {
		setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
	}

	/**
	 * 通过 assets 下的资源路径来设置图片
	 * 
	 * @author solomon.wen
	 * @date 2013-05-25
	 * @param path
	 */
	public void setImageAssetsPath(String path) {
		setImageBytes(AssetsLoader.loadFileBytes(path));
	}

	/**
	 * 通过字节流来设置当期图片
	 * 
	 * @author solomon.wen
	 * @date 2013-05-25
	 */
	public void setImageBytes(byte[] imageData) {
		if (null == imageData) {
			return;
		}

		try {
			setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
		} catch (Throwable e) {
			AppUtil.print(e);
		}
	}

	/**
	 * 获取当前是否启用缩放功能
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	public boolean getZoomEnable() {
		return zoomEnable;
	}

	/**
	 * 计算两点之间的距离
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	public void setZoomEnable(boolean enable) {
		if (zoomEnable == enable) {
			return;
		}

		zoomEnable = enable;

		post(new Runnable() {
			@Override
			public void run() {
				autoInitImagePosition();
			}
		});
	}

	/**
	 * 手离开屏幕后，自动调整位置和缩放比例
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	private void autoInitImagePosition() {
		lastWidth = getWidth();
		lastHeight = getHeight();

		imageViewHasInited = lastWidth > 0 && lastHeight > 0;

		if (!imageViewHasInited) {
			ViewUtil.setImageAlpha(this, 255);
			return;
		}

		if (null == imageBitMap) {
			ViewUtil.setImageAlpha(this, 255);
			return;
		}

		if (!zoomEnable) {
			setOnTouchListener(null);
			setLongClickable(false);
			setOnDoubleTap(null);

			Matrix matrix = new Matrix();

			matrix.reset();
			matrix.setTranslate((getWidth() - imageBitMap.getWidth()) / 2, (getHeight() - imageBitMap.getHeight()) / 2);

			setImageMatrix(matrix);
		} else {
			setOnTouchListener(this);
			setLongClickable(true);
			installDoubleClickEvent();

			float[] matrix_values = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

			Matrix matrix = new Matrix();
			matrix.set(getImageMatrix());
			matrix.getValues(matrix_values);

			float scale = DeviceUtil.getScreenDensity();
			float bitmapWidth = imageBitMap.getWidth() * DeviceUtil.getScreenDensity();
			float bitmapHeight = imageBitMap.getHeight() * DeviceUtil.getScreenDensity();

			if (getWidth() > bitmapWidth) {
				scale = DeviceUtil.getScreenDensity();
				matrix_values[Matrix.MTRANS_X] = (getWidth() - bitmapWidth) / 2;

				if (getHeight() > bitmapHeight) {
					matrix_values[Matrix.MTRANS_Y] = (getHeight() - bitmapHeight) / 2;
				} else {
					matrix_values[Matrix.MTRANS_Y] = 0;
				}
			} else {
				scale = (float) getWidth() / (float) bitmapWidth * DeviceUtil.getScreenDensity();
				matrix_values[Matrix.MTRANS_X] = 0;
				
				float realHeight = scale * imageBitMap.getHeight();
				if (getHeight() > realHeight) {
					matrix_values[Matrix.MTRANS_Y] = (getHeight() - realHeight) / 2;
				} else {
					matrix_values[Matrix.MTRANS_Y] = 0;
				}
			}

			minAdjustScale = Math.min(scale, minScale);
			maxAdjustScale = Math.max(2.0f, maxScale);

			matrix_values[Matrix.MSCALE_X] = scale;
			matrix_values[Matrix.MSCALE_Y] = scale;

			initScale = scale;

			matrix.setValues(matrix_values);
			setImageMatrix(matrix);
		}

		ViewUtil.setImageAlpha(this, 255);
	}

	/**
	 * 设置双击监听器
	 * 
	 * @author solomon.wen
	 * @date 2012-12-14
	 * @param listener
	 */
	protected synchronized void setOnDoubleTap(OnClickListener listener) {
		if (null == doubleClickDetector) {
			doubleClickDetector = new GestureDetector(getContext(), new OnDoubleClick());
		}

		doubleClickListener = listener;
	}

	/**
	 * 设置默认的双击效果
	 * 
	 * @author solomon.wen
	 * @date 2012-12-14
	 */
	protected void installDoubleClickEvent() {
		setOnDoubleTap(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float[] matrix_values = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

				Matrix matrix = new Matrix();
				matrix.set(getImageMatrix());
				matrix.getValues(matrix_values);

				float curScale = matrix_values[Matrix.MSCALE_X];
				float deltScale = Math.abs(curScale - initScale);

				if (deltScale < 0.05) {
					if (initScale < 1) {
						if (Math.abs(1 - initScale) > 0.1) {
							curScale = 1; // 图够大，双击操作时，在原尺寸和缩小尺寸之间切换
						} else {
							curScale = 1.25f; // 图不够大，双击操作时，在原尺寸和1.25倍之间切换
						}
					} else { // 小图，双击操作时，在原尺寸和1.25倍之间切换
						float boxScale = (float) getWidth() / imageBitMap.getWidth();

						if (boxScale > 1.25f) {
							curScale = boxScale;
						} else {
							curScale = 1.25f;
						}
					}
				} else {
					curScale = initScale; // 恢复原尺寸
				}

				matrix_values[Matrix.MSCALE_X] = curScale;
				matrix_values[Matrix.MSCALE_Y] = curScale;

				matrix.setValues(matrix_values);

				autoAdjustPosition(matrix);
			}
		});
	}

	/**
	 * 重绘事件
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (getWidth() != lastWidth || getHeight() != lastHeight) {
			autoInitImagePosition();
		}
	}

	/**
	 * 手离开屏幕后，自动调整位置
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	private void autoAdjustPosition(Matrix matrix) {
		if (null == matrix || null == imageBitMap || !zoomEnable || !imageViewHasInited) {
			return;
		}

		float[] matrix_values = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		matrix.getValues(matrix_values);

		float scale = matrix_values[Matrix.MSCALE_X];

		scale = Math.max(minAdjustScale, scale);
		scale = Math.min(maxAdjustScale, scale);

		float curWidth = scale * imageBitMap.getWidth();
		float curHeight = scale * imageBitMap.getHeight();
		float curX = matrix_values[Matrix.MTRANS_X];
		float curY = matrix_values[Matrix.MTRANS_Y];
		float boxWidth = getWidth();
		float boxHeight = getHeight();

		if (curWidth > boxWidth) {
			if (curX > 0) {
				curX = 0;
			} else if (curX + curWidth < boxWidth) {
				curX = boxWidth - curWidth;
			}
		} else {
			curX = (boxWidth - curWidth) / 2;
		}

		if (curHeight > boxHeight) {
			if (curY > 0) {
				curY = 0;
			} else if (curY + curHeight < boxHeight) {
				curY = boxHeight - curHeight;
			}
		} else {
			curY = (boxHeight - curHeight) / 2;
		}

		matrix_values[Matrix.MSCALE_X] = scale;
		matrix_values[Matrix.MSCALE_Y] = scale;
		matrix_values[Matrix.MTRANS_X] = curX;
		matrix_values[Matrix.MTRANS_Y] = curY;

		matrix.setValues(matrix_values);
		setImageMatrix(matrix);
	}

	/**
	 * 计算两点之间的距离
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	private static float calculationDistanceBetweenFingers(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算两点连线中间的点
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	private static void calculationMiddlePointBetweenFingers(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * 截获touch事件，用来处理手势，以此识别缩放或是移动
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (null == imageBitMap || !zoomEnable || !imageViewHasInited) {
			return false;
		}

		if (null != doubleClickDetector) {
			doubleClickDetector.onTouchEvent(event);

			if (doubleClickHasCalled) {
				currentMatrix.set(getImageMatrix());
				lastSavedMatrix.set(currentMatrix);

				doubleClickHasCalled = false;

				return true;
			}
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			currentMatrix.set(getImageMatrix());
			lastSavedMatrix.set(currentMatrix);
			lastTouchStartPoint.set(event.getX(), event.getY());
			currentTouchMode = TOUCH_MODE_DRAG;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			lastDistanceBetweenFingers = calculationDistanceBetweenFingers(event);

			if (lastDistanceBetweenFingers > 10f) {
				lastSavedMatrix.set(currentMatrix);
				calculationMiddlePointBetweenFingers(lastTouchMultiMiddlePoint, event);
				currentTouchMode = TOUCH_MODE_ZOOM;
			}

			break;

		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			currentTouchMode = TOUCH_MODE_NONE;
			break;

		case MotionEvent.ACTION_MOVE:
			if (currentTouchMode == TOUCH_MODE_DRAG) {
				currentMatrix.set(lastSavedMatrix);
				currentMatrix.postTranslate(event.getX() - lastTouchStartPoint.x, event.getY() - lastTouchStartPoint.y);
			} else if (currentTouchMode == TOUCH_MODE_ZOOM) {
				float newDistanceBetweenFingers = calculationDistanceBetweenFingers(event);
				if (newDistanceBetweenFingers > 10) {
					currentMatrix.set(lastSavedMatrix);
					float scale = newDistanceBetweenFingers / lastDistanceBetweenFingers;
					currentMatrix.postScale(scale, scale, lastTouchMultiMiddlePoint.x, lastTouchMultiMiddlePoint.y);
				}
			}
			break;
		}

		if (currentTouchMode == TOUCH_MODE_NONE) {
			autoAdjustPosition(currentMatrix);
		} else {
			setImageMatrix(currentMatrix);
		}

		return false;
	}

	/**
	 * 处理双击事件
	 * 
	 * @author solomon.wen
	 * @date 2012-12-14
	 */
	private class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (null != doubleClickListener) {
				doubleClickHasCalled = true;
				doubleClickListener.onClick(ZoomableImageView.this);
			}

			return false;
		}
	}
}
