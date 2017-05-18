package com.use.tempsdk.Interface;

import android.app.Activity;

import com.joym.PaymentSdkV2.PaymentJoy;
import com.use.tempsdk.TempSdkFace;
import com.use.tempsdk.TempSdkImpl;

/**
 * Created by shuxiong on 2017/5/17.
 *
 * 调用TempSdkImpl
 */

public class TempSdkInterface {

    //初始化
    public static void init(final Activity MyAct , final TempSdkFace.InitCb cb){

        String cpid = PaymentJoy.getCpId(MyAct);
        String channelid = PaymentJoy.getcid(MyAct);
        String gameid = PaymentJoy.getgameid(MyAct);

        TempSdkImpl.init(MyAct, cpid, channelid, gameid, cb);
    }

    //计费
    public static void doBilling(final Activity MyAct, String price, TempSdkFace.DoBillingCb cb){
        //String fee = PaymentJoy.getfee(MyAct);//首次支付费用
        String cpParam = "1111";  //userid
        TempSdkImpl.doBilling(MyAct, price, cpParam, cb);

    }
}
