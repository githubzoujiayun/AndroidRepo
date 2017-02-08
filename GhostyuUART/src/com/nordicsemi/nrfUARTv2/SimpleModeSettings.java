package com.nordicsemi.nrfUARTv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.nordicsemi.nrfUARTv2.utils.Ini;

import java.text.DecimalFormat;

/**
 * @author chao.qin
 * @since 2017/1/9
 */
@LayoutID(R.layout.simple_mode_fragment)
public class SimpleModeSettings extends GeneralFragment implements ISimpleMode, View.OnClickListener {


    private GridView mGridView;
    private TextView mLocalTimeView;
    private Button mRefreshTimeBtn;
    private Button mShowDataBtn;
    private Button mSendReportBtn;
    private TextView mStationView;
//    private SparseArray<byte[]> mShowCache = new SparseArray<byte[]>();

    private static final String[] TITLES = new String[]{
            "仪器", "仪器", "仪器", "仪器",
            "仪器", "仪器", "仪器", "仪器",
            "仪器", "仪器", "仪器", "仪器",
            "仪器", "仪器", "仪器", "仪器",
            "充电电压", "电池电压", "", ""
    };

    private static final String[] CHANNELS = new String[]{
            "通道1", "通道2", "通道3", "通道4",
            "通道5", "通道6", "通道7", "通道8",
            "通道9", "通道10", "通道11", "通道12",
            "通道13", "通道14", "通道15", "通道16",
            "充电电压", "电池电压", "", ""
    };

    public static final int CHANNEL_LENGTH = 16;

    DataHolder[] mHolders = new DataHolder[CHANNEL_LENGTH + 2];


    @Override
    public void showLocalTime(String summary) {
        mLocalTimeView.setText(summary);
    }

    @Override
    public void onDataReciver(String action, Intent intent) {
        if (UartService.ACTION_GATT_CONNECTED.equals(action)) {
            String deviceName = intent.getStringExtra("deviceName");
//            Drawable d = getActivity().getResources().getDrawable(R.drawable.bt_connect_status);
            mStationView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bt_connect_status,0,0,0);
            mStationView.setText(deviceName.replaceFirst("RTU Station","站号"));
        } else if (UartService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mStationView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.bt_disconnect_status,0,0,0);
            mStationView.setText(R.string.bt_disconnected);
        } else if (UartService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_LOCAL_TIME);
            new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_SHOW_DATAS);
        }
    }


    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mHolders.length;
        }

        @Override
        public DataHolder getItem(int position) {
            return mHolders[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView != null) {
                textView = (TextView) convertView;
            } else {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                textView = (TextView) inflater.inflate(R.layout.simple_mode_grid_item, null);
            }
            DataHolder holder = getItem(position);
            String title = holder.getTitle();
            int value = holder.value;
            boolean isV = holder.isV();
            int drawableId = R.drawable.channel_shift_off;
            if (value != 0xffffffee) {
                drawableId = R.drawable.channel_shift_on;
            }
            if (isV) {
                drawableId = R.drawable.ic_voltage;
            }

            textView.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
            textView.setText(title);
            return textView;
        }
    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);

        DataManager.getInstance(getActivity()).service_init();

        GridAdapter adapter = new GridAdapter();
        setupDate(null);
        mGridView = findViewById(R.id.gridview);
        mGridView.setAdapter(adapter);


        mLocalTimeView = findViewById(R.id.local_refresh);

        mRefreshTimeBtn = findViewById(R.id.simple_mode_refresh_time);
        mShowDataBtn = findViewById(R.id.simple_mode_refresh_channels);
        mStationView = findViewById(R.id.ble_station_name);
        mStationView.setOnClickListener(this);
        mSendReportBtn = findViewById(R.id.simple_mode_send_test_report);

        mLocalTimeView.setOnClickListener(this);
        mRefreshTimeBtn.setOnClickListener(this);
        mShowDataBtn.setOnClickListener(this);
        mSendReportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_refresh:
                new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_LOCAL_TIME);
                break;
            case R.id.simple_mode_refresh_channels:
                new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_SHOW_DATAS);
                break;
            case R.id.simple_mode_refresh_time:
                new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_REFRESH_TIME);
                break;
            case R.id.simple_mode_send_test_report:
                new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_SEND_REPORT);
                break;
            case R.id.ble_station_name:
                if (!DataManager.getInstance(getActivity()).isConnected()) {
                    return;
                }
                String stationName[] = mStationView.getText().toString().split(":");
                String oldStation = "";
                if (stationName.length == 2) {
                    oldStation = stationName[1];
                }
                final EditText editText = new EditText(getActivity());
                editText.setSingleLine();
                editText.setTextSize(16);
                editText.setText(oldStation);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.simple_mode_alert_set_station_no)
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .setView(editText)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String no = editText.getText().toString();
                                if (TextUtils.isEmpty(no)) {
                                    no = "0";
                                }
                                int stationNo = Integer.parseInt(no);
                                new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_WRITE_STATION,stationNo);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel,null)
                        .create();
                alertDialog.show();
                break;
        }
    }

    private class DataHolder {
        int no; //代号
        int value;
        byte[] sections;
        int index;


        private String name;
        private String hexNo;
        private String unit;
        public String defaultName;
        private Number realValue;

        public boolean isV() {
            return index >= CHANNEL_LENGTH;
        }

        private void build() {
            hexNo = Integer.toHexString(no).toUpperCase();
            if ("0".equals(hexNo)) {
                hexNo = "";
            }
            String unitIndex = "0";
            int dotIndex = 0;
            if (sections != null) {
                unitIndex = Utils.toHexString(sections, 1, 1);
                dotIndex = Utils.toInteger(sections[2]) >> 4;
            }
            unit = Ini.getValue(Ini.SECTION_UNIT, unitIndex,"");
            name = Ini.getValue(Ini.SECTION_CHANNEL,hexNo,"");
            if (dotIndex == 0 || dotIndex > 6) {
                realValue = value;
            } else {
                realValue = ((double)value) / Math.pow(10,dotIndex);
                DecimalFormat decimalFormat = new DecimalFormat(".######");
                realValue = Double.parseDouble(decimalFormat.format((realValue))) ;
            }
            if (TextUtils.isEmpty(name)) {
                name = defaultName;
            }
            if (isV()) {
                unit = "V";
            }
        }

        public String getTitle() {
            build();
            double valueDouble = -1;
            String v = String.valueOf(realValue);
            if (isV()) {
                DecimalFormat decimalFormat = new DecimalFormat(".#");
                valueDouble = Double.parseDouble(decimalFormat.format(((double) value)/10)) ;
                v = String.valueOf(valueDouble);
            }
            if (value == 0xffffffff) {
                v = getStringSafely(R.string.tennal_failed);
                return name + hexNo + ": " + v;
            } else if (value == 0xffffffee) {
                v = getStringSafely(R.string.tennal_closed);
                return name +  ": " + v;
            }
            return name + hexNo + ": " + v + unit;
        }
    }

    private void setupDate(SparseArray<byte[]> showCache) {


        //读取代号
        final int noStart = 0x2a; //42 - 57
        final int valueStart = 0x800; //
        final int unitStart = 0xba;  //186 - 201

        if (showCache == null) {
            showCache = new SparseArray<byte[]>();
        }

        for (int i=0; i< CHANNEL_LENGTH;i++) {
            mHolders[i] = new DataHolder();
            mHolders[i].defaultName = TITLES[i];
            mHolders[i].no = Utils.toInteger(showCache.get(noStart + i));
            mHolders[i].value = Utils.toInteger(showCache.get(valueStart + i));
            if (mHolders[i].value == 0xffffffee) {
                mHolders[i].defaultName = CHANNELS[i];
            }
            mHolders[i].sections = showCache.get(unitStart + i);
        }

        for(int i= 0;i < 2;i++) {
            int index = CHANNEL_LENGTH + i;
            mHolders[index] = new DataHolder();
            mHolders[index].index = CHANNEL_LENGTH + i;
            mHolders[index].defaultName = TITLES[index];
            mHolders[index].value = Utils.toInteger(showCache.get(0x810 + i));

        }

//
//        if (showCache.size() == 0) {
//            for (int i = start; i < start + registerLength; i++) {
//                if (i == start) {
//                    showCache.put(i, Utils.toHexBytes("ffffffff"));
//                } else if (i == start + 1) {
//                    showCache.put(i, Utils.toHexBytes("ffffffee"));
//                } else {
//                    showCache.put(i, new byte[]{0});
//                }
//            }
//        }

//        length = showCache.size() - 2;// 812,813 is null;
//        for (int i = 0; i < length; i++) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            byte[] datas = showCache.get(start + i);
////			byte[] dataPart = Utils.getDataPart(datas);
////			if (datas == null) {
////				continue;
////			}
//            if (datas.length > 4) {
//                Utils.log("warnning : data length must be less than 4.");
//            }
//            int value = Utils.toInteger(datas);
//            map.put("title", TITLES[i] + " : " + value);
//            map.put("value",value);
//            map.put("v",false);
//            String unit = "";
//            if (i >= 0 && i <= 15) {
//                if (value == 0xffffffff) {
//                    map.put("title", TITLES[i] + " : " + getString(R.string.tennal_failed));
//                } else if (value == 0xffffffee) {
//                    map.put("title", TITLES[i] + " : " + getString(R.string.tennal_closed));
//                }
//            } else if (i == 16 || i == 17) {
////				unit="0.1V";
//                map.put("v",true);
//                map.put("title", TITLES[i] + " : " + (value < 0 ? value : (value * 0.1)) + "V");
//            }
//            map.put("summary", unit);
//        }
    }

    public void setData(SparseArray<byte[]> showCache) {
        Utils.log("showCache : " + Utils.map2String(showCache));
        setupDate(showCache);
        mGridView.invalidateViews();
    }
}
