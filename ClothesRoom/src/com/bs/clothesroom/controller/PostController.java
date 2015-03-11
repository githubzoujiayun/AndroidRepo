package com.bs.clothesroom.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.bs.clothesroom.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class PostController {

    public static final String SERVICE_URL = "http://123.57.15.28/VirtualCloset/login";
    /**
     * post type
     * 
     * @see #POST_TYPE_LOGIN
     * @see #POST_TYPE_REGISTER
     * @see #POST_TYPE_FETCH_USERINFO
     */
    public static final String POST_ARGS_TYPE = "type";
    public static final String POST_ARGS_USERNAME = "user";
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
    public static final int POST_ID_LOGIN = 1;
    public static final int POST_ID_REGISTER = 2;
    public static final int POST_ID_FETCH_USERINFO = 3;
    
    public static final int POST_ERR_SUCCEED = 0;
    public static final int POST_ERR_NETWORK_NOT_AVIABLE = 1;
    public static final int POST_ERR_NULL_RETURN = 2;

    private Context mContext;

    private static PostController mPostController;
    private PostHandler mHandler;

    ArrayList<IPostCallback> mCallbacks = new ArrayList<IPostCallback>();

    DeliverCallback mDelivery;
    
    private class PostHandler extends Handler {

        public static final int FAILED = 0;
        public static final int SUCCEED = 1;

        public void sendPostResult(int result, String strResult) {
            Message msg = obtainMessage();
            msg.arg1 = result;
            msg.obj = strResult;
            msg.sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean succeed = msg.arg1 == 1;
            if (succeed) {
                Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(mContext, "请求错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public static PostController getInstance(Context _context) {
//        if (mPostController != null) {
//            return mPostController;
//        }
//        mPostController = new PostController(_context);
//        return mPostController;
//    }

    public PostController(Context _context) {
        mContext = _context;
        mHandler = new PostHandler();
        mDelivery = new DeliverCallback();
    }

    private boolean fetchUserInfo() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(POST_ARGS_TYPE,
                POST_TYPE_FETCH_USERINFO));
        return false;
    }

    public void login(String username, String psw) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(POST_ARGS_TYPE, POST_TYPE_LOGIN));
        params.add(new BasicNameValuePair(POST_ARGS_USERNAME, username));
        params.add(new BasicNameValuePair(POST_ARGS_PASSWORD, psw));
        new PostTask(POST_ID_LOGIN).execute(params);
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
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        addArgument(params, POST_ARGS_TYPE, POST_TYPE_REGISTER);
        addArgument(params, POST_ARGS_USERNAME, info.userName);
        addArgument(params, POST_ARGS_PASSWORD, info.password);
        addArgument(params, POST_ARGS_AGE, info.age);
        addArgument(params, POST_ARGS_SEX, info.sex);
        addArgument(params, POST_ARGS_EMAIL, info.email);
        addArgument(params, POST_ARGS_PHONE, info.phone);
        addArgument(params, POST_ARGS_WORK, info.work);
        new PostTask(POST_ID_REGISTER).execute(params);
    }

    private void addArgument(List<NameValuePair> _params, String key,
            String value) {
        final List<NameValuePair> params = _params;
        params.add(new BasicNameValuePair(key, value));
    }

    class PostTask extends AsyncTask<List<NameValuePair>, Boolean, Boolean> {

        private int mPostId= POST_ID_UNKNOWN;
        private int err = POST_ERR_SUCCEED;
        private String msg = null;
        

        public PostTask(int postType) {
            mPostId = postType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(List<NameValuePair>... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
//            Toast.makeText(mContext, "result = " + result, Toast.LENGTH_LONG)
//                    .show();
            if (succeed) {
                mDelivery.onPostSucceed(mPostId,msg);
            } else {
                mDelivery.onPostFailed(mPostId,err,msg);
            }
            super.onPostExecute(succeed);
        }

        private Boolean doPost(List<NameValuePair> params) {
            if (!checkNetworkAvaliable()) {
//                Toast.makeText(mContext, R.string.network_not_avaliable,
//                        Toast.LENGTH_SHORT).show();
                err = POST_ERR_NETWORK_NOT_AVIABLE;
                return false;
            }
            mDelivery.onPostStart(mPostId, "");
            String strResult = null;

            HttpPost httpRequest = new HttpPost(SERVICE_URL);
            HttpEntity httpentity = null;
            try {
                httpentity = new UrlEncodedFormEntity(params, "utf8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            httpRequest.setEntity(httpentity);
            HttpClient httpclient = new DefaultHttpClient();
            // httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
            // 30000);
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpclient.execute(httpRequest);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StatusLine line = httpResponse.getStatusLine();
            if (line == null) {
                err = POST_ERR_NULL_RETURN;
                msg = null;
                return false;
            }
            if (line.getStatusCode() == 200) {
                try {
                    strResult = EntityUtils.toString(httpResponse.getEntity());
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                msg = strResult;
                return true;
            } else {
                
            }
            return false;
        }
    }

    class PostException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = 2125539439742249516L;
    }
    
    private class DeliverCallback implements IPostCallback{

        @Override
        public void onPostSucceed(int postId,String message) {
            for (IPostCallback callback : mCallbacks) {
                callback.onPostSucceed(postId,message);
            }
        }

        @Override
        public void onPostFailed(int postId, int err,String errMessage) {
            for (IPostCallback callback : mCallbacks) {
                callback.onPostFailed(postId,err,errMessage);
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
        
        public void onPostSucceed(int post,String message);

        public void onPostFailed(int post, int errId,String errMessage);
        
        public void onPostInfo(int post, int infoId,String info);
    }
}
