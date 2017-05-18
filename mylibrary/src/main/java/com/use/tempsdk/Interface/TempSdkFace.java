package com.use.tempsdk.Interface;//package com.use.tempsdk.Interface;
//
//import android.app.Activity;
//import android.util.Log;
//
//import com.winter.mdm.CallBack;
//import com.winter.mdm.MMLogManager;
//
//public class TempSdkFace {
//    private static final int TYPE = 3;
//    private static Object logObj;
//
//    public TempSdkFace() {
//    }
//    //初始化  回调值 = 300为成功  否则为失败
//    public static void init(final Activity act, final TempSdkFace.InitCb cb) {
//        try {
//            logObj = MMLogManager.getLogObj(3);
//            if(logObj != null) {
//                Class[] e = new Class[]{Activity.class, TempSdkFace.InitCb.class};
//                Object[] params = new Object[]{act, cb};
//                MMLogManager.doLog(logObj, "com.use.tempsdk.Interface.TempSdkInterface", "init", (Object)null, e, params);
//            } else {
//                MMLogManager.setLogCb(3, new CallBack() {
//                    public void onCallback(Object arg0, int type, int status) {
//                        if(type != 3) {
//                            cb.onResult(303);
//                        } else {
//                            if(arg0 != null && status == MMLogManager.MODULE_LOADED) {
//                                Class[] classes = new Class[]{Activity.class, TempSdkFace.InitCb.class};
//                                Object[] params = new Object[]{act, cb};
//
//                                try {
//                                    TempSdkFace.logObj = arg0;
//                                    MMLogManager.doLog(TempSdkFace.logObj, "com.use.tempsdk.Interface.TempSdkInterface", "init", (Object)null, classes, params);
//                                } catch (Throwable var7) {
//                                    var7.printStackTrace();
//                                    cb.onResult(303);
//                                }
//                            } else {
//                                cb.onResult(303);
//                            }
//
//                        }
//                    }
//                });
//            }
//        } catch (Throwable var7) {
//            var7.printStackTrace();
//            cb.onResult(303);
//            Log.e("dddd","1111");
//        }
//
//    }
//    //计费 回调值 = 100为支付成功  否则为失败  参数fee为点击传入的价钱 (给String类型 单位为元)
//    public static void doBilling(Activity act, String fee,TempSdkFace.DoBillingCb cb) {
//        if(logObj == null) {
//            cb.onBilling(998);
//        } else {
//            Class[] classes = new Class[]{Activity.class, String.class, TempSdkFace.DoBillingCb.class};
//            Object[] params = new Object[]{act, fee, cb};
//
//            try {
//                MMLogManager.doLog(logObj, "com.use.tempsdk.Interface.TempSdkInterface", "doBilling", (Object)null, classes, params);
//            } catch (Throwable var7) {
//                var7.printStackTrace();
//                cb.onBilling(998);
//            }
//        }
//
//    }
//
//    public interface DoBillingCb {
//        void onBilling(int var1);
//    }
//
//    public interface InitCb {
//        void onResult(int var1);
//    }
//}
