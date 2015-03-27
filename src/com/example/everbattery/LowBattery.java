package com.example.everbattery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LowBattery extends BroadcastReceiver {

	Functions f = new Functions();
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	if (!f.isMyServiceRunning(context, AppService.class))
    		f.initNotificationBatteryLow(context);
    }
	
}
