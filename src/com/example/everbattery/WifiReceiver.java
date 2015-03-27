package com.example.everbattery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		SharedPreferences settings = context.getSharedPreferences("EverBattery", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit();
		
		int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
		
		// L'écran se vérouille
        if ((state == WifiManager.WIFI_STATE_ENABLED) && !settings.getBoolean("wifi_enabled", true)) { 

            Log.i("EverBattery", "WifiReceiver - Wifi has been enabled manually");
            
            editor.putBoolean("wifi_enabled", true);
        	editor.commit();
        } 
        else if ((state == WifiManager.WIFI_STATE_DISABLED) && settings.getBoolean("wifi_enabled", false) && powerManager.isScreenOn()){ 

            Log.i("EverBattery", "WifiReceiver - Wifi has been disabled manually");
            
            editor.putBoolean("wifi_enabled", false);
        	editor.commit();
        } 
        
	
	}

}
