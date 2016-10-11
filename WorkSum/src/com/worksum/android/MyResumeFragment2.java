package com.worksum.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.worksum.android.apis.DictsApi;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.controller.ApiLoaderTask;
import com.worksum.android.controller.Task;
import com.worksum.android.controller.TaskManager;
import com.worksum.android.controller.UserCoreInfo;
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
public class MyResumeFragment2 extends TitlebarFragment {

    private static final String TYPE_FIRST_ITEM = "请选择";

    private static final String[] TYPE_SEX= {TYPE_FIRST_ITEM,"男","女"};
    private static final String[] TYPE_AGE = new String[]{TYPE_FIRST_ITEM,"18-25","25-40","40-65","65+"};

    private DataItemResult mAreaDict;
    private DataItemResult mFuctionTypeDict;

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle(R.string.my_resume_title);
        setActionLeftDrawable(R.drawable.jobsearch_close);

        setupTitle(R.id.resume_user_name, R.string.resume_user_name);
        setupTitle(R.id.resume_user_phone, R.string.resume_user_phone);
        setupTitle(R.id.resume_first_name, R.string.resume_first_name);
        setupTitle(R.id.resume_last_name, R.string.resume_last_name);
        setupSpinnerTitle(R.id.resume_job_name, R.string.resume_job_name);
        setupSpinnerTitle(R.id.resume_area, R.string.resume_area);
        setupSpinnerTitle(R.id.resume_sex, R.string.resume_sex);
        setupSpinnerTitle(R.id.resume_age, R.string.resume_age);

        setupTitle(R.id.resume_job_address, R.string.resume_address);
        setupTitle(R.id.resume_user_email, R.string.resume_email);

        setupInputType(R.id.resume_user_phone, InputType.TYPE_CLASS_PHONE);
        setupInputType(R.id.resume_user_email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        updateValues(false);

        View phonelayout = findViewById(R.id.resume_user_phone);
        View phone = phonelayout.findViewById(R.id.resume_content);
        phone.setEnabled(false);

        new AreaTask(getTaskManager()).executeOnPool();
    }

    private void updateValues(boolean hasLoaded) {
        updateEditValue(R.id.resume_first_name, UserCoreInfo.getFirstName());
        updateEditValue(R.id.resume_last_name, UserCoreInfo.getLastName());
        updateEditValue(R.id.resume_user_name, UserCoreInfo.getUserName());
        updateEditValue(R.id.resume_user_phone, UserCoreInfo.getMobilePhone());
        updateEditValue(R.id.resume_job_address, findCname(mAreaDict, UserCoreInfo.getCity()));

        setupSpinnerValue(R.id.resume_sex, TYPE_SEX);
        setupSpinnerValue(R.id.resume_age, TYPE_AGE);

        setSpinnerValue(R.id.resume_sex, UserCoreInfo.getGender());
        setSpinnerValue(R.id.resume_age, UserCoreInfo.getAgeFrom());

        if (hasLoaded) {
            setSpinnerValue(R.id.resume_job_name, findCname(mFuctionTypeDict,UserCoreInfo.getFunctionType()));
            setSpinnerValue(R.id.resume_area, findCname(mAreaDict,UserCoreInfo.getCity()));
        } else {
            setupSpinnerValue(R.id.resume_job_name, new String[]{UserCoreInfo.getFunctionName()});
            setupSpinnerValue(R.id.resume_area,new String[]{UserCoreInfo.getAreaName()});
        }
        EditText editText = (EditText) findViewById(R.id.resume_memo);
        editText.setText(UserCoreInfo.getMemo());
    }

    private void setupSpinnerValue(int includeId, DataItemResult result) {
        DataItemDetail detail = new DataItemDetail();
        detail.setStringValue("Cname", getStringSafely(R.string.spinner_item_choose));
        result.addItem(0, detail);
        final int count = result.getDataCount();
        String[] values = new String[count];
        for (int i=0; i<count; i++) {
            detail = result.getItem(i);
            values[i] = detail.getString("Cname");
        }
        setupSpinnerValue(includeId, values);
    }

    private void setupSpinnerValue(int includeId, String[] resources) {
        View view = findViewById(includeId);
        Spinner spinner = (Spinner) view.findViewById(R.id.resume_spinner_item_spinner);
        String value = null;
        if (spinner.getAdapter() != null) {
            value = getSpinnerValue(includeId);
        }
        spinner.setAdapter(new SpinnerArrayAdapter(getActivity(), R.layout.resume_simple_item, resources));
        if(value != null) {
            setSpinnerValue(includeId, value);
        }
    }

    private void setSpinnerValue(int includeId,String value) {
        View view = findViewById(includeId);
        Spinner spinner = (Spinner) view.findViewById(R.id.resume_spinner_item_spinner);
        SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) spinner.getAdapter();
        String[] values = adapter.getValues();
        int i;
        for (i=0;i<values.length;i++) {
            if (values[i].equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
        if (i == values.length) {
            adapter.setValues(new String[]{value});
        }
    }


    private String getSpinnerValue(int includeId) {
        View view = findViewById(includeId);
        Spinner spinner = (Spinner) view.findViewById(R.id.resume_spinner_item_spinner);
        if (spinner.getAdapter().getCount() < 1) {
            return "";
        }
        if (spinner.getSelectedItem() == null) {
            Object detail = spinner.getAdapter().getItem(0);
            if (detail != null) {
                return detail.toString();
            }
            return "";
        }
        return spinner.getSelectedItem().toString();
    }

    private class SpinnerArrayAdapter extends ArrayAdapter<String> {

        private String[] mValues;

        public SpinnerArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            mValues = objects;
        }

        public void setValues(String values[]) {
            mValues = values;
        }

        public String[] getValues() {
            return mValues;
        }
    }

    private void updateEditValue(int includeId, String value) {
        final View view = findViewById(includeId);
        EditText editText = (EditText)view.findViewById(R.id.resume_content);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ImageView checkView = (ImageView)view.findViewById(R.id.resume_item_check);
                checkView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(charSequence)) {
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
        Editable text =  editText.getText();
        if (text != null) {
            return text.toString();
        }
        return "";
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
        return R.layout.my_resume3;
    }

    private String findCode(DataItemResult dictMap,String name) {
        if (dictMap == null) {
            return "";
        }
        for (int i=0;i<dictMap.getDataCount();i++) {
            if (dictMap.getItem(i).getString("Cname").equals(name)) {
                return dictMap.getItem(i).getString("CODE");
            }
        }
        return "";
    }

    private String findCname(DataItemResult dictMap,String code) {
        if (dictMap == null) {
            return code;//默認返回code，因為舊數據數據code就是name
        }
        for (int i=0;i<dictMap.getDataCount();i++) {
            if (dictMap.getItem(i).getString("CODE").equals(code)) {
                return dictMap.getItem(i).getString("Cname");
            }
        }
        return code;
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        UserCoreInfo.setFirstName(getEditValue(R.id.resume_first_name));
        UserCoreInfo.setLastName(getEditValue(R.id.resume_last_name));
        UserCoreInfo.setUserName(getEditValue(R.id.resume_user_name));
        UserCoreInfo.setCity(findCode(mAreaDict,getSpinnerValue(R.id.resume_area)));
        UserCoreInfo.setFunctionType(findCode(mFuctionTypeDict,getSpinnerValue(R.id.resume_job_name)));
        UserCoreInfo.setMobilePhone(getEditValue(R.id.resume_user_phone));
        UserCoreInfo.setGender(getSpinnerValue(R.id.resume_sex));
        UserCoreInfo.setAgeFrom(getSpinnerValue(R.id.resume_age));
        EditText editText = (EditText) findViewById(R.id.resume_memo);
        UserCoreInfo.setMemo(editText.getText().toString());
        new ResumeUploadTask(getTaskManager()).execute();
    }


    private class ResumeUpdateTask extends ApiLoaderTask{
        public ResumeUpdateTask(Context context) {
            super(context,getTaskManager());
        }

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
                updateValues(true);
                setActionRightText(R.string.my_resume_action_save);
            }
            Tips.hiddenWaitingTips();
        }
    }

    private class ResumeUploadTask extends Task{


        public ResumeUploadTask(TaskManager taskManager) {
            super(taskManager);
        }

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
//            return JobsApi.updateResumeInfo();
            return  null;
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
            getActivity().setResult(Activity.RESULT_OK, new Intent());
            getActivity().finish();
        }
    }

    private class AreaTask extends Task{

        public AreaTask(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips();
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            return DictsApi.getArea();
        }

        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (result.hasError) {
                Tips.showTips(R.string.tips_load_failed);
                return;
            }
            mAreaDict = result;
            setupSpinnerValue(R.id.resume_area,result);
            new FunctionTypeTask().executeOnPool();
        }
    }

    private class FunctionTypeTask extends SilentTask {

        @Override
        protected DataItemResult doInBackground(String... params) {
            return DictsApi.getFunctionType();
        }

        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (result.hasError) {
                Tips.showTips(R.string.tips_load_failed);
                return;
            }
            mFuctionTypeDict = result;
            setupSpinnerValue(R.id.resume_job_name,result);
            new ResumeUpdateTask(getActivity()).executeWithCheck();
        }
    }
}
