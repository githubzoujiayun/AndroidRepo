package com.bs.clothesroom;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;

public class VituralSearchResult extends GridFragment {

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
		String videoIds = getArguments().getString("videoIds");
		return ClothesInfo.createVideoResultCursorLoader(getActivity(), Preferences.getUsername(getActivity()), videoIds);
	}

	@Override
	public void sync() {
	}

}
