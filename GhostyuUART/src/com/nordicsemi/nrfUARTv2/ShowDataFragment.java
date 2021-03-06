package com.nordicsemi.nrfUARTv2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowDataFragment extends Fragment {
	
	private SimpleAdapter mAdapter;
	private ListView mListView;
	
	private static final String[] TITLES = new String[]{
		"通道1","通道2","通道3","通道4",
		"通道5","通道6","通道7","通道8",
		"通道9","通道10","通道11","通道12",
		"通道13","通道14","通道15","通道16",
		"充电电压", "电池电压" ,"" ,""
	};

	public static void show(Context context) {
		Intent intent = new Intent(context,FragmentContainer.class);
		intent.putExtra(FragmentContainer.KEY_FRAGMENT,ShowDataFragment.class);
//		intent.putExtra()
		context.startActivity(intent);
	}

	
	private ArrayList<HashMap<String,Object>> mDataList = new ArrayList<HashMap<String,Object>>();
	private SparseArray<byte[]> mShowCache;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		View view = inflater.inflate(R.layout.show_data, container,false);
		mListView = (ListView) view.findViewById(R.id.list);
		setupDate();
		mAdapter = new SimpleAdapter(getActivity(), mDataList,
				R.layout.preference_item, new String[] { "title", "summary" },
				new int[] { R.id.title, R.id.summary });
		mListView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	private void setupDate() {
		final SparseArray<byte[]> showCache = mShowCache;
		int start = 0x800;
		mDataList.clear();
		if (showCache.size() == 0) {
			for (int i = start; i < start + 20; i++) {
				if (i == start) {
					showCache.put(i, Utils.toHexBytes("ffffffff"));
				} else if (i == start + 1) {
					showCache.put(i, Utils.toHexBytes("ffffffee"));
				} else {
					showCache.put(i, new byte[]{0});
				}
			}
		}
		
		int length = showCache.size() - 2;// 812,813 is null;
		for (int i=0;i< length ;i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			byte[] datas = showCache.get(start + i);
//			byte[] dataPart = Utils.getDataPart(datas);
//			if (datas == null) {
//				continue;
//			}
			if (datas.length > 4) {
				Utils.log("warnning : data length must be less than 4.");
			}
			int value = Utils.toInteger(datas);
			map.put("title", TITLES[i] + " : "+ value);
			String unit = "";
			if (i >= 0 && i <= 15) {
				if (value == 0xffffffff) {
					map.put("title", TITLES[i] + " : " + getString(R.string.tennal_failed));
				} else if (value == 0xffffffee) {
					map.put("title", TITLES[i] + " : " + getString(R.string.tennal_closed));
				}
			} else if (i== 16 || i==17) {
//				unit="0.1V";
				map.put("title", TITLES[i] + " : " + (value < 0?value:(value * 0.1)) + "V");
			}
			map.put("summary",unit);
			mDataList.add(map);
		}		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setData(SparseArray<byte[]> showCache) {
		Utils.log("showCache : " + showCache);
		mShowCache = showCache;
	}
	
	

}
