package com.bs.clothesroom;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.R.drawable;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


public class HomePage extends GeneralFragment implements LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener{
    
    private GridView mGridView;
    private View mRootView;
    private VideoAdapter mAdapter;
    private Handler mHandler;
    private VideoObserver mObserver;
    private ContentResolver mResolver;
    
    private ConcurrentHashMap<String, SoftReference<BitmapDrawable>> mThumbnailCache = new ConcurrentHashMap<String, SoftReference<BitmapDrawable>>(
            10);
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mHandler = new Handler();
        mObserver = new VideoObserver(mHandler);
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
	    mAdapter = new VideoAdapter(getActivity(), null, false);
	    mGridView.setAdapter(mAdapter);
	    mGridView.setOnItemClickListener(this);
	    
//	    View emptyView = inflater.inflate(R.layout.gridview_empty, null);
//	    View emptyView = mRootView.findViewById(R.id.refresh);
        Button b = (Button) mRootView.findViewById(R.id.refresh);
        b.setOnClickListener(this);
        mGridView.setEmptyView(b);
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
		mResolver.unregisterContentObserver(mObserver);
	}

	private class VideoAdapter extends CursorAdapter {

        private Drawable mDefaultDrawable = getResources().getDrawable(R.drawable.ic_launcher);

        public VideoAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            String videoPath = c.getString(c.getColumnIndex(ClothesInfo.COLUMN_NAME_DATA));
            String name = c.getString(c.getColumnIndex(ClothesInfo.COLUMN_NAME_MEDIA_NAME));
            String mimeType = c.getString(c.getColumnIndex(ClothesInfo.COLUMN_NAME_MIMETYPE));
            TextView tv = (TextView)v.findViewById(R.id.video_thumbnail);
//            videoPath = null;
            if (videoPath == null) {
                tv.setCompoundDrawablesWithIntrinsicBounds(null, mDefaultDrawable, null, null);
                tv.setText(name);
                return;
            }
            SoftReference<BitmapDrawable> ref = mThumbnailCache.get(videoPath);
            Drawable bd = null;
            
            if (ref == null || (bd = ref.get()) == null) {
                if (ClothesInfo.MIMETYPE_IMAGE.equals(mimeType)) {
//                    bd = Drawable.createFromPath(videoPath);
                    Options ops = new Options();
                    ops.inSampleSize = 4;
                    Bitmap bp = BitmapFactory.decodeFile(videoPath, ops);
                    bd = new BitmapDrawable(getResources(),bp);
                } else {
                    bd = new BitmapDrawable(getResources(),
                            createVideoThumbnail(videoPath));
                    mThumbnailCache.put(videoPath,new SoftReference<BitmapDrawable>((BitmapDrawable) bd));
                }
            }
            log("bd = "+bd);
//            tv.setCompoundDrawables(null, bd, null, null);
            tv.setCompoundDrawablesWithIntrinsicBounds(null, bd, null, null);
            tv.setText(name);
            tv.setTag(videoPath);
        }

        @Override
        public View newView(Context context, Cursor c, ViewGroup container) {
            View view = null;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.video_item, container, false);
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
//        CursorLoader loader = ClothesInfo.getVideoCursorLoader(getActivity(), userId);
        CursorLoader loader = ClothesInfo.createVideoCursorLoader(getActivity(), userId);
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
    
    private class VideoObserver extends ContentObserver {

        public VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().restartLoader(0, null, HomePage.this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
        View tv = view.findViewById(R.id.video_thumbnail);
        String path = (String) tv.getTag();
        log("path : "+path);
        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setComponent(new ComponentName("com.android.music", "com.android.music.VideoPlay"));
        i.setDataAndType(Uri.fromFile(new File(path)), "video/mp4");
        startActivity(i);
    }

    @Override
    public void onClick(View arg0) {
        sync();
    }
    
    private void sync() {
        String userId = Preferences.getUsername(getActivity());
        if (TextUtils.isEmpty(userId)) {
            GeneralActivity.startLogin(getActivity());
            return;
        }
        mPostController.fetchVideoIds(userId);
    }
}
