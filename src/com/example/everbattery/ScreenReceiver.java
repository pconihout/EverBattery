/*
 * ScreenReceiver - détermine si l'utilisateur verrouille ou déverouille l'écran
 */

package com.example.everbattery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		// L'écran se vérouille
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) { 

            Log.i("EverBattery", "ScreenReceiver - Screen turned off");
           
  
            // We launch OffService - 
            Intent i = new Intent(context, OffService.class);
            context.startService(i);
        } 
        
        // L'utilisateur déverouille l'écran
        else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){ 
            
            Log.i("EverBattery", "ScreenReceiver - Screen turned on");
            
            // We stop OffService - 
            Intent i = new Intent(context, OffService.class);
            context.stopService(i);
        }
        
		
	}

}
