package com.worksum.android;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.worksum.android.apis.DictsApi;

/**
 * @author chao.qin
 *         <p/>
 *         16/5/19
 */
public class DictFragment extends TitlebarFragment implements AdapterView.OnItemClickListener {

    public static final int POSITION_UNKOWN = -1;
    public static final int POSITION_AREA = 0;
    public static final int POSITION_FUNCTION = 1;
    public static final int POSITION_SEX = 2;
    public static final int POSITION_AGE = 3;

    private static final int[] TITLE_IDS = {R.string.dict_area,R.string.dict_function,R.string.dict_sex,R.string.dict_age};

    private static final String[] TYPE_SEX= {"男","女"};
    private static final String[] TYPE_AGE = new String[]{"18-25","25-40","40-65","65+"};

    private DataListView mListView;
    private int mPosition = POSITION_UNKOWN;

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mPosition = getArguments().getInt("position");

        setActionLeftDrawable(R.drawable.common_nav_arrow);
        setTitle(TITLE_IDS[mPosition]);

        mListView = (DataListView) findViewById(R.id.dict_list);
        mListView.setDataCellClass(DictCell.class,this);
        mListView.setDividerHeight(1);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
        mListView.setOnItemClickListener(this);


        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {

                DataItemResult result = new DataItemResult();
                switch (mPosition) {
                    case POSITION_AREA:
                        return DictsApi.getArea();
                    case POSITION_FUNCTION:
                        return  DictsApi.getFunctionType();
                    case POSITION_SEX:
                    case POSITION_AGE:
                        String[] values = mPosition == POSITION_SEX?TYPE_SEX:TYPE_AGE;
                        for (String value : values) {
                            DataItemDetail detail = new DataItemDetail();
                            detail.setStringValue("Cname",value);
                            result.addItem(detail);
                        }
                        return result;
                }
                throw new RuntimeException("Invalid position :" + mPosition);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.dict;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String dict = mListView.getItem(position).getString("Cname");
        String code = mListView.getItem(position).getString("CODE");
        Intent intent = new Intent();
        intent.putExtra("dict", dict);
        intent.putExtra("code", code);
        getActivity().setResult(RESULT_OK,intent);
        getActivity().finish();
    }


    private class DictCell extends DataListCell{

        private TextView mTitle;

        @Override
        public int getCellViewLayoutID() {
            return R.layout.dict_cell;
        }

        @Override
        public void bindView() {
            mTitle = (TextView)findViewById(R.id.dict_title);
        }

        @Override
        public void bindData() {
            mTitle.setText(mDetail.getString("Cname"));
        }
    }
}
