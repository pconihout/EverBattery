package com.example.everbattery;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
        settings = context.getSharedPreferences("EverBattery", Context.MODE_MULTI_PROCESS);
        editor = settings.edit();
		  
		if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK) ) {
			Log.i("EverBattery", "CallReceiver - offhook");
            editor.putBoolean("appservice_enabled", false);
        	editor.commit();
		}
		else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE) ) {
			Log.i("EverBattery", "CallReceiver - idle");
            editor.putBoolean("appservice_enabled", true);
        	editor.commit();
		}
	}
}
