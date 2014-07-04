package shikar.in.voidify.object;

import android.widget.EditText;
import android.widget.TextView;
import shikar.in.voidify.utils.FormSpinnerDialog;

public class FormInputObject 
{
	private String _type;
	private Object _object;
	
	public FormInputObject(String type, Object object)
	{
		_type = type;
		_object = object;
	}
	
	public void setType(String type)
	{
		_type = type;
	}
	
	public void setObject(Object object)
	{
		_object = object;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public Object getObject()
	{
		return _object;
	}
	
	public int getOption()
	{
		int option = -1;
		
		if(_type.equals("Spinner"))
		{
			FormSpinnerDialog inputSpinner = (FormSpinnerDialog) _object;

			return inputSpinner.getOption();
		}
		
		return option;
	}
	
	public String getTitle()
	{
		if(_type.equals("Spinner"))
		{
			FormSpinnerDialog inputSpinner = (FormSpinnerDialog) _object;

			FormSpinnerOptionObject inputOption =  inputSpinner.getOptionObject();
			
			
			return inputOption.getTitle();
		}
		
		return null;
	}
	
	public String getValue()
	{
		if(_type.equals("Text") || _type.equals("Password"))
		{
			EditText inputText = (EditText)_object;
			
			return inputText.getText().toString();
		}
		else if(_type.equals("Spinner"))
		{
			FormSpinnerDialog inputSpinner = (FormSpinnerDialog) _object;
			
			FormSpinnerOptionObject inputOption =  inputSpinner.getOptionObject();
			
			return  inputOption.getValue();
		}
		
		return null;
	}

}
