/*
 * ScreenReceiver - détermine si l'utilisateur verrouille ou déverouille l'écran
 */

package com.example.everbattery;

import com.google.gson.Gson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
	private boolean wasOn = true;
	private SharedPreferences settings;
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		SharedPreferences settings = context.getSharedPreferences("EverBattery", Context.MODE_MULTI_PROCESS);

		// On veut que screen fonctionne
		if (settings.getBoolean("appservice_enabled", true)){
			// L'écran se vérouille
	        if ((intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) && wasOn) { 
	
	            Log.i("EverBattery", "ScreenReceiver - Screen turned off");
	            
	            wasOn = false;
	           
	  
	            // We launch OffService - 
	            Intent i = new Intent(context, OffService.class);
	            context.startService(i);
	        } 
	        
	        // L'utilisateur déverouille l'écran
	        else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){ 
	            
	            Log.i("EverBattery", "ScreenReceiver - Screen turned on");
	            
	            wasOn = true;
	            
	            // We stop OffService - 
	            Intent i = new Intent(context, OffService.class);
	            context.stopService(i);
	        }
	        
			
		}
	}

}
