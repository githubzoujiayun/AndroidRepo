package com.jobs.lib_v1.external;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import android.util.TypedValue;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.external.AXMLParser.AXmlResourceParser;

public class AndroidMainfestParser {
	private static final String DEFAULT_XML = "AndroidManifest.xml";

	/**
	 * 获取当前 APK 的 scheme 字符数组
	 * 
	 * @author janzon.tang
	 * @data 2013-8-12
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> getSchemeList() {
		ArrayList<String> schemeList = new ArrayList<String>();

		try {
			String manifestString = getManifestXMLString();
			String find = "\\<data\\s+android:scheme\\s*=\\s*\"([^\\\"]+)\"";

			Pattern pattern = Pattern.compile(find);
			Matcher matcher = pattern.matcher(manifestString);
			while (matcher.find()) {
				if (!schemeList.contains(matcher.group(1))) {
					schemeList.add(matcher.group(1));
				}
			}
		} catch (Throwable e) {
		}

		return schemeList;
	}

	/**
	 * 获取manifest.xml的全部内容
	 * 
	 * @data 2013-8-12
	 */
	public static String getManifestXMLString(){
		ZipFile file = null;
		StringBuilder xmlSb = new StringBuilder(100);
		try {
			File apkFile = new File(AppUtil.getPackagePath());
			file = new ZipFile(apkFile, ZipFile.OPEN_READ);
			ZipEntry entry = file.getEntry(DEFAULT_XML);

			AXmlResourceParser parser = new AXmlResourceParser();
			parser.open(file.getInputStream(entry));

			StringBuilder sb = new StringBuilder(10);
			final String indentStep = "	";

			int type;
			while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT: {
					log(xmlSb, "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					break;
				}
				case XmlPullParser.START_TAG: {
					log(false, xmlSb, "%s<%s%s", sb, getNamespacePrefix(parser.getPrefix()), parser.getName());
					sb.append(indentStep);

					int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
					int namespaceCount = parser.getNamespaceCount(parser.getDepth());

					for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
						log(xmlSb, "%sxmlns:%s=\"%s\"", i == namespaceCountBefore ? "  " : sb, parser.getNamespacePrefix(i), parser.getNamespaceUri(i));
					}

					for (int i = 0, size = parser.getAttributeCount(); i != size; ++i) {
						log(false, xmlSb, "%s%s%s=\"%s\"", " ", getNamespacePrefix(parser.getAttributePrefix(i)), parser.getAttributeName(i), getAttributeValue(parser, i));
					}
					log(xmlSb, ">");
					break;
				}
				case XmlPullParser.END_TAG: {
					sb.setLength(sb.length() - indentStep.length());
					log(xmlSb, "%s</%s%s>", sb, getNamespacePrefix(parser.getPrefix()), parser.getName());
					break;
				}
				case XmlPullParser.TEXT: {
					log(xmlSb, "%s%s", sb, parser.getText());
					break;
				}
				}
			}
			parser.close();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		if (null != file) {
			try {
				file.close();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		return xmlSb.toString();
	}

	private static String getNamespacePrefix(String prefix) {
		if (prefix==null || prefix.length()==0) {
			return "";
		}
		return prefix+":";
	}

	private static String getAttributeValue(AXmlResourceParser parser,int index) {
		int type=parser.getAttributeValueType(index);
		int data=parser.getAttributeValueData(index);
		if (type==TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type==TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X",getPackage(data),data);
		}
		if (type==TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X",getPackage(data),data);
		}
		if (type==TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type==TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X",data);
		}
		if (type==TypedValue.TYPE_INT_BOOLEAN) {
			return data!=0?"true":"false";
		}
		if (type==TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))+
				DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type==TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))+
				FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type>=TypedValue.TYPE_FIRST_COLOR_INT && type<=TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X",data);
		}
		if (type>=TypedValue.TYPE_FIRST_INT && type<=TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>",data,type);
	}
	
	private static String getPackage(int id) {
		if (id>>>24==1) {
			return "android:";
		}
		return "";
	}
	
	public static float complexToFloat(int complex) {
		return (float)(complex & 0xFFFFFF00)*RADIX_MULTS[(complex>>4) & 3];
	}
	
	private static final float RADIX_MULTS[]={
		0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F
	};
	
	private static final String DIMENSION_UNITS[]={
		"px","dip","sp","pt","in","mm","",""
	};
	
	private static final String FRACTION_UNITS[]={
		"%","%p","","","","","",""
	};
	
	private static void log(StringBuilder xmlSb,String format,Object...arguments) {
		log(true,xmlSb, format, arguments);
	}
	
	private static void log(boolean newLine,StringBuilder xmlSb,String format,Object...arguments) {
		xmlSb.append(String.format(format, arguments));
		if(newLine) xmlSb.append("\n");
	}
}
