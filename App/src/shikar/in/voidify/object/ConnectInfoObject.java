package shikar.in.voidify.object;

import shikar.in.voidify.R;
import android.content.Context;

public class ConnectInfoObject 
{	
	public static enum ConnectType{AUTO, TIPS, DISABLE};
	
	private long _id;
	private String _uuid;
	private String _title;
	private String _account;
	private ConnectType _connectType;
	
	public long getID()
	{
		return _id;
	}
	
	public String getUUID()
	{
		return _uuid;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public String getAccount()
	{
		return _account;
	}
	
	public ConnectType getConnectType()
	{
		return _connectType;
	}
	
	public String getConnectTypeStr(Context context)
	{
		String str = "";
		
		switch(_connectType)
		{
			case AUTO:
				str = context.getResources().getString(R.string.connect_type_auto);
				break;
				
			case TIPS:
				str = context.getResources().getString(R.string.connect_type_tips);
				break;
				
			case DISABLE:
				str = context.getResources().getString(R.string.connect_type_disable);
				break;
		}
		
		return str;
	}
	
	public void setID(long id)
	{
		_id = id;
	}
	
	public void setUUID(String uuid)
	{
		_uuid = uuid;
	}
	
	public void setTitle(String title)
	{ 
		_title = title;
	}
	
	public void setAccount(String account)
	{
		_account = account;
	}
	
	public void setConnectType(ConnectType connectType)
	{
		_connectType = connectType;
	}
	
	public ConnectInfoObject()
	{
		_id = 0;
		_uuid = "";
		_title = "";
		_account = "";
		_connectType = ConnectType.AUTO;
	}
	

	
	public ConnectInfoObject(long id, String uuid, String title, String account)
	{
		_id = id;
		_uuid = uuid;
		_title = title;
		_account = account;
		_connectType = ConnectType.AUTO;
	}
	
	public ConnectInfoObject(long id, String uuid, String title, String account, ConnectType connectType)
	{
		_id = id;
		_uuid = uuid;
		_title = title;
		_account = account;
		_connectType = connectType;
	}

}
