/* MainActivity -> launch AppService
 * 
 * 
 */

package com.example.everbattery;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AppService extends Service {
	
	// ATTRIBUTS
	
	// Receivers
	private BroadcastReceiver wifiReceiver = null;
	private BroadcastReceiver blueReceiver = null;
	private BroadcastReceiver screenReceiver = null;
	
	// Filters
	private IntentFilter intentFilter = null;
	
	
	// Fonctions
	private Functions f = new Functions();
	
	// METHODES
	
	public void onCreate() {
		Log.i("EverBattery", "AppService : onCreate()");
		
		// Receivers in thread
		/*
		HandlerThread handlerThread = new HandlerThread("ht");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		Handler handler = new Handler(looper);
		
		
		HandlerThread handlerThread_ = new HandlerThread("ht_");
		handlerThread_.start();
		Looper looper_ = handlerThread_.getLooper();
		Handler handler_ = new Handler(looper_);
		*/
		if (screenReceiver != null) 
			unregisterReceiver(screenReceiver);
		if (wifiReceiver != null) 
			unregisterReceiver(screenReceiver);
		if (blueReceiver != null) 
			unregisterReceiver(screenReceiver);
		
		// On créé les Receiver
		// - screen on/off
		intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenReceiver = new ScreenReceiver();
		registerReceiver(screenReceiver, intentFilter);
		
		// - bluetooth activé/desactivé manuellement
		intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		blueReceiver = new BlueReceiver();
		registerReceiver(blueReceiver, intentFilter);
		
		// - wifi activé/desactivé manuellement
		intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
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
    	if (blueReceiver != null) {
    		unregisterReceiver(blueReceiver);
    		blueReceiver = null;
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
