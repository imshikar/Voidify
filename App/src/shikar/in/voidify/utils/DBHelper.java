package shikar.in.voidify.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shikar.in.voidify.object.ConnectGroupObject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
	private final static String DEBUG_TAG = "Voidify_Utils_DBHelper";
	
	private final static int _DBVersion = 12; //Code:201408250
	private final static String _DBName = "Voidify.db"; 
	
	private Context _context;
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) 
	{
		super(context, name, factory, version);
		_context = context;
	}
	
	public DBHelper(Context context, String name) 
	{
		super(context, name, null, _DBVersion);
		_context = context;
	}
	
	public DBHelper(Context context, String name, int version) 
	{
		super(context, name, null, version);
		_context = context;
	}
	
	public DBHelper(Context context) 
	{
		super(context, _DBName, null, _DBVersion);
		_context = context;
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		final String SQL_Connect = "CREATE TABLE IF NOT EXISTS connect ( " +
								   "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
								   "_UUID VARCHAR(40), " +
								   "_Name VARCHAR(255), " +
								   "_Account VARCHAR(255),"+
								   "_Version INTEGER, " +
								   "_ConnectType INTEGER," +
								   "_IsRoam BLOB," +
								   "_Form TEXT," +
								   "_Script TEXT"+
								   ");";

		db.execSQL(SQL_Connect);
		
		final String SQL_Ap  = "CREATE TABLE IF NOT EXISTS ap ( " +
							   "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
							   "_SSID VARCHAR(255), " +
							   "_ConnectID INTEGER " +
							   ");";
		db.execSQL(SQL_Ap);
		
		final String SQL_Support = "CREATE TABLE IF NOT EXISTS support ( " +
								   "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
								   "_UUID VARCHAR(40)," +
								   "_SSID VARCHAR(255), " +
								   "_Name VARCHAR(255) " +
								   ");";
		db.execSQL(SQL_Support);	
		
		createAPList(db);
	}
	
	private void createAPList(SQLiteDatabase db)
	{
		try 
		{
			JSONArray  jsonArray = new JSONArray(Common.readJSONinString("list.json", _context));
			
			for(int i=0 ; i<jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				JSONArray jsonArrayList = jsonObject.getJSONArray("List");
				
				for(int j=0 ; j<jsonArrayList.length() ; j++)
				{
					JSONObject jsonObjectWifi = jsonArrayList.getJSONObject(j);
					String wifiUUID = jsonObjectWifi.getString("UUID");
					String wifiName = jsonObjectWifi.getString("Name");
					
					JSONObject jsonObjectScript = new JSONObject(Common.readJSONinString(wifiUUID+".json", _context));
					
					JSONArray jsonArraySSID = jsonObjectScript.getJSONArray("SSID");
					
					for(int k=0 ; k<jsonArraySSID.length() ; k++)
					{
						String wifiSSID = jsonArraySSID.getString(k);
						
						ContentValues contentValues = new ContentValues();
						
						contentValues.put("_UUID", wifiUUID);
						contentValues.put("_SSID", wifiSSID);
						contentValues.put("_Name", wifiName);
						
						db.insert("support", null, contentValues);
					}
					
				}
				
			}
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();	
		}
	}
	
	private String getAccount(JSONArray jsonArrayAccount, JSONObject jsonForm)
	{
		String account = "";
		
		try 
		{
			for(int i=0 ; i<jsonArrayAccount.length() ; i++)
			{
				String formName = jsonArrayAccount.getString(i);
				try
				{
					account += jsonForm.getString(formName);
				}
				catch (JSONException e) 
				{
					account += formName;
				}
			}
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return account;
	}
	
	private String modifyScript(JSONArray jsonArrayForm, JSONObject jsonObjectForm, String script)
	{
		String result = script;
		
		try
		{	
			for(int i=0 ; i<jsonArrayForm.length() ; i++)
			{
				JSONObject jsonObjectInput = jsonArrayForm.getJSONObject(i);
				
				String inputLabel = jsonObjectInput.getString("ID");
				String inputType = jsonObjectInput.getString("Type");
				
				
				
				if(inputType.equals("Spinner"))
				{
					inputLabel += "_Value";
				}
				
				String inputValue = jsonObjectForm.getString(inputLabel);
				
				result = result.replaceAll("%"+inputLabel+"%", inputValue);
			}
		}
		catch (JSONException e)
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
		
		Log.d(DEBUG_TAG, result);
		
		return result;
	}
	
	private void updateConnect(SQLiteDatabase db)
	{
		Cursor cursor = db.rawQuery("SELECT connect._ID,  connect._UUID, connect._Form FROM connect", null);
		int rowsNum = cursor.getCount();
		
		cursor.moveToFirst();
		
		for(int i=0 ; i<rowsNum ; i++)
		{
			Long connectID = cursor.getLong(0);
			String connectUUID = cursor.getString(1);
			
			try 
			{
				JSONObject jsonObjectScript = new JSONObject(Common.readJSONinString(connectUUID+".json", _context));
				
				String name = jsonObjectScript.getString("Name");
				JSONArray jsonArrayForm = jsonObjectScript.getJSONArray("Form");
				JSONObject jsonObjectForm = new JSONObject(cursor.getString(2));
				String account = getAccount(jsonObjectScript.getJSONArray("Account"), jsonObjectForm);
				int version = jsonObjectScript.getInt("Version");
				String script = jsonObjectScript.getString("Script");
				String modScript = modifyScript(jsonArrayForm, jsonObjectForm, script);
				
				ContentValues contentValues = new ContentValues();
				
				contentValues.put("_UUID", connectUUID);
				contentValues.put("_Name", name);
				contentValues.put("_Account", account);
				contentValues.put("_Version", version);
				contentValues.put("_Script", modScript);
				
				db.update("connect", contentValues, "_ID=?", new String[]{String.valueOf(connectID)});
				
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			cursor.moveToNext();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		updateConnect(db);
		//db.execSQL("DROP TABLE IF EXISTS connect");
		//db.execSQL("DROP TABLE IF EXISTS ap");
		db.execSQL("DROP TABLE IF EXISTS support");
		//onCreate(db);
		
		final String SQL_Support = "CREATE TABLE IF NOT EXISTS support ( " +
				   "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   "_UUID VARCHAR(40)," +
				   "_SSID VARCHAR(255), " +
				   "_Name VARCHAR(255) " +
				   ");";
		db.execSQL(SQL_Support);	
		
		createAPList(db);
		
	}
	
	@Override   
	public void onOpen(SQLiteDatabase db) 
	{     
		super.onOpen(db);       
	} 
	 
	@Override
	public synchronized void close() 
	{
		super.close();
	}
}
