package com.android.worksum;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.worksum.views.TabFragmentHost;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.misc.loc.AppLocation;
import com.jobs.lib_v1.misc.loc.AppLocationManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class Main extends GeneralActivity {

	private TabFragmentHost mTabHost;

	private int iconIds[] = new int[]{R.drawable.indicator_job_selector,
			R.drawable.indicator_apply_selector, R.drawable.indicator_msg_selector,
			R.drawable.indicator_me_selector};

	private int titleIds[] = new int[] { R.string.tab_joblist,
			R.string.tab_apply_record, R.string.tab_inbox, R.string.tab_self };
	
	private String tabSpace[] = new String[]{
			"JobListFragment","ApplyRecordFragment","InboxFragment","SelfFragment"
	};
	
	private Class fragments[] = {
			JobListFragment.class,ApplyRecordFragment.class,
			InboxFragment.class,SelfFragment.class
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupTabHost();
	}

	private void setupTabHost() {
		mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		LayoutInflater inflater = LayoutInflater.from(this);
		 
		for (int i = 0; i < fragments.length; i++) {
			
			TabSpec tabSpec = mTabHost.newTabSpec(tabSpace[i]).setIndicator(getIndicatorView(inflater,i));
			mTabHost.addTab(tabSpec, fragments[i], null);
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(getResources().getColor(R.color.blue_main));
		}
		mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                GeneralFragment fragment = (GeneralFragment)fragmentManager.findFragmentByTag(tabId);
                if (fragment != null && fragment.isAdded()) {
                    fragment.onTabSelect();
                }
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
		title.setTextColor(getResources().getColor(R.color.white_ffffff));
		return indicator;
	}

	public void onUserStatusChanged(Integer loginType) {
		FragmentManager manager = getSupportFragmentManager();
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
}
