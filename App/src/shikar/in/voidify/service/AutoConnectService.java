package shikar.in.voidify.service;

import java.util.Timer;
import java.util.TimerTask;

import shikar.in.voidify.R;
import shikar.in.voidify.utils.AutoConnect;
import shikar.in.voidify.utils.AutoConnectWebView;
import shikar.in.voidify.utils.JSAlertMessage;
import shikar.in.voidify.utils.NotificationBox;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AutoConnectService extends Service
{
	private final static String DEBUG_TAG = "Voidify_Service_AutoConnect";

	public final static String BROADCAST_AUTO_CONNECT = "shikar.in.voidify.AUTO_CONNECT";
	
	private WindowManager _windowManager;
	private LinearLayout _linearLayoutView;
	private AutoConnectWebView _autoConnectWebView;
	private NotificationBox _notification;
	
	private String _connectSSID;
	private AutoConnect _autoConnect;
	
	private Thread _thread;
	
	private int _scriptFinishedCount = 0 ;
	
	private long _autoConnectTimeOut = 30000;
	private Timer _autoConnectTimer;
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.d(DEBUG_TAG, "Service Start");

		_connectSSID = intent.getStringExtra("connect_ssid");
		
		if(_connectSSID != null)
		{
			startConnect();
		}
		
		registerAutoConnectReciver();
		
	    return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
	}
	
	@Override
	public void onDestroy() 
	{
		unregisterAutoConnectReciver();
		destoryView();
		super.onDestroy();
	}
	
	private Handler webHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{       
		    	case AutoConnectWebView.MSG_PAGE_FINISHED:
		    		
		    		Log.d(DEBUG_TAG, "Page Finished:" + _scriptFinishedCount);
		    		
		    		String[] thisPage = (String[]) msg.obj;
		    		
		    		checkLoginSuccess(thisPage[0], thisPage[1]);
		    		
		    		break;
		    		
		    	case AutoConnectWebView.MSG_JQUERY_SUPPORTED:
		    		
		    		Log.d(DEBUG_TAG, "JQuery Supported");
		    		
		    		scriptWorker();
		    		
		    		break;
		    		
		    	case AutoConnectWebView.MSG_JS_ALERT:
		    		
		    		JSAlertMessage jsAlert = (JSAlertMessage) msg.obj;

		    		Message message = new Message();
	                message.what = AutoConnect.AUTO_CONNECT_ALERT;
	                message.obj = jsAlert;
	                
	                _handler.sendMessage(message);
		    				
		    		//Toast.makeText(AutoConnectService.this, jsAlert.getMessage(), Toast.LENGTH_SHORT).show();
		    		
		    		break;
		    }
		}
	};
	
	private void checkLoginSuccess(String thisPage, String thisFile)
	{
		Log.d(DEBUG_TAG, "File:" + thisFile);
		
		String checkType = _autoConnect.getScriptCheckType();
		int scriptLength = _autoConnect.getScriptListLength();
		
		destoryConnectTimeOut();
		
		if(checkType.equals("Index"))
		{
			if(_scriptFinishedCount >= scriptLength)
			{
				_thread = new Thread(_autoConnect.checkNetworkStatusRunnable);
				_thread.start();
			}
			else
			{
				String script = _autoConnect.getScriptJS(_scriptFinishedCount);
				
				setConnectTimeOut();
				
				if(!script.equals(""))
				{
					_autoConnectWebView.addJQuerySupported(true);
				}
				else
				{
					_scriptFinishedCount++;
				}
			}
		}
		else if(checkType.equals("Page"))
		{
			String thisCheckPage = _autoConnect.getCheckPage(_scriptFinishedCount);
			
			if(_scriptFinishedCount >= scriptLength)
			{
				_thread = new Thread(_autoConnect.checkNetworkStatusRunnable);
				_thread.start();
			}
			else
			{
				setConnectTimeOut();
				
				if(thisPage.equals(thisCheckPage))
				{
					_autoConnectWebView.addJQuerySupported(true);
				}
			}
			
		}
		else if(checkType.equals("File"))
		{
			String thisCheckFile = _autoConnect.getCheckFile(_scriptFinishedCount);
			
			if(_scriptFinishedCount >= scriptLength)
			{
				_thread = new Thread(_autoConnect.checkNetworkStatusRunnable);
				_thread.start();
			}
			else
			{
				setConnectTimeOut();
				
				if(thisFile.equals(thisCheckFile))
				{
					_autoConnectWebView.addJQuerySupported(true);
				}
			}
		}
	}
	
	private void scriptWorker()
	{	
		Thread thread = new Thread()
		{
			@Override
		    public void run() 
			{
				try {
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				finally
				{
					String script = _autoConnect.getScriptJS(_scriptFinishedCount);
					_autoConnectWebView.loadJavaScript(script);

					_scriptFinishedCount++;
				}
			}

		};
		
		thread.start();
	}
	
	private void destoryView()
	{
		try
		{
			destoryConnectTimeOut();
			
			_windowManager.removeView(_linearLayoutView);
	    	stopService(new Intent( AutoConnectService.this, AutoConnectService.class));
		}
		catch (Exception e)
		{
			Log.d(DEBUG_TAG, e.getMessage());
		}
	}
	
	private void destoryConnectTimeOut()
	{
		if(_autoConnectTimer != null)
		{
			_autoConnectTimer.cancel();
			_autoConnectTimer.purge();
			_autoConnectTimer = null;
		}
	}
	
	private void registerAutoConnectReciver()
	{
		IntentFilter autoConnectIntent = new IntentFilter(BROADCAST_AUTO_CONNECT);
		registerReceiver(autoConnectBroadcastReceiver, autoConnectIntent);
	}
	
	private void unregisterAutoConnectReciver()
	{
		unregisterReceiver(autoConnectBroadcastReceiver);
	}
	
	private void startConnect()
	{
		_scriptFinishedCount = 0;
		
		_autoConnect = new AutoConnect(AutoConnectService.this, _connectSSID, _handler);
		
		if(_autoConnect.checkConnectConfig())
		{
			String notifyTitle = getResources().getString(R.string.app_name);
			String notifyTicker = getResources().getString(R.string.notification_ticker_connect_do);	
			notifyTicker = notifyTicker.replace("[ConnectName]", _autoConnect.getName());
			
			String notifyContent = getResources().getString(R.string.notification_content_connect_do);	
			notifyContent = notifyContent.replace("[ConnectName]", _autoConnect.getName());

			initialView();
			
			Intent contentIntent = new Intent(); 
			_notification = new NotificationBox(AutoConnectService.this, contentIntent, notifyTitle, notifyContent, notifyTicker, Notification.DEFAULT_LIGHTS);
			_notification.notifyBox();
			
			setConnectTimeOut();
		}
	}
	
	private void initialView()
	{
		_windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		 
		 WindowManager.LayoutParams params = new WindowManager.LayoutParams(
										        WindowManager.LayoutParams.WRAP_CONTENT,
										        WindowManager.LayoutParams.WRAP_CONTENT,
										        WindowManager.LayoutParams.TYPE_PHONE,
										        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
										        PixelFormat.TRANSLUCENT);
		 
		 params.gravity = Gravity.TOP | Gravity.LEFT;
	     params.x = 0;
	     params.y = 0;
	     params.width = 0;
	     params.height = 0;
		 
	     _linearLayoutView  = new LinearLayout(this);
	     
	     _linearLayoutView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		
	     _autoConnectWebView = new AutoConnectWebView(AutoConnectService.this, webHandler);
	     _linearLayoutView.addView(_autoConnectWebView);

	     long timestamp = System.currentTimeMillis();
	     _autoConnectWebView.loadUrl(AutoConnect.CHECK_NETWORK_PAGE+"?r="+timestamp);
	     
	     _windowManager.addView(_linearLayoutView, params);

	}
	
	private void setConnectTimeOut()
	{
		TimerTask timerTaskConnectTimeOut = new TimerTask() 
		{
			@Override
	        public void run() 
			{
				destoryConnectTimeOut();
	            
				Message message = new Message();
	            message.what = AutoConnect.AUTO_CONNECT_TIMEOUT ;
	            _handler.sendMessage(message);
			}
		};
		 
		_autoConnectTimer = new Timer();
		_autoConnectTimer.schedule(timerTaskConnectTimeOut, _autoConnectTimeOut); 
		
		
	}
	
	private Handler _handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			String notifyTitle = "";
			String notifyTicker = "";
			String notifyContent = "";
			Intent contentIntent = new Intent(); 
			
			switch(msg.what)
			{
				case AutoConnect.CHECK_NETWORK_STATUS:
					
					boolean networkStatusResult = (Boolean)msg.obj;
					
					if(networkStatusResult)
					{
						notifyTitle = getResources().getString(R.string.app_name);
						notifyTicker = getResources().getString(R.string.notification_ticker_connect_success);	
						notifyTicker = notifyTicker.replace("[ConnectName]", _autoConnect.getName());
						
						notifyContent = getResources().getString(R.string.notification_ticker_connect_success);	
						notifyContent = notifyContent.replace("[ConnectName]", _autoConnect.getName());
												
						_notification = new NotificationBox(AutoConnectService.this, contentIntent, notifyTitle, notifyContent, notifyTicker, Notification.DEFAULT_LIGHTS);
						_notification.notifyBox();
					}
					else
					{
						notifyTitle = getResources().getString(R.string.app_name);
						notifyTicker = getResources().getString(R.string.notification_ticker_connect_failure);	
						notifyTicker = notifyTicker.replace("[ConnectName]", _autoConnect.getName());
						
						notifyContent = getResources().getString(R.string.notification_ticker_connect_failure);	
						notifyContent = notifyContent.replace("[ConnectName]", _autoConnect.getName());
						
						_notification = new NotificationBox(AutoConnectService.this, contentIntent, notifyTitle, notifyContent, notifyTicker, Notification.DEFAULT_LIGHTS);
						_notification.notifyBox();
					}
					
					destoryView();
					
		            break;
		            
				case AutoConnect.AUTO_CONNECT_TIMEOUT:
					
					notifyTitle = getResources().getString(R.string.app_name);
					notifyTicker = getResources().getString(R.string.notification_ticker_connect_timeout);	
					notifyTicker = notifyTicker.replace("[ConnectName]", _autoConnect.getName());
					
					notifyContent = getResources().getString(R.string.notification_ticker_connect_timeout);	
					notifyContent = notifyContent.replace("[ConnectName]", _autoConnect.getName());
					
					_notification = new NotificationBox(AutoConnectService.this, contentIntent, notifyTitle, notifyContent, notifyTicker, Notification.DEFAULT_LIGHTS);
					_notification.notifyBox();
					
					destoryView();
					
					break;
					
				case AutoConnect.AUTO_CONNECT_ALERT:
					
					JSAlertMessage jsAlert = (JSAlertMessage) msg.obj;
					
					notifyTitle = getResources().getString(R.string.app_name);
					
					notifyTicker = jsAlert.getMessage();	
					notifyTicker = notifyTicker.replace("[ConnectName]", _autoConnect.getName());
						
					notifyContent = jsAlert.getMessage();	
					notifyContent = notifyContent.replace("[ConnectName]", _autoConnect.getName());
					
					_notification = new NotificationBox(AutoConnectService.this, contentIntent, notifyTitle, notifyContent, notifyTicker, Notification.DEFAULT_LIGHTS);
					_notification.notifyBox();
					
					if(jsAlert.getAction() == JSAlertMessage.ActionType.DIMISS)
					{
						destoryView();
					}
					
					break;
			}
		}
	};
	
	private BroadcastReceiver autoConnectBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			
			if(action.equals(BROADCAST_AUTO_CONNECT))
			{
				_connectSSID = intent.getStringExtra("connect_ssid");
				
				if(_connectSSID != null)
				{
					startConnect();
				}
			}
			
		} 
    	
    };

    
	
}
