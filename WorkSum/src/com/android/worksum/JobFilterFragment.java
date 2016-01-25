package com.android.worksum;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.worksum.views.SelectView;

/**
 * 职位过滤
 * chao.qin
 * 15/12/20.
 */
public class JobFilterFragment extends TitlebarFragment implements SeekBar.OnSeekBarChangeListener {

    private SelectView mSelectView;
    private SeekBar mSalarySeek;
    private TextView mSalaryView;

    @Override
    public int getLayoutId() {
        return R.layout.job_filter;
    }

    @Override
    void setupView(View v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle("");
        setActionLeftText(R.string.job_filter_done);

        mSalarySeek = (SeekBar) v.findViewById(R.id.filter_pay_seek);
        mSalaryView = (TextView) v.findViewById(R.id.filter_pay_size);
        mSalarySeek.setOnSeekBarChangeListener(this);
        mSalarySeek.setMax(5000);
    }

    @Override
    protected void onActionLeft() {
        super.onActionLeft();
        finish();
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
}
