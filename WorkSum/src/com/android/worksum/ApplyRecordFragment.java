package com.android.worksum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.worksum.controller.UserCoreInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ApplyRecordFragment extends TitlebarFragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;

//    private Class fragments[] = {
//            AppliedFragment.class, PassedFragment.class,
//            RefusedFragment.class
//    };
//
//
//    private int titleIds[] = new int[]{R.string.applied_apply,
//            R.string.applied_passed, R.string.applied_refused};
//
//    private String tabSpace[] = new String[]{
//            "AppliedFragment", "PassedFragment", "RefusedFragment"
//    };

    private Class fragments[] = {
            AppliedFragment.class, PassedFragment.class
    };


    private int titleIds[] = new int[]{R.string.applied_apply,
            R.string.applied_passed};

    private String tabSpace[] = new String[]{
            "AppliedFragment", "PassedFragment"
    };

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();

    @Override
    public int getLayoutId() {
        return R.layout.apply;
    }

    private void setupViewPager(View v) {
        AppliedFragment af = new AppliedFragment();
        PassedFragment pf = new PassedFragment();
        RefusedFragment rf = new RefusedFragment();
        mFragmentList.add(af);
        mFragmentList.add(pf);
//        mFragmentList.add(rf);

        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);

        mViewPager.setOnPageChangeListener(this);

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentList.get(i);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);

        setTitle(R.string.title_applied);
        setupTabHost(v);

        setupViewPager(v);
    }

    private void setupTabHost(View v) {
        FragmentActivity activity = getActivity();
        mTabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);
        mTabHost.setup(activity, getChildFragmentManager(), R.id.realtabcontent);

        for (int i = 0; i < fragments.length; i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View indicator = inflater.inflate(R.layout.applied_tab_indicator, mTabHost.getTabWidget(), false);
            TextView text = (TextView) indicator.findViewById(R.id.indicator_title);
            text.setText(titleIds[i]);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabSpace[i]).setIndicator(indicator);
            mTabHost.addTab(tabSpec, fragments[i], null);
        }
        mTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        mTabHost.setCurrentTab(i);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabChanged(String tabId) {
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i].getSimpleName().equals(tabId)) {
                mViewPager.setCurrentItem(i, true);
                return;
            }
        }
    }

    @Override
    public void onTabSelect() {
        dispatchTabSelect();
    }

    private void dispatchTabSelect() {
        FragmentManager fragmentManager = getChildFragmentManager();
        for (Fragment fragment: fragmentManager.getFragments()) {
            GeneralFragment generalFragment = (GeneralFragment)fragment;
            if (generalFragment != null && generalFragment.isAdded()) {
                generalFragment.onTabSelect();
            }
        }
    }

    @Override
    public void onUserStatusChanged(int loginType) {
        dispatchUserStatusChanged(loginType);
    }

    private void dispatchUserStatusChanged(int loginType) {
        FragmentManager fragmentManager = getChildFragmentManager();
        for (String tag: tabSpace) {
            GeneralFragment fragment = (GeneralFragment) fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                fragment.onUserStatusChanged(loginType);
            }
        }
    }
}
