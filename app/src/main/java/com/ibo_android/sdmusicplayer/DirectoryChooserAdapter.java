package com.ibo_android.sdmusicplayer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import com.ibo_android.sdmusicplayer.R;
import com.ibo_android.sdmusicplayer.FilesAdapter.ViewHolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class DirectoryChooserAdapter extends BaseAdapter {

	public ArrayList<MusicFile> mfiles;
	private String _rootdir;
	private LayoutInflater minfl;	 
	public String selectedDirectory;
	 SharedPreferences prefs ;
	public ListView _fileslist;
	public WeakReference<DirectoryChooserActivity>  _act;
	 
	 		public class DirectoryViewHolder
			{
				MusicFile DirectoryName;
				TextView mTitle;
				RadioButton mRadio;
			}
				
		
			public DirectoryChooserAdapter(DirectoryChooserActivity act, String rootdir )
			{				
				minfl = LayoutInflater.from(act);
				mfiles = new ArrayList<MusicFile>();
				_rootdir=rootdir;
				 			 
				 prefs = PreferenceManager.getDefaultSharedPreferences(act);
				 _act = new WeakReference<DirectoryChooserActivity>(act);	
				getDirectories(_rootdir);		
			         
			}//FilesAdapter	
			
			public void getDirectories(String rootfolder)
			{		
				
				//mfiles.add(object);
				//this is for 1.7
				/*Iterable<Path> dirs =
					    FileSystems.getDefault().getRootDirectories();
					for (Path name: dirs) {
					    System.err.println(name);
					}*/

				
				File f = new File(rootfolder);
				
				if (f.isDirectory())//first file is the root directory
				{
				//	MusicFile mf = new  MusicFile("", "UP",f.getAbsolutePath(),0); 
				//	mf.bIsRootDirectory = 1;
				//	mfiles.add(mf);	
					
					setRootDirectory(f.getAbsolutePath());
				}
			 
				if (f.isDirectory())
				{	
					if (f.listFiles() != null)
					{
						for (File name: f.listFiles()) {				
							
							if (name.isDirectory())
							{
								MusicFile mf = new  MusicFile("", name.getName(),name.getAbsolutePath(),0); 					
								mfiles.add(mf);
							}						
						}	
					}//if (f.listFiles() != null)					
					
				}
			 
				Collections.sort(mfiles);
			}//getmusicfiles	
			
				
			
				
				public int getCount() {
					// TODO Auto-generated method stub
					return mfiles.size();
				}
			
				public Object getItem(int arg0) {
					// TODO Auto-generated method stub
					//return null;
					return mfiles.get(arg0);
				}
			
				public long getItemId(int arg0) {
					// TODO Auto-generated method stub
					//return 0;
					return arg0;
				}
			
				public View getView(int pos, View v, ViewGroup vg) {
					DirectoryViewHolder holder=null;
					 
					
					if (v==null || v.getTag()== null)
					{
						
						v = minfl.inflate(R.layout.directorychooseractivity_directory_item, null);
						holder = new DirectoryViewHolder();
						holder.mTitle = (TextView)v.findViewById(R.id.txtDirectory);
						holder.mRadio = (RadioButton)v.findViewById(R.id.rbdirectory);
						holder.mRadio.setTag(holder);
						
						v.setTag(holder);
						
					}
					else
					{			
						holder = (DirectoryViewHolder) v.getTag();				
					}
					
					
					holder.DirectoryName = mfiles.get(pos);
					holder.mTitle.setText( holder.DirectoryName.title);				
					 int  TextSize = prefs.getInt("text_size", 12);
					 holder.mTitle.setTextSize(TextSize);
					 
					v.setTag(holder);	
					
					if (selectedDirectory == holder.DirectoryName.filepath)
					{
						holder.mRadio.setChecked(true);
					}
					else
					{
						holder.mRadio.setChecked(false);
					}	
					
				 
					/*  v.setOnClickListener(new OnClickListener() {
				    	  public void  onClick(View v)
				    	  {
				    		  PlayStop(v);
				    		  
				    	  }
				       }
				       );*/		
					
					 					
						holder.DirectoryName = mfiles.get(pos);
						holder.mTitle.setText( holder.DirectoryName.title);				
						
						try {
							MainActivity.ApplySelectorsSize(_act.get(), holder.mRadio);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 			
						
						
						v.setTag(holder);	
						//holder.mCheck = null;		 
						//holder.mCheck.setVisibility(View.INVISIBLE);
						 v.setOnClickListener(new OnClickListener() {
					    	  public void  onClick(View v)
					    	  {
					    		  ChangeDirectory(v);					    		      		  
					    	  }
					       }
					       );					
					 
									
						 if ( mfiles.get(pos).title != "UP")
						 {
							 	holder.mRadio.setOnClickListener(new OnClickListener() {
						    	  public void  onClick(View v)
						    	  {
						    		  ChooseDirectory(v);	    		      		  
						    	  }
						       }
						       );						 
						 }	 
					 
					return v;
				}//getView		 
				
			/*	public MusicFile PlayStop(View v)
				{ 				 
		    			  ViewHolder vh = (ViewHolder)  v.getTag();
		    			  MusicFile mf = vh.mfile;	  
		    		 	return mf;
					
				}*///PlayStop
				
				public void ChooseDirectory(View v)
				{				 
					DirectoryViewHolder vh = (DirectoryViewHolder)  v.getTag();
					//String dir = vh.DirectoryName.filepath;
					//RadioButton cb = vh.mRadio;	 
									 
						 if (  vh.mRadio.isChecked())						 			
							 selectedDirectory = vh.DirectoryName.filepath;
						 this.notifyDataSetChanged();
						  								
				}//ChooseDirectory	
				
				
				public boolean GoUp()
				{
					//if (mfiles.size() == 0)
					//	return false;
					
					//MusicFile mf = mfiles.get(0);
					
//					if (mf.title == "UP")
//					{
//						 File fc = new File(mf.filepath);
//						String root =   fc.getParent();
//						if (!root.equals(null))
//						{
//							 File froot = new File(root);
//							 mfiles.clear();
//							 getmusicfiles(root);
//							this.notifyDataSetChanged();
//						}
//						else
//						{
//							 return false;				
//						}
//						
//						return true;
//					}		
					
					
					if (_rootdir != "")
					{
						 File fc = new File(_rootdir);
						String root =   fc.getParent();
						if (root != null)
						{
							// File froot = new File(root);
							 mfiles.clear();
							 getDirectories(root);
							this.notifyDataSetChanged();
						}
						else
						{
							 return false;				
						}
						
						return true;
					}		
					
					return false;						
					
				}	
				
				
				public void setRootDirectory(String rootDirectory) {
					_rootdir = rootDirectory;
					if(_act.get() != null)
						_act.get().SetRootDirectoryTextView(_rootdir);		
				}
				 
				
				public boolean ChangeDirectory(View v)
				{	 
						 
					DirectoryViewHolder vh = (DirectoryViewHolder)  v.getTag();
					MusicFile mf = vh.DirectoryName;
										
					if (mf.title == "UP")
					{
						 File fc = new File(mf.filepath);
						String root =   fc.getParent();
						if ( root!= null )
						{
							 //File froot = new File(root);
							 mfiles.clear();
							 getDirectories(root);
							this.notifyDataSetChanged();
							
						}
						else
						{						
							 return false;				
						}
						
						return true;
					}
					else
					{
						mfiles.clear();
						getDirectories(mf.filepath);
						this.notifyDataSetChanged();	
						return true;						
					}						  
					
				}//ChangeDirectory				 

}
