package tsapalos11598712.bill3050.battery;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;

public class BackgroundTasks extends AsyncTask<String, Integer, Boolean> {
	
	private Boolean shutdown;
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * Specifically, this method writes the data to the log file
	 */
	@SuppressWarnings("deprecation")
	protected Boolean doInBackground(String... info) {
		//write Log file to sdcard
		File logFile=new File(
				Environment.getExternalStoragePublicDirectory(""),
				"battery_log.txt");
		try {
			if(!logFile.exists()){
				logFile.createNewFile();
				logFile.setWritable(true);
				logFile.setReadable(true);
			}
			int dateModified=new Date(logFile.lastModified()).getDay();
			int currentDate=Calendar.getInstance().getTime().getDay();
			byte[] data=new byte[(int)logFile.length()];
			if(dateModified==currentDate){
				FileInputStream fis=new FileInputStream(logFile);
				BufferedInputStream bis=new BufferedInputStream(fis);
				bis.read(data);
				bis.close();
				fis.close();
			}
			FileOutputStream fos=new FileOutputStream(logFile);
			BufferedOutputStream bos=new BufferedOutputStream(fos);
			bos.write(info[0].getBytes());
			if(dateModified==currentDate)
				bos.write(data);
			bos.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return Boolean.valueOf(info[1]);
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    protected void onProgressUpdate(Integer... progress) {
        
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     * 
     * When the AsyncTask object has finished, this method is called 
     * and instatiate a CountDownTimer object to make this thread to wait 
     * for a while before shuting down the device.
     */
    protected void onPostExecute(Boolean result) {
    	shutdown=result;
    	new CountDownTimer(7000, 1000) {

    	     public void onTick(long millisUntilFinished) {
    	         //Do not do a thing
    	     }

    	     public void onFinish() {
    	    	 if(shutdown.booleanValue()){
    	 	    	//shutdown
    	 			try {
    	 	            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot -p" });
    	 	            proc.waitFor();
    	 	        } 
    	 			catch (IOException ioe){
    	 	    		//Toast.makeText(ctx, "IOException: Είσαι σίγουρα root?", Toast.LENGTH_LONG).show();
    	 	        }
    	 	    	catch (InterruptedException ie){
    	 	    		//Toast.makeText(ctx, "InterruptedException: Είσαι σίγουρα root?", Toast.LENGTH_LONG).show();
    	 	    	}
    	     	}
    	     }
    	  }.start();
    }
}
