package fr.ydelouis.yrelessadb.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ydelouis.yrelessadb.R;
import fr.ydelouis.yrelessadb.receiver.ADBReceiver;
import fr.ydelouis.yrelessadb.util.ADB;
import fr.ydelouis.yrelessadb.util.Dialog;
import fr.ydelouis.yrelessadb.util.Root;
import fr.ydelouis.yrelessadb.util.Wifi;

public class MainActivity extends Activity
{
	private boolean prefWifiOn = true;
	private boolean isWorking = false;
	
	private ImageView image;
	private TextView hint;
	private View ipContainer;
	private TextView ipText;
	private TextView commandText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new RootCheckTask().execute();
		
		loadData();
		setViews();
		
		boolean isWifiConnected = Wifi.isConnected(this);
		if(!isWifiConnected && prefWifiOn) {
			Wifi.saveInitialWifiState(this, isWifiConnected);
			Wifi.setEnabled(this, true);
		}
		
		updateUI();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadData();
		updateUI();
		sendBroadcast();
	}
	
	private void setViews() {
		image = (ImageView) findViewById(R.id.main_image);
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!isWorking)
					toggleAdb();
			}
		});
		hint = (TextView) findViewById(R.id.main_hint);
		ipContainer = findViewById(R.id.main_ipContainer);
		ipText = (TextView) findViewById(R.id.main_ip);
		commandText = (TextView) findViewById(R.id.main_command);
	}
	
	private void loadData() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefWifiOn = prefs.getBoolean(getString(R.string.pref_wifi_on_key), prefWifiOn);
	}
	
	private void updateUI() {
		if(ADB.isEnabled(this)) {
			image.setImageResource(R.drawable.img_adb_on);
			hint.setText(R.string.main_hintOff);
			ipContainer.setVisibility(View.VISIBLE);
			String completeIp = Wifi.getIp(this)+":"+ADB.getPort(this);
			ipText.setText(completeIp);
			commandText.setText(String.format(getString(R.string.main_command), completeIp));
		} else {
			image.setImageResource(R.drawable.img_adb_off);
			hint.setText(R.string.main_hintOn);
			ipContainer.setVisibility(View.INVISIBLE);
		}
	}
	
	private void sendBroadcast() {
		sendBroadcast(new Intent(this, ADBReceiver.class));
	}
	
	private void toggleAdb() {
		if(!Wifi.isConnected(this)) 
			wifiOffDialog();
		else {
			boolean isADBEnabled = ADB.isEnabled(this);
			image.setImageResource(isADBEnabled ? R.drawable.anim_adb_off : R.drawable.anim_adb_on);
			((AnimationDrawable) image.getDrawable()).start();
			new ADBTask().execute();
		}
	}
	
	private void wifiOffDialog() {
		Dialog.question(this, R.string.main_wifiQuestion,
				R.string.main_connect, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Wifi.setEnabled(MainActivity.this, true);
						dialog.dismiss();
					}
				},
				R.string.main_quit, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
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
	
	private class ADBTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			isWorking = true;
			ADB.toggle(MainActivity.this);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			updateUI();
			isWorking = false;
		}
	}
	
	private class RootCheckTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			return Root.hasRootPermission();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(!result)
				Dialog.error(MainActivity.this, R.string.main_noRoot);
		}
	}
}
