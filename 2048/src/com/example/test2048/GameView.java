package com.example.test2048;

import static com.example.test2048.ViewEntity.EMPTY_ENTITY;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.example.test2048.GameManager.State;

public class GameView extends SurfaceView {

	private static final boolean DEBUG = false;
	private static final boolean DEBUG_POINTS = false || Main.DEBUG;
	private static final boolean DEBUG_WIN = false || Main.DEBUG;

	private static final int ENTITY_SIZE = 2;
	public static final int LENGTH = 4; // GameTable.LENGTH

	private static final int MASK = 10;
	private static final int ROUND = 10;
//	private static final int ENTITY_TEXT_COLOR = Color.BLACK;
	private static final int ENTITY_BG_COLOR = Color.LTGRAY;
	private static final int BG_COLOR = Color.GRAY;
//	private static final int ENTITY_FG_COLOR = Color.DKGRAY;

	public static int[] ENTITY_COLORS;

	// private ViewEntity mNewEntity;
	private HolderCallback mCallback;
	private SurfaceHolder mHolder;
	private GameManager mManager;
	private GameTable mTable;
	private GestureDetector mGestureDetector;
	private FlingAnimationListener mFlingListener;

	private MessageHandler mHandler;
	// private LinkedList<String> mQueue = new LinkedList<String>();

	private int mCommand = Settings.RESULT_COMMAND_UNKNOWN;

	private boolean mIsCombineSucced = false;
	private boolean mIsMoveSucced = false;
	private boolean mIsDrawingUnitTranslateFinished = false;
//	private int mScore = 0;
	private boolean mIsViewReady = false;
	private boolean mIsTrying = false;

	class MessageHandler extends Handler {

		private final static int MSG_CREATE_NEW_ENTITY = 0;
		private final static int MSG_SCORE_CHANGED = 1;
		private final static int MSG_GAME_OVER = 2;
		private final static int MSG_FLING_FINISHED = 3;
		private final static int MSG_NEW_TARGET = 4;

		@Override
		public void handleMessage(Message msg) {
			final int what = msg.what;
			switch (what) {
			case MSG_CREATE_NEW_ENTITY:
				startAnimationDraw(DrawAnimationTask.EVENT_NEW_ENTITY);
				break;
			case MSG_SCORE_CHANGED:
				mManager.updateScore(msg.arg1);
				break;
			case MSG_GAME_OVER:
				mManager.gameOver();
				break;
			case MSG_FLING_FINISHED:
				if (mFlingListener != null) {
					mFlingListener.onFlingAnimationFinished();
				}
				break;
			case MSG_NEW_TARGET:
				mManager.newTargetUpdate(msg.arg1);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

		public void postNewEntityCreate() {
			Message msg = obtainMessage(MSG_CREATE_NEW_ENTITY);
			msg.sendToTarget();
		}

		public void postScoreChanged(int increaseScore) {
			Message msg = obtainMessage(MSG_SCORE_CHANGED);
			msg.arg1 = increaseScore;
			msg.sendToTarget();
		}

		public void postGameOverMessage() {
			Message msg = obtainMessage(MSG_GAME_OVER);
			msg.sendToTarget();
		}

		public void postFlingFinished() {
			Message msg = obtainMessage(MSG_FLING_FINISHED);
			msg.sendToTarget();
		}
		
		public void postNewTarget(int target) {
			Message msg = obtainMessage(MSG_NEW_TARGET);
			msg.arg1 = target;
			msg.sendToTarget();
		}
	};

	private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent downEvent, MotionEvent upEvent,
				float velocityX, float velocityY) {
			Log.e("qinchao", "onFling : velocityX = " + velocityX
					+ " ,velocityY = " + velocityY);
			final MotionEvent devent = downEvent;
			final MotionEvent uevent = upEvent;
			final float vx = velocityX;
			final float vy = velocityY; 
			if (mIsDrawingUnitTranslateFinished) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						onFling(devent, uevent, vx, vy);
					}
				}, 50);
				return false;
			}
			ArrayList<String> serilizes = new ArrayList<String>();
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					ViewEntity entity = mTable.getEntity(i, j);
					serilizes.add(entity.serialize());
				}
			}

			float dx = upEvent.getX() - downEvent.getX();
			float dy = upEvent.getY() - downEvent.getY();
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx > 0) {
					onRightFling();
				} else {
					onLeftFling();
				}
			} else {
				if (dy > 0) {
					onDownFling();
				} else {
					onUpFling();
				}
			}
			if (mIsMoveSucced) {
				// new DrawTaskImp(DrawTask.EVENT_OTHER).startDraw();
				mManager.pushSerializeList(serilizes);
				mManager.playSound(mIsCombineSucced);
				startTranslateDraw();
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	};

	private boolean tryMoveEntity() {
		boolean result = false;
		mIsTrying = true;
		int size = GameTable.LENGTH * GameTable.LENGTH;
		if (mTable.getAll().size() != size) {
			result = true;
		}
		if (onDownFling() || onUpFling() || onRightFling() || onLeftFling()) {
			result = true;
		}
		mIsTrying = false;
		return result;
	}

	private void combineEntity(ViewEntity entity, ViewEntity ne) {
		if (entity.number == ne.number) {
			ViewEntity newEntity = ViewEntity.createNewEntity(entity.point,
					entity.number * 2);
			entity.oldPoint = new Point(entity.point);
			ne.oldPoint = new Point(ne.point);
			mTable.addTranslateEntity(newEntity.point, entity);
			mTable.addTranslateEntity(newEntity.point, ne);
			mTable.removeEntity(entity);
			mTable.removeEntity(ne);
			newEntity.fromEntity[0] = entity;
			newEntity.fromEntity[1] = ne;
			mTable.addEntity(newEntity);
			mIsMoveSucced = true;
			mIsCombineSucced = true;
//			mScore = mScore + entity.number + ne.number;
			int increaseScore = entity.number + ne.number;
			mHandler.postScoreChanged(increaseScore);
			final int number = newEntity.number;
			int power = log(2,number/2048);
			if (DEBUG_WIN) {
				power = log(2,number);
				if (Math.pow(2, power) == number) {
					mHandler.postNewTarget(number * 2);
				}
				return ;
			}
			if (Math.pow(2, power) * 2048 == number) {
				mHandler.postNewTarget(number * 2);
			}
		}
	}
	
	static int log(double v1, double v2){
		return (int)(Math.log(v2)/Math.log(v1));
	}

	private void moveEntity(ViewEntity src, Point nextP) {
		mTable.removeEntity(src);
		src.point.x = nextP.x;
		src.point.y = nextP.y;
		mTable.addEntity(src);
		mIsMoveSucced = true;
	}

	private void saveTranslateTrack(ViewEntity entity) {
		// if (!entity.translateNewFlag) {
		// mTable.views.add(entity);
		// } else {
		// ArrayList<ViewEntity> entitys = mTable
		// .getTranslateEntity(entity.oldPoint);
		// if (entitys == null)
		// return;
		// // if (entitys.size() == 0) throw new RuntimeException();
		// if (entitys.size() > 0) {
		// for (ViewEntity e : entitys) {
		// e.oldPoint = e.point;
		// e.point = entity.point;
		// mTable.views.add(e);
		// }
		// }
		// }
	}

	private boolean onDownFling() {
		Log.e("qinchao", "onDownFling");
		boolean result = false;
		int length = GameTable.LENGTH;
		for (int i = 0; i < length; i++) {
			for (int j = length - 1; j >= 0; j--) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;
				if (j >= 1) {
					for (int k = j - 1; k >= 0; k--) {
						Point np = new Point(i, k);
						ViewEntity ne = mTable.getEntity(np);
						if (ne == EMPTY_ENTITY) {
							continue;
						}
						if (mIsTrying && entity.number == ne.number) {
							result = true;
						} else {
							combineEntity(entity, ne);
						}
						break;
					}
				}
			}
		}

		for (int i = 0; i < length; i++) {
			for (int j = length - 1; j >= 0; j--) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;

				entity.oldPoint = new Point(entity.point);
				if (j == length - 1) {
					continue;
				}
				for (int k = j + 1; k < length; k++) {
					Point nextP = new Point(i, k);
					ViewEntity nextEntity = mTable.getEntity(nextP);
					if (nextEntity != EMPTY_ENTITY)
						break;
					if (mIsTrying) {
						result = true;
					} else {
						moveEntity(entity, nextP);
					}
				}
				saveTranslateTrack(entity);
			}
		}
		return result;
	}

	private boolean onUpFling() {
		Log.e("qinchao", "onUpFling");
		boolean result = false;
		int length = GameTable.LENGTH;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;
				if (j < length - 1) {
					for (int k = j + 1; k < length; k++) {
						Point np = new Point(i, k);
						ViewEntity ne = mTable.getEntity(np);
						if (ne == EMPTY_ENTITY) {
							continue;
						}
						if (mIsTrying && entity.number == ne.number) {
							result = true;
						} else {
							combineEntity(entity, ne);
						}
						break;
					}
				}
			}
		}

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;

				entity.oldPoint = new Point(entity.point);
				if (j == 0) {
					continue;
				}
				for (int k = j - 1; k >= 0; k--) {
					Point nextP = new Point(i, k);
					ViewEntity nextEntity = mTable.getEntity(nextP);
					if (nextEntity != EMPTY_ENTITY)
						break;
					if (mIsTrying) {
						result = true;
					} else {
						moveEntity(entity, nextP);
					}
				}
				saveTranslateTrack(entity);
			}
		}
		return result;
	}

	private boolean onRightFling() {
		Log.e("qinchao", "onRightFling");
		int length = GameTable.LENGTH;
		boolean result = false;
		for (int i = length - 1; i >= 0; i--) {
			for (int j = 0; j < length; j++) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;
				if (i >= 1) {
					for (int k = i - 1; k >= 0; k--) {
						Point np = new Point(k, j);
						ViewEntity ne = mTable.getEntity(np);
						if (ne == EMPTY_ENTITY) {
							continue;
						}
						if (mIsTrying && entity.number == ne.number) {
							result = true;
						} else {
							combineEntity(entity, ne);
						}
						break;
					}
				}
			}
		}

		for (int i = length - 1; i >= 0; i--) {
			for (int j = 0; j < length; j++) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;

				entity.oldPoint = new Point(entity.point);
				if (i == length - 1) {
					continue;
				}
				for (int k = i + 1; k < length; k++) {
					Point nextP = new Point(k, j);
					ViewEntity nextEntity = mTable.getEntity(nextP);
					if (nextEntity != EMPTY_ENTITY)
						break;
					if (mIsTrying) {
						result = true;
					} else {
						moveEntity(entity, nextP);
					}
				}
				saveTranslateTrack(entity);
			}
		}
		return result;
	}

	private boolean onLeftFling() {
		Log.e("qinchao", "onLeftFling");
		boolean result = false;
		int length = GameTable.LENGTH;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;
				if (i < length - 1) {
					for (int k = i + 1; k < length; k++) {
						Point np = new Point(k, j);
						ViewEntity ne = mTable.getEntity(np);
						if (ne == EMPTY_ENTITY) {
							continue;
						}
						if (mIsTrying && entity.number == ne.number) {
							result = true;
						} else {
							combineEntity(entity, ne);
						}
						break;
					}
				}
			}
		}

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Point p = new Point(i, j);
				ViewEntity entity = mTable.getEntity(p);
				if (entity == EMPTY_ENTITY)
					continue;

				entity.oldPoint = new Point(entity.point);
				if (i == 0) {
					continue;
				}
				for (int k = i - 1; k >= 0; k--) {
					Point nextP = new Point(k, j);
					ViewEntity nextEntity = mTable.getEntity(nextP);
					if (nextEntity != EMPTY_ENTITY)
						break;
					if (mIsTrying) {
						result = true;
					} else {
						moveEntity(entity, nextP);
					}
				}
				saveTranslateTrack(entity);
			}
		}
		return result;
	}

	public void test() {
		ViewEntity entity = ViewEntity.testCreateNewEntity(mTable,16384);
		mTable.addEntity(entity);
		startAnimationDraw(DrawAnimationTask.EVENT_NEW_ENTITY);
	}

	public void testTranslate(Editor editor) {
		mTable.testputState(editor);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	public GameView(Context context) {
		super(context);
		init(context);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		ENTITY_COLORS = context.getResources().getIntArray(
				R.array.entity_colors);
		mHandler = new MessageHandler();
		mTable = new GameTable();
		mGestureDetector = new GestureDetector(context, mGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);
		mHolder = getHolder();
		mHolder.setFormat(PixelFormat.TRANSPARENT);
		mCallback = new HolderCallback();
		mHolder.addCallback(mCallback);
	}

	public void setTable(GameTable table) {
		mTable = table;
	}

	class GameTable {
		public static final int LENGTH = 4;
		private HashMap<Point, ViewEntity> entitys = new HashMap<Point, ViewEntity>();
		private HashMap<Point, ArrayList<ViewEntity>> translateEntity = new HashMap<Point, ArrayList<ViewEntity>>();

		private ArrayList<ViewEntity> views = new ArrayList<ViewEntity>();

		public GameTable() {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					Point point = new Point(i, j);
					entitys.put(point, EMPTY_ENTITY);
				}
			}
		}

		public void clear() {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					Point p = new Point(i, j);
					entitys.put(p, EMPTY_ENTITY);
				}
			}
		}

		public ArrayList<ViewEntity> getAll() {
			ArrayList<ViewEntity> all = new ArrayList<ViewEntity>();
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					ViewEntity entity = getEntity(i, j);
					if (entity != EMPTY_ENTITY) {
						all.add(entity);
					}
				}
			}
			return all;
		}

		public ArrayList<ViewEntity> getNewEntitys() {
			ArrayList<ViewEntity> newEntitys = new ArrayList<ViewEntity>();
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					ViewEntity entity = getEntity(i, j);
					if (entity != EMPTY_ENTITY && entity.newFlag) {
						newEntitys.add(entity);
					}
				}
			}
			return newEntitys;
		}

		public AbstractCollection<ViewEntity> getEntitys() {
			return (AbstractCollection<ViewEntity>) entitys.values();
		}

		private ArrayList<ViewEntity> getTranslateEntity(Point key) {
			return translateEntity.get(key);
		}

		private void addTranslateEntity(Point key, ViewEntity value) {
			// if (!translateKey.contains(key)) {
			// translateKey.add(key);
			// }
			ArrayList<ViewEntity> entitys = translateEntity.get(key);
			if (entitys == null) {
				entitys = new ArrayList<ViewEntity>();
				translateEntity.put(key, entitys);
			}
			entitys.add(value);
		}

		public void removeTranslateEntity(ViewEntity from) {
			translateEntity.remove(from);
		}

		public void addEntity(ViewEntity entity) {
			final Point point = entity.point;
			entitys.put(point, entity);
		}

		public void removeEntity(int x, int y) {
			Point p = new Point(x, y);
			entitys.put(p, EMPTY_ENTITY);
		}

		public void removeEntity(ViewEntity entity) {
			final Point point = entity.point;
			entitys.put(point, EMPTY_ENTITY);
		}

		public ViewEntity getEntity(int x, int y) {
			Point p = new Point(x, y);
			return getEntity(p);
		}

		public ViewEntity getEntity(Point p) {
			return entitys.get(p);
		}

		public void clearNewFlag() {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					ViewEntity entity = getEntity(i, j);
					entity.newFlag = false;

				}
			}
		}

		public void clearTrack() {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					ViewEntity entity = getEntity(i, j);
					entity.oldPoint = new Point(EMPTY_ENTITY.point);
				}
			}
			views.clear();
			translateEntity.clear();
		}

		public void saveState(Editor editor) {
			clearState(editor);
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					ViewEntity entity = mTable.getEntity(i, j);
					String key = entity.point.toString();
					editor.putString(key, entity.serialize());
				}
			}
		}

		public void restoreState(ArrayList<String> serializes) {
			if (serializes == null)
				return;
			mTable.clear();
			for (String serialize : serializes) {
				ViewEntity entity = ViewEntity.createNewEntity(serialize);
				mTable.addEntity(entity);
			}
		}

		public void restoreState(SharedPreferences prefs) {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					Point p = new Point(i, j);
					String key = p.toString();
					String serialize = prefs.getString(key,
							EMPTY_ENTITY.serialize());
					ViewEntity entity = ViewEntity.createNewEntity(serialize);
					mTable.addEntity(entity);
				}
			}
		}

		public void testputState(Editor editor) {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					Point p = new Point(i, j);
					String key = p.toString();
					if (j == 0) {
						editor.putString(key,
								String.format("16,%d,%d,-1,-1,false", i, j));
					} else if (i == 3 && j == 3) {
						editor.putString(key,
								String.format("16,%d,%d,2,0,false", i, j));
					} else {
						editor.putString(key, EMPTY_ENTITY.serialize());
					}
				}
			}
			editor.commit();
		}

		public void clearState(Editor editor) {
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					Point p = new Point(i, j);
					String key = p.toString();
					editor.putString(key, EMPTY_ENTITY.serialize());
				}
			}
		}

		public void saveGame(Editor editor) {
			saveState(editor);
		}
	}

	class DrawTranslateAnimation extends DrawTask {

		public DrawTranslateAnimation(int event) {
			mEvent = event;
		}

		private static final int ANIMATION_TIMES = 4;

		@Override
		public void run() {
			mIsDrawingUnitTranslateFinished = true;
			for (int i = 0; i < ANIMATION_TIMES; i++) {
				Canvas canvas = mHolder.lockCanvas();
				drawBackground(canvas);
				// drawAllEntitys(canvas);
				drawTranslateEntitys(canvas, i + 1);
				mHolder.unlockCanvasAndPost(canvas);
			}
			super.run();
		}

		private void drawTranslateEntitys(Canvas canvas, int i) {
			// final AbstractCollection<ViewEntity> entitys = mTable.views;
			final AbstractCollection<ViewEntity> entitys = mTable.getEntitys();
			final AbstractCollection<ViewEntity> newEntitys = mTable
					.getNewEntitys();
			ArrayList<ViewEntity> translateEntitys = new ArrayList<ViewEntity>();
			translateEntitys.addAll(entitys);
			translateEntitys.removeAll(newEntitys);
			for (ViewEntity entity : newEntitys) {
				if (entity.fromEntity[0] != null
						&& entity.fromEntity[1] != null) {
					entity.fromEntity[0].point = new Point(entity.point);
					entity.fromEntity[1].point = new Point(entity.point);
					translateEntitys.add(entity.fromEntity[0]);
					translateEntitys.add(entity.fromEntity[1]);
				}
			}
			final int T = i;
			mCanvasWidth = getWidth();
			// int piece = mCanvasWidth / GameTable.LENGTH / MASK;
			int interval = 1 * piece;
			int lbase = interval;
			int tbase = interval;
			int entityWidth = 8 * piece;
			for (ViewEntity entity : translateEntitys) {
				if (entity != EMPTY_ENTITY) {
					int px = lbase + (2 * entity.point.x + 1) * interval
							+ entity.point.x * entityWidth;
					int py = tbase + (2 * entity.point.y + 1) * interval
							+ entity.point.y * entityWidth;
					int oldx = lbase + (2 * entity.oldPoint.x + 1) * interval
							+ entity.oldPoint.x * entityWidth;
					int oldy = tbase + (2 * entity.oldPoint.y + 1) * interval
							+ entity.oldPoint.y * entityWidth;
					int fakex = oldx + (px - oldx) * T / ANIMATION_TIMES;
					int fakey = oldy + (py - oldy) * T / ANIMATION_TIMES;
					drawEntityInternal(canvas, entity, fakex, fakey,
							entityWidth);
				}
			}
		}

		@Override
		protected void onDrawFinished() {
			// mTable.clearTrack();
			mIsDrawingUnitTranslateFinished = false;
			startAnimationDraw(DrawTask.EVENT_COMBINE_NEW_ENTITY);
		}
	}

	class DrawAnimationTask extends DrawTask {

		private static final int ANIMATION_TIMES = 16;

		private int px, py;

		public DrawAnimationTask(int event) {
			super(event);
		}

		@Override
		public void run() {
			// mHandler.post(new DrawTaskImp(DrawTask.EVENT_DRAW_BG));
			// new DrawTaskImp(DrawTask.EVENT_DRAW_BG).run();
			synchronized (mHolder) {
				ArrayList<ViewEntity> newEntitys = mTable.getNewEntitys();
				if (newEntitys.size() == 0) {
					onDrawFinished();
					return;
				}
				int index = 0;
				if (mEvent == EVENT_COMBINE_NEW_ENTITY) {
					index = 14;
				} else if (mEvent == EVENT_NEW_ENTITY) {
					index = 10;
				}
				for (int i = index; i < ANIMATION_TIMES + 4; i++) {
					if (mEvent == EVENT_NEW_ENTITY && i == ANIMATION_TIMES) {
						break;
					}
					Canvas canvas = mHolder.lockCanvas();
					drawBackground(canvas);
					drawAllEntitys(canvas);
					for (int j = 0; j < newEntitys.size(); j++) {
						final ViewEntity entity = newEntitys.get(j);
						if (entity == EMPTY_ENTITY) {
							break;
						}
						px = lbase + (2 * entity.point.x + 1) * interval
								+ entity.point.x * entityWidth;
						py = tbase + (2 * entity.point.y + 1) * interval
								+ entity.point.y * entityWidth;
						drawNewEntity(canvas, entity, i + 1);
					}
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
			super.run();
		}

		private void drawNewEntity(Canvas canvas, ViewEntity newEntity,
				int index) {
			final ViewEntity entity = newEntity;
			// int piece = mCanvasWidth / GameTable.LENGTH / MASK;
			int entityWidth = 8 * piece;
			int x = px + entityWidth / 2
					- (entityWidth * index / ANIMATION_TIMES / 2);
			int y = py + entityWidth / 2
					- (entityWidth * index / ANIMATION_TIMES / 2);
			int width = entityWidth * index / ANIMATION_TIMES;
			mTextSizeScale *= ((float) index) / ANIMATION_TIMES;
			drawEntityInternal(canvas, entity, x, y, width);
			mTextSizeScale = 1;
		}

		@Override
		protected void onDrawFinished() {
			onAnimationDrawFinished(mEvent);
			if (mEvent == EVENT_NEW_ENTITY || mEvent == EVENT_START) {
				new DrawTaskImp(EVENT_DRAW_OVER).startDraw();
			}
		}
	}

	class DrawTaskImp extends DrawTask {

		public DrawTaskImp(int event) {
			this.mEvent = event;
		}

		@Override
		public void run() {
			synchronized (mHolder) {
				Canvas canvas = mHolder.lockCanvas();
				drawBackground(canvas);
				drawAllEntitys(canvas);
				mHolder.unlockCanvasAndPost(canvas);
			}
			super.run();
		}

		@Override
		protected void onDrawFinished() {
			if (mEvent == EVENT_START) {
				startAnimationDraw(EVENT_START);
			} else if (mEvent == EVENT_DRAW_OVER) {
				mTable.clearNewFlag();
				mTable.clearTrack();
				mIsCombineSucced = false;
				mIsMoveSucced = false;
				mHandler.postFlingFinished();
				if (!tryMoveEntity()) {
					mHandler.postGameOverMessage();
				}
			} else {
				// startTranslateDraw();
			}
		}
	}

	abstract class DrawTask implements Runnable {

		public static final int EVENT_START = 0;
		public static final int EVENT_DRAW_BG = 1;
		public static final int EVENT_TRANSLATE_BG = 2;
		public static final int EVENT_COMBINE_NEW_ENTITY = 3;
		public static final int EVENT_NEW_ENTITY = 4;
		public static final int EVENT_DRAW_OVER = 5;
		public static final int EVENT_DRAW_TRANSLATE = 6;
		public static final int EVENT_OTHER = 100;
		// public static final int EVENT_NEW_ENTITY = 2;

		protected int mCanvasWidth;
		protected int lbase;
		protected int tbase;
		protected int interval;
		protected int entityWidth;
		protected int piece;

		protected int mEvent;

		protected float mTextSizeScale = 1f;

		public DrawTask() {
			mCanvasWidth = getWidth();
			piece = mCanvasWidth / GameTable.LENGTH / MASK;
			interval = 1 * piece;
			entityWidth = 8 * piece;
			tbase = interval;
			lbase = interval;
		}

		public DrawTask(int event) {
			this();
			mEvent = event;
		}

		@Override
		public void run() {
			Log.e("qinchao", getClass().getName() + " : " + mEvent);
			onDrawFinished();
		}

		protected void drawAllEntitys(Canvas canvas) {
			for (ViewEntity entity : mTable.getEntitys()) {
				if (entity != EMPTY_ENTITY) {
					if (mEvent == EVENT_NEW_ENTITY && entity.newFlag) {
						continue;
					}
					if (mEvent == EVENT_START && entity.newFlag) {
						continue;
					}

					if (mEvent == EVENT_COMBINE_NEW_ENTITY && entity.newFlag) {
						continue;
					}

					if (mEvent == EVENT_DRAW_TRANSLATE && entity.isTranslate()) {
						continue;
					}
					drawEntity(canvas, entity);
				}
			}
		}

		protected void drawBackground(Canvas canvas) {
			canvas.drawColor(BG_COLOR);
			mCanvasWidth = getWidth();
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(ENTITY_BG_COLOR);
			RectF rectf = new RectF();
			int l = 0;
			int t = 0;
			int r = 0;
			int b = 0;
			for (int i = 0; i < GameTable.LENGTH; i++) {
				if (i == 0) {
					l = lbase + interval;
				} else {
					l = r + 2 * interval;
				}
				r = l + entityWidth;
				for (int j = 0; j < GameTable.LENGTH; j++) {
					if (j == 0) {
						t = tbase + interval;
					} else {
						t = b + 2 * interval;
					}
					b = t + entityWidth;
					rectf.set(l, t, r, b);
					paint.setTextSize(10);
					if (DEBUG_POINTS) {
						int color = paint.getColor();
						paint.setColor(Color.BLACK);
						canvas.drawText(String.format("(%d,%d)", i, j), l, t,
								paint);
						paint.setColor(color);
					}
					canvas.drawRoundRect(rectf, ROUND, ROUND, paint);
				}
			}
		}

		protected void drawEntity(Canvas canvas, ViewEntity entity) {
			// int piece = mCanvasWidth / GameTable.LENGTH / MASK;
			int interval = 1 * piece;
			int entityWidth = 8 * piece;
			int x = lbase + (2 * entity.point.x + 1) * interval
					+ entity.point.x * entityWidth;
			int y = tbase + (2 * entity.point.y + 1) * interval
					+ entity.point.y * entityWidth;
			drawEntityInternal(canvas, entity, x, y, entityWidth);
		}

		void drawEntityInternal(Canvas canvas, ViewEntity entity, int x, int y,
				int entityWidth) {
			// canvas.drawColor(ENTITY_BG_COLOR);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(entity.getColor());
			RectF rf = new RectF(x, y, x + entityWidth, y + entityWidth);
			canvas.drawRoundRect(rf, ROUND, ROUND, paint);
			paint.setColor(entity.getTextColor());
			String number = String.valueOf(entity.number);
			paint.setTextSize(mTextSizeScale * 90
					* (float) Math.pow(0.80f, (number.length() - 1)));
			FontMetricsInt fmi = paint.getFontMetricsInt();
			Rect rect = new Rect();
			paint.getTextBounds(number, 0, number.length(), rect);
			float textY = rf.top + (entityWidth - fmi.bottom + fmi.top) / 2
					- fmi.top;
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(String.valueOf(entity.number), rf.centerX(), textY,
					paint);
		}

		public void startDraw() {
			new Thread(this).start();
		}

		public void startDraw(int event) {
			mEvent = event;
			startDraw();
		}

		protected abstract void onDrawFinished();
	}

	private void startTranslateDraw() {
		DrawTranslateAnimation task = new DrawTranslateAnimation(
				DrawTask.EVENT_DRAW_TRANSLATE);
		new Thread(task).start();
	}

	private void onAnimationDrawFinished(int event) {
		mTable.clearNewFlag();
		if (mIsMoveSucced
				&& event == DrawAnimationTask.EVENT_COMBINE_NEW_ENTITY) {
			if (!DEBUG || !(mTable.entitys.size() >= ENTITY_SIZE)) {
				ViewEntity.createNewEntityFromMap(mTable, mHandler);
			}
		}
	}

	private void startAnimationDraw(int event) {
		DrawAnimationTask task = new DrawAnimationTask(event);
		new Thread(task).start();
	}

	public void createTwoEntitys() {
		if (mManager.getState() == State.INIT) {
			// ViewEntity.createNewEntityFromMap(mTable, new Point(0, 0), 2);
			ViewEntity.createNewEntityFromMap(mTable);
			ViewEntity.createNewEntityFromMap(mTable);
		}
	}

	void startGame() {
		// new DrawTaskImp(DrawTask.EVENT_START).startDraw();
		startAnimationDraw(DrawTask.EVENT_START);
	}

	void gameOver() {
		mManager.gameOver();
	}

	class HolderCallback implements SurfaceHolder.Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e("qinchao", "surfaceCreated");
			// ViewEntity entity = ViewEntity.createNewEntityFromMap(mTable);
			// mTable.addEntity(entity);
			// startAnimationDraw(DrawAnimationTask.EVENT_OTHER);
			setLayoutParams(new FrameLayout.LayoutParams(getWidth(), getWidth()));
			mIsViewReady = true;
			switch (mCommand) {
			case Settings.RESULT_COMMAND_RESTART:
			case OveredActivity.RESULT_COMMAND_RESTART:
				mManager.restartGame();
				break;
			case Settings.RESULT_COMMAND_LOAD:
				mManager.loadGame();
				mManager.startGame();
				break;
			default:
				mManager.startGame();
				break;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.e("qinchao", "surfaceChanged");

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.e("qinchao", "surfaceDestroyed");
			mIsViewReady = false;
			mManager.setState(State.PAUSE);
		}

	}

	public boolean isViewReady() {
		return mIsViewReady;
	}

	public void setManager(GameManager gameManager) {
		mManager = gameManager;
	}

	public void saveState(Editor editor) {
		mTable.saveState(editor);
	}

	public void saveGame(Editor editor) {
		mTable.saveGame(editor);
	}

	public void restoreGame(SharedPreferences prfs) {
		restoreState(prfs);
	}

	public HashMap<String, GameTable> getGameTables(SharedPreferences prfs) {
		Set<String> keys = prfs.getAll().keySet();
		ArrayList<String> stamps = new ArrayList<String>();
		HashMap<String, GameTable> tableMap = new HashMap<String, GameView.GameTable>();

		for (String key : keys) {
			GameTable table = null;
			String[] keysplit = key.split(":");
			final String stamp = keysplit[1];
			if (!stamps.contains(keysplit[1])) {
				stamps.add(stamp);
				table = new GameTable();
				tableMap.put(stamp, table);
			} else {
				table = tableMap.get(keysplit[1]);
			}
			String serialize = prfs.getString(key, EMPTY_ENTITY.serialize());
			ViewEntity entity = ViewEntity.createNewEntity(serialize);
			table.addEntity(entity);
		}
		return tableMap;
	}

	public void restoreState(SharedPreferences prefs) {
		if (mTable == null) {
			mTable = new GameTable();
		}
		mTable.clear();
//		mScore = prefs.getInt(GameManager.CURRENT_SCORE_PRFS, 0);
		mTable.restoreState(prefs);
	}

	public void clearState(Editor editor) {
		mTable.clearState(editor);
	}

	public void resumeTableView(ArrayList<String> serializes) {
		mTable.restoreState(serializes);
	}

	public void setCommand(int command) {
		mCommand = command;
	}

	interface FlingAnimationListener {
		void onFlingAnimationFinished();
	}

	public void setFlingAnimationListener(FlingAnimationListener _listener) {
		mFlingListener = _listener;
	}
}
