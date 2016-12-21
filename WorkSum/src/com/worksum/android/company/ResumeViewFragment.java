package com.worksum.android.company;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.FragmentContainer;
import com.worksum.android.GeneralFragment;
import com.worksum.android.ListCell;
import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.CustomerJobsApi;
import com.worksum.android.apis.CustomerResumeApi;
import com.worksum.android.apis.IMApi;
import com.worksum.android.apis.WorkExpApi;
import com.worksum.android.nim.session.SessionHelper;
import com.worksum.android.utils.Utils;


/**
 * @author chao.qin
 *
 * @since 2016/12/12
 */

@Titlebar(titleId = R.string.title_resume_view_fragment,leftDrawableId = R.drawable.common_nav_arrow)
@LayoutID(R.layout.resume_view_fragment)
@DataManagerReg(actions = {
        IMApi.ACTION_GET_VIEW_TOKEN,
        CustomerResumeApi.ACTION_GET_APPLY_RESUME_INFO,
        CustomerResumeApi.ACTION_GET_RESUME_INFO,
        CustomerJobsApi.ACTION_SET_RESUME_BACKUP,
        IMApi.ACTION_GET_VIEW_APPLY_TOKEN
            })
public class ResumeViewFragment extends TitlebarFragment implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {

    /**
     * <AgeFrom>36-45</AgeFrom>
     * <Gender>男</Gender>
     * <Mobile>66333333</Mobile>
     * <FunctionType>0104</FunctionType>
     * <City>0107</City>
     * <CityAddr>0107</CityAddr>
     * <EMail>piaowxy@163.com</EMail>
     * <UpdateDate>2016-12-15T10:24:10.17+08:00</UpdateDate>
     * <FirstName>piaopiao</FirstName>
     * <LastName>piaopiao</LastName>
     * <Cname>顾林彬</Cname>
     * <Memo>大家反馈反馈梦想就放假咕叽咕叽放假就放假春节长假奶粉促进出口</Memo>
     * <Source>ios</Source>
     * <WorkYear>2013-12-01T00:00:00+08:00</WorkYear>
     * <Degree>0106</Degree>
     * <Email>piaowxy@163.com</Email>
     * <workyears>3</workyears>
     * <IMToken>32e02f3ac014b2dd35c18600ec17ac01</IMToken>
     * <ImgUrl />
     * <AreaName>深水埗區</AreaName>
     * <FunctionTypeName>派發傳單員</FunctionTypeName>
     * <DegreeName>高級文憑/ 副學士</DegreeName>

     */

    private static final String DETAIL_KEY_AGE = "AgeFrom";
    private static final String DETAIL_KEY_GENDER = "Gender";
    private static final String DETAIL_KEY_FUNCTION_TYPE = "FunctionTypeName";
    private static final String DETAIL_KEY_CITY_CODE = "City";
    private static final String DETAIL_KEY_CITY_ADDR = "CityAddr";
    private static final String DETAIL_KEY_FIRST_NAME = "FirstName";
    private static final String DETAIL_KEY_LAST_NAME = "LastName";
    private static final String DETAIL_KEY_CHN_NAME = "Cname";
    private static final String DETAIL_KEY_MEMO = "Memo";
    private static final String DETAIL_KEY_SOURCE = "Source";
    private static final String DETAIL_KEY_DEGREE = "DegreeName";
    private static final String DETAIL_KEY_EMAIL = "EMail";
    private static final String DETAIL_KEY_IS_FUTO_OPEN = "IsFuTuOpen";
    private static final String DETAIL_KEY_WORK_YEARS = "workyears";
    private static final String DETAIL_KEY_CITY_NAME = "AreaName";
    private static final String DETAIL_KEY_RESUME_ID = "RMID";
    private static final String DETAIL_KEY_PHONE = "Mobile";
    private static final String DETAIL_KEY_IMG_URL = "ImgUrl";

    public static final int REQUEST_CODE_UPDATE_RESUME = 1;
    private static final String KEY_DETAILS = "resumeDetail";

    public static final int FROM_COMMON = 0;
    public static final int FROM_SEARCH_EMPLOYEE = 1;
    public static final int FROM_APPLY_JOB = 2;
    public static final int FROM_APPLY_FIRST = 3;
    public static final int FROM_JOB_WATCHER = 4;

    public static final boolean mFromApplyJob = false;

    private static final String KEY_FROM = "from";
    private static final String KEY_JOB_ID = "jobId";

    private static final int NO_JOB_ID = -1;


    private TextView mNameView;
    private TextView mAreaYearView;
    private TextView mFunctionView;
    private TextView mPhoneView;
    private TextView mEmailView;
    private TextView mMemoView;

    private Button mNimAction;
    private Button mCallAction;

    private CheckBox mPassedCheck;

    private DataListView mListView;


    private ImageView mHeaderView;

    private String mCallNumber;

    private String mResumeId;
    private int mJobId;
    private int mFrom;

    public static void show(GeneralFragment fragment, DataItemDetail detail, int from) {
        show(fragment,detail,from,NO_JOB_ID);
    }

    public static void show(GeneralFragment fragment, DataItemDetail detail,int from,int jobId) {
        Intent intent = new Intent(fragment.getActivity(),FragmentContainer.class);
        intent.putExtra(KEY_SCROLLBAR_ENABLED,false);
        intent.putExtra(KEY_FRAGMENT,ResumeViewFragment.class);
        intent.putExtra(KEY_DETAILS,detail);
        intent.putExtra(KEY_FROM,from);
        intent.putExtra(KEY_JOB_ID ,jobId);
        fragment.startActivity(intent);
    }


    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);


        DataItemDetail detail = getArguments().getParcelable(KEY_DETAILS);
        mFrom = getArguments().getInt(KEY_FROM);
        mJobId = getArguments().getInt(KEY_JOB_ID);

        mResumeId = detail.getString(DETAIL_KEY_RESUME_ID);



        mNameView = findViewById(R.id.resume_view_user_name);
        mAreaYearView = findViewById(R.id.resume_view_area_workday);
        mFunctionView = findViewById(R.id.resume_view_function);
        mPhoneView = findViewById(R.id.resume_view_phone);
        mEmailView = findViewById(R.id.resume_view_email);
        mMemoView = findViewById(R.id.resume_view_memo);

        mHeaderView = findViewById(R.id.resume_view_head);

        mNimAction = findViewById(R.id.resume_view_nim_action);
        mCallAction = findViewById(R.id.resume_view_call_action);
        mPassedCheck = findViewById(R.id.resume_view_checkbox);

        mNimAction.setOnClickListener(this);
        mCallAction.setOnClickListener(this);

        mPassedCheck.setOnCheckedChangeListener(this);

        mListView = findViewById(R.id.resume_view_experience_list);
        mListView.setDataCellClass(ExperienceCell.class,this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.transparent_00000000)));
        mListView.setDividerHeight(20);

        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                DataItemResult result = WorkExpApi.fetchWorkExp(mResumeId);
                if (result.getDataCount() <= 0 && TextUtils.isEmpty(result.message)) {
                    result.message = getString(R.string.resume_view_experience_empty);
                }
                return result;
            }
        });
        bindData(detail);

        if (mFrom == FROM_APPLY_JOB) {
            mPassedCheck.setVisibility(View.VISIBLE);
        }
        if (mFrom == FROM_APPLY_JOB || mFrom == FROM_APPLY_FIRST) {
            CustomerResumeApi.getApplyResumeViewInfo(mResumeId);
        } else {
            CustomerResumeApi.getResumeViewInfo(mResumeId);
        }
    }

    private void bindData(DataItemDetail detail) {
        String name = detail.getString(DETAIL_KEY_CHN_NAME);
        if (TextUtils.isEmpty(name)) {
            String firstName = detail.getString(DETAIL_KEY_FIRST_NAME);
            String lastName = detail.getString(DETAIL_KEY_LAST_NAME);
            StringBuilder nameBuffer = new StringBuilder();
            nameBuffer.append(firstName.trim());
            if (nameBuffer.length() > 0) {
                nameBuffer.append(" ");
            }
            nameBuffer.append(lastName);
            name = nameBuffer.toString();
        }

//        setTitle(name);
        setVisibleText(mNameView,name);
        setVisibleText(mFunctionView,detail.getString(DETAIL_KEY_FUNCTION_TYPE));
        setVisibleText(mPhoneView,detail.getString(DETAIL_KEY_PHONE));
        setVisibleText(mEmailView,detail.getString(DETAIL_KEY_EMAIL));
        setVisibleText(mMemoView,detail.getString(DETAIL_KEY_MEMO));

        String ageFrom = detail.getString(DETAIL_KEY_AGE);
        String gender = detail.getString(DETAIL_KEY_GENDER);
        String degree = detail.getString(DETAIL_KEY_DEGREE);
        String workYears = detail.getString(DETAIL_KEY_WORK_YEARS);
        String area = detail.getString(DETAIL_KEY_CITY_NAME);

        mAreaYearView.setText(buildDescription(ageFrom,degree,workYears,area));

        mCallNumber = detail.getString(DETAIL_KEY_PHONE);

        String imgUrl = detail.getString(DETAIL_KEY_IMG_URL);
        ImageLoader.getInstance().displayImage(imgUrl,mHeaderView);
    }

    private void setVisibleText(TextView textView,String text) {
        textView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
        }
        textView.setText(text);
    }

    private String buildDescription(String... args) {
        StringBuilder descriptionBuffer = new StringBuilder();
        final String divider = " / ";
        for (String arg: args) {
            if (!TextUtils.isEmpty(descriptionBuffer) && !descriptionBuffer.toString().endsWith(divider)) {
                descriptionBuffer.append(divider);
            }
            descriptionBuffer.append(arg.trim());
        }
        return descriptionBuffer.toString();
    }

    @Override
    public void onStartRequest(String action) {
        super.onStartRequest(action);
        if (CustomerJobsApi.ACTION_SET_RESUME_BACKUP.equals(action)) {
            Tips.showTips(R.string.tips_setting);
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        if (WorkExpApi.ACTION_GET_WORK_EXP.equals(action)) {
            mListView.replaceData(result);
        } else if (IMApi.ACTION_GET_VIEW_TOKEN.equals(action) || IMApi.ACTION_GET_VIEW_APPLY_TOKEN.equals(action)) {
            if (!result.hasError && result.getDataCount() > 0) {
                String imId = result.getItem(0).getString("ID").toLowerCase();
                SessionHelper.startP2PSession(getActivity(),imId);
            } else {
                Tips.showTips(R.string.tips_unknown_contact);
            }
        } else if (CustomerResumeApi.ACTION_GET_RESUME_INFO.equals(action) || CustomerResumeApi.ACTION_GET_APPLY_RESUME_INFO.equals(action)) {
            if (!result.hasError && result.getDataCount() > 0) {
                bindData(result.getItem(0));
                if (TextUtils.isEmpty(mCallNumber)) {
                    mCallAction.setVisibility(View.GONE);
                    mNimAction.setVisibility(View.VISIBLE);
                    mNimAction.setBackgroundResource(R.drawable.ic_long_nim_selector);
                } else {
                    mCallAction.setVisibility(View.VISIBLE);
                    mNimAction.setVisibility(View.VISIBLE);
                    mNimAction.setBackgroundResource(R.drawable.ic_nim_selector);
                }
            } else {
                String message = result.message;
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.tips_load_failed);
                }
                Tips.showTips(message);
            }
        } else if (CustomerJobsApi.ACTION_SET_RESUME_BACKUP.equals(action)) {
            Tips.hiddenWaitingTips();
            if (result.statusCode == 1) {
                Tips.showTips(R.string.tips_set_succed);
                mPassedCheck.setVisibility(View.GONE);
            } else {
                int tips = R.string.tips_set_failed;
                if (result.statusCode == -10) {
                    tips = R.string.tips_argument_error;
                }
                Tips.showTips(tips);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == mNimAction) {
            if (mFrom == FROM_APPLY_JOB || mFrom == FROM_APPLY_FIRST) {
                IMApi.getViewApplyToken(mResumeId);
            } else {
                IMApi.getViewToken(mResumeId);

            }

        } else if (view == mCallAction) {
            if (!TextUtils.isEmpty(mCallNumber)) {
                Tips.showConfirm("", getString(R.string.confirm_content_call,mCallNumber), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            call(mCallNumber);
                        }
                    }
                });

            }
        }
    }

    private void call(String callNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + callNumber);
        intent.setData(data);
        startActivity(intent);
    }


    public static final String API_KEY_EXP_ID = "ID";
    public static final String API_KEY_EXP_STATUS = "Status";
    public static final String API_KEY_EXP_COMPANY = "CompanyName";
    public static final String API_KEY_EXP_POSITION = "Position";
    public static final String API_KEY_EXP_FROM_TIME = "FromTime";
    public static final String API_KEY_EXP_CREATE_DATE = "CreateDate";
    public static final String API_KEY_EXP_TO_TIME = "ToTime";
    public static final String API_KEY_EXP_MEMO = "Memo";

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Tips.showConfirm("", getString(R.string.confirm_content_add_to_first_list), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        CustomerJobsApi.setResumeBackUp(mResumeId, mJobId);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        mPassedCheck.setChecked(false);
                    }
                }
            });
        }
    }


    @LayoutID(R.layout.me_exp_cell)
    private class ExperienceCell extends ListCell {

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
                }
                if (!TextUtils.isEmpty(toTime)) {
                    content += " - ";
                    content += Utils.dateFormat(toTime);
                }
            }

            mBasicView.setText(content);
            mMemoView.setText(memo);
        }
    }
}

