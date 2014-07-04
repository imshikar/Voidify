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

public class DialogListview extends Dialog implements android.view.View.OnClickListener{

	 private Context _context;
	 private Button _buttonCancel;
	 private ListView _listView;
	 private TextView _textTitle;
	 private FormSpinnerDiagloAdapter _adapter;
	 private OnItemClickListener _onItemClickListener;
	 private String _title;
	 
	 public DialogListview(Context context, FormSpinnerDiagloAdapter adapter) 
	 {
		 super(context);
		 _context = context;
		 _adapter = adapter;
		 _title = "請選擇";
	 }
	 
	 public DialogListview(Context context, FormSpinnerDiagloAdapter adapter, String title) 
	 {
		 super(context);
		 _context = context;
		 _adapter = adapter;
		 _title = title;
	 }
	 
	 public void setOnItemClickListener(OnItemClickListener listener)
	 {
		 _onItemClickListener = listener;
	 }
	 
	 public void setAdapter(FormSpinnerDiagloAdapter adapter)
	 {
		 _adapter = adapter;
	 }
	 
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState) 
	 {
	    super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    setContentView(R.layout.dialog_spinner);
	    
	    _textTitle = (TextView) findViewById(R.id.dialog_spinner_text_title);
	    _textTitle.setText(_title);
	    
	    _buttonCancel = (Button) findViewById(R.id.dialog_spinner_btn_cancel);
	    _buttonCancel.setOnClickListener(this);
	    
	    intialListView();
	 }
	 
	 private void intialListView()
	 {
		 _listView = (ListView) findViewById(R.id.dialog_spinner_listview);
		 
		 _listView.setAdapter(_adapter);
		 
		 _listView.setOnItemClickListener(_onItemClickListener);
	 }
	 
	 @Override
	 public void onClick(View v) 
	 {
	    switch (v.getId()) 
	    {
		    case R.id.dialog_spinner_btn_cancel:
		      dismiss();
		      break;
		    default:
		      break;
	    }
	    dismiss();
	  }

}
