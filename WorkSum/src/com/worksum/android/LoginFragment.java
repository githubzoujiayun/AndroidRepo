package com.worksum.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.data.ObjectSessionStore;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.controller.UserCoreInfo;
import com.worksum.android.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * chao.qin
 *
 * 登陆Fragment
 */

@LayoutID(R.layout.login_fragment)
public class LoginFragment extends GeneralFragment implements View.OnClickListener {

    public static final int REQUEST_FOR_REGISTER = 100;

    EditText mLoginView;
    EditText mPwdView;
    Button mLoginBtn;
    Button mRegisterBtn;
    TextView mForgetText;
    private ImageView mCloseBtn;
    private LoginButton mFBLoginButton;

    protected static final int OPRATE_LOGIN = 1;
    protected static final int OPRATE_REGISTER = 2;

    protected int mOpration = OPRATE_LOGIN;

    private LoginCallback mCallback;

    public static void showLoginFragment(Context context) {
        showLoginFragment(context, null);
    }

    public static void showLoginFragment(Context context,LoginFragment.LoginCallback callback) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.FullScreenContainer.class);
        if(!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(KEY_FRAGMENT, LoginFragment.class);
        extras.putString("callback_key", ObjectSessionStore.insertObject(callback));
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    private CallbackManager mFBcallbackManager;
    private FacebookCallback mFBCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AppUtil.print("fb onSuccess!");
            registerFacebookInfo();
        }

        @Override
        public void onCancel() {
            AppUtil.print("fb onCancel");

        }

        @Override
        public void onError(FacebookException e) {
            AppUtil.print("fb onError");
            AppUtil.print(e);
        }
    };


    public interface LoginCallback {
        void onLoginSucceed();
    }

    @Override
    public void onStartRequest(String action) {
        super.onStartRequest(action);
        if (ResumeApi.ACTION_REGISTER_FACEBOOK.equals(action)) {
            //to do something
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        if (ResumeApi.ACTION_REGISTER_FACEBOOK.equals(action)) {
            String[] message = result.message.split("@");

            if (result.hasError || result.message.startsWith("-") || message.length != 2) {
                Tips.hiddenWaitingTips(getActivity());
                Tips.showTips(R.string.fb_register_failed);
                return;
            }

            AppCoreInfo.getCoreDB().setStrValue("user_info", "user_id", message[0]);
            int statusCode = Integer.parseInt(message[1]);
            if (statusCode == 0) {
                ResumeEditPage.showResumeEditPage(this);
                Tips.hiddenWaitingTips();
                onBackPressed();
            } else {
                JobsApi.getResumeInfo();
            }
        } else if (JobsApi.ACTION_GET_RESUME_INFO.equals(action)) {
            Tips.hiddenWaitingTips();
            if (!result.hasError) {
                UserCoreInfo.setUserLoginInfo(result, true, UserCoreInfo.USER_LOGIN_MANUAL);
                if (mCallback != null) {
                    mCallback.onLoginSucceed();
                }
                onBackPressed();
                onLoginSucceed();
            } else {
                Tips.showTips(R.string.login_get_resume_info_failed);
            }
        }
    }

    private void registerFacebookInfo() {
        final AccessToken token = AccessToken.getCurrentAccessToken();

        Bundle bundle = new Bundle();
        bundle.putString(GraphRequest.FIELDS_PARAM, "birthday,age_range,email,picture,education,first_name,last_name,name,work,gender,hometown,cover");
        Tips.showWaitingTips();
        new GraphRequest(token, token.getUserId(), bundle, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject jsonObj = graphResponse.getJSONObject();

                DataJsonResult jsonResult = null;
                String age = "";
                try {

                    jsonResult = new DataJsonResult(jsonObj.toString());
                    JSONObject ageJson = jsonObj.getJSONObject("age_range");
                    age = String.valueOf(ageJson.getInt("min"));
                } catch (JSONException e) {
                    if(jsonResult == null) {
                        jsonResult = new DataJsonResult();
                    }
                }

                DataItemDetail jsonDetail = jsonResult.toDataItemDetail();


                String firstName = jsonDetail.getString("first_name");
                String lastName = jsonDetail.getString("last_name");
                String name = jsonDetail.getString("name");

                String gender = jsonDetail.getString("gender");

                String email = jsonDetail.getString("email");

                String picUrl = "http://graph.facebook.com/" + token.getUserId() + "/picture?type=large";


                ResumeApi.registerFacebook(token.getUserId(),firstName,lastName,name,gender,age,email,picUrl);
            }
        }).executeAsync();
    }

    private void getFBPhoto() {


        Bundle bundle = new Bundle();
        bundle.putString(GraphRequest.FIELDS_PARAM,"people");

        AccessToken token = AccessToken.getCurrentAccessToken();
        String userId = token.getUserId();
        GraphRequest.newMeRequest(token,new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                AppUtil.print(graphResponse.toString());
            }
        }).executeAsync();

//        new GraphRequest(token,userId, bundle, HttpMethod.GET, new GraphRequest.Callback() {
//            @Override
//            public void onCompleted(GraphResponse graphResponse) {
//                AppUtil.print(graphResponse.toString());
//            }
//        }).executeAsync();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFBcallbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCallback = (LoginCallback) ObjectSessionStore.popObject(bundle.getString("callback_key"));
        }

        mLoginView = (EditText) findViewById(R.id.login_username);
        mPwdView = (EditText) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mForgetText = (TextView) findViewById(R.id.login_forget_psw);
        mCloseBtn = (ImageView) findViewById(R.id.login_close_btn);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        mFBLoginButton = (LoginButton) findViewById(R.id.login_fb_btn);
        mFBLoginButton.setFragment(this);
        mFBLoginButton.setReadPermissions(Arrays.asList("public_profile","email","user_photos"));
        mFBLoginButton.registerCallback(mFBcallbackManager,mFBCallback);

        mLoginBtn.setOnClickListener(this);
        mForgetText.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

    }

    class LoginTask extends SilentTask {

        LoginTask() {
        }

        boolean mTips = true;
        LoginTask(boolean tips) {
            mTips = tips;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips(getString(R.string.logining));

        }

        /**
         * 执行异步任务
         *
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            String phoneNumber = params[0];
            String password = params[1];
            return JobsApi.login(phoneNumber,password);
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (!result.hasError && result.statusCode >= 0) {
                AppCoreInfo.getCoreDB().setStrValue("user_info", "user_id", result.message);
                if (result.statusCode > 0) {
                    if(mTips) {
                        Tips.showTips(R.string.login_succeed);
                    }
                    JobsApi.getResumeInfo();
                } else if(result.statusCode <=0 ) {
                    Tips.showTips(R.string.login_failed);
                    Tips.hiddenWaitingTips();
                }
            } else {
                Tips.showTips(result.message);
            }

        }
    }

    @Override
    public void onClick(View view) {

        if (view == mLoginBtn && mOpration == OPRATE_LOGIN) {
            String phoneNumber = mLoginView.getText().toString();
            String password = mPwdView.getText().toString();
            if (!Utils.matchesPhone(phoneNumber)) {
                Tips.showTips(R.string.invalide_phone_number);
                return;
            }

            new LoginTask().execute(phoneNumber, password);
        } else if (view == mRegisterBtn) {
            RegisterFragment.showRegisterFragment(this);
        } else if (view == mForgetText) {
            DialogContainer.showForgetPassword(getActivity(),mLoginView.getText().toString());
        } else if (view == mCloseBtn) {
            onBackPressed();
        }
    }

    protected void onLoginSucceed() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_REGISTER && resultCode == RESULT_OK) {
            onBackPressed();
            return;
        }
        mFBcallbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
