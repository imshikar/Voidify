package shikar.in.voidify.activity;

import shikar.in.voidify.R;
import shikar.in.voidify.R.layout;
import shikar.in.voidify.service.AutoConnectService;
import shikar.in.voidify.utils.Common;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 3000;
	
	private Intent _autoConnectServiceIntent;
	
	private TextView _textView;
	
	private String _strStartService;
	
	private boolean _falgDotAnimation;
	
	private Handler _AnimationHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		_textView = (TextView) findViewById(R.id.activity_splash_text_status);
		
		startAutoConnectService();
		
	}
	
    
    private void startAutoConnectService()
    {
    	if(!Common.checkAutoConnectService(SplashActivity.this))
    	{
    		_strStartService = getResources().getString(R.string.splash_start_service);
    		
    		_textView.setText(_strStartService);
    		
    		_falgDotAnimation = true;
    		
    		_AnimationHandler = new Handler();
        	_AnimationHandler.post(runnableDotAnimation);
    		
    		_autoConnectServiceIntent = new Intent(SplashActivity.this, AutoConnectService.class); 
    		startService(_autoConnectServiceIntent);
    		
    		new Handler().postDelayed(new Runnable()
    		{ 
    	         @Override 
    	         public void run() 
    	         { 
    	        	 _falgDotAnimation = false;
    	        	 startMainActivity();
    	         } 
    	            
    	    }, SPLASH_DISPLAY_LENGHT); 
    	}
    	else
    	{
    		startMainActivity();
    	}
    }
    
	int count = 0;
	String _strStartServiceDot = "";
	
    private Runnable runnableDotAnimation = new Runnable() 
	{
	    public void run() 
	    {
			if(count++ < 4)
			{
				_textView.setText(_strStartService+_strStartServiceDot);
				_strStartServiceDot += ".";
			}
			else
			{
				_strStartServiceDot = "";
				count = 0;
			}
			
			if(_falgDotAnimation)
			{
				_AnimationHandler.postDelayed(runnableDotAnimation, 750);
			}
	    }
	};
        
    private void startMainActivity()
    {
    	Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class); 
        
        SplashActivity.this.startActivity(mainIntent); 
        SplashActivity.this.finish(); 
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        if (_AnimationHandler != null) {
        	_AnimationHandler.removeCallbacks(runnableDotAnimation);
        }
    }
}
