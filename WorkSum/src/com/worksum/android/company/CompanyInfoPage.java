package com.worksum.android.company;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.CustomerApi;
import com.worksum.android.controller.LoginManager;
import com.worksum.android.login.LoginSelectorFragment;

/**
 */

@LayoutID(R.layout.company_info_page)
@Titlebar(titleId = R.string.company_info_account_title,rightTextId = R.string.right_action_logout)
@DataManagerReg(actions = CustomerApi.ACTION_GET_CTM_INFO)
public class CompanyInfoPage extends TitlebarFragment {


    private static final int REQUEST_CODE_EDIT_ACCOUNT = 1;

    private TextView mCompanyNameView;
    private TextView mCompanyEmailView;
    private TextView mCompanyContactView;
    private TextView mCompanyPhoneView;

    private TextView mCompanyMemoView;

    private TextView mBusinessNumberView;
    private TextView mCompanyAddressView;


    private TextView mEditButton;

    private ImageView mCompanyPhotoView;



    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mCompanyNameView = (TextView) findViewById(R.id.company_name);
        mCompanyEmailView = (TextView) findViewById(R.id.company_email);
        mCompanyContactView = (TextView) findViewById(R.id.company_contact);
        mCompanyPhoneView = (TextView) findViewById(R.id.company_phone);

        mCompanyMemoView = (TextView) findViewById(R.id.company_memo);

        mBusinessNumberView = (TextView) findViewById(R.id.company_business_number);
        mCompanyAddressView = (TextView) findViewById(R.id.company_address);

        mCompanyPhotoView = (ImageView) findViewById(R.id.me_head);


        mEditButton = (TextView) findViewById(R.id.company_profile_edit);
        mEditButton.setOnClickListener(this);

        CustomerApi.getCtmInfo();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mEditButton) {
            CompanyInfoEditPage.show(this,REQUEST_CODE_EDIT_ACCOUNT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_EDIT_ACCOUNT) {
            CustomerApi.getCtmInfo();
        }
    }

    @Override
    public void onStartRequest(String action) {
        super.onStartRequest(action);
        Tips.showWaitingTips(getActivity(),R.string.waiting_loading);
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        Tips.hiddenWaitingTips();

        if (result.statusCode == 1 && result.getDataCount() > 0) {
            DataItemDetail detail = result.getItem(0);
            String companyName = detail.getString("Name");
            String email = detail.getString("Email");
            String phone = detail.getString("Mobile");
            String alias = detail.getString("Alias");
            String contact = detail.getString("Contact");
            String ctmId = detail.getString("CtmID");
            String tel = detail.getString("Tel");
            String longitude = detail.getString("Longitude");
            String dimension = detail.getString("Dimension");
            String address = detail.getString("Address");
            String customerInfo = detail.getString("CustomerInfo");
            String customerImg = detail.getString("CustomerImg");
            String status = detail.getString("Status");

            String businessNumber = detail.getString("CtmNO");

            setVisibleText(mCompanyNameView,getString(R.string.company_format_name,companyName));
            setVisibleText(mCompanyEmailView,getString(R.string.company_email_format,email));
            setVisibleText(mCompanyPhoneView,getString(R.string.company_phone_format,phone));
            setVisibleText(mCompanyContactView,getString(R.string.company_contact_format,contact));

            mCompanyMemoView.setText(customerInfo);

            mCompanyAddressView.setText(getString(R.string.company_address_format,address));
            mBusinessNumberView.setText(getString(R.string.company_business_format,businessNumber));

            ImageLoader.getInstance().displayImage(customerImg,mCompanyPhotoView);
        }
    }

    private void setVisibleText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        Tips.showConfirm(getString(R.string.self_logout_alter_title),getString(R.string.self_logout_alter), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    LoginManager.logout();
                    LoginSelectorFragment.showLoginSelector(getActivity());
                    getActivity().finish();
                }
            }
        });
    }
}
