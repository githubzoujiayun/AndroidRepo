package com.gorillalogic.monkeytalk.processor.report.detail;

import java.util.HashMap;
import java.util.Map;

public class DetailReportHtmlFactory {
	private static final String DEFAULT_KEY = "default";
	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	static {
		registerDefaultClass(DetailReportHtml.class);
	}

	public static void registerClass(String key, Class<?> klass) {
		classMap.put(key, klass);
	}

	public static void registerDefaultClass(Class<?> klass) {
		DetailReportHtmlFactory.registerClass(DetailReportHtmlFactory.DEFAULT_KEY, klass);
	}

	public static DetailReportHtml createDetailReportHtml() {
		return createDetailReportHtml(DEFAULT_KEY);
	}

	public static DetailReportHtml createDetailReportHtml(String key) {
		Class<?> klass = classMap.get(key);
		if (klass == null) {
			throw new IllegalArgumentException("no DetailReportHtml registered with key '" + key
					+ "'");
		}

		DetailReportHtml report = null;
		try {
			report = (DetailReportHtml) klass.newInstance();
		} catch (Exception ex) {
			// somebody registered something bad here
			throw new RuntimeException(ex.getMessage());
		}
		return report;
	}
}
