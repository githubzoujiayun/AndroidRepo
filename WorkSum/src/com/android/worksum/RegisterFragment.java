package com.android.worksum;

import android.os.Bundle;
import android.view.ViewGroup;

import com.android.worksum.apis.JobsApi;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;

/**
 * 注册界面
 * chao.qin
 * 2016/2/22
 */
public class RegisterFragment extends LoginFragment {

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setActionRightText(R.string.register_right_action);
    }

    @Override
    protected void onActionRight() {
        String phoneNumber = mLoginView.getText().toString();
        String password = mPwdView.getText().toString();

        new RegisterTask().execute(phoneNumber, password);
    }

    private class RegisterTask extends SilentTask {
        private String phoneNumber;
        private String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips();
        }

        /**
         * 执行异步任务
         *
         * @param params
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            phoneNumber = params[0];
            password = params[1];
            return JobsApi.register(phoneNumber, password);
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         * @param result
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            Tips.hiddenWaitingTips();
            if (result.statusCode == -1) {
                Tips.showTips(R.string.register_phone_has_exist);
                return;
            }
            if(!result.hasError && result.statusCode > 0) {
                Tips.showTips(R.string.register_succeed);
                FragmentContainer.showMyResume(getActivity());
                new LoginTask(false).execute(phoneNumber,password);
            }
        }
    }
}
