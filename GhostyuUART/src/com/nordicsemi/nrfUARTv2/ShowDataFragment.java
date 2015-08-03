package com.nordicsemi.nrfUARTv2;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowDataFragment extends Fragment {
	
	private SimpleAdapter mAdapter;
	private ListView mListView;
	
	private static final String[] TITLES = new String[]{
		"通道1","通道2","通道3","通道4",
		"通道5","通道6","通道7","通道10",
		"通道9","通道10","通道11","通道12",
		"通道13","通道14","通道15","通道16",
		"充电电压", "电池电压" ,"" ,""
	};
	
	private ArrayList<HashMap<String,Object>> mDataList = new ArrayList<HashMap<String,Object>>();

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
		View view = inflater.inflate(R.layout.show_data, container,false);
		mListView = (ListView) view.findViewById(R.id.list);
		mAdapter = new SimpleAdapter(getActivity(), mDataList,
				R.layout.show_data_item, new String[] { "title", "unit" },
				new int[] { R.id.data_part, R.id.unit_part });
		mListView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setData(SparseArray<byte[]> showCache) {
		Utils.log("showCache : " + showCache);
		int start = 0x800;
		mDataList.clear();
		if (showCache.size() == 0) {
			for (int i=start;i< start + 19;i++) {
				showCache.put(i, new byte[]{0});
			}
		}
		
		for (int i=0;i<showCache.size();i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			byte[] datas = showCache.get(start + i);
//			byte[] dataPart = Utils.getDataPart(datas);
			if (datas == null) {
				continue;
			}
			if (datas.length > 4) {
				Utils.log("warnning : data length must be less than 4.");
			}
			int value = Utils.toInteger(datas);
			map.put("title", TITLES[i] + " : "+ value);
			String unit = "";
			if (i== 16 || i==17) {
				unit="0.1V";
			}
			map.put("unit",unit);
			mDataList.add(map);
		}
	}
	
	

}
