package com.worksum.android.company;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.worksum.android.ListCell;
import com.worksum.android.R;
import com.worksum.android.TitlebarFragment;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.CustomerResumeApi;
import com.worksum.android.views.HeaderIconView;

/**
 * @author chao.qin
 * @since 2016/12/7
 */
@Titlebar(titleId = R.string.title_search_employee)
@LayoutID(R.layout.search_employee_fragment)
public class SearchEmployee extends TitlebarFragment implements AdapterView.OnItemClickListener {

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
    private static final String DETAIL_KEY_EMAIL = "Email";
    private static final String DETAIL_KEY_IS_FUTO_OPEN = "IsFuTuOpen";
    private static final String DETAIL_KEY_WORK_YEARS = "workyears";
    private static final String DETAIL_KEY_CITY_NAME = "CityName";
    private static final String DETAIL_KEY_RESUME_ID = "RMID";
    private static final String DETAIL_KEY_IMG_URL = "ImgUrl";

    private ImageView mEditCloseView;
    private EditText mSearchEditView;
    private ImageView mSearchButton;

    private DataListView mListView;


    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mEditCloseView = findViewById(R.id.edit_close);
        mSearchButton = findViewById(R.id.search_employee_search_btn);
        mSearchEditView = findViewById(R.id.search_employee_search_edit);
        mListView = findViewById(R.id.listview);

        mSearchEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditCloseView.setVisibility(View.INVISIBLE);
                if (s.length() > 0) {
                    mEditCloseView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEditCloseView.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mListView.setOnItemClickListener(this);
        mListView.setAllowAutoTurnPage(true);
        mListView.setDataCellClass(EmployeeCell.class, this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.transparent_00000000)));
        mListView.setDividerHeight(12);
        mListView.setTimeTag(getClass().getName());

        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                return CustomerResumeApi.getResumeList(mSearchEditView.getText().toString(),pageAt - 1,pageSize);
            }
        });
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mSearchButton) {
            mListView.refreshData();
        } else if (view == mEditCloseView) {
            mSearchEditView.setText("");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ResumeViewFragment.show(this,mListView.getItem(position),ResumeViewFragment.FROM_SEARCH_EMPLOYEE);
    }

    @LayoutID(R.layout.search_employee_cell)
    private class EmployeeCell extends ListCell{
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
                String firstName = mDetail.getString(DETAIL_KEY_FIRST_NAME);
                String lastName = mDetail.getString(DETAIL_KEY_LAST_NAME);
                StringBuilder nameBuffer = new StringBuilder();
                nameBuffer.append(firstName.trim());
                if (nameBuffer.length() > 0) {
                    nameBuffer.append(" ");
                }
                nameBuffer.append(lastName);
                name = nameBuffer.toString();
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

            mHeaderView.loadURL(mDetail.getString(DETAIL_KEY_IMG_URL));
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
