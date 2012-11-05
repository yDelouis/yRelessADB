package fr.ydelouis.yrelessadb.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Wifi
{
	public static boolean isWifiConnected(Context context) {
		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			return wifiManager.isWifiEnabled() && wifiInfo.getSSID() != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void setWifiEnabled(Context context, boolean enable) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enable);
	}
}
