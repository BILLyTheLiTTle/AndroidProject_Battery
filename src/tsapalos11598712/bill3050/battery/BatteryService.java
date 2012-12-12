package tsapalos11598712.bill3050.battery;


import tsapalos11598712.bill3050.battery.database.StateHelper;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BatteryService extends Service {

	private BatteryStateReceiver bsr=new BatteryStateReceiver();
	private StateHelper sHelper;
		
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated constructor stub
		super.onCreate();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 * 
	 * Registers the BroadcastReceiver for battery state updates
	 */
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		registerReceiver(bsr,   
        	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
				
		// We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 * 
	 * Also unregisters the BroadcastReceiver for the battery state updates
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(bsr);
		stopSelf();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
