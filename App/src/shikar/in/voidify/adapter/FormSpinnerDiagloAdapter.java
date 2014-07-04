package shikar.in.voidify.adapter;

import java.util.ArrayList;

import shikar.in.voidify.R;
import shikar.in.voidify.object.FormSpinnerOptionObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FormSpinnerDiagloAdapter extends BaseAdapter
{
	
	private LayoutInflater _layoutInflater;
	private ArrayList<FormSpinnerOptionObject> _list;

	public FormSpinnerDiagloAdapter(Context c, ArrayList<FormSpinnerOptionObject> l) 
	{
		_layoutInflater = LayoutInflater.from(c);
		_list = l;
	}
	
	@Override
	public int getCount() 
	{
		return _list.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return _list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
        convertView = _layoutInflater.inflate(R.layout.listview_spinner_dialog, null);

        TextView optionTitle = (TextView) convertView.findViewById(R.id.listview_dialog_text_title);

        FormSpinnerOptionObject optionObject = _list.get(position);
        
        optionTitle.setText(optionObject.getTitle());
        
        return convertView;
    }

}
