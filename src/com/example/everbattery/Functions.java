package com.example.everbattery;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Functions {
	
	private static SharedPreferences settings;
	private NotificationManager notificationmanager;
	
	
	public void initConnection(Context context){
		setDataEnabled(context, true);
		setWifiEnabled(context, true);
	}
	
	public void stopConnection(Context context){
		
		if (isSharingWiFi(context)) {
			setDataEnabled(context, false);
			setWifiEnabled(context, false);
		}
	}
	
	public Boolean isMobileDataEnabled(Context context){
    Object connectivityService = context.getSystemService(Context.CONNECTIVITY_SERVICE); 
    ConnectivityManager cm = (ConnectivityManager) connectivityService;
	
	    try {
	        Class<?> c = Class.forName(cm.getClass().getName());
	        Method m = c.getDeclaredMethod("getMobileDataEnabled");
	        m.setAccessible(true);
	        
	    return (Boolean)m.invoke(cm);	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    return null;
	    }
	}
	
	
	
	public boolean setDataEnabled(Context context, boolean enabled){
	Class<?> conmanClass = null;
	Field connectivityManagerField = null;
	Object connectivityManager = null;
	Class<?> connectivityManagerClass = null;
	Method setMobileDataEnabledMethod = null;

		final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			conmanClass = Class.forName(conman.getClass().getName());
		} catch (Exception e) {
		}
		try {
			connectivityManagerField = conmanClass.getDeclaredField("mService");
		} catch (Exception e) {
		}
		connectivityManagerField.setAccessible(true);
		try {
			connectivityManager = connectivityManagerField.get(conman);
		} catch (Exception e) {
		}
		try {
			connectivityManagerClass = Class.forName(connectivityManager
					.getClass().getName());
		} catch (Exception e) {
		}
		try {
			setMobileDataEnabledMethod = connectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		} catch (Exception e) {
		}

		try {
			// Verif si 3G desactiv & qu'on veut activ
			if (!isMobileDataEnabled(context) && enabled) {
				setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
				Log.i("EverBattery", "setMobileData : Data is enabled");
			}
			// 3G activ && on veut desact
			else if (isMobileDataEnabled(context) && !enabled) {
				setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
				Log.i("EverBattery", "setMobileData : Data is disabled");
			}
			else
				Log.i("EverBattery", "setMobileData : No operations on data");
			
			
			return true; 
			
		} 
		catch (Exception e) {

			return false;
		}
	 }
	
	public static boolean isSharingWiFi(Context context)
	{
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
	    try
	    {
	    	
	        Method method = manager.getClass().getDeclaredMethod("isWifiApEnabled");
	        method.setAccessible(true); //in the case of visibility change in future APIs
	        
	        Log.i("EverBattery", "isSharingWifi() = " + method.invoke(manager));
	        
	        return (Boolean) method.invoke(manager);
	    }
	    catch (final Throwable ignored)
	    {
	    }

	    return false;
	}
	
	public void setWifiEnabled(Context context, boolean enabled) {
    	// On check  si le WiFi était activé avant le lancement de l'appli
    	boolean wifiEnabled = true;
    	
		settings = context.getSharedPreferences("EverBattery", Context.MODE_MULTI_PROCESS);
    	
    	// On récupère paramètres
    	wifiEnabled = settings.getBoolean("wifi_enabled", true);
    	
    	Log.i("EverBattery", "setWifi : Wifi = " + wifiEnabled);
	    
	    if  (wifiEnabled) {
	    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	    	wifiManager.setWifiEnabled(enabled); // true pour activer
	    	
			if (enabled)
				Log.i("EverBattery", "setWifi: Wifi is enabled");
			else
				Log.i("EverBattery", "setWifi : Wifi is disabled");
	    }
	}
	   
	
	public void initNotification(Context context) {
		
        // Open NotificationView Class on Notification Click
        PendingIntent mIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent aIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent sIntent =  PendingIntent.getBroadcast(context, 0, new Intent("stopAppService"), PendingIntent.FLAG_UPDATE_CURRENT);
 	
 
        //Create Notification using NotificationCompat.Builder 
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
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
        notificationmanager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
        
	}
	
	public void stopNotification(Context context){
    	// Cancel notification
    	notificationmanager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationmanager.cancel(0);
	}
		
	   
		
	
	
}
