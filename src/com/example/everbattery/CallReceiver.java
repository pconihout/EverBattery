package com.example.everbattery;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  
		  if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
	                TelephonyManager.EXTRA_STATE_IDLE) ) {
			  
			  KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			  
			  Log.i("EverBattery", "CallReceiver - end of call");
			  
			  
			  if (!myKM.inKeyguardRestrictedInputMode()) {
		            Intent i = new Intent(context, OffService.class);
		            context.stopService(i);
			  }
			  else  {
		            Intent i = new Intent(context, OffService.class);
		            context.startService(i);
			  }
			 
	        }

	  }
	}
