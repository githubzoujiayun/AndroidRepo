package com.bs.clothesroom;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

public class HomePage2 extends GridFragment {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_page, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            sync();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void sync() {
		String userId = Preferences.getUsername(getActivity());
        if (TextUtils.isEmpty(userId)) {
            GeneralActivity.startLogin(getActivity());
            return;
        }
        mPostController.fetchVideoIds(userId);
	}

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
    	String userId = Preferences.getUsername(getActivity());
//      CursorLoader loader = ClothesInfo.getVideoCursorLoader(getActivity(), userId);
      CursorLoader loader = ClothesInfo.createVideoCursorLoader(getActivity(), userId);
      return loader;
    }

}
