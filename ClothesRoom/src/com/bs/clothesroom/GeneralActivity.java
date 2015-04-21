package com.bs.clothesroom;

import java.io.File;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.bs.clothesroom.provider.ClothesInfo;
import com.bs.clothesroom.provider.ClothesInfo.ImageInfo;
import com.bs.clothesroom.provider.ClothesInfo.VideoInfo;
import com.bs.clothesroom.provider.UserInfo;

public class GeneralActivity extends FragmentActivity {

    private static final String ACTION_LOGIN = "com.bs.clothesroom.login";
    private static final String ACTION_REGISTER = "com.bs.clothesroom.register";
    private static final String ACTION_SEARCH = "com.bs.clothesroom.search";
    private static final String ACTION_RACK = "com.bs.clothesroom.rack";
    private static final String ACTION_UPLOAD = "com.bs.clothesroom.upload_image";
    private static final String ACTION_MODIFY = "com.bs.clothesroom.upload_modify";

    private static final int REQUEST_CODE_LOGIN = 0;
    private static final boolean DEBUG_CLASS = false;

    PostController mPostController;
    private PostCallback mPostCallback;
    private Fragment mCurrentFragment;

    private CheckingProgressDialog mCheckingDialog;
    UserInfo mUserInfo;
    Preferences mPrefs;
    
    public static void openRack(Activity from,Bundle b) {
        Intent i = new Intent(from,GeneralActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(ACTION_RACK);
        i.putExtras(b);
        from.startActivity(i);
    }

	public static void search(Activity from,Bundle b) {
		Intent i = new Intent(from,GeneralActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(ACTION_SEARCH);
		i.putExtras(b);
		from.startActivity(i);
	}
    
	public static void upload(Activity from, Bundle b) {
		Intent i = new Intent(from, GeneralActivity.class);
		i.putExtras(b);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(ACTION_UPLOAD);
        from.startActivity(i);
	}
	
	public static void modify(Activity from, Bundle b) {
		Intent i = new Intent(from, GeneralActivity.class);
		i.putExtras(b);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(ACTION_MODIFY);
        from.startActivity(i);
	}
	
    public static void startLogin(Activity from) {
        Intent i = new Intent(from, GeneralActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(ACTION_LOGIN);
        from.startActivityForResult(i, REQUEST_CODE_LOGIN);
    }

    public static void startRegister(Activity from) {
        Intent i = new Intent(from, GeneralActivity.class);
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
            bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE);
        }
        if (ACTION_LOGIN.equals(action)) {
            // openFragment(R.id.fragment, LoginFragment.class, null, "login");
            replaceFragment(LoginFragment.class, null, R.id.fragment);
        } else if (ACTION_REGISTER.equals(action)) {
            replaceFragment(RegisterFragment.class, null, R.id.fragment);
            // openFragment(R.id.fragment, RegisterFragment.class, null,
            // "register");
        } else if (ACTION_SEARCH.equals(action)) {
        	Bundle b = getIntent().getExtras();
        	log("bundle = "+b);
        	replaceFragment(SearchResultFragment.class, b, R.id.fragment);
        } else if (ACTION_RACK.equals(action)) {
            replaceFragment(ClothesRack.class, getIntent().getExtras(), R.id.fragment);
        } else if (ACTION_UPLOAD.equals(action)) {
        	Bundle b = getIntent().getExtras();
        	replaceFragment(ImageUploadFragment.class, b, R.id.fragment);
        } else if (ACTION_MODIFY.equals(action)) {
        	Bundle b = getIntent().getExtras();
        	b.putBoolean("modify", true);
        	replaceFragment(ImageUploadFragment.class, b, R.id.fragment);
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

    public void replaceFragment(Class<? extends Fragment> f, Bundle b,
            int replace) {
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

    protected void openFragment(int containerId, Class<? extends Fragment> f,
            Bundle b, String tag) {
        try {
            Fragment fragment = f.newInstance();
            fragment.setArguments(b);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(containerId, fragment, tag);
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
            return inflater.inflate(R.layout.empty, container, false);
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

    private void onModifySucced(JSONObject json) throws JSONException {
    	log("onModifySucced.json : "+json);
    	ContentResolver resolver = getContentResolver();
    	int sid = json.getInt(ClothesInfo.JSON_KEY_IMAGE_SERVERID);
    	String uid = Preferences.getUsername(this);
        ClothesInfo info = ClothesInfo.getImageInfoBySID(resolver, sid, uid);
        
        ImageInfo srvInfo = ImageInfo.fromJson(json);
        
        info.mSeason = srvInfo.mSeason;
        info.mSituation = srvInfo.mSituation;
        info.mStyle = srvInfo.mStyle;
        info.mType = srvInfo.mType;
        ContentValues values = info.toContentValues();
//        getContentResolver().insert(ImageInfo.CONTENT_URI, values);
        Uri uri = ContentUris.withAppendedId(ImageInfo.CONTENT_URI, info.mId);
        getContentResolver().update(uri, values, null, null);
        toastMessage(R.string.modify_succed);
    }
    
    private void onFetchedImageInfo(JSONObject jsonObj) throws JSONException {
        final JSONObject json = jsonObj;
        int id = json.getInt("imageid");
        String imageName = json.getString("imagename");
        ImageInfo info = ImageInfo.fromJson(json);
        info.mMediaName = imageName;
        info.mMimeType = "image";
        info.mFlag = ClothesInfo.FLAG_DOWNLOAD_START;
        ContentValues values = info.toContentValues();
        getContentResolver().insert(ImageInfo.CONTENT_URI, values);
        log("startDownload");
        mPostController.downloadImage(info.mUserId, id);
    }
    
    private void onFetchedVideoInfo(JSONObject jsonObj) throws JSONException {
        final JSONObject json = jsonObj;
        int id = json.getInt("videoid");
        String videoName = json.getString("videoname");
        VideoInfo info = VideoInfo.fromJson(json);
        info.mMediaName = videoName; 
        info.mMimeType = "video";
        info.mFlag = ClothesInfo.FLAG_DOWNLOAD_START;
        ContentValues values = info.toContentValues();
        getContentResolver().insert(VideoInfo.CONTENT_URI, values);
        log("startDownload : json = "+json);
        mPostController.downloadVideo(info.mUserId, id);
    }
    
    private void onFetchedIds(String mimeType ,JSONObject jsonObj) throws JSONException {
        final JSONObject json = jsonObj;
        ContentResolver resolver = getContentResolver();
        String userId = Preferences.getUsername(this);
        if (ClothesInfo.MIMETYPE_IMAGE.equals(mimeType)) {
            JSONArray array = json.getJSONArray("image_ids");
            int size = array.length();
            if (size <= 0) {
                log("empty image ids : size = "+size);
                return;
            }
            int ids[] = new int[size];
            for (int i = 0; i < size; i++) {
                ids[i] = array.getInt(i);
            }
            log("ids[] = " + Arrays.toString(ids));
            int localIds[] = ClothesInfo.getImageIds(resolver, userId);
            log("localIds : "+Arrays.toString(localIds));
            // fetch serverId
            for (int i = 0; i < size; i++) {
                int j = 0;
                int length = localIds.length;
                for (; j < length; j++) {
                    if (ids[i] == localIds[j]) {
                        ClothesInfo info = ClothesInfo.getImageInfoBySID(resolver, ids[i], userId);
                        String mediaPath = info.mMediaPath;
                        String mediaName = info.mMediaName;
                        if (mediaName != null && mediaPath != null
                                && !new File(mediaPath).exists()) {
                            mPostController.fetchImageInfo(userId,ids[i]);
                        }
                        break;
                    }
                }
                if (j == localIds.length) {
                    mPostController.fetchImageInfo(userId, ids[i]);
                }
            }
            //delete local image 
            for (int i=0;i<localIds.length;i++) {
                
                int j=0;
                for (;j<size;j++) {
                    if (localIds[i] == ids[j]) {
                        break;
                    }
                }
                if (j == size) {
                    ClothesInfo info = ClothesInfo.getImageInfoBySID(resolver, localIds[i], userId);
                    String path = info.mMediaPath;
                    if (path != null) {
                        File f = new File(path);
                        if (f.exists()) 
                            f.delete();
                    }
                    Uri uri = ContentUris.withAppendedId(ImageInfo.CONTENT_URI, info.mId);
                    resolver.delete(uri, null, null);
                }
            }
            
        } else if (ClothesInfo.MIMETYPE_VIDEO.equals(mimeType)){
            JSONArray array = json.getJSONArray("video_ids");
            int size = array.length();
            if (size <= 0) {
                log("empty video ids : size = "+size);
                return;
            }
            int ids[] = new int[size];
            for (int i = 0; i < size; i++) {
                ids[i] = array.getInt(i);
            }
            log("ids[] = " + Arrays.toString(ids));
            int localIds[] = ClothesInfo.getVideoIds(resolver, userId);
            log("localIds : "+Arrays.toString(localIds));
            for (int i = 0; i < size; i++) {
                int j = 0;
                int length = localIds.length;
                for (; j < length; j++) {
                    if (ids[i] == localIds[j]) {
                        ClothesInfo info = ClothesInfo.getVideoInfoBySID(resolver, ids[i], userId);
                        String mediaPath = info.mMediaPath;
                        String mediaName = info.mMediaName;
                        if (mediaName != null && mediaPath != null
                                && !new File(mediaPath).exists()) {
                            mPostController.fetchVideoInfo(userId,ids[i]);
                        }
                        break;
                    }
                }
                if (j == localIds.length) {
                    mPostController.fetchVideoInfo(userId, ids[i]);
                }
            }
        } else {
            throw new IllegalArgumentException("unkown mimeType : " + mimeType);
        }
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
                    // mPostController.fetchUserInfo(result.primaryKey);
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
                    finish();
                    break;
                case PostController.POST_ID_FETCH_FETCH_IMAGE_IDS:
                    try {
                        onFetchedIds(ClothesInfo.MIMETYPE_IMAGE, result.json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PostController.POST_ID_FETCH_FETCH_IMAGE_INFO:
                    try {
                        onFetchedImageInfo(result.json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PostController.POST_ID_FETCH_FETCH_VIDEO_IDS:
                    try {
                        onFetchedIds(ClothesInfo.MIMETYPE_VIDEO, result.json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PostController.POST_ID_FETCH_FETCH_VIDEO_INFO:
                    try {
                        onFetchedVideoInfo(result.json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PostController.POST_ID_DELETE_IMAGE:
                    String userId = Preferences.getUsername(GeneralActivity.this);
                    mPostController.fetchImageIds(userId);
                    break;
                case PostController.POST_ID_MODIFY_FILE:
                	log("modify succed from server !");
                	//delete local image from sdcard and database
//                	ContentResolver resolver = getContentResolver();
//                	int sid = (Integer) result.obj;
//                	String uid = Preferences.getUsername(GeneralActivity.this);
//                	ClothesInfo info = ClothesInfo.getImageInfoBySID(resolver,sid, uid);
//                	Uri uri = ContentUris.withAppendedId(ImageInfo.CONTENT_URI, info.mId);
//                	resolver.delete(uri, null,null);
//                	File f = new File(info.mMediaPath);
//                	if (f.exists()) {
////                		boolean succed = f.delete();
//                		log("delete succed from sdcard !");
//                	}
//                	//sync image
//                	mPostController.fetchImageIds(uid);
                	
                	try {
						onModifySucced(result.json);
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
                	break;
                default:
                    break;
                }
            } finally {
                log("mCheckingDialog = " + mCheckingDialog + "");
                if (mCheckingDialog != null) {
                    mCheckingDialog.dismissAllowingStateLoss();
                }
            }
        }

        @Override
        public void onPostFailed(PostResult result) {
            log("onPostFailed");
            final int post = result.postId;
            final int err = result.errId;
            try {
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
                    // toastMessage(R.string.re);
                    break;
                case PostResult.ERR_MESSAGE_ILLEGAL_USERNAME:
                	startLogin(GeneralActivity.this);
                	break;

                default:
                    toastMessage("Error Code = " + result.errId);
                    break;
                }
            } finally {
                log("mCheckingDialog = " + mCheckingDialog + "");
                if (mCheckingDialog != null) {
                    mCheckingDialog.dismissAllowingStateLoss();
                }
            }
            log(mCheckingDialog + "");
            if (mCheckingDialog != null) {
                mCheckingDialog.dismissAllowingStateLoss();
            }
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
            case PostController.POST_ID_FETCH_FETCH_VIDEO_IDS:
            case PostController.POST_ID_FETCH_FETCH_IMAGE_IDS:
                message = getString(R.string.fetch_data);
                break;
            case PostController.POST_ID_DOWNLOAD_IMAGE:
            case PostController.POST_ID_DOWNLOAD_VIDEO:
                break;
            default:
                break;
            }
            if (!mPostController.isBinaryId(post)) {
                if (mCheckingDialog != null) {
                    mCheckingDialog.dismiss();
                }
                mCheckingDialog = new CheckingProgressDialog();
                // mCheckingDialog.setCancelable(false);

                mCheckingDialog.show(getSupportFragmentManager(), message);
            }
        }

    }

    private class CheckingProgressDialog extends DialogFragment {

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            log("onCreateDialog");
            ProgressDialog dialog = new ProgressDialog(GeneralActivity.this,
                    ProgressDialog.THEME_HOLO_LIGHT);
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
        log(GeneralActivity.class.getName() + " : onActivityResult");
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

    public static void log(String str) {
        android.util.Log.e("qinchao", str);
    }

    public void classLog(String str) {
        if (DEBUG_CLASS) {
            log(getClass().getName() + " --->" + str);
        }
    }
}
