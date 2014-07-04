package shikar.in.voidify.object;

public class ConnectGroupObject 
{
	private int _type;
	private String _title;

	public int getType()
	{
		return _type;
	}
	
	public String getTitle()
	{
		return _title;
	}

	public void setType(int type)
	{
		_type = type;
	}
	
	public void setTitle(String title)
	{ 
		_title = title;
	}
	
	public ConnectGroupObject()
	{
		_type = 0;
		_title = "";
	}
	
	public ConnectGroupObject(int type, String title)
	{
		_type = type;
		_title = title;
	}
}
