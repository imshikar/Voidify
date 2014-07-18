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
	
	public static String getAPWhereSQL(String ssid, String column)
	{
		String whereSQL = "";
		
		String[] ssidList = ssid.split("[_\\- ]");
		
		for(int i=0 ; i<ssidList.length ; i++)
		{
			whereSQL += "OR "+column+" = '"+ssidList[i]+"' ";
		}
		
		return whereSQL.substring(2);
	}
	
}
