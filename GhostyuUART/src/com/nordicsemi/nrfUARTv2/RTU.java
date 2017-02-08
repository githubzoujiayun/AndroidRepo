package com.nordicsemi.nrfUARTv2;

public class RTU {
	
	private int mAddress;
	private String mUnit = "";
	private int mUnitMeasure = 1;
	private double mMeasure = 1.0;
	private int mMaxValue = Integer.MAX_VALUE;
	private int mMinValue = Integer.MIN_VALUE;
	
	static final int FLAG_INTEGER = 0;
	static final int FLAG_DOUBLE = 1;
	
	private int mFlag = FLAG_INTEGER;
	
	
	public RTU(int address) {
		mAddress = address;
	}
	
	public RTU(int address,String unit, int unitMeasure) {
		this(address,unit,unitMeasure,Integer.MAX_VALUE,Integer.MIN_VALUE);
	}
	
	public RTU(int address,int max, int min) {
		this(address,"",1,max,min);
	}
	
	public RTU(int address,String unit, double measure) {
		mAddress = address;
		mUnit = unit;
		mMeasure = measure;
		mMaxValue = Integer.MAX_VALUE;
		mMinValue = Integer.MIN_VALUE;
		mFlag = FLAG_DOUBLE;
	}
	
	public RTU(int address,String unit, int unitMeasure,int maxValue,int minValue) {
		mAddress = address;
		mUnit = unit;
		mUnitMeasure = unitMeasure;
		mMaxValue = maxValue;
		mMinValue = minValue;
		mFlag = FLAG_INTEGER;
	}
	
	public RTU(int address, String unit) {
		this(address,unit,1,Integer.MAX_VALUE,Integer.MIN_VALUE);
	}

	public int getAddress() {
		return mAddress;
	}
	
	public int getUnitMeasure() {
		return mUnitMeasure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mAddress;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RTU other = (RTU) obj;
		if (mAddress != other.mAddress)
			return false;
		return true;
	}

	public String getUnit() {
		return mUnit;
	}
	
	public int getFlag() {
		return mFlag;
	}

	public double getMeasure() {
		return mMeasure;
	}
}
