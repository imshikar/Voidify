package shikar.in.voidify.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AutoConnectWebView extends WebView
{
	private final static String DEBUG_TAG = "Voidify_Utils_AutoConnectWebView";
	
	public final static int MSG_PAGE_TIMEOUT = 1;
	public final static int MSG_PAGE_FINISHED = 2;
	public final static int MSG_JQUERY_SUPPORTED = 3;
	public final static int MSG_JS_ALERT = 4;
	
	private Context _context;
	private long _timeOut = 20000;
	private Timer _timer;
	
	private Handler _webHandler;
	
	private String _thisPageURL;
	
	public void setTimeOut(long time)
	{
		_timeOut = time;
	}

	public AutoConnectWebView(Context context, Handler handler)
	{
		super(context);
		_context = context;
		_webHandler = handler;
		
		if(_webHandler == null)
		{
			_webHandler = defaultWebHandler;
		}
		
		initial();
	}
	
	public AutoConnectWebView(Context context)
	{
		super(context);
		_context = context;
		_webHandler = defaultWebHandler;
		
		initial();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void initial()
	{
		this.setWebChromeClient(new AutoConnectWebChromeClient());
		this.setWebViewClient(new AutoConnectWebViewClient());
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setAllowContentAccess(true);
		this.getSettings().setAllowFileAccess(true); 
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setSavePassword(false);
	}
	
	public void addJQuerySupported(boolean trriger)
	{
		this.loadJavaScript("if(typeof($) == 'undefined'){var js_element=document.createElement(\"script\");js_element.setAttribute(\"src\",\"//voidify-app-asset/jquery.min.js\");document.getElementsByTagName(\"head\")[0].appendChild(js_element);}");
		
		if(trriger)
		{
			Message message = new Message();
	        message.what = MSG_JQUERY_SUPPORTED;
	        _webHandler.sendMessage(message);
		}
	}
	
	public void loadJavaScript(String script)
	{
		Log.d(DEBUG_TAG, "javascript:"+script);
		
		this.loadUrl("javascript:"+script);
	}
	
	private String getBasePageURL(String url)
	{
		String result = url;
		
		try {
			URL realURL = new URL(url);
			URL baseURL = new URL(realURL.getProtocol(), realURL.getHost(), realURL.getPort(), realURL.getPath());
		
			result = baseURL.toString();	
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
	
		return result;
	}
	
	private String getBasePageName(String url)
	{
		String result = url;
		
		try {
			URL realURL = new URL(url);	
			String[] splitPath = realURL.getPath().split("/");
			
			if(splitPath.length > 0)
			{
				result = splitPath[splitPath.length - 1];
			}
			else
			{
				result = "index.page";
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
	
		return result;
	}
	
	private Handler defaultWebHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
		}
	}; 
	
	private Handler timeOutHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{       
		    	case MSG_PAGE_TIMEOUT :
		    		Log.d(DEBUG_TAG, "WebView Timeout");    
		    		break;  
		    }
		}
	};
	
	private class AutoConnectWebChromeClient extends WebChromeClient 
	{
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) 
		{
			String[] msgSplit = message.split("@");
			String showFlag = msgSplit[0];
			
			if(showFlag.equals("Voidify_Alert"))
			{
				String showAction = msgSplit[1];
				String showMessage = msgSplit[2];
				
				JSAlertMessage jsAlert = new JSAlertMessage(showAction, showMessage);
				
				Message messageHandler = new Message();
				messageHandler.what = MSG_JS_ALERT;
				messageHandler.obj = jsAlert;
		        _webHandler.sendMessage(messageHandler);
			}
			
			result.confirm();
		    return true;
		}
	}
	
	private class AutoConnectWebViewClient extends WebViewClient 
	{
		public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) 
		{
			if ( SslError.SSL_UNTRUSTED == error.getPrimaryError() )
		    {
				handler.proceed();
		    } 
		    else
		    {
		    	super.onReceivedSslError(view, handler, error);
		    }
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) 
		{
			Log.d(DEBUG_TAG, "URL:"+url);
			
			/*
			_timer = new Timer();
			TimerTask timerTask = new TimerTask() 
			{
				@Override
	            public void run() 
				{
					if( AutoConnectWebView.this.getProgress() < 100)
					{
						Message message = new Message();
		                message.what = MSG_PAGE_TIMEOUT ;
		                timeOutHandler.sendMessage(message);
		                _webHandler.sendMessage(message);
		                _timer.cancel();
		                _timer.purge();
					}
				}
			};
			
			 TimeOut will bug!			
			_timer.schedule(timerTask, _timeOut, 1);  
			 */
		}
		
		@Override
		public void onPageFinished(WebView view, String url) 
		{
			//_timer.cancel();
            //_timer.purge();
			
			_thisPageURL = url;
			
			Thread thread = new Thread()
			{
				@Override
			    public void run() 
				{
					String[] msgObj = new String[2];
					msgObj[0] = getBasePageURL(_thisPageURL);
					msgObj[1] = getBasePageName(_thisPageURL);
					
			        Message message = new Message();
					message.what = MSG_PAGE_FINISHED;
					message.obj = msgObj;
					_webHandler.sendMessage(message);
			    }

			};
			thread.start();
			
			Log.d(DEBUG_TAG, "PageFinished");
		}
		
		 
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url)
		{
			URL aURL = null;
			
			try 
			{
				aURL = new URL(url);

				if (aURL.getHost().equals("voidify-app-asset"))
			    {
					String filePath = aURL.getFile().substring(1);
					
					return new WebResourceResponse("text/javascript", "utf-8",
			                   					  _context.getAssets().open(filePath));
 
			    }
					
			} 
			catch (MalformedURLException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}    
		    return null;
		}
	 }
	

}
