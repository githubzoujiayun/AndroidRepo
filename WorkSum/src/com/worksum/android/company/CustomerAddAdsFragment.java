package com.worksum.android.company;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.imageloader.core.DisplayImageOptions;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.DictFragment;
import com.worksum.android.FragmentContainer;
import com.worksum.android.GeneralFragment;
import com.worksum.android.Main;
import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.CustomerJobsApi;
import com.worksum.android.utils.Utils;
import com.worksum.android.views.DictTableRow;
import com.worksum.android.views.EditTableRow;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 */

@LayoutID(R.layout.customer_ads)
@DataManagerReg(actions = {
        CustomerJobsApi.ACTION_INSERT_JOBS,
        CustomerJobsApi.ACTION_SAVE_JOB_IMAGE,
        CustomerJobsApi.ACTION_UPDATE_JOB,
        CustomerJobsApi.ACTION_STOP_JOB
})
@Titlebar(titleId = R.string.customer_ads,rightTextId = R.string.action_save)
public class CustomerAddAdsFragment extends TitlebarFragment {

    private static final String KEY_EXTRAS = "extras";
    private static final String KEY_IN_MAIN_MENU = "inMainMenu";

    private ImageView mPhotoView;

    private EditTableRow mJobNameRow;
    private DictTableRow mAreaRow;
    private DictTableRow mFunctionTypeRow;
    private DictTableRow mSalaryTypeRow;
    private EditTableRow mSalaryRow;
    private DictTableRow mJobTypeRow;
    private DictTableRow mStartDateRow;
    private DictTableRow mEndDateRow;
    private DictTableRow mStartTimeRow;
    private DictTableRow mEndTimeRow;

    private Button mCancelPublish;
    private EditText mAdsMemoView;
    private Uri mHeaderUri;
    private String mBase64Data = "";

    private int mAdsId = -1;

    private boolean mInMainMenu = true;

    private Uri mTempPhotoUri;

    public static void show(GeneralFragment fragment){
        show(fragment,null);
    }


    public static void show(GeneralFragment fragment, DataItemDetail detail) {
        Intent intent = new Intent(fragment.getActivity(), FragmentContainer.class);
        intent.putExtra(FragmentContainer.KEY_FRAGMENT,CustomerAddAdsFragment.class);
        intent.putExtra(KEY_SCROLLBAR_ENABLED,true);
        intent.putExtra(KEY_IN_MAIN_MENU,false);
        if (detail != null) {
            intent.putExtra(KEY_EXTRAS,detail);
        }
        fragment.startActivityForResult(intent,CustomerJobs.REQUEST_CODE_ADD_ADS);
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        doSave();
    }

    private void doSave() {
        try {
            String jobName = mJobNameRow.getText().trim();
            String jobMemo = mAdsMemoView.getText().toString().trim();
            String functionType = mFunctionTypeRow.getTag().toString();
            String workType = mJobTypeRow.getText();
            String area = mAreaRow.getTag().toString();
            String salaryType = mSalaryTypeRow.getText();
            String salary = mSalaryRow.getText();
            String startDate = mStartDateRow.getText();
            String endDate = mEndDateRow.getText();
            String startTime = mStartTimeRow.getText();
            String endTime = mEndTimeRow.getText();

            setupFullJob(mJobTypeRow.getText());

            if (isModify()) {
                CustomerJobsApi.updateJobs(mAdsId,jobName,jobMemo,functionType,workType,area,salaryType,salary,startDate,endDate,startTime,endTime,"","","");
            } else {
                CustomerJobsApi.insertJobs(jobName, jobMemo, functionType, workType, area, salaryType, salary, startDate, endDate, startTime, endTime, "", "", "");
            }

        } catch (EditTableRow.NecessaryException e) {
            Tips.showTips(e.getMessage());
        }
    }

    @Override
    public void onStartRequest(String action) {
        super.onStartRequest(action);
        if (CustomerJobsApi.ACTION_INSERT_JOBS.equals(action) || CustomerJobsApi.ACTION_SAVE_JOB_IMAGE.equals(action)) {
            Tips.showWaitingTips(getActivity(), R.string.tips_upload_infos);
        } else if (CustomerJobsApi.ACTION_UPDATE_JOB.equals(action)) {
            Tips.showWaitingTips(getActivity(),R.string.tips_updating);
        } else if (CustomerJobsApi.ACTION_STOP_JOB.equals(action)) {
            Tips.showWaitingTips(getActivity(),R.string.tips_stop_ads);
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        int tips = R.string.tips_publish_failed;
        if (CustomerJobsApi.ACTION_INSERT_JOBS.equals(action) || CustomerJobsApi.ACTION_UPDATE_JOB.equals(action)) {
            //跟新职位信息 -12開始時間格式不準確！-13結束時間格式不準確！-14職位名必填！
            // -15行業必填！-16地區必填！-17薪資類別必填！ -18薪資必須為數值！
            // -19工作類型必填 -20請上傳圖片！-21職位描述必填！
            Tips.hiddenWaitingTips();
            switch (result.statusCode) {
                case -12:
                    tips = R.string.tips_start_time_invalide_format;
                    break;
                case -13:
                    tips = R.string.tips_end_time_invalide_format;
                    break;
                case -20:
                    tips = R.string.tips_ads_photo_necessnary;
                    break;
                case -21:
                    tips = R.string.tips_ads_memo_necessnary;
                    break;
            }
            if (result.statusCode > 0) {
                if (!TextUtils.isEmpty(mBase64Data)) {
                    int jobId = result.statusCode;
                    if (isModify()) {
                        jobId = mAdsId;
                    }
                    CustomerJobsApi.saveJobImg(jobId, mBase64Data);
                } else {
                    if (!mInMainMenu) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
//                    Tips.showTips(R.string.tips_publish_succeed);
                }
            } else {
                Tips.hiddenWaitingTips();
                Tips.showTips(tips);
            }
        } else if (CustomerJobsApi.ACTION_SAVE_JOB_IMAGE.equals(action)) {
            Tips.hiddenWaitingTips();
            if (result.statusCode > 0 ) {
                mBase64Data = "";
                if (!mInMainMenu) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    Main main = (Main) getActivity();
                    main.setTab(0);
                }
                tips = R.string.tips_publish_succeed;
            } else {
                tips = R.string.tips_img_upload_failed;
            }
            Tips.showTips(tips);
        } else if (CustomerJobsApi.ACTION_STOP_JOB.equals(action)) {
            Tips.hiddenWaitingTips();
            tips = R.string.tips_stop_ads_failed;
            if (result.statusCode == 1) {
                tips = R.string.tips_stop_ads_succed;
            }
            Tips.showTips(tips);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scrollbarEnable();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);


        mPhotoView = findViewById(R.id.add_ads_photo);

        mJobNameRow = findViewById(R.id.add_ads_job_name);
        mAreaRow = findViewById(R.id.add_ads_address);
        mFunctionTypeRow = findViewById(R.id.add_ads_function_type);
        mSalaryTypeRow = findViewById(R.id.add_ads_salary_type);
        mSalaryRow = findViewById(R.id.add_ads_salary);
        mJobTypeRow = findViewById(R.id.add_ads_work_type);
        mStartDateRow = findViewById(R.id.add_ads_start_date);
        mEndDateRow = findViewById(R.id.add_ads_end_date);
        mStartTimeRow = findViewById(R.id.add_ads_start_time);
        mEndTimeRow = findViewById(R.id.add_ads_end_time);

        mCancelPublish = findViewById(R.id.add_ads_action_cancel);
        mAdsMemoView = findViewById(R.id.add_ads_memo);

        mAreaRow.setOnClickListener(this);
        mFunctionTypeRow.setOnClickListener(this);
        mSalaryTypeRow.setOnClickListener(this);
        mJobTypeRow.setOnClickListener(this);
        mStartDateRow.setOnClickListener(this);
        mEndDateRow.setOnClickListener(this);
        mStartTimeRow.setOnClickListener(this);
        mEndTimeRow.setOnClickListener(this);

        mCancelPublish.setOnClickListener(this);
        mPhotoView.setOnClickListener(this);

        DataItemDetail detail = getActivity().getIntent().getParcelableExtra(KEY_EXTRAS);
        if (detail != null) {
            mJobNameRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_JOB_NAME));
            mAreaRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_JOB_ADDRESS_NAME));
            mAreaRow.setTag(detail.getString(CustomerJobs.DETAIL_KEY_JOB_ADDRESS));
            mFunctionTypeRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_FUNCTION_TYPE_NAME));
            mFunctionTypeRow.setTag(detail.getString(CustomerJobs.DETAIL_KEY_FUNCTION_TYPE));
            mSalaryTypeRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_SALARY_TYPE));
            mSalaryRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_SALARY));
            mJobTypeRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_WORK_TYPE));
            mStartDateRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_DATE_START));
            mEndDateRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_DATE_END));
            mStartTimeRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_TIME_START));
            mEndTimeRow.setText(detail.getString(CustomerJobs.DETAIL_KEY_TIME_END));
            mAdsMemoView.setText(detail.getString(CustomerJobs.DETAIL_KEY_MEMO));
            mAdsId = detail.getInt(CustomerJobs.DETAIL_KEY_JOB_ID);

            String imgUrl = detail.getString(CustomerJobs.DETAIL_KEY_JOB_IMG);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.bg_add_ads_default)
                    .showImageOnFail(R.drawable.bg_add_ads_default)
                    .build();
            ImageLoader.getInstance().displayImage(imgUrl,mPhotoView,options);
        }


        setupFullJob(mJobTypeRow.getText());

        Bundle data = getArguments();

        if (data != null) {
            mInMainMenu = data.getBoolean(KEY_IN_MAIN_MENU,true);
        }
        if (!mInMainMenu) {
            setActionLeftDrawable(R.drawable.common_nav_arrow);
        }

        if (isModify()) {
            setActionRightText(R.string.action_publish);
            mCancelPublish.setVisibility(View.VISIBLE);
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Utils.REQUEST_CODE_PICK_PHOTO);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view == mCancelPublish) {
            CustomerJobsApi.stopJobs(mAdsId);
        } else if (view == mPhotoView) {
            pickPhoto();
        }


        if (view instanceof DictTableRow) {

            if (view == mStartDateRow || view == mEndDateRow) {
                final DictTableRow dateRow = (DictTableRow) view;
                final int[]  date = Utils.stringToDateArray(dateRow.getText());
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateRow.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                },date[0],date[1],date[2]);
                datePicker.show();
            } else if (view == mStartTimeRow || view == mEndTimeRow) {
                final DictTableRow timeRow = (DictTableRow) view;
                int time[] = Utils.stringToTimeArray(timeRow.getText());
                TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeRow.setText(hourOfDay + ":" + minute);
                    }
                },time[0],time[1],true);
                timePicker.show();

            } else {
                int dict = ((DictTableRow) view).getDictType();
                DictFragment.showDict(this, dict);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if ((requestCode & DictFragment.REQUEST_CODE_DICT_MASK) != 0) {
            String dict = data.getStringExtra("dict");
            String code = data.getStringExtra("code");
            int position = requestCode & ~DictFragment.REQUEST_CODE_DICT_MASK;
            switch (position) {
                case DictFragment.POSITION_AREA:
                    mAreaRow.setText(dict);
                    mAreaRow.setTag(code);
                    break;
                case DictFragment.POSITION_FUNCTION:
                    mFunctionTypeRow.setText(dict);
                    mFunctionTypeRow.setTag(code);
                    break;
                case DictFragment.POSITION_SALARY:
                    mSalaryTypeRow.setText(dict);
                    mSalaryTypeRow.setTag(code);
                    break;
                case DictFragment.POSITION_WORK_TYPE:
                    mJobTypeRow.setText(dict);
                    mJobTypeRow.setTag(code);
                    setupFullJob(dict);
                    break;
            }
        } if (requestCode == Utils.REQUEST_CODE_PICK_PHOTO) {
            mHeaderUri = data.getData();
            if (mHeaderUri == null) {
                return;
            }
            mTempPhotoUri = Utils.startImageZoom(this,mHeaderUri,2,1);

        } else if (requestCode == Utils.REQUEST_CODE_CROP_IMAGE) {
            String filePath = null;
            if (data.getData() != null) {
                if (data.getScheme().equals("file")) {
                    filePath = data.getData().getPath();
                } else if (data.getScheme().equals("content")) {
                    Cursor c = getActivity().getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (c != null && c.moveToNext()) {
                        filePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                }
            } else {
                Cursor c = getActivity().getContentResolver().query(mTempPhotoUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c != null && c.moveToNext()) {
                    filePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                }
            }
            if (filePath == null && mTempPhotoUri != null) {
                filePath = mTempPhotoUri.getPath();
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
//            Bitmap bitmap = rotaingImage(readPictureDegree(filePath),srcBitmap);
            mPhotoView.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapData = stream.toByteArray();
            mBase64Data = Base64.encode(bitmapData, 0, bitmapData.length);

            //最後發佈再上傳
//            if (isModify()) {
//                CustomerJobsApi.saveJobImg(mAdsId,mBase64Data);
//            }

            if (file.exists()) {
                if (!file.delete()) {
                    AppUtil.print("delete failed!");
                }
            }

        }
    }

    @Override
    protected void onActionLeft() {
        getActivity().setResult(Activity.RESULT_OK);
        super.onActionLeft();
    }

    private boolean isModify() {
        return mAdsId > 0;
    }

    private void setupFullJob(String type) {
        int visible = View.VISIBLE;
        if("全職".equals(type)) {
            visible = View.GONE;
            mStartDateRow.setText("");
            mEndDateRow.setText("");
            mStartTimeRow.setText("");
            mEndTimeRow.setText("");
        }
        mStartDateRow.setVisibility(visible);
        mEndDateRow.setVisibility(visible);
        mStartTimeRow.setVisibility(visible);
        mEndTimeRow.setVisibility(visible);
    }

}
