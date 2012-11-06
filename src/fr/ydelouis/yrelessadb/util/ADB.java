package fr.ydelouis.yrelessadb.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import fr.ydelouis.yrelessadb.receiver.ADBReceiver;

public class ADB
{
	private static final String PREF_STATE = "adbState";
	private static final String SERVICE = "service.adb.tcp.port";
	public static final String PORT = "5555";
	private static final String PROCESS = "adbd";
	private static final String START_ADB = "start abdb";
	private static final String STOP_ADB = "stop abdb";
	
	public static boolean isEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_STATE, false);
	}
	
	private static void setEnabled(Context context, boolean isEnabled) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(PREF_STATE, isEnabled).commit();
	}
	
	public static boolean toggle(Context context) {
		if(isEnabled(context))
			return stop(context);
		else
			return start(context);
	}
	
	public static boolean start(Context context) {
		try {
			Root.setProp(SERVICE, PORT);
			if (Root.isProcessRunning(PROCESS)) {
				Root.runCommand(STOP_ADB);
			}
			Root.runCommand(START_ADB);
			setEnabled(context, true);
			
			context.sendBroadcast(new Intent(context, ADBReceiver.class));
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean stop(Context context) {
		try {
			Root.setProp(SERVICE, "-1");
			Root.runCommand(STOP_ADB);
			Root.runCommand(START_ADB);
			setEnabled(context, false);
			
			context.sendBroadcast(new Intent(context, ADBReceiver.class));
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
