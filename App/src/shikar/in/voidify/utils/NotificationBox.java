package shikar.in.voidify.utils;

import shikar.in.voidify.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationBox 
{
	 public static final String BROADCAST_ACTION = "shikar.in.voidify.NOTIFY_CLICK";
	
	 private static final String DEBUG_TAG = "Voidify_Utils_NotificationBox"; 
	 
	 private Notification _notification;
	 private NotificationManager _notificationManager;
	 
	 private Context _context;
	 
	 private String _title;
	 private String _content;
	 private String _ticker;
	 private Intent _intent;
	 private int _defaultShow;

	 public NotificationBox(Context context, Intent intent, String title, String content, String ticker, int defaultShow)
	 {
		 _context = context;
		 _intent = intent;
		 _content = content;
		 _title = title;
		 _ticker = ticker;
		 _defaultShow = defaultShow;
		 
		 initNotification();
	 }
	
	 private void initNotification()
	 {

	     
	     PendingIntent contentPendingIntent = PendingIntent.getBroadcast(_context, 
	    		 														 0, 
	    		 														 _intent, 
	    		 														 PendingIntent.FLAG_UPDATE_CURRENT);
	     

	     _notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
	     

	     Builder builder = new Notification.Builder(_context);
	     
	     builder.setContentIntent(contentPendingIntent) 
	     		.setSmallIcon(R.drawable.ic_stat_notify)
	     		.setTicker(_ticker)
	     		.setWhen(System.currentTimeMillis())
	     		.setAutoCancel(false)
	     		.setDefaults(_defaultShow)
	     		.setContentTitle(_title)
	     		.setContentText(_content);
	     
	     if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) 
	     {
	    	 _notification =  builder.getNotification();
	     } 
	     else 
	     {
	    	 _notification =  builder.build();
	     }
	 }
	 
	 
	 public void notifyBox()
	 {
		 _notificationManager.notify(1, _notification);
	 }
	 
}
