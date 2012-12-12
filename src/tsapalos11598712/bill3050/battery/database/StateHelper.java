/**
 * 
 */
package tsapalos11598712.bill3050.battery.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * This class is used to find out if the BroadcastReceiver,
 * for battery state update, is
 * activated from the Service or the Activity. 
 * 
 * @author little
 *
 */

//TODO I have to re-write this class.
public class StateHelper extends SQLiteOpenHelper {

	private SQLiteDatabase db;
	private String data;
	/**
	 * This constructor opens the Database as writable and 
	 * prepares the main variable.
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public StateHelper(Context context, String name, CursorFactory factory,
			int version, String data) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		if(!data.equals("GET")){
			//db.close();
			this.data=data;
		}
		db = getWritableDatabase();
		//Log.e("CONSTRUCTOR","OK");
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	
	/**
	 * Check if the BroadcastReceiver,
	 * for battery state update, is
	 * activated from the Service or the Activity. 
	 * 
	 * @return "Activity" for Activity, "Service" for Service 
	 */
	public String getState(){
		Log.e("getState","OK");
		//db = getWritableDatabase();
		Cursor c=db.rawQuery("select state from state_data where _id=1;",null);
        c.moveToFirst();
		data=c.getString(0);
        c.close();
        //Log.e("DATA",data);
        //db.close();
		return data;
	}
	
	/**
	 * Parse a String to show that the BroadcastReceiver,
	 * for battery state update, is
	 * activated from the Service or the Activity.
	 * 
	 * @param data "Activity" for Activity, "Service" for Service
	 */
	public void setState(String data){
		//db = getWritableDatabase();
		db.execSQL("UPDATE state_data SET state=\""+data+"\" where _id=1");
		Log.e("STATE", "STATE4");
		//db.close();
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 * 
	 * Creates the Database we want.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.e("ONCREATE","OK");
		//db = getWritableDatabase();
		//does not need to check because onCreate is called if the db
		//does not exist but I am checking!!! Besides that if the check
		//is activated it doesn't run at the first time too!!!
		//if(!data.equals("GET")){
			db.execSQL("CREATE TABLE state_data (_id INTEGER PRIMARY KEY, state TEXT);");
			db.execSQL("INSERT INTO state_data VALUES (1, \"Service\");");
		//}
		//db.close();
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
