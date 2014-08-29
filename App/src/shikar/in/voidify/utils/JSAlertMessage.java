package shikar.in.voidify.utils;

public class JSAlertMessage 
{
	public static enum ActionType{
		TIPS,
		DIMISS
	}
	
	private ActionType _action;
	private String _message;
	
	public ActionType getAction()
	{
		return _action;
	}
	
	public void setAction(ActionType action)
	{
		_action = action;
	}
	
	public void setAction(String action)
	{
		_action = ActionType.valueOf(action);
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public void setMessage(String message)
	{
		_message = message;
	}
	
	public JSAlertMessage(String message)
	{
		setAction(ActionType.TIPS);
		setMessage(message);
	}
	
	public JSAlertMessage(ActionType action, String message)
	{
		setAction(action);
		setMessage(message);
	}
	
	public JSAlertMessage(String action, String message)
	{
		setAction(action);
		setMessage(message);
	}
}
