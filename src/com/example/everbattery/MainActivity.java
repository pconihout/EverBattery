package com.example.everbattery;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.os.Build;

public class MainActivity extends Activity {

	// SharedPrefs files
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
    
	// Button ON/OFF which launch service
	private ToggleButton launchButton = null;
	
	// Wi-Fi
	private WifiManager wifi = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		addListenerOnButton();
		
	}
	
	public void addListenerOnButton() {
        settings = getSharedPreferences("EverBattery", MODE_MULTI_PROCESS);
        editor = settings.edit();
        
        wifi = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);
        
		launchButton = (ToggleButton) findViewById(R.id.launchButton);
	 
		launchButton.setOnCheckedChangeListener(
				
			new CompoundButton.OnCheckedChangeListener() {
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			    	
			        if (isChecked) {
			        	Log.i("EverBattery", "MainActivity : ToggleButton is on."); 
			        
	                    // On lance le wifi ?
	            		if (wifi.isWifiEnabled()) {
	            			Log.i("SMB-DATA", "MainActivity : Wifi is enabled."); 
	            		    
	            	        editor.putBoolean("wifi_enabled", true);
	            	        
	            		}
	            		else {
	            			Log.i("SMB-DATA", "MainActivity : Wifi is disabled."); 
	            			editor.putBoolean("wifi_enabled", false);
	            			
	            		}
	            		
	            		
	            		// On lance le bluetooth ?
	            	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	            	    
	            	    if (bluetoothAdapter.isEnabled()) {
	            			Log.i("SMB-DATA", "MainActivity : Bluetooth is enabled."); 
	            		    
	            	        editor.putBoolean("bluetooth_enabled", true);
	            	        
	            		}
	            		else {
	            			Log.i("SMB-DATA", "MainActivity : Bluetooth is disabled."); 
	            			editor.putBoolean("bluetooth_enabled", false);
	            			
	            		}
	            		
	            		editor.commit();
	            		
			        	Intent intentAppService = new Intent(MainActivity.this, AppService.class);
			        	startService(intentAppService); 
			        }
			        else {
			        	Log.i("EverBattery", "MainActivity : ToggleButton is off"); 
			        	
			        	Intent i = new Intent(MainActivity.this, AppService.class);
			        	MainActivity.this.stopService(i);
			        }
			        	
			    }
			}
		);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
