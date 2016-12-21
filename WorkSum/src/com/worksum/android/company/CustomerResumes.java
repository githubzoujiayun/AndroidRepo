package com.worksum.android.company;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.worksum.android.FragmentContainer;
import com.worksum.android.GeneralFragment;
import com.worksum.android.ListCell;
import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.CustomerJobApplyApi;
import com.worksum.android.views.HeaderIconView;

/**
 * @author chao.qin
 * @since 2016/12/9
 */
@LayoutID(R.layout.customer_resumes)
@Titlebar(leftDrawableId = R.drawable.common_nav_arrow)
public class CustomerResumes extends TitlebarFragment implements AdapterView.OnItemClickListener {

    private static final String KEY_JOB_ID = "jobId";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TITLE = "title";

    private static final String DETAIL_KEY_AGE = "AgeFrom";
    private static final String DETAIL_KEY_GENDER = "Gender";
    private static final String DETAIL_KEY_FUNCTION_TYPE = "FunctionTypeName";
    private static final String DETAIL_KEY_CITY_CODE = "City";
    private static final String DETAIL_KEY_CITY_ADDR = "CityAddr";
    private static final String DETAIL_KEY_ENG_NAME = "EName";
    private static final String DETAIL_KEY_CHN_NAME = "CName";
    private static final String DETAIL_KEY_MEMO = "Memo";
    private static final String DETAIL_KEY_SOURCE = "Source";
    private static final String DETAIL_KEY_DEGREE = "DegreeName";
    private static final String DETAIL_KEY_EMAIL = "Email";
    private static final String DETAIL_KEY_IS_FUTO_OPEN = "IsFuTuOpen";
    private static final String DETAIL_KEY_WORK_YEARS = "workyears";
    private static final String DETAIL_KEY_CITY_NAME = "CityName";
    private static final String DETAIL_KEY_HEADER = "ImgUrl";

    private DataListView mListView;

    private int mJobId;
    private String mStatus;


    public static void show(GeneralFragment fragment,int jobId,String status,String title) {
        Intent intent = new Intent(fragment.getActivity(),FragmentContainer.class);
        intent.putExtra(KEY_FRAGMENT, CustomerResumes.class);
        intent.putExtra(TitlebarFragment.KEY_SCROLLBAR_ENABLED,false);
        intent.putExtra(KEY_JOB_ID,jobId);
        intent.putExtra(KEY_STATUS,status);
        intent.putExtra(KEY_TITLE,title);
        fragment.startActivity(intent);
    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);

        Bundle data = getArguments();
        mJobId = data.getInt(KEY_JOB_ID);
        mStatus = data.getString(KEY_STATUS);
        setTitle(data.getString(KEY_TITLE));

        mListView = (DataListView) findViewById(R.id.resume_list);
        mListView.setAllowAutoTurnPage(true);
        mListView.setDataCellClass(ResumeCell.class, this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
        mListView.setDividerHeight(1);
        mListView.setOnItemClickListener(this);
        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {

                return CustomerJobApplyApi.getJobApplyResumeList(mJobId,pageAt - 1,pageSize,mStatus,0,0);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int from = ResumeViewFragment.FROM_COMMON;
        if (mStatus.equals(CustomerApprovedAds.APPROVED_STATUS_APPLIED)) {
            from = ResumeViewFragment.FROM_APPLY_JOB;
        } else if (mStatus.equals(CustomerApprovedAds.APPROVED_STATUS_FIRSTOR)) {
            from = ResumeViewFragment.FROM_APPLY_FIRST;
        }
        ResumeViewFragment.show(this,mListView.getItem(position),from,mJobId);
    }



    @LayoutID(R.layout.resume_list_cell)
    private class ResumeCell extends ListCell {

        private HeaderIconView mHeaderView;
        private TextView mTitleView;
        private TextView mDescriptionView;
        private TextView mSecondDescriptionView;

        @Override
        public void bindView() {
            mHeaderView = (HeaderIconView) findViewById(R.id.resume_list_cell_head);
            mTitleView = (TextView) findViewById(R.id.resume_list_cell_title);
            mDescriptionView = (TextView) findViewById(R.id.resume_list_cell_description);
            mSecondDescriptionView = (TextView) findViewById(R.id.resume_list_cell_second_description);
        }

        @Override
        public void bindData() {
            String name = mDetail.getString(DETAIL_KEY_CHN_NAME);
            if (TextUtils.isEmpty(name)) {
                name = mDetail.getString(DETAIL_KEY_ENG_NAME);
            }
            mTitleView.setText(name);

            String workYear = mDetail.getString(DETAIL_KEY_WORK_YEARS);
            String workYearDpt = "";
            if (!TextUtils.isEmpty(workYear)) {
                workYearDpt = getString(R.string.format_work_years,workYear);
            }

            mDescriptionView.setText(buildDescription(mDetail.getString(DETAIL_KEY_AGE),
                    mDetail.getString(DETAIL_KEY_DEGREE),
                    workYearDpt,
                    mDetail.getString(DETAIL_KEY_FUNCTION_TYPE)
            ));

            mSecondDescriptionView.setText(buildDescription(mDetail.getString(DETAIL_KEY_CITY_NAME)));

//            ImageLoader.getInstance().displayImage(mDetail.getString(DETAIL_KEY_HEADER),mHeaderView);
            mHeaderView.loadURL(mDetail.getString(DETAIL_KEY_HEADER));
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
            String description = descriptionBuffer.toString();
            if (description.endsWith(divider)) {
                description = description.substring(0,description.lastIndexOf(divider));
            }
            return description;
        }

    }
}
