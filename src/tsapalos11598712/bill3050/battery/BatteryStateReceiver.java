package tsapalos11598712.bill3050.battery;


import java.text.DateFormat;
import java.util.Date;

import tsapalos11598712.bill3050.battery.database.StateHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryStateReceiver extends BroadcastReceiver {

	private StateHelper sHelper;
	private int healthState,statusState,plugged,temperatureState,
				levelState,scaleState,voltageState;
	private String healthStateStr,statusStateStr,temperatureStateStr,
					levelStateStr,voltageStateStr,batterylog,data;
	private boolean shutdown=true;
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 * 
	 * Prepares the Database for communication, 
	 * gets the notification service and it does
	 * aother actions according the battery states
	 */
	@Override
	public void onReceive(Context ctx, Intent intent) {
		//Auto-generated method stub
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
		
		//get the stored state from the DB
		sHelper=new StateHelper(ctx, "States.db", null, 1, "GET");
        data=sHelper.getState();
		
		getBatteryStates(intent);
		
		getBatteryStatesStr(mNotificationManager, ctx);
		
		doHealthActions(mNotificationManager, ctx);
		
		doTemperatureActions(mNotificationManager, ctx);
		
		//set the new state in the DB
		//sHelper.setState("Service");
	}
	
	/**
	 * Gets the battery states and modifying them to be understood 
	 * by human beings! Except for adding any symbols (%, etc) 
	 * it writes words (COLD, etc) depending the battery state.
	 * This, modified, data will be written in the log file 
	 * in another method.
	 * 
	 * @param mNotificationManager
	 * @param ctx
	 */
	public void getBatteryStatesStr(NotificationManager mNotificationManager, Context ctx){
		//similar as method showHealthInfo @ BatteryStateActivity
		if(healthState==BatteryManager.BATTERY_HEALTH_COLD){
			healthStateStr="COLD";
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_DEAD){
			healthStateStr="DEAD";			
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_GOOD){
			healthStateStr="GOOD";			
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_OVERHEAT){
			healthStateStr="OVERHEAT";
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
			healthStateStr="OVERVOLTAGE";
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_UNKNOWN){
			healthStateStr="UNKNOWN";
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
			healthStateStr="UNSPECIFIED FAILURE";
		}
		
		//similar as method showStatusInfo @ BatteryStateActivity
		if(statusState==BatteryManager.BATTERY_STATUS_CHARGING){			
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				statusStateStr="CHARGING (AC)";
				shutdown=false;
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				statusStateStr="CHARGING (USB)";
				shutdown=false;
			}
			else
				shutdown=true;
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_DISCHARGING){
			statusStateStr="DISCHARGING";		
			shutdown=true;	
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_FULL){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				statusStateStr="FULL (AC)";
				shutdown=false;
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				statusStateStr="FULL (USB)";
				shutdown=false;
			}		
			else
				shutdown=true;	
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_NOT_CHARGING){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				statusStateStr="NOT CHARGING (AC)";
				shutdown=false;
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				statusStateStr="NOT CHARGING (USB)";
				shutdown=false;
			}
			else
				shutdown=true;
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_UNKNOWN){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				statusStateStr="UNKNOWN (AC)";
				shutdown=false;
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				statusStateStr="UNKNOWN (USB)";
				shutdown=false;
			}		
			else
				shutdown=true;	
		}
		//displayNotification(mNotificationManager, ctx, statusStateStr+levelState+"-"+temperatureState, false);
		//similar as method showOtherInfo @ BatteryStateActivity
		temperatureStateStr=""+(float)temperatureState/10+"°C";
		float batteryPct = levelState* 100 / (float) scaleState;
		levelStateStr=""+batteryPct+"%";
		voltageStateStr=""+voltageState;
	}
	
	/**
	 * Get the raw values from the battery
	 * 
	 * @param battery
	 */
	public void getBatteryStates(Intent battery){
    	healthState=battery.getIntExtra(BatteryManager.EXTRA_HEALTH,-1);
    	statusState=battery.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
    	plugged=battery.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
    	temperatureState=battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);
    	levelState=battery.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
    	scaleState=battery.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
    	voltageState=battery.getIntExtra(BatteryManager.EXTRA_VOLTAGE,-1);
    }
	
	/**
	 * Do specific actions, depending the health state of the battery
	 * 
	 * @param mNotificationManager
	 * @param ctx
	 */
	public void doHealthActions(NotificationManager mNotificationManager, Context ctx){
		if(healthState==BatteryManager.BATTERY_HEALTH_COLD){
			displayNotification(mNotificationManager, ctx, "Υγεία: COLD", false);
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_DEAD){
			displayNotification(mNotificationManager, ctx, "Υγεία: DEAD", true);
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_GOOD){
			//Do not do anything. its everything OK.
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_OVERHEAT){
			displayNotification(mNotificationManager, ctx, "Υγεία: OVERHEAT("+temperatureStateStr+")", true);
			writeLogNdoShutdown(ctx);
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
			displayNotification(mNotificationManager, ctx, "Υγεία: OVER VOLTAGE("+voltageStateStr+")", true);
			writeLogNdoShutdown(ctx);
			
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_UNKNOWN){
			displayNotification(mNotificationManager, ctx, "Υγεία: UNKNOWN", true);
			writeLogNdoShutdown(ctx);
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
			displayNotification(mNotificationManager, ctx, "Υγεία: UNSPECIFIED FAILURE", true);
			writeLogNdoShutdown(ctx);
		}
	}
	
	/**
	 * Do specific actions, depending the temperature state of the battery
	 * 
	 * @param mNotificationManager
	 * @param ctx
	 */
	public void doTemperatureActions(NotificationManager mNotificationManager, Context ctx){
		if(temperatureState>=400&&temperatureState<=440){
			//show notification
			displayNotification(mNotificationManager, ctx, "Θερμοκρασία: "+temperatureStateStr, false);
		}
		else if(temperatureState>=450&&temperatureState<=480){
			//show notification
			displayNotification(mNotificationManager, ctx, "Θερμοκρασία: "+temperatureStateStr, true);
		}
		else if(temperatureState>=490){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC || 
					plugged==BatteryManager.BATTERY_PLUGGED_USB){
				displayNotification(mNotificationManager, ctx, "Θερμοκρασία: "+temperatureStateStr, true);
			}
			else {
				displayNotification(mNotificationManager, ctx, "Θερμοκρασία: "+temperatureStateStr, true);
			}
			writeLogNdoShutdown(ctx);
		}
	}
	
	/**
	 * It is responsible to show the notification and make 
	 * a sound, if necessary.
	 * 
	 * @param mNotificationManager
	 * @param ctx
	 * @param msg
	 * @param playSound
	 */
	@SuppressWarnings("deprecation")
	public void displayNotification(NotificationManager mNotificationManager, Context ctx,String msg, boolean playSound){
		if(data.equals("Service")){
			CharSequence tickerText = "Μπαταρία";
			long when = System.currentTimeMillis();
			Notification notification = new Notification
					(R.drawable.ic_launcher, tickerText, when);
			notification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
			CharSequence contentTitle = "Ειδοποίηση Μπαταρίας";
			Intent notificationIntent = new Intent(ctx, BatteryStateActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
			if(playSound){
				//notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "1");
				notification.defaults |= Notification.DEFAULT_SOUND;
				AudioManager mAudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
			notification.setLatestEventInfo(ctx, contentTitle, msg, contentIntent);
			mNotificationManager.notify(548853, notification);
		}
	}
	
	/**
	 * Prepares the data, that will be written at the log file 
	 * and proceed to the writing of the log file and shuting down 
	 * the device.
	 * 
	 * @param ctx
	 */
	public void writeLogNdoShutdown(Context ctx){
		//Create the log string here and remove it from anywhere else
		batterylog="Ημ/νία: "+DateFormat.getDateTimeInstance().format(new Date())+"\n";
		batterylog=batterylog+"Υγεία: "+healthStateStr+"\n";
		batterylog=batterylog+"Κατάσταση: "+statusStateStr+"\n";
		batterylog=batterylog+"Πληρότητα: "+levelStateStr+"\n";
		batterylog=batterylog+"Θερμοκρασία: "+temperatureStateStr+"\n";
		batterylog=batterylog+"Τάση: "+voltageStateStr+"\n";
		batterylog=batterylog+"-----------------------------\n";
		new BackgroundTasks().execute(
				new String[]{batterylog,String.valueOf(shutdown)});
	}
}
