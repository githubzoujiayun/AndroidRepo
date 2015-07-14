/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2013 Gorilla Logic, Inc.

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
package com.gorillalogic.monkeytalk.java.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.gorillalogic.monkeytalk.java.Logger;
import com.gorillalogic.monkeytalk.java.api.Application;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.processor.ScriptProcessor;

/**
 * Proxy handler that handles MonkeyTalk components. Responsible for pulling the component and
 * monkeyId together and passing them on to the {@link ActionProxyHandler}.
 */
public class ComponentProxyHandler extends Player implements InvocationHandler {
	private ActionProxyHandler actionProxy;
	private Class<? extends Application> customApplication;

	public ComponentProxyHandler(ScriptProcessor processor, Scope scope) {
		super(processor, scope);
		actionProxy = new ActionProxyHandler(processor, scope);
	}

	/** Set the custom Application extension. */
	public void setCustomApplication(Class<? extends Application> klass) {
		this.customApplication = klass;
	}

	/** Get the custom Application extension. */
	public Class<? extends Application> getCustomApplication() {
		return customApplication;
	}

	@Override
	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		if ("raw".equals(m.getName()) && args != null && args.length > 0 && args[0] != null
				&& args[0].toString().length() > 0) {
			return play(args[0].toString());
		}

		if (customApplication != null) {
			// check if the invoked method is in our custom Application extension
			for (Method meth : customApplication.getDeclaredMethods()) {
				if (m.getName() == meth.getName()
						&& m.getParameterTypes().length == meth.getParameterTypes().length) {

					// found custom extension, so instantiate custom Component class and return
					Class<?> klass = meth.getReturnType();
					Constructor<?> con = klass.getConstructor(meth.getParameterTypes());
					Object obj = con.newInstance(args);

					// setter injection of the player if present
					try {
						Method setPlayer = klass.getDeclaredMethod("setPlayer", Player.class);
						setPlayer.invoke(obj, this);
					} catch (NoSuchMethodException ex) {
						// ignore
					}

					// setter injection of processor if present
					try {
						Method setScriptProcessor = klass.getDeclaredMethod("setScriptProcessor",
								ScriptProcessor.class);
						setScriptProcessor.invoke(obj, processor);
					} catch (NoSuchMethodException ex) {
						// ignore
					}

					// return instance
					return obj;
				}
			}
		}

		// get the component interface
		String component = m.getName().substring(0, 1).toUpperCase() + m.getName().substring(1);
		String klassName = "com.gorillalogic.monkeytalk.java.api." + component;
		Class<?> klass = Class.forName(klassName);

		// compute the monkeyId
		String monkeyId = "*";
		if (args != null && args.length > 0 && args[0] != null && args[0].toString().length() > 0) {
			monkeyId = args[0].toString();
		}

		actionProxy.setComponent(component);
		actionProxy.setMonkeyId(monkeyId);

		// return the action proxy (for the given component)
		return Proxy.newProxyInstance(klass.getClassLoader(), new Class[] { klass }, actionProxy);
	}

	@Override
	public void setVerbose(boolean verbose) {
		super.setVerbose(verbose);
		if (actionProxy != null) {
			actionProxy.setVerbose(verbose);
		}
	}

	@Override
	public void setLogger(Logger logger) {
		super.setLogger(logger);
		if (actionProxy != null) {
			actionProxy.setLogger(logger);
		}
	}
}