package tsapalos11598712.bill3050.battery;

import tsapalos11598712.bill3050.battery.database.StateHelper;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.*;

//TODO This class has a bad implementation of Handler and Message
public class BatteryStateActivity extends Activity {

	private StateHelper sHelper;
	private BatteryStateReceiver bsr=new BatteryStateReceiver();
	private int healthState,statusState,plugged,temperatureState,
				levelState,scaleState,voltageState;
	private boolean run=true;
    private Handler statusHandler;
    //private Context ctx;
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * It also prepares the Database for communication and 
	 * creates Handler and Message 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //ctx=getApplicationContext();
        sHelper=new StateHelper(this, "States.db", null, 1, "Activity");
        sHelper.setState("Activity");
        
        //Start the service it is not already running.
        if(isMyServiceRunning(this)){
        	Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show();
        }
        else{
	        //I want to start the service when the app starts.
			Intent battery=new Intent(this, BatteryService.class);
			startService(battery);
			Toast.makeText(this, "Service is starting", Toast.LENGTH_SHORT).show();
        }
        
        
        statusHandler=new Handler(){
        	public void handleMessage(Message msg){
        		//modify sql db before calling the receiver
                sHelper.setState("Activity");
                
                Intent battery=registerReceiver(bsr,   
                	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                
                getBatteryStates(battery);
                
                showHealthInfo();
        		
                showStatusInfo();
                
                showOtherInfo();
                
                unregisterReceiver(bsr);
                
                //send message to re-run the handler
                if(msg.getData().getBoolean("run"))
                	sendMyMessage();
                else
                	sHelper.setState("Service");
        	}
        	/**
        	 * Obtains a Message object and put this object 
        	 * in the main stack of Android execution thread.
        	 */
        	public void sendMyMessage(){
        		Message msg=obtainMessage();
        		Bundle b=new Bundle();
            	b.putBoolean("run", run);
        		msg.setData(b);
        		sendMessage(msg);
        	}
        };
        Bundle b=new Bundle();
    	b.putBoolean("run", run);
    	sendMessage(b);
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
	
	/**
	 * Obtains a Message object and put this object 
     * in the main stack of Android execution thread.
	 * 
	 * @param b
	 */
	public void sendMessage(Bundle b){
		Message msg=statusHandler.obtainMessage();
		msg.setData(b);
		statusHandler.sendMessageDelayed(msg,2000);
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     * 
     * Also, makes the Handler to stop running
     */
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	run=false;
    	//unregisterReceiver(bsr);
    }
    
    /**
     * Get the raw values from the battery
     * 
     * @param intent
     */
    public void getBatteryStates(Intent intent){
    	healthState=intent.getIntExtra(BatteryManager.EXTRA_HEALTH,-1);
    	statusState=intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
    	plugged=intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
    	temperatureState=intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);
    	levelState=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
    	scaleState=intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
    	voltageState=intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,-1);
    }
    
    /**
     * Show the values that have relation with the health 
     * of the battery.
     */
    public void showHealthInfo(){
    	if(healthState==BatteryManager.BATTERY_HEALTH_COLD){
			((TextView)findViewById(R.id.healthView)).setText("COLD");			
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_DEAD){
			((TextView)findViewById(R.id.healthView)).setText("DEAD");			
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_GOOD){
			((TextView)findViewById(R.id.healthView)).setText("GOOD");			
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_OVERHEAT){
			((TextView)findViewById(R.id.healthView)).setText("OVERHEAT");
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
			((TextView)findViewById(R.id.healthView)).setText("OVERVOLTAGE");
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_UNKNOWN){
			((TextView)findViewById(R.id.healthView)).setText("UNKNOWN");
		}
		else if(healthState==BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
			((TextView)findViewById(R.id.healthView)).setText("UNSPECIFIED FAILURE");
		}
    }
    
    /**
     * Show the values that have relation with the status 
     * of the battery.
     */
    public void showStatusInfo(){
    	if(statusState==BatteryManager.BATTERY_STATUS_CHARGING){			
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				((TextView)findViewById(R.id.statusView)).setText("CHARGING (AC)");
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				((TextView)findViewById(R.id.statusView)).setText("CHARGING (USB)");
			}
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_DISCHARGING){
			((TextView)findViewById(R.id.statusView)).setText("DISCHARGING");			
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_FULL){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				((TextView)findViewById(R.id.statusView)).setText("FULL (AC)");
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				((TextView)findViewById(R.id.statusView)).setText("FULL (USB)");
			}			
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_NOT_CHARGING){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				((TextView)findViewById(R.id.statusView)).setText("NOT CHARGING (AC)");
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				((TextView)findViewById(R.id.statusView)).setText("NOT CHARGING (USB)");
			}
		}
		else if(statusState==BatteryManager.BATTERY_STATUS_UNKNOWN){
			if(plugged==BatteryManager.BATTERY_PLUGGED_AC){
				((TextView)findViewById(R.id.statusView)).setText("UNKNOWN (AC)");
			}
			else if(plugged==BatteryManager.BATTERY_PLUGGED_USB){
				((TextView)findViewById(R.id.statusView)).setText("UNKNOWN (USB)");
			}
		}
    }
    
    /**
     * Show the other values that have relation with the battery.
     */
    public void showOtherInfo(){
    	((TextView)findViewById(R.id.temperatureView)).setText(""+(float)temperatureState/10+"°C");
		float batteryPct = levelState* 100 / (float) scaleState;
		((TextView)findViewById(R.id.levelView)).setText(""+batteryPct+"%");
		((TextView)findViewById(R.id.voltageView)).setText(""+voltageState);
    }
}
