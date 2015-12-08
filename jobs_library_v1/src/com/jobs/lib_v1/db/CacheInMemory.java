package com.jobs.lib_v1.db;

import java.util.HashMap;

/**
 * 内存缓存对象：在数据库无法读写时起作用
 */
public class CacheInMemory {
	private final HashMap<String, HashMap<String, Object>> cache = new HashMap<String, HashMap<String,Object>>();

	private synchronized HashMap<String,Object> getDataType(String dataType){
		if(null == dataType){
			return new HashMap<String, Object>();
		}

		if (!cache.containsKey(dataType)) {
			cache.put(dataType, new HashMap<String, Object>());
		}

		return cache.get(dataType);
	}

	public void setString(String dataType, String dataKey, String dataValue){
		if(null == dataKey || null == dataValue){
			return;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		synchronized(this){
			cacheType.put("string::"+dataKey, dataValue);
		}
	}

	public long setInt(String dataType, String dataKey, long dataValue){
		if(null == dataKey){
			return 0;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		synchronized(this){
			Long inputValue = dataValue;
			cacheType.put("int::"+dataKey, inputValue);
		}
		
		return dataValue;
	}

	public long setBytes(String dataType, String dataKey, byte[] dataValue){
		if(null == dataKey || null == dataValue){
			return 0;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);
		synchronized(this){
			cacheType.put("bytes::"+dataKey, dataValue.clone());
		}
		
		return 1;
	}

	public String getString(String dataType, String dataKey){
		String dataValue = "";

		if(null == dataKey || null == dataType){
			return dataValue;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		synchronized(this){
			String cacheKey = "string::" + dataKey;
			if(cacheType.containsKey(cacheKey)){
				Object tmpValue = cacheType.get(cacheKey);

				if(tmpValue instanceof String){
					dataValue = (String)tmpValue;
				}
			}
		}
		
		return dataValue;
	}

	public long getInt(String dataType, String dataKey){
		long dataValue = 0;

		if(null == dataKey || null == dataType){
			return dataValue;
		}

		HashMap<String, Object> cacheType = getDataType(dataType);

		synchronized(this){
			String cacheKey = "int::" + dataKey;
			if(cacheType.containsKey(cacheKey)){
				Object tmpValue = cacheType.get(cacheKey);

				if(tmpValue instanceof Long){
					dataValue = (Long)tmpValue;
				}
			}
		}

		return dataValue;
	}

	public byte[] getBytes(String dataType, String dataKey){
		byte[] dataValue = null;

		if(null == dataKey || null == dataType){
			return dataValue;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		synchronized(this){
			String cacheKey = "bytes::" + dataKey;
			if(cacheType.containsKey(cacheKey)){
				Object tmpValue = cacheType.get(cacheKey);

				if(tmpValue instanceof byte[]){
					dataValue = ((byte[])tmpValue).clone();
				}
			}
		}
	
		return dataValue;
	}

	public boolean hasString(String dataType, String dataKey){
		if(null == dataKey || null == dataType){
			return false;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		return cacheType.containsKey("string::" + dataKey);
	}

	public boolean hasInt(String dataType, String dataKey){
		if(null == dataKey || null == dataType){
			return false;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		return cacheType.containsKey("int::" + dataKey);
	}

	public boolean hasBytes(String dataType, String dataKey){
		if(null == dataKey || null == dataType){
			return false;
		}

		HashMap<String,Object> cacheType = getDataType(dataType);

		return cacheType.containsKey("bytes::" + dataKey);
	}
}
