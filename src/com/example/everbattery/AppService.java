/* MainActivity -> launch AppService
 * 
 * 
 */

package com.example.everbattery;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AppService extends Service {
	
	// ATTRIBUTS
	
	// Receivers
	private BroadcastReceiver wifiReceiver = null;
	private BroadcastReceiver screenReceiver = null;
	
	// Filters
	private IntentFilter intentFilter = null;
	
	
	// Fonctions
	private Functions f = new Functions();
	
	// METHODES
	
	public void onCreate() {
		Log.i("EverBattery", "AppService : onCreate()");
		
		if (screenReceiver != null) 
			unregisterReceiver(screenReceiver);
		if (wifiReceiver != null) 
			unregisterReceiver(screenReceiver);
		
		// On créé les Receiver
		// - screen on/off
		intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		screenReceiver = new ScreenReceiver();
		registerReceiver(screenReceiver, intentFilter);
		
		// - wifi activé/desactivé
		intentFilter = new IntentFilter(WifiManager.EXTRA_WIFI_STATE);
		
		wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, intentFilter);
		
		// - le service se tue lui-même
		registerReceiver(stopServiceReceiver, new IntentFilter("stopAppService"));
		
		// - 
		
		
		// Création de la notification
        f.initNotification(getApplicationContext());
	
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("EverBattery", "AppService : onStart()");
		

	}
	

	@Override
	public void onDestroy() {
		Log.i("EverBattery", "AppService : onDestroy()");
		
    	// Cancel Receiver
    	if (screenReceiver != null) {
    		unregisterReceiver(screenReceiver);
    		screenReceiver = null;
    	}
    	if (wifiReceiver != null) {
    		unregisterReceiver(wifiReceiver);
    		wifiReceiver = null;
    	}
    	if (stopServiceReceiver != null) {
    		unregisterReceiver(stopServiceReceiver);
    		stopServiceReceiver = null;
    	}
    	
    	// Destroy OffService
        Intent i = new Intent(getApplicationContext(), OffService.class);
        getApplicationContext().stopService(i);
        
        
        // Destroy notification
        f.stopNotification(getApplicationContext());
		 
    	super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	//We need to declare the receiver with onReceive function as below
	protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {   
		  @Override
		  public void onReceive(Context context, Intent intent) {
		
		  Intent local = new Intent();
		  local.setAction("stopMainActivity");
		  sendBroadcast(local);
			  
		  stopSelf();
				  
		  // on stoppe l'activité
		  Intent myIntent = new Intent(AppService.this, MainActivity.class);
		  myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  
		  Bundle myKillerBundle = new Bundle();
		  
		  myKillerBundle.putInt("kill",1);
		  
		  myIntent.putExtras(myKillerBundle);
		  
		  getApplication().startActivity(myIntent);
		  }
	};

}
