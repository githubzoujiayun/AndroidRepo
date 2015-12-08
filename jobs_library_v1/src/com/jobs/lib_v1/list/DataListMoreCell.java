package com.jobs.lib_v1.list;

import com.jobs.lib_v1.settings.LocalStrings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

/**
 * 默认的下一页单元格
 * 如果需要写子类，请直接继承 DataListCell
 * 当前类不希望被其他子类继承
 * 
 * @author solomon.wen
 * @date 2013-12-18
 */
public final class DataListMoreCell extends DataListDataCell {
	@Override
	public final View createCellView() {
		View tmpView = super.createCellView();
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
		return tmpView;
	}

	@Override
	public final void bindData() {
		mTextView.setText(LocalStrings.common_text_show_next_page);

		if (mAdapter.getListView().getEnableAutoHeight()) {
			mTextView.setMaxWidth(mAdapter.getListView().getWidth());
		}
	}
}
