package shikar.in.voidify.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter
{
	
	private List<View> _listView; 
	
	public ViewPagerAdapter (List<View> listView)
	{  
        this._listView = listView;  
    }
	
	@Override  
	public Object instantiateItem(View view, int position) 
	{
		((ViewPager) view).addView(_listView.get(position), 0);  
	          
		return _listView.get(position);  
	}
	
 
    @Override  
    public boolean isViewFromObject(View view, Object obj) 
    {  
        return (view == obj);  
    } 
	
	@Override  
    public void destroyItem(View view, int position, Object object) 
	{  
        ((ViewPager) view).removeView(_listView.get(position));       
    }  
	
	@Override  
    public void finishUpdate(View view) 
	{  

    } 

	@Override
	public int getCount() 
	{
		if (_listView != null)  
        {  
            return _listView.size();  
        }  
          
        return 0; 
	}


}
