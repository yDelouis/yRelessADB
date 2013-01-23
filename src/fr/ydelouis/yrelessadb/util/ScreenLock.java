package fr.ydelouis.yrelessadb.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import fr.ydelouis.yrelessadb.R;

public class ScreenLock
{
	private static final String TAG_WAKELOCK = "yRelessADB_wakeLock";
	private static WakeLock lock;
	
	public static void start(Context context) {
		if(isActive(context))
			getLock(context).acquire();
	}
	
	public static void stop(Context context) {
		getLock(context).release();
	}
	
	private static WakeLock getLock(Context context) {
		if(lock == null) {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG_WAKELOCK);
			lock.setReferenceCounted(false);
		}
		return lock;
	}
	
	private static boolean isActive(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.pref_wakelock_key), false);
	}
}
