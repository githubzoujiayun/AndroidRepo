package com.worksum.android;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.worksum.android.apis.ResumeApi;
import com.worksum.android.controller.DataController;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.utils.Utils;

/**
 * @author chao.qin
 *         <p>
 *         16/4/13
 */
public class ForgetFragment extends GeneralFragment implements View.OnClickListener {

    private EditText mForgetContent;
    private Button mCancelBtn;
    private Button mSendBtn;

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mForgetContent = (EditText) findViewById(R.id.forget_content);
        mCancelBtn = (Button) findViewById(R.id.forget_cancel);
        mSendBtn = (Button) findViewById(R.id.forget_send);

        mForgetContent.setText(getExtra("phoneNumber",""));

        mSendBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.forget_password;
    }

    @Override
    public void onClick(View v) {
        if (v == mCancelBtn) {
            onBackPressed();
        } else if (v == mSendBtn) {
            String phoneNumber = mForgetContent.getText().toString();
            if (!Utils.matchesPhone(phoneNumber)) {
                Tips.showTips(R.string.invalide_phone_number);
                return;
            }
            DataController.DataAdapter dataAdapter = DataController.getInstance().newDataAdapter();
            dataAdapter.setDataListener(new DataController.DataLoadListener() {
                @Override
                public void onSucceed(DataItemResult result) {
                    if (result.statusCode == 1) {
                        Tips.showTips(R.string.send_succed);
                    } else {
                        Tips.showTips(R.string.send_failed);
                    }
                    onBackPressed();
                }

                @Override
                public void onFailed(DataItemResult result, boolean isNetworkConnected) {
                    onBackPressed();
                }

                @Override
                public void onEmpty(DataItemResult result) {

                }

                @Override
                public void onBeforeLoad() {

                }

                @Override
                public DataItemResult onLoadData() {
                    return ResumeApi.forgetPsw(mForgetContent.getText().toString());
                }

                @Override
                public void onCancelled() {
                    onBackPressed();
                }
            });
            dataAdapter.execute();

        }
    }
}
