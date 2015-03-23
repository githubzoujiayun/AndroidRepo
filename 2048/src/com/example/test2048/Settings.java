package com.example.test2048;

import cn.waps.AppConnect;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Settings extends Activity implements OnClickListener {
	
	public static final int RESULT_COMMAND_UNKNOWN = 0;
	public static final int RESULT_COMMAND_BASE = 1;
	public static final int RESULT_COMMAND_GO = RESULT_COMMAND_BASE << 0;
	public static final int RESULT_COMMAND_RESTART = RESULT_COMMAND_BASE << 1;
	public static final int RESULT_COMMAND_SOUND_EFFECT = RESULT_COMMAND_BASE << 2;
	public static final int RESULT_COMMAND_LOAD = RESULT_COMMAND_BASE << 3;
	public static final int RESULT_COMMAND_SAVE = RESULT_COMMAND_BASE << 4;
	
	private int mCommand = RESULT_COMMAND_UNKNOWN;
	private Bundle mResult;
	
	private Button mGo;
	private Button mRestart;
	private Button mHistory;
	private Button mSoundEffect;
	private Button mSave,mLoad;
	private Button mGetApps,mGetGames;
	
	private GameManager mGameManager;
	
	public static void actionSettings(Activity a) {
		Intent i = new Intent(a,Settings.class);
		a.startActivityForResult(i, Main.REQUEST_CODE_SETTINGS);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.settings);
		mGo = (Button) findViewById(R.id.go_btn);
		mRestart = (Button) findViewById(R.id.restart_game);
		mHistory = (Button) findViewById(R.id.historys);
		mSoundEffect = (Button) findViewById(R.id.sound_effects);
		mSave = (Button) findViewById(R.id.settings_save);
		mLoad = (Button) findViewById(R.id.settings_load);
		mGetApps = (Button) findViewById(R.id.install_apps);
		mGetGames = (Button) findViewById(R.id.install_games);
		
		mGo.setOnClickListener(this);
		mRestart.setOnClickListener(this);
		mHistory.setOnClickListener(this);
		mSoundEffect.setOnClickListener(this);
		mSave.setOnClickListener(this);
		mLoad.setOnClickListener(this);
		mGetApps.setOnClickListener(this);
		mGetGames.setOnClickListener(this);
		
		if (!showAds()) {
			mGetApps.setVisibility(View.GONE);
			mGetGames.setVisibility(View.GONE);
		}
		
		mGameManager = GameManager.getInstance(this);
		setSoundText(mGameManager.isSoundOn());
		
		mCommand = RESULT_COMMAND_UNKNOWN;
		mResult = new Bundle();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.go_btn) {
			mCommand |= RESULT_COMMAND_GO;
			finish();
		} else if (id == R.id.restart_game){
			mCommand |= RESULT_COMMAND_RESTART;
			finish();
		} else if (id == R.id.historys) {
			
		} else if (id == R.id.sound_effects) {
			boolean isOn = mGameManager.switchSound(!mGameManager.isSoundOn());
			setSoundText(isOn);
		} else if (id == R.id.settings_save) {
			/*AlertDialog.Builder b =*/ new Builder(this)
				.setTitle(R.string.settings_alert_save_title)
				.setMessage(R.string.settings_alert_save_message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mGameManager.saveGame();
						mCommand = RESULT_COMMAND_SAVE;
						finish();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
		} else if (id == R.id.settings_load) {
			/*AlertDialog.Builder b =*/ new Builder(this)
			.setTitle(R.string.settings_alert_read_title)
			.setMessage(R.string.settings_alert_read_message)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mCommand |= RESULT_COMMAND_LOAD;
					finish();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.show();
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
	
	private void setSoundText(boolean isOn) {
		String text = getString(R.string.sound_effects);
		String state = getString(isOn?R.string.sound_on:R.string.sound_off);
		mSoundEffect.setText(String.format(text, state));
	}

	@Override
	public void finish() {
		mResult.putInt("command", mCommand);
		Intent i = new Intent();
		i.putExtras(mResult);
		setResult(RESULT_OK,i);
		super.finish();
	}
	
	private boolean showAds() {
		return AppConnect.getInstance(this)
				.getConfig("waps_config_2048_ads", "1").equals("1");
	}
}
