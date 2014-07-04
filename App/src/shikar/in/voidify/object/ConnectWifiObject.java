package shikar.in.voidify.object;

public class ConnectWifiObject 
{	
	private String _uuid;
	private String _name;
	
	public String getUUID()
	{
		return _uuid;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setUUID(String uuid)
	{ 
		_uuid = uuid;
	}
	
	public void setName(String name)
	{
		_name = name;
	}
	
	public ConnectWifiObject()
	{
		_uuid = "";
		_name = "";
	}
	
	public ConnectWifiObject(String uuid, String name)
	{
		_uuid = uuid;
		_name = name;
	}


}
