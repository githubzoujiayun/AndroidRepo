package com.bs.clothesroom;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;


public class HomePage extends GeneralFragment implements LoaderCallbacks<Cursor>{
    
    private GridView mGridView;
    private View mRootView;
    private VedioAdapter mAdapter;
    private Handler mHandler;
    private VedioObserver mObserver;
    private ContentResolver mResolver;
    
    private ConcurrentHashMap<String, SoftReference<BitmapDrawable>> mThumbnailCache = new ConcurrentHashMap<String, SoftReference<BitmapDrawable>>(
            10);
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mHandler = new Handler();
        mObserver = new VedioObserver(mHandler);
        mResolver = getActivity().getContentResolver();
        mResolver.registerContentObserver(ClothesInfo.CONTENT_URI, false,
                mObserver);
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
	    
	    mGridView = (GridView) mRootView.findViewById(android.R.id.list);
	    mAdapter = new VedioAdapter(getActivity(), null, false);
	    mGridView.setAdapter(mAdapter);
		return mRootView;
	}
    
	@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0,null,this);
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
            log("menu.refresh");
            mPostController.fetchImageIds(Preferences.getUsername(getActivity()));
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
		mResolver.unregisterContentObserver(mObserver);
	}

	private class VedioAdapter extends CursorAdapter {

        private Drawable mDefaultDrawable = getResources().getDrawable(R.drawable.ic_launcher);

        public VedioAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            String vedioPath = c.getString(c.getColumnIndex(ClothesInfo.COLUMN_NAME_DATA));
            String name = c.getString(c.getColumnIndex(ClothesInfo.COLUMN_NAME_MEDIA_NAME));
            TextView tv = (TextView)v.findViewById(R.id.vedio_thumbnail);
            vedioPath = null;
            if (vedioPath == null) {
                tv.setCompoundDrawablesWithIntrinsicBounds(null, mDefaultDrawable, null, null);
                tv.setText(name);
                return;
            }
            SoftReference<BitmapDrawable> ref = mThumbnailCache.get(vedioPath);
            BitmapDrawable bd = null;
            if (ref != null && ref.get() != null) {
                bd = ref.get();
            } else {
                bd = new BitmapDrawable(getResources(),
                        createVideoThumbnail(vedioPath));
                mThumbnailCache.put(vedioPath,new SoftReference<BitmapDrawable>(bd));
            }
            tv.setCompoundDrawables(null, bd, null, null);
            tv.setText(name);
        }

        @Override
        public View newView(Context context, Cursor c, ViewGroup container) {
            View view = null;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.vedio_item, container, false);
            return view;
        }
        
        private Bitmap createVideoThumbnail(String filePath) {
            Bitmap bitmap = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(filePath);
                bitmap = retriever.getFrameAtTime();
            } catch(IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    retriever.release();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }
            return bitmap;
        }	    
	}

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String userId = Preferences.getUsername(getActivity());
//        CursorLoader loader = ClothesInfo.getVedioCursorLoader(getActivity(), userId);
        CursorLoader loader = ClothesInfo.getImageCursorLoader(getActivity(), userId);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }
    
    private class VedioObserver extends ContentObserver {

        public VedioObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().restartLoader(0, null, HomePage.this);
        }
    }
}
