package com.worksum.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worksum.android.apis.ResumeApi;
import com.worksum.android.controller.DataController;
import com.worksum.android.controller.UserCoreInfo;
import com.worksum.android.views.HeaderIconView;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.misc.BitmapUtil;
import com.jobs.lib_v1.misc.Tips;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SelfFragment extends TitlebarFragment implements AdapterView.OnItemClickListener {

    private static final int ID_MY_RESUME = 1;
    private static final int ID_MY_VIDEO = 2;
    private static final int ID_SYSTEM_SETTINGS = 3;
    private static final int REQUST_CODE_UNKOWN = -1;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;
    private static final int REQUEST_CODE_UPDATE_RESUME = 2;

    private DataListView mListView;
    private HeaderIconView mHeaderView;

    private TextView mUsername;
    private TextView mUserTitle;
    private TextView mUserProfile;

    private Button mLoginButton;
    private DataController.DataAdapter mDataAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.self;
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);

        setTitle(R.string.title_self);

        mHeaderView = (HeaderIconView) findViewById(R.id.header_icon);
        mHeaderView.setOnClickListener(this);

        mListView = (DataListView) findViewById(R.id.items_list);
        mListView.setDataCellClass(ItemCell.class, this);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(this);
        mListView.setAllowAutoTurnPage(false);

        mListView.appendData(buildItems());

        mLoginButton = (Button) findViewById(R.id.self_login_button);
        mLoginButton.setOnClickListener(this);

        mUserTitle = (TextView) findViewById(R.id.user_title);
        mUsername = (TextView) findViewById(R.id.user_name);
        mUserProfile = (TextView) findViewById(R.id.user_profile);


        initView();
    }

    private void requestHeadPhoto() {
        if (mDataAdapter != null) {
            mDataAdapter.cancel(true);
        }
        mDataAdapter = DataController.getInstance().newDataAdapter();
        mDataAdapter.setDataListener(new DataController.DataLoadListener() {
            @Override
            public void onSucceed(DataItemResult result) {
                if (result.statusCode == 1) {
                    byte[] data = Base64.decode(result.message);
                    try {
                        Bitmap bitmap = getThumbnail(data, 100);
                        mHeaderView.setImageBitmap(bitmap);

                        AppCoreInfo.getCacheDB().setBinValue("head_icon", UserCoreInfo.getUserID(), data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(DataItemResult result, boolean isNetworkConnected) {

            }

            @Override
            public void onEmpty(DataItemResult result) {

            }

            @Override
            public void onBeforeLoad() {
                byte[] data = AppCoreInfo.getCacheDB().getBinValue("head_icon", UserCoreInfo.getUserID());
                if (data != null) {
                    try {
                        Bitmap bitmap = getThumbnail(data,100);
                        if (bitmap != null) {
                            mHeaderView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public DataItemResult onLoadData() {
                return ResumeApi.getPhoto();
            }

            @Override
            public void onCancelled() {

            }
        });
        mDataAdapter.setShowTips(false);
        if (UserCoreInfo.hasLogined()) {
            mDataAdapter.execute();
        }
    }

    private void initView() {
        View loginLayout = findViewById(R.id.login_layout);
        View unloginLayout = findViewById(R.id.unlogin_layout);
        loginLayout.setVisibility(View.GONE);
        unloginLayout.setVisibility(View.GONE);
        if (UserCoreInfo.hasLogined()) {
            loginLayout.setVisibility(View.VISIBLE);
            setActionRightText(R.string.self_right_action_logout);
        } else {
            unloginLayout.setVisibility(View.VISIBLE);
            setActionRightText(R.string.self_right_action_login);
        }

        mUsername.setText(UserCoreInfo.getUserName());
        mUserTitle.setText(UserCoreInfo.getFunctionName());
        mUserProfile.setText(UserCoreInfo.getMemo());

        requestHeadPhoto();
    }

    private DataItemResult buildItems() {
        DataItemResult datas = new DataItemResult();

        DataItemDetail detail = new DataItemDetail();
        detail.setIntValue("id", ID_MY_RESUME);
        detail.setIntValue("titleId", R.string.self_my_resume);
        detail.setIntValue("descriptionId", R.string.self_resume_description);
        detail.setIntValue("iconId", R.drawable.me_resume);
        datas.addItem(detail);

        detail = new DataItemDetail();
        detail.setIntValue("id", ID_MY_VIDEO);
        detail.setIntValue("titleId", R.string.self_my_video);
        detail.setIntValue("descriptionId", R.string.self_video_description);
        detail.setIntValue("iconId", R.drawable.me_video);
//        datas.addItem(detail); //// TODO: 16/3/8 暂时不用

        detail = new DataItemDetail();
        detail.setIntValue("id", ID_SYSTEM_SETTINGS);
        detail.setIntValue("titleId", R.string.self_my_settings);
        detail.setIntValue("descriptionId", R.string.self_settings_description);
        detail.setIntValue("iconId", R.drawable.me_settings);
        datas.addItem(detail);
        return datas;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.header_icon:
                pickPhoto();
                break;
            case R.id.self_login_button:
                DialogContainer.showLoginDialog(getActivity());
                break;
            case R.id.bar_right_action:
                if (UserCoreInfo.hasLogined()) {
                    logout();
                } else {
                    DialogContainer.showLoginDialog(getActivity());
                }
                break;

        }
    }

    private void logout() {
        Tips.showConfirm(getString(R.string.self_logout_alter), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    UserCoreInfo.logout();
                    mHeaderView.resetHeadIcon();
                }
            }
        });
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_PHOTO) {
            if (data.getData() == null) {
                return;
            }
            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Bitmap bitmap = getThumbnail(data.getData(), 100);
                mHeaderView.setImageBitmap(bitmap);

                uploadPhoto(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_RESUME) {
            initView();
        }
    }

    public Bitmap getThumbnail(byte[] data, int size) throws FileNotFoundException, IOException {
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeByteArray(data, 0, data.length, onlyBoundsOptions);
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
        return bitmap;
    }

    public Bitmap getThumbnail(Uri uri, int size) throws FileNotFoundException, IOException {
        InputStream input = getActivity().getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = getActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    private void uploadPhoto(Bitmap bitmap) {
        byte[] bitmapData = BitmapUtil.getBitmapBytes(bitmap,-1,-1, -1);
        String base64Data = Base64.encode(bitmapData, 0, bitmapData.length);

        AppCoreInfo.getCacheDB().setBinValue("head_icon", UserCoreInfo.getUserID(), bitmapData);

        if (mDataAdapter != null) {
            mDataAdapter.cancel(true);
        }

        DataController.DataAdapter adapter = DataController.getInstance().newDataAdapter();
        adapter.setDataListener(new DataController.DataLoadListener() {
            @Override
            public void onSucceed(DataItemResult result) {
                if (result.statusCode == 1) {
                    Tips.showTips(R.string.self_photo_upload_succeed);
                } else {
                    Tips.showTips(R.string.self_photo_upload_failed);
                }
            }

            @Override
            public void onFailed(DataItemResult result, boolean isNetworkConnected) {
                if (!isNetworkConnected) {
                    Tips.showTips(R.string.network_invalid);
                }
            }

            @Override
            public void onEmpty(DataItemResult result) {

            }

            @Override
            public void onBeforeLoad() {

            }

            @Override
            public DataItemResult onLoadData() {
                return ResumeApi.savePhotoFile(base64Data);
            }

            @Override
            public void onCancelled() {

            }
        });
        adapter.executeOnPool();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DataListAdapter adapter = (DataListAdapter) adapterView.getAdapter();

        DataItemResult result = adapter.getListData();
        DataItemDetail detail = result.getItem(i);
        switch (detail.getInt("id")) {
            case ID_MY_RESUME:
                if (UserCoreInfo.hasLogined()) {
                    FragmentContainer.showMyResume(this, REQUEST_CODE_UPDATE_RESUME);
                } else {
                    DialogContainer.showLoginDialog(getActivity(), new LoginFragment.LoginCallback() {
                        @Override
                        public void onLoginSucceed() {
                            FragmentContainer.showMyResume(SelfFragment.this, REQUEST_CODE_UPDATE_RESUME);
                        }
                    });
                }
                break;
            case ID_MY_VIDEO:
                break;
            case ID_SYSTEM_SETTINGS:
                break;
        }
    }


    private class ItemCell extends DataListCell {

        private TextView mTitle;
        private TextView mDescription;
        private ImageView mIcon;

        /**
         * 获取单元格对应的 layoutID
         * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
         * getCellViewLayoutID 方法返回0时会调用 createCellView
         */
        @Override
        public int getCellViewLayoutID() {
            return R.layout.self_item_cell;
        }

        /**
         * 绑定单元格视图中的控件到变量
         * 该方法由子类实现
         */
        @Override
        public void bindView() {
            mTitle = (TextView) findViewById(R.id.self_item_title);
            mDescription = (TextView) findViewById(R.id.self_item_description);
            mIcon = (ImageView) findViewById(R.id.item_icon);
        }

        /**
         * 绑定单元格数据到控件
         * 该方法由子类实现
         */
        @Override
        public void bindData() {
            mTitle.setText(mDetail.getInt("titleId"));
            mDescription.setText(mDetail.getInt("descriptionId"));
            mIcon.setImageResource(mDetail.getInt("iconId"));
        }
    }

    @Override
    public void onUserStatusChanged(int loginType) {
        super.onUserStatusChanged(loginType);
        if (loginType == UserCoreInfo.USER_LOGIN_OTHERS || loginType == UserCoreInfo.USER_LOGIN_OTHERS) {
            return;
        }
        AppUtil.print("onUserStatusChanged");
        initView();
    }

}
