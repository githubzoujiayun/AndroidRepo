package com.jobs.lib_v1.data.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.jobs.lib_v1.app.AppException;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.settings.LocalStrings;

/**
 * XML 数据解析器
 * 
 * @author xmwen
 * @date 2013-05-24
 */
public class XmlDataParser {
	/**
	 * 从XML字节数组中解析出 DataItemResult 对象
	 *
	 * @param data XML字节数组
	 * @param retVal 输出的对象
	 * @return boolean
	 */
	public static boolean parserData(byte[] data, DataItemResult retVal) {
		ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
		KXmlParser parser = new KXmlParser();

		try {
			parser.setInput(dataStream, "UTF-8");
			parseRoot(parser, retVal);
		} catch (Throwable e) {
			AppUtil.print(e);
			retVal.localError = true;
			retVal.hasError = true;
			retVal.parseError = true;
			retVal.message = LocalStrings.common_error_parser_prefix + AppException.getErrorString(e);
			retVal.errorRecord(e);
		}

		try {
			dataStream.close();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return !retVal.hasError;
	}

	/**
	 * 解析item节点，并返回解析状态
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param parser XML解析器对象
	 * @param retVal 用以存储解析结果的容器
	 * @return boolean 解析节点是否成功
	 */
	private static boolean parseItem(KXmlParser parser, DataItemResult retVal) throws XmlPullParserException {
		DataItemDetail dataItem = new DataItemDetail();
		int evtType = XmlPullParser.START_TAG;
		int tagStart = 1;
		String tagName = "item";
		StringBuffer nodeValue = new StringBuffer();

		/** 取得item节点的属性值 **/
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			dataItem.setAttributeValue(tagName, parser.getAttributeName(i), parser.getAttributeValue(i));
		}

		/** 循环取子节点 **/
		fetch_subnotes: do {
			try {
				evtType = parser.next();
			} catch (IOException e) {
				AppUtil.print(e);
				retVal.hasError = true;
				retVal.message = AppException.getErrorString(e);
				retVal.errorRecord(e);
				break;
			}

			switch (evtType) {
			case XmlPullParser.START_TAG:
				tagStart++;
				tagName = parser.getName();
				nodeValue = new StringBuffer();

				/** 取得子节点的属性值 **/
				for (int i = 0; i < parser.getAttributeCount(); i++) {
					dataItem.setAttributeValue(tagName, parser.getAttributeName(i), parser.getAttributeValue(i));
				}

				break;

			case XmlPullParser.END_TAG:
				if (nodeValue != null) {
					String nodeString = nodeValue.toString().trim();
					if (nodeString.length() > 0) {
						if (tagName.equals("item")) {
							dataItem.setStringValue(tagStart > 1 ? "item" : "text", nodeString);
						} else {
							dataItem.setStringValue(tagName, nodeString);
						}
					}
					nodeValue = null;
				}

				tagStart--;

				if (tagStart < 1) {
					break fetch_subnotes;
				}

				break;

			case XmlPullParser.TEXT:
				if (nodeValue != null) {
					nodeValue.append(parser.getText());
				}
				break;

			default:
				break;
			}
		} while (evtType != XmlPullParser.END_DOCUMENT);

		retVal.addItem(dataItem);

		return dataItem.getCount() > 0;
	}

	/**
	 * 解析XML数据
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param parser XML解析器对象
	 * @param retVal 用以存储解析结果的容器
	 * @return boolean 解析节点是否成功
	 */
	private static boolean parseRoot(KXmlParser parser, DataItemResult retVal) throws XmlPullParserException {
		int evtType = parser.getEventType();
		boolean hasData = false;
		boolean resultBodyStarted = false;
		StringBuffer nodeValue = null;
		String tagName = "";
		while (evtType != XmlPullParser.END_DOCUMENT) {
			switch (evtType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				nodeValue = new StringBuffer();

				if (resultBodyStarted) {
					if (tagName.equals("item")) {
						tagName = "";
						nodeValue = null;
						if (parseItem(parser, retVal)) {
							hasData = true;
						}
					}
				} else {
					if (tagName.equalsIgnoreCase("resultbody")) {
						resultBodyStarted = true;
						nodeValue = null;
					}
				}
				break;

			case XmlPullParser.END_TAG:
				if (nodeValue != null) {
					String result = nodeValue.toString().trim();

					if (resultBodyStarted) {
						if (tagName.equalsIgnoreCase("totalcount")) {
							try {
								retVal.maxCount = Integer.parseInt(result);
							} catch (NumberFormatException e) {
								AppUtil.print(e);
								retVal.maxCount = 0;
							}
						} else if (tagName.equalsIgnoreCase("jobcount")) {
							try {
								retVal.maxCount = Integer.parseInt(result);
							} catch (NumberFormatException e) {
								AppUtil.print(e);
								retVal.maxCount = 0;
							}
						}

						// 无论是 totalcount 还是 jobcount，现在都把值放到 detailInfo 中; 
						// 因为 maxcount 如果小于items数量的话，其值 会在 DataItemResult 中被自动改变。
						// By solomon.wen / 2014-04-23
						if (result.length() > 0) {
							retVal.detailInfo.setStringValue(tagName, result);
						}
					} else {
						if (tagName.equalsIgnoreCase("result")) {
							try {
								int resultCode = Integer.parseInt(result);

								if (resultCode == 1) {
									retVal.hasError = false;
								} else {
									retVal.hasError = true;
								}
							} catch (NumberFormatException e) {
								AppUtil.print(e);
								retVal.hasError = true;
							}
						} else if (tagName.equalsIgnoreCase("status")) {
							try {
								retVal.statusCode = Integer.parseInt(result);
							} catch (NumberFormatException e) {
								AppUtil.print(e);
								retVal.statusCode = 0;
							}
						} else if (tagName.equalsIgnoreCase("message")) {
							retVal.message = result;
						} else if (result.length() > 0) {
							retVal.detailInfo.setStringValue(tagName, result);
						}
					}

					nodeValue = null;
				}

				break;

			case XmlPullParser.TEXT:
				if (nodeValue != null) {
					nodeValue.append(parser.getText());
				}
				break;

			default:
				break;
			}

			try {
				evtType = parser.next();
			} catch (IOException e) {
				AppUtil.print(e);
				retVal.hasError = true;
				retVal.message = AppException.getErrorString(e);
				retVal.errorRecord(e);
			}
		}

		return hasData && (!retVal.hasError);
	}
}
