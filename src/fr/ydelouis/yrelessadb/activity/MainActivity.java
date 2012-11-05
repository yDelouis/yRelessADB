package fr.ydelouis.yrelessadb.activity;

import java.text.Format;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ydelouis.yrelessadb.R;
import fr.ydelouis.yrelessadb.util.ADB;
import fr.ydelouis.yrelessadb.util.Dialog;
import fr.ydelouis.yrelessadb.util.Root;
import fr.ydelouis.yrelessadb.util.Wifi;

public class MainActivity extends Activity
{
	private boolean prefWifiOn = true;
	private boolean prefWifiOff = true;
	private boolean prefNotif = true;
	private String prefPort = "5555";
	
	private ImageView image;
	private View ipContainer;
	private TextView ipText;
	private TextView commandText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(!Root.hasRootPermission())
			Dialog.error(this, R.string.main_noRoot);
		
		loadData();
		setViews();
		
		boolean isWifiConnected = Wifi.isWifiConnected(this);
		if(!isWifiConnected && prefWifiOn) {
			saveInitialWifiState(isWifiConnected);
			Wifi.setWifiEnabled(this, true);
		}
		
		updateUI();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}
	
	private void setViews() {
		image = (ImageView) findViewById(R.id.main_image);
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleAdb();
			}
		});
		ipContainer = findViewById(R.id.main_ipContainer);
		ipText = (TextView) findViewById(R.id.main_ip);
		commandText = (TextView) findViewById(R.id.main_command);
	}
	
	private void loadData() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefWifiOn = prefs.getBoolean(getString(R.string.pref_wifi_on_key), prefWifiOn);
		prefWifiOff = prefs.getBoolean(getString(R.string.pref_wifi_off_key), prefWifiOff);
		prefNotif = prefs.getBoolean(getString(R.string.pref_notif_key), prefNotif);
		prefPort = prefs.getString(getString(R.string.pref_port_key), prefPort);
	}
	
	private void saveInitialWifiState(boolean isWifiConnected) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putBoolean("wifiState", isWifiConnected).commit();
	}
	
	private void updateUI() {
		if(ADB.isEnabled()) {
			image.setImageResource(R.drawable.img_adb_on);
			ipContainer.setVisibility(View.VISIBLE);
			String completeIp = Wifi.getCompleteIp();
			ipText.setText(completeIp);
			commandText.setText(String.format(getString(R.string.main_command), completeIp));
		} else {
			image.setImageResource(R.drawable.img_adb_off);
			ipContainer.setVisibility(View.INVISIBLE);
		}
	}
	
	private void wifiOnDialog() {
		Dialog.question(this, R.string.main_wifiQuestion,
				R.string.main_connect, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Wifi.setWifiEnabled(MainActivity.this, true);
						dialog.dismiss();
					}
				},
				R.string.main_quit, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.main_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
