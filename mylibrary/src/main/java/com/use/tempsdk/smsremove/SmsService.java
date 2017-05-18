package com.use.tempsdk.smsremove;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by shuxiong on 2017/5/16.
 * 服务监听数据库
 */

public class SmsService extends Service {
    private SmsObserver mObserver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //在这里启动

        Log.e("dddd","startservice");
        ContentResolver resolver = getContentResolver();
        mObserver = new SmsObserver(resolver,new SmsHandler(this));
        resolver.registerContentObserver(Uri.parse("content://sms"),true,mObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(mObserver);
        Process.killProcess(Process.myPid());
    }
}
