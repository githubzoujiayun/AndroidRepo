package com.android.worksum;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.worksum.apis.JobsApi;
import com.android.worksum.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;

/**
 * chao.qin
 * 2016/1/11
 *
 * 简历
 */
public class MyResumeFragment extends TitlebarFragment {

    private static final String[] TYPE_SEX= {"男","女"};
    private static final String[] TYPE_AGE = new String[80];

    static {
        for (int i=0;i<TYPE_AGE.length;i++) {
            TYPE_AGE[i] = "" + (16 + i);
        }
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle(R.string.my_resume_title);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        setActionRightText(R.string.my_resume_action_save);

        setupTitle(R.id.resume_user_name, R.string.resume_user_name);
        setupTitle(R.id.resume_user_phone, R.string.resume_user_phone);
        setupTitle(R.id.resume_first_name, R.string.resume_first_name);
        setupTitle(R.id.resume_last_name,R.string.resume_last_name);
        setupSpinnerTitle(R.id.resume_job_name, R.string.resume_job_name);
        setupSpinnerTitle(R.id.resume_area, R.string.resume_area);
        setupSpinnerTitle(R.id.resume_sex, R.string.resume_sex);
        setupSpinnerTitle(R.id.resume_age,R.string.resume_age);

        setupTitle(R.id.resume_job_address, R.string.resume_address);
        setupTitle(R.id.resume_user_email, R.string.resume_email);

        setupInputType(R.id.resume_user_phone, InputType.TYPE_CLASS_PHONE);
        setupInputType(R.id.resume_user_email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        updateValues();

        new ResumeUpdateTask().execute();
    }

    private void updateValues() {
        updateEditValue(R.id.resume_first_name, UserCoreInfo.getFirstName());
        updateEditValue(R.id.resume_last_name, UserCoreInfo.getLastName());
        updateEditValue(R.id.resume_user_name, UserCoreInfo.getUserName());
        updateEditValue(R.id.resume_user_phone, UserCoreInfo.getMobilePhone());
//        updateEditValue(R.id.resume_job_name, UserCoreInfo.getFunctionType());
        updateEditValue(R.id.resume_job_address, UserCoreInfo.getCityAddr());
        updateEditValue(R.id.resume_user_email, UserCoreInfo.getEmail());

        setupSpinnerValue(R.id.resume_sex, TYPE_SEX);
        setupSpinnerValue(R.id.resume_age,TYPE_AGE);

        EditText editText = (EditText) findViewById(R.id.resume_memo);
        editText.setText(UserCoreInfo.getMemo());
    }



    private void setupSpinnerValue(int includeId, String[] resources) {
        View view = findViewById(includeId);
        Spinner spinner = (Spinner) view.findViewById(R.id.resume_spinner_item_spinner);
        spinner.setAdapter(new SpinnerArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, resources));
        spinner.setSelection(0);
    }

    private void setSpinnerValue(int includeId,String value) {
        View view = findViewById(includeId);
        Spinner spinner = (Spinner) view.findViewById(R.id.resume_spinner_item_spinner);
        SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) spinner.getAdapter();
        String[] values = adapter.getValues();
        for (int i=0;i<values.length;i++) {
            if (values[i].equals(value)) {
                spinner.setSelection(i);
            }
        }
    }

    private class SpinnerArrayAdapter extends ArrayAdapter<String> {

        private String[] mValues;

        public SpinnerArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            mValues = objects;
        }

        public String[] getValues() {
            return mValues;
        }
    }

    private void updateEditValue(int includeId, String value) {
        View view = findViewById(includeId);
        EditText editText = (EditText)view.findViewById(R.id.resume_content);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ImageView checkView = (ImageView)findViewById(R.id.resume_item_check);
                checkView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(value)) {
                    checkView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editText.setText(value);
    }

    private String getEditValue(int includeId) {
        View view = findViewById(includeId);
        EditText editText = (EditText)view.findViewById(R.id.resume_content);
        return editText.getText().toString();
    }

    private void setupTitle(int includeId,int titleId){
        View view = findViewById(includeId);
        TextView title = (TextView)view.findViewById(R.id.resume_title);
        title.setText(titleId);
    }

    private void setupSpinnerTitle(int includeId,int titleId) {
        View view = findViewById(includeId);
        TextView title = (TextView) view.findViewById(R.id.resume_spinner_item_title);
        title.setText(titleId);
    }

    private void setupInputType(int includeId,int inputType) {
        View view = findViewById(includeId);
        EditText editText = (EditText) view.findViewById(R.id.resume_content);
        editText.setInputType(inputType);
    }

    @Override
    public int getLayoutId() {
        return R.layout.my_resume;
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();

        UserCoreInfo.setUserName(getEditValue(R.id.resume_user_name));
//        UserCoreInfo.setAgeFrom("");
//        UserCoreInfo.setGender("");
        UserCoreInfo.setMobilePhone(getEditValue(R.id.resume_user_phone));
        UserCoreInfo.setFunctionType(getEditValue(R.id.resume_job_name));
        UserCoreInfo.setCity("");
        UserCoreInfo.setCityAddr(getEditValue(R.id.resume_job_address));
        UserCoreInfo.setEmail(getEditValue(R.id.resume_user_email));
        new ResumeUploadTask().execute();
    }


    private class ResumeUpdateTask extends SilentTask{
        /**
         * 执行异步任务
         *
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            return JobsApi.getUserInfo();
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (!result.hasError) {
                UserCoreInfo.setUserLoginInfo(result, true);
                updateValues();
            }
        }
    }

    private class ResumeUploadTask extends SilentTask{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips(getString(R.string.resume_upload));
        }

        /**
         * 执行异步任务
         *
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            return JobsApi.updateResumeInfo();
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (!result.hasError && result.statusCode > 0) {
                Tips.showTips(R.string.resume_upload_succeed);
            } else {
                Tips.showTips(R.string.resume_upload_failed);
            }
            Tips.hiddenWaitingTips();
        }
    }
}
