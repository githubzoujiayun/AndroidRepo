package com.jobs.lib_v1.tab;

import com.jobs.lib_v1.misc.ViewUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * 底部选项卡切换控件
 * 
 * 使用方法举例：

{{{ 使用方法开始
DEMO 文件 1 (TabHostActivity.java)：

public class TabHostActivity extends TabActivity {
	TabSwitcher mSwitcher = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_view);

		mSwitcher = (TabSwitcher) getTabHost();

		mSwitcher.AddTab(PathLikeComposerActivity.class, R.drawable.tab_item_01);
		mSwitcher.AddTab(NetworkListActivity.class, R.drawable.tab_item_02);
		mSwitcher.AddTab(PathLikeComposerActivity.class, R.drawable.tab_item_03);
		mSwitcher.AddTab(NetworkListActivity.class, R.drawable.tab_item_04);
		mSwitcher.AddTab(PathLikeComposerActivity.class, R.drawable.tab_item_05);

		mSwitcher.setTabSliderResource(R.drawable.tab_slider); // 滑块图片
		mSwitcher.setTabBarResource(R.drawable.tab_bar); // bar的背景图片
	}
}

DEMO 文件2 (res/drawable/tab_item_01.xml)：

<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/tab_on_01" android:state_checked="true"/>
    <item android:drawable="@drawable/tab_off_01" android:state_checked="false"/>
</selector>

DEMO 文件3 (res/layout/tabhost_view.xml)：

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical" >
    <com.jobs.lib_v1.tab.TabSwitcher
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"/>
</LinearLayout>

}}} 使用方法结束

 * @author solomon.wen
 * @date 2013-05-28
 */
public class TabSwitcher extends TabHost implements View.OnClickListener {
	// 控件
	private LinearLayout mLinearLayout = null;
	private FrameLayout mFrameLayout = null;
	private TabWidget mTabWidget = null;
	private ImageView mTabBackgroundView = null;
	private RelativeLayout mRelativeLayout = null;
	private RadioGroup mRadioGroup = null;

	// tab 序列
	private int mTabSequence = 0;
	private int mSelectedIndex = 0;

	// 点击事件监听器
	private TabSwitcherListener mOnClickListener = null;

	// tab 数量
	private int mTabHeight = 44;
	private int mTabMaxCount = 6;
	private Class<?>[] mTabClass = null;
	private Drawable[] mTabResource = null;
	private Drawable mTabSliderDrawable = null;

	public TabSwitcher(Context context) {
		super(context);
		initSwitcher(context);
	}

	public TabSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSwitcher(context);
	}

	/**
	 * 添加一个页卡
	 * 
	 * @param tabCls
	 * @param tabBgResID
	 * @return 返回页卡的序列
	 */
	public int AddTab(Class<?> tabCls, int tabBgResID) {
		return AddTab(tabCls, getResources().getDrawable(tabBgResID));
	}

	/**
	 * 添加一个页卡
	 * 
	 * @param tabCls
	 * @param tabBgResID
	 * @return 返回页卡的序列
	 */
	public int AddTab(Class<?> tabCls, Drawable tabBg) {
		if (null == mTabClass) {
			setTabCountAndHeight(mTabMaxCount, mTabHeight);
		}

		if (mTabSequence >= mTabMaxCount) {
			return -1;
		}

		mTabClass[mTabSequence] = tabCls;
		mTabResource[mTabSequence] = tabBg;

		return mTabSequence++;
	}

	/**
	 * 设置滑块资源
	 * 
	 * @param resID
	 */
	public void setTabSliderResource(int resID) {
		setTabSliderDrawable(getResources().getDrawable(resID));
	}

	/**
	 * 设置滑块资源
	 * 
	 * @param resID
	 */
	public void setTabSliderDrawable(Drawable d) {
		mTabSliderDrawable = d;
	}

	/**
	 * 设置页卡背景
	 * 
	 * @param resID
	 */
	public void setTabBarResource(int resID) {
		mRadioGroup.setBackgroundResource(resID);
	}

	/**
	 * 设置页卡背景
	 * 
	 * @param d
	 */
	public void setTabBarDrawable(Drawable d) {
		ViewUtil.setBackground(mRadioGroup, d);
	}

	/**
	 * 点击事件监听器
	 * 
	 * @param l
	 */
	public void setOnclickListener(TabSwitcherListener l) {
		mOnClickListener = l;
	}

	/**
	 * 获取当前激活的页卡索引
	 * 
	 * @return int
	 */
	public int getSelectedTabIndex() {
		return mSelectedIndex;
	}

	/**
	 * 设置一个激活的页卡索引 (不会触发监听事件)
	 * 
	 * @param index
	 */
	public void setTabIndexSelected(int index) {
		if (index < 0 || index >= mTabSequence) {
			return;
		}

		String strTabIndex = "tab_" + index;

		setCurrentTabByTag(strTabIndex);

		if (null != mTabBackgroundView) {
			int moveFromX = mTabBackgroundView.getWidth() * mSelectedIndex;
			int moveToX = mTabBackgroundView.getWidth() * index;

			TranslateAnimation anim = new TranslateAnimation(moveFromX, moveToX, 0, 0);
			anim.setDuration(200);
			anim.setFillAfter(true);

			mTabBackgroundView.startAnimation(anim);
		}

		if (index != mRadioGroup.getCheckedRadioButtonId()) {
			mRadioGroup.check(index);
		}

		mSelectedIndex = index;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof RadioButton) {
			int tabIndex = v.getId();

			if (tabIndex != mSelectedIndex) {
				setTabIndexSelected(tabIndex);
			}

			if (null != mOnClickListener) {
				mOnClickListener.onClick(this, tabIndex);

				if (tabIndex != mSelectedIndex) {
					mOnClickListener.onChange(this, tabIndex);
				} else {
					mOnClickListener.onReClick(this, tabIndex);
				}
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		applyAllTabs();
	}

	private void applyAllTabs() {
		post(new Runnable() {
			@Override
			public void run() {
				if (mTabWidget.getChildCount() > 0) {
					clearAllTabs();
				}

				if (mTabClass == null) {
					return;
				}

				int tabBarWidth = getWidth();
				float density = getResources().getDisplayMetrics().density;
				int tabWidth = tabBarWidth < 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (tabBarWidth / mTabSequence);
				int tabHeight = (int) (mTabHeight * density);

				for (int i = 0; i < mTabSequence; i++) {
					createTab(mTabClass[i], mTabResource[i], i, tabWidth, tabHeight);
				}

				if (null != mTabBackgroundView) {
					mRelativeLayout.removeView(mTabBackgroundView);
					mTabBackgroundView = null;
				}

				if (null != mTabSliderDrawable) {
					ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(tabWidth, tabHeight);
					mTabBackgroundView = new ImageView(getContext());
					mTabBackgroundView.setLayoutParams(param);
					mTabBackgroundView.setImageDrawable(mTabSliderDrawable);
					mRelativeLayout.addView(mTabBackgroundView);
				}
			}
		});
	}

	private void createTab(Class<?> tabCls, Drawable tabBg, int tabIndex, int tabWidth, int tabHeight) {
		String strTabIndex = "tab_" + tabIndex;

		TabSpec tab = newTabSpec(strTabIndex).setIndicator(strTabIndex).setContent(new Intent(getContext(), tabCls));
		super.addTab(tab);

		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(tabWidth, tabHeight);

		RadioButton radio = new RadioButton(getContext());
		radio.setLayoutParams(param);
		radio.setId(tabIndex);
		ViewUtil.setBackground(radio, tabBg);
		radio.setChecked(tabIndex < 1);
		radio.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
		radio.setOnClickListener(this);

		mRadioGroup.addView(radio);
	}

	private void setTabCountAndHeight(int tabCount, int height) {
		mTabHeight = height < 20 ? 20 : height;
		mTabMaxCount = tabCount < 1 ? 1 : tabCount;

		mTabClass = new Class<?>[mTabMaxCount];
		mTabResource = new Drawable[mTabMaxCount];
		mTabSequence = 0;
	}

	private void initSwitcher(Context context) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		setLayoutParams(params);
		setId(android.R.id.tabhost);

		initLinearLayout(context);
		initFrameLayout(context);
		initTabWidget(context);
		initRelativeLayout(context);
		initRadioGroup(context);
	}

	private void initRadioGroup(Context context) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		mRadioGroup = new RadioGroup(context);
		mRadioGroup.setLayoutParams(params);
		mRadioGroup.setGravity(Gravity.CENTER_VERTICAL);
		mRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

		mRelativeLayout.addView(mRadioGroup);
	}

	private void initRelativeLayout(Context context) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		mRelativeLayout = new RelativeLayout(context);
		mRelativeLayout.setLayoutParams(params);
		mRelativeLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

		mLinearLayout.addView(mRelativeLayout);
	}

	private void initTabWidget(Context context) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		mTabWidget = new TabWidget(context);
		mTabWidget.setLayoutParams(params);
		mTabWidget.setId(android.R.id.tabs);
		mTabWidget.setVisibility(View.GONE);

		mLinearLayout.addView(mTabWidget);
	}

	private void initFrameLayout(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);

		mFrameLayout = new FrameLayout(context);
		mFrameLayout.setLayoutParams(params);
		mFrameLayout.setId(android.R.id.tabcontent);

		mLinearLayout.addView(mFrameLayout);
	}

	private void initLinearLayout(Context context) {
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setLayoutParams(params);
		mLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		if (isInEditMode()) {
			mLinearLayout.setBackgroundResource(android.R.drawable.editbox_background);
		}

		addView(mLinearLayout);
	}
}