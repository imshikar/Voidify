package shikar.in.voidify.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shikar.in.voidify.R;
import shikar.in.voidify.adapter.FormSpinnerDiagloAdapter;
import shikar.in.voidify.object.FormInputObject;
import shikar.in.voidify.object.FormSpinnerOptionObject;
import shikar.in.voidify.utils.Common;
import shikar.in.voidify.utils.DBHelper;
import shikar.in.voidify.utils.DialogAlert;
import shikar.in.voidify.utils.DialogAlert.ButtonType;
import shikar.in.voidify.utils.DialogAlert.DialogAlertCallback;
import shikar.in.voidify.utils.FormSpinnerDialog;
import shikar.in.voidify.utils.ScrollViewExt;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConnectConfigActivity extends Activity 
{
	private static enum ConfigMode
	{
		Add,
		Edit
	}
	
	private final String DEBUG_TAG = "Voidify_Activity_ConnectConfig"; 
	
	private LinearLayout _linearLayoutForm;
	
	private RelativeLayout _relativeLayoutInfoBg;
	
	private ScrollViewExt _scrollViewExtMain;
	
	private TextView _textHeaderTitle;
	private TextView _textWifiTitle;
	private TextView _textWifiSSID;
	
	private Button _btnSave;
	private Button _btnCancel;
	
	private JSONObject _jsonWiFiObject;
	private JSONArray _jsonOtherArray;
	private String _wifiUuid;
	private Long _connectID;

	private HashMap<String, FormInputObject> _inputHashMap;
	
	private DialogAlert _alertDialog;
	
	private ConfigMode _configMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_config);
		
		loadIntentExtra();
		
		initialView();
		
		try 
		{
			initialForm(_jsonWiFiObject.getJSONArray("Form"));
			initialOtherForm();
			
			if(_configMode == ConfigMode.Edit)
			{
				initialConnectConfig();
			}
			
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	private OnClickListener _onClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.activity_connect_config_btn_save:
					saveConnectConfig();
				    break;
				    
				case R.id.activity_connect_config_btn_cancel:
					backToPrevious();
					break;
			}
		}
    
    };
    
    private void backToPrevious()
    {    	
    	this.finish();
    }
	
	private void initialView()
	{
		_linearLayoutForm = (LinearLayout) findViewById(R.id.activity_connect_config_linearlayout_form);
		
		
		_scrollViewExtMain = (ScrollViewExt) findViewById(R.id.activity_connect_config_scroll_main);
		_scrollViewExtMain.setScrollViewListener(scrollViewListener);
		
		_inputHashMap = new HashMap<String, FormInputObject>();
		
		_btnSave = (Button) findViewById(R.id.activity_connect_config_btn_save);
		_btnSave.setOnClickListener(_onClickListener);
		
		_btnCancel = (Button) findViewById(R.id.activity_connect_config_btn_cancel);
		_btnCancel.setOnClickListener(_onClickListener);
		
		_textHeaderTitle = (TextView) findViewById(R.id.activity_connect_config_text_header_title);
		switch(_configMode)
		{
			case Add:
				_textHeaderTitle.setText(getResources().getString(R.string.connect_config_title_mode_add));
				_btnSave.setText(getResources().getString(R.string.connect_config_button_save_mode_add));
				break;
				 
			case Edit:
				_textHeaderTitle.setText(getResources().getString(R.string.connect_config_title_mode_edit));
				_btnSave.setText(getResources().getString(R.string.connect_config_button_save_mode_edit));
				break;
		}
		
		_textWifiTitle = (TextView) findViewById(R.id.activity_connect_config_text_wifi_title);
		_textWifiSSID = (TextView) findViewById(R.id.activity_connect_config_text_wifi_ssid);
	
		_jsonWiFiObject = loadWifiJSON(_wifiUuid);
		
		try 
		{
			String wifiName = _jsonWiFiObject.getString("Name");
			
			JSONArray jsonArraySSID = _jsonWiFiObject.getJSONArray("SSID");
			
			String wifiSSID = "";

			for(int i=0 ; i<jsonArraySSID.length(); i++)
			{
				wifiSSID += ", "+ jsonArraySSID.getString(i);
			}
			
			wifiSSID = wifiSSID.substring(1);
			
			_textWifiTitle.setText(wifiName);
			_textWifiSSID.setText(wifiSSID);
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
	private void initialConnectConfig()
	{
		DBHelper dbHelper = new DBHelper(ConnectConfigActivity.this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT _FORM, _ConnectType FROM connect WHERE _ID = "+_connectID+" LIMIT 1", null);
		
		int rowsNum = cursor.getCount();
		
		cursor.moveToFirst();

		if(rowsNum == 1)
		{
			try 
			{
				JSONObject jsonObjectConfig = new JSONObject(cursor.getString(0));
				int connectType = cursor.getInt(1);
				
				JSONArray jsonArrayForm = _jsonWiFiObject.getJSONArray("Form");
				
				for(int i=0 ; i<jsonArrayForm.length() ; i++)
				{
					JSONObject jsonObjectInput = jsonArrayForm.getJSONObject(i);
					
					String inputID = jsonObjectInput.getString("ID");
					FormInputObject inputObject = _inputHashMap.get(inputID);
					
					String inputType = inputObject.getType();
					
					if(inputType.equals("Text") || inputType.equals("Password"))
					{
						EditText editTextTemp = (EditText) inputObject.getObject();
						editTextTemp.setText(jsonObjectConfig.getString(inputID));
					}
					else if(inputType.equals("Spinner"))
					{
						FormSpinnerDialog spinnerDialogTemp = (FormSpinnerDialog) inputObject.getObject();
						spinnerDialogTemp.setOption(jsonObjectConfig.getInt(inputID+"_Index"));
					}
				}
				
		
				FormInputObject inputObject = _inputHashMap.get("ConnectType");
				FormSpinnerDialog spinnerDialogTemp = (FormSpinnerDialog) inputObject.getObject();
				spinnerDialogTemp.setOption(connectType);
				
				
			} 
			catch (JSONException e) 
			{
				Log.d(DEBUG_TAG, e.getMessage());
			}
		}
		
		db.close();
		dbHelper.close();
	}
	
	private void loadIntentExtra()
	{
		Intent intent = this.getIntent();
		
		_wifiUuid = intent.getStringExtra("wifi_uuid");
		_connectID = intent.getLongExtra("connect_id", 0);
		
		if(_connectID == 0)
		{
			_configMode = ConfigMode.Add;
		}
		else
		{
			_configMode = ConfigMode.Edit;
		}
	}
	
	private JSONObject loadWifiJSON(String uuid)
	{
		JSONObject result = null;
		
		try 
		{
			result = new JSONObject(Common.readJSONinString(uuid+".json",getApplicationContext()));
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();	
		}
		
		return result;
	}
	
	private void initialOtherForm()
	{
		try 
		{
			_jsonOtherArray = new JSONArray(Common.readJSONinString("otherform.json",getApplicationContext()));
			
			initialForm(_jsonOtherArray);
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();	
		}
				
	}
	
	private void initialForm(JSONArray jsonArrayForm)
	{
		try 
		{
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			Log.d(DEBUG_TAG, "Form Length:"+jsonArrayForm.length());
			
			for(int i=0 ; i<jsonArrayForm.length(); i++)
			{
				JSONObject jsonObjectInput = jsonArrayForm.getJSONObject(i);
				
				String inputType = jsonObjectInput.getString("Type");
				String inputID =  jsonObjectInput.getString("ID");
				String inputTitle =  jsonObjectInput.getString("Title");
				
				View view = null;
				
				Object objectValue = null;
				
				if(inputType.equals("Text"))
				{
					view = createInpuTextView(inflater, inputID, inputTitle);
					
					objectValue = view.findViewById(R.id.dynamic_form_input_text_value);
				}
				else if(inputType.equals("Password"))
				{
					view = createInpuPasswordView(inflater, inputID, inputTitle);
					
					objectValue = view.findViewById(R.id.dynamic_form_input_password_value);
				}
				else if(inputType.equals("Spinner"))
				{
					JSONArray inputOption = jsonObjectInput.getJSONArray("Option");
					
					view = createInpuSpinnerView(inflater, inputID, inputTitle, inputOption);
					
					objectValue = view.findViewById(R.id.dynamic_form_input_spinner_option);
				}
				
				FormInputObject inputObject = new FormInputObject(inputType, objectValue);
				
				if(view != null)
				{
					_inputHashMap.put(inputID, inputObject);
					_linearLayoutForm.addView(view);
				}
			}
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	private View createInpuTextView(LayoutInflater inflater, String id, String title)
	{
		View view = inflater.inflate(R.layout.dynamic_form_input_text , null, true); 
		
		TextView textViewTitle = (TextView) view.findViewById(R.id.dynamic_form_input_text_title);
		
		textViewTitle.setText(title);
		
		return view;
	}
	
	private View createInpuPasswordView(LayoutInflater inflater, String id, String title)
	{
		View view = inflater.inflate(R.layout.dynamic_form_input_password , null, true); 
		
		TextView textViewTitle = (TextView) view.findViewById(R.id.dynamic_form_input_password_title);
		
		textViewTitle.setText(title);
		
		return view;
	}
	
	private View createInpuSpinnerView(LayoutInflater inflater, String id, String title, JSONArray option)
	{
		View view = inflater.inflate(R.layout.dynamic_form_input_spinner , null, true); 
		
		TextView textViewTitle = (TextView) view.findViewById(R.id.dynamic_form_input_spinner_title);
		
		textViewTitle.setText(title);
		
		FormSpinnerDialog spinnerOption = (FormSpinnerDialog) view.findViewById(R.id.dynamic_form_input_spinner_option);
		
		ArrayList<FormSpinnerOptionObject> formSpinnerOption = new ArrayList<FormSpinnerOptionObject>();
		try
		{
			for(int i=0 ; i<option.length(); i++)
			{
				JSONObject jsonObjectOption = option.getJSONObject(i);		
				FormSpinnerOptionObject formSpinnerOptionObject = new FormSpinnerOptionObject(jsonObjectOption.getString("Title"),
																							  jsonObjectOption.getString("Value"));
				formSpinnerOption.add(formSpinnerOptionObject);
			}
		}
        catch (JSONException e) 
        {
        	e.printStackTrace();
        }
		
		
		FormSpinnerDiagloAdapter formSpinnerAdapter = new FormSpinnerDiagloAdapter(ConnectConfigActivity.this, formSpinnerOption);
		
		Log.d(DEBUG_TAG, "Spinner Option Length:"+formSpinnerAdapter.getCount());
		
		spinnerOption.setAdapter(formSpinnerAdapter,null);
		spinnerOption.setTitle(title);
		
		return view;
	}
	
	private long addDBAp(String ssid, long connectID)
	{
		String tableName = "ap";
		long apID = 0;
		
		DBHelper dbHelper = new DBHelper(ConnectConfigActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("_SSID", ssid);
		contentValues.put("_ConnectID", connectID);
		
		apID = db.insert(tableName, "", contentValues);
		
		db.close();
		
		dbHelper.close();
		
		return apID;
	}
	
	private long updateDBConnect(String uuid, String name, String account, int version, int connectType, boolean isRoam, String form, String script)
	{
		String tableName = "connect";
		long connectID = 0;
		
		DBHelper dbHelper = new DBHelper(ConnectConfigActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("_UUID", uuid);
		contentValues.put("_Name", name);
		contentValues.put("_Account", account);
		contentValues.put("_Version", version);
		contentValues.put("_ConnectType", connectType);
		contentValues.put("_IsRoam", isRoam);
		contentValues.put("_Form", form);
		contentValues.put("_Script", script);
		
		connectID = db.update(tableName, contentValues, "_ID=?", new String[]{_connectID.toString()});
		
		db.close();
		
		dbHelper.close();
		
		return connectID;
	}
	
	private long addDBConnect(String uuid, String name, String account, int version, int connectType, boolean isRoam, String form, String script)
	{
		String tableName = "connect";
		long connectID = 0;
		
		DBHelper dbHelper = new DBHelper(ConnectConfigActivity.this);
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("_UUID", uuid);
		contentValues.put("_Name", name);
		contentValues.put("_Account", account);
		contentValues.put("_Version", version);
		contentValues.put("_ConnectType", connectType);
		contentValues.put("_IsRoam", isRoam);
		contentValues.put("_Form", form);
		contentValues.put("_Script", script);
		
		connectID = db.insert(tableName, "", contentValues);
		
		db.close();
		
		dbHelper.close();
		
		return connectID;
	}
	
	private JSONObject getFormJSON()
	{
		JSONObject json = new JSONObject();
		
		try 
		{
			JSONArray jsonArrayForm = _jsonWiFiObject.getJSONArray("Form");
			
			for(int i=0 ; i<jsonArrayForm.length(); i++)
			{
				JSONObject jsonObjectInput = jsonArrayForm.getJSONObject(i);
				
				String inputID = jsonObjectInput.getString("ID");
				
				FormInputObject inputObject = _inputHashMap.get(inputID);
				
				if(inputObject.getType().equals("Spinner"))
				{
					json.put(inputID+"_Index", inputObject.getOption());
					json.put(inputID+"_Value", inputObject.getValue());
					json.put(inputID+"_Title", inputObject.getTitle());
				}
				else
				{
					
					json.put(inputID, inputObject.getValue());
				}
				
			}
			
		}
		catch (JSONException e) 
		{			
			Log.d(DEBUG_TAG, e.getMessage());
		}
		
		return json;
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
				
				String inputValue = "";
				
				if(inputType.equals("Spinner"))
				{
					String inputLabelNew = inputLabel + "_Title";
					inputValue = jsonObjectForm.getString(inputLabelNew);
					result = result.replaceAll("%"+inputLabelNew+"%", inputValue);
					
					inputLabelNew = inputLabel + "_Value";
					inputValue = jsonObjectForm.getString(inputLabelNew);
					result = result.replaceAll("%"+inputLabelNew+"%", inputValue);
					
					inputLabelNew = inputLabel + "_Index";
					inputValue = jsonObjectForm.getString(inputLabelNew);
					result = result.replaceAll("%"+inputLabelNew+"%", inputValue);
				}
				else
				{
					inputValue = jsonObjectForm.getString(inputLabel);
					result = result.replaceAll("%"+inputLabel+"%", inputValue);
				}
				
				
			}
		}
		catch (JSONException e)
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
		
		Log.d(DEBUG_TAG, result);
		
		return result;
	}
	
	private String getAccount(JSONObject jsonForm)
	{
		String account = "";
		
		try 
		{
			JSONArray jsonArrayAccount = _jsonWiFiObject.getJSONArray("Account");
		
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
	
	private boolean checkFormInput()
	{
		try 
		{
			JSONArray jsonArrayForm = _jsonWiFiObject.getJSONArray("Form");
			
			for(int i=0 ; i<jsonArrayForm.length() ; i++)
			{
				JSONObject jsonObjectInput = jsonArrayForm.getJSONObject(i);
				
				FormInputObject inputObject = _inputHashMap.get(jsonObjectInput.getString("ID"));
				
				String inputType = inputObject.getType();
				
				if(inputType.equals("Text") || inputType.equals("Password"))
				{
					if(inputObject.getValue().equals("") || inputObject.getValue() == null)
					{
						String alertTitle = getResources().getString(R.string.connect_config_alert_checkforminput_title);
						String alertMessage = getResources().getString(R.string.connect_config_alert_checkforminput_message);
						alertMessage = alertMessage.replace("[InputTitle]", jsonObjectInput.getString("Title"));
						
						_alertDialog = new DialogAlert(ConnectConfigActivity.this, alertTitle, alertMessage, ButtonType.OK);
						_alertDialog.show();
						
						
 						return false;
					}
				}
			}
			
		} 
		catch (JSONException e) 
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
				
		return true;
	}
	
	private void saveConnectConfig()
	{
		try {
			
			if(checkFormInput())
			{
				JSONObject formJSON = getFormJSON();

				String uuid = _jsonWiFiObject.getString("UUID");
				String name = _jsonWiFiObject.getString("Name");
				int version = _jsonWiFiObject.getInt("Version");
				
				FormInputObject inputConnectTypeObject = _inputHashMap.get("ConnectType");
				int connectType = Integer.parseInt(inputConnectTypeObject.getValue());
				
				boolean isRoam = false;

				String account = getAccount(formJSON);
				
				String script = modifyScript(_jsonWiFiObject.getJSONArray("Form"),formJSON,_jsonWiFiObject.getJSONObject("Script").toString());
				
				Log.d(DEBUG_TAG, "Script:"+script);
				
				long connectID  =  0;
				
				switch(_configMode)
				{
					case Add:				
						connectID = addDBConnect(uuid, 
												 name, 
												 account, 
												 version, 
												 connectType, 
												 isRoam, 
												 formJSON.toString(), 
												 script);
						
						if(connectID != -1)
						{
							
							JSONArray jsonArraySSID = _jsonWiFiObject.getJSONArray("SSID");
							
							for(int i=0 ; i<jsonArraySSID.length() ; i++)
							{
								String ssid = jsonArraySSID.getString(i);
								
								addDBAp(ssid, connectID);
							}
							
							String alertTitle = getResources().getString(R.string.connect_config_alert_saveconfig_title);
							String alertMessage = getResources().getString(R.string.connect_config_alert_saveconfig_message_add);
							
							
							_alertDialog = new DialogAlert(ConnectConfigActivity.this, alertTitle, alertMessage, ButtonType.OK);
							_alertDialog.show();
							_alertDialog.setCallback(new DialogAlertCallback(){

								@Override
								public void onButtonClick(int buttonID, Object object)
								{
									Intent intent = new Intent(ConnectConfigActivity.this, ControlActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
									startActivity(intent);
								}
								
							});

						}
						else
						{
							
						}
						break;
						
					case Edit:
						connectID = updateDBConnect(uuid, 
													name, 
													account, 
													version, 
													connectType, 
													isRoam, 
													formJSON.toString(),
													script);
						
						if(connectID != -1)
						{
							String alertTitle = getResources().getString(R.string.connect_config_alert_saveconfig_title);
							String alertMessage = getResources().getString(R.string.connect_config_alert_saveconfig_message_edit);
							
							_alertDialog = new DialogAlert(ConnectConfigActivity.this, alertTitle, alertMessage, ButtonType.OK);
							_alertDialog.show();
							_alertDialog.setCallback(new DialogAlertCallback(){

								@Override
								public void onButtonClick(int buttonID, Object object)
								{
									Intent intent = new Intent(ConnectConfigActivity.this, ControlActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
									startActivity(intent);
								}
							
								
							});

						}
						else
						{
							
						}
						break;
				}
			
			} 
		}
		catch (JSONException e) 
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
	}

	
	private ScrollViewExt.ScrollViewListener scrollViewListener = new ScrollViewExt.ScrollViewListener()
	{

		@Override
		public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) 
		{
			
		}
		
	};
	
	
}
