package shikar.in.voidify.utils;

import shikar.in.voidify.R;
import shikar.in.voidify.activity.ConnectConfigActivity;
import shikar.in.voidify.activity.ConnectWifiActivity;
import shikar.in.voidify.adapter.FormSpinnerDiagloAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class DialogAlert extends Dialog implements android.view.View.OnClickListener{
	
	 public static enum ButtonType
	 {
		 OKAndCancel,
		 OK
	 }
	
	 private Context _context;
	 private Button _buttonCancel;
	 private Button _buttonOK;
	 private TextView _textTitle;
	 private TextView _textContent;
	 private DialogAlertCallback _callBack;
	 
	 private String _title;
	 private String _content;
	 private ButtonType _type;
	 
	 private String _buttonOKText;
	 private String _buttonCancelText;
	 
	 private Object _object;
	 
	 
	 public DialogAlert(Context context, String title, String content, ButtonType type) 
	 {
		 super(context);
		 _context = context;
		 _title = title;
		 _content = content;
		 _type = type;
		 _buttonOKText = null;
		 _buttonCancelText = null;
		 _object = null;
	 }
	 
	 public void setCallbackObject (Object object)
	 {
		 _object = object;
	 }
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState) 
	 {
	    super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    setContentView(R.layout.dialog_alert);
	    
	    if(_buttonOKText == null)
	    {
	    	_buttonOKText = _context.getResources().getString(R.string.dialog_alert_button_ok);
	    }
	    
	    if(_buttonCancelText == null)
	    {
	    	_buttonCancelText = _context.getResources().getString(R.string.dialog_alert_button_cancel);
	    }
	    
	    _textTitle = (TextView) findViewById(R.id.dialog_alert_text_title);
	    _textTitle.setText(_title);
	    
	    _textContent = (TextView) findViewById(R.id.dialog_alert_text_content);
	    _textContent.setText(_content);
	    
	    _buttonOK = (Button) findViewById(R.id.dialog_alert_btn_ok);
	    _buttonOK.setText(_buttonOKText);
	    _buttonOK.setOnClickListener(this);
	    
	    _buttonCancel = (Button) findViewById(R.id.dialog_alert_btn_cancel);
	    _buttonCancel.setText(_buttonCancelText);
	    _buttonCancel.setOnClickListener(this);
	    
	    switch(_type)
	    {
	    	case OK:
	    		_buttonCancel.setVisibility(View.GONE);
	    		break;
	    }
		 
	    if(_callBack == null)
	    {
			_callBack = new DialogAlertCallback()
			{
				@Override
				public void onButtonClick(int buttonID, Object object) 
				{
					
					
				}
			};
	    }	    
	 }
	 
	 public void setButtonOKText(String text)
	 {
		 _buttonOKText = text;
	 }
	 
	 public void setButtonOKText(int id)
	 {
		 _buttonOKText =  _context.getResources().getString(id);
	 }
	 
	 public void setButtonCancelText(String text)
	 {
		 _buttonCancelText = text;
	 }
	 
	 public void setButtonCancelText(int id)
	 {
		 _buttonCancelText =  _context.getResources().getString(id);
	 }
	 
	 public void setCallback(DialogAlertCallback callback)
	 {
		 _callBack = callback;
	 }
	 
	 
	 @Override
	 public void onClick(View v) 
	 {
		_callBack.onButtonClick(v.getId(), _object);
	    dismiss();
	  }

	 public interface DialogAlertCallback
	 {	 
		 public void onButtonClick(int buttonID, Object object);
	 }
}
