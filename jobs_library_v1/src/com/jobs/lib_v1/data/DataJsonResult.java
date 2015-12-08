package com.jobs.lib_v1.data;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jobs.lib_v1.app.AppException;
import com.jobs.lib_v1.app.AppUtil;

/**
 * 根据接口规则的定制 JSONObject 结构
 * 
 * @author xmwen
 * @date 2013-05-24
 */
public class DataJsonResult extends JSONObject {
	public DataJsonResult() {
		super();
	}

	public DataJsonResult(String source) throws JSONException {
		super(source);
	}

	public int getStatusCode() {
		return getInt("status");
	}

	public DataJsonResult setStatusCode(int value) {
		try {
			put("status", value);
		} catch (JSONException e) {
			AppUtil.print(e);
		}
		return this;
	}

	public int getResultCode() {
		return getInt("result");
	}

	public boolean getHasError() {
		return !getBoolean("result");
	}

	public DataJsonResult setHasError(boolean value) {
		try {
			put("result", !value);
		} catch (JSONException e) {
			AppUtil.print(e);
		}

		return this;
	}

	public String getErrorStack() {
		return getString("errorStack");
	}

	public DataJsonResult setErrorStack(String value) {
		try {
			put("errorStack", value);
		} catch (JSONException e) {
			AppUtil.print(e);
		}
		return this;
	}

	public void errorRecord(Throwable e) {
		setErrorStack(AppException.getExceptionStackInfo(e));
	}

	public String getMessage() {
		return getString("message");
	}

	public DataJsonResult setMessage(String value) {
		try {
			put("message", value);
		} catch (JSONException e) {
			AppUtil.print(e);
		}
		return this;
	}

	public int getMaxCount() {
		return getInt("totalcount");
	}

	public DataJsonResult setMaxCount(int value) {
		try {
			put("totalcount", value);
		} catch (JSONException e) {
			AppUtil.print(e);
		}
		return this;
	}

	public boolean getParseError() {
		return getBoolean("parseError");
	}

	public DataJsonResult setParseError(boolean value) {
		try {
			put("parseError", value);
		} catch (JSONException e) {
			AppUtil.print(e);
		}
		return this;
	}

	@Override
	public int getInt(String name) {
		try {
			return super.optInt(name, 0);
		} catch (Throwable e) {
			return 0;
		}
	}

	public String getString(String name) {
		try {
			Object val = opt(name);

			if (null != val && val instanceof String) {
				return (String) val;
			} else if(val instanceof Long){
				return String.valueOf((Long)val);
			} else if(val instanceof Integer){
				return String.valueOf((Integer)val);
			} else if(val instanceof Boolean){
				return String.valueOf((Boolean)val);
			} else if(val instanceof Double){
				return String.valueOf((Double)val);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return "";
	}

	@Override
	public boolean getBoolean(String name) {
		try {
			Object val = opt(name);

			if (null != val) {
				if (val instanceof Boolean) {
					return (Boolean) val;
				} else if (val instanceof String) {
					if ("true".equalsIgnoreCase((String) val)) {
						return true;
					} else if ("1".equalsIgnoreCase((String) val)) {
						return true;
					}

					return false;
				} else if (val instanceof Integer) {
					return (0 != (Integer) val);
				}
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return false;
	}

	/**
	 * 转成 DataItemDetail 对象，会丢弃列表内容
	 * 
	 * @return DataItemDetail
	 */
	public DataItemDetail toDataItemDetail() {
		DataItemDetail detail = new DataItemDetail();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIter = keys();

		while (keyIter.hasNext()) {
			String key = keyIter.next();
			Object value = opt(key);

			if (value instanceof String) {
				detail.setStringValue(key, (String) value);
			} else if (value instanceof Integer) {
				detail.setStringValue(key, String.valueOf((Integer) value));
			} else if (value instanceof Long) {
				detail.setStringValue(key, String.valueOf((Long) value));
			} else if (value instanceof Boolean) {
				detail.setStringValue(key, (Boolean) value ? "1" : "0");
			} else if (value instanceof Double) {
				detail.setStringValue(key, String.valueOf((Double) value));
			}
		}

		return detail;
	}

	/**
	 * 转成 DataItemResult 对象，会丢弃items之外的列表内容
	 * 
	 * @return DataItemResult
	 */
	public DataItemResult toDataItemResult() {
		DataItemResult result = new DataItemResult();

		result.maxCount = getMaxCount();
		result.hasError = getHasError();
		result.message = getMessage();
		result.statusCode = getStatusCode();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIter = keys();

		while (keyIter.hasNext()) {
			String key = keyIter.next();
			Object value = opt(key);

			if (value instanceof String) {
				result.detailInfo.setStringValue(key, (String) value);
			} else if (value instanceof Integer) {
				result.detailInfo.setStringValue(key, String.valueOf((Integer) value));
			} else if (value instanceof Long) {
				result.detailInfo.setStringValue(key, String.valueOf((Long) value));
			} else if (value instanceof Boolean) {
				result.detailInfo.setStringValue(key, (Boolean) value ? "1" : "0");
			} else if (value instanceof Double) {
				result.detailInfo.setStringValue(key, String.valueOf((Double) value));
			}
		}

		JSONArray items = optJSONArray("items");
		if (null != items) {
			int itemsLength = items.length();
			for (int i = 0; i < itemsLength; i++) {
				Object value = items.opt(i);

				if (null == value) {
					continue;
				}

				DataItemDetail item = new DataItemDetail();

				if (value instanceof String) {
					item.setItemText((String) value);
				} else if (value instanceof Integer) {
					item.setItemText(String.valueOf((Integer) value));
				} else if (value instanceof Long) {
					item.setItemText(String.valueOf((Long) value));
				} else if (value instanceof Boolean) {
					item.setItemText((Boolean) value ? "1" : "0");
				} else if (value instanceof Double) {
					item.setItemText(String.valueOf((Double) value));
				} else if (value instanceof JSONObject) {
					@SuppressWarnings("unchecked")
					Iterator<String> subKeyIter = ((JSONObject) value).keys();

					while (subKeyIter.hasNext()) {
						String tempKey = subKeyIter.next();
						Object tempValue = ((JSONObject) value).opt(tempKey);

						if (tempValue instanceof String) {
							item.setStringValue(tempKey, (String) tempValue);
						} else if (tempValue instanceof Integer) {
							item.setStringValue(tempKey, String.valueOf((Integer) tempValue));
						} else if (tempValue instanceof Long) {
							item.setStringValue(tempKey, String.valueOf((Long) tempValue));
						} else if (tempValue instanceof Boolean) {
							item.setStringValue(tempKey, (Boolean) tempValue ? "1" : "0");
						} else if (tempValue instanceof Double) {
							item.setStringValue(tempKey, String.valueOf((Double) tempValue));
						}
					}
				}

				result.addItem(item);
			}
		}

		return result;
	}
}
