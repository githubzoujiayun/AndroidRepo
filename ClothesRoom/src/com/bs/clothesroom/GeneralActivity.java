package com.bs.clothesroom;

import com.bs.clothesroom.controller.PostController;
import com.bs.clothesroom.controller.PostController.IPostCallback;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
	
	PostController mPostController;
	private PostCallback mPostCallback;
	
	private CheckingProgressDialog mCheckingDialog;
	
	
	public static void startLogin(Activity from) {
		Intent i = new Intent(from,GeneralActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(ACTION_LOGIN);
		from.startActivity(i);
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
        public void onPostSucceed(int post,String info) {
            if (post == PostController.POST_ID_LOGIN) {
                toastMessage(getString(R.string.login_succeed));
            }
        }

        @Override
        public void onPostFailed(int post, int err, String errMessage) {
            if (post == PostController.POST_ID_LOGIN) {
                if (errMessage != null) {
                    toastMessage(errMessage);
                } else {
                    switch (err) {
                    case PostController.POST_ERR_NETWORK_NOT_AVIABLE:
                        toastMessage(R.string.network_not_avaliable);
                        break;

                    default:
                        toastMessage(R.string.login_failed);
                        break;
                    }
                }
            }
        }


        @Override
        public void onPostInfo(int post, int infoId, String info) {
            
        }

        @Override
        public void onPostStart(int post, String message) {
            CheckingProgressDialog dialog = new CheckingProgressDialog();
//            dialog.setTargetFragment(fragment, requestCode)
            String title = "is login...";
            dialog.show(getSupportFragmentManager(), title);
        }
        
    }
    
    private class CheckingProgressDialog extends DialogFragment{

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog dialog = new ProgressDialog(GeneralActivity.this);
            dialog.setIndeterminate(true);
            dialog.setTitle(getTag());
            dialog.setCancelable(false);
            return dialog;
        }
        
    }
    
    private void toastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    
    private void toastMessage(int id) {
        String msg = getString(id);
        toastMessage(msg);
    }
}
