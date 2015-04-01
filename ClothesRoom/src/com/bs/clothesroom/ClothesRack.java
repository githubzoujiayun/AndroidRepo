package com.bs.clothesroom;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;

public class ClothesRack extends GridFragment {
    
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
        Bundle args = getArguments();
        String type = args.getString("type");
        String userId = Preferences.getUsername(getActivity());
        return ClothesInfo.createTypeCursorLoader(getActivity(), userId, type);
    }

    @Override
    public void sync() {

    }

}
