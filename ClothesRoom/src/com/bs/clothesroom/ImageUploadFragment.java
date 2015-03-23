package com.bs.clothesroom;


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
import com.bs.clothesroom.provider.ClothesInfo.Style;
import com.bs.clothesroom.provider.ClothesInfo.Type;

public class ImageUploadFragment extends GeneralFragment implements OnClickListener {
    
    Button mUpload;
    RadioGroup mSeason,mStyle,mType;
    RadioButton mLeisure,mBuesiness,mFashion,mGentleman;
    RadioButton mOvercoat,mSleeved,mTrousers;
    ImageView mPreviewView;
    
    private String mImagePath;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_upload,container,false);
        mUpload = (Button) v.findViewById(R.id.upload);
        mLeisure = (RadioButton) v.findViewById(R.id.leisure);
        mBuesiness = (RadioButton) v.findViewById(R.id.business);
        mFashion = (RadioButton) v.findViewById(R.id.fashion);
        mGentleman = (RadioButton) v.findViewById(R.id.gentleman);
        mOvercoat = (RadioButton) v.findViewById(R.id.overcoat);
        mSleeved = (RadioButton) v.findViewById(R.id.sleeved);
        mTrousers = (RadioButton) v.findViewById(R.id.trousers);
        mSeason = (RadioGroup) v.findViewById(R.id.style_season_group);
        mStyle = (RadioGroup) v.findViewById(R.id.style_form_group);
        mType = (RadioGroup) v.findViewById(R.id.style_type_group);
        mPreviewView = (ImageView) v.findViewById(R.id.preview);
        mPreviewView.setOnClickListener(this);
        mUpload.setOnClickListener(this);
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
            throw new IllegalArgumentException("style id not found : "+styleId);
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
        case R.id.winter:
            season = Season.WINTER;
        default:
            throw new IllegalArgumentException("season id not found :"+seasonId);
        }
        int typeId = mType.getCheckedRadioButtonId();
        Type type = null;
        switch (typeId) {
        case R.id.trousers:
            type = Type.TROUSERS;
            break;
        case R.id.overcoat:
            type = Type.OVERCOAT;
            break;
        case R.id.sleeved:
            type = Type.SLEEVED;
            break;
        default:
            throw new IllegalArgumentException("typeId id not found :"+typeId);
        }
        ClothesInfo info = new ClothesInfo(style,season,type);
        mPostController.uploadFile(mImagePath,info);
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
