package com.jobs.lib_v1.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.jobs.lib_v1.app.AppUtil;

/** 通过反射调用类的静态变量或方法 */
public class JavaReflectClass {
	private Class<?> mClass = null;
	private String mClassName = "";

	public JavaReflectClass(String className) {
		try {
			mClass = Class.forName(className);
		} catch (Throwable e) {
			if (AppUtil.allowDebug()) {
				AppUtil.error(JavaReflectClass.class, "Class.forName(" + className + ")");
			}

			mClass = null;
		}

		mClassName = className;
	}

	public Class<?> getReflectClass() {
		return mClass;
	}

	public String getReflectClassName() {
		return mClassName;
	}

	public Object getStaticObject(String fieldName) {
		if (null == mClass) {
			return null;
		}

		try {
			Field field = mClass.getField(fieldName);

			if (!Modifier.isStatic(field.getModifiers())) {
				return null;
			}

			return field.get(null);
		} catch (Throwable e) {
			if (AppUtil.allowDebug()) {
				AppUtil.error(JavaReflectClass.class, "getStaticObject(" + mClassName + "::" + fieldName + ") failed!");
			}

			return null;
		}
	}

	public Object getStaticMethodResult(String methodName) {
		if (null == mClass) {
			return null;
		}

		try {
			Method method = mClass.getMethod(methodName);

			if (!Modifier.isStatic(method.getModifiers())) {
				return null;
			}

			return method.invoke(null);
		} catch (Throwable e) {
			if (AppUtil.allowDebug()) {
				AppUtil.error(JavaReflectClass.class, "getStaticMethodResult(" + mClassName + "::" + methodName + ") failed!");
			}

			return null;
		}
	}

	public String getStaticMethodString(String methodName, String defaultValue) {
		Object result = getStaticMethodResult(methodName);

		if (null == result || !(result instanceof String)) {
			return defaultValue;
		}

		return (String) result;
	}

	public int getStaticMethodInt(String methodName, int defaultValue) {
		Object result = getStaticMethodResult(methodName);

		if (null == result || !(result instanceof Integer)) {
			return defaultValue;
		}

		return (Integer) result;

	}

	public boolean getStaticMethodBoolean(String methodName, boolean defaultValue) {
		Object result = getStaticMethodResult(methodName);

		if (null == result || !(result instanceof Boolean)) {
			return defaultValue;
		}

		return (Boolean) result;
	}

	public String getStaticString(String fieldName, String defaultValue) {
		Object obj = getStaticObject(fieldName);

		if (null == obj) {
			return defaultValue;
		}

		if (obj instanceof String) {
			return (String) obj;
		}

		return defaultValue;
	}

	public int getStaticInt(String fieldName, int defaultValue) {
		if (null == mClass) {
			return defaultValue;
		}

		try {
			Field field = mClass.getField(fieldName);

			if (!Modifier.isStatic(field.getModifiers())) {
				return defaultValue;
			}

			return field.getInt(null);
		} catch (Throwable e) {
			if (AppUtil.allowDebug()) {
				AppUtil.error(JavaReflectClass.class, "getStaticInt(" + mClassName + "::" + fieldName + ") failed!");
			}

			return defaultValue;
		}
	}

	public boolean getStaticBoolean(String fieldName, boolean defaultValue) {
		if (null == mClass) {
			return defaultValue;
		}

		try {
			Field field = mClass.getField(fieldName);

			if (!Modifier.isStatic(field.getModifiers())) {
				return defaultValue;
			}

			return field.getBoolean(null);
		} catch (Throwable e) {
			if (AppUtil.allowDebug()) {
				AppUtil.error(JavaReflectClass.class, "getStaticBoolean(" + mClassName + "::" + fieldName + ") failed!");
			}

			return defaultValue;
		}
	}

	public List<Field> getStaticFileds() {
		if (null == mClass) {
			return null;
		}

		try {
			List<Field> fields = new ArrayList<Field>();
			Field[] fieldArray = mClass.getFields();

			for (Field field : fieldArray) {
				if (!Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				fields.add(field);
			}

			return fields;
		} catch (Throwable e) {
			if (AppUtil.allowDebug()) {
				AppUtil.error(JavaReflectClass.class, "getStaticFileds(" + mClassName + ") failed!");
			}

			return null;
		}
	}

	public boolean isValid() {
		return (null != mClass);
	}
}
