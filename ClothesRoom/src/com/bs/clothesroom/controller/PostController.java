package com.bs.clothesroom.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bs.clothesroom.R;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.JsonToken;
import android.util.Log;
import android.widget.Toast;

public class PostController {

    public static final String SERVICE_URL = "http://123.57.15.28/VirtualCloset/manager";
    
    /**
     * post type
     * 
     * @see #POST_TYPE_LOGIN
     * @see #POST_TYPE_REGISTER
     * @see #POST_TYPE_FETCH_USERINFO
     */
    public static final String POST_ARGS_TYPE = "type";
    public static final String POST_ARGS_USERNAME = "username";
    public static final String POST_ARGS_PASSWORD = "password";
    public static final String POST_ARGS_SEX = "sex";
    public static final String POST_ARGS_AGE = "age";
    public static final String POST_ARGS_PHONE = "phone";
    public static final String POST_ARGS_EMAIL = "email";
    public static final String POST_ARGS_WORK = "job";

    private static final String POST_TYPE_LOGIN = "login";
    private static final String POST_TYPE_REGISTER = "register";
    private static final String POST_TYPE_FETCH_USERINFO = "fetch_userinfo";
    private static final String POST_TYPE_UPLOAD_IMAGE = "upload_image";
    private static final String POST_TYPE_UPLOAD_FILE = "upload_file";
    private static final String POST_TYPE_DOWNLOAD_IMAGE = "download_image";
    private static final String POST_TYPE_DOWNLOAD_FILE = "download_file";
    
    public static final int POST_ID_UNKNOWN = 0;
    public static final int POST_ID_STRING_MASK = 0x0001;
    public static final int POST_ID_LOGIN = POST_ID_STRING_MASK << 0;
    public static final int POST_ID_REGISTER = POST_ID_STRING_MASK << 1;
    public static final int POST_ID_FETCH_USERINFO = POST_ID_STRING_MASK << 2;
    
    public static final int POST_ID_BINARY_MASK = 0x1001;
    
    private Context mContext;

    private static PostController mPostController;

    ArrayList<IPostCallback> mCallbacks = new ArrayList<IPostCallback>();

    DeliverCallback mDelivery;
    private PostTask mPostTask;
    

    public PostController(Context _context) {
        mContext = _context;
        mDelivery = new DeliverCallback();
    }

    private void fetchUserInfo() {
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = new JSONObject();
        try {
            addArgument(json, POST_ARGS_TYPE, POST_TYPE_FETCH_USERINFO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_FETCH_USERINFO);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                json.toString());
    }

//    public void login(String username, String psw) {
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(POST_ARGS_TYPE, POST_TYPE_LOGIN));
//        params.add(new BasicNameValuePair(POST_ARGS_USERNAME, username));
//        params.add(new BasicNameValuePair(POST_ARGS_PASSWORD, psw));
//        mPostTask = new PostTask(POST_ID_LOGIN);
//        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
//    }

    public void login(String username, String psw) {
        JSONObject json = new JSONObject();
        try {
            addArgument(json, POST_ARGS_TYPE, POST_TYPE_LOGIN);
            addArgument(json, POST_ARGS_USERNAME, username);
            addArgument(json, POST_ARGS_PASSWORD, psw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_LOGIN);
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
        JSONObject params = new JSONObject();
        try {
            addArgument(params, POST_ARGS_TYPE, POST_TYPE_REGISTER);
            addArgument(params, POST_ARGS_USERNAME, info.userName);
            addArgument(params, POST_ARGS_PASSWORD, info.password);
            addArgument(params, POST_ARGS_AGE, info.age);
            addArgument(params, POST_ARGS_SEX, info.sex);
            addArgument(params, POST_ARGS_EMAIL, info.email);
            addArgument(params, POST_ARGS_PHONE, info.phone);
            addArgument(params, POST_ARGS_WORK, info.job);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPostTask = new PostTask(POST_ID_REGISTER);
        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params.toString());
    }
    
    private void addArgument(JSONObject json,String key,String value) throws JSONException {
        json.put(key, value);
    }

    private void addArgument(List<NameValuePair> _params, String key,
            String value) {
        final List<NameValuePair> params = _params;
        params.add(new BasicNameValuePair(key, value));
    }

    class PostTask extends AsyncTask<String, Integer, PostResult> {

        private int mPostId= POST_ID_UNKNOWN;

        public PostTask(int postType) {
            mPostId = postType;
        }

        public PostTask() {
        }
        
        public void setPostType(int id){
            mPostId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected PostResult doInBackground(String... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(PostResult result) {
            boolean succeed = result.errId == PostResult.SUCCED;
            if (succeed) {
                if (result.postId == POST_ID_LOGIN) {
                    fetchUserInfo();
                }
                mDelivery.onPostSucceed(result);
            } else {
                mDelivery.onPostFailed(result);
            }
            super.onPostExecute(result);
        }

        private PostResult doPost(String json) {
            
            PostResult result = new PostResult();
            result.postId = mPostId;
            if (!checkNetworkAvaliable()) {
                result.errId = PostResult.ERR_NETWORK_NOT_AVIABLE;
                return result;
            }
            mDelivery.onPostStart(mPostId, "");

            HttpPost httpRequest = new HttpPost(SERVICE_URL);
            HttpEntity entity = null;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("requestJson",json));
                entity = new UrlEncodedFormEntity(params);
            } catch (UnsupportedEncodingException e1) {
                throw new PostException("json parse error.");
            }
            httpRequest.setEntity(entity);
            HttpClient httpclient = new DefaultHttpClient();
             httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
             10000); 
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpclient.execute(httpRequest);
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
                    parseEntity(httpResponse.getEntity(),result);
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
                    String error = EntityUtils.toString(entity);
                    result.errId = code;
                    Log.e("qinchao","code = "+code);
                    Log.e("qinchao","error = "+error);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        private void parseEntity(HttpEntity entity, PostResult result) throws ParseException, IOException, JSONException {
            String s = EntityUtils.toString(entity);
            JSONTokener parser = new JSONTokener(s);
            JSONObject json = (JSONObject) parser.nextValue();
            result.json = json;
            Log.e("qinchao","json = "+json);
            if ((mPostId >> 12) == 0) {
                /*
                 * POST_ID_STRING_MASK
                 */
                switch (mPostId) {
                case POST_ID_LOGIN:
                    result.errId = json.getInt("message");
                    break;
                case POST_ID_REGISTER: 
                    result.errId = json.getInt("message");
                    break;
                case POST_ID_FETCH_USERINFO:
                    result.errId = json.getInt("message");
                    break;

                default:
                    break;
                }
            } else {
                /*
                 * POST_ID_BINARY_MASK
                 */
                
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
        
        public static final int SUCCED = 0;
        public static final int ERR_INVALIDE_USERNAME = 1;
        public static final int ERR_PASSWORD_NOT_MATCH = 2;
        public static final int ERR_NETWORK_NOT_AVIABLE = 3;
        public static final int ERR_NULL_RETURN = 4;
        public static final int ERR_NETWORK_EXCEPTION = 5;
    }

    public void cancelPost() {
        Log.e("qinchao","cancelPost");
        mPostTask.cancel(true);
    }
}
