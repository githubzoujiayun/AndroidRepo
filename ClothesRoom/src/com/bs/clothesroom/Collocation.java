package com.bs.clothesroom;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;
import com.bs.clothesroom.provider.ClothesInfo.ImageInfo;
import com.bs.clothesroom.provider.ClothesInfo.Season;
import com.bs.clothesroom.provider.ClothesInfo.Situation;
import com.bs.clothesroom.provider.ClothesInfo.Style;
import com.bs.clothesroom.provider.ClothesInfo.Type;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;


public class Collocation extends GeneralFragment implements OnClickListener {
    
    private Spinner mSeason;
    private Spinner mSituation;
    private Spinner mStyle;
    private Spinner mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.collocation, container,false);
        mSeason = (Spinner) v.findViewById(R.id.sp_season);
        mSituation = (Spinner) v.findViewById(R.id.sp_situation);
        mStyle = (Spinner) v.findViewById(R.id.sp_style);
        mType = (Spinner) v.findViewById(R.id.sp_type);
        v.findViewById(R.id.find).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.find) {
            Season season = Season.valueAt(mSeason.getSelectedItemPosition());
            Style style = Style.valueAt(mStyle.getSelectedItemPosition());
            Type type = Type.valueAt(mType.getSelectedItemPosition());
//            Situation situation = Situation.valueAt(mSituation.getSelectedItemPosition());
            String userId = Preferences.getUsername(getActivity());
            ImageInfo images[] = ClothesInfo.getImageInfoArgs(getActivity()
                    .getContentResolver(), userId, season, style, type);
            
            openFragment(R.id.grid_fragment, GridFragment.class, null, "find_grid");
        }
    }

}
