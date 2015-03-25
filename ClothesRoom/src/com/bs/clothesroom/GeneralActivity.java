package com.bs.clothesroom;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bs.clothesroom.controller.PostController;
import com.bs.clothesroom.controller.PostController.IPostCallback;
import com.bs.clothesroom.controller.PostController.PostResult;
import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.UserInfo;

public class GeneralActivity extends FragmentActivity {
	
	private static final String ACTION_LOGIN = "com.bs.clothesroom.login";
	private static final String ACTION_REGISTER = "com.bs.clothesroom.register";
	
	private static final int REQUEST_CODE_LOGIN = 0;
    private static final boolean DEBUG_CLASS = false;
	
	PostController mPostController;
	private PostCallback mPostCallback;
	private Fragment mCurrentFragment;
	
	private CheckingProgressDialog mCheckingDialog;
    UserInfo mUserInfo;
    Preferences mPrefs;
	
	
	public static void startLogin(Activity from) {
		Intent i = new Intent(from,GeneralActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(ACTION_LOGIN);
		from.startActivityForResult(i, REQUEST_CODE_LOGIN);
	}
	
	public static void startRegister(Activity from) {
		Intent i = new Intent(from,GeneralActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(ACTION_REGISTER);
		from.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = getIntent().getAction();
		setContentView(R.layout.general_actvity);
		mPrefs = Preferences.getInstance(this);
		mPostController = new PostController(this);
		mPostCallback = new PostCallback();
		if (!(GeneralActivity.this instanceof Main)) {
			ActionBar bar = getActionBar();
			bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		}
		if (ACTION_LOGIN.equals(action)) {
//			openFragment(R.id.fragment, LoginFragment.class, null, "login");
			replaceFragment(LoginFragment.class, null, R.id.fragment);
		} else if (ACTION_REGISTER.equals(action)) {
			replaceFragment(RegisterFragment.class, null, R.id.fragment);
//			openFragment(R.id.fragment, RegisterFragment.class, null, "register");
		}
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPostController.addCallback(mPostCallback);
	}
	
	@Override
    protected void onStop() {
	    mPostController.removeCallback(mPostCallback);
	    super.onStop();
    }

    public void replaceFragment(Class<? extends Fragment> f,Bundle b,int replace) {
		try {
			Fragment fragment = f.newInstance();
			fragment.setArguments(b);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(replace, fragment);
			transaction.commit();
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void openFragment(int containerId,Class<? extends Fragment> f,Bundle b,String tag) {
		try {
			Fragment fragment = f.newInstance();
			fragment.setArguments(b);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.add(containerId,fragment, tag);
			transaction.addToBackStack(tag);
			transaction.commit();
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static class DefaultFragment extends GeneralFragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			return inflater.inflate(R.layout.empty, container,false);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final long id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

    public PostController getController() {
        return mPostController;
    }
    
    class PostCallback implements IPostCallback {

        @Override
        public void onPostSucceed(PostResult result) {
            log("onPostSucceed");
            int post = result.postId;
            try {
                switch (post) {
                case PostController.POST_ID_LOGIN:
                    toastMessage(getString(R.string.login_succeed));
//                    mPostController.fetchUserInfo(result.primaryKey);
                    finish();
                    break;
                case PostController.POST_ID_REGISTER:
                    toastMessage(R.string.register_succeed);
                    startLogin(GeneralActivity.this);
                    finish();
                    break;
                case PostController.POST_ID_FETCH_USERINFO:
                    UserInfo userInfo = UserInfo.fromJson(result.json);
                    break;
                case PostController.POST_ID_UPLOAD_FILE:
                    toastMessage(R.string.upload_succeed);
                    break;
                default:
                    break;
                }
            } finally {
                log("mCheckingDialog = "+mCheckingDialog+"");
                if (mCheckingDialog != null) {
                    mCheckingDialog.dismissAllowingStateLoss();
                }
//                getSupportFragmentManager().popBackStack();
            }
        }

        @Override
        public void onPostFailed(PostResult result) {
            log("onPostFailed");
            final int post = result.postId;
            final int err = result.errId;
            switch (err) {
            case PostResult.ERR_INVALIDE_USERNAME:
                toastMessage(R.string.invalide_username);
                break;
            case PostResult.ERR_PASSWORD_NOT_MATCH:
                toastMessage(R.string.password_not_match);
                break;
            case PostResult.ERR_NETWORK_NOT_AVIABLE:
                toastMessage(R.string.network_not_avaliable);
                break;
            case PostResult.ERR_NETWORK_EXCEPTION:
                toastMessage(R.string.network_exception);
                break;
            case PostResult.ERR_UPLOAD_FAILED:
                toastMessage(R.string.upload_failed);
                break;
            case PostResult.ERR_MESSAGE_EMAILEXIST:
                toastMessage(R.string.email_exist);
                break;
            case PostResult.ERR_MESSAGE_PHONEEXIST:
                toastMessage(R.string.phone_exist);
                break;
            case PostResult.ERR_MESSAGE_USEREXIST:
                toastMessage(R.string.username_exist);
                break;
            case PostResult.ERR_MESSAGE_FAILED:
//                toastMessage(R.string.re);
                break;

            default:
                toastMessage("Error Code = "+result.errId);
                break;
            }
            log(mCheckingDialog+"");
            if (mCheckingDialog != null) {
                mCheckingDialog.dismissAllowingStateLoss();
            }
//            getSupportFragmentManager().popBackStack();
        }


        @Override
        public void onPostInfo(int post, int infoId, String info) {
            
        }

        @Override
        public void onPostStart(int post, String info) {
            String message = null;
            switch (post) {
            case PostController.POST_ID_LOGIN:
                message = getString(R.string.logining);
                break;
            case PostController.POST_ID_REGISTER:
                message = getString(R.string.registering);
                break;
            case PostController.POST_ID_FETCH_USERINFO:
                message = getString(R.string.fetch_userinfoing);
            case PostController.POST_ID_FETCH_FETCH_VEDIO_IDS:
            case PostController.POST_ID_FETCH_FETCH_IMAGE_IDS:
                message = getString(R.string.fetch_data);
                break;
            default:
                break;
            }
            mCheckingDialog = new CheckingProgressDialog();
            mCheckingDialog.setCancelable(false);
            
//            dialog.setTargetFragment(fragment, requestCode)
            mCheckingDialog.show(getSupportFragmentManager(), message);
        }
        
    }
    
    private class CheckingProgressDialog extends DialogFragment{

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            log("onCreateDialog");
            ProgressDialog dialog = new ProgressDialog(GeneralActivity.this,ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setMessage(getTag());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            mPostController.cancelPost();
            super.onCancel(dialog);
        }
    }
    
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        log(GeneralActivity.class.getName()+" : onActivityResult");
        super.onActivityResult(arg0, arg1, arg2);
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
    
    private void toastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    
    private void toastMessage(int id) {
        String msg = getString(id);
        toastMessage(msg);
    }

    public static void log(String str){
        android.util.Log.e("qinchao",str);
    }
    
    public void classLog(String str) {
        if (DEBUG_CLASS) {
            log(getClass().getName()+" --->" + str);
        }
    }
}
