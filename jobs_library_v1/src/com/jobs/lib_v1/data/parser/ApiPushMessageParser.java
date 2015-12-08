package com.jobs.lib_v1.data.parser;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.net.pull.MessagePullService;

/**
 * 从API接口中提取属于推送消息的节点
 * 
 * @author solomon.wen
 * @date 2014-01-14
 */
public class ApiPushMessageParser {
	/**
	 * 从一个 DataItemDetail 对象中提取推送消息
	 * 
	 * @param detail
	 */
	public static void parserDetail(DataItemDetail detail){
		try {
			if (!detail.hasKey("push_msg_flag") || !detail.hasKey("push_msg_content")) {
				return;
			}

			// push_msg_flag 长度校验
			String push_msg_flag = detail.getString("push_msg_flag").trim();
			if (push_msg_flag.length() < 34) {
				return;
			}

			String[] push_msg_flag_arr = push_msg_flag.split("\\|");
			if (push_msg_flag_arr.length != 2) {
				return;
			}

			// 校验推送消息ID
			String push_msg_id = push_msg_flag_arr[0];
			String push_msg_verify = push_msg_flag_arr[1];
			if (push_msg_verify.length() != 32) {
				return;
			}

			if (!push_msg_verify.equalsIgnoreCase(Md5.md5(push_msg_id.getBytes()))) {
				return;
			}

			// 提取剩余的键值对
			String push_msg_content = detail.getString("push_msg_content").trim();
			String push_msg_title = detail.getString("push_msg_title").trim();
			String push_msg_badge = detail.getString("push_msg_badge").trim();
			String push_msg_type = detail.getString("push_msg_type").trim();
			String push_msg_uri = detail.getString("push_msg_uri").trim();
			String push_msg_button_ok = detail.getString("push_msg_button_ok").trim();
			String push_msg_button_cancel = detail.getString("push_msg_button_cancel").trim();

			// 删除对应的键值对
			detail.remove("push_msg_flag");
			detail.remove("push_msg_content");
			detail.remove("push_msg_title");
			detail.remove("push_msg_badge");
			detail.remove("push_msg_type");
			detail.remove("push_msg_uri");
			detail.remove("push_msg_button_ok");
			detail.remove("push_msg_button_cancel");

		    // 重新构造消息格式
		    DataJsonResult jsonMsg = new DataJsonResult();
		    jsonMsg.putOpt("messageid", push_msg_id);
		    jsonMsg.putOpt("content", push_msg_content);
		    jsonMsg.putOpt("pushtype", push_msg_type);
		    jsonMsg.putOpt("pushurl", push_msg_uri);
		    jsonMsg.putOpt("ok_button_text", push_msg_button_ok);
		    jsonMsg.putOpt("cancel_button_text", push_msg_button_cancel);
		    jsonMsg.putOpt("badge", push_msg_badge);
		    jsonMsg.putOpt("title", push_msg_title);

		    // 交给委托对象处理此消息
		    MessagePullService.onReceivedMessage(jsonMsg);
		} catch (Throwable e) {
			AppUtil.print(e);
		}
	}

	/**
	 * 从一个 DataJsonResult 对象中提取推送消息
	 * 
	 * @param json
	 */
	public static void parserJson(DataJsonResult json){
		try {
			if (!json.has("push_msg_flag") || !json.has("push_msg_content")) {
				return;
			}

			// push_msg_flag 长度校验
			String push_msg_flag = json.getString("push_msg_flag").trim();
			if (push_msg_flag.length() < 34) {
				return;
			}

			String[] push_msg_flag_arr = push_msg_flag.split("\\|");
			if (push_msg_flag_arr.length != 2) {
				return;
			}

			// 校验推送消息ID
			String push_msg_id = push_msg_flag_arr[0];
			String push_msg_verify = push_msg_flag_arr[1];
			if (push_msg_verify.length() != 32) {
				return;
			}

			if (!push_msg_verify.equalsIgnoreCase(Md5.md5(push_msg_id.getBytes()))) {
				return;
			}

			// 提取剩余的键值对
			String push_msg_content = json.getString("push_msg_content").trim();
			String push_msg_title = json.getString("push_msg_title").trim();
			String push_msg_badge = json.getString("push_msg_badge").trim();
			String push_msg_type = json.getString("push_msg_type").trim();
			String push_msg_uri = json.getString("push_msg_uri").trim();
			String push_msg_button_ok = json.getString("push_msg_button_ok").trim();
			String push_msg_button_cancel = json.getString("push_msg_button_cancel").trim();

			// 删除对应的键值对
			json.remove("push_msg_flag");
			json.remove("push_msg_content");
			json.remove("push_msg_title");
			json.remove("push_msg_badge");
			json.remove("push_msg_type");
			json.remove("push_msg_uri");
			json.remove("push_msg_button_ok");
			json.remove("push_msg_button_cancel");

		    // 重新构造消息格式
		    DataJsonResult jsonMsg = new DataJsonResult();
		    jsonMsg.putOpt("messageid", push_msg_id);
		    jsonMsg.putOpt("content", push_msg_content);
		    jsonMsg.putOpt("pushtype", push_msg_type);
		    jsonMsg.putOpt("pushurl", push_msg_uri);
		    jsonMsg.putOpt("ok_button_text", push_msg_button_ok);
		    jsonMsg.putOpt("cancel_button_text", push_msg_button_cancel);
		    jsonMsg.putOpt("badge", push_msg_badge);
		    jsonMsg.putOpt("title", push_msg_title);

		    // 交给委托对象处理此消息
		    MessagePullService.onReceivedMessage(jsonMsg);
		} catch (Throwable e) {
			AppUtil.print(e);
		}
	}
}
