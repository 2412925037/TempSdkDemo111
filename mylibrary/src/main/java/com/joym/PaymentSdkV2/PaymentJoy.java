package com.joym.PaymentSdkV2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joym.PaymentSdkV2.Logic.NetworkManager;
import com.joym.PaymentSdkV2.Logic.PaymentResultCode;
import com.joym.gamecenter.sdk.offline.api.SdkAPI;
import com.unity3d.player.UnityPlayer;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @brief main entry
 * @brief please DO NOT MODIFY IT!!!
 * 
 * */
public class PaymentJoy {
	private static PaymentJoy mInstance = null;
	private static PaymentCb mCb = null;
	private static Activity mActivity = null;

	private final static String TAG = "PaymentJoy";
	private static Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();

	/**
	 * @
	 * 
	 * */
	private PaymentJoy(PaymentCb resultCb) {

	}

	private PaymentJoy() {

	}

	/**
	 * @brief
	 * 
	 * */
	public static PaymentJoy getInstance(PaymentCb resultCb) {
		mCb = resultCb;
		if (null == mInstance) {
			mInstance = new PaymentJoy(resultCb);
		}

		return mInstance;
	}

	/**
	 * @brief
	 * 
	 * 
	 * */
	
	private static LinearLayout layout;
	private static int logoIndex = 1;
	
	private static String picname="";
	public static void onCreate(final Activity act) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG, "PaymentJoy onCreate");
				if (null == mInstance) {
					mInstance = new PaymentJoy();
					mActivity = act;
				}
				getInstance(mCb);
				  layout = new LinearLayout(mActivity);
					layout.setGravity(Gravity.CENTER);
					show();
				
					mActivity.addContentView(layout, new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				Toast.makeText(mActivity, "PaymentJoy onCreate", Toast.LENGTH_SHORT)
						.show();
				
				
//				Log.i(TAG, "SdkAPI initGameCenter");
//				SdkAPI.initGameCenter(mActivity);
				
			}
		});
	}
	
	
	public static void show(){
		
		
		layout.setBackgroundColor(Color.rgb(255, 255, 255));
		final TextView textView = new TextView(mActivity);
		textView.setText("这里将展示渠道商/运营商LOGO");
		final ImageView iView = new ImageView(mActivity);
		layout.addView(iView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		picname="assets/payment_lt_"+logoIndex+".png";
		InputStream is = mActivity.getClassLoader().getResourceAsStream(
				picname);
		Drawable drawable = Drawable.createFromStream(is, "payment_lt_logo");
		iView.setImageDrawable(drawable);
		iView.setVisibility(View.VISIBLE);
		logoIndex++;
		Message message = new Message();
		message.obj = iView;
		getHander().sendMessageDelayed(message, 3000);
		
		
	}
	
	static Handler getHander() {
		if (handler == null) {
			handler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					if (logoIndex > 2)
					{
						Log.i("ImgViewLogo", "has no logo,logoindex ==> " + logoIndex);
						layout.setVisibility(View.GONE);
						
					} else
					{
						Log.i("ImgViewLogo", "has more logo,logoindex ==> " + logoIndex);
						layout.removeView((View) msg.obj);
						show();
					}
				}
			};
		}
		
		return handler;
	}
	
	static Handler handler = null;
	

	public static void initSdk(final Activity act) {

		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Toast.makeText(act, "PaymentJoy initSdk", Toast.LENGTH_SHORT)
						.show();
				Log.i(TAG, "PaymentJoy initSdk");
			}
		});
	}

	/**
	 * android调用
	 * 
	 * @param1 退出游戏
	 */
	public void preExitGame(final Activity act) {
		Log.i(TAG, "PaymentJoy preExitGame");
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(act).setTitle("信息")
						.setMessage("是否退出游戏?")
						.setPositiveButton("退出", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
//								final LinearLayout layout = new LinearLayout(
//										act);
//								layout.setGravity(Gravity.CENTER);
//								layout.setBackgroundColor(Color.rgb(255, 255,
//										255));
//								TextView textView = new TextView(act);
//								textView.setText("这里将展示渠道商/运营商LOGO");
//
//								final ImageView iView = new ImageView(act);
//								layout.addView(
//										textView,
//										new LinearLayout.LayoutParams(
//												LinearLayout.LayoutParams.FILL_PARENT,
//												LinearLayout.LayoutParams.FILL_PARENT));
//								InputStream is = act
//										.getClassLoader()
//										.getResourceAsStream(
//												"paymentSdkV2_res/payment_lt_logo.png");
//								Drawable drawable = Drawable.createFromStream(
//										is, "src");
//								iView.setImageDrawable(drawable);
//								iView.setVisibility(View.VISIBLE);
//								act.addContentView(layout, new LayoutParams(
//										LayoutParams.FILL_PARENT,
//										LayoutParams.FILL_PARENT));
//								new Handler() {
//									@Override
//									public void handleMessage(Message msg) {
//										Log.e("PaymentJoy", "exit game");
//										act.finish();
//										System.exit(0);
//									}
//								}.sendEmptyMessageDelayed(1, 2000);
								
								act.finish();
								System.exit(0);
							}
						}).setNegativeButton("取消", null).create().show();
			}
		});

	}

	/**
	 * COCOS调用
	 * 
	 * @param1 退出游戏
	 */
	public void preExitGame(final Activity act, final Runnable runnable) {
		Log.i(TAG, "PaymentJoy preExitGame");
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(act).setTitle("信息")
						.setMessage("是否退出游戏?")
						.setPositiveButton("退出", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								final LinearLayout layout = new LinearLayout(
										act);
								layout.setGravity(Gravity.CENTER);
								layout.setBackgroundColor(Color.rgb(255, 255,
										255));
								TextView textView = new TextView(act);
								textView.setText("这里将展示渠道商/运营商LOGO");

								final ImageView iView = new ImageView(act);
								layout.addView(
										textView,
										new LinearLayout.LayoutParams(
												LinearLayout.LayoutParams.FILL_PARENT,
												LinearLayout.LayoutParams.FILL_PARENT));
								InputStream is = act
										.getClassLoader()
										.getResourceAsStream(
												"paymentSdkV2_res/payment_lt_logo.png");
								Drawable drawable = Drawable.createFromStream(
										is, "src");
								iView.setImageDrawable(drawable);
								iView.setVisibility(View.VISIBLE);
								act.addContentView(layout, new LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.FILL_PARENT));
								new Handler() {
									@Override
									public void handleMessage(Message msg) {
										runnable.run();
										act.finish();
										System.exit(0);
									}
								}.sendEmptyMessageDelayed(1, 2000);
							}
						}).setNegativeButton("取消", null).create().show();
			}
		});

	}

	/**
	 * android调用
	 * 
	 * @param1 进入主菜单之前
	 */
	public void preEntryMenu(final Activity act) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// Toast.makeText(act, "PaymentJoy preEntryMenu",
				// Toast.LENGTH_SHORT).show();
				Log.i(TAG, "PaymentJoy preEntryMenu");
			}
		});
	}

	/**
	 * J2ME调用
	 * 
	 * @param1 退出游戏
	 */
	public void preExitGame() {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// Toast.makeText(mActivity, "PaymentJoy preExitGame",
				// Toast.LENGTH_SHORT).show();

				Log.i(TAG, "PaymentJoy preExitGame");
				mActivity.finish();
			}
		});
	}

	/**
	 * J2ME调用
	 * 
	 * @param1 1进入主菜单之前
	 */
	public void preEntryMenu() {
		// Toast.makeText(mActivity, "PaymentJoy preEntryMenu",
		// Toast.LENGTH_SHORT)
		// .show();
		Log.i(TAG, "PaymentJoy preEntryMenu");
	}

	/**
	 * 
	 * @param1 是否有更多游戏按钮
	 */
	public boolean isHasMoreGame() {
		Log.i(TAG, "PaymentJoy isHasMoreGame");

		return false;
	}
	
	
	public void startCharge(final Activity act, final PaymentParam param) {
		act.runOnUiThread(new Runnable() {
			public void run() {

				alertDialog = new AlertDialog.Builder(act)
						.setTitle("提示")
						.setCancelable(false)
						.setMessage("即将支付的计费点序号为:" + param.getmChargePt())
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (PaymentJoy.mInstance != null) {
											String[] result = new String[4];
											result[0] = "";
											result[1] = "";
											result[2] = "";
											result[3] = "";
											PaymentJoy.mCb.PaymentResult(1,
													result);
										} else {
											Log.e("startCharge",
													"Please create instance first!");
										}
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										String[] result = new String[4];
										result[0] = "";
										result[1] = "";
										result[2] = "";
										result[3] = "";
										PaymentJoy.mCb.PaymentResult(2, result);
									}
								}).create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();

				// Toast.makeText(PaymentJoy.mActivity,
				// "PaymentJoy startCharge",
				// 0).show();
				Log.i("PaymentJoy", "PaymentJoy startCharge");
			}
		});
	}




	public void startCharge(final PaymentParam param) {

//		doCharge(param.getmChargePt());
		mActivity.runOnUiThread(new Runnable() {
			public void run() {

				alertDialog = new AlertDialog.Builder(PaymentJoy.mActivity)
						.setTitle("提示")
						.setCancelable(false)
						.setMessage("即将支付的计费点序号为:" + param.getmChargePt())
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (PaymentJoy.mInstance != null) {
											String[] result = new String[4];
											result[0] = param.getmChargePt()+"";
											result[1] = "";
											result[2] = "";
											result[3] = "";
											PaymentJoy.mCb.PaymentResult(1,
													result);
										} else {
											Log.e("startCharge",
													"Please create instance first!");
										}
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										String[] result = new String[4];
										result[0] = param.getmChargePt()+"";
										result[1] = "";
										result[2] = "";
										result[3] = "";
										PaymentJoy.mCb.PaymentResult(2, result);
									}
								}).create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();

				// Toast.makeText(PaymentJoy.mActivity,
				// "PaymentJoy startCharge",
				// 0).show();
				Log.i("PaymentJoy", "PaymentJoy startCharge");
			}
		});
	}

	public static void onPause() {
		Log.i(TAG, "PaymentJoy onPause");
	}

	public static void onResume() {
		Log.i(TAG, "PaymentJoy onResume");
	}

	public static void onStart() {
		// Toast.makeText(mActivity, "PaymentJoy onStart", Toast.LENGTH_SHORT)
		// .show();
		Log.i(TAG, "PaymentJoy onStart");
	}

	public static void onDestroy() {
		// Toast.makeText(mActivity, "PaymentJoy onDestroy", Toast.LENGTH_SHORT)
		// .show();
		Log.i(TAG, "PaymentJoy onDestroy");
	}

	public static void onStop() {
		// Toast.makeText(mActivity, "PaymentJoy onStop", Toast.LENGTH_SHORT)
		// .show();
		Log.i(TAG, "PaymentJoy onStop");
	}

	public static String Mapid = "0";
	private static AlertDialog alertDialog;

	public static void doCharge(final int chargeIndex) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				final String mapId = Mapid;

				resultMap.put(chargeIndex, -1);
				Toast.makeText(mActivity, mapId, Toast.LENGTH_SHORT).show();
				final PaymentParam param = new PaymentParam(chargeIndex);
				alertDialog = new AlertDialog.Builder(mActivity).setTitle("提示")
						.setCancelable(false)
						.setMessage("即将支付的计费点序号为:" + chargeIndex)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (mInstance != null) {
									String[] result = new String[4];
									result[0] = String.valueOf(param
											.getmChargePt());
									result[1] = new Random().nextInt(10) + "";
									result[2] = "";
									result[3] = "";
									nativeCb.PaymentResult(
											PaymentResultCode.PAYMENT_SUCCESS,
											result);
									
//									PaymentJoy.mCb.PaymentResult(1, result);

									new Thread() {
										public void run() {
											try {

												String uid;
												try {
													uid = SdkAPI.getUid();
												} catch (Throwable e) {
													uid = "111111111";
													// TODO: handle exception
												}

												// http://hijoyusers.joymeng.com:8100/Initlog/initLog?uid=123456&app_id=255&pid=99&cid=1
												NetworkManager networkManager = new NetworkManager(
														mActivity);
												networkManager
														.addUrlNameValuePair(
																"uid", uid);
												networkManager
														.addUrlNameValuePair(
																"app_id",
																getgameid());
												networkManager
														.addUrlNameValuePair(
																"pid", mapId);
												networkManager
														.addUrlNameValuePair(
																"cid",
																""
																		+ chargeIndex);

												String resp = networkManager
														.SendAndWaitResponse("http://hijoyusers.joymeng.com:8100/Initlog/initLog");
												Log.e(TAG, "resp is" + resp);

											} catch (Exception e) {
												Log.e(TAG, "can not post data");
												e.printStackTrace();
											}
										};
									}.start();

								} else {
									Log.e("startCharge",
											"Please create instance first!");
								}

							}
						}).setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String[] result = new String[4];
								result[0] = String.valueOf(param.getmChargePt());
								result[1] = "";
								result[2] = "";
								result[3] = "";
								nativeCb.PaymentResult(
										PaymentResultCode.PAYMENT_FAILED,
										result);
//								PaymentJoy.mInstance.nativeCb
//								PaymentJoy.mCb.PaymentResult(2, result);
							}
						}).create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();

				// Toast.makeText(mActivity, "PaymentJoy startCharge",
				// Toast.LENGTH_SHORT).show();
				Log.i(TAG, "PaymentJoy startCharge");
			}
		});
	}

	/***
	 * 爱贝接口 第一个参数是计费点 第2个参数是支付类型 当前有ZFB(支付包) WX(微信) ,CHANNEL
	 * 
	 * **/

	public static void doCharge(final int chargeIndex, final String json) {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				resultMap.put(chargeIndex, -1);
				final PaymentParam param = new PaymentParam(chargeIndex);
				alertDialog = new AlertDialog.Builder(mActivity).setTitle("提示")
						.setCancelable(false)
						.setMessage(json + "第三方支付计费点序号为:" + chargeIndex)
						.setPositiveButton("确认", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (mInstance != null) {
									String[] result = new String[4];
									result[0] = String.valueOf(param
											.getmChargePt());
									result[1] = new Random().nextInt(10) + "";
									result[2] = "";
									result[3] = "";
									nativeCb.PaymentResult(
											PaymentResultCode.PAYMENT_SUCCESS,
											result);
								} else {
									Log.e("startCharge",
											"Please create instance first!");
								}

							}
						}).setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String[] result = new String[4];
								result[0] = String.valueOf(param.getmChargePt());
								result[1] = "";
								result[2] = "";
								result[3] = "";
								nativeCb.PaymentResult(
										PaymentResultCode.PAYMENT_FAILED,
										result);
							}
						}).create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();

				Log.i(TAG, "PaymentJoy doCharge channel");
			}
		});

	}

	public static String getPayInfo() {
		return "0";
	}

	public static void donewCharge(int chargeIndex, boolean addhint) {
		doCharge(chargeIndex);

	}

	public static void donewCharge(int chargeIndex, boolean addhint, int mapid) {
		doCharge(chargeIndex);

	}

	public static String getChargePrice(int chargeIndex) {

		return "2";
	}

	public static void setMapid(final int mapid) {

		if ((mapid + "").length() == 1) {
			Mapid = "0" + mapid;
		} else {
			Mapid = mapid + "";
		}

		// mActivity.runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// Toast.makeText(mActivity, "mapid "+mapid,Toast.LENGTH_SHORT).show();
		//
		// }
		// });

	}

	public static String getPcardType(Activity act) {
		String mOperatorNumber = "";
		String mOperatorName = "";
		try {
			TelephonyManager manager = (TelephonyManager) (act
					.getSystemService(Context.TELEPHONY_SERVICE));
			mOperatorNumber = manager.getSimOperator();

			if (mOperatorNumber != null && !"".equals(mOperatorNumber)) {
				if (mOperatorNumber.equals("46000")
						|| mOperatorNumber.equals("46002")
						|| mOperatorNumber.equals("46007")) {
					mOperatorName = "CM";
				} else if (mOperatorNumber.equals("46001")
						|| mOperatorNumber.equals("46006")) {
					mOperatorName = "CU";
				} else if (mOperatorNumber.equals("46003")
						|| mOperatorNumber.equals("46005")) {
					mOperatorName = "CT";
				} else {
					mOperatorName = "CM";
				}
			} else {
				mOperatorName = "NOSIM";
			}

		} catch (Exception e) {
			mOperatorName = "NOSIM";
			e.printStackTrace();
			// TODO: handle exception
		}

		return mOperatorName;
	}

	/****
	 * 
	 *  
	 * 
	 * ***/

	public static String getPayType() {

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(mActivity, "PaymentJoy getPayType",
						Toast.LENGTH_SHORT).show();
				Log.i(TAG, "PaymentJoy getPayType");
			}
		});

		return "SMSCHANNEL";
	}

	public static boolean IsHaveInternet(Activity act) {
		try {
			Log.i(TAG, "PaymentJoy IsHaveInternet");
			ConnectivityManager manger = (ConnectivityManager) act
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}

	}
	
	  //是否是模拟点击
	  private static boolean isBwc = false;
	  
	  //设置模拟点击状态
	 public static void  setBwc(boolean bwc){
		 isBwc = bwc;
		 Log.i("Payment","isBwc ="+isBwc);
	 }
	 //获取模拟点击状态
	 public static boolean  getBwc(){
		 if(getsTBwc().equals("1")){
			 isBwc = true; 
		 }else{
			 isBwc = false; 
		 }
		 return isBwc;
	 }
	 
	 
	  //是否是包月状态
	  private static boolean isMonthlyStatus = false;
	  //设置模拟点击状态
		 public static void  setMonthlyStatus(boolean MonthlyStatus){
			 isMonthlyStatus = MonthlyStatus;
		 }
	 //返回给游戏电信包月状态，方便游戏查看
	 public static  boolean getMonthlyStatus(){
		 return isMonthlyStatus;
	 }
	

	public static void setMobileNet(Activity act, boolean open) {

		try {
			Log.i(TAG, "PaymentJoy setMobileNet");
			ConnectivityManager manager = (ConnectivityManager) act
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class<?> cls = manager.getClass();
			Method method = cls.getMethod("setMobileDataEnabled",
					new Class[] { boolean.class });
			method.invoke(manager, open);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initOperator(Activity act) {

		Log.i(TAG, "PaymentJoy initOperator");

	}

	public static int getResult(int chargeIndex) {
		Integer result = resultMap.get(chargeIndex);
		if (result != null) {
			Log.i(TAG, "result ==> " + result);
			return result;
		} else {
			Log.i(TAG, "no result for this chargeIndex");
			return -1;
		}
	}

	/**
	 * 游戏中调暂停界面 调用此方法
	 * */
	public static void pause(Activity act) {

	}

	public static void startMoreGame(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Uri uri = Uri
						.parse("http://list.mobappbox.com/android?language=en&tag=mobappbox");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				activity.startActivity(intent);
			}
		});
	}

	public static boolean haveMoreGame() {
		return mInstance.isHasMoreGame();
	}

	public static void exitGame(Activity act) {
		mInstance.preExitGame(act);
	}

	public static boolean isCT() {
		TelephonyManager manager = (TelephonyManager) (mActivity
				.getSystemService(Context.TELEPHONY_SERVICE));
		String imsi = manager.getSubscriberId();
		if (imsi != null) {
			if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// 控制 移动的音乐开关
	public static boolean isMusicon() {
		Log.i(TAG, "isMusicon");
		return true;
	}

	public static boolean isCMuser() {
		TelephonyManager manager = (TelephonyManager) (mActivity
				.getSystemService(Context.TELEPHONY_SERVICE));
		String opName = manager.getSimOperatorName();
		String imsi = manager.getSubscriberId();
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")
					|| opName.equals("中国移动") || imsi.startsWith("46007")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// 判断是否是移动的卡
	public boolean isCM() {
		TelephonyManager manager = (TelephonyManager) (mActivity
				.getSystemService(Context.TELEPHONY_SERVICE));
		String opName = manager.getSimOperatorName();
		String imsi = manager.getSubscriberId();
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")
					|| opName.equals("中国移动") || imsi.startsWith("46007")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static String pubKey = "000000";
	public static String signNumber = "00000000000000000000";

	public static String getpubkey(Activity act) {
		Log.i(TAG, "signNumber ==>" + signNumber);
		return signNumber;
	}

	public static String gameId = "155";

	public static String getcid() {
		Properties properties = getProperties();
		String chanid = properties.getProperty("channelId");
		return chanid;
	}

	public static String getCpId(Context con){
		Properties properties = getProperties(con);
		String cpId = properties.getProperty("cpId");
		return cpId;
	}

	
	
	public static String getsTBwc() {
		Properties properties = getProperties();
		String chanid = properties.getProperty("Bwc");
		return chanid;
	}

	public static String getcid(Context con) {

		Properties properties = getProperties(con);
		String chanid = properties.getProperty("channelId");

		return chanid;
	}

	private static Properties getProperties() {
		try {
			InputStream is = mActivity.getAssets().open("cha.chg");

			Properties properties = new Properties();
			properties.load(is);
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Properties getProperties(Context con) {
		try {
			InputStream is = con.getAssets().open("cha.chg");

			Properties properties = new Properties();
			properties.load(is);
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getfee(Context con){
		Properties properties = getProperties(con);
		String fee = properties.getProperty("Fee");
		return fee;
	}

	public static String getgameid() {

		Properties properties = getProperties();
		gameId = properties.getProperty("gameId");
		return gameId;
	}

	public static String getgameid(Context con) {
		Properties properties = getProperties(con);
		gameId = properties.getProperty("gameId");
		return gameId;
	}

	/****
	 * @author wujinzhong 获取帮助和关于信息
	 * 
	 ***/
	public static String helpstr = "";
	public static String aboutstr = "";

	public static String gethelpstr() {
		if (TextUtils.isEmpty(helpstr)) {
			int start = gethelpabout().indexOf("help=");
			int end = gethelpabout().indexOf("about");
			start = start + 5;
			helpstr = gethelpabout().substring(start, end);
		}

		return helpstr;
	}

	public static String getaboutstr() {
		if (TextUtils.isEmpty(aboutstr)) {
			int start = gethelpabout().indexOf("about=");
			int end = gethelpabout().indexOf("end");
			start = start + 6;
			aboutstr = gethelpabout().substring(start, end);

		}

		return aboutstr;
	}

	private static String gethelpabout() {
		try {
			InputStream in;
			in = mActivity.getAssets().open("helpabout");
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			String res = EncodingUtils.getString(buffer, "UTF-8");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Unity SDK新增方法用于设置回调的GameObject名称和返回计费结果
	 */
	private static String HANDLE_NAME;

	public static void setHandleName(String name) {
		if (name != null && !name.equals("")) {
			HANDLE_NAME = name;
		} else {
			throw new NullPointerException("handle name is null");
		}
	}

	private static PaymentCb nativeCb = new PaymentCb() {
		@Override
		public void PaymentResult(int resultCode, String[] cbParam) {
			resultMap.put(Integer.parseInt(cbParam[0]), resultCode);
			try {
				Class.forName("com.unity3d.player.UnityPlayer");
				UnityPlayer.UnitySendMessage("JavaInterface", "handleResult",
						resultCode + "");
			} catch (Throwable e) {
				e.printStackTrace();
//				PaymentJoy.mCb.PaymentResult(resultCode, cbParam);
				// TODO: handle exception
			}

		}
	};

	public static String getprice() {

		String price = "";

		Properties properties = getProperties();
		price = properties.getProperty("param1");
		return price;

	}

	/**
	 * da
	 */
	public static void cpda() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, "cpda");
			}
		});

	}

	/**
	 * da
	 */
	public static void bnda() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, "bnda");
			}
		});

	}

	/**
	 * da
	 */
	public static void poda() {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, "poda");
			}
		});

	}

}
