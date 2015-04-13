package com.bs.clothesroom;

import java.io.File;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ClothesRack extends GridFragment {
    
	private String mType = null;
	Holder mHolder;
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		 Bundle args = getArguments();
		 mType = args.getString("type");
//		mGridView.setOnCreateContextMenuListener(this);
		registerForContextMenu(mGridView);
	}
	
    @Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Holder holder = (Holder) info.targetView.getTag();
    	switch (item.getItemId()) {
		case 1:
			openMedia(holder);
		case 2:
			deleteMedia(holder);
		case 3:
//		    dressVirtual(holder);
			break;
		case 4:
			mediaDetails(holder);
			break;
		default:
			throw new IllegalArgumentException("unkown item id :"+item.getItemId());
		}
		return super.onContextItemSelected(item);
	}

    private void mediaDetails(Holder holder) {
    	if (holder == null) return;
    	final ClothesInfo info = new ClothesInfo(holder.c);
    	StringBuilder sbuilder = new StringBuilder();
    	final String SPLITE = " : ";
    	final String ENTER = "\r\n";
    	sbuilder.append(getString(R.string.sp_label_season))
    			.append(SPLITE)
    			.append(info.mSeason.name2(getActivity()))
    			.append(ENTER)
    			.append(getString(R.string.sp_label_situation))
    			.append(SPLITE)
    			.append(info.mSituation.name2(getActivity()))
    			.append(ENTER)
    			.append(getString(R.string.sp_label_style))
    			.append(SPLITE)
    			.append(info.mStyle.name2(getActivity()))
    			.append(ENTER)
    			.append(getString(R.string.sp_label_type))
    			.append(SPLITE)
    			.append(info.mType.name2(getActivity()));
    	AlertDialog.Builder builder = new Builder(getActivity())
    	.setTitle(info.mMediaName)
    	.setMessage(sbuilder.toString())
    	.setPositiveButton(android.R.string.ok, null)
		.setNegativeButton(R.string.modify, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Bundle b = new Bundle();
				b.putSerializable("info", info);
				GeneralActivity.modify(getActivity(), b);
			}
		});
    	builder.create().show();
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 1, Menu.NONE, getString(R.string.open));
		menu.add(0, 2, Menu.NONE, getString(R.string.delete));
		menu.add(0, 3, Menu.NONE, getString(R.string.try_it));
		menu.add(0, 4, Menu.NONE, getString(R.string.details));
		mHolder = (Holder) v.getTag();
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
        getLoaderManager().restartLoader(0, null, this);
    	String userId = Preferences.getUsername(getActivity());
		mPostController.fetchImageIds(userId);
    }

}
