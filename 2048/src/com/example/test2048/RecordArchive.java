package com.example.test2048;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test2048.GameView.GameTable;

public class RecordArchive extends ListActivity implements OnItemClickListener{

	private ListView mListView;
	private ListAdapter mAdapter;
	private GameManager mManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_archive);
		mManager = GameManager.getInstance(this);
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
		HashMap<String, GameTable> map = mManager.restoreGameTables();
//		Collection<GameTable> tables = map.values();
//		HashMap<String, Integer> adapterMap = new HashMap<String, Integer>();
//		adapterMap.put(key, value);
		mAdapter = new ViewListAdapter(this,map);
		mListView.setAdapter(mAdapter);
		
	}
	
	class ViewListAdapter extends BaseAdapter {

		Context mContext = null;
		ArrayList<GameTable> mTables = null;
		private LayoutInflater mInflater;
		public ViewListAdapter(RecordArchive recordArchive,
				HashMap<String, GameTable> map) {
			mContext = recordArchive;
			mInflater = LayoutInflater.from(mContext);
			mTables = new ArrayList<GameView.GameTable>(map.values());
		}

		@Override
		public int getCount() {
			return mTables.size();
		}

		@Override
		public Object getItem(int position) {
			return mTables.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.record_item, null);
				holder = new ViewHolder();
				holder.gameView = (GameView) convertView.findViewById(R.id.game_view);
				holder.textView = (TextView) convertView.findViewById(R.id.item_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.gameView.setTable(mTables.get(position));
//			holder.textView.setText(text);
			return convertView;
		}
	}
	
	class ViewHolder {
		GameView gameView;
		TextView textView;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

}
