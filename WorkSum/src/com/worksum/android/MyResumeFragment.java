package com.worksum.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.controller.UserCoreInfo;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author chao.qin
 *         <p/>
 *         16/5/18
 */
public class MyResumeFragment extends TitlebarFragment implements AdapterView.OnItemClickListener{

    DataItemDetail mTempUserInfo;

    @Override
    public int getLayoutId() {
        return R.layout.my_resume;
    }

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setTitle(R.string.my_resume_title);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        setActionRightText(R.string.my_resume_action_save);

        mTempUserInfo = UserCoreInfo.copy();

        setupTitle(R.id.resume_user_name, R.string.resume_user_name);
        setupTitle(R.id.resume_user_phone, R.string.resume_user_phone);
        setupTitle(R.id.resume_first_name, R.string.resume_first_name);
        setupTitle(R.id.resume_last_name, R.string.resume_last_name);

        setupDictTitle(R.id.resume_job_name, R.string.resume_job_name);
        setupDictTitle(R.id.resume_area, R.string.resume_area);
        setupDictTitle(R.id.resume_sex, R.string.resume_sex);
        setupDictTitle(R.id.resume_age, R.string.resume_age);

        setupTitle(R.id.resume_job_address, R.string.resume_address);
        setupTitle(R.id.resume_user_email, R.string.resume_email);

        setupInputType(R.id.resume_user_phone, InputType.TYPE_CLASS_PHONE);
        setupInputType(R.id.resume_user_email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

//        updateValues();

        View phonelayout = findViewById(R.id.resume_user_phone);
        View phone = phonelayout.findViewById(R.id.resume_content);
        phone.setEnabled(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        new ResumeInfoTask().executeOnPool();
    }

    private class ResumeInfoTask extends SilentTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips(getString(R.string.resume_loading_jobinfo));
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
            Tips.hiddenWaitingTips();
            if (!result.hasError) {
                UserCoreInfo.setUserLoginInfo(result, true,UserCoreInfo.USER_LOGIN_OTHERS);
                updateValues();
            } else {
                Tips.showTips(R.string.login_get_resume_info_failed);
            }
        }
    }

    private void updateValues() {
        updateEditValue(R.id.resume_first_name, UserCoreInfo.getFirstName());
        updateEditValue(R.id.resume_last_name, UserCoreInfo.getLastName());
        updateEditValue(R.id.resume_user_name, UserCoreInfo.getUserName());
        updateEditValue(R.id.resume_user_phone, UserCoreInfo.getMobilePhone());
        updateEditValue(R.id.resume_job_address, UserCoreInfo.getCity());

        updateDictValue(R.id.resume_sex, UserCoreInfo.getGender(), DictFragment.POSITION_SEX);
        updateDictValue(R.id.resume_age, UserCoreInfo.getAgeFrom(), DictFragment.POSITION_AGE);
        updateDictValue(R.id.resume_job_name, UserCoreInfo.getFunctionName(), DictFragment.POSITION_FUNCTION);
        updateDictValue(R.id.resume_area, UserCoreInfo.getAreaName(), DictFragment.POSITION_AREA);

        EditText editText = (EditText) findViewById(R.id.resume_memo);
        editText.setText(UserCoreInfo.getMemo());
    }

    private void updateDictValue(final int includeId, String value,final int position) {
        final View view = findViewById(includeId);
        TextView textView = (TextView) view.findViewById(R.id.resume_dict_item_content);
        textView.setText(value);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(null, view, position, includeId);
            }
        });
    }

    private void updateEditValue(int includeId, String value) {
        final View view = findViewById(includeId);
        EditText editText = (EditText) view.findViewById(R.id.resume_content);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ImageView checkView = (ImageView) view.findViewById(R.id.resume_item_check);
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

    private void setupDictTitle(int includeId, int titleId) {
        View view = findViewById(includeId);
        TextView title = (TextView) view.findViewById(R.id.resume_dict_item_title);
        title.setText(titleId);
    }

    private void setupTitle(int includeId, int titleId) {
        View view = findViewById(includeId);
        TextView title = (TextView) view.findViewById(R.id.resume_title);
        title.setText(titleId);
    }

    private void setupInputType(int includeId, int inputType) {
        View view = findViewById(includeId);
        EditText editText = (EditText) view.findViewById(R.id.resume_content);
        editText.setInputType(inputType);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentContainer.showDict(this, position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        String dict = data.getStringExtra("dict");
        String code = data.getStringExtra("code");
        switch (requestCode) {
            case DictFragment.POSITION_AREA:
                updateDictValue(R.id.resume_area, dict, DictFragment.POSITION_AREA);
                mTempUserInfo.setStringValue("City",code);
                break;
            case DictFragment.POSITION_FUNCTION:
                updateDictValue(R.id.resume_job_name, dict,DictFragment.POSITION_FUNCTION);
                mTempUserInfo.setStringValue("FunctionType",code);
                break;
            case DictFragment.POSITION_SEX:
                updateDictValue(R.id.resume_sex, dict,DictFragment.POSITION_SEX);
                mTempUserInfo.setStringValue("Gender",dict);
                break;
            case DictFragment.POSITION_AGE:
                updateDictValue(R.id.resume_age, dict,DictFragment.POSITION_AGE);
                mTempUserInfo.setStringValue("AgeFrom",dict);
                break;
        }
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();

        EditText memoEdit = (EditText) findViewById(R.id.resume_memo);

        mTempUserInfo.setStringValue("FirstName", getEditValue(R.id.resume_first_name));
        mTempUserInfo.setStringValue("LastName", getEditValue(R.id.resume_last_name));
        mTempUserInfo.setStringValue("Cname", getEditValue(R.id.resume_user_name));
//        mTempUserInfo.setStringValue("City", getEditValue(R.id.resume_job_address));
        mTempUserInfo.setStringValue("Memo", memoEdit.getText().toString());

        new SilentTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Tips.showWaitingTips(getString(R.string.resume_upload));
            }

            @Override
            protected DataItemResult doInBackground(String... params) {
                return JobsApi.updateResumeInfo(mTempUserInfo);
            }

            @Override
            protected void onTaskFinished(DataItemResult result) {
                Tips.hiddenWaitingTips();
                if (!result.hasError && result.statusCode > 0) {
                    Tips.showTips(R.string.resume_upload_succeed);
                    getActivity().setResult(Activity.RESULT_OK, new Intent());
                    getActivity().finish();
                } else {
                    Tips.showTips(R.string.resume_upload_failed);
                }

            }
        }.executeOnPool();



//        Observable observable = Observable.create(new Observable.OnSubscribe<DataItemResult>() {
//
//            @Override
//            public void call(Subscriber<? super DataItemResult> subscriber) {
//                subscriber.onNext(JobsApi.updateResumeInfo(mTempUserInfo));
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.io())
//        .doOnSubscribe(new Action0() {
//            @Override
//            public void call() {
//                Tips.showWaitingTips(getString(R.string.resume_upload));
//            }
//        }).subscribeOn(AndroidSchedulers.mainThread());
//        observable.observeOn(AndroidSchedulers.mainThread());
//
//        observable.subscribe(new Subscriber<DataItemResult>() {
//
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(DataItemResult result) {
//                if (!result.hasError && result.statusCode > 0) {
//                    Tips.showTips(R.string.resume_upload_succeed);
//                } else {
//                    Tips.showTips(R.string.resume_upload_failed);
//                }
//                Tips.hiddenWaitingTips();
//
//                getActivity().setResult(Activity.RESULT_OK, new Intent());
//                getActivity().finish();
//            }
//        });

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
}
