package com.ibo_android.sdmusicplayer;

import java.util.ArrayList; 

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResultsPickerAdapter extends BaseAdapter {
	
	ArrayList<MusicFile> _mfiles;
	private LayoutInflater minfl;
	public ResultsPickerActivity _act;
	 SharedPreferences prefs ;
	 
	public class SearchResultViewHolder
	{
		MusicFile SearchResult;
		TextView mTitle;		 
	}
	
	public ResultsPickerAdapter(Context con, ArrayList<MusicFile> mfiles, ResultsPickerActivity act) {
		super();
		 
		_act = act;
		_mfiles = mfiles;
		minfl = LayoutInflater.from(con);
		 prefs = PreferenceManager.getDefaultSharedPreferences(con);
	}

	public int getCount() {
		 
		return _mfiles.size();
	}

	public Object getItem(int position) {
		 
		return _mfiles.get(position);
	}

	public long getItemId(int position) {
		 
		return position;
	}

	public View getView(int pos, View v, ViewGroup parent) {
		SearchResultViewHolder holder=null;
		 
		
		if (v==null || v.getTag()== null)
		{
			
			v = minfl.inflate(R.layout.search_results_item, null);
			v.setBackgroundResource(R.drawable.list_item_appearances);
			holder = new SearchResultViewHolder();
			holder.mTitle = (TextView)v.findViewById(R.id.txtSearchResultItem);
					
			v.setTag(holder);
			
		}
		else
		{			
			holder = (SearchResultViewHolder) v.getTag();				
		}
		
		
		holder.SearchResult = _mfiles.get(pos);
		holder.mTitle.setText( holder.SearchResult.title);	
		
		 int  TextSize = prefs.getInt("text_size", 12);
		 holder.mTitle.setTextSize(TextSize);
		
		v.setTag(holder);		 	
		
		 					
			holder.SearchResult = _mfiles.get(pos);
			holder.mTitle.setText( holder.SearchResult.title);				
			
			v.setTag(holder);	
			 
			 v.setOnClickListener(new OnClickListener() {
		    	  public void  onClick(View v)
		    	  {
		    		  ReturnResult(v);					    		      		  
		    	  }
		       }
		       );				 
		 
		return v;		
		
	}//getView
	
	private void ReturnResult(View v)
	{		
		SearchResultViewHolder vh = (SearchResultViewHolder)  v.getTag();
		MusicFile mf = vh.SearchResult; 

		_act.ReturnResult(mf);		
	}//ReturnResult

}
