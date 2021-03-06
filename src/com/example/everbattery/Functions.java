package com.example.everbattery;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

import com.example.everbattery.AppService.Logging;

public class Functions {
	
	private static SharedPreferences settings;
	private NotificationManager notificationmanager;
	Logger l = null;
	SendLog sendlog = null;  
	
	public void initConnection(Context context){
		
		
		setDataEnabled(context, true);
		setWifiEnabled(context, true);
		setBluetoothEnabled(context, true);
		
		Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		context.getContentResolver().startSync(null, extras);
		
		l = (Logger) context.getApplicationContext();
		
		if (sendlog == null) {
			//Log.i("EverBattery", "sendlog = null");
			sendlog = new SendLog();
			sendlog.start();
		}
	}
	
	public void stopConnection(Context context){
		
		if (!isSharingWiFi(context)) {
			setDataEnabled(context, false);
			setWifiEnabled(context, false);
			
		}
		if (!areBlueDevicesPaired()) {
			setBluetoothEnabled(context, false);
		}
		
    	// 	Logging Thread
    	if (sendlog != null) {
    		sendlog.interrupt();
			sendlog = null;
    	}
	}
	
	public boolean areBlueDevicesPaired() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		if (pairedDevices.size() > 0) {
            Log.i("EverBattery", "Bluetooth - They are devices paired");
            		
        return true;
		}
    	else {
            Log.i("EverBattery", "Bluetooth - No devices connected");
            
        return false;
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
	
	public static boolean setBluetoothEnabled(Context context, boolean enable) {
	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    boolean isEnabled = bluetoothAdapter.isEnabled();
	    boolean bluetoothEnabled = settings.getBoolean("bluetooth_enabled", true);
		settings = context.getSharedPreferences("EverBattery", Context.MODE_MULTI_PROCESS);
    	
    	// On récupère paramètres
    	
    	if (bluetoothEnabled) {
		    if (enable && !isEnabled) {
		        return bluetoothAdapter.enable(); 
		    }
		    else if(!enable && isEnabled) {
		        return bluetoothAdapter.disable();
		    }
    	}
	    // No need to change bluetooth state
	    return true;
	}
	
	
	
	public boolean setDataEnabled(Context context, boolean enabled){
	
	
	if (android.os.Build.VERSION.SDK_INT < 21){
		
		Log.i("EverBattery", "setMobileData : NOT LOLLIPOP");
		
		Class<?> conmanClass = null;
		Field connectivityManagerField = null;
		Object connectivityManager = null;
		Class<?> connectivityManagerClass = null;
		Method setMobileDataEnabledMethod = null;
	
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			try {
				conmanClass = Class.forName(conman.getClass().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				connectivityManagerField = conmanClass.getDeclaredField("mService");
			} catch (Exception e) {
				e.printStackTrace();
			}
			connectivityManagerField.setAccessible(true);
			try {
				connectivityManager = connectivityManagerField.get(conman);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				connectivityManagerClass = Class.forName(connectivityManager
						.getClass().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				setMobileDataEnabledMethod = connectivityManagerClass
						.getDeclaredMethod("setMobileDataEnabled", boolean.class);
				setMobileDataEnabledMethod.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
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
		
		
		// IF 5.x
		else { 
			
			Log.i("EverBattery", "setMobileData : LOLLIPOP");
			
		    try
		    {
		        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		        Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

		        if (null != setMobileDataEnabledMethod)
		        {
		            setMobileDataEnabledMethod.invoke(telephonyService, enabled);
		            Log.e("EverBattery", "no err - data is enabled/dis");
		           
		        }
		        
		        return true;
		    }
		    catch (Exception ex)
		    {
		        Log.e("EverBattery", "Error setting mobile data state", ex);
		        
		        return false;
		    }
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
	   
	
	public void initNotificationBatteryLow(Context context) {
		
        // Open NotificationView Class on Notification Click
        PendingIntent mIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent aIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
 	
 
        //Create Notification using NotificationCompat.Builder 
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_ebattery_notificonwhite)
                // Set Ticker Message
                .setTicker("Nearly out of charge ! Activate EverBattery ?")
                // Set Title
                .setContentTitle("EverBattery")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set Text
                .setContentText("Touch to launch EverBattery.")
               
                // Set PendingIntent into Notification = MainActiv
                .setContentIntent(mIntent);
 
        // Create Notification Manager
        notificationmanager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
        
	}
	
	public void initNotification(Context context) {
		
        // Open NotificationView Class on Notification Click
        PendingIntent mIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent aIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent sIntent =  PendingIntent.getBroadcast(context, 0, new Intent("stopAppService"), PendingIntent.FLAG_UPDATE_CURRENT);
 	
 
        //Create Notification using NotificationCompat.Builder 
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_ebattery_notificonwhite)
                // Set Ticker Message
                .setTicker("EverBattery is running...")
                // Set Title
                .setContentTitle("EverBattery")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set Text
                .setContentText("EverBattery is running. Touch to open.")
                
                // Add an Action Button below Notification
                .addAction(R.drawable.btn_off_notif, "Switch off", sIntent)//replace 0 by R.drawable.btn_off_notif
                
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
	
	
	public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
		
	
	public String getDeviceName() {
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    } else {
	        return capitalize(manufacturer) + " " + model;
	    }
	}


	public String capitalize(String s) {
	    if (s == null || s.length() == 0) {
	        return "";
	    }
	    char first = s.charAt(0);
	    if (Character.isUpperCase(first)) {
	        return s;
	    } else {
	        return Character.toUpperCase(first) + s.substring(1);
	    }
	} 
	  
	
	
	public String readCPUcurrent() {
		String cpuFreq = "";
		try {
	    RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
	    cpuFreq = reader.readLine();
	    reader.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	    
	    
	    return cpuFreq;
	} 
	
	public String readCPUmax() {
		String cpuFreq = "";
		try {
	    RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
	    cpuFreq = reader.readLine();
	    reader.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	    
	    
	    return cpuFreq;
	} 
	
	public float getCPUfreq() {

	    float cpuFreq = 100*(Float.parseFloat(readCPUcurrent()) / Float.parseFloat(readCPUmax()));
	    
	    return cpuFreq;
	} 
	
	
	
	
	public class SendLog extends Thread {
		private Handler handler = new Handler();
		
	    
		private Runnable senddata = new Runnable() {
			
		   @Override
		   public void run() {
			   
			   if (l != null) {
			    	do {
						try {
						Thread.sleep(800);
    
						} 
						catch(InterruptedException ex) {
						    Thread.currentThread().interrupt();
						}
						catch(IllegalStateException ex) {
			    		    Thread.currentThread().interrupt();
			         	}
	
						Log.i("EverBattery", "SendLog - Sending data.");
						
					} while(l.pilePleine());
			   }
			   else 
				   Log.i("EverBattery", "SendLog - l = null");
		   }
		};
			
		@Override
		public void run(){
			
			if (handler == null)
				handler = new Handler();
			
			handler.postDelayed(senddata, 1000*3);
				
		}
		
		@Override
		public void interrupt() {
			Log.i("EverBattery", "SendLog - interrupt()");
			
			handler.removeCallbacks(senddata);
			senddata = null;
			handler = null;
			
			super.interrupt();
		}
		
	}
	
}
