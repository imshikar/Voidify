package shikar.in.voidify.activity;

import java.util.ArrayList;
import java.util.List;

import shikar.in.voidify.R;
import shikar.in.voidify.R.layout;
import shikar.in.voidify.adapter.ViewPagerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideActivity extends Activity 
{
	private final int GUID_LENGTH = 11;
	
	private ViewPager _viewPager;
	private ViewPagerAdapter _viewPagerAdapter; 
	private List<View> _listView;
	
	private Button _btnStart;
	
	private ImageView[] _pagerIndicator;
	
	private int[][] _guideContent = {
			{R.string.guide_view_title_1, R.string.guide_view_content_1,  R.drawable.guide_1},
			{R.string.guide_view_title_2, R.string.guide_view_content_2,  R.drawable.guide_2},
			{R.string.guide_view_title_3, R.string.guide_view_content_3,  R.drawable.guide_3},
			{R.string.guide_view_title_4, R.string.guide_view_content_4,  R.drawable.guide_4},
			{R.string.guide_view_title_5, R.string.guide_view_content_5,  R.drawable.guide_5},
			{R.string.guide_view_title_6, R.string.guide_view_content_6,  R.drawable.guide_6},
			{R.string.guide_view_title_7, R.string.guide_view_content_7,  R.drawable.guide_7},
			{R.string.guide_view_title_8, R.string.guide_view_content_8,  R.drawable.guide_8},
			{R.string.guide_view_title_9, R.string.guide_view_content_9,  R.drawable.guide_9},
			{R.string.guide_view_title_10, R.string.guide_view_content_10,  R.drawable.guide_10},
			{R.string.guide_view_title_11, R.string.guide_view_content_11,  R.drawable.guide_11}
	};
	
	private int _currentIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		_btnStart = (Button) findViewById(R.id.activity_guide_btn_start);
		_btnStart.setOnClickListener(onBtnClickListener);
		
		intialViewPager();
		intialPagerIndicator();
	}
	
	private void intialViewPager()
	{
		_listView = new ArrayList<View>();

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for(int i=0; i<GUID_LENGTH; i++)
		{  
			View view = inflater.inflate(R.layout.dynamic_pager_guid , null, true);  
			
			TextView textTitle = (TextView) view.findViewById(R.id.dynamic_page_guid_text_title);
			textTitle.setText(getResources().getString(_guideContent[i][0]));
			
			TextView textContent = (TextView) view.findViewById(R.id.dynamic_page_guid_text_content);
			textContent.setText(getResources().getString(_guideContent[i][1]));
			
			ImageView imgIcon = (ImageView) view.findViewById(R.id.dynamic_page_guid_img_icon);
			imgIcon.setImageDrawable(getResources().getDrawable(_guideContent[i][2]));

			
            _listView.add(view);  
        }
		
		_viewPager = (ViewPager) findViewById(R.id.activity_guide_viewpager);
		
		_viewPagerAdapter = new ViewPagerAdapter(_listView); 
		
		_viewPager.setAdapter(_viewPagerAdapter);
		
		_viewPager.setOnPageChangeListener(onPageChangerListener);
		
	}
	
	private void intialPagerIndicator()
	{
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_guide_linearlayout_pager_indicator);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		_pagerIndicator = new ImageView[GUID_LENGTH];
		
		for (int i = 0 ; i < GUID_LENGTH ; i++) 
		{  
			View view = inflater.inflate(R.layout.dynamic_pager_indicator_dot , null, true); 
			
			_pagerIndicator[i] = (ImageView) view.findViewById(R.id.dynmaic_pager_indicator_imgview_dot); 
			_pagerIndicator[i].setEnabled(true);
			_pagerIndicator[i].setOnClickListener(onClickListener);  
			_pagerIndicator[i].setTag(i);
			
			linearLayout.addView(view);
        }  
		
		_currentIndex = 0; 
		_pagerIndicator[_currentIndex].setEnabled(false);
	}
	
	private void setCurrentView(int position)  
    {  
        if (position < 0 || position >= GUID_LENGTH) {  
            return;  
        }  
  
        _viewPager.setCurrentItem(position);  
    } 
	
	private void setCurrentDot(int position)  
	{  
		if (position < 0 || position > GUID_LENGTH - 1 || _currentIndex == position) 
		{  
			return;  
	    }  
	  
		_pagerIndicator[position].setEnabled(false);  
		_pagerIndicator[_currentIndex].setEnabled(true);  
	  
		_currentIndex = position;  
	}  
	
	private void setCurrentButton(int position)
	{
		if(position >= GUID_LENGTH-1)
		{
			_btnStart.setText(getResources().getString(R.string.guide_button_start_finish));
		}
		else
		{
			_btnStart.setText(getResources().getString(R.string.guide_button_start_next));
		}
	}
	
	private OnPageChangeListener onPageChangerListener = new OnPageChangeListener()
	{

		@Override
		public void onPageScrollStateChanged(int arg0) 
		{
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) 
		{
			
		}

		@Override
		public void onPageSelected(int position) 
		{
			setCurrentDot(position);
			setCurrentButton(position);			
		}
		
	};
	
	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			int position = (Integer)v.getTag();  
			
		    setCurrentView(position);  
		    setCurrentDot(position);  
		    setCurrentButton(position);	
		}
		
	};
	
	private OnClickListener onBtnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if(_currentIndex >= GUID_LENGTH-1)
			{
				Intent mainIntent = new Intent(GuideActivity.this,MainActivity.class); 
		        
		        startActivity(mainIntent); 
		        finish(); 
			}
			else
			{
				
				 setCurrentDot(_currentIndex + 1);
				 setCurrentView(_currentIndex);  
				 setCurrentButton(_currentIndex);
				  
					
			}
		}
		
	};
}
