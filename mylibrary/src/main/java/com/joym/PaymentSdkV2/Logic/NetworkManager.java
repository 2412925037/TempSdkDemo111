/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.joym.PaymentSdkV2.Logic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * 网络连接工具类
 * 
 */
public class NetworkManager {
	static final String TAG = "NetworkManager";

	private int connectTimeout = 30 * 1000;
	private int readTimeout = 30 * 1000;
	Proxy mProxy = null;
	Context mContext;
	ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

	public NetworkManager(Context context) {
		this.mContext = context;
		setDefaultHostnameVerifier();
		resetNetPost();
	}

	/**
	 * 检查代理，是否cnwap接入
	 */
	
	public static boolean iscnwap = false;
	private void detectProxy() {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
		Log.e(TAG, "111111"+ni.isAvailable());
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE ) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			Log.e(TAG, "proxyHost = "+proxyHost);
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null) {
				final InetSocketAddress sa = new InetSocketAddress(proxyHost,
						port);
				mProxy = new Proxy(Proxy.Type.HTTP, sa);
			}
			iscnwap = true;
			Log.i(TAG, "手机网络");
		}else{
			iscnwap = false;
			Log.i(TAG, "无线网络 ");
		}
	}

	private void setDefaultHostnameVerifier() {
		//
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
	
	/**
	 * 发送和接收数据。注：先使用resetNetPost()清除数据，再使用addUrlNameValuePair()添加数据
	 * 
	 * @param1 strReqData
	 *            请求数据
	 * @param strUrl
	 *            请求地址
	 * @return
	 */
	public String SendAndWaitResponse(String strUrl) {
		//
//		Log.e(TAG, "1111");
		detectProxy();		
//		Log.e(TAG, "2222");		
		String strResponse = null;
		
		HttpURLConnection httpConnect = null;
		UrlEncodedFormEntity p_entity;
		try {
			
//			try
//			{
//				String sendString = XEnDeCode.encrypt(pairs+"", "abc");
//			} catch (Exception e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			if(iscnwap){
				Log.e("PaymentJoy", "pairs =="+pairs);
				p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
				
				
				URL url = new URL(strUrl);
//				if(mProxy==null){
//					Log.e(TAG, "55555");
//				}
				if (mProxy != null) {				
					httpConnect = (HttpURLConnection) url.openConnection(mProxy);
				} else {				
					httpConnect = (HttpURLConnection) url.openConnection();
				}	
				if(httpConnect==null){
					Log.e(TAG, "httpConnect is null");
				}
				httpConnect.setConnectTimeout(connectTimeout);
				httpConnect.setReadTimeout(readTimeout);
				httpConnect.setDoOutput(true);
				httpConnect.addRequestProperty("Content-type",
						"application/x-www-form-urlencoded;charset=utf-8");
				httpConnect.connect();
				OutputStream os = httpConnect.getOutputStream();
				p_entity.writeTo(os);
				
				Log.e("PaymentJoy", "strResponse =="+os);
				os.flush();			
				InputStream content = httpConnect.getInputStream();
				strResponse = convertStreamToString(content);
				
				Log.e("PaymentJoy", "strResponse =="+strResponse);
//			}else{
//				
//				Log.e(TAG, "44444");
//				strResponse = "111111";
//			}
		} catch (IOException e) {
//			strResponse = "1";
//			 if(PaymentJoy.iscmmmsms()){
//				 strResponse ="4"; 
//			}else{
//				
//				strResponse ="1"; 
//			}
			
			e.printStackTrace();
		} finally {
//			if(iscnwap){
			resetNetPost();
			httpConnect.disconnect();
//			}
		}

		return strResponse;
	}
	
	public void resetNetPost(){
		pairs.clear();
	}

	public NetworkManager addUrlNameValuePair(String name, String value){
		pairs.add(new BasicNameValuePair(name, value));
		return this;
	}

	/**
	 * 下载文件
	 * 
	 * @param context
	 *            上下文环境
	 * @param strurl
	 *            下载地址
	 * @param path
	 *            下载路径
	 * @return
	 */
	public boolean urlDownloadToFile(Context context, String strurl, String path) {
		boolean bRet = false;

		//
		detectProxy();

		try {
			URL url = new URL(strurl);
			HttpURLConnection conn = null;
			if (mProxy != null) {
				conn = (HttpURLConnection) url.openConnection(mProxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			conn.setDoInput(true);

			conn.connect();
			InputStream is = conn.getInputStream();

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}
	
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
