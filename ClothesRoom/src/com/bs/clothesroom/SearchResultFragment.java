package com.bs.clothesroom;

import java.io.File;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

public class SearchResultFragment extends GridFragment implements OnItemClickListener {

	private Handler mHandler;
	private MediaObserver mObserver;
	private ContentResolver mResolver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		mObserver = new MediaObserver(mHandler);
		mResolver = getActivity().getContentResolver();
		mResolver.registerContentObserver(ClothesInfo.CONTENT_URI, false,
				mObserver);
	}

	private class MediaObserver extends ContentObserver {

		public MediaObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getLoaderManager().restartLoader(0, null, SearchResultFragment.this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v =  super.onCreateView(inflater, container, savedInstanceState);
		mGridView.setOnItemClickListener(this);
		return v;
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
		// return ClothesInfo.getImageCursorLoader(context, userId);
		Bundle args = getArguments();
		log("bundle : " + args);
		if (args == null)
			return null;
		String season = args.getString("season");
		String style = args.getString("style");
		String type = args.getString("type");
		// String userId = args.getString("userId");
		String userId = Preferences.getUsername(getActivity());
		return ClothesInfo.createMediaCursorLoader(getActivity(), userId,
				season, style, type);
	}

	@Override
	public void sync() {
		String userId = Preferences.getUsername(getActivity());
		mPostController.fetchImageIds(userId);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		 String path = (String) v.getTag();
	        Intent i = new Intent(Intent.ACTION_VIEW);
	        File f = new File(path);
	        if (!f.exists()) return;
	        i.setDataAndType(Uri.fromFile(f), "image/*");
	        startActivity(i);		
	}

}
