package com.worksum.android.controller;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.task.BasicTask;

import java.util.HashMap;
import java.util.UUID;

/**
 * chao.qin
 */
public class DataController {

    private static DataController mDataController;

    private HashMap<String,DataAdapter> mAdapterMap = new HashMap<String,DataAdapter>();

    private DataController() {
    }

    public static DataController getInstance(){
        if (mDataController == null) {
            mDataController = new DataController();
        }
        return mDataController;
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }

    public DataAdapter newDataAdapter() {
        String key = generateKey();
        DataAdapter adapter = new DataAdapter(key);
        mAdapterMap.put(key,adapter);
        return adapter;
    }

    public DataAdapter getAdapter(String key){
        return mAdapterMap.get(key);
    }

    public class DataAdapter extends BasicTask{

        private static final String DB_TYPE_ITEMS_CACHE = "db.type.items.cache";
        private static final String DB_TYPE_ITEMS_TIMESTAMP = "db.type.items.duration";

        DataOptions mDataOptions;

        public class DataOptions {
            public int mItemsCacheDuration;
            public String mItemsCacheKey;
            public String mItemsCacheTimestampKey;
            public DataLoadListener mDataListener;

            public DataOptions() {
                mItemsCacheDuration = 0;
                mItemsCacheKey = null;
                mItemsCacheTimestampKey = null;
                mDataListener = null;
            }
        }

        private String mKey;
        private DataLoadListener mDataListener;

        public void setShowTips(boolean show) {
            showTipsDialog(show);
        }

        private DataAdapter(String key) {
            mKey = key;
            mDataOptions = new DataOptions();
        }

        public String getKey() {
            return mKey;
        }

        public void setDataListener(DataLoadListener listener) {
            mDataListener = listener;
        }

        private void checkListener(){
            if (mDataListener == null) {
                mDataListener = mDataOptions.mDataListener;
            }
            if (mDataListener == null) {
                throw new IllegalStateException("mDataListener must not be null.");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkListener();
            mDataListener.onBeforeLoad();
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            checkListener();
            int oldTime = 0;
            int newTime = (int) System.currentTimeMillis();
            if (mDataOptions.mItemsCacheTimestampKey != null) {
                oldTime = AppCoreInfo.getCacheDB().getIntValue(DB_TYPE_ITEMS_TIMESTAMP, mDataOptions.mItemsCacheTimestampKey, newTime);
            }
            if (mDataOptions.mItemsCacheKey != null && (newTime - oldTime) <= mDataOptions.mItemsCacheDuration) {
                return AppCoreInfo.getCacheDB().getItemsCache(DB_TYPE_ITEMS_CACHE, mDataOptions.mItemsCacheKey);
            }
            return mDataListener.onLoadData();
        }

        @Override
        protected void onTaskFinished(DataItemResult result) {
            checkListener();
            if (result.hasError) {
                mDataListener.onFailed(result, NetworkManager.networkIsConnected());
            }else {
                mDataListener.onSucceed(result);
            }

            if (result.getDataCount() < 1) {
                mDataListener.onEmpty(result);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            checkListener();
            mDataListener.onCancelled();
        }
    }

    public interface DataLoadListener {
        void onSucceed(DataItemResult result);
        void onFailed(DataItemResult result, boolean isNetworkConnected);
        void onEmpty(DataItemResult result);
        void onBeforeLoad();
        DataItemResult onLoadData();
        void onCancelled();
    }
}
