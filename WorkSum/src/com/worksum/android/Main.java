package com.worksum.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.misc.loc.AppLocation;
import com.jobs.lib_v1.misc.loc.AppLocationManager;
import com.netease.nim.uikit.common.ui.drop.DropCover;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.worksum.android.company.CompanyInfoPage;
import com.worksum.android.company.CustomerAddAdsFragment;
import com.worksum.android.company.CustomerJobs;
import com.worksum.android.company.SearchEmployee;
import com.worksum.android.controller.LoginManager;
import com.worksum.android.debug.FragmentDebug;
import com.worksum.android.nim.helper.SystemMessageUnreadManager;
import com.worksum.android.nim.recent.RecentContactsFragment;
import com.worksum.android.nim.reminder.ReminderItem;
import com.worksum.android.nim.reminder.ReminderManager;
import com.worksum.android.utils.Utils;
import com.worksum.android.views.TabFragmentHost;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Main extends GeneralActivity implements ReminderManager.UnreadNumChangedCallback{

	private static long mFirstTimeOfClickBackKey;
	private static Toast mExitTipsLayer;
	private TabFragmentHost mTabHost;

	private LoginManager.LoginType mLoginType = LoginManager.LoginType.U;


    private static final int PERSON_FRAGMENT_COUNT = 4;

	private int iconIds[] = new int[]{
            R.drawable.indicator_job_selector,
			R.drawable.indicator_apply_selector,
            R.drawable.indicator_msg_selector,
			R.drawable.indicator_me_selector,

            R.drawable.indicator_jobs_selector,
            R.drawable.indicator_chat_selector,
            R.drawable.indicator_add_ads_selector,
            R.drawable.indicator_search_employee_selector,
            R.drawable.indicator_account_selector
    };

	private int titleIds[] = new int[] { R.string.tab_joblist,
			R.string.tab_apply_record, R.string.tab_inbox, R.string.tab_self,R.string.tab_my_jobs,R.string.tab_chat,R.string.tab_add_ads,R.string.tab_search_employee,R.string.tab_account };
	
	private String tabSpace[] = new String[]{
			JobListFragment.class.getSimpleName(),
			MyJobsFragment.class.getSimpleName(),
			RecentContactsFragment.class.getSimpleName(),
			MeFragment.class.getSimpleName(),

            CustomerJobs.class.getSimpleName(),
            RecentContactsFragment.class.getSimpleName(),
            CustomerAddAdsFragment.class.getSimpleName(),
            SearchEmployee.class.getSimpleName(),
            CompanyInfoPage.class.getSimpleName()

	};
	
	private Class fragments[] = {
			JobListFragment.class,
			MyJobsFragment.class,
			RecentContactsFragment.class,
			MeFragment.class,

            CustomerJobs.class,
			RecentContactsFragment.class,
			CustomerAddAdsFragment.class,
            SearchEmployee.class,
			CompanyInfoPage.class
	};

	private Observer<List<IMMessage>> receiveMessageObserver =  new Observer<List<IMMessage>>() {
		@Override
		public void onEvent(List<IMMessage> imMessages) {
			Utils.playSound(Utils.SOUND_NOTIFY_MSG);
		}
	};


	public static void showMain(Context context) {
		Intent intent = new Intent(context,Main.class);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //调试界面
        if (FragmentDebug.DEBUG) {
			setContentView(R.layout.debug_main);
			findViewById(R.id.debug_action).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentDebug.show(Main.this);
				}
			});
			FragmentDebug.show(this);
            return;
        }

		mLoginType = LoginManager.getInstance().getLoginType();

        setContentView(R.layout.main);
        setupTabHost();

		initUnreadCover();
		registerMsgUnreadInfoObserver(true);
		registerSystemMessageObservers(true);
		requestSystemMessageUnreadCount();
		registerMessageObservers(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		registerMsgUnreadInfoObserver(false);
		registerSystemMessageObservers(false);
		requestSystemMessageUnreadCount();
		registerMessageObservers(false);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		ArrayList<IMMessage> msgs = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
		int index = 2;
		if (LoginManager.getInstance().getLoginType() == LoginManager.LoginType.C) {
			index = 1;
		}
		if(msgs != null) {
			setTab(index);
		}
	}

	public void setTab(int index) {
		mTabHost.setCurrentTab(index);
	}

	private void setupTabHost() {
		mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		LayoutInflater inflater = LayoutInflater.from(this);

        int index = 0;
		int count = PERSON_FRAGMENT_COUNT;
        if (mLoginType == LoginManager.LoginType.C) {
            index = PERSON_FRAGMENT_COUNT;
			count = fragments.length - PERSON_FRAGMENT_COUNT;
        }
		for (int i = 0; i < count; i++,index++) {
			
			TabSpec tabSpec = mTabHost.newTabSpec(tabSpace[index]).setIndicator(getIndicatorView(inflater,index));
			mTabHost.addTab(tabSpec, fragments[index], null);
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(getResources().getColor(R.color.white_ffffff));
		}
		mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				GeneralFragment fragment = (GeneralFragment) fragmentManager.findFragmentByTag(tabId);
				if (fragment != null && fragment.isAdded()) {
					fragment.onTabSelect();
				}
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken()
						,InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
	}
	
	private View getIndicatorView(LayoutInflater inflater,int index) {
		LinearLayout indicator = (LinearLayout) inflater.inflate(
				R.layout.indicator_view, null);
		ImageView icon = (ImageView) indicator
				.findViewById(R.id.indicator_icon);
		icon.setImageResource(iconIds[index]);
		TextView title = (TextView) indicator
				.findViewById(R.id.indicator_title);
		title.setText(titleIds[index]);
		title.setTextColor(getResources().getColor(R.color.white_7f7f7f));
		return indicator;
	}

	public void onUserStatusChanged(Integer loginType) {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getFragments() == null) {
			return;
		}
		for (Fragment fragment : manager.getFragments()) {
			if (fragment instanceof GeneralFragment) {
				((GeneralFragment)fragment).onUserStatusChanged(loginType);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}


	public void onLocationChanged() {
		AppLocation location = AppLocationManager.getManager().getCurrentLocation();
		AppUtil.print("currentLocation : " + location);

		new MapThread(location.lat,location.lng).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getFragments() == null || fragmentManager.getFragments().size() <= 0) {
                return super.onKeyDown(keyCode,event);
            }
			for(Fragment fragment : fragmentManager.getFragments()) {
				GeneralFragment generalFragment = (GeneralFragment) fragment;
				if (generalFragment.isVisible()) {
					generalFragment.onBackPressed();
					return true;
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (!getSupportFragmentManager().popBackStackImmediate()) {
			appConfirmExit();
		}
	}

	@Override
	public void onUnreadNumChanged(ReminderItem item) {

	}

	class MapThread extends Thread {
		double mLat,mLng;


		private HttpResponse httpResponse = null;
		private HttpEntity httpEntity = null;
		private MapThread mapThread;
		private Handler handler;
		private String result;

		public MapThread(Double lat,double lng) {
			mLat = lat;
			mLng = lng;
		}

		private String mapUriStr = "http://maps.google.cn/maps/api/geocode/json?latlng={0},{1}&sensor=true&language=zh-CN";

		@Override

		public void run() {
			super.run();
			String uriStr = MessageFormat.format(mapUriStr, mLat, mLng);
			HttpGet httpGet = new HttpGet(uriStr);//生成一个请求对象
			HttpClient httpClient = new DefaultHttpClient(); //生成一个Http客户端对象

			try {
				httpResponse = httpClient.execute(httpGet); //使用Http客户端发送请求对象
				httpEntity = httpResponse.getEntity(); //获取响应内容
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
				result = "";

				String line = "";
				while ((line = reader.readLine()) != null) {
					result += line;
				}
				Log.v("地址:", result);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerMessageObservers(true);
		enableMsgNotification(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		registerMessageObservers(false);
		enableMsgNotification(true);
	}

	private void enableMsgNotification(boolean enable) {
		if (enable) {
			/**
			 * 设置最近联系人的消息为已读
			 *
			 * @param account,    聊天对象帐号，或者以下两个值：
			 *                    {@link #MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
			 *                    {@link #MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
			 */
			NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
		} else {
			NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
		}
	}

	private void registerMessageObservers(boolean register) {
		NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(receiveMessageObserver,register);
	}

	/**
	 * 查询系统消息未读数
	 */
	private void requestSystemMessageUnreadCount() {
		int unread = NIMClient.getService(SystemMessageService.class).querySystemMessageUnreadCountBlock();
		SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unread);
		ReminderManager.getInstance().updateContactUnreadNum(unread);
	}

	/**
	 * 注册/注销系统消息未读数变化
	 *
	 * @param register
	 */
	private void registerSystemMessageObservers(boolean register) {
		NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver,
				register);
	}

	private Observer<Integer> sysMsgUnreadCountChangedObserver = new Observer<Integer>() {
		@Override
		public void onEvent(Integer unreadCount) {
			SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
			ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
		}
	};

	/**
	 * 注册未读消息数量观察者
	 */
	private void registerMsgUnreadInfoObserver(boolean register) {
		if (register) {
			ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
		} else {
			ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this);
		}
	}

	/**
	 * 初始化未读红点动画
	 */
	private void initUnreadCover() {
		DropManager.getInstance().init(this, (DropCover) findViewById(R.id.unread_cover),
				new DropCover.IDropCompletedListener() {
					@Override
					public void onCompleted(Object id, boolean explosive) {
						if (id == null || !explosive) {
							return;
						}

						if (id instanceof RecentContact) {
							RecentContact r = (RecentContact) id;
							NIMClient.getService(MsgService.class).clearUnreadCount(r.getContactId(), r.getSessionType());
							LogUtil.i("HomeFragment", "clearUnreadCount, sessionId=" + r.getContactId());
						} else if (id instanceof String) {
							if (((String) id).contentEquals("0")) {
								List<RecentContact> recentContacts = NIMClient.getService(MsgService.class).queryRecentContactsBlock();
								for (RecentContact r : recentContacts) {
									if (r.getUnreadCount() > 0) {
										NIMClient.getService(MsgService.class).clearUnreadCount(r.getContactId(), r.getSessionType());
									}
								}
								LogUtil.i("HomeFragment", "clearAllUnreadCount");
							} else if (((String) id).contentEquals("1")) {
								NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
								LogUtil.i("HomeFragment", "clearAllSystemUnreadCount");
							}
						}
					}
				});
	}


	public static void appConfirmExit() {
		// 按两次“返回”键退出系统规则
		//
		// 1. 本次按键距离上次按键时间超过 2 秒钟，则弹出提示浮层“连按两次退出”。
		// 2. 本次按键距离上次按键时间小于50毫秒，则不做任何操作。
		// 3. 本次按键距离上次按键时间在50毫秒到2秒钟之间的，则退出应用。
		//
		long secondTime = System.currentTimeMillis();
		long internalTime = secondTime - mFirstTimeOfClickBackKey;

		if (internalTime < 50) {
			return;
		} else if (internalTime >= 50 && internalTime <= 2000) {
			AppMain.appExit();
		} else {
			mFirstTimeOfClickBackKey = secondTime;

			hiddenExitTipsLayer();

			mExitTipsLayer = Toast.makeText(AppMain.getApp(), R.string.common_text_system_exit_msg, Toast.LENGTH_LONG);
			mExitTipsLayer.show();
		}
	}

	/**
	 * 消去之前的 Toast 提示信息
	 */
	private static void hiddenExitTipsLayer() {
		if (null != mExitTipsLayer) {
			try {
				mExitTipsLayer.cancel();
			} catch (Throwable e) {
			}

			mExitTipsLayer = null;
		}
	}

	public static void resetFirstTimeOfClickBackKey() {
		mFirstTimeOfClickBackKey = 0;
		hiddenExitTipsLayer();
	}
}
