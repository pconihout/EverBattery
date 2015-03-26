/* MainActivity -> launch AppService
 * 
 * 
 */

package com.example.everbattery;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class OffService extends Service {
	

	
	// METHODES
	public void onCreate() {
	
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("EverBattery", "OffService : onStart()");
		

	}
	

	@Override
	public void onDestroy() {
		Log.i("EverBattery", "OffService : onDestroy()");
		
		 
    	super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
