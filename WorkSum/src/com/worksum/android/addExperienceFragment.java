package com.worksum.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.WorkExpApi;
import com.worksum.android.utils.Utils;
import com.worksum.android.views.DictTableRow;
import com.worksum.android.views.EditTableRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author chao.qin
 */

@LayoutID(R.layout.add_experience)
@Titlebar(titleId = R.string.add_experience_title, leftDrawableId = R.drawable.common_nav_arrow, rightTextId = R.string.action_save)
public class AddExperienceFragment extends TitlebarFragment {

    public static final int REQUEST_CODE_ADD_EXP = 1;

    private EditTableRow mCompanyView;
    private EditTableRow mJobNameView;
    private DictTableRow mStartTimeView;
    private DictTableRow mEndTimeView;
    private TextView mMemoView;


    private int mWorkId = -1;

    public static void showAddExperience(Fragment fragment, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        Intent intent = new Intent(fragment.getActivity(), FragmentContainer.class);
        intent.putExtra(KEY_FRAGMENT, AddExperienceFragment.class);
        intent.putExtras(extras);
        fragment.startActivityForResult(intent,REQUEST_CODE_ADD_EXP);
    }

    @Override
    public void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mCompanyView = (EditTableRow) findViewById(R.id.add_experience_company_name);
        mJobNameView = (EditTableRow) findViewById(R.id.add_experience_job_name);
        mStartTimeView = (DictTableRow) findViewById(R.id.add_experience_work_day);
        mEndTimeView = (DictTableRow) findViewById(R.id.add_experience_work_endtime);
        mMemoView = (TextView) findViewById(R.id.add_experience_work_content);

        mStartTimeView.setOnClickListener(this);
        mEndTimeView.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            DataItemDetail detail = bundle.getParcelable("detail");
            if (detail != null) {
                mCompanyView.setText(detail.getString(ExperienceHomeFragment.API_KEY_EXP_COMPANY));
                mJobNameView.setText(detail.getString(ExperienceHomeFragment.API_KEY_EXP_POSITION));
                mStartTimeView.setText(Utils.dateFormat(detail.getString(ExperienceHomeFragment.API_KEY_EXP_FROM_TIME)));
                mEndTimeView.setText(Utils.dateFormat(detail.getString(ExperienceHomeFragment.API_KEY_EXP_TO_TIME)));
                mMemoView.setText(detail.getString(ExperienceHomeFragment.API_KEY_EXP_MEMO));

                mWorkId = detail.getInt(ExperienceHomeFragment.API_KEY_EXP_ID);
                setTitle(R.string.add_experience_title_modify);
            }

        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mStartTimeView || view == mEndTimeView) {

            final DictTableRow row = (DictTableRow) view;

            String rowText = row.getText().toString();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINESE);
            Date date;
            try {
                date = dateFormat.parse(rowText);
            } catch (ParseException e) {
                date = new Date();
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;


            final YearsPickerDialog datePicker = new YearsPickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
                    String date = year + "-" + (monthOfYear + 1);
                    row.setText(date);

                }
            },year,month);


            datePicker.setTitle(getString(R.string.date_picker_title));
            datePicker.show();
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        if (WorkExpApi.ACTION_INSERT_WORK_EXP.equals(action) || WorkExpApi.ACTION_UPDATE_WORK_EXP.equals(action)) {
            if (result.hasError || result.statusCode < 0) {
                String message = getString(R.string.add_experience_failed);
                if(mWorkId > 0 ) {
                    message = getString(R.string.modify_experience_failed);
                }
                if (result.message.length() > 0 ) {
                    message = result.message;
                }
                Tips.showTips(message);
                return;
            }

            int tips = R.string.add_experience_succeed;
            if (mWorkId > 0 ) {
                tips = R.string.modify_experience_succed;
            }

            Tips.showTips(tips);
            getActivity().setResult(Activity.RESULT_OK);
            WorkExpApi.fetchWorkExp(true);
            onBackPressed();
        }
    }

    @Override
    protected void onActionRight() {
        try {
            String companyName = mCompanyView.getText();
            String jobName = mJobNameView.getText();
            String startTime = mStartTimeView.getText();
            String endTime = mEndTimeView.getText();
            String memo = mMemoView.getText().toString();

            if (mWorkId > 0) {
                WorkExpApi.updateWorkExp(mWorkId,companyName,jobName,memo,startTime,endTime);
            } else {
                WorkExpApi.insertWorkExp(companyName, jobName, memo, startTime, endTime);
            }
        } catch (EditTableRow.NecessaryException e) {
            Tips.showTips(e.getMessage());
        }


    }
}
