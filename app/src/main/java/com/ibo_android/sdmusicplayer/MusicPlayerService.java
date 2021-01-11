package com.ibo_android.sdmusicplayer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
 

public class MusicPlayerService extends Service
		implements OnCompletionListener, OnErrorListener, OnPreparedListener {

	public MediaPlayer _mp ;	
	private final IBinder binder = new MyBinder();
	public WeakReference<FilesAdapter> _fa ;
	
	public MusicFile _nowplaying = null;	
	public ArrayList<MusicFile> mfiles;
	AudioManager mAudioManager;
	public AudioFocusHelper _afh = null;
	ComponentName musicbuttonrec;

	public PlayerIntentReceiverMusicButton musicbuttonrec_new;

	private Handler handler = new Handler();
	private MediaPlayerProxy mpp = null;
	NotificationManager mNotificationManager;
	public IntentReceiverFromPlayer mMessageReceiver ;
	public PlayerIntentReceiver mIntentReceiver;
	
	public class MyBinder extends Binder
	{
		MusicPlayerService getService()
		{
			return MusicPlayerService.this; 
		}
		
	}


	public void initMediaPlayer( )
	{
		
	//	if (  _mpp.mp != null)
			//return;				
		
		if (  _mp != null)
		{
			_mp.release();
			_mp = null;
		}	
		
		
		
		 _mp = new MediaPlayer();  
		 
		 //<uses-permission android:name="android.permission.WAKE_LOCK" />
		// _mpp.mp.setWakeMode(_con, PowerManager.PARTIAL_WAKE_LOCK);//needs permission, throws exception at runtime
		// _mpp.mp.setWakeMode(_con, PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime			 
		
		 //if ( _fa != null && _fa.get() != null)
		 //  {
			 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);			        
			   String  ScreenBeh = prefs.getString( "settings_screen", "");
			   		  
			 //  Window w = _fa.get()._act.get().getWindow(); // in Activity's onCreate() for instance
			 //  w.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
			   //_mpp.mp.(_con,  PowerManager.ACQUIRE_CAUSES_WAKEUP);//needs permission, throws exception at runtime
			//_mpp.mp.setWakeMode(_con,  PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime   
		 
		   				
			String[]  ScreenBehArr =    ScreenBeh.split(",");  
			if (ScreenBehArr != null)
					if (ScreenBehArr.length > 0)
						ScreenBeh = ScreenBehArr[0];
						
			// if (ScreenBeh.equals("2")  )				 				
			//	 w.setFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  
				      //   WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);					
							 
			 if (ScreenBeh == "1")				  
				_mp.setWakeMode(this,  PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime
						 
		//  }
		// _mp.setScreenOnWhilePlaying(true);//does not need permission, does not work
		 				 
		 //	 WifiLock wifiLock = ((WifiManager) _con.getSystemService(Context.WIFI_SERVICE))
		//		   .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

			//	wifiLock.acquire();				 
			//	wifiLock.release();
				
		 _mp.setAudioStreamType(AudioManager.STREAM_MUSIC);				 
		 _mp.setOnCompletionListener(this);				 
		 _mp.setOnPreparedListener(this);				 
		 _mp.setOnErrorListener(this);			 		
	}			 
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
						
		//Toast.makeText(getApplicationContext(), " from Service : oncreate ", Toast.LENGTH_LONG).show();
		
	}
	  
		
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {

		if(_mp != null)
		{
			_mp.release();
			_mp = null;
		}


		try
		{
			if (mMessageReceiver != null)
			{
				unregisterReceiver(mMessageReceiver);
			}


			if (mIntentReceiver != null)
			{
				unregisterReceiver(mIntentReceiver);
			}

			//unregisterReceiver(musicbuttonrec_new);

			if (musicbuttonrec != null)
			{
				mAudioManager.unregisterMediaButtonEventReceiver(musicbuttonrec);
			}

			//Toast.makeText(getApplicationContext(), " from Service : ondestroy ", Toast.LENGTH_LONG).show();
		}
		catch (Exception ex)
		{

		}

		super.onDestroy();
	}


	private Object GetFileAdapter()
	{

		if (_fa == null) {
			return null;
		}

		if (_fa.get() == null) {
			return null;
		}


		return _fa.get();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		   return binder;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
			
		  mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		  
		if (_mp == null)
		{		
			initMediaPlayer();
			
			mpp =new MediaPlayerProxy(_mp); 
			
			mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			if (android.os.Build.VERSION.SDK_INT >= 8)
			{									
				_afh = new AudioFocusHelper(mAudioManager, mpp ,null,this);					
			}
			
	 			
			//Toast.makeText(getApplicationContext(), " from Service : onstart ", Toast.LENGTH_LONG).show();
			
			   mMessageReceiver = new IntentReceiverFromPlayer(this);
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("STOP_MUSIC"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("PLAY_STOP"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("PLAY_BACK"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("PLAY_FOR"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("PLAY_NEXT"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("PLAY_PREVIOUS"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("CLEAR_NOTIFICATION"));
			    
			    this.registerReceiver(mMessageReceiver,
		        	      new IntentFilter("PLAY_THIS_SONG"));
			    
			    
			    musicbuttonrec = new ComponentName(getPackageName(), PlayerIntentReceiverMusicButton.class.getName());			   		
			    mAudioManager.registerMediaButtonEventReceiver(musicbuttonrec );

			mIntentReceiver = new PlayerIntentReceiver( );

			 this.registerReceiver(mIntentReceiver,
					new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));


			//musicbuttonrec_new = new PlayerIntentReceiverMusicButton();
		//	this.registerReceiver(musicbuttonrec_new,
				//	new IntentFilter("android.intent.action.MEDIA_BUTTON"));
			   
				//Notification not = GetNotification();
			    
			    //startForeground(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, not);
		}//(_mp == null)
		return super.onStartCommand(intent, flags, startId);
					
	}

	@Override
	public boolean onUnbind(Intent intent) {
		
		//  Toast.makeText(getApplicationContext(), " from Service : unbind ", Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);	
			  
	}
	
	public void PlayThisSong(String songpath)
	{
		if(songpath == "")
			return;
		
		File f = new File(songpath);
		MusicFile mf = new  MusicFile("", f.getName(),f.getAbsolutePath(),0); 					
	
	///	Collections.sort(fa.mfiles);
		 
			//mfiles.add(mf);
		 	if (GetFileAdapter() != null)
		 	{
		 		MusicFile foundmf = MusicFile.Search(_fa.get().mfiles, mf);
		 		if (foundmf == null)
		 			_fa.get().mfiles.add(mf);
		 		else
		 			mf = foundmf;
				
				_fa.get().PlayStop(null, mf, false);
					
		 	}			
			
				
	}
	  /*  public int getRandomNumber() {
		
		 java.util.Random r = new java.util.Random();
	      return r.nextInt(100);
	    }*/

		public void startForeground()
		{
		  Notification not = GetNotification();
		  startForeground(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, not);		
		}
	
		public void onPrepared(MediaPlayer mp) {
			 
			
			if (_nowplaying != null)
				if (_nowplaying.CurrentPosition != 0 )					 
					mp.seekTo(_nowplaying.CurrentPosition);
			 
			  mp.start(); 
			  startForeground();

			if (_nowplaying != null)
			  _nowplaying.bInError = 0;
			  
			  if (GetFileAdapter() != null)
			  {
				  handler.post(new Runnable() { 
 				         public void run() {
 				            
 				        	//  int idx =  this.mfiles.indexOf(_nowplaying);
 							//  this._fileslist.setSelection(idx);
 				        	//_fa.get().SendNotification();
 				        	//_fa.get().notifyDataSetChanged();
 				        	_fa.get().DataSetChanged();
 							  if (_fa.get()._act.get() != null)
 							  {
 								 _fa.get()._act.get().SetPaused();
 								_fa.get()._act.get().updateSeekBar(false);
								 // _fa.get().startVisualiser(_mp.getAudioSessionId());
								  _fa.get().startVisualiserThis(_mp.getAudioSessionId(),null);
 							  }						
 				        	
 				         }
 				     });
				 	  
			  }	
			  
			   SendNotification();
			
		}//onPrepared


		public boolean onError(MediaPlayer mp, int what, int extra) {
			mp.reset();
			stopForeground(true);
			//	this.initMediaPlayer( );//causes the error to be thrown constantly
				//in combination with the code below

				if (GetFileAdapter() != null)
				{
					handler.post(new Runnable() {
						public void run() {

							_fa.get().notifyDataSetChanged();
							if (_fa.get() != null)
							{
								_fa.get()._act.get().SetPlayed();

								//_fa.get().DeleteNotification();
							}

						}
					});

				}


				
				// Button btPlay = (Button) _act.findViewById(R.id.btPlay);
				// btPlay.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(R.drawable.play), null, null, null);
				// DeleteNotification();
			   DeleteNotification();
				 return true;
				// return false;
		}
		 
		
		ArrayList<MusicFile> alreadyPlayed = new ArrayList<MusicFile>();			
		private Object GetIndexOfNextSongToPlayInRandom()//implement  back button too?
		{
		//	if (_act.get() == null)
			//{
			//	return null;
			//}

			if (mfiles == null)
			{
				return null;
			}

			if (mfiles.size() == 0)
			{
				return null;
			}
			
			
			 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);			        
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
		
		
		Object objRandomIndex = null;
		int NonRandomIdx =0;
		public void onCompletion(MediaPlayer mp) {
			//if (_act.get() == null)
			//{
			//	return;
			//}
			 objRandomIndex = GetIndexOfNextSongToPlayInRandom();
			boolean bRandom = false;
			NonRandomIdx =0;
			MusicFile nextToPlay=null;

            if (_nowplaying == null)//sth went wrong
                return;

            _nowplaying.CurrentPosition = 0;//null


			if (_nowplaying.bRepeat == 1)
			{
				nextToPlay = _nowplaying;
			}
			else
			{
				if (objRandomIndex == null)
				{
						int idx = mfiles.indexOf(_nowplaying);
						
						if (( idx + 1 ) == mfiles.size() ||  ( idx + 1 ) > mfiles.size())
							idx= -1;

						if (mfiles.size() != 0 )
                        {
                            nextToPlay = mfiles.get(idx+1);
                            NonRandomIdx = idx+1;
                        }

				}
				else
				{					
					Integer RandomIndex = (Integer) objRandomIndex;
					nextToPlay = mfiles.get(RandomIndex);
					bRandom = true;
				}	
				
			}//(_nowplaying.bRepeat == 1)



            if (GetFileAdapter() != null)
            {
                handler.post(new Runnable() {
                    public void run() {

                        _fa.get().HandleViewItem(_nowplaying,ViewAppearanceMode.NORMAL);

                    }
                });

            }

            if (nextToPlay == null)
                return;

            Uri uri = Uri.parse(nextToPlay.filepath);
							 
			   		_mp.reset();//setdatasource throws exception, due to illegal state
	    			 // apparently you cannot reset the datasource once you set it again
	    			 //so media player object works only for one song
	    			  try {
					_mp.setDataSource(this, uri);
					

					 
					 _nowplaying = nextToPlay;	
					 
					 
					  if (GetFileAdapter() != null)
					  {
						  _fa.get()._nowplaying = _nowplaying;
						  handler.post(new Runnable() { 
		 				         public void run() {
		 				            
		 				        	 _fa.get().HandleViewItem(_nowplaying,ViewAppearanceMode.PLAYING);	
		 				        	
		 				         }
		 				     });
						 					  
					  }
					   
					  
					
					  if (GetFileAdapter() != null)
					  {
						  if(bRandom )
						  {
						 // int idx =  this.mfiles.indexOf(_nowplaying);
						 // this._fileslist.setSelection(idx);
							 
							  handler.post(new Runnable() { 
			 				         public void run() {
			 				        	 Integer RandomIndex = (Integer) objRandomIndex;           
			 				        	_fa.get().SetSelection(RandomIndex);	
			 				        	
			 				         }
			 				     });
							  
						  }
						  else
						  {			
							  handler.post(new Runnable() { 
			 				         public void run() {
			 				        	           
			 				        	_fa.get().SetSelection(NonRandomIdx);	
			 				        	
			 				         }
			 				     });
							  							  
						  }						  
					  }
					   
					  _mp.prepareAsync();
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
			
		}//onCompletion		
		
		////DIFFERENT API APK
		public Notification GetNotification()
		{					
			Notification not;
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) 
					not = SendNotificationNew();
			else
				not = SendNotificationOld();			
			
			return not;
			//mNotificationManager.notify(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, not);
		}

		public void SendNotification()
		{					
			Notification not = GetNotification();			
			mNotificationManager.notify(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, not);
		}
		
		private Notification SendNotificationNew()
		{			 
			String Title = "";
			if (_nowplaying != null)
			{
				Title =_nowplaying.title;
			}			
			
			RemoteViews myRemoteView;



			ResetMediaPlayer(this);
			
			if(this._mp.isPlaying())
			{				
				myRemoteView = new RemoteViews(this.getPackageName(),R.layout.notification_custom_view_paused);
				//style="?android:attr/borderlessButtonStyle" requires API level 11
				myRemoteView.setImageViewResource(R.id.img_not_paused, R.drawable.ic_launcher);
				myRemoteView.setTextViewText(R.id.txt_not_title_paused, Title);
				 
				
				Intent newIntent = new Intent("PLAY_STOP");
				PendingIntent newPendingIntent = PendingIntent.getBroadcast(this, 2, newIntent, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_pause, newPendingIntent);
								
				Intent newIntentBack = new Intent("PLAY_BACK");
				PendingIntent newPendingIntentBack = PendingIntent.getBroadcast(this, 2, newIntentBack, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_back_paused, newPendingIntentBack);
				
				Intent newIntentFor = new Intent("PLAY_FOR");
				PendingIntent newPendingIntentFor = PendingIntent.getBroadcast(this, 2, newIntentFor, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_for_pause, newPendingIntentFor);		
				
				Intent newIntentPrevious = new Intent("PLAY_PREVIOUS");
				PendingIntent newPendingIntentPrevious = PendingIntent.getBroadcast(this, 2, newIntentPrevious, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_prev_paused, newPendingIntentPrevious);		
				
				Intent newIntentNext = new Intent("PLAY_NEXT");
				PendingIntent newPendingIntentNext = PendingIntent.getBroadcast(this, 2, newIntentNext, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_next_paused, newPendingIntentNext);		
				
				Intent newIntentClear = new Intent("CLEAR_NOTIFICATION");
				PendingIntent newPendingIntentClear = PendingIntent.getBroadcast(this, 2, newIntentClear, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_close_paused, newPendingIntentClear);		
				
				
				
			}
			else
			{				
				myRemoteView = new RemoteViews(this.getPackageName(),R.layout.notification_custom_view_played);
				
				myRemoteView.setImageViewResource(R.id.img_not, R.drawable.ic_launcher);
				myRemoteView.setTextViewText(R.id.txt_not_title, Title);
				
				Intent newIntent = new Intent("PLAY_STOP");
				PendingIntent newPendingIntent = PendingIntent.getBroadcast(this, 2, newIntent, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_play, newPendingIntent);
								
				Intent newIntentBack = new Intent("PLAY_BACK");
				PendingIntent newPendingIntentBack = PendingIntent.getBroadcast(this, 2, newIntentBack, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_back, newPendingIntentBack);
				
				Intent newIntentFor = new Intent("PLAY_FOR");
				PendingIntent newPendingIntentFor = PendingIntent.getBroadcast(this, 2, newIntentFor, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_for, newPendingIntentFor);
				
				Intent newIntentPrevious = new Intent("PLAY_PREVIOUS");
				PendingIntent newPendingIntentPrevious = PendingIntent.getBroadcast(this, 2, newIntentPrevious, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_previous, newPendingIntentPrevious);		
				
				Intent newIntentNext = new Intent("PLAY_NEXT");
				PendingIntent newPendingIntentNext = PendingIntent.getBroadcast(this, 2, newIntentNext, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_next, newPendingIntentNext);		
				
				Intent newIntentClear = new Intent("CLEAR_NOTIFICATION");
				PendingIntent newPendingIntentClear = PendingIntent.getBroadcast(this, 2, newIntentClear, 0);
				myRemoteView.setOnClickPendingIntent(R.id.bt_not_close, newPendingIntentClear);		
							
			}
			
		/*	Notification.Builder mBuilder =
			        new Notification.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle("SDMusicPlayer")
			        .setContent(myRemoteView)			        
			        .setContentText(Title);*/


			Notification.Builder mBuilder = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			{

				String NOTIFICATION_CHANNEL_ID = "com.ibo_android.sdmusicplayer";
				String channelName =getString( R.string.not_channel_name);//"My Background Service";
				android.app.NotificationChannel chan = new android.app.NotificationChannel(NOTIFICATION_CHANNEL_ID,getString(R.string.not_channel_name), NotificationManager.IMPORTANCE_LOW);
				//chan.setLightColor(Color.BLUE);
				chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
				NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				assert manager != null;
				manager.createNotificationChannel(chan);


				mBuilder =
						new Notification.Builder(this,NOTIFICATION_CHANNEL_ID)
								.setSmallIcon(R.drawable.ic_launcher)
								.setContentTitle("SDMusicPlayer")
								.setContent(myRemoteView)
								.setContentText(Title);
			}
			else
			{
				mBuilder =
						new Notification.Builder(this)
								.setSmallIcon(R.drawable.ic_launcher)
								.setContentTitle("SDMusicPlayer")
								.setContent(myRemoteView)
								.setContentText(Title);
			}
		 	
					 
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, MainActivity.class);
					
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
			
			PendingIntent pin = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
			mBuilder.setContentIntent(pin);
		//	mBuilder.setDeleteIntent(intent)
		 
			
			//T DO - how should i use this deprecated
			//mBuilder.getNotification().flags = Notification.FLAG_NO_CLEAR;
			
			
			// mId allows you to update the notification later on.
			return mBuilder.build();
			//mNotificationManager.notify(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, mBuilder.build());			
			
		}


		private void ResetMediaPlayer(MusicPlayerService mps)
		{
			if (mps._mp == null)
			{
				initMediaPlayer();
			}

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
		
	/*	@SuppressWarnings("deprecation")
		private Notification SendNotificationOld()
		{	 
				
			String Title = "";
			if (_nowplaying != null)
			{
				Title =_nowplaying.title;
			}
			
			Notification not = new Notification (R.drawable.ic_launcher,"SDMusicPlayer",
					System.currentTimeMillis());
			
			Intent resultIntent = new Intent(this, MainActivity.class);			 
			PendingIntent pin = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			not.flags= Notification.FLAG_NO_CLEAR;
			not.setLatestEventInfo(this, "SDMusicPlayer", Title, pin);
		 
			return not;
			//mNotificationManager.notify(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, not);				 
		//	return null; 	
			
		}*///SendNotificationOld
		
		
		private Notification SendNotificationOld()
		{
			String Title = "";
			if (_nowplaying != null)
			{
				Title =_nowplaying.title;
			}			
		 
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle("SDMusicPlayer")			       		        
			        .setContentText(Title);
		 	
					 
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, MainActivity.class);
					
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
			
			PendingIntent pin = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
			mBuilder.setContentIntent(pin);
		//	mBuilder.setDeleteIntent(intent)
		 
			
			//T DO - how should i use this deprecated
			//mBuilder.getNotification().flags = Notification.FLAG_NO_CLEAR;
			
			
			// mId allows you to update the notification later on.
			return mBuilder.build();
			//mNotificationManager.notify(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID, mBuilder.build());	
			
			
			
		}
		
		
		public void DeleteNotification()
		{				
			NotificationManager mNotificationManager =
				    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
				
			mNotificationManager.cancel(FilesAdapter.PLAYING_SONG_NOTIFICATION_ID);				
			
		}//DeleteNotification




		

}
