package com.bs.clothesroom;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.ClothesInfo;
import com.bs.clothesroom.provider.ClothesInfo.ImageInfo;

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
		    dressVirtual(holder);
			break;
		case 4:
			mediaDetails(holder);
			break;
		default:
			throw new IllegalArgumentException("unkown item id :"+item.getItemId());
		}
		return super.onContextItemSelected(item);
	}
    

    private void dressVirtual(Holder holder) {
        if (holder == null) return;
        final ImageInfo info = new ImageInfo(holder.c);
//        String videoIds[] = info.mRelativeVideoIds.split("v");
//        log("videoIds = "+videoIds);
//        ContentResolver resolver = getActivity().getContentResolver();
//        String userId = Preferences.getUsername(getActivity());
//        for (String id:videoIds) {
//        	int sid = Integer.parseInt(id);
//        	ClothesInfo videoInfo = ClothesInfo.getVideoInfoBySID(resolver, sid, userId);
//        	
//        }
        Bundle b = new Bundle();
        b.putString("videoIds", info.mRelativeVideoIds);
        replaceFragment(VituralSearchResult.class, b, R.id.fragment);
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
