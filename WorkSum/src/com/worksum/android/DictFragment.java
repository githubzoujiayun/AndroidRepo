package com.worksum.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCellSelector;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.DictsApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @author chao.qin
 *         <p/>
 *         16/5/19
 */
@LayoutID(R.layout.dict)
public class DictFragment extends TitlebarFragment implements AdapterView.OnItemClickListener {

    public static final int POSITION_UNKOWN = -1;
    public static final int POSITION_AREA = 0;
    public static final int POSITION_FUNCTION = 1;
    public static final int POSITION_SEX = 2;
    public static final int POSITION_AGE = 3;
    public static final int POSITION_WORKDAY = 4;
    public static final int POSITION_DEGREE = 5;
    public static final int POSITION_SALARY = 6;
    public static final int POSITION_WORK_TYPE = 7; //兼职、全职

    private static final int[] TITLE_IDS = {R.string.dict_area,R.string.dict_function,R.string.dict_sex,R.string.dict_age,R.string.resume_edit_workday,R.string.resume_edit_degree,R.string.dict_title_salary,R.string.dict_title_work_type};

    private static String[] SEX_TYPE;
    private static String[] WORK_TYPE;
    private static String[] AGE_TYPE;
    private static String[] SALARY_TYPE;

    public static final int REQUEST_CODE_DICT_MASK = 0x1000;

    private DataListView mListView;
    private int mPosition = POSITION_UNKOWN;

    public DictFragment() {
    }

    private void loadLocalDicts() throws IOException, XmlPullParserException {
        XmlResourceParser parser = getResources().getXml(R.xml.dict_config);
        parser.next();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    if ("dicts".equalsIgnoreCase(name)) {

                    } else if ("dict".equalsIgnoreCase(name)) {

                    }
                    break;
                case XmlPullParser.END_TAG:

                    break;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        SEX_TYPE = getResources().getStringArray(R.array.array_dict_sex);
        AGE_TYPE = getResources().getStringArray(R.array.array_dict_ages);
        WORK_TYPE = getResources().getStringArray(R.array.array_dict_work_type);
        SALARY_TYPE = getResources().getStringArray(R.array.array_dict_salary);

    }

    public static void showDict(Fragment fragment, int position) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(fragment.getActivity(),FragmentContainer.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, DictFragment.class);
        extras.putInt("position", position);
        intent.putExtras(extras);
        fragment.startActivityForResult(intent, position | REQUEST_CODE_DICT_MASK);
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        mPosition = getArguments().getInt("position");

        setActionLeftDrawable(R.drawable.common_nav_arrow);
        setTitle(TITLE_IDS[mPosition]);

        mListView = (DataListView) findViewById(R.id.dict_list);
        mListView.setDataCellSelector(new DataListCellSelector() {
            @Override
            protected Class<?>[] getCellClasses() {
                return new Class<?>[]{DictCell.class,DictType.class};
            }

            @Override
            public Class<?> getCellClass(DataListAdapter adapter, int position) {
                DataItemResult result = adapter.getListData();
                DataItemDetail detail = result.getItem(position);
                String code = detail.getString("CODE");
                if (code != null && code.endsWith("00") ) {
                    return DictType.class;
                }
                return DictCell.class;
            }
        },this);
        mListView.setOnItemClickListener(this);


        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {

                DataItemResult result = new DataItemResult();
                String[] values = null;
                switch (mPosition) {
                    case POSITION_AREA:
                        return DictsApi.getArea();
                    case POSITION_FUNCTION:
                        return  DictsApi.getFunctionType();
                    case POSITION_SEX:
                        values = SEX_TYPE;
                    case POSITION_AGE:
                        if (values == null) {
                            values = AGE_TYPE;
                        }
                    case POSITION_WORK_TYPE:
                        if (values == null) {
                            values = WORK_TYPE;
                        }
                    case POSITION_SALARY:
                        if (values == null) {
                            values = SALARY_TYPE;
                        }
                        for (String value : values) {
                            DataItemDetail detail = new DataItemDetail();
                            detail.setStringValue("Cname",value);
                            result.addItem(detail);
                        }
                        return result;

                    case POSITION_DEGREE:
                        result = DictsApi.getDegree();
                        return result;


                }
                throw new RuntimeException("Invalid position :" + mPosition);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String dict = mListView.getItem(position).getString("Cname");
        String code = mListView.getItem(position).getString("CODE");

        if (code.endsWith("00")) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("dict", dict);
        intent.putExtra("code", code);
        getActivity().setResult(RESULT_OK,intent);
        getActivity().finish();
    }


    @LayoutID(R.layout.dict_cell)
    private class DictCell extends ListCell{

        protected TextView mTitle;

        @Override
        public void bindView() {
            mTitle = (TextView)findViewById(R.id.dict_title);
        }

        @Override
        public void bindData() {
            mTitle.setText(mDetail.getString("Cname"));
        }
    }

    @LayoutID(R.layout.dict_type)
    private class DictType extends DictCell{
        @Override
        public void bindView() {
            super.bindView();
        }

    }
}
