package com.example.everbattery;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Functions {
	
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
		
}
