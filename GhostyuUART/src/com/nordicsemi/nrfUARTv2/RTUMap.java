package com.nordicsemi.nrfUARTv2;

import java.util.HashMap;

public class RTUMap extends HashMap<String, RTU>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void put(String key,int address, String unit,int unitMessure) {
		put(key,new RTU(address,unit,unitMessure));
	}
	
	public void put(String key,int address, String unit,double messure) {
		put(key,new RTU(address,unit,messure));
	}
	
	public void put(String key,int address, String unit) {
		put(key,new RTU(address,unit));
	}
	
	public void put(String key, int address, int max, int min) {
		put(key,new RTU(address,max,min));
	}

	public void put(String key,int address) {
		put(key, new RTU(address));
	}
	
	public int getAddress(String key) {
		return get(key).getAddress();
	}
}
