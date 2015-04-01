package com.example.everbattery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.NetworkOnMainThreadException;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

// API CRUD or rest

public class Logger extends Application {
	private Stack<JSONObject> data_stack = new Stack<JSONObject>();
	private Functions f = new Functions();
	
	// BATTERY LEVEL
    private class BatteryReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context arg0, Intent arg1) {  
             if (arg1.getAction().equalsIgnoreCase(Intent.ACTION_BATTERY_LOW)  
                       || arg1.getAction().equalsIgnoreCase(  
                                 Intent.ACTION_BATTERY_CHANGED)  
                       || arg1.getAction().equalsIgnoreCase(  
                                 Intent.ACTION_BATTERY_OKAY)) {  
                  //int level = arg1.getIntExtra("level", 0);  
                 
             }  
        }  
   }  
    
   public float getBatteryLevel(Intent batteryIntent) {  
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);  
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);  
        if (level == -1 || scale == -1) {  
             return 50.0f;  
        }  
        return ((float) level / (float) scale) * 100.0f;  
   }  
   
   
   public void determineChargingState() {  

  }  
  
	
	public boolean logIt(Context context) {	
		// On créé un objet JSON pour stocker toute les datas dedans
		
		JSONObject object = new JSONObject();
		  
		
		// IMEI & PHONE NUMBER
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// get IMEI
		String imei = tm.getDeviceId();
		//get The Phone Number
		//String phone = tm.getLine1Number();
		
		
		// DATE & TIME
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    
	    // MODEL
	    String model = f.getDeviceName();
	    
	    // COUNTRY
	    String country = context.getResources().getConfiguration().locale.getCountry();
	    
	    // BATT VALUE
	    BatteryReceiver mArrow = new BatteryReceiver();  
        IntentFilter mIntentFilter = new IntentFilter();  
        mIntentFilter.addAction(Intent.ACTION_BATTERY_LOW);  
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);  
        mIntentFilter.addAction(Intent.ACTION_BATTERY_OKAY);  
        Intent batteryIntent = context.registerReceiver(mArrow, mIntentFilter);  
        float batteryLevel = getBatteryLevel(batteryIntent);  
       
        context.unregisterReceiver(mArrow);
	    
        
        // ON CHARGE
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);  
        Intent batteryStatus = context.registerReceiver(null, ifilter);  
        
        // Are we charging / charged?  
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);  
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING  
                  || status == BatteryManager.BATTERY_STATUS_FULL;  
        
        //Log.i("SMB-DATA", "Logger - Batt_val = " + String.valueOf(batteryLevel) + " cha ? " + isCharging); 

	    // SCREEN ON
	    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	    
	    // DATA ON
	    boolean mobileDataEnabled = false; // Assume disabled
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    try {
	        Class<?> cmClass = Class.forName(cm.getClass().getName());
	        Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
	        method.setAccessible(true); // Make the method callable
	        // get the setting for "mobile data"
	        mobileDataEnabled = (Boolean)method.invoke(cm);
	    } catch (Exception e) {
	        // Some problem accessible private API
	        // TODO do whatever error handling you want here
	    }
	    
	    // WIFI ON
	    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    
	    // BLUETOOTH
	    BluetoothAdapter blue = BluetoothAdapter.getDefaultAdapter();
	    
	    // NFC
	    //NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
	    //NfcAdapter nfc = manager.getDefaultAdapter();
	    
	    
		try {
			object.put("IMEI", imei); // done  
			object.put("DATE", strDate); // done
			object.put("MODEL", model); // done
			object.put("COUNTRY", country); // done
			object.put("BATT_VAL", batteryLevel); // done
			object.put("CHARGE_ON", isCharging); // done
			object.put("SCREEN_ON", powerManager.isScreenOn()); // done
			object.put("DATA_ON", mobileDataEnabled); // done
			object.put("WIFI_ON", wifi.isWifiEnabled()); // done
			object.put("BLUETOOTH_ON", blue.isEnabled()); // done
			object.put("NFC_ON", "");// nfc.isEnabled()); //
		} catch (JSONException e) {
			e.printStackTrace();
		}
		  
		// Et on le push dans notre pile de données
		data_stack.push(object);
		Log.i("EverBattery", "Logger - Pushed in stack of JSON");
		
	return true;
	}
	
	public boolean pilePleine() {
		if (!data_stack.empty()) {
			return true;
		}
		else {
			Log.i("EverBattery", "Logger - Stack is empty");
			return false;
		}
	}
	
	
	public boolean sendIt() {
		JSONObject object = new JSONObject();
		
		//Log.i("SMB-DATA", "Logguer - sendIt()");
		
		// On récupère l'element au dessus de la pile si elle n'est pas vide
		if(!data_stack.empty())  
		{
			object = (JSONObject) data_stack.peek();
			
			// On vérifie que le JSON existe bien
			if (object.has("IMEI")) {
			Log.i("SMB-DATA", "Logger - Catching JSON");
			
			
			
				// On l'envoie et on le supprime
				if (send_datas(object)) {
					data_stack.pop();
					
					Log.i("SMB-DATA", "Logger - Deleting JSON");
			
				return true;
				}
				else
					return false;
			}
		}
		
    return false;
	}
	
	public boolean send_datas(JSONObject object)
	{
		//Log.i("SMB-DATA", "Logger - send_datas()");
		
		final HttpClient httpclient = new DefaultHttpClient(); 
		
		try {
		final HttpUriRequest request = new HttpGet("http://54.154.167.89/post_sql.php?obj=" + URLEncoder.encode(object.toString(), "UTF-8")); 
		//HttpUriRequest request = new HttpGet("http://www.google.fr");
		
		Thread thread = new Thread(new Runnable(){
			
			@Override
			public void run() {
				try {
			    	try {
			    		
			    		httpclient.execute(request);  
			    		
			    		Log.i("SMB-DATA", "Logger - JSON SENT");
			    	}
			    	catch (ClientProtocolException e) { 
			    		Log.i("SMB-DATA", "Logger - JSON NOT SENT - CP");
			    		e.printStackTrace();
			    		
			    	}
			    	catch (IOException e) { 
			    		Log.i("SMB-DATA", "Logger - JSON NOT SENT - IO");
			    		e.printStackTrace();
			    		
			    		
			    	}
			    	catch (NetworkOnMainThreadException e) { 
			    		Log.i("SMB-DATA", "Logger - JSON NOT SENT - Network");
			    		e.printStackTrace();
			    		
			    		
			    	}
				} catch (Exception e) {
		            e.printStackTrace();
				}
			}
		});

		thread.start(); 
	    	
		}
		catch (UnsupportedEncodingException e) {
			
			return false;
		}
		
		return true;
	}
	
	
}
