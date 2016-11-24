package com.worksum.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.apis.WorkExpApi;
import com.worksum.android.controller.DataController;
import com.worksum.android.controller.UserCoreInfo;
import com.worksum.android.utils.Utils;

import java.util.Locale;

/**
 * @author chao.qin
 *
 * @since jobpedia 1.0.6 at 2016/11/1.
 */
@LayoutID(R.layout.me)
public class MeFragment extends TitlebarFragment {

    public static final int REQUEST_CODE_UPDATE_RESUME = 1;

    private TextView mNameView;
    private TextView mAreaYearView;
    private TextView mFunctionView;
    private TextView mPhoneView;
    private TextView mEmailView;
    private TextView mMemoView;

    private TextView mProfileEditView;
    private TextView mExperienceEditView;

    private DataListView mListView;

    private Button mUnLoginButton;

    private ImageView mHeaderView;
    private DataController.DataAdapter mDataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollbarEnable();
    }


    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        setTitle(R.string.title_self);

        mNameView = (TextView) findViewById(R.id.me_user_name);
        mAreaYearView = (TextView) findViewById(R.id.me_area_workday);
        mFunctionView = (TextView) findViewById(R.id.me_function);
        mPhoneView = (TextView) findViewById(R.id.me_phone);
        mEmailView = (TextView) findViewById(R.id.me_email);
        mMemoView = (TextView) findViewById(R.id.me_memo);

        mHeaderView = (ImageView) findViewById(R.id.me_head);

        mListView = (DataListView) findViewById(R.id.me_experience_list);

        mProfileEditView = (TextView) findViewById(R.id.me_profile_edit);
        mExperienceEditView = (TextView) findViewById(R.id.me_experience_edit);

        mProfileEditView.setOnClickListener(this);
        mExperienceEditView.setOnClickListener(this);

        mListView = (DataListView) findViewById(R.id.me_experience_list);
        mListView.setDataCellClass(ExperienceCell.class,this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.transparent_00000000)));
        mListView.setDividerHeight(20);

        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                DataItemResult result = WorkExpApi.fetchWorkExp();
                if (result.getDataCount() <= 0 && TextUtils.isEmpty(result.message)) {
                    result.message = getString(R.string.experience_empty);
                }
                return result;
            }
        });

        mUnLoginButton = (Button) findViewById(R.id.me_unlogin_button);
        mUnLoginButton.setOnClickListener(this);

        switchLoginUI();
    }

    private void switchLoginUI() {
        if (UserCoreInfo.hasLogined()) {
            findViewById(R.id.me_unlogin).setVisibility(View.GONE);
            findViewById(R.id.me_login).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.me_unlogin).setVisibility(View.VISIBLE);
            findViewById(R.id.me_login).setVisibility(View.GONE);
        }
        updateBasicInfo();
    }

    @Override
    public void onTabSelect() {
        super.onTabSelect();
        final ScrollView sv = getScrollView();
        sv.smoothScrollTo(0,0);
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        if (WorkExpApi.ACTION_GET_WORK_EXP.equals(action)) {
            mListView.replaceData(result);
        } else if (WorkExpApi.ACTION_DELETE_WORK_EXP.equals(action)) {
            mListView.refreshData();
        } else if (JobsApi.ACTION_GET_RESUME_INFO.equals(action)) {
            Tips.hiddenWaitingTips();
            if (!result.hasError) {
//                UserCoreInfo.setUserLoginInfo(result, true, UserCoreInfo.USER_LOGIN_OTHERS);
                switchLoginUI();
            } else {
                Tips.showTips(R.string.login_get_resume_info_failed);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.me_experience_edit:
                ExperienceHomeFragment.showExperienceHome(getActivity(),mListView.getListData());
                break;
            case R.id.me_profile_edit:
                ResumeEditPage.showResumeEditPage(this);
                break;
            case R.id.me_unlogin_button:
                LoginFragment.showLoginFragment(getActivity());
                break;
        }
    }

    private void updateBasicInfo() {

        int rightText = R.string.self_right_action_login;
        if (UserCoreInfo.hasLogined()) {
            rightText = R.string.self_right_action_logout;
        }
        setActionRightText(rightText);

        String name = UserCoreInfo.getUserName();
        int visible = View.GONE;
        if (!TextUtils.isEmpty(name)) {
            visible = View.VISIBLE;
            mNameView.setText(name);
        }
        mNameView.setVisibility(visible);

        String area = UserCoreInfo.getAreaName();
        int workYear = UserCoreInfo.getWorkYears();
        StringBuilder builder = new StringBuilder();
        visible = View.GONE;
        if (!TextUtils.isEmpty(area)) {
            builder.append(area);
        }

        if (builder.length() > 0) {
            builder.append(" | ");
        }

        int workYearId = workYear <= 0? R.string.me_no_workyears:R.string.me_format_workyears;
        builder.append(getString(workYearId,workYear));

        if (builder.length() > 0) {
            visible = View.VISIBLE;
            mAreaYearView.setText(builder.toString());
        }
        mAreaYearView.setVisibility(visible);

        String functionType = UserCoreInfo.getFunctionName();
        visible = View.GONE;
        if(!TextUtils.isEmpty(functionType)) {
            visible = View.VISIBLE;
            mFunctionView.setText(functionType);
        }
        mFunctionView.setVisibility(visible);

        visible = View.GONE;
        String phone = UserCoreInfo.getMobilePhone();
        if (!TextUtils.isEmpty(phone)) {
            visible = View.VISIBLE;
            mPhoneView.setText(getString(R.string.me_formate_phone,phone));
        }
        mPhoneView.setVisibility(visible);

        visible = View.GONE;
        String email = UserCoreInfo.getEmail();
        if (!TextUtils.isEmpty(email)) {
            visible = View.VISIBLE;
            mEmailView.setText(getString(R.string.me_formate_email,email));
        }
        mEmailView.setVisibility(visible);

        mMemoView.setText(UserCoreInfo.getMemo());

        byte[] imgData = AppCoreInfo.getCacheDB().getBinValue("head_icon", UserCoreInfo.getUserID());
        mHeaderView.setImageResource(R.drawable.ico_default_head);
        if (imgData != null) {
            mHeaderView.setImageBitmap(BitmapFactory.decodeByteArray(imgData,0,imgData.length));
        }
        requestHeadPhoto();

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
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
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

    @Override
    protected void onActionRight() {
        if (UserCoreInfo.hasLogined()) {
            logout();
        } else {
            FragmentContainer.FullScreenContainer.showLoginFragment(getActivity());
        }
    }

    private void logout() {
        Tips.showConfirm(getString(R.string.self_logout_alter_title),getString(R.string.self_logout_alter), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    UserCoreInfo.logout();
                    switchLoginUI();
                }
            }
        });
    }

    @Override
    public void onUserStatusChanged(int loginType) {
        super.onUserStatusChanged(loginType);
        mListView.refreshData();
        switchLoginUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
    }

    public static final String API_KEY_EXP_ID = "ID";
    public static final String API_KEY_EXP_STATUS = "Status";
    public static final String API_KEY_EXP_COMPANY = "CompanyName";
    public static final String API_KEY_EXP_POSITION = "Position";
    public static final String API_KEY_EXP_FROM_TIME = "FromTime";
    public static final String API_KEY_EXP_CREATE_DATE = "CreateDate";
    public static final String API_KEY_EXP_TO_TIME = "ToTime";
    public static final String API_KEY_EXP_MEMO = "Memo";


    @LayoutID(R.layout.me_exp_cell)
    private class ExperienceCell extends ListCell{

        private TextView mBasicView;
        private TextView mMemoView;

        @Override
        public void bindView() {
            mBasicView = (TextView) findViewById(R.id.me_exp_cell_basic_infos);
            mMemoView = (TextView) findViewById(R.id.me_exp_cell_memo);
        }

        @Override
        public void bindData() {
            String companyName = mDetail.getString(API_KEY_EXP_COMPANY);
            String jobName = mDetail.getString(API_KEY_EXP_POSITION);
            String fromTime = mDetail.getString(API_KEY_EXP_FROM_TIME);
            String toTime = mDetail.getString(API_KEY_EXP_TO_TIME);
            String memo = mDetail.getString(API_KEY_EXP_MEMO);

            String content = "公司 : ";
            content +=  companyName;
            content += "\r\n";
            content += "職位 : ";
            content += jobName;

            if(!TextUtils.isEmpty(fromTime.trim() + toTime.trim()) || true) {
                content += "\r\n";
                content += "时间 : ";
                String startTime = Utils.dateFormat(fromTime);
                if (!TextUtils.isEmpty(startTime)) {
                    content += startTime;
                    content += " - ";
                }
                content += Utils.dateFormat(toTime);
            }

            mBasicView.setText(content);
            mMemoView.setText(memo);
        }
    }
}
