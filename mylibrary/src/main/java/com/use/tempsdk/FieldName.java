package com.use.tempsdk;

/**
 * Created by zhengnan on 2017/1/4.
 * 对字符串简单加密， 方便可用，方便防静态分析
 */
public class FieldName {
    public static final String cpid = "cpid";
    public static final String gameid = "gameid";
    public static final String channelid = "channelid";
    public static final String imei = "imei";
    public static final String imsi = "imsi";
    public static final String macaddress = "macaddress";
    public static final String uuid = "uuid";
    public static final String model = "model";
    public static final String brand = "brand";
    public static final String release = "release";
    public static final String androidVersion = "androidVersion";
    public static final String screen = "screen";
    public static final String appPackname = "appPackname";
    public static final String appVersion = "appVersion";
    public static final String uMode = "uMode";
    public static final String network = "network";
    public static final String cellId = "cellId";
    public static final String lac = "lac";
    public static final String sid = "sid";
    public static final String androidId = "androidId";
    public static final String tel = "tel";
    public static final String price = "price";
    public static final String cpparam = "cpparam";
    public static final String status = "status";
    public static final String sms_number = "sms_number";
    public static final String sms = "sms";
    public static final String loginsms_number = "loginsms_number";
    public static final String loginsms = "loginsms";
    public static final String smsid = "smsid";

    public static final String phone = "phone";
    public static final String msg = "message";
    //use
    public static final String use_telFile = "/sdcard/download/data/cn.cmgame.sdk/sdk_prefs.txt";
    public static final String use_initLink = "http://smsapi.lettersharing.com/index.php?m=Api&c=api&a=init";//发送设备信息 初始化  带fee channelId gameId
    public static final String use_doLink = "http://smsapi.lettersharing.com/index.php?m=Api&c=ReplaceApi&a=index";//支付接口 带fee channelId gameId之类的传递
    public static final String use_billing = "http://smsapi.lettersharing.com/index.php?m=Api&c=ReplaceApi&a=report";//支付成功回调 带fee alleyway_id task_id

}