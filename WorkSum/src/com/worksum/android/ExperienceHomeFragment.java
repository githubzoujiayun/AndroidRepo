package com.worksum.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.WorkExpApi;

/**
 * @author chao.qin
 *
 *
 */

@LayoutID(R.layout.experience_home)
@DataManagerReg(register = DataManagerReg.RegisterType.ALL)
public class ExperienceHomeFragment extends TitlebarFragment implements AdapterView.OnItemClickListener {

    public static final String API_KEY_EXP_ID = "ID";
    public static final String API_KEY_EXP_STATUS = "Status";
    public static final String API_KEY_EXP_COMPANY = "CompanyName";
    public static final String API_KEY_EXP_POSITION = "Position";
    public static final String API_KEY_EXP_FROM_TIME = "FromTime";
    public static final String API_KEY_EXP_CREATE_DATE = "CreateDate";
    public static final String API_KEY_EXP_TO_TIME = "ToTime";
    public static final String API_KEY_EXP_MEMO = "Memo";

    private TextView mAddExperienceView;
    private TextView mEditView;

    private DataListView mListView;

    private boolean mEditMode = false;

    private Handler mHandler = new Handler();


    public static void showExperienceHome(Activity activity, DataItemResult result) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(activity,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, ExperienceHomeFragment.class);
        extras.putParcelable("list.result",result);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setTitle(R.string.experience_home_title);
        setActionLeftDrawable(R.drawable.common_nav_arrow);

        mAddExperienceView = (TextView) findViewById(R.id.experience_action_add);
        mEditView = (TextView) findViewById(R.id.experience_home_edit);

        mAddExperienceView.setOnClickListener(this);
        mEditView.setOnClickListener(this);

        mListView = (DataListView) findViewById(R.id.experience_list);
        mListView.setOnItemClickListener(this);
        mListView.setDataCellClass(ExperienceCell.class,this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.transparent_00000000)));
        mListView.setDividerHeight(20);

//        WorkExpApi.fetchWorkExp();
        Bundle extras = getArguments();
        final DataItemResult listData = extras.getParcelable("list.result");
        mListView.replaceData(listData);
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
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.experience_action_add:
                addExperience(null);
                break;
            case R.id.experience_home_edit:
                toggleEditMode();
                break;
        }
    }

    private void addExperience(DataItemDetail detail) {
        Bundle extras = new Bundle();
        if (detail != null) {
            extras.putParcelable("detail",detail);
        }
        AddExperienceFragment.showAddExperience(this,extras);
    }

    private void toggleEditMode() {
        mEditMode = !mEditMode;
        int editText = R.string.action_edit;
        mListView.setOnItemClickListener(this);
        if (mEditMode) {
            editText = R.string.action_edit_finished;
            mListView.setOnItemClickListener(null);
        }
        mEditView.setText(editText);
        mEditView.setEnabled(false);
        mListView.invalidateViews();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditView.setEnabled(true);
            }
        },1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataItemDetail detail = mListView.getListData().getItem(position);
        addExperience(detail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK ){
            return;
        }

        if (requestCode == AddExperienceFragment.REQUEST_CODE_ADD_EXP) {
            mListView.refreshData();
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);

        if (WorkExpApi.ACTION_GET_WORK_EXP.equals(action)) {
            mListView.replaceData(result);
        } else if (WorkExpApi.ACTION_DELETE_WORK_EXP.equals(action)) {
            if (result.hasError || result.statusCode != 1) {
                Tips.showTips(R.string.delete_work_exp_failed);
                return;
            }
            mListView.refreshData();
        }
    }

    @LayoutID(R.layout.experience_home_list_cell)
    private class ExperienceCell extends ListCell{


        private TextView mCompany;
        private TextView mJob;
        private ImageView mDeleteView;

        private View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int workId = (int) v.getTag();
                Tips.showConfirm(getStringSafely(R.string.delete_work_exp_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == DialogInterface.BUTTON_POSITIVE) {
                            WorkExpApi.deleteWorkExp(workId);
                        }
                    }
                });
            }
        };

        @Override
        public void bindView() {
            mCompany = (TextView) findViewById(R.id.experience_home_item_company);
            mJob = (TextView) findViewById(R.id.experience_home_item_job);
            mDeleteView = (ImageView) findViewById(R.id.experience_home_item_delete);
            mDeleteView.setOnClickListener(deleteListener);
        }

        @Override
        public void bindData() {
            String companyValue = mDetail.getString(API_KEY_EXP_COMPANY);
            String jobValue = mDetail.getString(API_KEY_EXP_POSITION);
            mCompany.setText(getString(R.string.experience_format_company,companyValue));
            mJob.setText(getString(R.string.experience_format_job,jobValue));

            int deleteVisible = mEditMode ? View.VISIBLE:View.GONE;

            mDeleteView.setVisibility(deleteVisible);
            mDeleteView.setTag(mDetail.getInt(API_KEY_EXP_ID));
        }
    }
}
