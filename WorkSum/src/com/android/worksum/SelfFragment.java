package com.android.worksum;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;

import org.w3c.dom.Text;

public class SelfFragment extends TitlebarFragment implements AdapterView.OnItemClickListener {

    private static final int ID_MY_RESUME = 1;
    private static final int ID_MY_VIDEO = 2;
    private static final int ID_SYSTEM_SETTINGS = 3;

    private DataListView mListView;

	@Override
	public int getLayoutId() {
		return R.layout.self;
	}
	
	@Override
	void setupView(View v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		
		setTitle(R.string.title_self);

		mListView = (DataListView) findViewById(R.id.items_list);
        mListView.setDataCellClass(ItemCell.class,this);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(this);
        mListView.setAllowAutoTurnPage(false);

        mListView.appendData(buildItems());
    }

    private DataItemResult buildItems() {
        DataItemResult datas = new DataItemResult();

        DataItemDetail detail = new DataItemDetail();
        detail.setIntValue("id",ID_MY_RESUME);
        detail.setIntValue("titleId",R.string.self_my_resume);
        detail.setIntValue("descriptionId",R.string.self_resume_description);
        detail.setIntValue("iconId", R.drawable.me_resume);
        datas.addItem(detail);

        detail = new DataItemDetail();
        detail.setIntValue("id",ID_MY_VIDEO);
        detail.setIntValue("titleId",R.string.self_my_video);
        detail.setIntValue("descriptionId",R.string.self_video_description);
        detail.setIntValue("iconId", R.drawable.me_video);
        datas.addItem(detail);

        detail = new DataItemDetail();
        detail.setIntValue("id",ID_SYSTEM_SETTINGS);
        detail.setIntValue("titleId",R.string.self_my_settings);
        detail.setIntValue("descriptionId",R.string.self_settings_description);
        detail.setIntValue("iconId", R.drawable.me_settings);
        datas.addItem(detail);
        return datas;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DataListAdapter adapter = (DataListAdapter) adapterView.getAdapter();

        DataItemResult result = adapter.getListData();
        DataItemDetail detail = result.getItem(i);
        switch (detail.getInt("id")) {
            case ID_MY_RESUME:
                FragmentContainer.showMyResume(getActivity());
                break;
            case ID_MY_VIDEO:
                break;
            case ID_SYSTEM_SETTINGS:
                break;
        }
    }


    private class ItemCell extends DataListCell{

        private TextView mTitle;
        private TextView mDescription;
        private ImageView mIcon;
        /**
         * 获取单元格对应的 layoutID
         * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
         * getCellViewLayoutID 方法返回0时会调用 createCellView
         */
        @Override
        public int getCellViewLayoutID() {
            return R.layout.self_item_cell;
        }

        /**
         * 绑定单元格视图中的控件到变量
         * 该方法由子类实现
         */
        @Override
        public void bindView() {
            mTitle = (TextView) findViewById(R.id.self_item_title);
            mDescription = (TextView) findViewById(R.id.self_item_description);
            mIcon = (ImageView) findViewById(R.id.item_icon);
        }

        /**
         * 绑定单元格数据到控件
         * 该方法由子类实现
         */
        @Override
        public void bindData() {
            mTitle.setText(mDetail.getInt("titleId"));
            mDescription.setText(mDetail.getInt("descriptionId"));
            mIcon.setImageResource(mDetail.getInt("iconId"));
        }
    }
}
