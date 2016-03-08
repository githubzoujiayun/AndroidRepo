package com.android.worksum;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.worksum.apis.DictsApi;
import com.android.worksum.views.SelectView;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;

/**
 * 职位过滤
 * chao.qin
 * 15/12/20.
 */
public class JobFilterFragment extends TitlebarFragment implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener, View.OnTouchListener {

    private SelectView mSelectView;
    private SeekBar mSalarySeek;
    private TextView mSalaryView;
    private Button mResetButton;

    private RadioGroup mSortGroup;
    private RadioGroup mScheduleGroup;
    private RadioGroup mDistanceGroup;

    private CheckBox mWorkTmpRadio;
    private CheckBox mWorkPartRadio;
    private CheckBox mWorkFullRadio;

    private DataListView mDictListView;


    @Override
    public int getLayoutId() {
        return R.layout.job_filter;
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle("");
        setActionLeftDrawable(R.drawable.titlebar_action_done);

        mSalarySeek = (SeekBar) v.findViewById(R.id.filter_pay_seek);
        mSalaryView = (TextView) v.findViewById(R.id.filter_pay_size);
        mSalarySeek.setOnSeekBarChangeListener(this);
        mSalarySeek.setMax(5000);

        mResetButton = (Button) findViewById(R.id.job_filter_refilter);
        mResetButton.setOnClickListener(this);
        mResetButton.setOnTouchListener(this);

        mSortGroup = (RadioGroup) findViewById(R.id.sort_group);
//        mScheduleGroup = (RadioGroup) findViewById(R.id.filter_schedule_group);
        mDistanceGroup = (RadioGroup) findViewById(R.id.filter_distance_group);
        mSortGroup.setOnCheckedChangeListener(this);
//        mScheduleGroup.setOnCheckedChangeListener(this);
        mDistanceGroup.setOnCheckedChangeListener(this);

        mWorkTmpRadio = (CheckBox) findViewById(R.id.filter_schedule_temp);
        mWorkFullRadio = (CheckBox) findViewById(R.id.filter_schedule_full);
        mWorkPartRadio = (CheckBox) findViewById(R.id.filter_schedule_part);
        mWorkPartRadio.setOnCheckedChangeListener(this);
        mWorkFullRadio.setOnCheckedChangeListener(this);
        mWorkTmpRadio.setOnCheckedChangeListener(this);
        mWorkFullRadio.setOnClickListener(this);
        mWorkPartRadio.setOnClickListener(this);
        mWorkTmpRadio.setOnClickListener(this);

        setupIndustry();
    }

    private void setupIndustry() {
        mDictListView = (DataListView) findViewById(R.id.industries_list);
        mDictListView.setOnItemClickListener(this);
        mDictListView.setDataCellClass(IndustryCell.class, this);
        mDictListView.setAllowAutoTurnPage(false);
        mDictListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mDictListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {

                return DictsApi.getFunctionType();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.job_filter_refilter:
                mSortGroup.check(R.id.sort_type_date);
                mDistanceGroup.check(R.id.filter_distance_any);
                mWorkPartRadio.setChecked(false);
                mWorkTmpRadio.setChecked(false);
                mWorkFullRadio.setChecked(false);
                mDictListView.clearChoices();
                mSalarySeek.setProgress(0);
                mSalaryView.setText("$0.00");
                mDictListView.statusChangedNotify();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        double salaryValue = (double)i / 100;
        String salary = String.format("%1$.2f",salaryValue);
        mSalaryView.setText("$" + salary);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getId()) {
            case R.id.sort_group:

                break;
            case R.id.filter_schedule_group:

                break;
            case R.id.filter_distance_group:

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mDictListView.setItemChecked(position,mDictListView.isItemChecked(position));
        mDictListView.statusChangedNotify();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == mResetButton) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.filter_reset_button_scale_animator);
                    animator.setTarget(mResetButton);
                    animator.start();
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.filter_reset_button_scale_animator_up);
                    animator.setTarget(mResetButton);
                    animator.start();
                break;
            }
        }
        return false;
    }

    private class IndustryCell extends DataListCell{
        boolean mChecked;

        private  TextView mIndustryName;
        private ImageView mCheckedView;
        /**
         * 获取单元格对应的 layoutID
         * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
         * getCellViewLayoutID 方法返回0时会调用 createCellView
         */
        @Override
        public int getCellViewLayoutID() {
            return R.layout.industry_item;
        }

        /**
         * 绑定单元格视图中的控件到变量
         * 该方法由子类实现
         */
        @Override
        public void bindView() {
            mIndustryName = (TextView) findViewById(R.id.industry_name);
            mCheckedView = (ImageView) findViewById(R.id.industry_checked);
        }

        /**
         * 绑定单元格数据到控件
         * 该方法由子类实现
         */
        @Override
        public void bindData() {
            mIndustryName.setText(mDetail.getString("Cname"));
            mCheckedView.setVisibility(View.GONE);
            if(isChecked()) {
                mCheckedView.setVisibility(View.VISIBLE);
            }
        }

        private boolean isChecked() {
            DataListView listView = mAdapter.getListView();
            return listView.isItemChecked(mPosition);
        }
    }
}
