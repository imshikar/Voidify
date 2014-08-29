package shikar.in.voidify.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shikar.in.voidify.object.ConnectInfoObject.ConnectType;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

public class AutoConnect 
{
	private final static String DEBUG_TAG = "Voidify_Utils_AutoConnect";
	
	public final static String CHECK_NETWORK_PAGE = "http://tw.voidify.net/api/";
	
	public final static int CHECK_NETWORK_STATUS 	= 1;
	public final static int DO_NETWORK_LOGIN 		= 2;
	public final static int AUTO_CONNECT_TIMEOUT 	= 3;
	public final static int AUTO_CONNECT_ALERT 		= 4;
	
	private String _name;
	private String _ssid;
	
	private Context _context;
	private ConnectType _connectType;
	
	private String _scriptCheckType;
	private JSONArray _jsonArrayScriptList;
	
	private Handler _handler;
	
	public String getName()
	{
		return _name;
	}
	
	public String getSSID()
	{
		return _ssid;
	}
	
	public ConnectType getConnectType()
	{
		return _connectType;
	}
	
	public String getScriptCheckType()
	{
		return _scriptCheckType;
	}
	
	public String getScriptJS(int index)
	{
		String result = "";
		
		try
		{
			JSONObject jsonObjectScript = _jsonArrayScriptList.getJSONObject(index);
			result = jsonObjectScript.getString("JS");
		}
		catch (JSONException e)
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
		
		return result;
	}
	
	public String getCheckFile(int index)
	{
		String result = "";
		
		try
		{
			JSONObject jsonObjectScript = _jsonArrayScriptList.getJSONObject(index);
			result = jsonObjectScript.getString("File");
		}
		catch (JSONException e)
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
		
		return result;
	}
	
	public String getCheckPage(int index)
	{
		String result = "";
		
		try
		{
			JSONObject jsonObjectScript = _jsonArrayScriptList.getJSONObject(index);
			result = jsonObjectScript.getString("Page");
		}
		catch (JSONException e)
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
		
		return result;
	}
	
	public int getScriptListLength()
	{
		return _jsonArrayScriptList.length();
	}
	
	public AutoConnect(Context context, String ssid, Handler handler)
	{
		_context = context;
		_ssid = ssid;
		_handler = handler;
	}
	
	
	public boolean checkNetworkStatus()
	{
		boolean result = false;
		
		try 
		{
			
			long timestamp = System.currentTimeMillis();
			int timeoutConnection = 3000;
			int timeoutSocket = 5000;
	
			HttpGet httpRequest = new HttpGet(CHECK_NETWORK_PAGE+"?r="+timestamp);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			
			HttpEntity resEntity = httpResponse.getEntity();
			
			String content = EntityUtils.toString(resEntity);
			
			//Log.d(DEBUG_TAG, content); 
			
			if(timestamp == Long.parseLong(content))
			{
				result = true;
			}
			
		} 
		catch (Exception e) 
		{
			Log.d(DEBUG_TAG, e.getMessage());
		} 
		

		return result;
	}
	
	public boolean checkConnectConfig()
	{
		boolean result = false;
		
		DBHelper dbHelper = new DBHelper(_context);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT connect._Name, connect._ConnectType, connect._Script FROM connect, ap WHERE connect._ID = ap._ConnectID AND ap._SSID = ?  LIMIT 1", new String[]{_ssid});
		
		int rowsNum = cursor.getCount();
		
		boolean haveConnect = false;
		
		if(rowsNum == 1)
		{
			haveConnect = true;
		}
		else
		{
			cursor.close();
			
			String whereSQL = Common.getAPWhereSQL(_ssid, "ap._SSID");
			
			cursor = db.rawQuery("SELECT connect._Name, connect._ConnectType, connect._Script FROM connect, ap WHERE connect._ID = ap._ConnectID AND ("+whereSQL+") LIMIT 1", null);
		
			rowsNum = cursor.getCount();
			
			if(rowsNum == 1)
			{
				haveConnect = true;
			}
		}
		
		if(haveConnect)
		{
			cursor.moveToFirst();	
			
			_name = cursor.getString(0);
			_connectType = ConnectType.values()[cursor.getInt(1)];
			try {
				JSONObject jsonObjectScript = new JSONObject(cursor.getString(2));
				_scriptCheckType = jsonObjectScript.getString("CheckType");
				_jsonArrayScriptList = jsonObjectScript.getJSONArray("List");
				
				Log.d(DEBUG_TAG, "Json:" + _jsonArrayScriptList.toString());
			} 
			catch (JSONException e) 
			{
				_scriptCheckType = "Index";
				_jsonArrayScriptList = new JSONArray();
			}
			
			result = true;
		}
		
		
		cursor.close();
		db.close();
		dbHelper.close();
		
		return result;
	}

	public Runnable checkNetworkStatusRunnable = new Runnable()
	{
		@Override
		public void run() 
		{
			boolean result = checkNetworkStatus();
			_handler.obtainMessage(CHECK_NETWORK_STATUS, result).sendToTarget();
		}
		
	};
	
	
	
}
