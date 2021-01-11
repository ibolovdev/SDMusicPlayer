package com.ibo_android.sdmusicplayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList; 

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

public class DeletePlayListsAdapter extends BaseAdapter {

    public ArrayList<String> selectedplaylists = null;	
	private CharSequence[] playlists;
	private LayoutInflater minfl;	
	 SharedPreferences prefs ;
		public WeakReference<DeletePlaylistsActivity>  _act;
		
		
	public class DeletePlaylistViewHolder
	{
		String playlist;
		CheckBox mChoose;
		TextView mTitle;				
	}		
	
	public DeletePlayListsAdapter(DeletePlaylistsActivity act, CharSequence[] playlists_asparam) {
		super();
		
		playlists = playlists_asparam;
		minfl = LayoutInflater.from(act);
		selectedplaylists = new ArrayList<String>();
	    prefs = PreferenceManager.getDefaultSharedPreferences(act);
	    
		_act = new WeakReference<DeletePlaylistsActivity>(act);		
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return playlists.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return playlists[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int pos, View v, ViewGroup vg) {
		DeletePlaylistViewHolder holder=null;		 
		 
		if (v==null || v.getTag()== null)
		{
			v = minfl.inflate(R.layout.activity_delete_playlist_item, null);
			holder = new DeletePlaylistViewHolder();
			holder.mTitle = (TextView)v.findViewById(R.id.tv_delete_pl_item);
			
			holder.mChoose = (CheckBox)v.findViewById(R.id.cb_delete_pl_item);			
			holder.mChoose.setTag(holder);
			v.setTag(holder);			
		}
		else
		{			
			holder = (DeletePlaylistViewHolder) v.getTag();				
		}
		
				
		holder.playlist =(String) playlists[pos];
		holder.mTitle.setText( holder.playlist);
		
	
		 int  TextSize = prefs.getInt("text_size", 12);
		 holder.mTitle.setTextSize(TextSize);
		
		v.setTag(holder);	
		holder.mChoose.setTag(holder);//check if this good from a performance point of view
	
		
		
		
		try {
			MainActivity.ApplySelectorsSize(_act.get(), holder.mChoose);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		
		
		
		if (selectedplaylists.contains(holder.playlist))
		{
			holder.mChoose.setChecked(true);
		}
		else
		{
			holder.mChoose.setChecked(false);
		}	
		 
		 holder.mChoose.setOnClickListener(new OnClickListener() {
	    	  public void  onClick(View v)
	    	  {
	    		  ChoosePlaylist(v);	    		      		  
	    	  }
	       }
	       );	  
		  
				 
		return v;	
	}
	
	
	public void ChoosePlaylist(View v)
	{				 
		DeletePlaylistViewHolder vh = (DeletePlaylistViewHolder)  v.getTag();
		//String playlist = vh.playlist;
		//CheckBox cb = vh.mChoose;	 
						 
			 if ( vh.mChoose.isChecked())
			 {				
				 selectedplaylists.add(vh.playlist);			 
			 }
			 else
			 {				 
				 selectedplaylists.remove(vh.playlist);
			 }			 
		
	}//ChoosePlaylist	

}
