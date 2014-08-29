package shikar.in.voidify.activity;


import java.util.Iterator;
import java.util.List;

import shikar.in.voidify.R;
import shikar.in.voidify.service.AutoConnectService;
import shikar.in.voidify.utils.Common;
import shikar.in.voidify.utils.DBHelper;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "Voidify_Activity_Main";
	
	private ImageView _imgGoControl;
	private ImageView _imgConnectStatus;
	private TextView _textConnectTitle;
	private TextView _textConnectSSID;
	private TextView _textConnectStatus;
	
	private long _connectID;
	
	private ConnectivityManager _connectManager;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        intialView();
        
        updateView();
        
        registerBroadcastReceiver();
        
       
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }
    
    private void intialView()
    {
    	_textConnectTitle = (TextView) findViewById(R.id.activity_main_text_connect_title);
 
        _textConnectSSID = (TextView) findViewById(R.id.activity_main_text_connect_ssid);

        _textConnectStatus = (TextView) findViewById(R.id.activity_main_text_connect_status);
        
        _imgGoControl = (ImageView)findViewById(R.id.activity_main_img_gocontrol);
        _imgGoControl.setOnClickListener(imgOnClickListener);
        
        _imgConnectStatus = (ImageView)findViewById(R.id.activity_main_img_connect_status);
        
        _connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }
    
    private void updateView()
    {
    	Log.d(DEBUG_TAG, "Update View");
    	
    	boolean isConnectWiFi = _connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    	
    	String wifiSSID = "";
		String wifiTitle = "";
		String wifiStatus = "";
    	Drawable wifiStatusDrawable = null;
		
		
    	if(isConnectWiFi)
    	{
    		wifiSSID = getWifiSSID();
    		wifiTitle = getWifiTitle(wifiSSID);
    		
    		if(wifiTitle.equals(getResources().getString(R.string.main_undefined_wifi_title)))
    		{
    			wifiStatusDrawable = getResources().getDrawable(R.drawable.icon_status_unknow);
    		}
    		else
    		{
    			wifiStatusDrawable = getResources().getDrawable(R.drawable.icon_status_connected);
    			
    			_connectID = getConnectID(wifiSSID);
    			
    			if(_connectID > -1)
    			{
    				wifiStatus = getResources().getString(R.string.main_wifi_status_haveconfig);
    			}
    			else
    			{
    				wifiStatus = getResources().getString(R.string.main_wifi_status_supported);
    			}
    		}
    		
    	}
    	else
    	{
    		wifiSSID = getResources().getString(R.string.main_not_connected_wifi_ssid);
    		wifiTitle = getResources().getString(R.string.main_not_connected_wifi_title);
    		wifiStatusDrawable = getResources().getDrawable(R.drawable.icon_status_unconnected);
    	}
    	
    	_textConnectTitle.setText(wifiTitle);
    	_textConnectSSID.setText(wifiSSID);
    	_textConnectStatus.setText(wifiStatus);
    	_imgConnectStatus.setImageDrawable(wifiStatusDrawable);
    }
    
    
    private long getConnectID(String ssid)
    {
    	long result = -1;
    	
    	DBHelper dbHelper = new DBHelper(MainActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT ap._ConnectID FROM ap WHERE ap._SSID = ? LIMIT 1", new String[]{ssid});

		int rowsNum = cursor.getCount();
		
		Log.d(DEBUG_TAG, "Count C: "+ rowsNum);
		
		boolean haveConnect = false;
				
		if(rowsNum == 1)
		{
			haveConnect = true;
		}
		else
		{
			cursor.close();
			
			String whereSQL = Common.getAPWhereSQL(ssid, "ap._SSID");
			
			cursor = db.rawQuery("SELECT ap._ConnectID FROM ap WHERE "+whereSQL+" LIMIT 1", null);
			
			rowsNum = cursor.getCount();
			
			if(rowsNum == 1)
			{
				haveConnect = true;
			}
		}
		
		if(haveConnect)
		{
			cursor.moveToFirst();	
			result = cursor.getLong(0);
		}
		
		cursor.close();
		db.close();
		dbHelper.close();
    	
    	return result;
    }
    
    private String getWifiTitle(String ssid)
    {
    	String result = getResources().getString(R.string.main_undefined_wifi_title);
    	
    	DBHelper dbHelper = new DBHelper(MainActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT support._Name FROM support WHERE support._SSID = ? LIMIT 1", new String[]{ssid});

		int rowsNum = cursor.getCount();
		
		Log.d(DEBUG_TAG, "Count T: "+rowsNum );
		
		boolean haveConnect = false;
		
		if(rowsNum == 1)
		{
			haveConnect = true;
		}
		else
		{			
			cursor.close();
			
			String whereSQL = Common.getAPWhereSQL(ssid, "support._SSID");
			
			cursor = db.rawQuery("SELECT support._Name FROM support WHERE "+whereSQL+" LIMIT 1", null);
		
			rowsNum = cursor.getCount();
			
			if(rowsNum == 1)
			{
				haveConnect = true;
			}
			
		}
		
		if(haveConnect)
		{
			cursor.moveToFirst();		
			result = cursor.getString(0);
		}
		
		cursor.close();
		db.close();
		dbHelper.close();
    	
    	return result;
    }
    
    private String getWifiSSID()
    {
    	NetworkInfo netInfo = _connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	
    	if(netInfo != null)
    	{
			if(netInfo.isConnected())
			{
					
				WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
				WifiInfo info = wifiManager.getConnectionInfo ();
					
				String connectedSSID = info.getSSID().replace("\"", "");
				connectedSSID = connectedSSID.replace("'", "\'");
					
				return connectedSSID;
				
			}
			
			
    	}
		
		return "";
    }
  
    
    private void registerBroadcastReceiver()
    {
    	IntentFilter wifiMonitorFilter = new IntentFilter();
    	wifiMonitorFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, wifiMonitorFilter);
    }
    
    private void unregisterBroadcastReceiver()
    {
    	unregisterReceiver(wifiBroadcastReceiver);
    }
    
    private OnClickListener imgOnClickListener = new OnClickListener()
    {

		@Override
		public void onClick(View view) 
		{
			switch(view.getId())
			{
				case R.id.activity_main_img_gocontrol:
	
					Intent i = new Intent();
	                i.setClass(MainActivity.this, ControlActivity.class);
	                startActivity(i);  
				
					break;
			}
			
		}
    	
    };
    
    private BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			
			String action = intent.getAction();
			
			Log.d(DEBUG_TAG, action);
			
			if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION ))
			{
				NetworkInfo netInfo = intent.getParcelableExtra (WifiManager.EXTRA_NETWORK_INFO);
				
				if (ConnectivityManager.TYPE_WIFI == netInfo.getType())
				{
					updateView();
				}
			}
		}
    	
    };
    

}
