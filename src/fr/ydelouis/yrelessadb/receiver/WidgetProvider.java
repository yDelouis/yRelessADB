package fr.ydelouis.yrelessadb.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import fr.ydelouis.yrelessadb.R;
import fr.ydelouis.yrelessadb.activity.MainActivity;
import fr.ydelouis.yrelessadb.notif.NotifManager;
import fr.ydelouis.yrelessadb.util.ADB;
import fr.ydelouis.yrelessadb.util.Wifi;

public class WidgetProvider extends AppWidgetProvider
{
	private static final long UPDATE_DELAY = 5*60*1000;
	private static final String ACTION_TOGGLE = "action_toggle";
	private PendingIntent service = null;
	
	@Override
	public void onEnabled(Context context) {
		if (service == null)  
            service = PendingIntent.getBroadcast(context, 0, new Intent(context, WidgetProvider.class), 0);  
		
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
		m.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), UPDATE_DELAY, service);
	}
	
	public void onDisabled(Context context)  {  
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
        if(service != null)
        	m.cancel(service);  
    }
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if(intent.getAction() != null && intent.getAction().equals(ACTION_TOGGLE)) {
			if(ADB.isEnabled(context)) {
				ADB.stop(context);
				NotifManager.cancel(context);
			} else if(Wifi.isConnected(context)){
				ADB.start(context);
				NotifManager.show(context);
			} else {
				Toast.makeText(context, R.string.main_wifiQuestion, Toast.LENGTH_LONG).show();
			}
		}
		if(!Wifi.isConnected(context))
			setWifiDisconnected(context);
		else if(ADB.isEnabled(context))
			setOn(context);
		else setOff(context);
	}
	
	public static void setWifiDisconnected(Context context) {
		updateWidget(context, R.drawable.img_widget_off, context.getString(R.string.widget_wifiDisconnected));
	}
	
	public static void setOn(Context context) {
		String ip = Wifi.getIp(context)+":"+ADB.getPort(context);
		updateWidget(context, R.drawable.img_widget_on, ip);
	}
	
	public static void setOff(Context context) {
		updateWidget(context, R.drawable.img_widget_off, context.getString(R.string.widget_off));
	}
	
	private static void updateWidget(Context context, int imageId, String text) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		remoteViews.setImageViewResource(R.id.widget_image, imageId);
		remoteViews.setTextViewText(R.id.widget_text, text);
		
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        
        intent = new Intent(context, WidgetProvider.class);
        intent.setAction(WidgetProvider.ACTION_TOGGLE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context, WidgetProvider.class); 
        manager.updateAppWidget(widget, remoteViews); 
	}
}
