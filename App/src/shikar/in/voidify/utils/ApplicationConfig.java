package shikar.in.voidify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ApplicationConfig 
{
	private final String CONFIG_NAME = "Voidify_Config";
	private final int CONFIG_MODE = 0;
	
	private final String SETTING_SPLASH_SHOW_DATE = "SPLASH_SHOW_DATE";
	private final String SETTING_GUIDE_SHOW = "GUIDE_SHOW";
	
	private SharedPreferences _sharedPreferences;
	private Editor _editor;
	private Context _context;
	
	public ApplicationConfig(Context context)
	{
		_context = context;
		_sharedPreferences = _context.getSharedPreferences(CONFIG_NAME, CONFIG_MODE);
		_editor = _sharedPreferences.edit();
	}
	
	public Editor getEditor()
	{
		return _editor;
	}
	
	public SharedPreferences getSetting()
	{
		return _sharedPreferences;
	}

	public String getSplashShowDate()
	{
		return _sharedPreferences.getString(SETTING_SPLASH_SHOW_DATE, "1999-01-01");
	}
	
	public void setSplashShowDate(String date)
	{
		_editor.putString(SETTING_SPLASH_SHOW_DATE, date);
		_editor.commit();
	}
	
	public boolean getGuideShow()
	{
		return _sharedPreferences.getBoolean(SETTING_GUIDE_SHOW, false);
	}
	
	public void setGuideShow(boolean showed)
	{
		_editor.putBoolean(SETTING_GUIDE_SHOW, showed);
		_editor.commit();
	}
}
