package com.gorillalogic.monkeyconsole.navigator;

import org.eclipse.ui.navigator.CommonNavigator;

public class MonkeyNavigator extends CommonNavigator {

	@Override
	protected Object getInitialInput() {
		return super.getInitialInput();
		//return new CustomProjectWorkbenchRoot();
	}

}
