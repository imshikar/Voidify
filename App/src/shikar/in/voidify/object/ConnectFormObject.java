package shikar.in.voidify.object;

import java.util.ArrayList;

public class ConnectFormObject 
{
	private String _id;
	private String _type;
	private String _title;
	private ArrayList<FormSpinnerOptionObject> _option;
	
	public String getID()
	{
		return _id;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public ArrayList<FormSpinnerOptionObject> getOptionList()
	{
		return _option;
	}
	
	public FormSpinnerOptionObject getOptionObj(int index)
	{
		return _option.get(index);
	}
	
	public void setID(String id)
	{
		_id = id;
	}
	
	public void setType(String type)
	{
		_type = type;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	public void setOption(ArrayList<FormSpinnerOptionObject> option)
	{
		_option = option;
	}
	
	public ConnectFormObject(String id, String type, String title)
	{
		_id = id;
		_type = type;
		_title = title;
		_option = null;
	}
	
	public ConnectFormObject(String id, String type, String title, ArrayList<FormSpinnerOptionObject> option)
	{
		_id = id;
		_type = type;
		_title = title;
		_option = option;
	}
}

