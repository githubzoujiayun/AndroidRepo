package com.bs.clothesroom;

import org.json.JSONException;

import com.bs.clothesroom.controller.PostController;
import com.bs.clothesroom.controller.UserInfo;
import com.bs.clothesroom.controller.PostController.IPostCallback;
import com.bs.clothesroom.controller.PostController.PostResult;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

public class GeneralActivity extends FragmentActivity {
	
	private static final String ACTION_LOGIN = "com.bs.clothesroom.login";
	private static final String ACTION_REGISTER = "com.bs.clothesroom.register";
	
	private static final int REQUEST_CODE_LOGIN = 0;
	
	PostController mPostController;
	private PostCallback mPostCallback;
	private Fragment mCurrentFragment;
	
	private CheckingProgressDialog mCheckingDialog;
	
	
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
		mPostController = new PostController(this);
		mPostCallback = new PostCallback();
		mPostController.addCallback(mPostCallback);
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
	protected void onDestroy() {
	    mPostController.removeCallback(mPostCallback);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
            int post = result.postId;
            switch (post) {
            case PostController.POST_ID_LOGIN:
                toastMessage(getString(R.string.login_succeed));
                break;
            case PostController.POST_ID_REGISTER:
                toastMessage(R.string.register_succeed);
                break;
            case PostController.POST_ID_FETCH_USERINFO:
                try {
                    UserInfo userInfo = UserInfo.fromJson(result.json);
                    Intent i = new Intent();
                    i.putExtra("userinfo", userInfo);
                    setResult(RESULT_OK, i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
            }
            if (mCheckingDialog != null) {
                mCheckingDialog.dismissAllowingStateLoss();
            }
            getSupportFragmentManager().popBackStack();
        }

        @Override
        public void onPostFailed(PostResult result) {
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

            default:
                break;
            }
            if (mCheckingDialog != null) {
                mCheckingDialog.dismissAllowingStateLoss();
            }
            getSupportFragmentManager().popBackStack();
        }


        @Override
        public void onPostInfo(int post, int infoId, String info) {
            
        }

        @Override
        public void onPostStart(int post, String message) {
            String msg = "is login...";
            mCheckingDialog = new CheckingProgressDialog();
            mCheckingDialog.setCancelable(false);
            
//            dialog.setTargetFragment(fragment, requestCode)
            mCheckingDialog.show(getSupportFragmentManager(), msg);
        }
        
    }
    
    private class CheckingProgressDialog extends DialogFragment{

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
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
    
    private void toastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    
    private void toastMessage(int id) {
        String msg = getString(id);
        toastMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int result, Intent intent) {
        if (requestCode == REQUEST_CODE_LOGIN) {
            
        }
        super.onActivityResult(requestCode, result, intent);
    }
}
