package com.use.tempsdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.use.tempsdk.bean.Message;
import com.use.tempsdk.bean.Task;
import com.use.tempsdk.sms.SmsCb;
import com.use.tempsdk.sms.SmsUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.use.tempsdk.CommonUtil.sendPost;
import static com.use.tempsdk.TempSdkImpl.deviceInfo;
import static com.use.tempsdk.TempSdkImpl.executorSub;

/**
 * Created by shuxiong on 2017/4/25.
 */

public class TempBiz {

    private static TempBiz instance = null;
    private Activity mActivity = null;
    SharedPreferences sharedPreferences;
    private TempBiz(Activity act) {
        mActivity = act;
        sharedPreferences = act.getSharedPreferences("feeSms", Context.MODE_APPEND);
    }

    public static TempBiz getInstance(Activity act) {
        if (instance == null) {
            instance = new TempBiz(act);
        }
        return instance;
    }

    /**
     * 获取短信列表
     *
     * @param result
     * @return
     */
    Task task = null; //存放fee   callback
    public ArrayList<Message> getMessageList(String result){

        ArrayList<Message> datas = null;
        try {
            JSONObject object1 = new JSONObject(result);
            JSONArray jsonArray = object1.getJSONArray("data");
            if(jsonArray!=null && jsonArray.length()!=0) {
                task = new Task();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object2 = jsonArray.getJSONObject(i);
                    if (object2.has("fee")) {
                        String fee = object2.getString("fee");//价格
                        task.setFee(fee);
                    }
                    if (object2.has("callback")) {
                        String callback = object2.getString("callback");
                        task.setCallback(callback);
                    }
                    if(object2.has("dataType")){
                        int datatype = object2.getInt("dataType");
                        task.setDataType(datatype);
                    }
                    if (object2.has("smsData")) {
                        datas = new ArrayList<>();
                        JSONArray array = object2.getJSONArray("smsData");
                        if(array!=null && array.length()!=0) {
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object3 = array.getJSONObject(j);
                                String phone = object3.getString("phone");
                                String message = object3.getString("message");
                                Message message1 = new Message(phone, message);//放到实体类中
                                datas.add(message1);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            return null;
        }
        return datas;
    }

    private static TempSdkFace.DoBillingCb callback = null;

    //有短信的情况 就发送
    public void callParseCmd(ProgressDialog progressDialog, ArrayList<Message> msgList, TempSdkFace.DoBillingCb cb){
        callback = cb;
        if(msgList == null || msgList.size() == 0){
            progressDialog.cancel();
            //通知服务端支付成功
            cb.onBilling(100);
            sendServerSuccessBill(Integer.valueOf(task.fee),task.callback);
        }else {

            for (int i = 0; i < msgList.size(); i++) {
                Message message = msgList.get(i);
                doSmsBilling(message,msgList, progressDialog, task.dataType, Integer.valueOf(task.fee), task.callback);
            }
        }

    }

    public void doSmsBilling(final Message message, final ArrayList<Message> msgList, final ProgressDialog progressDialog, int dataType, final int fee, final String callback1) {

            progressDialog.setMessage("正在支付中...");
            progressDialog.show();

            SmsUtil.sendSms(mActivity, dataType,message.phone, message.message, new SmsCb() {
                private void closeDialog() {
                    progressDialog.cancel();
                }

                @Override
                public void onSendSuccess(String number, String text) {
                    super.onSendSuccess(number, text);

                    message.messageSendStatus = 1;
                    checkPayStatus(fee, callback1,msgList);
                    closeDialog();
                }

                @Override
                public void onSendFailed(String number, String text, String reason) {
                    super.onSendFailed(number, text, reason);

                    message.messageSendStatus = 0;
                    checkPayStatus(fee, callback1,msgList);
                    closeDialog();
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();

                    message.messageSendStatus = 0;
                    checkPayStatus(fee, callback1,msgList);
                    closeDialog();
                }
            }, 15000);
    }
    //剩余计费
    public void doSmsDoing(final Message message, final ArrayList<Message> msgList, final int dataType, final int fee, final String callback){

            SmsUtil.sendSms(mActivity, dataType, message.phone, message.message, new SmsCb() {

                @Override
                public void onSendSuccess(String number, String text) {
                    super.onSendSuccess(number, text);

                    message.messageSendStatus = 1;
                    checkPayStatus2(fee, callback, msgList);
                }

                @Override
                public void onSendFailed(String number, String text, String reason) {
                    super.onSendFailed(number, text, reason);

                    message.messageSendStatus = 0;
                    checkPayStatus2(fee, callback, msgList);
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();

                    message.messageSendStatus = 0;
                    checkPayStatus2(fee, callback, msgList);
                }
            }, 15000);

    }

    /**
     * 继续 剩余的计费
     */
    public void caculateBill(int fee) throws Exception {

        while (true) {

            Log.i("dd1","请求金额:"+fee);
            if (fee == 0) {//一把成功
                break;
            }

            JSONObject jsonObject = new JSONObject(TempSdkImpl.dobill);
            jsonObject.put("fee", fee);
            //String json = null;//剩余的第一次请求计费
            //还需要根据 结果是否有短信 进行发送

            JSONObject object = null;
            for(int i=0;i<2;i++){
                String json = sendPost(FieldName.use_doLink,jsonObject.toString());
                if(TextUtils.isEmpty(json)){
                    Thread.sleep(10000);
                }
                if(!TextUtils.isEmpty(json)){
                    object = new JSONObject(json);
                    if(object.getInt("status") == 1){
                        break;
                    }else {
                        Thread.sleep(10000);
                    }
                }
            }
            if (object!= null && (object.getInt("status") == 1)) {
                int fee2 = Integer.valueOf(object.getJSONArray("data").getJSONObject(0).getString("fee"));//剩余第一次消费的结果
                String callback = object.getJSONArray("data").getJSONObject(0).getString("callback");
                if (object.getJSONArray("data").getJSONObject(0).has("dataType")) {
                    int dataType = object.getJSONArray("data").getJSONObject(0).getInt("dataType");

                    if (object.getJSONArray("data").getJSONObject(0).has("smsData")) {
                        JSONArray array = object.getJSONArray("data").getJSONObject(0).getJSONArray("smsData");

                        ArrayList<Message> data = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = array.getJSONObject(i);
                            String phone = object1.getString("phone");
                            String msg = object1.getString("message");
                            //// TODO: 2017/5/5
                            Message message = new Message(phone, msg);
                            data.add(message);
                        }
                        for (int j = 0;j<data.size();j++) {
                            Message message = data.get(j);
                            doSmsDoing(message, data, dataType, fee2, callback);
                        }
                    }

                } else {

                    JSONObject jsonObject1 = new JSONObject(deviceInfo);
                    jsonObject1.put("fee", fee2);
                    jsonObject1.put("callback", callback);
                    jsonObject1.put("status",1);
                    sendPost(FieldName.use_billing, jsonObject1.toString());
                }
                Log.i("dd2","金额:"+fee2);

                fee = fee - fee2;
                Log.i("dd3","金额:"+fee);
            }else {
                break;
            }
        }

    }
    //剩余短信的发送 不需要 通知 游戏
    private void checkPayStatus2(int fee, String callback1, ArrayList<Message> msgList) {

        for (Message message : msgList) {
            if(message.messageSendStatus == 0){
                if(callback !=null) {
                    sendServerFailedBill(fee,callback1);
                    callback = null;
                    return;
                }
            }
            // 短信还在发送中
            if (message.messageSendStatus == -1) {
                return;
            }
        }
        int result = 0;
        for (Message message : msgList) {
            if (message.messageSendStatus == 1) {
                ++result;
            }
            if(message.messageSendStatus == 0){
                if(callback !=null) {
                    sendServerFailedBill(fee,callback1);
                    callback = null;
                    return;
                }
            }
        }

        if(result == msgList.size()){
            if(callback !=null) {
                try{

                    final JSONObject jsonObject = new JSONObject(deviceInfo);
                    jsonObject.put("fee", fee);
                    jsonObject.put("callback", callback1);
                    jsonObject.put("status",1);//成功
                    executorSub.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendPost(FieldName.use_billing, jsonObject.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

                //sendServerSuccessBill(fee,callback1);
            }
        }

    }

    /**
     * 获取支付状态
     *
     * @return
     */
    private void checkPayStatus(int fee, String callback1, ArrayList<Message> msgList) {

        for (Message message : msgList) {
            if(message.messageSendStatus == 0){
                if(callback !=null) {
                    callback.onBilling(50);
                    sendServerFailedBill(fee,callback1);
                    callback = null;
                    return;
                }
            }
            // 短信还在发送中
            if (message.messageSendStatus == -1) {
                return;
            }
        }
        int result = 0;
        for (Message message : msgList) {
            if (message.messageSendStatus == 1) {
                ++result;
            }
            if(message.messageSendStatus == 0){
                if(callback !=null) {
                    callback.onBilling(50);
                    sendServerFailedBill(fee,callback1);
                    callback = null;
                    return;
                }
            }
        }

        if(result == msgList.size()){
            if(callback !=null) {
                callback.onBilling(100);
                sendServerSuccessBill(fee,callback1);
            }
        }

    }

    public void sendServerSuccessBill(final int fee,final String callback) {
        if (TempSdkImpl.future != null) {
            TempSdkImpl.future.cancel(false);
        }
        TempSdkImpl.executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(deviceInfo);
                    jsonObject.put("fee", fee);
                    jsonObject.put("callback", callback);
                    jsonObject.put("status",1);//成功
                    sendPost(FieldName.use_billing, jsonObject.toString());

//                    Thread.sleep(10000);
//                    int fee1 = TempSdkImpl.money - fee;//查看第一次 是否有剩余
//                    if(fee1 != 0) {
//                        caculateBill(fee1);
//                    }

//                    if (TempSdkImpl.future != null) {
//                        TempSdkImpl.future.cancel(false);
//                    }

                    TempSdkImpl.executorSub.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(8000);
                                int fee1 = TempSdkImpl.money - fee;//查看第一次 是否有剩余
                                if(fee1 != 0 ) {
                                    caculateBill(fee1);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //继续剩余的计费

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("剩余计费", "失败");
                }
            }
        });



    }

    public void sendServerFailedBill(final int fee,final String callback) {
        if (TempSdkImpl.future != null) {
            TempSdkImpl.future.cancel(false);
        }
        TempSdkImpl.executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(deviceInfo);
                    jsonObject.put("fee", fee);
                    jsonObject.put("callback", callback);
                    jsonObject.put("status",0);//失败
                    sendPost(FieldName.use_billing, jsonObject.toString());

                    //继续剩余的计费
                    //Thread.sleep(8000);
                    //caculateBill(fee);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("计费", "失败");
                }
            }
        });

    }


}
