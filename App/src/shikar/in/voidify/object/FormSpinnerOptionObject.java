package shikar.in.voidify.object;

public class FormSpinnerOptionObject 
{
	private String _title;
	private String _value;
	
	public String getTitle()
	{
		return _title;
	}
	
	public String getValue()
	{
		return _value;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	public void setValue(String value)
	{
		_value = value;
	}
	
	public FormSpinnerOptionObject(String title)
	{
		_title = title;
		_value = null;
	}
	
	public FormSpinnerOptionObject(String title, String value)
	{
		_title = title;
		_value = value;
	}

}
