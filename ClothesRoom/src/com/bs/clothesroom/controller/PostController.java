package com.bs.clothesroom.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bs.clothesroom.provider.ClothesInfo;
import com.bs.clothesroom.provider.UserInfo;
import com.bs.clothesroom.provider.ClothesInfo.ImageInfo;

public class PostController {

    /**
     * post type
     * 
     * @see #POST_TYPE_LOGIN
     * @see #POST_TYPE_REGISTER
     * @see #POST_TYPE_FETCH_USERINFO
     */
    public static final String POST_ARGS_TYPE = "post_type";
    public static final String POST_ARGS_JSON = "requestJson";
    public static final String POST_ARGS_IMAGE = "image";
    public static final String ARGS_IMAGE_ID = "imageid";
    public static final String ARGS_VIDEO_ID = "videoid";
    public static final String ARGS_USERNAME = "username";
    public static final String ARGS_PASSWORD = "password";
    public static final String ARGS_SEX = "sex";
    public static final String ARGS_AGE = "age";
    public static final String ARGS_PHONE = "phone";
    public static final String ARGS_EMAIL = "email";
    public static final String ARGS_WORK = "job";
    private static final String ARGS_FILE_NAME = "imagename";

    private static final String POST_TYPE_LOGIN = "login";
    private static final String POST_TYPE_REGISTER = "register";
    private static final String POST_TYPE_FETCH_USERINFO = "fetch_userinfo";
    private static final String POST_TYPE_FETCH_VIDEO_IDS = "fetch_video_ids";
    private static final String POST_TYPE_FETCH_IMAGE_IDS = "fetch_image_ids";
    private static final String POST_TYPE_FETCH_IMAGE_INFO = "fetch_image_info";
    private static final String POST_TYPE_FETCH_VIDEO_INFO = "fetch_video_info";
    private static final String POST_TYPE_DOWNLOAD_IMAGE = "download_image";
    private static final String POST_TYPE_DOWNLOAD_VIDEO = "download_video";
    private static final String POST_TYPE_UPLOAD_IMAGE = "upload_image";
    private static final String POST_TYPE_DELETE_IMAGE = "delete_image";
    private static final String POST_TYPE_DELETE_VIDEO = "delete_video";

    
    public static final int POST_ID_UNKNOWN = 0;
    public static final int POST_ID_STRING_MASK = 0x01000;
    public static final int POST_ID_LOGIN = POST_ID_STRING_MASK + (1 << 0);
    public static final int POST_ID_REGISTER = POST_ID_STRING_MASK +(1 << 1);
    public static final int POST_ID_FETCH_USERINFO = POST_ID_STRING_MASK + (1 << 2);
    public static final int POST_ID_UPLOAD_FILE = POST_ID_STRING_MASK + (1 << 3);
    public static final int POST_ID_FETCH_FETCH_VIDEO_IDS = POST_ID_STRING_MASK + (1 << 5);
    public static final int POST_ID_FETCH_FETCH_IMAGE_IDS = POST_ID_STRING_MASK + (1 << 6);
    public static final int POST_ID_FETCH_FETCH_IMAGE_INFO = POST_ID_STRING_MASK + (1 << 7);
    public static final int POST_ID_FETCH_FETCH_VIDEO_INFO = POST_ID_STRING_MASK + (1 << 8);
    public static final int POST_ID_DELETE_IMAGE = POST_ID_STRING_MASK + (1 << 9);
    public static final int POST_ID_DELETE_VIDEO = POST_ID_STRING_MASK + (1 << 10);
    
    public static final int POST_ID_BINARY_MASK = 0x10000;
    public static final int POST_ID_DOWNLOAD_IMAGE = POST_ID_BINARY_MASK + (1 << 1);
    public static final int POST_ID_DOWNLOAD_VIDEO = POST_ID_BINARY_MASK + (1 << 2);

    
    private Context mContext;

    ArrayList<IPostCallback> mCallbacks = new ArrayList<IPostCallback>();

    DeliverCallback mDelivery;
    private PostTask mPostTask;
    File mRoomDir = null;
    

    public PostController(Context _context) {
        mContext = _context;
        mDelivery = new DeliverCallback();
        mRoomDir = new File(Environment.getExternalStorageDirectory() + "/" + "ClothesRoom/media");
        if (!mRoomDir.exists()){
            mRoomDir.mkdirs();// 新建文件夹
        }
    }

    private boolean checkJsonFormat(String json) {
        try {
            JSONObject js = new JSONObject(json);
            return js.has(ARGS_USERNAME);
        } catch (JSONException e) {
            throw new PostException("json not well format.");
        }
    }
    
    public void fetchUserInfo(String primaryKey) {
        if (TextUtils.isEmpty(primaryKey)) {
            throw new RuntimeException("primary is not allow valued null.");
        }
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_FETCH_USERINFO);
            json.put(ARGS_USERNAME, primaryKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_FETCH_USERINFO);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }

    public void login(String username, String psw) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_LOGIN);
            json.put(ARGS_USERNAME, username);
            json.put(ARGS_PASSWORD, psw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_LOGIN);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void fetchVideoIds(String userId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_FETCH_VIDEO_IDS);
            json.put(ARGS_USERNAME, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_FETCH_FETCH_VIDEO_IDS);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void fetchImageIds(String userId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_FETCH_IMAGE_IDS);
            json.put(ARGS_USERNAME, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_FETCH_FETCH_IMAGE_IDS);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void deleteImage(String userId,int imageId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_DELETE_IMAGE);
            json.put(ARGS_USERNAME, userId);
            json.put(ARGS_IMAGE_ID, imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_DELETE_IMAGE);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void fetchImageInfo(String userId,int imageId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_FETCH_IMAGE_INFO);
            json.put(ARGS_USERNAME, userId);
            json.put(ARGS_IMAGE_ID, imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_FETCH_FETCH_IMAGE_INFO);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void fetchVideoInfo(String userId,int videoId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_FETCH_VIDEO_INFO);
            json.put(ARGS_USERNAME, userId);
            json.put(ARGS_VIDEO_ID, videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_FETCH_FETCH_VIDEO_INFO);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void uploadFile(File file, ClothesInfo info) {
        JSONObject json = null;
        try {
            json = info.toJson();
            json.put(POST_ARGS_TYPE, POST_TYPE_UPLOAD_IMAGE);
            json.put(ARGS_USERNAME, Preferences.getUsername(mContext));
            json.put(ARGS_FILE_NAME, file.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_UPLOAD_FILE,file,json.toString());
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    
    public void downloadImage(String userId,int imageId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_DOWNLOAD_IMAGE);
            json.put(ARGS_USERNAME, userId);
            json.put(ARGS_IMAGE_ID, imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_DOWNLOAD_IMAGE,imageId);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }
    
    public void downloadVideo(String userId,int videoId) {
        JSONObject json = new JSONObject();
        try {
            json.put(POST_ARGS_TYPE, POST_TYPE_DOWNLOAD_VIDEO);
            json.put(ARGS_USERNAME, userId);
            json.put(ARGS_VIDEO_ID, videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_DOWNLOAD_VIDEO,videoId);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }

    private boolean checkNetworkAvaliable() {
        if (mContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public void register(UserInfo info) {
        JSONObject params = null;
        try {
            params = info.toJson();
            params.put(POST_ARGS_TYPE, POST_TYPE_REGISTER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_REGISTER);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params.toString());
    }
    
    public boolean isBinaryId(int postId) {
        return (postId & POST_ID_BINARY_MASK) != 0;
    }
    
    class PostTask extends AsyncTask<String, Integer, PostResult> {

        private int mPostId = POST_ID_UNKNOWN;
        private String mJson;
        private File mTargetFile;
        private String mUrl = SERVER_GENERAL_URL;
        private int mServerMediaId;

        public static final String SERVER_GENERAL_URL = "http://123.57.15.28/VirtualCloset/manager";
        public static final String SERVER_UPLOAD_URL = "http://123.57.15.28/VirtualCloset/uploadfile";

        public PostTask(int postType) {
            mPostId = postType;
        }

        public PostTask() {
        }

        public PostTask(int postId, File file, String json) {
            mPostId = postId;
            mTargetFile = file;
            mJson = json;
        }

        public PostTask(int postId, int serverId) {
            this(postId);
            mServerMediaId = serverId;
        }

        public void setPostType(int id) {
            mPostId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mPostId == POST_ID_UPLOAD_FILE) {
                mUrl = SERVER_UPLOAD_URL;
            }
        }

        @Override
        protected PostResult doInBackground(String... params) {
            if (mPostId == POST_ID_UPLOAD_FILE) {
                assert mTargetFile != null;
                return doMuiltyPost(mTargetFile, mJson);
            }
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(PostResult result) {
            boolean succeed = result.errId == PostResult.SUCCED;
            log("onPostExecute");
            if (succeed) {
                mDelivery.onPostSucceed(result);
            } else {
                mDelivery.onPostFailed(result);
            }
            super.onPostExecute(result);
        }

        private PostResult doMuiltyPost(File f, String json) {
            MultipartEntity entity = new MultipartEntity();
            if (f.exists()) {
                FileBody fbody = new FileBody(f, "image/*");
                entity.addPart(POST_ARGS_IMAGE, fbody);
            }
            try {
                StringBody stringBody = new StringBody(json);
                entity.addPart(POST_ARGS_JSON, stringBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            log("post >> " + json);
            return doPostInternal(entity);
        }

        private PostResult doPost(String json) {
            HttpEntity entity = null;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(POST_ARGS_JSON, json));
                entity = new UrlEncodedFormEntity(params);
                log("post >> " + json);
            } catch (UnsupportedEncodingException e1) {
                throw new PostException("json parse error.");
            }
            return doPostInternal(entity);
        }

        private PostResult doPostInternal(HttpEntity entity) {
        	PostResult result = new PostResult();
        	result.postId = mPostId;
        	
        	String userId = Preferences.getUsername(mContext);
        	if (TextUtils.isEmpty(userId)) {
        		result.errId = PostResult.ERR_MESSAGE_ILLEGAL_USERNAME;
        		return result;
        	}

            result.errId = PostResult.SUCCED;
            if (!checkNetworkAvaliable()) {
                result.errId = PostResult.ERR_NETWORK_NOT_AVIABLE;
                return result;
            }
            HttpPost httpRequest = new HttpPost(mUrl);
            httpRequest.setEntity(entity);
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 100000);
            HttpResponse httpResponse = null;
            
            try {
                mDelivery.onPostStart(mPostId, null);
                httpResponse = httpclient.execute(httpRequest);
                log("post succeed!");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (httpResponse == null) {
                result.errId = PostResult.ERR_NETWORK_EXCEPTION;
                result.info = "httpRespone is null.";
                return result;
            }
            StatusLine line = httpResponse.getStatusLine();
            if (line == null) {
                result.errId = PostResult.ERR_NETWORK_EXCEPTION;
                result.info = "status line is null";
                return result;
            }
            if (line.getStatusCode() == 200) {
                try {
                    parseEntity(httpResponse.getEntity(), result);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                int code = line.getStatusCode();
                try {
//                    String error = EntityUtils.toString(entity);
                    result.errId = code;
                    log("code = " + code);
//                    log("error = " + error);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        private void parseEntity(HttpEntity entity, PostResult result)
                throws ParseException, IOException, JSONException {
            if (!isBinaryId(mPostId)) {
                /*
                 * POST_ID_STRING_MASK
                 */
                String s = EntityUtils.toString(entity);
                JSONTokener parser = new JSONTokener(s);
                JSONObject json = (JSONObject) parser.nextValue();
                result.json = json;
                if (json.has(ARGS_USERNAME)) {
                    result.primaryKey = json.getString(ARGS_USERNAME);
                }
                log("get << " + json);
                try{
                    result.errId = json.getInt("message");
                }catch (JSONException je) {
                    result.errId = PostResult.SUCCED;
                }
            } else {
                /*
                 * POST_ID_BINARY_MASK
                 */
                ContentResolver resolver = mContext.getContentResolver();
                ClothesInfo info = null;
                switch (mPostId) {
                case POST_ID_DOWNLOAD_IMAGE:
                    info = ClothesInfo.getImageInfoBySID(resolver,
                            mServerMediaId, Preferences.getUsername(mContext));
                    break;
                case POST_ID_DOWNLOAD_VIDEO:
                    info = ClothesInfo.getVideoInfoBySID(resolver,
                            mServerMediaId, Preferences.getUsername(mContext));
                    break;

                default:
                    throw new IllegalArgumentException("unkown post id : " + Integer.toHexString(mPostId));
                }
                InputStream input = entity.getContent();
                // File
                copyFile(input,info);
            }
        }

        private void copyFile(InputStream input,ClothesInfo localInfo) {
            ContentResolver resolver = mContext.getContentResolver();
            log("localInfo : "+localInfo);
            String fileName = localInfo.mMediaName;
            String pathName = mRoomDir.getPath() + "/" + fileName;
            File file = new File(pathName);
            FileOutputStream output = null;
            try {
                if (file.exists()) {
                    file.delete();
                    System.out.println("exits");
                }
                {
                    output = new FileOutputStream(file);
                    // 读取大文件
                    byte[] buffer = new byte[4 * 1024];
                    int len = 0;
                    while ((len = input.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                    output.flush();
                    
                    //write database
                    ContentValues update = new ContentValues();
                    log("cp file pathName : "+pathName);
                    update.put(ClothesInfo.COLUMN_NAME_DATA,pathName);
                    update.put(ClothesInfo.COLUMN_NAME_DOWNLOAD_FLAG, ClothesInfo.FLAG_DOWNLOAD_DONE);
                    Uri uri = ContentUris.withAppendedId(ClothesInfo.CONTENT_URI, localInfo.mId);
                    resolver.update(uri, update, null, null);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                ContentValues update = new ContentValues();
                update.put(ClothesInfo.COLUMN_NAME_DOWNLOAD_FLAG, ClothesInfo.FLAG_DOWNLOAD_FAILED);
                Uri uri = ContentUris.withAppendedId(ClothesInfo.CONTENT_URI, localInfo.mId);
                resolver.update(uri, update, null, null);
            } catch (IOException e) {
                e.printStackTrace();
                ContentValues update = new ContentValues();
                update.put(ClothesInfo.COLUMN_NAME_DOWNLOAD_FLAG, ClothesInfo.FLAG_DOWNLOAD_FAILED);
                Uri uri = ContentUris.withAppendedId(ClothesInfo.CONTENT_URI, localInfo.mId);
                resolver.update(uri, update, null, null);
            } finally {
                try {
                    output.close();
                    System.out.println("success");
                } catch (IOException e) {
                    System.out.println("fail");
                    e.printStackTrace();
                }
            }
        }
    }

    class PostException extends RuntimeException {
        
        public PostException(){}
        
        public PostException(String message) {
            super(message);
        }

        /**
         * 
         */
        private static final long serialVersionUID = 2125539439742249516L;
    }
    
    private class DeliverCallback implements IPostCallback{

        @Override
        public void onPostSucceed(PostResult result) {
            for (IPostCallback callback : mCallbacks) {
                callback.onPostSucceed(result);
            }
        }

        @Override
        public void onPostFailed(PostResult result) {
            for (IPostCallback callback : mCallbacks) {
                callback.onPostFailed(result);
            }
        }

        @Override
        public void onPostInfo(int postId, int infoId,String infoMessage) {
            for (IPostCallback callback : mCallbacks) {
                callback.onPostInfo(postId,infoId,infoMessage);
            }
        }

        @Override
        public void onPostStart(int post, String message) {
            for (IPostCallback callback : mCallbacks) {
                callback.onPostStart(post,message);
            }
        }

    }

    public void addCallback(IPostCallback callback) {
        mCallbacks.add(callback);
    }

    public void removeCallback(IPostCallback callback) {
        mCallbacks.remove(callback);
    }

    public interface IPostCallback {
        
        public void onPostStart(int post,String message);
        
        public void onPostSucceed(PostResult result);

        public void onPostFailed(PostResult result);
        
        public void onPostInfo(int post, int infoId,String info);
    }
    
    public class PostResult {
        public int postId;
        public int resultId;
        public JSONObject json;
        public int errId;
        public String info;
        public String primaryKey;
        public Object obj;
        
        public static final int SUCCED = 0;
        public static final int ERR_INVALIDE_USERNAME = 1;
        public static final int ERR_PASSWORD_NOT_MATCH = 2;
        public static final int ERR_MESSAGE_FAILED = 3;
        public static final int ERR_MESSAGE_USEREXIST    = 5;
        public static final int ERR_MESSAGE_EMAILEXIST  = 6;
        public static final int ERR_MESSAGE_PHONEEXIST = 7;
        public static final int ERR_NETWORK_EXCEPTION = 8;
        public static final int ERR_UPLOAD_FAILED = 9;
        public static final int ERR_NULL_RETURN = 10;
        public static final int ERR_NETWORK_NOT_AVIABLE = 11;

        public static final int ERR_MESSAGE_ILLEGAL_USERNAME = 12;
    }

    public void cancelPost() {
        log("cancelPost");
        mPostTask.cancel(true);
    }
    
    public static void log(String str){
        android.util.Log.e("qinchao",str);
    }
    
    public void classLog(String str) {
        log(getClass().getName()+" --->" + str);
    }
}
