package com.jobs.lib_v1.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算中英文字符占位字数类
 * 
 * @author janzon.tang
 */
public class TextSizeUtil {
	/**
	 * 计算文字的长度，一个中文算两个英文，返回英文长度
	 * 
	 * @param text 文字
	 * @return int 文字折算成英文的长度
	 */
	public static int getTextSize(String text) {
		if(null != text){
			if(text.length() > 0){
				Matcher matcher = Pattern.compile("[\\u0391-\\uFFE5]").matcher(text);
				StringBuffer sb = new StringBuffer();
				while(matcher.find()){
					sb.append(matcher.group(0));
				}
				return (int) Math.ceil((double)(((double)text.length() + (double)sb.length()) / (double)2));
			}else {
				return 0;
			}
		}else{
			return 0;
		}
	}
}
