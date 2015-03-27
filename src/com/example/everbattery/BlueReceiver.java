package com.example.everbattery;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

public class BlueReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		SharedPreferences settings = context.getSharedPreferences("EverBattery", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit();
		
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
		

        if ((state == BluetoothAdapter.STATE_ON) && !settings.getBoolean("blue_enabled", true)) { 

            Log.i("EverBattery", "BlueReceiver - Bluetooth has been enabled manually");
            
            editor.putBoolean("blue_enabled", true);
        	editor.commit();
        } 
        else if ((state == BluetoothAdapter.STATE_OFF) && settings.getBoolean("blue_enabled", false) && powerManager.isScreenOn()){ 

            Log.i("EverBattery", "BlueReceiver - Bluetooth has been disabled manually");
            
            editor.putBoolean("blue_enabled", false);
        	editor.commit();
        } 
        
	
	}

}
