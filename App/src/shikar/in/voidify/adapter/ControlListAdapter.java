package shikar.in.voidify.adapter;

import java.util.ArrayList;



import shikar.in.voidify.R;
import shikar.in.voidify.object.ConnectInfoObject;
import shikar.in.voidify.object.ConnectWifiObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ControlListAdapter extends BaseAdapter
{
	private LayoutInflater _layoutInflater;
	private ArrayList<ConnectInfoObject> _list;
	
	private Context _context;
	
	public ControlListAdapter(Context c, ArrayList<ConnectInfoObject> l) 
	{
		_context = c;
		_layoutInflater = LayoutInflater.from(c);
		_list = l;
	}
	
	public void setList( ArrayList<ConnectInfoObject> l)
	{
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
        convertView = _layoutInflater.inflate(R.layout.listview_control, null);

        TextView wifiTitle = (TextView) convertView.findViewById(R.id.activity_connect_group_text_title);
        TextView wifiAccount = (TextView) convertView.findViewById(R.id.listview_conrol_text_wifi_account);
        TextView connectType = (TextView) convertView.findViewById(R.id.listview_control_text_connect_type);
        
        ConnectInfoObject connectInfo = _list.get(position);
        
        wifiTitle.setText(connectInfo.getTitle());
        wifiAccount.setText(connectInfo.getAccount());
        connectType.setText(connectInfo.getConnectTypeStr(_context));
        
        return convertView;
    }

}
