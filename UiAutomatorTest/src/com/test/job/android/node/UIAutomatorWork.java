package com.test.job.android.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.test.job.android.JobException;
import com.test.job.android.Logging;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.Scrollable;
import com.test.job.android.node.Node.SwipeDirection;
import com.test.job.android.node.PressEvent.PressKey;

public class UIAutomatorWork implements IWork {

	private static final String JOBTEST_HOME = "/data/local/tmp/jobtest";
	private static final String LOG_PATH = "/data/local/tmp/jobtest/logs/";
	private static final String RESOURCE_PATH = "/data/local/tmp/jobtest/res/";
	private static final String CONFIG_PATH = RESOURCE_PATH;
	
	/* SD卡路径,UIAutomator没有读写SDCard权限*/
//	private static final String JOBTEST_HOME = Environment
//			.getExternalStorageDirectory() + "/jobtest";
//	private static final String LOG_PATH = JOBTEST_HOME + "/logs";
//	private static final String CONFIG_PATH = JOBTEST_HOME + "/res/";
//	private static final String RESOURCE_PATH = CONFIG_PATH;

	public boolean click(IView view, Scrollable _scrollable) {

		Scrollable scrollable = _scrollable;
		UiScrollable uiScrollable = new UiScrollable(
				new UiSelector().scrollable(true));
		switch (scrollable) {
		case HORIZONTAL:
			uiScrollable.setAsHorizontalList();
			break;
		case VERTICAL:
			uiScrollable.setAsVerticalList();
			break;
		default:
			break;
		}
		try {
			if (scrollable != Scrollable.NONE) {
				uiScrollable.scrollIntoView(build(view));
			}
			return build(view).clickAndWaitForNewWindow();
		} catch (UiObjectNotFoundException e) {
			throw new JobException(e);
		}
	}

	public void input(IView view, String chars) {
		Logging.logInfo("input : %s --->  " + getSelector(view) + "\n", chars);
		try {
			build(view).clickBottomRight();
			int length = build(view).getText().length();
			Configurator configurator = Configurator.getInstance();
			configurator.setKeyInjectionDelay(40);
			for (int i = 0; i < length; i++) {
				presskey(PressKey.DELETE);
			}
			build(view).setText(chars);
			configurator.setKeyInjectionDelay(0);
		} catch (UiObjectNotFoundException e) {
			throw new JobException(e);
		}
	}

	public boolean exists(IView view, Scrollable _scrollable) {
		if (_scrollable != null && _scrollable != Scrollable.NONE) {
			Scrollable scrollable = _scrollable;
			UiScrollable uiScrollable = new UiScrollable(
					new UiSelector().scrollable(true));
			switch (scrollable) {
			case HORIZONTAL:
				uiScrollable.setAsHorizontalList();
				break;
			case VERTICAL:
				uiScrollable.setAsVerticalList();
				break;
			default:
				break;
			}
			if (scrollable != Scrollable.NONE) {
				try {
					uiScrollable.scrollIntoView(build(view));
				} catch (UiObjectNotFoundException e) {
					throw new JobException(e);
				}
			}
		} else {
			waitForExists(view);
		}
		return build(view).exists();
	}

	public boolean exists(IView view) {
		waitForExists(view);
		return build(view).exists();
	}

	public boolean waitForExists(IView view) {
		final int timeout = view.getTimeout();
		if (timeout == 0) {
			return true;
		}
		if (timeout > 0) {
			return waitForExists(view, timeout);
		}
		return waitForExists(view, 10000);
	}

	public boolean waitForExists(IView view, int timeout) {
		Logging.logInfo("waitForExist --->  timeout " + timeout + "ms,  "
				+ getSelector(view));
		return build(view).waitForExists(timeout);
	}

	public String getText(IView view) {
		String text;
		try {
			text = build(view).getText();
			Logging.logInfo("getText : %s --->  " + getSelector(view), text);
			return text;
		} catch (UiObjectNotFoundException e) {
			throw new JobException(e);
		}
	}

	public UiObject build(IView view) {
		return new UiObject(getSelector(view));
	}

	public UiSelector getSelector(IView view) {
		UiSelector selector = new UiSelector();
		String resourceId = view.getResourceId();
		String resourceIdMatches = view.getResourceIdMatches();
		if (resourceId != null) {
			selector = selector.resourceId(resourceId);
		}
		if (resourceIdMatches != null) {
			selector = selector.resourceIdMatches(resourceIdMatches);
		}
		if (view instanceof TextNode) {
			TextNode textView = (TextNode) view;
			String text = textView.getSearchText();
			String textMatches = textView.getSearchTextMatches();
			if (text != null) {
				selector = selector.text(text);
			}
			if (textMatches != null) {
				selector = selector.textMatches(textMatches);
			}
		} else if (view instanceof IndexView) {
			IndexView indexView = (IndexView) view;
			String rootClass = indexView.getRootClass();
			String rootResourceId = indexView.getRootResourceId();
			String rootIndex = indexView.getRootIndex();
			if (rootClass != null) {
				selector = new UiSelector().className(rootClass);
			}
			if (rootResourceId != null) {
				selector = selector.resourceIdMatches(rootResourceId);
			}
			if (rootIndex != null) {
				int index = Integer.parseInt(rootIndex);
				selector = selector.index(index);
			}

			int[] indexs = indexView.getIndexs();
			int length = indexs.length;
			for (int i = 0; i < length; i++) {
				selector = selector.childSelector(new UiSelector()
						.index(indexs[i]));
			}
		}
		return selector;
	}

	public boolean waitUntilGone(IView view, int timeout) {
		Logging.logInfo("waitUntilGone --->  timeout " + timeout + "ms,  "
				+ getSelector(view));
		return build(view).waitUntilGone(timeout);
	}

	public String getQueryParam(IView view) {
		return getSelector(view).toString();
	}

	public String[] listConfigs() {
		File resDir = new File(RESOURCE_PATH);
		String[] chiFiles = resDir.list(new FilenameFilter() {

			public boolean accept(File dir, String filename) {
				return filename.endsWith(".xml");
			}
		});
		return chiFiles;
	}

	public String getHomePath() {
		File home = new File(JOBTEST_HOME);
		if (!home.exists()) {
			home.mkdirs();
		}
		return JOBTEST_HOME;
	}

	public String getResourcePath() {
		// TODO Auto-generated method stub
		return RESOURCE_PATH;
	}

	public String getLogPath() {
		// TODO Auto-generated method stub
		return LOG_PATH;
	}

	@Override
	public void takeScreenshot(File parent,String fileName) {
		File target;
		try {
			target = File.createTempFile(fileName, ".png", parent);
			UiDevice.getInstance().takeScreenshot(target);
		} catch (IOException e) {
			throw new JobException(e);
		}
	}

	public InputStream getInputStream(String config) {
		try {
			return new FileInputStream(CONFIG_PATH + config);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void presskey(PressKey key) {
		UiDevice sUiDevice = UiDevice.getInstance();
		switch (key) {
		case SEARCH:
			sUiDevice.pressSearch();
			break;
		case BACK:
			sUiDevice.pressBack();
			break;
		case DELETE:
			sUiDevice.pressDelete();
			break;
		case ENTER:
			sUiDevice.pressEnter();
			break;
		case HOME:
			sUiDevice.pressHome();
			break;
		case MENU:
			sUiDevice.pressMenu();
			break;
		default:
			break;
		}
	}

	public void pressKeyCode(int keyCode, int metaState) {
		UiDevice.getInstance().pressKeyCode(keyCode, metaState);
	}

	public void swipe(ViewImp viewImp, SwipeDirection direction) {
		final int STEPS_COUNT = 10;
		try {
			switch (direction) {
			case UP:
			case D2U:
				build(viewImp).swipeUp(STEPS_COUNT);
				break;
			case U2D:
			case DOWN:
				build(viewImp).swipeDown(STEPS_COUNT);
				break;
			case LEFT:
			case R2L:
				build(viewImp).swipeLeft(STEPS_COUNT);
				break;
			case L2R:
			case RIGHT:
				build(viewImp).swipeRight(STEPS_COUNT);
				break;
			case NONE:
				break;
			default:
				break;
			}
		} catch (UiObjectNotFoundException e) {
			throw new JobException(e);
		}
	}

	@Override
	public void startHomeActivity(String componentName) {
		TestUtils.startHomeActivity(componentName);
	}
}
