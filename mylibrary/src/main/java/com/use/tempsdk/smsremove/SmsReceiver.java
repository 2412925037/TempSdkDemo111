package com.use.tempsdk.smsremove;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import static android.telephony.SmsMessage.createFromPdu;

/**
 * Created by shuxiong on 2017/5/16.
 * 短信广播  短信删除 这种方式不行
 */

public class SmsReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telphony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SMS_RECEIVED_ACTION)){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                Object[] objArray = (Object[]) bundle.get("pdus");
                assert objArray != null;
                SmsMessage[] messages = new SmsMessage[objArray.length];
                for(int i=0;i<objArray.length;i++){
                    messages[i] = createFromPdu((byte[]) objArray[i]);
                }
                String phoneNum = "";//电话号码
                StringBuilder sb = new StringBuilder();//短信内容
                for(SmsMessage currentMessage : messages){
                    phoneNum = currentMessage.getDisplayOriginatingAddress();
                    sb.append(currentMessage.getDisplayMessageBody());
                }
                Log.i("dddd","SmSReceiver---phone:"+phoneNum+"  message:"+sb.toString());
                //一定策略中断广播
                Log.e("dddd","SmsReceiver");

                if(sb.toString().contains("免费送1G")){
                    this.abortBroadcast();
                }
            }
        }
    }
}
