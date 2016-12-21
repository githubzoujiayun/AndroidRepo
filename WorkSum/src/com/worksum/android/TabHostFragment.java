package com.worksum.android;

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

import com.worksum.android.annotations.LayoutID;
import com.worksum.android.company.CustomerJobs;

import java.util.ArrayList;
import java.util.List;

/**
 */

@LayoutID(R.layout.tab_host)
public abstract class TabHostFragment extends TitlebarFragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;

    protected List<GeneralFragment> mFragmentList = new ArrayList<>();

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        setupTabHost(vg);

        setupViewPager(vg);
    }

    private void setupTabHost(View v) {
        FragmentActivity activity = getActivity();
        mTabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);
        mTabHost.setup(activity, getChildFragmentManager(), R.id.realtabcontent);

        for (int i = 0; i < getFragments().length; i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View indicator = inflater.inflate(R.layout.applied_tab_indicator, mTabHost.getTabWidget(), false);
            TextView text = (TextView) indicator.findViewById(R.id.indicator_title);
            text.setText(getTitleIds()[i]);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getTabSpaces()[i]).setIndicator(indicator);
            mTabHost.addTab(tabSpec, CustomerJobs.EmptyFragment.class, null);
        }
        mTabHost.setOnTabChangedListener(this);
    }

    protected abstract Class[] getFragments();

    protected abstract int[] getTitleIds();

    private String[] getTabSpaces() {
        checkTabHost();
        int length = getFragments().length;
        String tabSpaces[] = new String[length];
        for (int i = 0;i < length;i++) {
            tabSpaces[i] = getFragments()[i].getSimpleName();
        }
        return tabSpaces;
    }

    private void setupViewPager(View v) {
        checkTabHost();

        for(Class fragment: getFragments()) {
            try {
                mFragmentList.add((GeneralFragment) fragment.newInstance());
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);

        mViewPager.setVisibility(View.VISIBLE);

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

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return super.instantiateItem(container, position);
            }
        });
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
        checkTabHost();
        for (int i = 0; i < getFragments().length; i++) {
            if (getFragments()[i].getSimpleName().equals(tabId)) {
                mViewPager.setCurrentItem(i, true);
                break;
            }
        }
//        dispatchTabSelect();//// TODO: 16/3/17 此处是已申请Tab事件，暂时简单和底部相同方式处理
    }

    @Override
    public void onTabSelect() {
//        dispatchTabSelect();//// TODO: 16/3/17 此处是底部Tab选中事件
    }

    private void checkTabHost() {
        if (getFragments() == null) {
            throw new IllegalStateException("getFragments() cannot return null.");
        }

        if (getTitleIds() == null) {
            throw new IllegalStateException("getTitleIds() cannot return null.");
        }

        int length = getFragments().length;

        if (getTitleIds().length != length) {
            throw new IllegalStateException("fragment and titleId's length must the same.");
        }

        for (Class fragment: getFragments()) {
            if (!GeneralFragment.class.isAssignableFrom(fragment)) {
                throw new IllegalStateException("getFragments() should return an array of GeneralFragment instance.");
            }
        }
    }

    private void dispatchTabSelect() {
        checkTabHost();
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
        checkTabHost();
        FragmentManager fragmentManager = getChildFragmentManager();
        for (Fragment fragment: fragmentManager.getFragments()) {
            if (fragment != null) {
                ((GeneralFragment)fragment).onUserStatusChanged(loginType);
            }
        }
    }
}
