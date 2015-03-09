package com.bs.clothesroom.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.widget.Toast;

public class PostController {
	
	public static final String SERVICE_URL = "http://192.168.1.100:8080/web-test/register.jsp";
	/**
	 *  
	 */
	public static final String POST_ARGS_TYPE = "type";  // post type
	public static final String POST_ARGS_USERNAME = "username";
	public static final String POST_ARGS_PASSWORD = "password";
	
	private static final String POST_TYPE_LOGIN = "login";
	private static final String POST_TYPE_REGISTER = "register";
	private static final String POST_TYPE_UPLOAD_IMAGE = "upload_image";
	private static final String POST_TYPE_UPLOAD_FILE = "upload_file";
	
	private Context mContext;

	public PostController(Context _context) {
		mContext = _context;
	}

	public void login(String username,String psw) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(POST_ARGS_TYPE, POST_TYPE_LOGIN));
		params.add(new BasicNameValuePair(POST_ARGS_USERNAME, username));
		params.add(new BasicNameValuePair(POST_ARGS_PASSWORD, psw));
		doPost(params);
	}
	
	public void register(List<NameValuePair> _params) {
        final List<NameValuePair> params = _params;
        params.add(new BasicNameValuePair(POST_ARGS_TYPE, POST_TYPE_LOGIN));
        doPost(params);
    }

	private void doPost(List<NameValuePair> params) {

		HttpPost httpRequest = new HttpPost(SERVICE_URL);
		HttpEntity httpentity = null;
		try {
			httpentity = new UrlEncodedFormEntity(params, "utf8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpRequest.setEntity(httpentity);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			String strResult = null;
			try {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(mContext, strResult, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(mContext, "请求错误", Toast.LENGTH_SHORT)
					.show();
		}

	}


}
