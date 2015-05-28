package com.test.job.android.view;

import java.util.Arrays;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

public class IndexView extends View{

	String mRootClass;
	String mRootIndex;
	int mIndexs[];
	
	public void setIndexs(String indexStr){
		String indexArray[] = indexStr.split(":");
		mIndexs = new int[indexArray.length];
		for (int i=0;i<indexArray.length;i++) {
			mIndexs[i] = Integer.parseInt(indexArray[i]);
		}
	}

	@Override
	public UiObject build() {
		UiSelector selector = null;
		if (mRootClass != null) {
			selector = new UiSelector().className(mRootClass);
		}
		if (mRootIndex != null) {
			int index = Integer.parseInt(mRootIndex);
			selector = selector.index(index);
		}
		int length = mIndexs.length;
		for (int i=0;i<length;i++) {
			selector = selector.childSelector(new UiSelector().index(mIndexs[i]));
		}
		return new UiObject(selector);
	}

	@Override
	public String toString() {
		return "IndexView [mRootClass=" + mRootClass + ", mRootIndex="
				+ mRootIndex + ", mIndexs=" + Arrays.toString(mIndexs) + "]";
	}
}
