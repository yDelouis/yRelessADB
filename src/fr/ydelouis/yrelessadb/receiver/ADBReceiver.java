package fr.ydelouis.yrelessadb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import fr.ydelouis.yrelessadb.notif.NotifManager;
import fr.ydelouis.yrelessadb.util.ADB;
import fr.ydelouis.yrelessadb.util.ScreenLock;

public class ADBReceiver extends BroadcastReceiver
{
	public static final String ACTION_CANCEL = "action_cancel";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction() != null && intent.getAction().equals(ACTION_CANCEL)) {
			if(ADB.isEnabled(context))
				ADB.stop(context);
			stop(context);
		} else {
			if(ADB.isEnabled(context))
				start(context);
			else
				stop(context);
		}
	}
	
	private void start(Context context) {
		NotifManager.show(context);
		WidgetProvider1x1.setOn(context);
		WidgetProvider3x1.setOn(context);
		ScreenLock.start(context);
	}
	
	private void stop(Context context) {
		NotifManager.cancel(context);
		WidgetProvider1x1.setOff(context);
		WidgetProvider3x1.setOff(context);
		ScreenLock.stop(context);
	}
}
