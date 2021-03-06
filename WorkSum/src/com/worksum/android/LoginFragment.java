package com.worksum.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.data.ObjectSessionStore;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.CustomerApi;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.controller.LoginManager;
import com.worksum.android.controller.UserCoreInfo;
import com.worksum.android.login.NimLoginManager;
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
@DataManagerReg(register = DataManagerReg.RegisterType.ALL)
public class LoginFragment extends GeneralFragment implements View.OnClickListener {

    public static final int REQUEST_FOR_REGISTER = 100;

    EditText mPwdView;

    EditText mUserNameView;

    Button mLoginBtn;
    Button mRegisterBtn;
    TextView mForgetText;
    private ImageView mCloseBtn;
    private LoginButton mFBLoginButton;

    protected LoginManager.LoginType mLoginType = LoginManager.LoginType.U;

    protected static final int OPRATE_LOGIN = 1;
    protected static final int OPRATE_REGISTER = 2;

    public static void showLoginFragment(Context context) {
        showLoginFragment(context,LoginManager.getInstance().getLoginType());
    }

    public static void showLoginFragment(Context context,LoginManager.LoginType loginType) {
        showLoginFragment(context,loginType, null);
    }

    public static void showLoginFragment(Context context, LoginFragment.LoginCallback callback) {
        showLoginFragment(context,LoginManager.getInstance().getLoginType(),callback);
    }

    public static void showLoginFragment(Context context, LoginManager.LoginType loginType, LoginFragment.LoginCallback callback) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.FullScreenContainer.class);
        if(!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(KEY_FRAGMENT, LoginFragment.class);
        extras.putString("callback_key", ObjectSessionStore.insertObject(callback));
        extras.putString(LoginManager.PARAMS_LOGIN_TYPE,loginType.name());
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
            Tips.showTips(R.string.facebook_login_failed);
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
        if (ResumeApi.ACTION_REGISTER_FACEBOOK.equals(action)) {
            String[] message = result.message.split("@");

            if (result.hasError || result.message.startsWith("-") || message.length != 2) {
                Tips.hiddenWaitingTips(getActivity());
                Tips.showTips(R.string.fb_register_failed);
                return;
            }

            int statusCode = Integer.parseInt(message[1]);
            if (statusCode == 0) {
                ResumeEditPage.showResumeEditPage(this);
                Tips.hiddenWaitingTips();
            }
            onLoginSucceed(message[0]);
        }
    }

    private void registerFacebookInfo() {
        final AccessToken token = AccessToken.getCurrentAccessToken();

        Bundle bundle = new Bundle();
        bundle.putString(GraphRequest.FIELDS_PARAM, "birthday,age_range,email,picture,education,first_name,last_name,name,work,gender,hometown,cover");
        Tips.showWaitingTips(getActivity(),null);
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
            String name = bundle.getString(LoginManager.PARAMS_LOGIN_TYPE);
            mLoginType = LoginManager.LoginType.valueOf(name);
        }


        EditText loginView = (EditText) findViewById(R.id.login_phone_number);
        mPwdView = (EditText) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mForgetText = (TextView) findViewById(R.id.login_forget_psw);
        mCloseBtn = (ImageView) findViewById(R.id.login_close_btn);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        EditText companyEmailView = (EditText) findViewById(R.id.login_company_email);

        mFBLoginButton = (LoginButton) findViewById(R.id.login_fb_btn);
        mFBLoginButton.setFragment(this);
        mFBLoginButton.setReadPermissions(Arrays.asList("public_profile","email","user_photos"));
        mFBLoginButton.registerCallback(mFBcallbackManager,mFBCallback);

        mLoginBtn.setOnClickListener(this);
        mForgetText.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

        mUserNameView = loginView;
        if (mLoginType == LoginManager.LoginType.C) {
            findViewById(R.id.login_phone_number_layout).setVisibility(View.GONE);
            findViewById(R.id.login_company_email_layout).setVisibility(View.VISIBLE);
            mUserNameView = companyEmailView;
        }

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
            String username = params[0];
            String password = params[1];
            if (mLoginType == LoginManager.LoginType.C) {
                return CustomerApi.loginCustomer(username,password);
            }
            return ResumeApi.login(username,password);
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            Tips.hiddenWaitingTips();
            if (!result.hasError && result.statusCode > 0) {
                if (result.statusCode > 0) {
                    if(mTips) {
                        Tips.showTips(R.string.login_succeed);
                    }
                    onLoginSucceed(result.message);
                } else if(result.statusCode <=0 ) {
                    Tips.showTips(R.string.login_failed);
                    Tips.hiddenWaitingTips();
                }
            } else {
                String message = result.message;
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.tips_login_failed);
                }
                Tips.showTips(message);
            }

        }
    }

    @Override
    public void onClick(View view) {

        if (view == mLoginBtn) {
            String userName = mUserNameView.getText().toString();
            String password = mPwdView.getText().toString();
            if (mLoginType == LoginManager.LoginType.R) {
                if (!Utils.matchesPhone(userName)) {
                    Tips.showTips(R.string.tips_invalide_phone_number);
                    return;
                }
            } else if (mLoginType == LoginManager.LoginType.C) {
                if (!Utils.matchesEmail(userName)) {
                    Tips.showTips(R.string.tips_invalide_email_format);
                    return;
                }
            }

            new LoginTask().execute(userName, password);
        } else if (view == mRegisterBtn) {
            RegisterFragment.showRegisterFragment(this,mLoginType);
        } else if (view == mForgetText) {
            DialogContainer.showForgetPassword(getActivity(),mUserNameView.getText().toString());
        } else if (view == mCloseBtn) {
            onBackPressed();
        }
    }

    protected void onLoginSucceed(String accountId) {

        UserCoreInfo.setAccountId(accountId);
        LoginManager.getInstance().setLoginType(mLoginType);
        UserCoreInfo.updateUserStatus(UserCoreInfo.USER_LOGIN_MANUAL);//通知登入成功

        NimLoginManager.login();

        getActivity().finish();
        Main.showMain(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_REGISTER && resultCode == RESULT_OK) {
            getActivity().finish();
            return;
        }
        mFBcallbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
