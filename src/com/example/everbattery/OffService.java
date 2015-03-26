/* MainActivity -> launch AppService
 * 
 * 
 */

package com.example.everbattery;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class OffService extends Service {
	
	// ATTRIBUTS
	private Functions f = new Functions();
	private BackgroundSync backsync = null;
	
	
	// METHODE
	public void onCreate() {
		
	
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("EverBattery", "OffService : onStart()");
		backsync = new BackgroundSync();

		f.stopConnection(getApplicationContext());
			

		backsync.start();
	}
	

	@Override
	public void onDestroy() {
		Log.i("EverBattery", "OffService : onDestroy()");
    	
		if (backsync != null) {
			backsync.interrupt();
			backsync = null;
		}
    	
		f.initConnection(getApplicationContext());
		 
    	super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	public class BackgroundSync extends Thread {
		Handler handler = new Handler();
		
		private Runnable endsync = new Runnable() { 
			@Override
	         public void run() { 
				 f.stopConnection(getApplicationContext());
	        	 Log.i("EverBattery", "OffService - Autosync : Timer finished.");
	         } 
	    };
	    
		private Runnable autosync = new Runnable() {
			
			   @Override
			   public void run() {
			    	Log.i("EverBattery", "OffService - Autosync: Timer - 30s");
				    
					 
			    	f.initConnection(getApplicationContext());
			
				    handler.postDelayed(endsync, 1000*20); 
					
					if (handler != null)
						handler.postDelayed(this, 1000*30*1);
			   }
		};
		
		@Override
		public void run(){
			if (handler == null)
				handler = new Handler();
			
			handler.postDelayed(autosync, 1000*30*1);
		}
		
		@Override
		public void interrupt() {
			Log.i("EverBattery", "BackgroundSync - interrupt()");
			
			handler.removeCallbacks(endsync);
			handler.removeCallbacks(autosync);
			endsync = null;
			autosync = null;
			handler = null;
			
			super.interrupt();
		}
	}
}
		

