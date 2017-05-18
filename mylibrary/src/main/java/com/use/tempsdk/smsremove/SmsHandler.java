package com.use.tempsdk.smsremove;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by shuxiong on 2017/5/16.
 * 短信处理类
 */

public class SmsHandler extends Handler {

    private Context mcontext;
    public SmsHandler(Context context){
        this.mcontext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        Log.e("dddd","handlemessage");
        SmsInfo smsInfo = (SmsInfo) msg.obj;

        Log.e("dddd",smsInfo.toString());
        if(smsInfo.action == 1){
            ContentValues values = new ContentValues();
            values.put("read",1);
            mcontext.getContentResolver().update(Uri.parse("content://sms/inbox"),values,"thread_id=?",new String[]{smsInfo.thread_id});
        }else if(smsInfo.action == 2){
            Uri mUri = Uri.parse("content://sms/");
            int i = mcontext.getContentResolver().delete(mUri,"_id=?",new String[]{smsInfo._id});
            Log.e("dddd","is ready delete = " + i);
        }

    }
}
