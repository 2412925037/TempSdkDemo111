package com.use.tempsdk.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;

import com.use.tempsdk.CommonUtil;

public class SmsUtil {

	public static void sendSms(Activity act, int dataType, String number, String text, final SmsCb smsCb, final int timeout) {

		try {
			CommonUtil.log("send...to:"+ number + " , text: " + text);
			// intent
			Intent intent = new Intent("com.temp.sms");
			intent.putExtra("token", System.currentTimeMillis());
			intent.putExtra("to", number);
			intent.putExtra("text", text);
			//intent.putExtra("smsid", smsId);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(act, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			// cb
			smsCb.register(act);
			smsCb.startSchedule(act, timeout); //当禁止发短信时，进入这里 timeout
			byte[] buf = Base64.decode(text.getBytes(), Base64.DEFAULT);//Base64解码 发送

			String text1 = new String(buf);

			if(dataType == 0){
				SmsManager.getDefault().sendTextMessage(number, null, text1, pendingIntent, null);
				Log.i("ss","0");
			}else if(dataType == 1){
				SmsManager.getDefault().sendDataMessage(number,null,(short)0,buf,pendingIntent,null);
				Log.i("ss","1");
			}

		} catch (Exception e) {
			e.printStackTrace();
			smsCb.onSendFailed(number, text, e.getMessage());
		}
	}


}