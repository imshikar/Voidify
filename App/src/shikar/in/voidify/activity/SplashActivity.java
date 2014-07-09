package shikar.in.voidify.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import shikar.in.voidify.R;
import shikar.in.voidify.R.layout;
import shikar.in.voidify.service.AutoConnectService;
import shikar.in.voidify.utils.ApplicationConfig;
import shikar.in.voidify.utils.Common;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 2000;
	
	private Intent _autoConnectServiceIntent;
	
	private TextView _textView;
	
	private String _strStartService;
	
	private boolean _falgDotAnimation;
	
	private Handler _AnimationHandler;
	
	private ApplicationConfig _config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		_textView = (TextView) findViewById(R.id.activity_splash_text_status);
		
		_config = new ApplicationConfig(SplashActivity.this);
		
		startAutoConnectService();
		
	}
	
    
    private void startAutoConnectService()
    {
    	String splashShowDate = _config.getSplashShowDate();
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis()) ;
    	String curDateStr = formatter.format(curDate);
    	
    	if(!splashShowDate.equals(curDateStr))
    	{
    		_strStartService = getResources().getString(R.string.splash_start_service);
    		
    		_textView.setText(_strStartService);
    		
    		_falgDotAnimation = true;
    		
    		_AnimationHandler = new Handler();
        	_AnimationHandler.post(runnableDotAnimation);
        	
        	_config.setSplashShowDate(curDateStr);
    		
    		new Handler().postDelayed(new Runnable()
    		{ 
    	         @Override 
    	         public void run() 
    	         { 
    	        	 _falgDotAnimation = false;
    	        	 startNextActivity();
    	         } 
    	            
    	    }, SPLASH_DISPLAY_LENGHT); 
    	}
    	else
    	{
    		startNextActivity();
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
     
	private void startGuideActivity()
	{
		Intent mainIntent = new Intent(SplashActivity.this,GuideActivity.class); 
        
        SplashActivity.this.startActivity(mainIntent); 
        SplashActivity.this.finish(); 
	}
	
    private void startNextActivity()
    {   
    	boolean showGuide = _config.getGuideShow();
    			
    	Intent intent = null;
    	
    	if(showGuide)
    	{
    		intent = new Intent(SplashActivity.this,MainActivity.class); 
    	}
    	else
    	{
    		intent = new Intent(SplashActivity.this,GuideActivity.class); 
    		
    		_config.setGuideShow(true);
    	}    	
    	
        SplashActivity.this.startActivity(intent); 
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
