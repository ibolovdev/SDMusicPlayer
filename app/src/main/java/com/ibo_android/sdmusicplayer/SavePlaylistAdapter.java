package com.ibo_android.sdmusicplayer;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class SavePlaylistAdapter extends BaseAdapter {

	public String selectedplaylist = "";
	
	private CharSequence[] playlists;
	private LayoutInflater minfl;
	 SharedPreferences prefs ;
	 public WeakReference<Context>  _con;
	 
	 
	public class ShowPlaylistViewHolder
	{
		String playlist;
		RadioButton mChoose;
		TextView mTitle;				
	}	
	
	public SavePlaylistAdapter(Context con, CharSequence[] playlists_asparam) {
		super();
				
		playlists = playlists_asparam;
		minfl = LayoutInflater.from(con);
		 prefs = PreferenceManager.getDefaultSharedPreferences(con);
			_con = new WeakReference<Context>(con);	
		
	}//ShowPlaylistsAdapter
	
	
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
		ShowPlaylistViewHolder holder=null;		 
		 
		if (v==null || v.getTag()== null)
		{
			v = minfl.inflate(R.layout.activity_save_playlist_item, null);
			holder = new ShowPlaylistViewHolder();
			holder.mTitle = (TextView)v.findViewById(R.id.tvSavePlaylist_item);
			
			holder.mChoose = (RadioButton)v.findViewById(R.id.savepl_item_RadioButton);			
			holder.mChoose.setTag(holder);
			v.setTag(holder);			
		}
		else
		{			
			holder = (ShowPlaylistViewHolder) v.getTag();				
		}
		
				
		holder.playlist =(String) playlists[pos];
		holder.mTitle.setText( holder.playlist);				
		
		 int  TextSize = prefs.getInt("text_size", 12);
		 holder.mTitle.setTextSize(TextSize);
		 
		v.setTag(holder);	
		holder.mChoose.setTag(holder);//check if this good from a performance point of view
	
		
		
		try {
			MainActivity.ApplySelectorsSize(_con.get(), holder.mChoose);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		if (selectedplaylist.equals(holder.playlist))
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
		ShowPlaylistViewHolder vh = (ShowPlaylistViewHolder)  v.getTag();
		String playlist = vh.playlist;
		RadioButton cb = vh.mChoose;	 
						 
			 if ( cb.isChecked())
			 {				
				 selectedplaylist = playlist;			 
			 }
			 	 
		this.notifyDataSetChanged();
	}//ChoosePlaylist	

}
