package com.joymeng.paydemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.joym.PaymentSdkV2.Logic.PaymentResultCode;
import com.joym.PaymentSdkV2.PaymentCb;
import com.joym.PaymentSdkV2.PaymentJoy;
import com.zm.R;

/*** should implement the payment sdk interface */
public class UnityPayDemoActivity extends Activity implements OnClickListener,
		PaymentCb {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		
		/*** 此方法必须在游戏的主activity中调用，并且在所有其他方法之前调用 */
		PaymentJoy.onCreate(this);
		PaymentJoy.getInstance(this);
	
		

		Button payButton = (Button) findViewById(R.id.pay);
		payButton.setOnClickListener(this);

		Button exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(this);

		Button menuButton = (Button) findViewById(R.id.menu);
		menuButton.setOnClickListener(this);
		
		Button ishavemoreButton = (Button) findViewById(R.id.ishavemore);
		ishavemoreButton.setOnClickListener(this);
		
		Button musiconButton = (Button) findViewById(R.id.musicon);
		musiconButton.setOnClickListener(this);
		

		Button IntentButton = (Button) findViewById(R.id.ishaveIntent);
		IntentButton.setOnClickListener(this);
		
		Button channidButton = (Button) findViewById(R.id.channelid);
		channidButton.setOnClickListener(this);
		
		Button gameidButton = (Button) findViewById(R.id.gameid);
		gameidButton.setOnClickListener(this);
		
		
		Button helpButton = (Button) findViewById(R.id.help);
		helpButton.setOnClickListener(this);
		
		Button aboutButton = (Button) findViewById(R.id.about);
		aboutButton.setOnClickListener(this);
		
//		Button resultButton = (Button) findViewById(R.id.result);
//		resultButton.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
    	  Log.i("PaymentJoy", "exit");
//       PaymentJoy.exitGame(UnityPayDemoActivity.this);
        return true;
      }
      return super.onKeyDown(keyCode, event);
    }

	public void onClick(View v) {
		if (v instanceof Button) {
			int id = ((Button) v).getId();

			if (id == R.id.pay) {
				PaymentJoy.doCharge(1);
//				PaymentJoy.getInstance(this).startCharge(new PaymentParam(1));
			}

			else if (id == R.id.exit) {
				PaymentJoy.exitGame(UnityPayDemoActivity.this);
				
			}

			else if (id == R.id.menu) {
				
				if(PaymentJoy.haveMoreGame()){
					
					PaymentJoy.startMoreGame(UnityPayDemoActivity.this);
				}else{
					Toast.makeText(this, "没有更多游戏 " + PaymentJoy.haveMoreGame(),
							Toast.LENGTH_SHORT).show();
				}
			}

			else if (id == R.id.ishavemore) {
				Toast.makeText(this, "是否有更多游戏 " + PaymentJoy.haveMoreGame(),
				Toast.LENGTH_SHORT).show();
			}
			
			else if (id == R.id.musicon) {
				Toast.makeText(this, "音乐开关 " + PaymentJoy.isMusicon(),
				Toast.LENGTH_SHORT).show();
			}
			
			else if (id == R.id.ishaveIntent) {
				Toast.makeText(this, "当前网络是否打开 " + PaymentJoy.IsHaveInternet(UnityPayDemoActivity.this),
				Toast.LENGTH_SHORT).show();
			}
			
			
			else if (id == R.id.gameid) {
				Toast.makeText(this, "gameid " + PaymentJoy.getgameid(),
				Toast.LENGTH_SHORT).show();
			}
			
			
			else if (id == R.id.channelid) {
				Toast.makeText(this, "channid " + PaymentJoy.getcid(),
				Toast.LENGTH_SHORT).show();
			}
			
			else if (id == R.id.help) {
				Toast.makeText(this, "help内容 " + PaymentJoy.gethelpstr(),
				Toast.LENGTH_SHORT).show();
			}
			else if (id == R.id.about) {
				Toast.makeText(this, "about内容 " + PaymentJoy.getaboutstr(),
				Toast.LENGTH_SHORT).show();
			}
		
		
		
		
		
		}
	}

	/**
	 * @payment result call back
	 */
	@Override
	public void PaymentResult(int resultCode, String[] cbParam) {

		Log.e("MainActivity", "result + " + resultCode);

		if (PaymentResultCode.PAYMENT_SUCCESS == resultCode) {
			Toast.makeText(this, "pay success, charge point " + cbParam[3],
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "else：" + resultCode, Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * @ should call PaymentJoy.onDestroy if any.
	 */
	protected void onDestroy() {
		super.onDestroy();
		PaymentJoy.onDestroy();
	}

	/**
	 * @ should call PaymentJoy.onStop if any.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		PaymentJoy.onStart();
	}

	/**
	 * @ should call PaymentJoy.onStop if any.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		PaymentJoy.onStop();
	}

}
