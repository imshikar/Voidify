package shikar.in.voidify.receiver;


import shikar.in.voidify.R;
import shikar.in.voidify.activity.MainActivity;
import shikar.in.voidify.service.AutoConnectService;
import shikar.in.voidify.utils.AutoConnect;
import shikar.in.voidify.utils.Common;
import shikar.in.voidify.utils.NotificationBox;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class WifiBroadcastReceiver extends BroadcastReceiver 
{
	private final static String DEBUG_TAG = "Voidify_Receiver_WifiBroadcast";
	
	private Thread _thread;
	
	private NotificationBox _notification;
	
	private AutoConnect _autoConnect;
	
	private Context _context;
	
	private Handler _handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case AutoConnect.CHECK_NETWORK_STATUS:
					
					boolean networkStatusResult = (Boolean)msg.obj;
						
					handlerCheckNetworkStatus(networkStatusResult);

		            break;
			}
		}
	};
	
	private void handlerCheckNetworkStatus(boolean status)
	{
		if(!status)
		{
			String notifyTitle = _context.getResources().getString(R.string.app_name);
			String notifyTicker = "";
			String notifyContent = "";
			
			Intent broadcastIntent = new Intent(AutoConnectService.BROADCAST_AUTO_CONNECT);
			broadcastIntent.putExtra("connect_ssid", _autoConnect.getSSID());
			
			switch(_autoConnect.getConnectType())
			{
				case AUTO:
					notifyTicker = _context.getResources().getString(R.string.notification_ticker_auto);	
					notifyContent = _context.getResources().getString(R.string.notification_content_auto);

					_context.sendBroadcast(broadcastIntent);
					
					break;
					
				case TIPS:
					notifyTicker = _context.getResources().getString(R.string.notification_ticker_tips);	
					notifyContent = _context.getResources().getString(R.string.notification_content_tips);

					break;
					
				case DISABLE:
					notifyTicker = _context.getResources().getString(R.string.notification_ticker_disable);	
					notifyContent = _context.getResources().getString(R.string.notification_content_disable);
					break;
					
			}
			
			

			
			
			notifyTicker = notifyTicker.replace("[ConnectName]", _autoConnect.getName());
			notifyContent = notifyContent.replace("[ConnectName]", _autoConnect.getName());

			_notification = new NotificationBox(_context, broadcastIntent, notifyTitle, notifyContent, notifyTicker, Notification.DEFAULT_LIGHTS);
			_notification.notifyBox();
			
		}
	}
	
	private void startAutoConnectService()
    {

    	Intent _autoConnectServiceIntent = new Intent(_context, AutoConnectService.class); 
    	_context.startService(_autoConnectServiceIntent);
    	
    }
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		
		if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			NetworkInfo netInfo = intent.getParcelableExtra (WifiManager.EXTRA_NETWORK_INFO);
			
			if (ConnectivityManager.TYPE_WIFI == netInfo.getType())
			{
				_context = context;
				
				if(netInfo.isConnected())
				{					
					WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
					WifiInfo info = wifiManager.getConnectionInfo();
					
					String connectedSSID = info.getSSID().replace("\"", "");
					connectedSSID = connectedSSID.replace("'", "\'");
					
					_autoConnect = new AutoConnect(context, connectedSSID, _handler);
					
				    if(_autoConnect.checkConnectConfig())
				    {
						startAutoConnectService();
				    	
				    	_thread = new Thread(_autoConnect.checkNetworkStatusRunnable);
						_thread.start();
				    }
				}
			}
		}
	}

}
