package com.jobs.lib_v1.data;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.encoding.UrlEncode;

/**
 * 单条数据容器
 * 
 * 1.可存放 String - String 键值对 
 * 2.可存放 String - Integer 键值对 
 * 3.可存放 String - Boolean 键值对 
 * 4.支持序列化和反序列化 
 * 5.警告：不要随意给本类新增成员变量，否则会影响数据的兼容性； 
 * 6.警告：不要随意修改本类成员方法的返回值，否则会引起一系列出错的连锁反应
 */
public class DataItemDetail implements Parcelable {
	private Map<String, String> data;

	/**
	 * 构造函数，初始化容器哈希表
	 */
	public DataItemDetail() {
		this.data = new HashMap<String, String>();
	}

	/**
	 * 返回键值对的总数
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @return int
	 */
	public int getCount() {
		if (null == this.data) {
			return 0;
		}

		return this.data.size();
	}

	/**
	 * 获取属性字符串
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param attributeName
	 * @return String
	 */
	public String getAttributeString(String key, String attributeName) {
		if (key == null || key.length() < 1 || attributeName == null || attributeName.length() < 1) {
			return "";
		}

		return getString(key + "." + attributeName);
	}

	/**
	 * 把对象数据转为字节数组
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @return byte[]
	 */
	public byte[] toBytes() {
		byte[] data = null;

		try {
			Parcel out = Parcel.obtain();
			writeToParcel(out, 0);
			out.setDataPosition(0);

			data = out.marshall();
			out.recycle();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return data;
	}

	/**
	 * 把字节数据转换为 DataItemDetail 对象
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param bytesData 字节数据
	 * @return DataItemDetail 返回对象
	 */
	public static DataItemDetail fromBytes(byte[] bytesData) {
		if (null == bytesData) {
			return new DataItemDetail();
		}

		try {
			Parcel in = Parcel.obtain();

			in.unmarshall(bytesData, 0, bytesData.length);
			in.setDataPosition(0);

			return DataItemDetail.CREATOR.createFromParcel(in);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return new DataItemDetail();
	}

	/**
	 * 判断当前节点是否存在一个键名
	 * 
	 * @author solomon.wen
	 * @date 2012-12-05
	 * @param key
	 * @return Boolean
	 */
	public Boolean hasKey(String key) {
		if (key == null || key.length() < 1) {
			return false;
		}

		return data.containsKey(key);
	}

	/**
	 * 判断当前节点是否存在一个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param value
	 * @return Boolean
	 */
	public Boolean hasKeyValue(String key, String value) {
		if (key == null || key.length() < 1 || value == null) {
			return false;
		}

		if (!this.data.containsKey(key)) {
			return false;
		}

		return value.equals((String) this.data.get(key));
	}

	/**
	 * 获取属性 int 值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param attributeName
	 * @return int
	 */
	public int getAttributeInt(String key, String attributeName) {
		if (key == null || key.length() < 1 || attributeName == null || attributeName.length() < 1) {
			return 0;
		}

		return getInt(key + "." + attributeName);
	}

	/**
	 * 取得item节点默认文字
	 * 
	 * @author solomon.wen
	 * @date 2011-11-28
	 * @return String item文字
	 */
	public String getItemText() {
		return this.getString("text");
	}

	/**
	 * 设定 item 节点默认文字
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param value
	 * @return String
	 */
	public String setItemText(String value) {
		return this.setStringValue("text", value);
	}

	/**
	 * 取得节点默认的属性
	 * 
	 * @author solomon.wen
	 * @date 2011-11-28
	 * @param attributeName 属性名
	 * @return String 属性值
	 */
	public String getItemAttribute(String attributeName) {
		return this.getAttributeString("item", attributeName);
	}

	/**
	 * 获取属性 Boolean 值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param attributeName
	 * @return Boolean
	 */
	public Boolean getAttributeBoolean(String key, String attributeName) {
		if (key == null || key.length() < 1 || attributeName == null || attributeName.length() < 1) {
			return false;
		}

		return this.getBoolean(key + "." + attributeName);
	}

	/**
	 * 设定默认节点的一个属性值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param attributeName
	 * @param value
	 * @return String
	 */
	public String setItemAttributeValue(String attributeName, String value) {
		return this.setAttributeValue("item", attributeName, value);
	}

	/**
	 * 设定一个属性值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param attributeName
	 * @param value
	 * @return String
	 */
	public String setAttributeValue(String key, String attributeName, String value) {
		if (key == null || key.length() < 1 || attributeName == null || attributeName.length() < 1) {
			return null;
		}

		return setStringValue(key + "." + attributeName, value);
	}

	/** 设定一个属性的Boolean类型的值 **/
	public Boolean setAttributeBooleanValue(String key, String attributeName, Boolean value) {
		if (key == null || key.length() < 1 || attributeName == null || attributeName.length() < 1) {
			return false;
		}

		return setBooleanValue(key + "." + attributeName, value);
	}

	/** 通过键名取得一个String值 **/
	public String getString(String key) {
		if (key == null || key.length() < 1) {
			return "";
		}

		if (!this.data.containsKey(key)) {
			return "";
		}

        String value = (String) this.data.get(key);

        if (value == null) {//若key存在，value为null，调用trim（）可能会闪退 这里进行为空的判断 Oliver.chen 2015-7-6
            return "";
        }
        return value.trim();
    }

	/** 通过键名取得一个 Boolean 值 **/
	public Boolean getBoolean(String key) {
		if (key == null || key.length() < 1) {
			return false;
		}

		if (!this.data.containsKey(key)) {
			return false;
		}

		String value = (String) this.data.get(key);

		if (value.length() < 1) {
			return false;
		}

		return !value.equals("0") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
	}

	/** 通过键名取得一个 int 值 **/
	public int getInt(String key) {
		if (key == null || key.length() < 1) {
			return 0;
		}

		if (!this.data.containsKey(key)) {
			return 0;
		}

		String value = (String) this.data.get(key);
		int retValue;

		try {
			retValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			AppUtil.print(e);
			retValue = 0;
		}

		return retValue;
	}

	/**
	 * 设定一个值类型为Boolean的键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param key 键名
	 * @param value Boolean类型键值
	 * @return Boolean 如果成功返回 value值，可能为true也可能为false；如果失败则恒为false。
	 * @throws
	 */
	public Boolean setBooleanValue(String key, Boolean value) {
		if (key == null || key.length() < 1) {
			return false;
		}

		this.data.put(key, value ? "1" : "0");

		String res = (String) this.data.get(key);

		return (res != null && res.equals("1"));
	}

	/**
	 * 设定一个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param value
	 * @return int
	 */
	public int setIntValue(String key, int value) {
		if (key == null || key.length() < 1) {
			return 0;
		}

		String strValue = "" + value;

		this.data.put(key, strValue);

		if (!strValue.equals((String) this.data.get(key))) {
			return 0;
		}

		return value;
	}

	/**
	 * 设定一个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param key
	 * @param value
	 * @return String
	 */
	public String setStringValue(String key, String value) {
		if (key == null || key.length() < 1) {
			return null;
		}

		if (value == null) {
			value = "";
		}

		if (value.length() < 0) {
			return "";
		}

		this.data.put(key, value);

		return (String) this.data.get(key);
	}

	/**
	 * 从Map中导入所有键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @param map
	 * @return boolean
	 */
	public boolean importFromMap(Map<String, String> map) {
		if (map == null || map.size() < 1) {
			return false;
		}

		for (String key : map.keySet()) {
			this.data.put(key, map.get(key));
		}

		return true;
	}

	/**
	 * 为当前对象建立一份相同的副本
	 * 
	 * @author solomon.wen
	 * @date 2012-09-15
	 * @return DataItemDetail
	 */
	public DataItemDetail Copy() {
		DataItemDetail newCopy = new DataItemDetail();

		for (String key : data.keySet()) {
			newCopy.data.put(key, data.get(key));
		}

		return newCopy;
	}

	/**
	 * 构造参数字符串
	 * 
	 * @author solomon.wen
	 * @date 2012-09-15
	 * @return String
	 */
	public String toQueryParamString() {
		StringBuilder str = new StringBuilder();

		for (String key : data.keySet()) {
			if (str.length() > 1) {
				str.append("&");
			}

			str.append(UrlEncode.encode(key));
			str.append("=");
			str.append(UrlEncode.encode(data.get(key)));
		}

		return str.toString();
	}

	/**
	 * 判断当前对象是否和另一个对象相同
	 * 
	 * @author solomon.wen
	 * @date 2012-09-15
	 * @return boolean
	 */
	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}

		if (!(o instanceof DataItemDetail)) {
			return false;
		}

		DataItemDetail pO = (DataItemDetail) o;

		if (pO.data.size() != this.data.size()) {
			return false;
		}

		for (String key : data.keySet()) {
			if (!pO.data.containsKey(key)) {
				return false;
			}

			if (!pO.data.get(key).equals(data.get(key))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 得到当前键值对维护的哈希表
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @return Map<String,String>
	 */
	public Map<String, String> getAllData() {
		return this.data;
	}

	/**
	 * 清除所有元素
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 */
	public DataItemDetail clear() {
		this.data.clear();
		return this;
	}

	/**
	 * 从另一个 DataItemDetail 追加数据到本对象
	 * 
	 * @author solomon.wen
	 * @date 2012-09-12
	 */
	public DataItemDetail append(DataItemDetail item) {
		if(null != item){
			for (String key : item.data.keySet()) {
				data.put(key, item.data.get(key));
			}
		}

		return this;
	}

	/**
	 * 调试时使用，输出Map中所有元素
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 */
	public void Dump() {
		if (null == this) {
			return;
		}

		for (String key : data.keySet()) {
			String value = (String) data.get(key);
			Log.v("Dump", "  [" + key + "] => " + value);
		}
	}

	/**
	 * 序列化描述符，默认为0
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * 对象序列化函数
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		int itemCount = data.size() * 2;

		dest.writeInt(itemCount);

		for (String key : data.keySet()) {
			String value = (String) data.get(key);

			dest.writeString(key);
			dest.writeString(value);
		}
	}

	/**
	 * 对象反序列化构造容器
	 */
	public static final Parcelable.Creator<DataItemDetail> CREATOR = new Parcelable.Creator<DataItemDetail>() {
		public DataItemDetail createFromParcel(Parcel in) {
			return new DataItemDetail(in);
		}

		public DataItemDetail[] newArray(int size) {
			return new DataItemDetail[size];
		}
	};

	/**
	 * 对象反序列化函数
	 */
	public DataItemDetail(Parcel in) {
		this.data = new HashMap<String, String>();
		int itemCount = in.readInt() / 2;

		for (int i = 0; i < itemCount; i++) {
			String key = in.readString();
			String value = in.readString();

			this.data.put(key, value);
		}
	}

	/**
     * Removes the named mapping if it exists; does nothing otherwise.
     *
     * @return the value previously mapped by {@code name}, or null if there was
     *     no such mapping.
     */
    public String remove(String name) {
    	if(null == name){
    		return null;
    	}

    	return this.data.remove(name);
    }
}
