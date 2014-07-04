package shikar.in.voidify.utils;

import shikar.in.voidify.adapter.FormSpinnerDiagloAdapter;
import shikar.in.voidify.object.FormSpinnerOptionObject;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class FormSpinnerDialog  extends Button 
{
	private DialogListview _dialog;
	private String _title;
	
	private SpinnerDialogAdapterCallback _callbackAdapter;
		
	public FormSpinnerDiagloAdapter _adapter;
	
	public int _position;

	public FormSpinnerDialog(Context context) 
	{
		super(context, null);
	}
	
	public FormSpinnerDialog(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	
		init();
	}
	
	public void init() 
	{
		setOnClickListener(onClick);
		_position = 0;
	}
	
	public void setDefaultText(String string) 
	{
		if (string != null)
			this.setText(string);
	}

	public void setTitle(String title) 
	{
		_title = title;
	}

	@Override
	protected void onDetachedFromWindow() 
	{
		super.onDetachedFromWindow();
		if (_dialog != null && _dialog.isShowing())
			_dialog.dismiss();
	}

	public OnClickListener onClick = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			popupSpinnerAdapter();
		}
	};

	public void setAdapter(FormSpinnerDiagloAdapter adapter, SpinnerDialogAdapterCallback callbackAdapter) 
	{
		_adapter = adapter;
		_callbackAdapter = callbackAdapter;
		FormSpinnerOptionObject option = (FormSpinnerOptionObject) _adapter.getItem(_position);	
    	FormSpinnerDialog.this.setText(option.getTitle());
	}
	
	private void popupSpinnerAdapter() 
	{
		if (_dialog != null && _dialog.isShowing())
		{
			_dialog.dismiss();
		}
		
		_dialog  = new DialogListview(getContext(), _adapter, _title);
		
		_dialog.setOnItemClickListener(new OnItemClickListener() {
	    	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               
            	FormSpinnerOptionObject option = (FormSpinnerOptionObject) _adapter.getItem(position);
            	
            	FormSpinnerDialog.this.setText(option.getTitle());
            	
            	_position = position;
            	            	
            	_dialog.dismiss();
           
            }

       });
		
		_dialog.show();
	}
	
	
	public void setOnAdapterItemSelectionListener( SpinnerDialogAdapterCallback callback)
	{
		_callbackAdapter = callback;
	}

	public FormSpinnerDiagloAdapter getAdapter() 
	{
		return _adapter;
	}
	
	public int getOption()
	{
		return _position;
	}
	
	public void setOption(int index)
	{
		_position = index;
		
		FormSpinnerOptionObject option = (FormSpinnerOptionObject) _adapter.getItem(index);
    	
    	FormSpinnerDialog.this.setText(option.getTitle());
	}
	
	public FormSpinnerOptionObject getOptionObject()
	{
		return (FormSpinnerOptionObject)_adapter.getItem(_position);
	}

	interface SpinnerDialogAdapterCallback 
	{
		public void onItemselectionAdapter(FormSpinnerDialog spinner, int position);
	}

}
