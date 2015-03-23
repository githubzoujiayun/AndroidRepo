package com.bs.clothesroom;

import com.bs.clothesroom.provider.ClothesInfo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class HomePage extends GeneralFragment implements LoaderCallbacks<Cursor>{
    
    private GridView mGridView;
    private View mRootView;
    private VedioAdapter mAdapter;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	    
	    if (mRootView == null) {
	        mRootView = inflater.inflate(R.layout.home_page, container,false);
	    }
		
	    final ViewGroup v = (ViewGroup) mRootView.getParent();
	    if (v != null) {
	        v.removeView(mRootView);
	    }
	    
	    mGridView = (GridView) v.findViewById(android.R.id.list);
	    mAdapter = new VedioAdapter(getActivity(), null, false);
	    mGridView.setAdapter(mAdapter);
	    getLoaderManager().initLoader(0,null,this);
	    
		return mRootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class VedioAdapter extends CursorAdapter {

        public VedioAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            
        }

        @Override
        public View newView(Context context, Cursor c, ViewGroup container) {
            return null;
        }
	    
	}

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        
        CursorLoader loader = ClothesInfo.getCursorLoader();
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }
}
