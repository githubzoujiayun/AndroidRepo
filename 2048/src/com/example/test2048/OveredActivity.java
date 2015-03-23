package com.example.test2048;

import java.io.File;

import cn.waps.AppConnect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OveredActivity extends Activity implements OnClickListener {

	public static final int RESULT_COMMAND_UNKNOWN = 0;
	public static final int RESULT_COMMAND_RESTART = 1;
	public static final int RESULT_COMMAND_SHARED = 2;

	private TextView mScoreView;
	private Button mRestart;
	private Button mShared;
	private Button mGetApps, mGetGames;

	private String mScreenShot = null;

	public static void actionGameOvered(Activity a, int score, String screenPath) {
		Intent i = new Intent(a, OveredActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("score", score);
		i.putExtra("screen_shot", screenPath);
		a.startActivityForResult(i, Main.REQUEST_CODE_GAMEOVER);
	}

	private void showWaps() {
		if (showAds()) {
			AppConnect.getInstance(this).showPopAd(this);
			AppConnect.getInstance(this).setPopAdBack(true);
			// 设置迷你广告背景颜色
			AppConnect.getInstance(this).setAdBackColor(
					Color.argb(50, 120, 240, 120));
			// 设置迷你广告广告诧颜色
			AppConnect.getInstance(this).setAdForeColor(Color.YELLOW);
			// 若未设置以上两个颜色,则默认为黑底白字
			LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
			AppConnect.getInstance(this).showMiniAd(this, miniLayout, 10); // 默认
																			// 10
																			// 秒切换一次广告
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gameover);
		showWaps();
		mScoreView = (TextView) findViewById(R.id.game_over_score);
		mRestart = (Button) findViewById(R.id.game_over_restart);
		mShared = (Button) findViewById(R.id.game_over_shared);

		mGetApps = (Button) findViewById(R.id.install_apps);
		mGetGames = (Button) findViewById(R.id.install_games);

		mRestart.setOnClickListener(this);
		mShared.setOnClickListener(this);
		mGetApps.setOnClickListener(this);
		mGetGames.setOnClickListener(this);
		
		if (!showAds()) {
			mGetApps.setVisibility(View.GONE);
			mGetGames.setVisibility(View.GONE);
		}

		Intent i = getIntent();
		if (i != null) {
			int score = i.getIntExtra("score", 0);
			mScreenShot = i.getStringExtra("screen_shot");
			mScoreView.setText(String.valueOf(score));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// @Override
	// protected void onDestroy() {
	// removeWaps();
	// super.onDestroy();
	// }

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		Intent i = new Intent();
		if (id == R.id.game_over_restart) {
			Bundle b = new Bundle();
			b.putInt("command", RESULT_COMMAND_RESTART);
			i.putExtras(b);
			setResult(RESULT_OK, i);
			finish();
		} else if (id == R.id.game_over_shared) {
			// Bundle b = new Bundle();
			// b.putInt("command", RESULT_COMMAND_SHARED);
			// i.putExtras(b);
			// setResult(RESULT_OK,i);
			onShare();
		} else if (id == R.id.install_apps) {
			if (showAds()) {
				AppConnect.getInstance(this).showAppOffers(this);
			}
		} else if (id == R.id.install_games) {
			if (showAds()) {
				AppConnect.getInstance(this).showGameOffers(this);
			}
		}
	}

	private void onShare() {
		File f = new File(mScreenShot);
		Uri uri = Uri.fromFile(f);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setType("image/*");
		// intent.setData(uri);
		intent.setDataAndType(uri, "image/*");
		// intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		// intent.putExtra(Intent.EXTRA_TEXT, "终于可以了!!!");
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	private boolean showAds() {
		return AppConnect.getInstance(this)
				.getConfig("waps_config_2048_ads", "1").equals("1");
	}
}
