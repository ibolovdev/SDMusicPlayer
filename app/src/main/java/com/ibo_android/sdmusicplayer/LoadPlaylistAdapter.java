package com.ibo_android.sdmusicplayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.ibo_android.sdmusicplayer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class LoadPlaylistAdapter extends BaseAdapter {

	public ArrayList<String> selectedplaylists;
	private CharSequence[] playlists;
	private LayoutInflater minfl;
	 SharedPreferences prefs ;
		public WeakReference<LoadPlaylistActivity>  _act;
	 
	public class PickPlaylistViewHolder
	{
		String playlist;
		CheckBox mCheck;
		TextView mTitle;				
	}	
	
	public LoadPlaylistAdapter(LoadPlaylistActivity act, CharSequence[] playlists_asparam) {
		super();
				
		playlists = playlists_asparam;
		minfl = LayoutInflater.from(act);
		selectedplaylists = new ArrayList<String>();
		 prefs = PreferenceManager.getDefaultSharedPreferences(act);
		 
		 _act = new WeakReference<LoadPlaylistActivity>(act);		
		
	}//LoadPlaylistAdapter
	
	public int getCount() {
		// TODO Auto-generated method stub
		return playlists.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return playlists[arg0];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int pos, View v, ViewGroup vg) {
		PickPlaylistViewHolder holder=null;		 
		 
		if (v==null || v.getTag()== null)
		{
			v = minfl.inflate(R.layout.activity_load_playlist_item, null);
			holder = new PickPlaylistViewHolder();
			holder.mTitle = (TextView)v.findViewById(R.id.txtPlaylistName);
			
			holder.mCheck = (CheckBox)v.findViewById(R.id.cbPlaylistItem);			
			holder.mCheck.setTag(holder);
			v.setTag(holder);			
		}
		else
		{			
			holder = (PickPlaylistViewHolder) v.getTag();				
		}
		
				
		holder.playlist =(String) playlists[pos];
		holder.mTitle.setText( holder.playlist);				
		
		 int  TextSize = prefs.getInt("text_size", 12);
		 holder.mTitle.setTextSize(TextSize);
		 
		v.setTag(holder);	
		holder.mCheck.setTag(holder);//check if this good from a performance point of view
	
		
		
		try {
			MainActivity.ApplySelectorsSize(_act.get(), holder.mCheck);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		if (selectedplaylists.contains(holder.playlist))
		{
			holder.mCheck.setChecked(true);
		}
		else
		{
			holder.mCheck.setChecked(false);
		}	
		 
		 holder.mCheck.setOnClickListener(new OnClickListener() {
	    	  public void  onClick(View v)
	    	  {
	    		  ChoosePlaylist(v);	    		      		  
	    	  }
	       }
	       );	  
		  
				 
		return v;	
		
	}//getView
	
	
	public void ChoosePlaylist(View v)
	{				 
		PickPlaylistViewHolder vh = (PickPlaylistViewHolder)  v.getTag();
		String playlist = vh.playlist;
		CheckBox cb = vh.mCheck;	 
						 
			 if ( cb.isChecked())
			 {				
				 selectedplaylists.add(playlist);			 
			 }
			 else
			 {				 
				 selectedplaylists.remove(playlist);
			 }			 
		
	}//ChoosePlaylist	

}
