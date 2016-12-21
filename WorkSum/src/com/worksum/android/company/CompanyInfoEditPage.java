package com.worksum.android.company;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.imageloader.core.DisplayImageOptions;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.FragmentContainer;
import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.CustomerApi;
import com.worksum.android.controller.UserCoreInfo;
import com.worksum.android.utils.Utils;
import com.worksum.android.views.EditTableRow;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 *
 */

@LayoutID(R.layout.company_info_edit_page)
@Titlebar(titleId = R.string.company_info_account_title, leftDrawableId = R.drawable.common_nav_arrow, rightTextId = R.string.action_save)
@DataManagerReg(actions = {CustomerApi.ACTION_GET_CTM_IMAGE,CustomerApi.ACTION_SAVE_CTM_IMAGE,CustomerApi.ACTION_UPDATE_COMPANY_INFO,CustomerApi.ACTION_GET_CTM_INFO})
public class CompanyInfoEditPage extends TitlebarFragment {

    private static final int REQUEST_CODE_EDIT_ACCOUNT = 1;

    private ImageView mCompanyHeaderView;
    private EditTableRow mCompanyNameRow;
    private EditTableRow mCompanyContactRow;
    private EditTableRow mCompanyPhoneRow;
    private EditTableRow mCompanyEmailRow;
    private EditTableRow mCompanyBusinessNumberRow;
    private EditTableRow mCompanyAddressRow;
    private EditText mCompanyIntroductionView;
    private Uri mHeaderUri;

    private Uri mTempHeaderUri;

    public static void show(Fragment fragment,int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), FragmentContainer.class);
        intent.putExtra(KEY_FRAGMENT, CompanyInfoEditPage.class);
        intent.putExtra(TitlebarFragment.KEY_SCROLLBAR_ENABLED, true);
        fragment.startActivityForResult(intent,requestCode);
    }


    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mCompanyHeaderView = (ImageView) findViewById(R.id.company_info_header);
        mCompanyNameRow = (EditTableRow) findViewById(R.id.company_info_name);
        mCompanyContactRow = (EditTableRow) findViewById(R.id.company_info_contact);
        mCompanyPhoneRow = (EditTableRow) findViewById(R.id.company_info_phone);
        mCompanyEmailRow = (EditTableRow) findViewById(R.id.company_info_email);
        mCompanyBusinessNumberRow = (EditTableRow) findViewById(R.id.company_info_business_number);
        mCompanyAddressRow = (EditTableRow) findViewById(R.id.company_info_address);
        mCompanyIntroductionView = (EditText) findViewById(R.id.company_info_introduction);

        mCompanyHeaderView.setOnClickListener(this);



        if (UserCoreInfo.hasLogined()) {
            requestHeadPhoto();
        }

        CustomerApi.getCtmInfo();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mCompanyHeaderView) {
            pickPhoto();
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Utils.REQUEST_CODE_PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Tips.showTips(R.string.tips_canceled);
            return;
        }

        if (requestCode == Utils.REQUEST_CODE_PICK_PHOTO) {
            if (data.getData() == null) {
                return;
            }
            mHeaderUri = data.getData();
            if (mHeaderUri == null) {
                return;
            }
            mTempHeaderUri = Utils.startImageZoom(this, mHeaderUri);

        } else if (requestCode == Utils.REQUEST_CODE_CROP_IMAGE) {
            String filePath = null;
            if (data.getData() != null) {
                if (data.getScheme().equals("file")) {
                    filePath = data.getData().getPath();
                } else if (data.getScheme().equals("content")) {
                    Cursor c = getActivity().getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (c != null && c.moveToNext()) {
                        filePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                        c.close();
                    }
                }
            } else {
                Cursor c = getActivity().getContentResolver().query(mTempHeaderUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c != null && c.moveToNext()) {
                    filePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    c.close();
                }
            }
            if (filePath == null && mTempHeaderUri != null) {
                filePath = mTempHeaderUri.getPath();
            }
            if (filePath == null) {
                return;
            }
            File file = new File(filePath);
            if (file.length() <= 0) {
                return;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 3;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            mCompanyHeaderView.setImageBitmap(bitmap);

            uploadPhoto(bitmap);


            if (file.exists()) {
                if (!file.delete()) {
                    AppUtil.print("delete failed!");
                }

            }

        }
    }

    private void requestHeadPhoto() {
        CustomerApi.getCtmImg();
    }

    private void uploadPhoto(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapData = stream.toByteArray();
        final String base64Data = Base64.encode(bitmapData, 0, bitmapData.length);

        CustomerApi.saveCtmImg(base64Data);
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();

        try {
            CustomerApi.updateResumeInfo(
                    mCompanyNameRow.getText()
                    ,mCompanyPhoneRow.getText()
                    ,mCompanyContactRow.getText()
                    ,mCompanyAddressRow.getText()
                    ,mCompanyBusinessNumberRow.getText()
                    ,mCompanyIntroductionView.getText().toString());
        } catch (EditTableRow.NecessaryException e) {
            Tips.showTips(e.getMessage());
        }
    }

    @Override
    public void onStartRequest(String action) {
        super.onStartRequest(action);
        Tips.showWaitingTips(getActivity(), R.string.tips_requesting);
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        Tips.hiddenWaitingTips();
        switch (action) {
            case CustomerApi.ACTION_SAVE_CTM_IMAGE:
                if (result.statusCode == 1) {
                    getActivity().setResult(RESULT_OK, new Intent());
                    Tips.showTips(R.string.self_photo_upload_succeed);
                } else {
                    Tips.showTips(R.string.self_photo_upload_failed);
                }
                break;
            case CustomerApi.ACTION_GET_CTM_IMAGE:
                if (result.statusCode == 1) {
                    ImageLoader imageLoader = ImageLoader.getInstance();

                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.img_head_add).build();
                    imageLoader.displayImage(result.message,mCompanyHeaderView,options);
                }
                break;
            case CustomerApi.ACTION_GET_CTM_INFO:
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

                    mCompanyNameRow.setText(companyName);
                    mCompanyContactRow.setText(contact);
                    mCompanyPhoneRow.setText(phone);
                    mCompanyEmailRow.setText(email);
                    mCompanyBusinessNumberRow.setText(businessNumber);
                    mCompanyIntroductionView.setText(customerInfo);
                    mCompanyAddressRow.setText(address);

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.img_head_add).build();
                    imageLoader.displayImage(customerImg,mCompanyHeaderView,options);

                } else {
                    Tips.showTips(R.string.tips_get_data_failed);
                }
                break;
            case CustomerApi.ACTION_UPDATE_COMPANY_INFO:
                if(result.statusCode == 1) {
                    getActivity().setResult(Activity.RESULT_OK);
                    Tips.showTips(R.string.tips_upload_succeed);
                    getActivity().finish();
                } else if (result.statusCode == -10) {
                    Tips.showTips(R.string.tips_argument_error);
                } else {
                    Tips.showTips(R.string.tips_upload_failed);
                }
                break;
        }
    }

}
