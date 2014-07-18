 package shikar.in.voidify.activity;

import java.util.ArrayList;

import shikar.in.voidify.R;
import shikar.in.voidify.adapter.ControlListAdapter;
import shikar.in.voidify.adapter.FormSpinnerDiagloAdapter;
import shikar.in.voidify.object.ConnectInfoObject;
import shikar.in.voidify.object.FormSpinnerOptionObject;
import shikar.in.voidify.object.ConnectInfoObject.ConnectType;
import shikar.in.voidify.service.AutoConnectService;
import shikar.in.voidify.utils.Common;
import shikar.in.voidify.utils.DBHelper;
import shikar.in.voidify.utils.DialogAlert;
import shikar.in.voidify.utils.DialogAlert.ButtonType;
import shikar.in.voidify.utils.DialogAlert.DialogAlertCallback;
import shikar.in.voidify.utils.DialogListview;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ControlActivity extends Activity 
{
	private final String DEBUG_TAG = "Voidify_Activity_Control"; 
	
	public final static String CONTROL_ACT_EDIT   = "Edit";
	public final static String CONTROL_ACT_DELETE = "Delete";
	public final static String CONTROL_ACT_LOGIN  = "Login";
	
	private Button btnConnectAdd;
	private ListView _listView;
	private ArrayList<ConnectInfoObject> _connectInfoList;
	private ControlListAdapter _controlListAdapter;
	
	private ArrayList<FormSpinnerOptionObject> _longClickOptionList;
	private FormSpinnerDiagloAdapter _longClickOptionAdapter;
	
	private DialogListview _longClickDialog;
	private DialogAlert _messageAlertDialog;
	
	private int _configPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
	    
		initialView();
		initialLongClickOption();
	}
	
	private OnClickListener btnOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			Intent itent = new Intent();
			itent.setClass(ControlActivity.this, ConnectGroupActivity.class);
            startActivity(itent);  
		}
	};
	
	private OnItemClickListener listOnItemClickListener = new OnItemClickListener()
	{
		@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      
            goConnectConfigActivity(_connectInfoList.get(position).getID(),
            					    _connectInfoList.get(position).getUUID());
       
        }
		
	};
	
	private OnItemLongClickListener listOnItemLongClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
		{
			String title = _connectInfoList.get(position).getTitle();
			
			_configPosition = position;
			
			_longClickDialog  = new DialogListview(ControlActivity.this, _longClickOptionAdapter, title);
			_longClickDialog.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
					FormSpinnerOptionObject formSpinnerOptionObject = _longClickOptionList.get(position);	
					
					String controlAction = formSpinnerOptionObject.getValue();
					
					ConnectInfoObject connectInfoObj = _connectInfoList.get(_configPosition);
					
					if(controlAction.equals(CONTROL_ACT_EDIT))
					{
				    	goConnectConfigActivity(connectInfoObj.getID(),
				    							connectInfoObj.getUUID());
					}
					else if (controlAction.equals(CONTROL_ACT_DELETE))
					{
						delConnectConfig(connectInfoObj.getID(),
										 connectInfoObj.getUUID(),
										 connectInfoObj.getTitle());
					}
					else if( controlAction.equals(CONTROL_ACT_LOGIN))
					{
						manaulLogin(connectInfoObj.getID(), 
								   connectInfoObj.getTitle());
					}
					
					_longClickDialog.dismiss();
					
				}
				
			});
			_longClickDialog.show();
			
			return true;
		}
		
	};
	
	private void goConnectConfigActivity(long id, String uuid)
	{
		Intent itent = new Intent();
    	itent.setClass(ControlActivity.this, ConnectConfigActivity.class);
        
    	itent.putExtra("connect_id",id);
    	itent.putExtra("wifi_uuid", uuid);
    	
        startActivity(itent);  
	}
	
	private void delDBConnect(long id)
	{
		DBHelper dbHelper = new DBHelper(ControlActivity.this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		String[] arg =  new String[]{id+""};
		
		db.delete("connect", "_ID=?", arg);
		db.delete("ap", "_ConnectID=?", arg);
		
		db.close();
		dbHelper.close();
	}
	
	
	private void delConnectConfig(long id, String uuid, String title)
	{
		String alertTitle = getResources().getString(R.string.control_alert_delete_title);
		String alertMessage = getResources().getString(R.string.control_alert_delete_message);
		alertMessage = alertMessage.replace("[ConfigTitle]", title);
	
		_messageAlertDialog = new DialogAlert(ControlActivity.this, alertTitle, alertMessage, ButtonType.OKAndCancel);
		_messageAlertDialog.setButtonOKText(R.string.control_alert_delete_button_ok);
		_messageAlertDialog.setCallbackObject(id);
		_messageAlertDialog.setCallback(new DialogAlertCallback(){

			@Override
			public void onButtonClick(int buttonID, Object object)
			{

				Log.d(DEBUG_TAG, "test");
				
				switch(buttonID)
				{
					case R.id.dialog_alert_btn_ok:
						
						Long id = (Long)object;
						
						delDBConnect(id.longValue());
						
						_connectInfoList = loadDBConnect();
						_controlListAdapter.setList(_connectInfoList);
						_controlListAdapter.notifyDataSetChanged();
						
						break;
						
					case R.id.dialog_alert_btn_cancel:
						break;
				}
			}

			
		});
		
		_messageAlertDialog.show();
	}
	
	private void manaulLogin(Long id, String title)
	{
		String alertTitle = getResources().getString(R.string.control_alert_login_title);
		String alertMessage = getResources().getString(R.string.control_alert_login_message);
		alertMessage = alertMessage.replace("[ConfigTitle]", title);
	
		_messageAlertDialog = new DialogAlert(ControlActivity.this, alertTitle, alertMessage, ButtonType.OKAndCancel);
		_messageAlertDialog.setButtonOKText(R.string.control_alert_login_button_ok);
		_messageAlertDialog.setCallbackObject(id);
		_messageAlertDialog.setCallback(new DialogAlertCallback(){

			@Override
			public void onButtonClick(int buttonID, Object object)
			{
				switch(buttonID)
				{
					case R.id.dialog_alert_btn_ok:
						
						Long connectID = (Long)object;

						doManaulLogin(connectID);
						
						break;
						
					case R.id.dialog_alert_btn_cancel:
						break;
				}
			}

			
		});
		
		_messageAlertDialog.show();
	}
	
	private void doManaulLogin(Long id)
	{
		DBHelper dbHelper = new DBHelper(ControlActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT ap._SSID FROM ap WHERE ap._ConnectID = ? LIMIT 1", new String[]{String.valueOf(id)});

		int rowsNum = cursor.getCount();
		
		String connectSSID = "";
		
		if(rowsNum == 1)
		{
			cursor.moveToFirst();
			
			connectSSID = cursor.getString(0);
			
			Intent _autoConnectServiceIntent = new Intent(ControlActivity.this, AutoConnectService.class); 
			_autoConnectServiceIntent.putExtra("connect_ssid", connectSSID);
			startService(_autoConnectServiceIntent);

		}
		
		cursor.close();
		db.close();
		dbHelper.close();

	}
	
	private void initialView()
	{
		btnConnectAdd = (Button) findViewById(R.id.activity_control_btn_connect_add);
		btnConnectAdd.setOnClickListener(btnOnClickListener);
		
		_listView = (ListView)findViewById(R.id.activity_control_listview);
		
		_connectInfoList = loadDBConnect();
		
		_controlListAdapter = new ControlListAdapter(this, _connectInfoList); 
		
		_listView.setAdapter(_controlListAdapter);
		_listView.setOnItemClickListener(listOnItemClickListener);
		_listView.setOnItemLongClickListener(listOnItemLongClickListener);
		
	}
	
	private void initialLongClickOption()
	{
		_longClickOptionList = new ArrayList<FormSpinnerOptionObject>();
		
		_longClickOptionList.add( new FormSpinnerOptionObject(getResources().getString(R.string.control_dialog_action_login), CONTROL_ACT_LOGIN) );
		_longClickOptionList.add( new FormSpinnerOptionObject(getResources().getString(R.string.control_dialog_action_edit), CONTROL_ACT_EDIT) );
		_longClickOptionList.add( new FormSpinnerOptionObject(getResources().getString(R.string.control_dialog_action_delete), CONTROL_ACT_DELETE) );
		
		_longClickOptionAdapter = new FormSpinnerDiagloAdapter(ControlActivity.this, _longClickOptionList);
	}
	
	private ArrayList<ConnectInfoObject> loadDBConnect()
	{
		ArrayList<ConnectInfoObject> list = new ArrayList<ConnectInfoObject>();
		
		DBHelper dbHelper = new DBHelper(ControlActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT _ID, _UUID, _Name, _Account, _ConnectType FROM connect", null);
		
		int rowsNum = cursor.getCount();
		
		cursor.moveToFirst();
		
		for(int i=0 ; i<rowsNum ; i++)
		{
			ConnectInfoObject connectInfo = new ConnectInfoObject(cursor.getLong(0),
																  cursor.getString(1),
																  cursor.getString(2),
																  cursor.getString(3),
																  ConnectType.values()[cursor.getInt(4)]);
		    
		    list.add(connectInfo);
		    
		    cursor.moveToNext();
		}
		
		cursor.close(); 
		
		dbHelper.close();
		
		return list;
	}

}
