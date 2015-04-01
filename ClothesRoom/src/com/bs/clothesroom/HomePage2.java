package com.bs.clothesroom;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

public class HomePage2 extends GridFragment {

	private Handler mHandler;
	private MediaObserver mObserver;
	private ContentResolver mResolver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mHandler = new Handler();
        mObserver = new MediaObserver(mHandler);
        mResolver = getActivity().getContentResolver();
        mResolver.registerContentObserver(ClothesInfo.CONTENT_URI, false,
                mObserver);
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
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, getArguments(), this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mResolver.unregisterContentObserver(mObserver);
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

    private class MediaObserver extends ContentObserver {

        public MediaObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().restartLoader(0, null, HomePage2.this);
        }
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
    	String userId = Preferences.getUsername(getActivity());
//      CursorLoader loader = ClothesInfo.getVideoCursorLoader(getActivity(), userId);
      CursorLoader loader = ClothesInfo.createVideoCursorLoader(getActivity(), userId);
      return loader;
    }

}
