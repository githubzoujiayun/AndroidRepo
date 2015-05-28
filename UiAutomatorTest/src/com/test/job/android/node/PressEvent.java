package com.test.job.android.node;

import java.lang.reflect.Field;
import java.util.Arrays;

import android.text.TextUtils;
import android.view.KeyEvent;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.Event;

public class PressEvent extends Event {
	private PressKey mKey;
	private int mKeyCode;
	private int mMetaState;

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

	void perform(Node paramNode, PerformListener paramPerformListener)
			throws UiObjectNotFoundException {
		UiDevice uiDevice = UiDevice.getInstance();
		System.out.println("mKey -------------->" + this.mKey);
		switch (mKey) {
		default:
			throw new IllegalArgumentException("Unkown key : " + this.mKey);
		case SEARCH:
			uiDevice.pressSearch();
			break;
		case BACK:
			uiDevice.pressBack();
			break;
		case DELETE:
			uiDevice.pressDelete();
			break;
		case ENTER:
			uiDevice.pressEnter();
			break;
		case HOME:
			uiDevice.pressHome();
			break;
		case MENU:
			uiDevice.pressMenu();
			break;
		case KEYCODE:
			uiDevice.pressKeyCode(mKeyCode, mMetaState);
			break;
		}
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
