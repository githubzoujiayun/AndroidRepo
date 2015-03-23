package com.example.test2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.waps.AppConnect;

import com.example.test2048.GameManager.GameListener;
import com.example.test2048.GameView.FlingAnimationListener;

public class Main extends Activity implements OnTouchListener, OnClickListener {

	public static final boolean DEBUG = false;

	public static final int REQUEST_CODE_SETTINGS = 0;
	public static final int REQUEST_CODE_GAMEOVER = 1;
	
	private static final String APP_ID = "77d317778db6544767462225c772d7d1";
	private static final String APP_PID = "goapk";

	private GameManager mManager;
	private GameView mGameView;
	private SampleGameListener mListener;

	private TextView mCurrent;
	private TextView mHistory;
	private Button mMenu;
	private Button mBack;
	private TextView mTargetView;

	private int mCommand = Settings.RESULT_COMMAND_UNKNOWN;
	private FlingAnimationListener mFlingListener = new FlingAnimationListener() {

		@Override
		public void onFlingAnimationFinished() {
			updateRollbackView();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("qinchao", "onCreate");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		mGameView = (GameView) findViewById(R.id.game_view);
		mTargetView = (TextView) findViewById(R.id.target);
		mTargetView.setOnTouchListener(this);
		mCurrent = (TextView) findViewById(R.id.current_score);
		mHistory = (TextView) findViewById(R.id.history_score);
		mMenu = (Button) findViewById(R.id.btn_menu);
		mBack = (Button) findViewById(R.id.btn_rollback);
		mMenu.setOnClickListener(this);
		mBack.setOnClickListener(this);
		addWaps();
	}

	private void addWaps() {
		 AppConnect.getInstance(APP_ID,APP_PID,this);
		if (showAds()) {
			AppConnect.getInstance(this).initPopAd(this);
			LinearLayout adlayout = (LinearLayout) findViewById(R.id.AdLinearLayout);
			AppConnect.getInstance(this).showBannerAd(this, adlayout);
		}
	}

	private boolean showAds() {
		return AppConnect.getInstance(this)
				.getConfig("waps_config_2048_ads", "1").equals("1");
//		String value = AppConnect.getInstance(this).getConfig("waps_config_2048_ads","1");
//		return value.equals("1");
	}

	private void removeWaps() {
		if (showAds()) {
			AppConnect.getInstance(this).close();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mManager = GameManager.getInstance(this);
		mManager.setMainView(mGameView);
		mListener = new SampleGameListener();
		mManager.setGameListener(mListener);
		mGameView.setFlingAnimationListener(mFlingListener);
		Log.e("qinchao", "onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("qinchao", "onResume()");
		updateRollbackView();
		mManager.restoreState();
		mManager.onResume(mCommand);
		mCommand = Settings.RESULT_COMMAND_UNKNOWN;
	}

	@Override
	protected void onPause() {
		mManager.onPause();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!DEBUG)
			return false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// mGameView.test();
			// mManager.testClearState();
			// mManager.testRestart();
			// mManager.testTranslate();
			// mManager.testPop();
			mManager.testGameOver();
			// mManager.testYouWin();
		}
		return false;
	}

	class SampleGameListener implements GameListener {

		@Override
		public void onScoreUpdate(int score) {
			final String scoreText = String.valueOf(score);
			mCurrent.setText(scoreText);
		}

		@Override
		public void onHistoryUpdate(int score) {
			final String scoreText = String.valueOf(score);
			mHistory.setText(scoreText);
		}

		@Override
		public void onNewTargetUpdate(int target) {
			updateNewTarget(target);
		}
	}

	private void updateNewTarget(int target) {
		String text = getString(R.string.your_new_target, target);
		mTargetView.setText(text);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.btn_menu) {
			Settings.actionSettings(this);
		} else if (id == R.id.btn_rollback) {
			if (mManager.canRollback()) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.rollback_title)
						.setMessage(R.string.rollback_message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										mManager.rollback();
										updateRollbackView();
									}
								})
						.setNegativeButton(android.R.string.cancel, null)
						.show();
			}
		}
	}

	private void updateRollbackView() {
		if (mManager.canRollback()) {
			mBack.setEnabled(true);
		} else {
			mBack.setEnabled(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("qinchao", "onActivityResult()");
		if (resultCode != RESULT_OK)
			return;
		Bundle b = data.getExtras();
		int command = b.getInt("command");
		if (requestCode == REQUEST_CODE_SETTINGS) {

			if (command == Settings.RESULT_COMMAND_UNKNOWN)
				return;
			if (command == Settings.RESULT_COMMAND_GO) {
				return;
			} else if ((command & Settings.RESULT_COMMAND_RESTART) != 0) {
				mCommand = command;
			}

			if ((command & Settings.RESULT_COMMAND_SAVE) != 0) {
				toastMessage(R.string.toast_record_save);
			}
			if ((command & Settings.RESULT_COMMAND_LOAD) != 0) {
				// mManager.loadGame();
				// mManager.startGame();
				mCommand = command;
			}
		} else if (requestCode == REQUEST_CODE_GAMEOVER) {
			if (command == OveredActivity.RESULT_COMMAND_RESTART) {
				mCommand = command;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void toastMessage(int resId) {
		toastMessage(getString(resId));
	}

	private void toastMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		removeWaps();
		super.onDestroy();
	}
}
