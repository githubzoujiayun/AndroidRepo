/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012 Gorilla Logic, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.gorillalogic.monkeytalk.api.meta.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.junit.Test;

import com.gorillalogic.monkeytalk.api.meta.API;
import com.gorillalogic.monkeytalk.api.meta.Action;
import com.gorillalogic.monkeytalk.api.meta.Arg;
import com.gorillalogic.monkeytalk.api.meta.Component;

public class MetaAPITest {

	@Test
	public void testGetComponents() {
		assertThat(API.getComponents(), notNullValue());
		assertThat(API.getComponents().size(), is(not(0)));

		Component browser = API.getComponents().get(1);
		assertThat(browser.getName(), is("Browser"));

		Component button = API.getComponents().get(2);
		assertThat(button.getName(), is("Button"));
	}

	@Test
	public void testComparable() {
		Component browser = API.getComponent("browser");
		assertThat(browser.getName(), is("Browser"));

		Component button = API.getComponent("button");
		assertThat(button.getName(), is("Button"));

		assertTrue(browser.compareTo(button) < 0);
		assertTrue(browser.compareTo(browser) == 0);

		assertTrue(button.compareTo(browser) > 0);
		assertTrue(button.compareTo(button) == 0);

		assertTrue(button.equals(button));
	}

	@Test
	public void testHasComponentType() {
		assertThat(API.hasComponentType(null), is(false));
		assertThat(API.hasComponentType(""), is(false));
		assertThat(API.hasComponentType("unknown"), is(false));
		assertThat(API.hasComponentType("browser"), is(true));
		assertThat(API.hasComponentType("button"), is(true));
	}

	@Test
	public void testHasAction() {
		Component browser = API.getComponent("browser");
		assertThat(browser.hasAction("open"), is(true));
		assertThat(browser.hasAction("fred"), is(false));

		Component button = API.getComponent("button");
		assertThat(button.hasAction("tap"), is(true));
		assertThat(button.hasAction("verify"), is(true));
		assertThat(button.hasAction("waitfor"), is(true));
		assertThat(button.hasAction("fred"), is(false));
	}

	@Test
	public void testHasSuper() {
		Component browser = API.getComponent("browser");
		assertThat(browser.getSuper(), nullValue());
		assertThat(browser.hasSuper("view"), is(false));
		assertThat(browser.hasSuper("verifiable"), is(false));
		assertThat(browser.hasSuper("fred"), is(false));

		Component button = API.getComponent("button");
		assertThat(button.getSuper().getName(), is("View"));
		assertThat(button.hasSuper("view"), is(true));
		assertThat(button.hasSuper("verifiable"), is(true));
		assertThat(button.hasSuper("fred"), is(false));
	}

	@Test
	public void testComponentTypes() {
		assertThat(API.getComponentTypes(), notNullValue());
		assertThat(API.getComponentTypes(), hasItems("Button", "CheckBox", "Input", "VideoPlayer"));
	}

	@Test
	public void testGetByComponentTypeForButton() {
		Component c = API.getComponent("Button");
		assertThat(c.getName(), is("Button"));
		assertThat(c.getDescription(), containsString("UIButton"));
		assertThat(c.getSuper().getName(), is("View"));
		assertThat(c.getActionNames(), hasItems("Verify", "TouchDown", "TouchUp", "Tap"));
	}

	@Test
	public void testGetByComponentTypeForDevice() {
		Component c = API.getComponent("Device");
		assertThat(c.getName(), is("Device"));
		assertThat(c.getDescription().toLowerCase(), containsString("the device"));
		assertThat(c.getSuper().getName(), is("Verifiable"));
		assertThat(c.getActionNames(),
				hasItems("Back", "Rotate", "Shake", "Get", "Verify", "WaitFor"));
	}

	@Test
	public void testGetByComponentTypeForView() {
		Component c = API.getComponent("View");
		assertThat(c.getName(), is("View"));
		assertThat(c.getDescription().toLowerCase(), containsString("base class for all"));
		assertThat(c.getSuper().getName(), is("Verifiable"));
		assertThat(c.getActionNames(), hasItems("Tap", "Drag", "Verify", "WaitFor"));

		Action a = c.getAction("Drag");
		assertThat(a.getName(), is("Drag"));
		assertThat(a.getArgNames(), hasItems("coords"));

		Arg arg = a.getArg("coords");
		assertThat(arg.getName(), is("coords"));
		assertThat(arg.getType(), is("int"));
		assertThat(arg.isVarArgs(), is(true));
	}

	@Test
	public void testCommandNames() {
		assertThat(API.getCommandNames(), notNullValue());
		assertThat(
				API.getCommandNames(),
				hasItems("Browser.Open", "Button.Tap", "CheckBox.VerifyNotRegex", "Device.Get",
						"Input.EnterText", "ButtonSelector.Select", "VideoPlayer.VerifyWildcard"));
	}
}