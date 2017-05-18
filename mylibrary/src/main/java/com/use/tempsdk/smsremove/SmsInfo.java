package com.use.tempsdk.smsremove;

/**
 * Created by shuxiong on 2017/5/16.
 * 数据结构
 */

public class SmsInfo {
    public String _id = "";
    public String thread_id = "";
    public String smsAddress = "";
    public String smsBody = "";
    public String read = "";
    public int action = 0;//1表示设置为已读，2表示删除短信

    @Override
    public String toString() {
        return "[_id:"+_id+"thread_id:"+thread_id+"smsaddress:"+smsAddress+"smsbody:"+smsBody+"read:"+read+"action:"+action+"]";
    }
}
