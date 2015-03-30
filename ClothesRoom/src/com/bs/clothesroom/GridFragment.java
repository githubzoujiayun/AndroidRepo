package com.bs.clothesroom;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class GridFragment extends GeneralFragment implements
        LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener {

    private Handler mHandler;
    private MediaObserver mObserver;
    private ContentResolver mResolver;
    private View mRootView;
    private GridView mGridView;
    private MediaAdapter mAdapter;

    private ConcurrentHashMap<String, SoftReference<BitmapDrawable>> mThumbnailCache = new ConcurrentHashMap<String, SoftReference<BitmapDrawable>>(
            10);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mObserver = new MediaObserver(mHandler);
        mResolver = getActivity().getContentResolver();
        mResolver.registerContentObserver(ClothesInfo.CONTENT_URI, false,
                mObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.grid_fragment, container,
                    false);
        }

        final ViewGroup v = (ViewGroup) mRootView.getParent();
        if (v != null) {
            v.removeView(mRootView);
        }

        mGridView = (GridView) mRootView.findViewById(R.id.list);
        mAdapter = new MediaAdapter(getActivity(), null, false);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        Button b = (Button) mRootView.findViewById(R.id.refresh);
        b.setOnClickListener(this);
        mGridView.setEmptyView(b);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MediaObserver extends ContentObserver {

        public MediaObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().restartLoader(0, null, GridFragment.this);
        }
    }

    private class MediaAdapter extends CursorAdapter {

        private Drawable mDefaultDrawable = getResources().getDrawable(
                R.drawable.ic_launcher);

        public MediaAdapter(Context context, Cursor c, boolean auto) {
            super(context, c, auto);
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            String mediaPath = c.getString(c
                    .getColumnIndex(ClothesInfo.COLUMN_NAME_DATA));
            String name = c.getString(c
                    .getColumnIndex(ClothesInfo.COLUMN_NAME_MEDIA_NAME));
            String mimeType = c.getString(c
                    .getColumnIndex(ClothesInfo.COLUMN_NAME_MIMETYPE));
            TextView tv = (TextView) v.findViewById(R.id.video_thumbnail);
            // videoPath = null;
            if (mediaPath == null) {
                tv.setCompoundDrawablesWithIntrinsicBounds(null,
                        mDefaultDrawable, null, null);
                tv.setText(name);
                int downloadFlag = c.getInt(c
                        .getColumnIndex(ClothesInfo.COLUMN_NAME_DOWNLOAD_FLAG));
                if (downloadFlag != ClothesInfo.FLAG_DOWNLOAD_START) {
                    String userId = c.getString(c
                            .getColumnIndex(ClothesInfo.COLUMN_NAME_USERID));
                    int serverId = c
                            .getInt(c
                                    .getColumnIndex(ClothesInfo.COLUMN_NAME_SYN_SERVER_ID));
                    if (ClothesInfo.MIMETYPE_IMAGE.equals(mimeType)) {
                        mPostController.downloadImage(userId, serverId);
                    } else {
                        mPostController.downloadVideo(userId, serverId);
                    }
                }
                return;
            }
            SoftReference<BitmapDrawable> ref = mThumbnailCache.get(mediaPath);
            Drawable bd = null;

            if (ref == null || (bd = ref.get()) == null) {
                if (ClothesInfo.MIMETYPE_IMAGE.equals(mimeType)) {
                    // bd = Drawable.createFromPath(videoPath);
                    Options ops = new Options();
                    ops.inSampleSize = 4;
                    Bitmap bp = BitmapFactory.decodeFile(mediaPath, ops);
                    bd = new BitmapDrawable(getResources(), bp);
                } else {
                    bd = new BitmapDrawable(getResources(),
                            createVideoThumbnail(mediaPath));
                    mThumbnailCache.put(mediaPath,
                            new SoftReference<BitmapDrawable>(
                                    (BitmapDrawable) bd));
                }
            }
            log("bd = " + bd);
            // tv.setCompoundDrawables(null, bd, null, null);
            tv.setCompoundDrawablesWithIntrinsicBounds(null, bd, null, null);
            tv.setText(name);
            tv.setTag(mediaPath);
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
            } catch (IllegalArgumentException ex) {
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
    public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
        // return ClothesInfo.getImageCursorLoader(context, userId);
        return ClothesInfo.createMediaCursorLoader(getActivity(),
                Preferences.getUsername(getActivity()));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        mAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh) {
            sync();
        }
    }

    private void sync() {
        String userId = Preferences.getUsername(getActivity());
        mPostController.fetchImageIds(userId);
//        mPostController.fetchVideoIds(userId);
    }
}
