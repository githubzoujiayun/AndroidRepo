package com.android.worksum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class ApplyRecordFragment extends TitlebarFragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;

    private Class fragments[] = {
            AppliedFragment.class, PassedFragment.class,
            RefusedFragment.class
    };


    private int titleIds[] = new int[]{R.string.applied_apply,
            R.string.applied_passed, R.string.applied_refused};

    private String tabSpace[] = new String[]{
            "AppliedFragment", "PassedFragment", "RefusedFragment"
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
        mFragmentList.add(rf);

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
    void setupView(View v, Bundle savedInstanceState) {
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

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabSpace[i]).setIndicator(getString(titleIds[i]));
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
}
