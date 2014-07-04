package shikar.in.voidify.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

public class Common 
{

	public static String readJSONinString(String fileName, Context c) 
	{
        try 
        {
            InputStream is = c.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);

            return text;
        } 
        catch (IOException e) 
        {
            throw new RuntimeException(e);
        }

    }
	
	public static boolean checkAutoConnectService(Context context)
	{
		boolean resutl = false;
		
		String autoConnectService = "shikar.in.voidify.service.AutoConnectService";
	    	
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

	    List<ActivityManager.RunningServiceInfo> info;

	    info = am.getRunningServices(200);

	    for(Iterator iterator = info.iterator(); iterator.hasNext();) 
	    {
	    	RunningServiceInfo runningTaskInfo = (RunningServiceInfo) iterator.next();
	    		
	    
	    	if (runningTaskInfo.service.getClassName().equals(autoConnectService))
	    	{
	    		resutl = true;
	    		break;
	    	}

	    }
	    	
	    return resutl;
	}
	
}
