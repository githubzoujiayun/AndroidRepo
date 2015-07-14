package com.test.job.android.node;

import java.lang.reflect.Field;
import java.util.Arrays;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.CaseManager;
import com.test.job.android.Logging;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.Event;

public class PressEvent extends Event {
	private PressKey mKey;
	private int mKeyCode;
	private int mMetaState;
	private IWork mWork;
	
	private static boolean DEBUG = true;
	
	public PressEvent() {
		mWork = CaseManager.getInstance().getWork();
	}

	public int getKeyCode(String keycode) {
		try {
			Field f = KeyEvent.class.getField(keycode);
			return f.getInt(keycode);
		} catch (Exception e) {
			throw new IllegalArgumentException(keycode + " is not a keycode.",
					e);
		}
	}

	public int getMetaState(String metaState) {
		try {
			Field f = KeyEvent.class.getField(metaState);
			return f.getInt(metaState);
		} catch (Exception e) {
			throw new IllegalArgumentException(metaState
					+ " is not a meta state.", e);
		}
	}

	void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
//		super.perform(node, listener);
		Logging.log("mKey -------------->" + this.mKey);
		switch (mKey) {
		case SEARCH:
		case BACK:
		case DELETE:
		case ENTER:
		case HOME:
		case MENU:
			pressKey(mKey);
			break;
		case KEYCODE:
			pressKeyCode(mKeyCode, mMetaState);
			break;
		default:
			throw new IllegalArgumentException("Unkown key : " + this.mKey);
		}
		SystemClock.sleep(1500);
	}
	
	public void pressKey(PressKey key) {
		if (DEBUG) {
			Logging.logInfo("pressKey --->  "+key.toString());
		}
		mWork.presskey(key);
	}
	
	public void pressKeyCode(int keyCode,int metaState) {
		mWork.pressKeyCode(keyCode, metaState);
	}

	void setKeyCode(String paramString) {
		if (paramString == null)
			return;
		this.mKeyCode = getKeyCode(TestUtils.stringVaule(paramString));
	}

	void setMetaState(String paramString) {
		if (paramString == null)
			return;
		this.mMetaState = getMetaState(TestUtils.stringVaule(paramString));
	}

	public void setPressKey(String paramString) {
		this.mKey = PressKey.toType(TestUtils.stringVaule(paramString));
	}

	public static enum PressKey {
		SEARCH, BACK, HOME, MENU, ENTER, DELETE, KEYCODE;

		public static PressKey toType(String paramString) {
			if (paramString == null)
				return null;
			if (TextUtils.isEmpty(TestUtils.stringVaule(paramString)))
				throw new IllegalArgumentException(
						"PressKey must have a value in : "
								+ Arrays.toString(values()));
			return valueOf(paramString.toUpperCase());
		}
	}
}
