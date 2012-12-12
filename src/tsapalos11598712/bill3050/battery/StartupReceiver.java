package tsapalos11598712.bill3050.battery;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

//TODO I have to rewritte better this class. 
//This will be re-written with the MethodsApplication
public class StartupReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub

		/*I start the service here cuz I want my service 
        to start when the device boots.
       */
		if(isMyServiceRunning(ctx)){
        	Toast.makeText(ctx, "Service is already running", Toast.LENGTH_SHORT).show();
        }
        else{
	        //I want to start the service when the app starts.
			Intent battery=new Intent(ctx, BatteryService.class);
			ctx.startService(battery);
			Toast.makeText(ctx, "Service is starting", Toast.LENGTH_SHORT).show();
        }
	}

	/**
	 * It checks if the service is already running.
	 * 
	 * @param ctx
	 * @return
	 */
	private boolean isMyServiceRunning(Context ctx) {
    	ActivityManager manager = (ActivityManager) ctx.getSystemService(ctx.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("tsapalos11598712.bill3050.battery.BatteryService".equals(service.service.getClassName())) {
            	return true;
            }
        }
        return false;
    }
}
