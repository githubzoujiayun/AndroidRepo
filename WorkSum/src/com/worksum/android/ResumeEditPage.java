package com.worksum.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.controller.DataController;
import com.worksum.android.controller.UserCoreInfo;
import com.worksum.android.utils.Utils;
import com.worksum.android.views.DictTableRow;
import com.worksum.android.views.EditTableRow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author chao.qin
 *         <p/>
 *         16/10/28
 */
@LayoutID(R.layout.resume_edit_page)
@DataManagerReg(register = DataManagerReg.RegisterType.ALL)
public class ResumeEditPage extends TitlebarFragment {

    private DataItemDetail mTempUserInfo;

    private ImageView mHeaderView;
    private DataController.DataAdapter mDataAdapter;
    private Uri mHeaderUri;
    private Uri mTempHeaderUri;


    private class CommonWatcher implements TextWatcher {

        private View rightView;

        CommonWatcher(View rightView) {
            this.rightView = rightView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int visible = View.INVISIBLE;
            if (!TextUtils.isEmpty(s)) {
                visible = View.VISIBLE;
            }
            rightView.setVisibility(visible);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public static void showResumeEditPage(GeneralFragment fragment) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(fragment.getActivity(), FragmentContainer.class);
        intent.putExtra(KEY_FRAGMENT, ResumeEditPage.class);
        intent.putExtras(extras);
        fragment.startActivityForResult(intent,MeFragment.REQUEST_CODE_UPDATE_RESUME);
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Utils.REQUEST_CODE_PICK_PHOTO);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle(R.string.my_resume_title);
        setActionLeftDrawable(R.drawable.common_nav_arrow);
        setActionRightText(R.string.action_save);

        mHeaderView = (ImageView) findViewById(R.id.resume_edit_head_add);
        mHeaderView.setOnClickListener(this);

        mTempUserInfo = UserCoreInfo.copy();

        setupAndCheckEmailEdit();
        setupAndCheckPhoneEdit();

        setupAndCheckCommonEdit(R.id.resume_edit_english_name);
        setupAndCheckCommonEdit(R.id.resume_edit_chn_name);
//        setupAndCheckCommonEdit(R.id.resume_edit_eng_name);


        setupCommonDict(R.id.resume_edit_area);
        setupCommonDict(R.id.resume_edit_function);
        setupCommonDict(R.id.resume_edit_age);
        setupCommonDict(R.id.resume_edit_sex);
        setupCommonDict(R.id.resume_edit_degree);
        setupCommonDict(R.id.resume_edit_workday);

        if (UserCoreInfo.hasLogined()) {
            ResumeApi.getResumeInfo();
            requestHeadPhoto();
        }
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();

        EditText memoEdit = (EditText) findViewById(R.id.resume_edit_memo);

        try {
            String[] splitName = readValue(R.id.resume_edit_english_name).trim().split("\\s+");
            String[] englishName = new String[2];
            englishName[0] = "";
            englishName[1] = "";
            if (splitName.length == 1) {
                englishName[0] = splitName[0];
            } else if (splitName.length >= 2) {
                englishName[0] = splitName[0];
                englishName[1] = splitName[1];
            }

            mTempUserInfo.setStringValue("FirstName", englishName[0]);
            mTempUserInfo.setStringValue("LastName", englishName[1]);
            mTempUserInfo.setStringValue("Cname", readValue(R.id.resume_edit_chn_name));

            String phone = readValue(R.id.resume_edit_phone);

            String oldPhone = UserCoreInfo.getMobilePhone();

            if (!TextUtils.isEmpty(UserCoreInfo.getMobilePhone()) && !oldPhone.equals(phone)) {
                Tips.showTips(R.string.alert_modify_phone);
                updateValue(R.id.resume_edit_phone,UserCoreInfo.getMobilePhone());
                return;
            }
            mTempUserInfo.setStringValue("Mobile", phone);
            mTempUserInfo.setStringValue("email", readValue(R.id.resume_edit_email));

            mTempUserInfo.setStringValue("Memo", memoEdit.getText().toString());
            mTempUserInfo.setStringValue("WorkFrom", readValue(R.id.resume_edit_workday));
            ResumeApi.updateResumeInfo(mTempUserInfo);
        } catch (EditTableRow.NecessaryException e) {
            Tips.showTips(e.getMessage());
        }

    }

    @Override
    public void onStartRequest(String action) {
        if (ResumeApi.ACTION_UPDATE_RESUME_INFO.equals(action)) {
            Tips.showWaitingTips(getActivity(),getString(R.string.resume_upload));
        } else if (ResumeApi.ACTION_GET_RESUME_INFO.equals(action)) {
            Tips.showWaitingTips(getActivity(),getString(R.string.tips_update_resume));
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        Tips.hiddenWaitingTips();
        if (ResumeApi.ACTION_UPDATE_RESUME_INFO.equals(action)) {
            if (!result.hasError && result.statusCode > 0) {

                Tips.showTips(R.string.resume_upload_succeed);
                getActivity().setResult(Activity.RESULT_OK, new Intent());
                getActivity().finish();
                return;
            }

            int tips = R.string.resume_upload_failed;
            if (result.statusCode == -10) {
                tips = R.string.tips_argument_error;
            } else if (result.statusCode == -11) {
                tips = R.string.tips_workday_error;
            } else if (result.statusCode == -12) {
                tips = R.string.tips_phone_already_exists;
            }
            Tips.showTips(tips);

        } else if (ResumeApi.ACTION_GET_RESUME_INFO.equals(action)) {
            Tips.hiddenWaitingTips();
            if (!result.hasError) {
                UserCoreInfo.setUserLoginInfo(result, true, UserCoreInfo.USER_LOGIN_OTHERS);
                updateValues();
            } else {
                Tips.showTips(R.string.login_get_resume_info_failed);
            }
        }
    }

    private void setupAndCheckCommonEdit(int editId) {
        EditTableRow row = (EditTableRow) findViewById(editId);
        EditText editText = (EditText) row.findViewById(R.id.edit_table_text);
        View rightView = row.findViewById(R.id.resume_item_check);
        editText.addTextChangedListener(new CommonWatcher(rightView));
    }

    private void setupCommonDict(int dictId) {
        DictTableRow row = (DictTableRow) findViewById(dictId);
        row.setOnClickListener(this);
        row.setTag(row.getDictType());
    }


    private void setupAndCheckEmailEdit() {
        EditTableRow row = (EditTableRow) findViewById(R.id.resume_edit_email);
        EditText editText = (EditText) row.findViewById(R.id.edit_table_text);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final ImageView rightView = (ImageView) row.findViewById(R.id.resume_item_check);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int visible = View.INVISIBLE;
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    visible = View.VISIBLE;
                }
                rightView.setVisibility(visible);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 手机不可编辑，简化
     */
    private void setupAndCheckPhoneEdit() {
        EditTableRow row = (EditTableRow) findViewById(R.id.resume_edit_phone);
        EditText editText = (EditText) row.findViewById(R.id.edit_table_text);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter lengthFilter = new InputFilter.LengthFilter(8);
        editText.setFilters(new InputFilter[]{lengthFilter});

        if (!TextUtils.isEmpty(UserCoreInfo.getMobilePhone())) {
            editText.setEnabled(false);
        }

        final ImageView rightView = (ImageView) row.findViewById(R.id.resume_item_check);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int visible = View.INVISIBLE;
                if (Utils.matchesPhone(s.toString())) {
                    visible = View.VISIBLE;
                }
                rightView.setVisibility(visible);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.resume_edit_head_add:
                pickPhoto();
                break;
        }

        //以下是数据字典
        if (view.getTag() == null) {
            return;
        }
        if (!(view.getTag() instanceof Integer)) {
            return;
        }
        int dictType = (Integer) view.getTag();

        if (dictType == DictFragment.POSITION_WORKDAY) {

            final DictTableRow row = (DictTableRow) view;

            String rowText = row.getText();

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
                    mTempUserInfo.setStringValue("WorkFrom",date);

                }
            },year,month);

            datePicker.show();
            return;
        }
        DictFragment.showDict(ResumeEditPage.this, dictType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Tips.showTips(R.string.tips_canceled);
            return;
        }
        if ((requestCode & DictFragment.REQUEST_CODE_DICT_MASK) != 0) {
            String dict = data.getStringExtra("dict");
            String code = data.getStringExtra("code");
            int position = requestCode & ~DictFragment.REQUEST_CODE_DICT_MASK;
            switch (position) {
                case DictFragment.POSITION_AREA:
                    updateValue(R.id.resume_edit_area, dict);
                    mTempUserInfo.setStringValue("City", code);
                    break;
                case DictFragment.POSITION_FUNCTION:
                    updateValue(R.id.resume_edit_function, dict);
                    mTempUserInfo.setStringValue("FunctionType", code);
                    break;
                case DictFragment.POSITION_SEX:
                    updateValue(R.id.resume_edit_sex, dict);
                    mTempUserInfo.setStringValue("Gender", dict);
                    break;
                case DictFragment.POSITION_AGE:
                    updateValue(R.id.resume_edit_age, dict);
                    mTempUserInfo.setStringValue("AgeFrom", dict);
                    break;
                case DictFragment.POSITION_DEGREE:
                    updateValue(R.id.resume_edit_degree,dict);
                    mTempUserInfo.setStringValue("Degree",code);
                    break;
            }
        } else if (requestCode == Utils.REQUEST_CODE_PICK_PHOTO) {
            mHeaderUri = data.getData();
            if (mHeaderUri == null) {
                return;
            }
            mTempHeaderUri = Utils.startImageZoom(this,mHeaderUri);

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
                Cursor c = getActivity().getContentResolver().query(mTempHeaderUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c != null && c.moveToNext()) {
                    filePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                }
            }
            if (filePath == null) {
                return;
            }
            File file = new File(filePath);
            if (file.length() <= 0) {
                return;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
//            Bitmap bitmap = rotaingImage(readPictureDegree(filePath),srcBitmap);
            mHeaderView.setImageBitmap(bitmap);

            uploadPhoto(bitmap);


            if (file.exists()) {
                if (!file.delete()) {
                    AppUtil.print("delete failed!");
                }

            }

        }
    }


    private void uploadPhoto(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapData = stream.toByteArray();
        final String base64Data = Base64.encode(bitmapData, 0, bitmapData.length);

        AppCoreInfo.getCacheDB().setBinValue("head_icon", UserCoreInfo.getUserID(), bitmapData);

        if (mDataAdapter != null) {
            mDataAdapter.cancel(true);
        }

        mDataAdapter = DataController.getInstance().newDataAdapter();
        mDataAdapter.setDataListener(new DataController.DataLoadAdapter() {
            @Override
            public void onSucceed(DataItemResult result) {
                if (result.statusCode == 1) {
                    getActivity().setResult(RESULT_OK,new Intent());
                    Tips.showTips(R.string.self_photo_upload_succeed);
                } else {
                    Tips.showTips(R.string.self_photo_upload_failed);
                }
            }

            @Override
            public void onFailed(DataItemResult result, boolean isNetworkConnected) {
                if (!isNetworkConnected) {
                    Tips.showTips(R.string.network_invalid);
                }
            }


            @Override
            public DataItemResult onLoadData() {
                return ResumeApi.savePhotoFile(base64Data);
            }

        });
        mDataAdapter.executeOnPool();
    }

    private void requestHeadPhoto() {
        if (mDataAdapter != null) {
            mDataAdapter.cancel(true);
        }
        mDataAdapter = DataController.getInstance().newDataAdapter();
        mDataAdapter.setDataListener(new DataController.DataLoadAdapter() {
            @Override
            public void onSucceed(DataItemResult result) {
                if (result.statusCode == 1) {
                    byte[] data = Base64.decode(result.message);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
                    mHeaderView.setImageBitmap(bitmap);

                    AppCoreInfo.getCacheDB().setBinValue("head_icon", UserCoreInfo.getUserID(), data);
                }
            }

            @Override
            public void onBeforeLoad() {
                byte[] data = AppCoreInfo.getCacheDB().getBinValue("head_icon", UserCoreInfo.getUserID());
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) {
                        mHeaderView.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public DataItemResult onLoadData() {
                return ResumeApi.getPhoto();
            }

        });
        mDataAdapter.setShowTips(false);
        if (UserCoreInfo.hasLogined()) {
            mDataAdapter.execute();
        }
    }

    private void updateValues() {
        updateValue(R.id.resume_edit_english_name, UserCoreInfo.getFirstName() + " " + UserCoreInfo.getLastName());
        updateValue(R.id.resume_edit_eng_name, UserCoreInfo.getLastName());
        updateValue(R.id.resume_edit_chn_name, UserCoreInfo.getUserName());
        updateValue(R.id.resume_edit_phone, UserCoreInfo.getMobilePhone());
        updateValue(R.id.resume_edit_email, UserCoreInfo.getEmail());

        updateValue(R.id.resume_edit_sex, UserCoreInfo.getGender());
        updateValue(R.id.resume_edit_age, UserCoreInfo.getAgeFrom());
        updateValue(R.id.resume_edit_function, UserCoreInfo.getFunctionName());
        updateValue(R.id.resume_edit_degree, UserCoreInfo.getDegree());
        updateValue(R.id.resume_edit_workday, UserCoreInfo.getWorkYear().toString());
        updateValue(R.id.resume_edit_area, UserCoreInfo.getAreaName());

        EditText editText = (EditText) findViewById(R.id.resume_edit_memo);
        editText.setText(UserCoreInfo.getMemo());
    }

    private void updateValue(int dictId, String dict) {
        View row = findViewById(dictId);
        TextView textView = (TextView) row.findViewById(R.id.edit_table_text);
        textView.setText(dict);
    }

    private String readValue(int viewId) throws EditTableRow.NecessaryException {
        EditTableRow row = (EditTableRow) findViewById(viewId);
        return row.getText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHeaderView.setImageBitmap(null);
    }
}
