package com.example.test2048;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.example.test2048.GameView.GameTable;

public class GameManager {

	private Context mContext;
	private GameListener mListener;
	private StateChangeListener mStateListener;
	private GameView mView;

	RollBackStack mBackStack;

	private static GameManager sGameManager;

	private boolean mSoundOn = false;

	private int mScore = 0;
	private int mHistory = 0;
	private int mNextTarget = 2048;

	private static final String HISTORY_SCORE_PRFS = "history";
	public static final String CURRENT_SCORE_PRFS = "current";
	private static final String GAME_STATE_PRFS = "state";
	private static final String CONFIG_SOUND_ON_PREFS = "sound";
	private static final String NEXT_TARGET_PREFS = "target";

	public static final String SHAREDPREFERENCE_RECORD_ARCHIVE_NAME = "record_archive";
	public static final String SHAREDPREFERENCE_RECORD_HISTORY_NAME = "record_history";

	private ArrayList<Integer> mSoundIds = new ArrayList<Integer>(2);
	private SoundPool mSoundPool;
	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;

	private static final String TEMP_FILE_NAME = ".temp";
	private static final String TEMP_FILE_SUFFIX = ".png";

	public enum State {
		START, PAUSE, PLAYING, OVER, INIT
	}

	private State mState = State.INIT;

	public static GameManager getInstance(Context context) {
		if (sGameManager == null) {
			sGameManager = new GameManager();
		}
		sGameManager.init(context);
		return sGameManager;
	}

	private GameManager() {
		mBackStack = new RollBackStack();
	}

	private void init(Context context) {
		mContext = context;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPrefs.edit();
		initAudio();
	}

	// @Override
	// public void onCreate() {
	// super.onCreate();
	// mBackStack = new RollBackStack();
	// mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	// mEditor = mPrefs.edit();
	// initAudio();
	// }

	private void initAudio() {
		AssetManager asm = mContext.getAssets();
		AssetFileDescriptor afd;
		try {
			mSoundIds.clear();
			String paths[] = asm.list("sound");
			mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
			afd = asm.openFd("sound/" + paths[0]);
			mSoundIds.add(mSoundPool.load(afd, 1));
			afd = asm.openFd("sound/" + paths[1]);
			mSoundIds.add(mSoundPool.load(afd, 1));
			Collections.sort(mSoundIds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public State getState() {
		return mState;
	}

	public void setState(State state) {
		mState = state;
		if (mStateListener != null) {
			mStateListener.onStateChanged(mState);
		}
	}

	public void gameOver() {
		mBackStack.clear();
		setState(State.OVER);
		// Toast.makeText(mContext, "game over!", Toast.LENGTH_SHORT).show();
//		String screenPath = screenShot();
		 String screenPath = "";
//				 makebitmap((Activity)mContext);
		OveredActivity
				.actionGameOvered((Activity) mContext, mScore, screenPath);
	}

	private String screenShot() {
		 View view = mView.getRootView();
		 view.setDrawingCacheEnabled(true);
		 view.buildDrawingCache();

		Bitmap b = null;
//		try {
//			WindowManager mWindowManager = (WindowManager) mContext
//					.getSystemService(Context.WINDOW_SERVICE);
//			Display mDisplay = mWindowManager.getDefaultDisplay();
//			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
//			mDisplay.getRealMetrics(mDisplayMetrics);
//			int[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
//			Class c = Class.forName("android.view.SurfaceControl");
//			Method m = c.getMethod("screenshot", int.class, int.class);
//			b = (Bitmap) m.invoke(null, dims[0], dims[1]);
//		} catch (ClassNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
		 Bitmap bitmap = view.getDrawingCache();
//		Bitmap bitmap = b;
		// bitmap.setHasAlpha(false);
//		bitmap.prepareToDraw();
		File fname = null;
		if (bitmap != null) {
			System.out.println("bitmap got!");
			try {
				fname = File.createTempFile(TEMP_FILE_NAME, TEMP_FILE_SUFFIX,
						Environment.getExternalStorageDirectory());
				FileOutputStream out = new FileOutputStream(fname);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				System.out.println("file " + fname + "output done.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fname.getPath();
	}

	public void startGame() {
		if (!mView.isViewReady())
			return;

		if (getState() == State.OVER) {
			clearState();
			setState(State.INIT);
		}

		if (getState() == State.INIT) {
			mView.createTwoEntitys();
			setState(State.START);
		}
		setState(State.PLAYING);
		mView.startGame();
	}

	public void setMainView(GameView gameView) {
		mView = gameView;
		mView.setManager(this);
	}

	public void updateScore(int increasScore) {
		mScore += increasScore;
		if (mListener != null) {
			mListener.onScoreUpdate(mScore);
			if (mScore > getHistory()) {
				mHistory = mScore;
				mListener.onHistoryUpdate(mHistory);
			}
		}
	}

	private int getHistory() {
		return mHistory;
	}
	
	public int getScore(){
		return mScore;
	}

	public void setGameListener(GameListener listener) {
		mListener = listener;
	}

	interface GameListener {
		void onScoreUpdate(int score);

		void onHistoryUpdate(int score);
		
		void onNewTargetUpdate(int target);
	}

	public void setStateChangeListener(StateChangeListener listener) {
		mStateListener = listener;
	}

	public interface StateChangeListener {
		public void onStateChanged(State state);
	}

	public void onResume(int command) {
		mView.setCommand(command);
		if (mState == State.PAUSE) {
			startGame();
		}
	}

	public void onPause() {
		if (mState != State.INIT && mState != State.OVER) {
			setState(State.PAUSE);
		}
		saveState();
	}

	private void clearState() {
		// mEditor.putInt(HISTORY_SCORE_PRFS, mHistory);
		mEditor.putInt(CURRENT_SCORE_PRFS, 0);
		mEditor.putString(GAME_STATE_PRFS, State.INIT.name());
		mView.clearState(mEditor);
		mBackStack.clear();
		mEditor.commit();
		restoreState();
	}

	public void restoreState() {
		mSoundOn = mPrefs.getBoolean(CONFIG_SOUND_ON_PREFS, true);
		mHistory = mPrefs.getInt(HISTORY_SCORE_PRFS, 0);
		mScore = mPrefs.getInt(CURRENT_SCORE_PRFS, 0);
		mNextTarget = mPrefs.getInt(NEXT_TARGET_PREFS, 2048);
		mListener.onHistoryUpdate(mHistory);
		mListener.onScoreUpdate(mScore);
		mListener.onNewTargetUpdate(mNextTarget);
		String state = mPrefs.getString(GAME_STATE_PRFS, State.INIT.name());
		mView.restoreState(mPrefs);
		mState = State.valueOf(state);
	}

	private void saveState() {
		mEditor.putBoolean(CONFIG_SOUND_ON_PREFS, mSoundOn);
		mEditor.putInt(HISTORY_SCORE_PRFS, mHistory);
		mEditor.putInt(CURRENT_SCORE_PRFS, mScore);
		mEditor.putString(GAME_STATE_PRFS, mState.name());
		mEditor.putInt(NEXT_TARGET_PREFS, mNextTarget);
		mView.saveState(mEditor);
		mEditor.commit();
	}
	
	public void rollback() {
		if (mScore < 500) return;
		updateScore(-500);
		ArrayList<String> serializes = popSerializeList();
		if (serializes == null) {
			Toast.makeText(mContext, "serialize is null!", Toast.LENGTH_SHORT)
					.show();
		}
		mView.resumeTableView(serializes);
		mView.startGame();
	}
	
	public boolean canRollback() {
		return !mBackStack.isEmpty() && mScore >= 500;
	}

	public void testPop() {
		rollback();
	}

	public void testClearState() {
		clearState();
		Toast.makeText(mContext, "clear done !", Toast.LENGTH_SHORT).show();
	}

	public void testRestart() {
		restartGame();
	}

	public void restartGame() {
		clearState();
		startGame();
	}

	public void testTranslate() {
		mView.testTranslate(mEditor);
		restoreState();
		startGame();
	}

	public void pushSerializeList(ArrayList<String> serilizes) {
		mBackStack.push(serilizes);
	}

	public ArrayList<String> popSerializeList() {
		return mBackStack.pop();
	}

	private class RollBackStack {

		private static final int MAX_STACK_SIZE = 5;

		Stack<ArrayList<String>> mStack = new Stack<ArrayList<String>>();

		public void push(ArrayList<String> serialize) {
			mStack.push(serialize);
			if (mStack.size() > MAX_STACK_SIZE) {
				mStack.remove(0);
			}
		}

		public ArrayList<String> pop() {
			if (!mStack.isEmpty()) {
				return mStack.pop();
			}
			return null;
		}

		public void clear() {
			mStack.clear();
		}
		
		public boolean isEmpty() {
			return mStack.isEmpty();
		}
	}

	public boolean isSoundOn() {
		return mPrefs.getBoolean(CONFIG_SOUND_ON_PREFS, true);
	}

	public boolean switchSound(boolean isOn) {
		mEditor.putBoolean(CONFIG_SOUND_ON_PREFS, isOn);
		mEditor.commit();
		mSoundOn = isOn;
		return isOn;
	}

	public void playSound(boolean combine) {
		if (!mSoundOn)
			return;
		int soundID = mSoundIds.get(combine ? 0 : 1);
		AudioManager am = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play(soundID, volume, volume, 0, 0, 1);
	}

	public void saveGame() {
		SharedPreferences prfs = mContext.getSharedPreferences(
				SHAREDPREFERENCE_RECORD_ARCHIVE_NAME, Context.MODE_PRIVATE);
		Editor editor = prfs.edit();
		// mView.saveGame(editor,String.valueOf(System.currentTimeMillis()));
		editor.putInt(HISTORY_SCORE_PRFS, mHistory);
		editor.putInt(CURRENT_SCORE_PRFS, mScore);
		editor.putString(GAME_STATE_PRFS, mState.name());
		mView.saveGame(editor);
		editor.commit();

		mEditor.putInt(HISTORY_SCORE_PRFS, mHistory);
		mEditor.putInt(CURRENT_SCORE_PRFS, mScore);
		mEditor.putString(GAME_STATE_PRFS, mState.name());
		mView.saveState(mEditor);
		mEditor.commit();
	}

	public HashMap<String, GameTable> restoreGameTables() {
		SharedPreferences prfs = mContext.getSharedPreferences(
				SHAREDPREFERENCE_RECORD_ARCHIVE_NAME, Context.MODE_PRIVATE);
		return mView.getGameTables(prfs);
	}

	public void loadGame() {
		// Intent i = new Intent(mContext,RecordArchive.class);
		// mContext.startActivity(i);
		SharedPreferences prfs = mContext.getSharedPreferences(
				SHAREDPREFERENCE_RECORD_ARCHIVE_NAME, Context.MODE_PRIVATE);
		mHistory = prfs.getInt(HISTORY_SCORE_PRFS, 0);
		mScore = prfs.getInt(CURRENT_SCORE_PRFS, 0);
		mListener.onHistoryUpdate(mHistory);
		mListener.onScoreUpdate(mScore);
		String state = prfs.getString(GAME_STATE_PRFS, State.INIT.name());
		mState = State.valueOf(state);
		mView.restoreGame(prfs);
	}

	public void testGameOver() {
		gameOver();
	}


	public void testYouWin() {
		mNextTarget = 2;
	}
	
	public void newTargetUpdate(int target) {
		if (mListener != null) {
			if (target > mNextTarget) {
				mListener.onNewTargetUpdate(target);
				mNextTarget = target;
				String win = mContext.getString(R.string.you_win);
				String yourtarget = mContext.getString(R.string.your_new_target, target);
				toastMessage(win+","+yourtarget);
			}
		}
	}
	
	public void toastMessage(int resId) {
		toastMessage(mContext.getString(resId));
	}

	public void toastMessage(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
}
