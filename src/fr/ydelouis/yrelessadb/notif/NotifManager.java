package fr.ydelouis.yrelessadb.notif;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import fr.ydelouis.yrelessadb.R;
import fr.ydelouis.yrelessadb.activity.MainActivity;
import fr.ydelouis.yrelessadb.receiver.ADBReceiver;
import fr.ydelouis.yrelessadb.util.ADB;
import fr.ydelouis.yrelessadb.util.Wifi;

public class NotifManager
{
	private static final int NOTIF_ID = 314873982;
	
	@SuppressWarnings("deprecation")
	public static void show(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(!prefs.getBoolean(context.getString(R.string.pref_notif_key), true))
			return;
		
		Notification.Builder builder = new Notification.Builder(context);
    	builder.setSmallIcon(R.drawable.ic_notif);
    	builder.setTicker(context.getResources().getString(R.string.notif_ticker));
    	builder.setOngoing(true);
    	builder.setAutoCancel(false);
    	builder.setWhen(System.currentTimeMillis());
    	
    	builder.setContentTitle(context.getString(R.string.notif_title));
    	builder.setContentText(Wifi.getIp(context)+":"+ADB.PORT);
    	
    	Intent notificationIntent = new Intent(context, MainActivity.class);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
    	builder.setContentIntent(contentIntent);
    	
    	Notification notif;
    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
    		String cancel = context.getString(R.string.notif_stop);
    		Intent cancelIntent = new Intent(context, ADBReceiver.class);
    		cancelIntent.setAction(ADBReceiver.ACTION_CANCEL);
    		PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);
    		builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, cancel, cancelPendingIntent);
    		notif = builder.build();
    	} else
    		notif = builder.getNotification();
    	
    	NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	notifManager.notify(NOTIF_ID, notif);
	}
	
	public static void cancel(Context context) {
		NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancel(NOTIF_ID);
	}
}
