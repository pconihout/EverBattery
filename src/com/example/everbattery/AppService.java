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
	
	// METHODES
	public void onCreate() {
		Log.i("EverBattery", "AppService : onCreate()");
		
		if (screenReceiver != null) 
			unregisterReceiver(screenReceiver);
		
		// On créé les Receiver
		// - screen on/off
		intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		screenReceiver = new ScreenReceiver();
		registerReceiver(screenReceiver, intentFilter);
		
		// - wifi activé/desactivé
		// - 
		// - 
	
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
    	
    	
    	// Destroy OffService
        Intent i = new Intent(getApplicationContext(), OffService.class);
        getApplicationContext().stopService(i);
		 
    	super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	
	// FONCTIONS
	public void createNotification(NotificationManager notificationmanager) {
        // Open NotificationView Class on Notification Click
        PendingIntent mIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent aIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent sIntent =  PendingIntent.getBroadcast(this, 0, new Intent("stopAppService"), PendingIntent.FLAG_UPDATE_CURRENT);
 
 
        //Create Notification using NotificationCompat.Builder 
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.ic_launcher)
                // Set Ticker Message
                .setTicker("EverBattery is running...")
                // Set Title
                .setContentTitle("EverBattery")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set Text
                .setContentText("EverBattery is running. Touch to open.")
                
                // Add an Action Button below Notification
                .addAction(0, "Switch off", sIntent)//replace 0 by R.drawable.btn_off_notif
                
                // Set PendingIntent into Notification = MainActiv
                .setContentIntent(mIntent)
                // Can't erase the notification
        		.setOngoing(true)
        		.setWhen(0);
 
        // Create Notification Manager
        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
        
	}
	
	public void killNotification(NotificationManager notificationmanager){
    	// Cancel notification
    	notificationmanager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationmanager.cancel(0);
	}
}
