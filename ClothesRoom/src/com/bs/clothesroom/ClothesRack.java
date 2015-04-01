package com.bs.clothesroom;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ClothesRack extends GridFragment {
    
	private String mType = null;
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		 Bundle args = getArguments();
		 mType = args.getString("type");
		mGridView.setOnCreateContextMenuListener(this);
	}
	
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
			
		case 2:
			
		case 3:
			break;

		default:
			throw new IllegalArgumentException("unkown item id :"+item.getItemId());
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 1, Menu.NONE, getString(R.string.open));
		menu.add(0, 2, Menu.NONE, getString(R.string.delete));
		menu.add(0, 3, Menu.NONE, getString(R.string.try_it));
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}



	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rack, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_upload:
        	Bundle b = new Bundle();
        	b.putString("type", mType);
        	GeneralActivity.upload(getActivity(),b);
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle b) {
       
        String userId = Preferences.getUsername(getActivity());
        if (mType == null) return null;
        return ClothesInfo.createTypeCursorLoader(getActivity(), userId, mType);
    }

    @Override
    public void sync() {
    	String userId = Preferences.getUsername(getActivity());
		mPostController.fetchImageIds(userId);
    }

}
