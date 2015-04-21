package com.bs.clothesroom;


import java.io.File;
import java.lang.reflect.Modifier;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bs.clothesroom.provider.ClothesInfo;
import com.bs.clothesroom.provider.ClothesInfo.Season;
import com.bs.clothesroom.provider.ClothesInfo.Situation;
import com.bs.clothesroom.provider.ClothesInfo.Style;
import com.bs.clothesroom.provider.ClothesInfo.Type;

public class ImageUploadFragment extends GeneralFragment implements OnClickListener {
    
    Button mUpload;
    RadioGroup mSeason,mStyle,mType,mSituation;
    RadioButton mLeisure,mBuesiness,mFashion,mGentleman;
    RadioButton mOvercoat,mSleeved,mTrousers;
    ImageView mPreviewView;
    
	private static final int[] SEASON_IDS = new int[] { R.id.spring,
			R.id.summer, R.id.autumn, R.id.winter };

	private static final int[] STYLE_IDS = new int[] { 
			R.id.gentleman,R.id.leisure, R.id.business, R.id.fashion };

	private static final int[] SITUATION_IDS = new int[] { R.id.public_place,
			R.id.office, R.id.cocktail };

	private static final int[] TYPE_IDS = new int[] { R.id.sleeved,
			R.id.trousers, R.id.overcoat };
    
    private String mImagePath;
    private ClothesInfo mInfo;
    
    private boolean mModify = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_upload,container,false);
        mUpload = (Button) v.findViewById(R.id.upload);
//        mLeisure = (RadioButton) v.findViewById(R.id.leisure);
//        mBuesiness = (RadioButton) v.findViewById(R.id.business);
//        mFashion = (RadioButton) v.findViewById(R.id.fashion);
//        mGentleman = (RadioButton) v.findViewById(R.id.gentleman);
//        mOvercoat = (RadioButton) v.findViewById(R.id.overcoat);
//        mSleeved = (RadioButton) v.findViewById(R.id.sleeved);
        mTrousers = (RadioButton) v.findViewById(R.id.trousers);
        mSeason = (RadioGroup) v.findViewById(R.id.style_season_group);
        mStyle = (RadioGroup) v.findViewById(R.id.style_form_group);
        mType = (RadioGroup) v.findViewById(R.id.style_type_group);
        mSituation = (RadioGroup) v.findViewById(R.id.style_situation_group);
        mPreviewView = (ImageView) v.findViewById(R.id.preview);
        mPreviewView.setOnClickListener(this);
        mUpload.setOnClickListener(this);
        
        Bundle b = getArguments();
        mInfo = (ClothesInfo) b.getSerializable("info");
        if (mInfo != null) {
	        mSeason.check(SEASON_IDS[mInfo.mSeason.ordinal()]);
	        mStyle.check(STYLE_IDS[mInfo.mStyle.ordinal()]);
	        mType.check(TYPE_IDS[mInfo.mType.ordinal()]);
	        mSituation.check(SITUATION_IDS[mInfo.mSituation.ordinal()]);
	        
	        final File file = new File(mInfo.mMediaPath);
	        if (file.exists()) {
	        	Uri uri = Uri.fromFile(file);
	        	mImagePath = mInfo.mMediaPath;
	        	performPreview(uri);
	        	mPreviewView.setClickable(false);
	        	mUpload.setText(R.string.modify);
	        }
	        mModify = true;
        }
        
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    private void performPreview(Uri uri) {
      if (mImagePath == null) {
          Toast.makeText(getActivity(), R.string.toast_null_preview, Toast.LENGTH_LONG).show();
          return;
      }
      Options op = new Options();
      op.inSampleSize = 2;
      Bitmap bm = BitmapFactory.decodeFile(mImagePath,op);
      mPreviewView.setImageBitmap(bm);
    }
    
    private void performUpload() {
        int styleId = mStyle.getCheckedRadioButtonId();
        Style style = null;
        switch (styleId) {
        case R.id.gentleman:
            style = Style.GENTLEMAN;
            break;
        case R.id.leisure:
            style = Style.LEISURE;
            break;
        case R.id.business:
            style = Style.BUSINESS;
            break;
        case R.id.fashion:
            style = Style.FASHION;
            break;
        default:
            throw new IllegalArgumentException("style id not found : "+Integer.toHexString(styleId));
        }
        int seasonId = mSeason.getCheckedRadioButtonId();
        Season season;
        switch (seasonId) {
        case R.id.spring:
            season = Season.SPRING;
            break;
        case R.id.summer:
            season = Season.SUMMER;
            break;
        case R.id.autumn:
            season = Season.AUTUMN;
            break;
        case R.id.winter:
            season = Season.WINTER;
            break;
        default:
            throw new IllegalArgumentException("season id not found :"+Integer.toHexString(seasonId));
        }
        
        int situationId = mSituation.getCheckedRadioButtonId();
        Situation situation = null;
        switch (situationId) {
        case R.id.public_place:
            situation = Situation.PUBLIC;
            break;
        case R.id.cocktail:
            situation = Situation.COCKTAIL;
            break;
        case R.id.office:
            situation = Situation.OFFICE;
            break;

        default:
            throw new IllegalArgumentException("situation id not found :"+Integer.toHexString(situationId));
        }
        
        int typeId = mType.getCheckedRadioButtonId();
        Type type = null;
        switch (typeId) {
        case R.id.sleeved:
            type = Type.SLEEVED;
            break;
        case R.id.trousers:
        	type = Type.TROUSERS;
            break;
        case R.id.overcoat:
        	type = Type.OVERCOAT;
            break;

        default:
            throw new IllegalArgumentException("typeId id not found :"+Integer.toHexString(situationId));
        }
        
        
        ClothesInfo info = new ClothesInfo(style,season,situation,type);
        info.mId = mInfo.mId;
        info.mSynServerId = mInfo.mSynServerId;

        if (mModify) {
        	mPostController.modify(info);
        	return;
        }
//        mPostController.uploadFile(mImagePath,info);
        if (mImagePath == null) return;
        File f = new File(mImagePath);
        if (f.exists()) {
            mPostController.uploadFile(f, info);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.preview:
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, 0);
            break;
        case R.id.upload:
            performUpload();
            break;

        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            mImagePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        performPreview(uri);
    }
}
