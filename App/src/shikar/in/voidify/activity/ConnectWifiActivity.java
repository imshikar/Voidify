package shikar.in.voidify.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shikar.in.voidify.R;
import shikar.in.voidify.adapter.ConnectGroupListAdapter;
import shikar.in.voidify.adapter.ConnectWifiListAdapter;
import shikar.in.voidify.object.ConnectGroupObject;
import shikar.in.voidify.object.ConnectWifiObject;
import shikar.in.voidify.utils.Common;
import shikar.in.voidify.utils.DBHelper;
import shikar.in.voidify.utils.DialogAlert;
import shikar.in.voidify.utils.DialogAlert.ButtonType;
import shikar.in.voidify.utils.DialogAlert.DialogAlertCallback;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ConnectWifiActivity extends Activity {

	private final String DEBUG_TAG = "Voidify_Activity_ConnectWifit"; 
	 
	private ListView _listView;
	private Button _btnCancel;
	private ArrayList<ConnectWifiObject> _wifiList;
	private ConnectWifiListAdapter _listAdapter;
	
	private DialogAlert _messageAlertDialog;
	
	private int _groupType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_wifi);

		intialView();

	}
	
	private void intialView()
	{
		Intent intent = this.getIntent();
		_groupType = intent.getIntExtra("group_type", 1);
			
		_wifiList = loadWifiList(_groupType);
		
		_listAdapter = new ConnectWifiListAdapter(this, _wifiList);

		_listView = (ListView)findViewById(R.id.activity_connect_wifi_listview);
		_listView.setAdapter(_listAdapter);
		_listView.setOnItemClickListener(new OnItemClickListener() {
	    	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              
            	ConnectWifiObject connectWifiObj =  _wifiList.get(position);
            	
            	String uuid = connectWifiObj.getUUID();
            	String name = connectWifiObj.getName();
            	
            	long connectID = checkConfigRepeat(uuid);
            	
                
                if(connectID != -1)
                {
                	String alertTitle = getResources().getString(R.string.connect_config_alert_configrepeat_title);
            		String alertMessage = getResources().getString(R.string.connect_config_alert_configrepeat_message);
            		alertMessage = alertMessage.replace("[ConnectName]", name);
                	
            		ContentValues contentValues = new ContentValues();
            		
            		contentValues.put("uuid", uuid);
            		contentValues.put("id", connectID);
            		
                	_messageAlertDialog = new DialogAlert(ConnectWifiActivity.this, alertTitle, alertMessage, ButtonType.OKAndCancel);
            		_messageAlertDialog.setButtonOKText(R.string.connect_config_alert_configrepeat_button_ok);
            		_messageAlertDialog.setCallbackObject(contentValues);
            		_messageAlertDialog.setCallback(new DialogAlertCallback(){

            			@Override
            			public void onButtonClick(int buttonID, Object object)
            			{
            				switch(buttonID)
            				{
            					case R.id.dialog_alert_btn_ok:
            						
            						ContentValues contentValues = (ContentValues)object;
            						
            						Intent i = new Intent();
            		                i.setClass(ConnectWifiActivity.this, ConnectConfigActivity.class);
            		                i.putExtra("connect_id", contentValues.getAsLong("id"));
            		                i.putExtra("wifi_uuid", contentValues.getAsString("uuid"));
            		                
            		                startActivity(i);
            						break;
            				}
            			}

            			
            		});
            		
            		_messageAlertDialog.show();
                	
                	
                }
                else
                {
                	Intent i = new Intent();
	                i.setClass(ConnectWifiActivity.this, ConnectConfigActivity.class);
	                i.putExtra("wifi_uuid", uuid);
	                
	                startActivity(i);
                }
                
                
                
                 
           
            }

       });
		
		_btnCancel = (Button) findViewById(R.id.activity_connect_wifi_btn_cancel);
		_btnCancel.setOnClickListener(_onClickListener);
	}
	
	private long checkConfigRepeat(String uuid)
	{
		long result = -1;
		
		DBHelper dbHelper = new DBHelper(ConnectWifiActivity.this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT _ID FROM connect WHERE _UUID=?", new String[]{uuid});
		
		int rowsNum = cursor.getCount();
		
		cursor.moveToFirst();
		
		if(rowsNum > 0)
		{
			result = cursor.getLong(0);
		}
		
		
		db.close();
		dbHelper.close();		
		
		return result;
	}
	
	private ArrayList<ConnectWifiObject> loadWifiList(int groupType)
	{
		ArrayList<ConnectWifiObject> result = new ArrayList<ConnectWifiObject>();
		
		try 
		{
			JSONArray  jsonArray = new JSONArray(Common.readJSONinString("list.json",getApplicationContext()));
			
			JSONObject jsonObject = jsonArray.getJSONObject(groupType-1);
			
		    jsonArray = jsonObject.getJSONArray("List");
	
			for(int i=0 ; i<jsonArray.length(); i++)
			{
				jsonObject = jsonArray.getJSONObject(i);
				
				ConnectWifiObject connectWifiObject = new ConnectWifiObject();
				connectWifiObject.setUUID(jsonObject.getString("UUID"));
				connectWifiObject.setName(jsonObject.getString("Name"));
				
				result.add(connectWifiObject);
				
				Log.d(DEBUG_TAG, "UUID:"+ connectWifiObject.getUUID() + ", Name:"+connectWifiObject.getName());
			}
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();	
		}
		
		return result;
	}
	
	private OnClickListener _onClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.activity_connect_wifi_btn_cancel:
					ConnectWifiActivity.this.finish();
				    break;
			}
		}
    
    };

}
