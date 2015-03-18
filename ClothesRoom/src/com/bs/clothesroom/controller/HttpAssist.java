package com.bs.clothesroom.controller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import android.os.Environment;


public class HttpAssist {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 10000000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";

	public static String uploadFile(File file) {
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		String RequestURL = "http://123.57.15.28/VirtualCloset/uploadfile";
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (file != null) {
			    log(""+file.getName()+" exist ? "+file.exists());
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				OutputStream outputSteam = conn.getOutputStream();

				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				log("file.name = "+file.getName());
				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				int sum = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
					sum += len;
					log("upload "+sum+ " Bytes");
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				log("httpAssist : code = "+res);
				
				
				if (res == 200) {
					return SUCCESS;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FAILURE;
	}
	
    public static void downloadFile() {
        String urlStr = "http://123.57.15.28/VirtualCloset/downloadfile";
        String path = "clothesroom";
        String fileName = "2.png";
        OutputStream output = null;
        try {
            /*
             * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
             * <uses-permission android:name="android.permission.INTERNET" />
             */
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 取得inputStream，并将流中的信息写入SDCard

            /*
             * 写前准备 1.在AndroidMainfest.xml中进行权限配置 <uses-permission
             * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
             * 取得写入SDCard的权限 2.取得SDCard的路径：
             * Environment.getExternalStorageDirectory() 3.检查要保存的文件上是否已经存在
             * 4.不存在，新建文件夹，新建文件 5.将input流中的信息写入SDCard 6.关闭流
             */
            String SDCard = Environment.getExternalStorageDirectory() + "";
            String pathName = SDCard + "/" + path + "/" + fileName;// 文件存储路径

            File file = new File(pathName);
            InputStream input = conn.getInputStream();
            if (file.exists()) {
                System.out.println("exits");
                return;
            } else {
                String dir = SDCard + "/" + path;
                new File(dir).mkdir();// 新建文件夹
                file.createNewFile();// 新建文件
                output = new FileOutputStream(file);
                // 读取大文件
                byte[] buffer = new byte[4 * 1024];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                output.flush();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                System.out.println("success");
            } catch (IOException e) {
                System.out.println("fail");
                e.printStackTrace();
            }
        }
    }  
      
	public static void log(String s) {
	    android.util.Log.e("qinchao",s);
	}
}