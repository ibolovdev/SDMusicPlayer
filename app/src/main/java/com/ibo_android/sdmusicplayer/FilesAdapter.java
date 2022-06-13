package com.ibo_android.sdmusicplayer;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import java.util.ArrayList;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 
import android.widget.BaseAdapter;
import android.widget.ImageView;
 
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.os.Parcelable;
 
import android.preference.PreferenceManager;

import java.io.*;
import java.lang.ref.WeakReference;

import com.ibo_android.sdmusicplayer.R;

import android.media.AudioManager;
 

public class FilesAdapter extends BaseAdapter
	//implements  OnCompletionListener,OnPreparedListener , OnErrorListener 
{



	/*@Override
	public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate)
	{
		if (waveformView != null) {
			waveformView.setWaveform(waveform);
		}
	}


	@Override
	public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate)
	{

	}*/





	public MusicFile _nowplaying = null;
    SharedPreferences prefs ;
	 
	//public WeakReference<BitmapCache> _bc;
	public ArrayList<MusicFile> mfiles;
	private String _rootdir;
	private LayoutInflater minfl;
	
	WeakReference<MainActivity> _act ;
	private String _playlist;
	public ListView _fileslist;
	public static  final int PLAYING_SONG_NOTIFICATION_ID = 1;  
	NotificationManager mNotificationManager;
	public MusicPlayerService _MusicSrvBinder = null;
	public   MyDB _dba;
	public boolean bShowWaveFormInList = false;


			public class ViewHolder
			{
				MusicFile mfile;
				TextView mTitle;
				ImageView mImg;
				WaveformView mWform;
			}
				
		
			/* (non-Javadoc)
			 * @see java.lang.Object#finalize()
			 */
			//@Override
			/*protected void finalize() throws Throwable {
				// TODO Auto-generated method stub
				super.finalize();
				
				  _con.unregisterReceiver(mMessageReceiver);
				 // _con = null;
				  _act = null;
				
			}*/


			public FilesAdapter(MainActivity act, String rootdir,
								MediaPlayerProxy mpp, String playlist,MyDB dba,
								MusicPlayerService MusicSrvBinder )
			{
				_act = new WeakReference<MainActivity>(act);
				 
				//_bc = new WeakReference<BitmapCache>(bc);
				minfl = LayoutInflater.from(_act.get());
				mfiles = new ArrayList<MusicFile>();
				_rootdir=rootdir;
	
				prefs = PreferenceManager.getDefaultSharedPreferences(_act.get());
	
				_playlist = playlist;
				_dba = dba;
				if (playlist.equals(""))				 
					getmusicfilesFromDirectory();				 
				else					 
					getmusicfilesFromPlaylist(_playlist);
				 
				  mNotificationManager = (NotificationManager) _act.get().getSystemService(Context.NOTIFICATION_SERVICE);
				
				//initMediaPlayer();
				
				_MusicSrvBinder = MusicSrvBinder;
				
				/*if (android.os.Build.VERSION.SDK_INT >= 8)
				{					 
					AudioManager mAudioManager = (AudioManager) _act.get().getSystemService(Context.AUDIO_SERVICE);					
					_afh = new AudioFocusHelper(mAudioManager, _mpp ,this);					
				}*/
				
				
			  /*  mMessageReceiver = new IntentReceiverFromPlayer(this);
			    
			    _act.get().registerReceiver(mMessageReceiver,
		        	      new IntentFilter("STOP_MUSIC"));*/
			    
			    
	 	         
			    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			    {
			    	   //with support library
					   // LocalBroadcastManager.getInstance(_con).registerReceiver(mMessageReceiver,
					        	   //   new IntentFilter("STOP_MUSIC"));	
			    	    
			         
			    }
			    else
			    {
			    	
			    }
			    
			 	         
			         
			}//FilesAdapter	
			
			
			
			
			public void getmusicfilesFromPlaylist(ArrayList<String> pls)
			{
				
				for (String pl: pls) 
				{					
					getmusicfilesFromPlaylist(pl);
				}		
				
			}//getmusicfilesFromPlaylist
			
			
			
			
			
			public void getmusicfilesFromPlaylist(String playlist)
			{
				//mfiles.clear();
				
				//ClearScreen();
				
				Cursor c = _dba.ShowAll(playlist);	
				
				//startManagingCursor(c);
				
				if(c.moveToFirst())
				{
					do
					{
									
						String code = c.getString(c.getColumnIndex(MyDBHelper.CODE_COL));
						
						String title = c.getString(c.getColumnIndex(MyDBHelper.TITLE_COL));
						
						String filepath = c.getString(c.getColumnIndex(MyDBHelper.FILEPATH_COL));
												
						//String wasplaing = c.getString(c.getColumnIndex(MyDBHelper.WASPLAYING_COL));
						
						Integer seqnum = c.getInt(c.getColumnIndex(MyDBHelper.SEQ_NUM_COL));
						
						File f = new File(filepath);
						 
						if (f.exists())
						{
							MusicFile mf = new  MusicFile(code,title,filepath,seqnum); 					
							mfiles.add(mf);								
						}
										 
						
					} while(c.moveToNext());
					
				}
				
				//this.notifyDataSetChanged();
				DataSetChanged();
				//this._nowplaying = null;
				
			}	//getmusicfilesFromPlaylist

	public void DataSetChanged()
	{
		this.notifyDataSetChanged();
		if (_MusicSrvBinder != null)
		{
			if (mfiles.size() < 1000)//it seems that there is a limit on the size that a intent can have
			{
				_MusicSrvBinder.mfiles = mfiles;
			}
			else
			{
				MyApplicationObject mApplication = (MyApplicationObject) this._act.get().getApplicationContext();
				mApplication.setSelectedFiles(null);
				mApplication.setSelectedFiles(mfiles);
				_MusicSrvBinder.GetFilesFromApplicationContext();
			}

		}

	}
			
			public void DeletePlaylists(ArrayList<String> pls)
			{			
				_dba.deletePlayLists(pls);
					
				//mfiles.clear();
				//this.notifyDataSetChanged();
			}//DeleteAll			
			
			public void ClearScreen()
			{		
				if(_MusicSrvBinder == null)
				{
					mfiles.clear();				 
					_nowplaying = null;
					return;
				}

				if(_MusicSrvBinder._mp == null)
				{
					mfiles.clear();
					_nowplaying = null;
					return;
				}
					
				if (_MusicSrvBinder._mp.isPlaying())
				{
					ArrayList<MusicFile>  al = new ArrayList<MusicFile>();
					
					al.add(_MusicSrvBinder._nowplaying);
							
				//	MusicFile[] list = new MusicFile[]{_MusicSrvBinder._nowplaying};	
							
				//	mfiles.retainAll(list);
					
					mfiles.retainAll(al);
				}
				else
				{
					mfiles.clear();
					_MusicSrvBinder._nowplaying = null;
					_nowplaying = null;
					
					if(this._act.get() != null)
					{
						this._act.get().ClearSeekBar();
					}					
					
				}
			
			
				//this.notifyDataSetChanged();
				DataSetChanged();
			}//ClearScreen			
			
			public void SavePlaylist(String playlist) throws Exception
			{						
				
				_dba.deletePlaylist(playlist);
				Integer seqnum = 1;
				for (MusicFile mf: mfiles) 
				{
					_dba.insertEntry(mf.title, mf.filepath, playlist, "", seqnum.toString());
					seqnum++;
				}				
				
			}	//SavePlaylist

			
			public void getmusicfilesFromDirectory()
			{		
				
				//mfiles.add(object);
				//this is for 1.7
				/*Iterable<Path> dirs =
					    FileSystems.getDefault().getRootDirectories();
					for (Path name: dirs) {
					    System.err.println(name);
					}*/

				if ( mfiles.size() == 0 && _rootdir != null && _rootdir != "")
				{
					
					File f = new File(_rootdir);
					 
					if (f.isDirectory())
					{			
						if (f.listFiles() != null)
						{
							for (File name: f.listFiles()) 
							{
								
								if (!name.isDirectory())
								{
									MusicFile mf = new  MusicFile("",name.getName(),name.getAbsolutePath(),0); 					
									mfiles.add(mf);
									
								}
								
							}//for (File name: f.listFiles()) 
						}//if (f.listFiles() != null)	
						
						
					}					
					
				}
				else
				{				
					
				}			
					
			}//getmusicfiles	
			
			
			public ArrayList<MusicFile> SearchFiles(String searchtext, boolean bFromSearchedPressed,
														boolean bFindExactMatch)
			{
				ArrayList<MusicFile> alMatches = new ArrayList<MusicFile>();
				
				for (Object omf: mfiles.toArray()) 
				{
					
					MusicFile mf = (MusicFile) omf;
					
					
					if (bFindExactMatch)
					{
						if  (mf.filepath.toLowerCase().contains(searchtext.toLowerCase()))
						{
							alMatches.add(mf);
							break;
						}
						
					}
					else
					{
						if  (mf.title.toLowerCase().contains(searchtext.toLowerCase()))
						{
							alMatches.add(mf);
						}
		
					}
										
				}//omf	
				
				
				if (alMatches.size() == 0)
				{					 
					if(bFromSearchedPressed)
						if(_act.get() != null)
						Toast.makeText(_act.get(), R.string.no_results, Toast.LENGTH_LONG).show(); 
				}
					
				if (alMatches.size() == 1)
				{
					if(bFromSearchedPressed)
					{
						MusicFile mf = (MusicFile) alMatches.get(0);
						int pos = mfiles.indexOf(mf); 
						
						SetSong(pos, 0, true);	
						return null;
					}
				}
						
				if (alMatches.size() > 1)
				{
					
					
				}
				
				if(bFromSearchedPressed)
				{
					
				}
				
				
				return alMatches;		
				
			}
			
		/*	public void onPrepared(MediaPlayer mp) {
				
				 if (_nowplaying.CurrentPosition != 0 )					 
					 mp.seekTo(_nowplaying.CurrentPosition);
				 
				  mp.start(); 
				 
				//  int idx =  this.mfiles.indexOf(_nowplaying);
				//  this._fileslist.setSelection(idx);
				  SendNotification();
				  this.notifyDataSetChanged();
				  if (_act.get() != null)
				  {
					  _act.get().SetPaused();
				  }
					
				 // Button btPlay = (Button) _act.findViewById(R.id.btPlay);
				 // btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.pause), null, null, null);
			}	*/
			
			//obsolete
			public void SendNotification()
			{				
				//	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
						SendNotificationNew();
				//	else
					//	SendNotificationOld();			
			}
			
			private void SendNotificationNew()
			{
				if (_act.get() == null)
				{
					return;
				}
					
				Notification.Builder mBuilder =
				        new Notification.Builder(_act.get())
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentTitle("SDMusicPlayer")
				        .setContentText(_nowplaying.title);
			 
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(_act.get(), MainActivity.class);
			
				//resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				//TaskStackBuilder stackBuilder = TaskStackBuilder.create(_con);
				// Adds the back stack for the Intent (but not the Intent itself)
				//stackBuilder.addParentStack(ResultActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
			//	stackBuilder.addNextIntent(resultIntent);
				
					
			/*	PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );*/
				
				
			/*	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );*/

				int IntentFlagIn = PendingIntent.FLAG_UPDATE_CURRENT;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
				{
					IntentFlagIn = PendingIntent.FLAG_UPDATE_CURRENT|FLAG_IMMUTABLE;
				}

				PendingIntent pin = PendingIntent.getActivity(_act.get(), 0, resultIntent,IntentFlagIn);
			 
				mBuilder.setContentIntent(pin);
			//	mBuilder.setDeleteIntent(intent)
			 
				
				//T DO - how should i use this deprecated
				//mBuilder.getNotification().flags = Notification.FLAG_NO_CLEAR;
				
				
				// mId allows you to update the notification later on.		 
				mNotificationManager.notify(PLAYING_SONG_NOTIFICATION_ID, mBuilder.build());			
				
			}
			
			
			//private void SendNotificationWithSupportLibrary()
			//{
			//	NotificationCompat.Builder mBuilder =
				//        new NotificationCompat.Builder(_con)
				//        .setSmallIcon(R.drawable.ic_launcher)
				//        .setContentTitle("SDMusicPlayer")
				//        .setContentText(_nowplaying.title);
			 
				// Creates an explicit intent for an Activity in your app
				//Intent resultIntent = new Intent(_con, MainActivity.class);
				 
				//resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				//TaskStackBuilder stackBuilder = TaskStackBuilder.create(_con);
				// Adds the back stack for the Intent (but not the Intent itself)
				//stackBuilder.addParentStack(ResultActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
			//	stackBuilder.addNextIntent(resultIntent);
				
				
				
				
			/*	PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );*/
				
				
			/*	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );*/
				
			//	PendingIntent pin = PendingIntent.getActivity(_con, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			 
			//	mBuilder.setContentIntent(pin);
			//	mBuilder.setDeleteIntent(intent)
			//	mBuilder.getNotification().flags = Notification.FLAG_NO_CLEAR;
				
			 
				// mId allows you to update the notification later on.		 
			//	mNotificationManager.notify(PLAYING_SONG_NOTIFICATION_ID, mBuilder.getNotification());			
				
			//}
			
			
			//DIFFERENT API APK
			@SuppressWarnings("deprecation")
			/*private void SendNotificationOld()
			{	
				if (_act.get() == null)
				{
					return;
				}
						
				Notification not = new Notification (R.drawable.ic_launcher,"SDMusicPlayer",
						System.currentTimeMillis());
				
				Intent resultIntent = new Intent(_act.get(), MainActivity.class);			 
				PendingIntent pin = PendingIntent.getActivity(_act.get(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				not.flags= Notification.FLAG_NO_CLEAR;
				
				not.setLatestEventInfo(_act.get(), "SDMusicPlayer", _nowplaying.title, pin);
				
				
				mNotificationManager.notify(PLAYING_SONG_NOTIFICATION_ID, not);			 
				 	
				
			}*///SendNotificationOld
			
			public void DeleteNotification()
			{					
				if (_act.get() == null)
				{
					return;
				}
				
				NotificationManager mNotificationManager =
					    (NotificationManager) _act.get().getSystemService(Context.NOTIFICATION_SERVICE);
					
					mNotificationManager.cancel(PLAYING_SONG_NOTIFICATION_ID);				
				
			}//DeleteNotification
			
			
			public void SetPlayButtonFromAudioFocusHelper()
			{	
				 if (_act.get() != null)
				 {
					 _act.get().SetPlayButtonFromAudioFocusHelper();
				 }
				 
				
			//	Button btPlay = (Button) _act.findViewById(R.id.btPlay);
			//	btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				
			}//SetPlayButtonFromAudioFocusHelper		
			
			public void SetPauseButtonFromAudioFocusHelper()
			{	
				 if (_act.get() != null)
				 {
					 _act.get().SetPauseButtonFromAudioFocusHelper();
				 }
				 
				
			//	Button btPlay = (Button) _act.findViewById(R.id.btPlay);
			//	btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				
			}//SetPauseButtonFromAudioFocusHelper		
			
			
			
			/*public boolean onError(MediaPlayer mp, int arg1, int arg2) {
				// TODO Auto-generated method stub
				mp.reset();
				
			//	this.initMediaPlayer( );//causes the error to be thrown constantly
				//in combination with the code below
				
				this.notifyDataSetChanged();
				if (_act.get() != null)
				{
					_act.get().SetPlayed();	
				}
				
				// Button btPlay = (Button) _act.findViewById(R.id.btPlay);
				// btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				 DeleteNotification();	
				 return true;
				// return false;
			}*/
			
			ArrayList<MusicFile> alreadyPlayed = new ArrayList<MusicFile>();			
			private Object GetIndexOfNextSongToPlayInRandom()//implement  back button too?
			{
				if (_act.get() == null)
				{
					return null;
				}
				
				if (mfiles.size() == 0)
				{
					return null;
				}
				
				// SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_act.get());			        
				 boolean  bPlayRandom = prefs.getBoolean("play_in_random", false);
			   
			     if (bPlayRandom)
			     {
			    	 bPlayRandom = true;
			    	 
			    	 java.util.Random r = new java.util.Random();
			    	 int count = 0;
			    	 while (count < 20) {
			    		 count++;
			    		 int res =  r.nextInt(mfiles.size());
			    		 
			    		 if (alreadyPlayed.size() == mfiles.size())	
			    			 alreadyPlayed.clear();
			    			 
			    		 if (!alreadyPlayed.contains(mfiles.get(res)))		    		  
			    			 return res;	
			              
			          }	
			    	 	    	 
			     }		     
			     
				return null;
			}
			
		/*	public void onCompletion(MediaPlayer mp) {
			 				
				if (_act.get() == null)
				{
					return;
				}
				Object objRandomIndex = GetIndexOfNextSongToPlayInRandom();
				boolean bRandom = false;
				int NonRandomIdx =0;
				MusicFile nextToPlay=null;
				_nowplaying.CurrentPosition = 0;
				
				if (_nowplaying.bRepeat == 1)
				{
					nextToPlay = _nowplaying;
				}
				else
				{
					if (objRandomIndex == null)
					{
							int idx = mfiles.indexOf(_nowplaying);
							
							if (( idx + 1 ) == mfiles.size())
								idx= -1;
							
							nextToPlay = mfiles.get(idx+1);	
							NonRandomIdx = idx+1;
					}
					else
					{					
						Integer RandomIndex = (Integer) objRandomIndex;
						nextToPlay = mfiles.get(RandomIndex);
						bRandom = true;
					}	
					
				}//(_nowplaying.bRepeat == 1)
				
				
				 Uri uri = Uri.parse(nextToPlay.filepath);   
								 
				   _mpp.mp.reset();//setdatasource throws exception, due to illegal state
		    			 // apparently you cannot reset the datasource once you set it again
		    			 //so media player object works only for one song
		    			  try {
						_mpp.mp.setDataSource(_act.get(), uri);
						
						 HandleViewItem(_nowplaying,ViewAppearanceMode.NORMAL); 
						 _nowplaying = nextToPlay;						 
						  HandleViewItem(_nowplaying,ViewAppearanceMode.PLAYING); 
						  
						  if(bRandom )
						  {
						 // int idx =  this.mfiles.indexOf(_nowplaying);
						 // this._fileslist.setSelection(idx);
							  Integer RandomIndex = (Integer) objRandomIndex;
							  this._fileslist.setSelection(RandomIndex);
						  }
						  else
						  {							 
							  this._fileslist.setSelection(NonRandomIdx);							  
						  }
						  
						  _mpp.mp.prepareAsync();
						// _mp.prepare();
						// _mp.start();
						 
						// HandleViewItem(_nowplaying,ViewAppearanceMode.NORMAL); 
						 //_nowplaying = nextToPlay;						 
						 // HandleViewItem(_nowplaying,ViewAppearanceMode.PLAYING); 
						 
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				
			}*/			  
		
		/*	private void initMediaPlayer( )
			{
				
			//	if (  _mpp.mp != null)
					//return;				
				
				if (  _mpp.mp != null)
				{
					_mpp.mp.release();
					_mpp.mp = null;
				}				
				
				 _mpp.mp = new MediaPlayer();  
				 
				 //<uses-permission android:name="android.permission.WAKE_LOCK" />
				// _mpp.mp.setWakeMode(_con, PowerManager.PARTIAL_WAKE_LOCK);//needs permission, throws exception at runtime
				// _mpp.mp.setWakeMode(_con, PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime			 
				
				 if (_act.get() != null)
				   {
					 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_act.get());			        
					   String  ScreenBeh = prefs.getString( "settings_screen", "");
					   
				  
					   Window w = _act.get().getWindow(); // in Activity's onCreate() for instance
					   w.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
					   //_mpp.mp.(_con,  PowerManager.ACQUIRE_CAUSES_WAKEUP);//needs permission, throws exception at runtime
					//_mpp.mp.setWakeMode(_con,  PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime   
				 
				   				
					String[]  ScreenBehArr =    ScreenBeh.split(",");  
					if (ScreenBehArr != null)
							if (ScreenBehArr.length > 0)
								ScreenBeh = ScreenBehArr[0];
								
					 if (ScreenBeh.equals("2")  )				 				
						 w.setFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  
						         WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);					
									 
					 if (ScreenBeh == "1")				  
						_mpp.mp.setWakeMode(_act.get(),  PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime
								 
				  }
				// _mp.setScreenOnWhilePlaying(true);//does not need permission, does not work
				 				 
				 //	 WifiLock wifiLock = ((WifiManager) _con.getSystemService(Context.WIFI_SERVICE))
				//		   .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

					//	wifiLock.acquire();				 
					//	wifiLock.release();
						
				 _mpp.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);				 
				 _mpp.mp.setOnCompletionListener(this);				 
				 _mpp.mp.setOnPreparedListener(this);				 
				 _mpp.mp.setOnErrorListener(this);
				 
				 
			}*/		
				
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
			
				public static boolean cancelPotentialWork(String data, ImageView imageView)
				{
				    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

				    if (bitmapWorkerTask != null) {
				        final String bitmapData = bitmapWorkerTask.data;
				        // If bitmapData is not yet set or it differs from the new data
				        if (bitmapData == "" || !bitmapData.equals(data)) {
				            // Cancel previous task
				            bitmapWorkerTask.cancel(true);
				        } else {
				            // The same work is already in progress
				            return false;
				        }
				    }
				    // No task associated with the ImageView, or an existing task was cancelled
				    return true;
				}//cancelPotentialWork
				
				private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView)
				{
					   if (imageView != null) {
					       final Drawable drawable = imageView.getDrawable();
					       if (drawable instanceof AsyncDrawable) {
					           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
					           return asyncDrawable.getBitmapWorkerTask();
					       }
					    }
					    return null;
				}//BitmapWorkerTask
				
			
				public BitmapCache bc = new BitmapCache();
				 
				public void loadBitmap(MusicFile mf, ImageView imageView)
				{				
					if(imageView == null)
						return;
						
					Bitmap bitmap = null;
					//if(_bc.get() != null)//this causes bc.get -> null in the asynctask
					//	 bitmap = _bc.get().getBitmapFromMemCache(mf.filepath);
					 bitmap = bc.getBitmapFromMemCache(mf.filepath);
					
				    if (bitmap != null) 
				    {
				    	imageView.setImageBitmap(bitmap);
				    }
				    else 
				    {
				    	BitmapWorkerTask task = new BitmapWorkerTask(imageView, bc );
						   
					    if (cancelPotentialWork(mf.filepath, imageView)) 
					    {				    	
					    	   Bitmap mPlaceHolderBitmap=null;
							final AsyncDrawable asyncDrawable =
						                new AsyncDrawable(_act.get().getResources(), mPlaceHolderBitmap, task);
						        imageView.setImageDrawable(asyncDrawable);
							    
							    task.execute(mf.filepath);					    	
					    }//(cancelPotentialWork(mf.filepath, imageView)) 	    	
				    	
				    }					
					
				}//loadBitmap

	private Visualizer visualiser;
	//private WaveformView waveformView;
	private static final int CAPTURE_SIZE = 128;



	public void restartVisualizer()
	{
		if(_MusicSrvBinder  != null)
		{
			if ( _MusicSrvBinder._mp != null && _MusicSrvBinder._mp.isPlaying())
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
					startVisualiserThis(_MusicSrvBinder._mp.getAudioSessionId(), null);
				}
			}
			else
			{
				pauseVisualiserThis(false);
			}

		}//if(_MusicSrvBinder  != null)

	}//public void restartVisualizer()

	public boolean ShouldShowVisualizer()
	{
		boolean ShowVisualizer = prefs.getBoolean("ShowVisualizer", true);
		if(!ShowVisualizer)
		{
			return false;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			return false;
		}

		boolean bRecordAudioNotGranted = ContextCompat.checkSelfPermission(_act.get(),
				Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED;

		if (bRecordAudioNotGranted)
		{
			return false;
		}

		return true;
	}

	public void startVisualiserThis(int AudioSessID, ViewHolder vh)
	{
		/*boolean ShowVisualizer = prefs.getBoolean("ShowVisualizer", true);
		if(!ShowVisualizer)
		{
			return;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			return;
		}

		boolean bRecordAudioNotGranted = ContextCompat.checkSelfPermission(_act.get(),
				Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED;*/

		if (!ShouldShowVisualizer())
			return;


		if (!bShowWaveFormInList)
		{
			startVisualiserInMainScreen(AudioSessID,vh);
		}
		else
		{
			startVisualiserInListItem(AudioSessID,vh);
		}

	}

	public void startVisualiserInMainScreen(int AudioSessID, ViewHolder vh)
	{

		if(_act.get() == null)
		{
			return;
		}

		if(_act.get().waveformView == null)
		{
			return;
		}

		WebformRendererFactory rendererFactory = new WebformRendererFactory();
		_act.get().waveformView.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.RED));
		_act.get().waveformView.invalidate();
			//vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.RED, Color.WHITE));

//		pauseVisualiserThis(false);


		try
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				visualiser = new Visualizer(AudioSessID);
				visualiser.setEnabled(false);
				visualiser.setDataCaptureListener(new MyOnDataCaptureListener(_act.get().waveformView), Visualizer.getMaxCaptureRate(), true, false);
				visualiser.setCaptureSize(CAPTURE_SIZE);
				visualiser.setEnabled(true);
			}

		}
		catch (UnsupportedOperationException e)//throw (new UnsupportedOperationException("Effect library not loaded"));
		{
			_act.get().waveformView.setRenderer(null);
			_act.get().waveformView.invalidate();
			if (visualiser != null)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

					visualiser.setEnabled(false);
					visualiser.release();
					visualiser.setDataCaptureListener(null, 0, false, false);

				}
			}

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("ShowVisualizer",false) ;

			editor.commit();
		}
		catch (Exception e)
		{
			throw e;
		}

	}


	public void startVisualiserInListItem(int AudioSessID, ViewHolder vh)
	{

		//ViewHolder vh = null;
		MusicFile mfile = null;

		if (vh == null)
		{
			if (_nowplaying == null)
			{
				if (mfiles.size() > 0)
				{
					mfile = mfiles.get(0);
					vh = this.GetViewItem(mfile);
				}

			}
			else
			{
				mfile = _nowplaying;
				vh = this.GetViewItem(this._nowplaying);

				if (vh.mfile != _nowplaying)
					return;
			}
		}//if (vh == null)


		if (vh == null)
			return;

		if (vh.mWform == null )
				return;


		// vh.mTitle.setBackgroundColor(Color.RED);
		  vh.mTitle.setTextColor(Color.WHITE);
		//vh.mTitle.setTextColor(Color.RED);

		if (vh.mWform != null)
		{
			WebformRendererFactory rendererFactory = new WebformRendererFactory();
			vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.RED));
			vh.mWform.invalidate();
			//vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.RED, Color.WHITE));
		}

		pauseVisualiserThis(false);


		try
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				visualiser = new Visualizer(AudioSessID);
				visualiser.setEnabled(false);
				visualiser.setDataCaptureListener(new MyOnDataCaptureListener(vh.mWform), Visualizer.getMaxCaptureRate(), true, false);
				visualiser.setCaptureSize(CAPTURE_SIZE);
				visualiser.setEnabled(true);
			}

		}
		catch (UnsupportedOperationException e)
		{
			_act.get().waveformView.setRenderer(null);
			_act.get().waveformView.invalidate();
			if (visualiser != null)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

					visualiser.setEnabled(false);
					visualiser.release();
					visualiser.setDataCaptureListener(null, 0, false, false);

				}
			}

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("ShowVisualizer",false) ;

			editor.commit();
		}
		catch (Exception e)
		{
			throw e;
		}

	}




	public void pauseVisualiserThis(boolean bHandleRenderer)
	{

		if (!ShouldShowVisualizer())
			return;

		if (bHandleRenderer)
		{
			if(_act.get() != null)
			{
				WebformRendererFactory rendererFactory = new WebformRendererFactory();
				_act.get().waveformView.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.BLUE));
				_act.get().waveformView.invalidate();
			}

		}


		try
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				visualiser.setEnabled(false);
				visualiser.release();
				visualiser.setDataCaptureListener(null, 0, false, false);
			}

		}
		catch (Exception e)
		{
			/*_act.get().waveformView.setRenderer(null);
			_act.get().waveformView.invalidate();

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("ShowVisualizer",false) ;

			editor.commit();*/
		}
		finally
		{

			/*if (visualiser != null)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

					visualiser.setEnabled(false);
					visualiser.release();
					visualiser.setDataCaptureListener(null, 0, false, false);

				}
			}*/

		}


	}//pauseVisualiserThis
				
				public View getView(int pos, View v, ViewGroup vg) {
						ViewHolder holder=null;
					
						//Bitmap bm = this.GetMp3Picture(mfiles.get(pos));
						//Bitmap bm = null;
						
						//ShowMp3Images
						 boolean  ShowImgs = false;
						 
						 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						 {
								ShowImgs = prefs.getBoolean("ShowMp3Images", true);
						 }

					boolean ShowVisualizer = prefs.getBoolean("ShowVisualizer", true);
							 
						// boolean  ShowImgs = false;	
						
						if (v==null || v.getTag()== null)
						{	
							 if (ShowImgs) 
							 {

								 if (bShowWaveFormInList)
								 {

									 v = minfl.inflate(R.layout.activity_main_listitem_waveform, null);
									 holder = new ViewHolder();
									 holder.mTitle = (TextView)v.findViewById(R.id.txtMusicFile_wf);

									 //holder.mImg = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview);
									 //SetMp3Picture(holder.mImg, bm);

									 ImageView iv = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview_wf);
									 // iv.setVisibility(android.view.View.VISIBLE);
									 //SetMp3Picture(iv, bm);
									 holder.mImg = iv;

									 WaveformView wv = (WaveformView)v.findViewById(R.id.wfv_li);
									 // iv.setVisibility(android.view.View.VISIBLE);
									 //SetMp3Picture(iv, bm);

									 holder.mWform = wv;

								 }
								else
								 {

									 v = minfl.inflate(R.layout.activity_main_list_item, null);
									holder = new ViewHolder();
									holder.mTitle = (TextView)v.findViewById(R.id.txtMusicFile);

									//holder.mImg = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview);
									//SetMp3Picture(holder.mImg, bm);

									ImageView iv = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview);
									// iv.setVisibility(android.view.View.VISIBLE);
									//SetMp3Picture(iv, bm);
									holder.mImg = iv;

									 //loadBitmap(mfiles.get(pos),iv);

								 }

								 
							 }
							 else
							 {								 
								    v = minfl.inflate(R.layout.activity_main_listitem_noimg, null);
									holder = new ViewHolder();
									holder.mTitle = (TextView)v.findViewById(R.id.mainactivity_listitem_title);
									holder.mImg = null;
									holder.mWform = null;
													
							 }						
							
							
							/*if( bm == null )
							{
								v = minfl.inflate(R.layout.mainactivity_listitem_noimg, null);
								holder = new ViewHolder();
								holder.mTitle = (TextView)v.findViewById(R.id.mainactivity_listitem_title);
								holder.mImg = null;
								
							//	v = minfl.inflate(R.layout.mainactivity_list_item, null);
							//	holder = new ViewHolder();
							//	holder.mTitle = (TextView)v.findViewById(R.id.txtMusicFile);		
								
							//	ImageView iv = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview);
								// iv.setVisibility(android.view.View.INVISIBLE);
							//	holder.mImg = iv;
								
								 
							}
							else
							{
								v = minfl.inflate(R.layout.mainactivity_list_item, null);
								holder = new ViewHolder();
								holder.mTitle = (TextView)v.findViewById(R.id.txtMusicFile);
								
								//holder.mImg = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview);
								//SetMp3Picture(holder.mImg, bm);
								
								ImageView iv = (ImageView)v.findViewById(R.id.main_activity_listitem_imgview);
								// iv.setVisibility(android.view.View.VISIBLE);
								SetMp3Picture(iv, bm);
								holder.mImg = iv;
							}*/							
							
							v.setTag(holder);
													}
						else
						{			
							holder = (ViewHolder) v.getTag();				
						}
												
						holder.mfile = mfiles.get(pos);
						holder.mTitle.setText( holder.mfile.title);
						
						 if (ShowImgs) 
						 {
							 loadBitmap(mfiles.get(pos),holder.mImg);
						 }
						 
						 
						// v.setOnLongClickListener(new MyOnLongClickListener(holder.mfile) {});						
							// MyDragEventListener mDragListen = new MyDragEventListener();						
							 //v.setOnDragListener(mDragListen);
						 
						
						// SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_act.get());			        
						 int  TextSize = prefs.getInt("text_size", 12);
						 holder.mTitle.setTextSize(TextSize);
						// holder.mTitle.setLines(10);
						  
						//'text_size
						 
						v.setTag(holder);
						
					if(!(_nowplaying==null) && _MusicSrvBinder != null && _MusicSrvBinder._mp != null )//_mpp.mp..isPlaying() was throwing an exception, when another app(egMusicFolder started to  play)
					{
						 if (_nowplaying.equals(holder.mfile))
						 {
							 if( _MusicSrvBinder._mp.isPlaying())
							 {
								 HandleViewItem(holder,ViewAppearanceMode.PLAYING );							 
							 }
							 else
							 {
								 if (_MusicSrvBinder._mp.getCurrentPosition() > 0)
								 {
									 HandleViewItem(holder,ViewAppearanceMode.PAUSED);								 
								 }
								 else
								 {
									 HandleViewItem(holder,ViewAppearanceMode.STOPPED);								  
								 }							 
							 }						 
							
						 }
						 else
						 {
							 HandleViewItem(holder,ViewAppearanceMode.NORMAL); 
						 }//(_nowplaying.equals(holder.mfile))
					}
					else
					{
						 HandleViewItem(holder,ViewAppearanceMode.NORMAL); 
					}
					
					
					if (_nowplaying != null)
					{
						if (_nowplaying.equals(holder.mfile))
						{
							if (_nowplaying.bInError == 1)
							 HandleViewItem(holder,ViewAppearanceMode.ERROR);	
						}	
					}				
						
						 /* v.setOnClickListener(new OnClickListener() {
					    	  public void  onClick(View v)
					    	  {
					    		  PlayStop(v);
					    		  
					    	  }
					       }
					       );*/			
						
						 
						return v;
				}//getView
				
			
				
				public int GetMusicFileIndex(MusicFile item)
				{					
					 int idx = mfiles.indexOf(item);
					 if(idx != -1)
						 return idx;	
					 
					 	ArrayList<MusicFile>  mfs = SearchFiles(item.title, false, true);
						if (mfs.size() == 1)
						{						
							 idx = mfiles.indexOf(mfs.get(0));
							 
						}
					 
					 return idx;
					 
				}
				
				
				public void HandleViewItem(MusicFile item, ViewAppearanceMode mode)
				{
					 int idx = mfiles.indexOf(item);

					 if (_fileslist == null)
                     {
                         return;
                     }

 					 if (!(  _fileslist.getChildAt(idx) == null))
 					 {
 						  View vprev = (View)   _fileslist.getChildAt(idx);	    					  
	    				  ViewHolder vh_prev = (ViewHolder)  vprev.getTag();	
	    				  HandleViewItem(vh_prev, mode);
 					 }
 					   
				} 
				
				
				public void HandleViewItem(int item_pos, ViewAppearanceMode mode)
				{
					 	
					if (!(_fileslist.getChildAt(item_pos) == null))
					{					 
						View vprev = (View)   _fileslist.getChildAt(item_pos);						
		    			ViewHolder vh_prev = (ViewHolder)  vprev.getTag();	
		    			HandleViewItem(vh_prev, mode);						
					}
					   
				}
				
				public ViewHolder GetViewItem(MusicFile item)
				{
					 
					if (item==null)
						return null;
					
					int idx = mfiles.indexOf(item);
					  
 					 if ( !( _fileslist.getItemAtPosition(idx) == null))
 					 {
 						 View vprev = (View)   _fileslist.getChildAt(idx);	    					  
	    				 ViewHolder vh_prev = (ViewHolder)  vprev.getTag();
	    				 return vh_prev;
	    				 
 					 }
 					 return null;
 					   
				}
				
				
				//fa.PlayStop(v,int pos,long id);
				public void PlayStop(View v,int viewpos,long id)
				{					
					 ViewHolder vh = (ViewHolder)  v.getTag();
					 PlayStop(vh, vh.mfile,false );
					 //this._fileslist.getPositionForView(view)
				}
				
				
				public void PlayStop(ViewHolder vh,  MusicFile mf, boolean bRefresh )
				{
					//add a parameter to notify from the play button				
					 try
		    		  {
						 
						/* if (vh == null)
						 {
							 return;							 
						 }*/
						 if(_act.get() == null)
						 {
							 return;
						 }
						 if (mf == null )
							 return;
						 
						 if ( _MusicSrvBinder != null)
							 if (!_MusicSrvBinder._afh.requestAudioFocus())						  
								 return;						 
						 
		    			//  ViewHolder vh = (ViewHolder)  v.getTag();
		    			  //MusicFile mf = vh.mfile;
		    			  
		    			//	Toast.makeText(_con, mf.filepath, Toast.LENGTH_LONG).show();
		    				
		    			  Uri uri = Uri.parse(mf.filepath);    
		    				
		    			  if (mf == _nowplaying  &&  _nowplaying!= null)
		    			  {
		    				 
		    				  try
		    				  {		    					  
		    					  if (_MusicSrvBinder._mp == null)
								  {
		    						  _MusicSrvBinder.initMediaPlayer();								       	 
		    						  _MusicSrvBinder._mp.setDataSource(_act.get(), uri);
								         
								        // HandleViewItem(vh, ViewAppearanceMode.PLAYING);	
		    						  
		    						  _nowplaying = mf;	    					  
			    					  _MusicSrvBinder._nowplaying = mf;
								         
		    						  _MusicSrvBinder._mp.prepareAsync();
								         
								         // _mp.prepare();				 		    			  
				 		    			 // _mp.seekTo(_nowplaying.CurrentPosition);
				    					//  _mp.start(); 				    					  
				    					 // HandleViewItem(vh, ViewAppearanceMode.PLAYING);									       	 
								  }
		    					  else
		    					  {		    						  
		    						  if (_MusicSrvBinder._mp.isPlaying())
					    			  {				    				  
		    							  _MusicSrvBinder._mp.pause();
		    							  _MusicSrvBinder._nowplaying.CurrentPosition = _MusicSrvBinder._mp.getCurrentPosition();
		    							  _MusicSrvBinder.stopForeground(false);
					    				  if (_act.get() != null)
					    				  {
					    					  _act.get().SetPlayed();
					    					  pauseVisualiserThis(true);
					    				  }
					    				  
					    				//  Button btPlay = (Button) _act.findViewById(R.id.btPlay);
					    				//  btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
					    				  
					    				 _MusicSrvBinder.SendNotification();
					    				  // DeleteNotification();					    				  
					    				  HandleViewItem(vh, ViewAppearanceMode.PAUSED);
					    				  if (bRefresh)
					    				  {
					    					  //this.notifyDataSetChanged();
					    					  DataSetChanged();
					    				  }
					    		      }
			    					  else
			    					  {
			    						  
			    						  _MusicSrvBinder.initMediaPlayer();								       	 
			    						  _MusicSrvBinder._mp.setDataSource(_act.get(), uri);
									         
									        // HandleViewItem(vh, ViewAppearanceMode.PLAYING);	
			    						  
			    						 // _nowplaying = mf;	    					  
				    					 // _MusicSrvBinder._nowplaying = mf;
									         
			    						  _MusicSrvBinder._mp.prepareAsync();
			    						  
			    						  
			    						  //_MusicSrvBinder._mp.start();
			    						 // _MusicSrvBinder.startForeground();
			    						  //SendNotification();
			    						 // HandleViewItem(vh, ViewAppearanceMode.PLAYING);
			    							//if (_act.get() != null)
			    							//{
			    							//	_act.get().SetPaused();	
			    							//}
			    										    						  
			    						//  Button btPlay = (Button) _act.findViewById(R.id.btPlay);
			    						//  btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.pause), null, null, null);
			    						  if (bRefresh)
					    				  {
					    					  this.notifyDataSetChanged();
					    				  }
			    					  }	    						  
		    						  
		    					  }	    					  
		    					 			
		    				  }
		    				  catch(Exception e)
		    				  {
								  if (_MusicSrvBinder != null)
								  		_MusicSrvBinder.initMediaPlayer();

		    					  e.printStackTrace(); 
		    					  if(_act.get() != null)
		    					  {
		    						  Toast.makeText(_act.get(), e.getMessage(), Toast.LENGTH_LONG).show();
									  pauseVisualiserThis(true);
		    					  }	
		    					  
		    					 
		    					  mf.bInError = 1;
		    					  _nowplaying = mf;

								  if (_MusicSrvBinder != null)
		    					  		_MusicSrvBinder._nowplaying = mf;

		    					  HandleViewItem(vh, ViewAppearanceMode.ERROR);
		    					  DataSetChanged(); 
		    				  }
		    				  finally
		    				  {
								//  _MusicSrvBinder.initMediaPlayer();
		    				  }
		    				  
		    			  }
		    			  else
		    			  {
		    				  try
		    				  {
		    					  
		    					if (_MusicSrvBinder._mp == null)
								{									 	
		    						_MusicSrvBinder.initMediaPlayer();
								}
		    					  
		    					_MusicSrvBinder._mp.reset();//setdatasource throws exception, due to illegal state
		 		    			 // apparently you cannot reset the datasource once you set it again
		 		    			 //so media player object works only for one song
		    					_MusicSrvBinder._mp.setDataSource(_act.get(), uri);
		 		    			  
		 		    			  
		 		    			//  MusicFile previousplaying = _nowplaying;
		 		    			  if (!(_nowplaying == null))
		 		    			  {
		 		    				_nowplaying.bRepeat = 0;
		 		    				_nowplaying.CurrentPosition = 0;
		 		    			  }
		 		    				  
		    					  _nowplaying = mf;	    					  
		    					  _MusicSrvBinder._nowplaying = mf;
		    					 
		    				     // HandleViewItem(previousplaying,ViewAppearanceMode.NORMAL); 		    					    					 
		    					 
		    					  _MusicSrvBinder._mp.prepareAsync();
		 		    			 // _mpp.mp.prepare();
		    					 // _mpp.mp.start();  
		    					  
		    					 // MusicFile previousplaying = _nowplaying;
		    					 // _nowplaying = mf;	    					  
		    					 
		    					//  HandleViewItem(previousplaying,ViewAppearanceMode.NORMAL); 
		    					  
		    					/*  int idx = mfiles.indexOf(previousplaying);
		    					  
		    					 if (  _fileslist.getItemAtPosition(idx) != null)
		    					 {
		    						 View vprev = (View)   _fileslist.getChildAt(idx);
			    					  
			    					//  View vprev = this.getView(idx, null, (ViewGroup) vh.mTitle.findViewById(R.id.lvSongsList));
			    					  ViewHolder vh_prev = (ViewHolder)  vprev.getTag();	
			    					  HandleViewItem(vh_prev, ViewAppearanceMode.NORMAL);
		    					 }*/		    					   
		    										  
		    					 // HandleViewItem(vh, ViewAppearanceMode.PLAYING);
		    					 		  
		    				  }
		    				  catch(Exception e)
		    				  {
								  if (_MusicSrvBinder != null)
								  		_MusicSrvBinder.initMediaPlayer();

		    					  //e.printStackTrace();
		    					  if(_act.get() != null)
		    					  {
		    						  Toast.makeText(_act.get(), e.getMessage(), Toast.LENGTH_LONG).show();
									  pauseVisualiserThis(true);
		    					  }
		    					  
		    					  if (!(_nowplaying == null))
		 		    			  {
		 		    				_nowplaying.bRepeat = 0;
		 		    				_nowplaying.CurrentPosition = 0;
		 		    				
		 		    			  }
		 		    				
		    					  mf.bInError = 1;
		    					  _nowplaying = mf;

								  if (_MusicSrvBinder != null)
		    					  		_MusicSrvBinder._nowplaying = mf;

		    					  HandleViewItem(vh, ViewAppearanceMode.ERROR);	
		    					  DataSetChanged();
		    					  
		    				  }
		    				  finally
		    				  {
		    					// this.notifyDataSetChanged(); //for updating the previous item appearance
		    					 //i could not any other way

		    				  }		    				 	
		    				  
		    			  }		    			   
		    			  
		    			  
		    			  
		    			  if ( !this.alreadyPlayed.contains(mf))
		    			  {
		    				  this.alreadyPlayed.add(mf);
		    			  }
		    			 
		    			//  TrackInfo[] ti = _mp.getTrackInfo();	    			  	 
		    			  
		    		  }
		    		  catch(Exception e)
		    		  {

		    		  	if (_MusicSrvBinder != null)
		    		  		_MusicSrvBinder.initMediaPlayer();

		    		  	e.printStackTrace();
		    			if(_act.get() != null)
		    			{
		    				 Toast.makeText(_act.get(),   e.toString() + ":" +  e.getMessage(), Toast.LENGTH_LONG).show();
			    			  //Toast.makeText(_con, , Toast.LENGTH_LONG).show();
							pauseVisualiserThis(true);
		    			}
		    			  
		    			 
		    		  }
					 finally
					 {
						 // this.notifyDataSetChanged(); //for updating the previous item appearance
						 //i could not any other way
						// _MusicSrvBinder.initMediaPlayer();
					 }

				}//PlayStop			
				
				
				public void GoForward()
				{

					if (_nowplaying == null)
							return;

					if (_MusicSrvBinder == null)
						return;

					if (_MusicSrvBinder._mp == null)
						return;

						int  bTimeToMove = prefs.getInt("SEEK_DURATION", 10);
						int cpos  = _MusicSrvBinder._mp.getCurrentPosition(); 
						_MusicSrvBinder._mp.seekTo(cpos + bTimeToMove*1000);
						_nowplaying.CurrentPosition = cpos + bTimeToMove*1000;

				}//GoForward
				
				public void GoBackward()
				{
					if (_nowplaying == null)
						return;

					if (_MusicSrvBinder == null)
						return;

					if (_MusicSrvBinder._mp == null)
						return;


						int  bTimeToMove = prefs.getInt("SEEK_DURATION", 10);
						int cpos  = _MusicSrvBinder._mp.getCurrentPosition(); 
						_MusicSrvBinder._mp.seekTo(cpos - bTimeToMove*1000);	
						_nowplaying.CurrentPosition = cpos - bTimeToMove*1000;

				}//GoBackward
				
				public void GoToTheStart()
				{
					if (!(_nowplaying == null))
					{ 
						_MusicSrvBinder._mp.seekTo(0);						 							
					}						
				}
						
				
				public void PlayStop( )
				{
					//ViewHolder vh=null;
					MusicFile mfile = null;
					if (_nowplaying == null)
					{
						if (mfiles.size() > 0)
						{
							mfile = mfiles.get(0);
							//vh = this.GetViewItem(mfile);		
						}						 
						
					}
					else
					{
						mfile = _nowplaying;
						//vh = this.GetViewItem(this._nowplaying);							
					}					 
				 
					this.PlayStop(null, mfile,true);	    		 
					
				}//PlayStop		
				
				
				public void PlayNext()
				{
					 
					Object objRandomIndex = GetIndexOfNextSongToPlayInRandom();
					
					MusicFile mfile = null;
					
					if (!(objRandomIndex == null))
					{
						Integer RandomIndex = (Integer) objRandomIndex;
						mfile = mfiles.get(RandomIndex);	
						this.PlayStop(null, mfile,true);

						if (this._fileslist != null )
						{
							this._fileslist.setSelection(RandomIndex);
							//this._fileslist.smoothScrollToPosition(RandomIndex);
						}

						return;
					}
					
					
					if (_nowplaying == null)
					{
						if (mfiles.size() > 0)
						{
							mfile = mfiles.get(0);						 
						}						 
						
					}
					else
					{
						int currentpos = mfiles.indexOf(_nowplaying);
						if (mfiles.size() == currentpos + 1)
						{		
							if (mfiles.size() > 0)
							{
								mfile = mfiles.get(0);						 
							}	
							
						}
						else
						{
							mfile = mfiles.get(currentpos + 1);								
						}						 
						 						
					}					 
				 
					this.PlayStop(null, mfile,true);
					// int idx =  this.mfiles.indexOf(mfile);
					 // this._fileslist.setSelection(idx);
				}
				
				public void ChangeDatasource(ArrayList<Parcelable>  selectedsongs)
				{
				 		
		    		mfiles.clear();
		    		//ClearScreen();
		    		
					for (Object omf: selectedsongs.toArray()) 
					{
						mfiles.add((MusicFile) omf);
					}	
										
					//notifyDataSetChanged();
					DataSetChanged();
				}
				
				public void PlayPrevious()
				{
					MusicFile mfile = null;
					if (_nowplaying == null)
					{
						if (mfiles.size() > 0)
						{
							mfile = mfiles.get(0);						 
						}						 
						
					}
					else
					{
						int currentpos = mfiles.indexOf(_nowplaying);
						
						if ((currentpos - 1) < 0 )
						{
							mfile = mfiles.get(mfiles.size()  - 1);
						}
						else
						{
							mfile = mfiles.get(currentpos - 1);
						}	 
					 					
					}					 
				 
					this.PlayStop(null, mfile,true);
				}
				
				
				public void DeleteFile(int pos)
				{
					
					//<uses-permission android:name="android.permission.READ_PHONE_STATE"/> // no need
				   // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
					MusicFile mfile = null;
					mfile = mfiles.get(pos);
									
					File f = new File(mfile.filepath);
					 
					if (f.delete())
					{
						mfiles.remove(mfile);						
						//this.notifyDataSetChanged();
						DataSetChanged();
					}			
					
				}
				
				public void RemoveFromList(int pos)
				{
					MusicFile mfile = null;
					mfile = mfiles.get(pos);
					
					mfiles.remove(mfile);					
					//this.notifyDataSetChanged();
					DataSetChanged();
				}
				
				public  int calculateInSampleSize(
			            BitmapFactory.Options options, int reqWidth, int reqHeight) {
			    // Raw height and width of image
			    final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;

			    if (height > reqHeight || width > reqWidth) {

			        final int halfHeight = height / 2;
			        final int halfWidth = width / 2;

			        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
			        // height and width larger than the requested height and width.
			        while ((halfHeight / inSampleSize) > reqHeight
			                && (halfWidth / inSampleSize) > reqWidth) {
			            inSampleSize *= 2;
			        }
			    }

			    return inSampleSize;
			}
				
				 
			 
				
				//@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
				public void GetInfo(int pos)
				{
					if(_act.get() == null)
					{
						return;
					}
					
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) 
					  return;
					
					MusicFile mfile = null;
					mfile = mfiles.get(pos);
					
												
					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
									
					try
					{
						mmr.setDataSource(mfile.filepath); 
						
						
						StringBuilder sb = new StringBuilder();
						
												
						String title = "";
						title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
						if (title != null)
							sb.append("Title: " +  title + "\n");
					
						//sb.append("seqnum: " +  mfile.seqnum + "\n");
						
						String artist = "";
						artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
						if (artist != null)
						sb.append( "Artist: " +  artist + "\n");
					    				   
						String album = "";
						album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
						if (album != null)
						sb.append("Album: " + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) + "\n");
					    			    
						
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
						{
							String bitrate = "";
							bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
							if (bitrate != null)
							sb.append( "Bitrate: " + bitrate+ "\n");
						}						
						 
						sb.append( pos + "/" +  mfiles.size() + "\n");
						//sb.append( "Duration: " + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
					    
					   
						sb.append( mfile.filepath + "\n");
						
						
						String info = "";
					    info = sb.toString() ;
					    
						if (info != "")
							Toast.makeText(_act.get(), info, Toast.LENGTH_LONG).show();
						else
							Toast.makeText(_act.get(), R.string.noinfo, Toast.LENGTH_LONG).show();
						
											
					}
					catch(Exception e)
					{
						
					}	
					
					
				}
				
				public void  SetSelection(int index)
				{
					if (this._fileslist != null)
					{
						this._fileslist.setSelection(index);
					}
					
				}
				
				
	public void SetSong(int pos, int item_pos, Boolean item_playing)
				{
					if (_act.get() == null)
					{
						return;
					}
					  MusicFile mfile = mfiles.get(pos);
					  mfile.CurrentPosition = item_pos;
					  Uri uri = Uri.parse(mfile.filepath);
					  
					  _MusicSrvBinder._mp.reset();
					  
					  try {
						  _MusicSrvBinder._mp.setDataSource(_act.get(), uri);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		         
					  this._nowplaying = mfile;
					  _MusicSrvBinder._nowplaying = mfile;
					
					//  _mpp.mp.prepareAsync();
					  try {
						  _MusicSrvBinder._mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    			 
		    			 
					 if (item_playing)
					{					
						 _MusicSrvBinder._mp.seekTo(item_pos);
						 _MusicSrvBinder._mp.start(); 
						 _MusicSrvBinder.SendNotification();
	 					//SendNotification();
						HandleViewItem(pos,ViewAppearanceMode.PLAYING ); 						
					}
					else
					{
						if (item_pos != 0)
						{
							_MusicSrvBinder._mp.seekTo(item_pos);
							HandleViewItem(pos,ViewAppearanceMode.PAUSED ); 
						}
						else
						{
							HandleViewItem(pos,ViewAppearanceMode.STOPPED );							
						}				
						
					}	
					 
					 
					 this._fileslist.setSelection(pos);
					
				}//SetSong				
				
	public void SetSongInUI(int pos, int item_pos, Boolean item_playing)
	{
		if (_act.get() == null)
		{
			return;
		}
		
		if (mfiles.size() < pos + 1 )
			return;
		
		  MusicFile mfile = mfiles.get(pos);
		  mfile.CurrentPosition = item_pos;
		 // Uri uri = Uri.parse(mfile.filepath);
		  
		  /*_MusicSrvBinder._mp.reset();
		  
		  try {
			  _MusicSrvBinder._mp.setDataSource(_act.get(), uri);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/		         
		  this._nowplaying = mfile; 
		//  this._MusicSrvBinder._nowplaying = mfile;
		
		//  _mpp.mp.prepareAsync();
		/*  try {
			  _MusicSrvBinder._mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
			 
			 
		 if (item_playing)
		{					
			// _MusicSrvBinder._mp.seekTo(item_pos);
			// _MusicSrvBinder._mp.start(); 
			 if (_MusicSrvBinder != null)
				 _MusicSrvBinder.SendNotification();
				//SendNotification();
			HandleViewItem(pos,ViewAppearanceMode.PLAYING ); 						
		}
		else
		{
			if (item_pos != 0)
			{
				//_MusicSrvBinder._mp.seekTo(item_pos);
				HandleViewItem(pos,ViewAppearanceMode.PAUSED ); 
			}
			else
			{
				HandleViewItem(pos,ViewAppearanceMode.STOPPED );							
			}				
			
		}	
		 		 
		 this._fileslist.setSelection(pos);
		
	}//SetSongInUI	


	private void HandleViewItem1(ViewHolder vh, ViewAppearanceMode mode)
	{
		return;
	}



				private void HandleViewItem(ViewHolder vh, ViewAppearanceMode mode)
				{
					
					 // Button btPlay = (Button) _act.findViewById(R.id.btPlay);
					
					//if (vh == null)
					//	return;
					
					switch (mode)
					{
						case PLAYING:
					
							if (vh != null)
							{
								  //vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.red));
		    					  //vh.mTitle.setTextColor(_con.getResources().getColor(R.color.white));
		    					
		    						  vh.mTitle.setBackgroundColor(Color.RED);
			    					  vh.mTitle.setTextColor(Color.WHITE);
								//vh.mTitle.setTextColor(Color.RED);



		    					
		    					  //vh.mTitle.setBackgroundColor(Color.RED);
		    					  //vh.mTitle.setTextColor(Color.WHITE);
		    					  //vh.mTitle.setTextSize(25);
								//startVisualiserThis(this._MusicSrvBinder._mp.getAudioSessionId(),vh);
								//startVisualiserThis(this._MusicSrvBinder._mp.getAudioSessionId(),null);
							}
							  //vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.red));
	    					 // vh.mTitle.setTextColor(_con.getResources().getColor(R.color.white));
	    					 // btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.pause), null, null, null);
	    					//  btPlay.setText("pause");
	    					  //  btPlay.setBackground(_con.getResources().getDrawable(R.drawable.pause));
							//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.pause), null, null, null);
	    					  break;
							  
						case  PAUSED:
							
							if (vh != null)
							{
								 vh.mTitle.setBackgroundColor(Color.BLUE);
		    					 vh.mTitle.setTextColor(Color.WHITE);

								if (vh.mWform != null)
								{
									WebformRendererFactory rendererFactory = new WebformRendererFactory();
									vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.BLUE));
									vh.mWform.invalidate();

								}
							//	pauseVisualiserThis();
							}
							// vh.mTitle.setBackgroundColor(Color.BLUE);
	    					// vh.mTitle.setTextColor(Color.WHITE);
	    					 //  btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.play), null, null, null);
	    					//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
	    					   //  btPlay.setBackground(_con.getResources().getDrawable(R.drawable.play));
	    					//  btPlay.setText("play");
	    					 break;
	    				
						case ERROR:
							if (vh != null)
							{							 
								 vh.mTitle.setBackgroundColor(Color.GREEN);
		    					 vh.mTitle.setTextColor(Color.WHITE);



								//pauseVisualiserThis();
							}
							break;
	    					 
						case  STOPPED:
							
							if (vh != null)
							{
								
									vh.mTitle.setBackgroundColor(Color.BLUE);
			    					 vh.mTitle.setTextColor(Color.WHITE);


								//pauseVisualiserThis();
								
								 
								// vh.mTitle.setBackgroundColor(Color.GREEN);
		    					 //vh.mTitle.setTextColor(Color.WHITE);
							}
							// vh.mTitle.setBackgroundColor(Color.GREEN);
	    					// vh.mTitle.setTextColor(Color.WHITE);
							// btPlay.setText("play");
	    					// btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.play), null, null, null);
	    					//  btPlay.setBackground(_con.getResources().getDrawable(R.drawable.play));
							//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
	    					 break;
	    					 
						case  NORMAL:
							
							if (vh != null)
							{
								 //vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.white));
		    					  //vh.mTitle.setTextColor(_con.getResources().getColor(R.color.black));
								if ( MainActivity.GetThemeBehaviour(_act.get()) == "black" )
								{
									vh.mTitle.setBackgroundColor(Color.BLACK);
			    					vh.mTitle.setTextColor(Color.WHITE);



								}
								else
								{
									vh.mTitle.setBackgroundColor(Color.WHITE);
			    					vh.mTitle.setTextColor(Color.BLACK);


								}

							//	pauseVisualiserThis();
		    					  
							}
							 // vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.white));
	    					  //vh.mTitle.setTextColor(_con.getResources().getColor(R.color.black));
	    					 // btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.play), null, null, null);
	    					  //btPlay.setBackground(_con.getResources().getDrawable(R.drawable.play));
	    						/// btPlay.setText("play");
							//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
	    					  break;
					}
										
				}//HandleViewItem


	private void HandleViewItem3(ViewHolder vh, ViewAppearanceMode mode)
	{

		// Button btPlay = (Button) _act.findViewById(R.id.btPlay);

		//if (vh == null)
		//	return;

		switch (mode)
		{
			case PLAYING:

				if (vh != null)
				{
					//vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.red));
					//vh.mTitle.setTextColor(_con.getResources().getColor(R.color.white));

					// vh.mTitle.setBackgroundColor(Color.RED);
					//  vh.mTitle.setTextColor(Color.WHITE);
					//vh.mTitle.setTextColor(Color.RED);

			    					 /* if (vh.mWform != null)
									  {
										  WebformRendererFactory rendererFactory = new WebformRendererFactory();
										  vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.RED));
										//  vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.RED, Color.WHITE));
									  }*/


					//vh.mTitle.setBackgroundColor(Color.RED);
					//vh.mTitle.setTextColor(Color.WHITE);
					//vh.mTitle.setTextSize(25);
					//startVisualiserThis(this._MusicSrvBinder._mp.getAudioSessionId(),vh);
					//startVisualiserThis(this._MusicSrvBinder._mp.getAudioSessionId(),null);
				}
				//vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.red));
				// vh.mTitle.setTextColor(_con.getResources().getColor(R.color.white));
				// btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.pause), null, null, null);
				//  btPlay.setText("pause");
				//  btPlay.setBackground(_con.getResources().getDrawable(R.drawable.pause));
				//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.pause), null, null, null);
				break;

			case  PAUSED:

				if (vh != null)
				{
					vh.mTitle.setBackgroundColor(Color.BLUE);
					vh.mTitle.setTextColor(Color.WHITE);

					if (vh.mWform != null)
					{
						WebformRendererFactory rendererFactory = new WebformRendererFactory();
						vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.BLUE));
						vh.mWform.invalidate();

					}
					//	pauseVisualiserThis();
				}
				// vh.mTitle.setBackgroundColor(Color.BLUE);
				// vh.mTitle.setTextColor(Color.WHITE);
				//  btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				//  btPlay.setBackground(_con.getResources().getDrawable(R.drawable.play));
				//  btPlay.setText("play");
				break;

			case ERROR:
				if (vh != null)
				{
					vh.mTitle.setBackgroundColor(Color.GREEN);
					vh.mTitle.setTextColor(Color.WHITE);

					if (vh.mWform != null)
					{
						WebformRendererFactory rendererFactory = new WebformRendererFactory();
						vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.GREEN));
						vh.mWform.invalidate();
					}

					//pauseVisualiserThis();
				}
				break;

			case  STOPPED:

				if (vh != null)
				{

					vh.mTitle.setBackgroundColor(Color.BLUE);
					vh.mTitle.setTextColor(Color.WHITE);

					if (vh.mWform != null)
					{
						WebformRendererFactory rendererFactory = new WebformRendererFactory();
						vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.WHITE, Color.BLUE));
						vh.mWform.invalidate();
					}
					//pauseVisualiserThis();


					// vh.mTitle.setBackgroundColor(Color.GREEN);
					//vh.mTitle.setTextColor(Color.WHITE);
				}
				// vh.mTitle.setBackgroundColor(Color.GREEN);
				// vh.mTitle.setTextColor(Color.WHITE);
				// btPlay.setText("play");
				// btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				//  btPlay.setBackground(_con.getResources().getDrawable(R.drawable.play));
				//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				break;

			case  NORMAL:

				if (vh != null)
				{
					//vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.white));
					//vh.mTitle.setTextColor(_con.getResources().getColor(R.color.black));
					if ( MainActivity.GetThemeBehaviour(_act.get()) == "black" )
					{
						vh.mTitle.setBackgroundColor(Color.BLACK);
						vh.mTitle.setTextColor(Color.WHITE);

						if (vh.mWform != null)
						{
							WebformRendererFactory rendererFactory = new WebformRendererFactory();
							vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer( Color.WHITE, Color.BLACK));
							vh.mWform.invalidate();
						}

					}
					else
					{
						vh.mTitle.setBackgroundColor(Color.WHITE);
						vh.mTitle.setTextColor(Color.BLACK);

						if (vh.mWform != null)
						{
							WebformRendererFactory rendererFactory = new WebformRendererFactory();
							vh.mWform.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.BLACK, Color.WHITE));
							vh.mWform.invalidate();
						}
					}

					//	pauseVisualiserThis();

				}
				// vh.mTitle.setBackgroundColor(_con.getResources().getColor(R.color.white));
				//vh.mTitle.setTextColor(_con.getResources().getColor(R.color.black));
				// btPlay.setCompoundDrawables(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				//btPlay.setBackground(_con.getResources().getDrawable(R.drawable.play));
				/// btPlay.setText("play");
				//btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				break;
		}

	}


	public MusicFile SearchMusicFile(MusicFile mf)
	{
		MusicFile foundmf = MusicFile.Search( mfiles, mf);
		if (foundmf == null)
		{
			mfiles.add(mf);
			return mf;
		}
		else
		{
			return foundmf;
		}

	}
			 
}
