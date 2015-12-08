package com.jobs.lib_v1.list;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.jobs.lib_v1.settings.LocalStrings;

/**
 * 默认的出错单元格
 * 如果需要写子类，请直接继承 DataListCell
 * 当前类不希望被其他子类继承
 * 
 * @author solomon.wen
 * @date 2013-12-18
 */
public final class DataListErrorCell extends DataListDataCell {
	@Override
	public final View createCellView() {
		View tmpView = super.createCellView();
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(ColorStateList.valueOf(Color.parseColor("#777777")));
		return tmpView;
	}

	@Override
	public final void bindData() {
		String message = mAdapter.getListData().message.trim();
		if (message.length() < 1) {
			message = LocalStrings.common_error_load_data_retry;
		}

		mTextView.setText(message);
		if (mAdapter.getListView().getEnableAutoHeight()) {
			mTextView.setMaxWidth(mAdapter.getListView().getWidth());
		}
	}
}
