package com.bs.clothesroom;

import com.bs.clothesroom.provider.ClothesInfo.Type;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class Rack extends GeneralFragment implements OnClickListener {
    
    private TextView mSleeveds;
    private TextView mTrousers;
    private TextView mOvercoats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rack, container,false);
        mSleeveds = (TextView) v.findViewById(R.id.sleeved_gallery);
        mTrousers = (TextView) v.findViewById(R.id.trousers_gallery);
        mOvercoats = (TextView) v.findViewById(R.id.overcoat_gallery);
        
        mSleeveds.setOnClickListener(this);
        mTrousers.setOnClickListener(this);
        mOvercoats.setOnClickListener(this);
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
        String type = null;
        switch(v.getId()) {
        case R.id.sleeved_gallery :
            type = Type.SLEEVED.name();
            break;
        case R.id.trousers_gallery :
            type = Type.TROUSERS.name();
            break;
        case R.id.overcoat_gallery :
            type = Type.OVERCOAT.name();
            break;
        default:
            throw new IllegalArgumentException("Unkown view id : "+v.getId());
        }
        Bundle b = new Bundle();
        b.putString("type",type);
        GeneralActivity.openRack(getActivity(), b);
    }
    
    


}
