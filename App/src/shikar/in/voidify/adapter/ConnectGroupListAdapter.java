package shikar.in.voidify.adapter;

import java.util.ArrayList;

import shikar.in.voidify.R;
import shikar.in.voidify.object.ConnectGroupObject;
import shikar.in.voidify.object.ConnectWifiObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConnectGroupListAdapter extends BaseAdapter
{
	private LayoutInflater _layoutInflater;
	private ArrayList<ConnectGroupObject> _list;
	
	public ConnectGroupListAdapter(Context c, ArrayList<ConnectGroupObject> l) 
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
        convertView = _layoutInflater.inflate(R.layout.listview_connect, null);

        TextView wifiTitle = (TextView) convertView.findViewById(R.id.listview_connect_text_wifi_title);
       
        ConnectGroupObject connectInfo = _list.get(position);
        
        wifiTitle.setText(connectInfo.getTitle());
        
        return convertView;
    }
}
