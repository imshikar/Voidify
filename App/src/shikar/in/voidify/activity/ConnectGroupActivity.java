package shikar.in.voidify.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shikar.in.voidify.R;
import shikar.in.voidify.adapter.ConnectGroupListAdapter;
import shikar.in.voidify.object.ConnectGroupObject;
import shikar.in.voidify.utils.Common;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ConnectGroupActivity extends Activity {
	
	private final String DEBUG_TAG = "Voidify_Activity_ConnectGroup"; 
	 
	private ListView _listView;
	private Button _btnCancel;
	private ArrayList<ConnectGroupObject> _groupList;
	private ConnectGroupListAdapter _listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_connect_group);
		
		initialView();
	}
	
	
	private void initialView()
	{
		_groupList = loadGroupList();
		
		_listAdapter = new ConnectGroupListAdapter(this, _groupList);

		_listView = (ListView)findViewById(R.id.activity_connect_group_listview);
		_listView.setAdapter(_listAdapter);
		_listView.setOnItemClickListener(new OnItemClickListener() {
	    	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              
            	Intent intent = new Intent();
            	intent.setClass(ConnectGroupActivity.this, ConnectWifiActivity.class);
                
            	intent.putExtra("group_type", _groupList.get(position).getType());
            	
            	startActivity(intent);  
           
            }

       }); 
		
		_btnCancel = (Button) findViewById(R.id.activity_connect_group_btn_cancel);
		_btnCancel.setOnClickListener(_onClickListener);		
	}
	
	private ArrayList<ConnectGroupObject> loadGroupList()
	{
		ArrayList<ConnectGroupObject> result = new ArrayList<ConnectGroupObject>();
		
		try 
		{
			JSONArray  jsonArray = new JSONArray(Common.readJSONinString("list.json",getApplicationContext()));
			
			Log.d(DEBUG_TAG, "List Length:"+jsonArray.length());
			
			for(int i=0 ; i<jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				ConnectGroupObject connectGroupObject = new ConnectGroupObject();
				connectGroupObject.setType(jsonObject.getInt("Type"));
				connectGroupObject.setTitle(jsonObject.getString("Title"));
				
				result.add(connectGroupObject);
				
				Log.d(DEBUG_TAG, "Type:"+ connectGroupObject.getType() + ", Title:"+connectGroupObject.getTitle());
			}
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();	
		}
		
		return result;
	}
	
	private OnClickListener _onClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.activity_connect_group_btn_cancel:
					ConnectGroupActivity.this.finish();
				    break;
			}
		}
    
    };

}
