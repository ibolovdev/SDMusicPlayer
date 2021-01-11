package com.ibo_android.sdmusicplayer;

import java.lang.ref.WeakReference; 
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;

public class AudioFocusHelper implements OnAudioFocusChangeListener {

	 AudioManager mAudioManager;	 
	 MediaPlayerProxy _mpp ;
	 WeakReference<FilesAdapter> _fa ;	 
	 MusicPlayerService _MusicPlayerSrv;
	 private Handler handler = new Handler();
	 
	  public AudioFocusHelper(AudioManager am,  MediaPlayerProxy mpp, 
			  FilesAdapter fa, MusicPlayerService MusicPlayerSrv  ) {
	        
		   mAudioManager = am;		 
	      _mpp = mpp;
	      _fa = new WeakReference<FilesAdapter>(fa);	      
	      _MusicPlayerSrv = MusicPlayerSrv; 
	    }
	
	public boolean requestAudioFocus()
	{
		
		//AudioManager audioManager = (AudioManager) _con.getSystemService(Context.AUDIO_SERVICE);
		int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
		    AudioManager.AUDIOFOCUS_GAIN);

		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
		    // could not get audio focus.
			return false;
		}
		else
			return true;
		
	}
	
	
	public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
            mAudioManager.abandonAudioFocus(this);
    }
 
	
	
	
	
	
	/*29 ?a? - 4:36 p.. st?? ??d?s? efa????? 21
		Samsung Galaxy J1 (j1xlte), 1024MB RAM, Android 5.1
		??af??? 1

java.lang.IllegalStateException: 
  at android.media.MediaPlayer.isPlaying (MediaPlayer.java)
  at com.ibo_android.sdmusicplayer.AudioFocusHelper.onAudioFocusChange (AudioFocusHelper.java)
  at android.media.AudioManager$FocusEventHandlerDelegate$1.handleMessage (AudioManager.java:3221)
  at android.os.Handler.dispatchMessage (Handler.java:102)
  at android.os.Looper.loop (Looper.java:145)
  at android.app.ActivityThread.main (ActivityThread.java:6917)
  at java.lang.reflect.Method.invoke (Method.java)
  at java.lang.reflect.Method.invoke (Method.java:372)
  at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run (ZygoteInit.java:1404)
  at com.android.internal.os.ZygoteInit.main (ZygoteInit.java:1199)*/


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
	
	public void onAudioFocusChange(int focusChange) {
		
		   switch (focusChange) {
	        case AudioManager.AUDIOFOCUS_GAIN:
	        	//does not come to this
	            // resume playback
	           /* if (_mpp.mp == null)
	            {
	            	_mpp.mp = new MediaPlayer();   	
	           	  	_mpp.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
	           	           	  	
	            } 
	            else if (!_mpp.mp.isPlaying())
	            {
	            	//_mpp.mp.start();//when a call is ended player starts to play
	            }*/
	          //  _mp.setVolume(1.0f, 1.0f);
	            
	            	            
	          /*  if (_fa.get() != null)
				  {
					  handler.post(new Runnable() { 
					         public void run() {
					            
					        	//  int idx =  this.mfiles.indexOf(_nowplaying);
								//  this._fileslist.setSelection(idx);
					        	//_fa.get().SendNotification();
					        	 _fa.get().SetPauseButtonFromAudioFocusHelper(); 				
					        	
					         }
					     });
					 	  
				  }	 */       
	            
	            
	            break;

	        case AudioManager.AUDIOFOCUS_LOSS:
	            // Lost focus for an unbounded amount of time: stop playback and release media player
		         		 
	        	
	       	if (!(_MusicPlayerSrv._mp == null )  )//in case of orientation change 
	        {
	        		 
	       		try 
	       		{
	       			if (GetFileAdapter() != null )
		       			if (_fa.get()._nowplaying != null )
		       				_fa.get()._nowplaying.CurrentPosition = _MusicPlayerSrv._mp.getCurrentPosition();
			        	
		       		 
		       		if (_MusicPlayerSrv._mp.isPlaying()) _MusicPlayerSrv._mp.pause();
			         
			            //_mpp.mp.release();				            
			           // _mpp.mp = null;
	    		}
	       		catch (Exception e)
	       		{
	    		//	throw e;
	    		} finally {
	    			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);// did
	    																			// nor
	    																			// work,
	    																			// the
	    																			// ui
	    																			// thread
	    																			// launches
	    																			// the
	    																			// thread
	    																			// and
	    																			// returns
	    			// work has to take place in the thread, sending a message or sth
	    			// like that
	    		}      				
			       		
		    }//(!(_mpp.mp == null )  )


				if (GetFileAdapter() != null)
				  {
					  handler.post(new Runnable() {
							 public void run() {

								//  int idx =  this.mfiles.indexOf(_nowplaying);
								//  this._fileslist.setSelection(idx);
								//_fa.get().SendNotification();
								 _fa.get().SetPlayButtonFromAudioFocusHelper();

							 }
						 });
								  }
	       	
	       	//_MusicPlayerSrv.DeleteNotification();
	    	_MusicPlayerSrv.SendNotification();
	        	   break;

	        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	            // Lost focus for a short time, but we have to stop
	            // playback. We don't release the media player because playback
	            // is likely to resume
	        	
	        	
	        	if (!(_MusicPlayerSrv._mp == null )  )//in case of orientation change 
		        {
		        		 
		       		try 
		       		{
		       			if (GetFileAdapter() != null )
			       			if (_fa.get()._nowplaying != null )
			       				_fa.get()._nowplaying.CurrentPosition=_MusicPlayerSrv._mp.getCurrentPosition();
				        	
			       		 
			       		if (_MusicPlayerSrv._mp.isPlaying()) _MusicPlayerSrv._mp.pause();
				         
				            //_mpp.mp.release();				            
				           // _mpp.mp = null;
		    		}
		       		catch (Exception e)
		       		{
		    		//	throw e;
		    		} finally {
		    			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);// did
		    																			// nor
		    																			// work,
		    																			// the
		    																			// ui
		    																			// thread
		    																			// launches
		    																			// the
		    																			// thread
		    																			// and
		    																			// returns
		    			// work has to take place in the thread, sending a message or sth
		    			// like that
		    		}      				
				       		
			    }//(!(_mpp.mp == null )  )
	        	
	        	
	        	
	        	
	        	
	        	
	        	
	        	
	        /*	try
	        	{
	        		
	        		if(_mpp.mp != null ) 
		        		if (_mpp.mp.isPlaying()) _mpp.mp.pause();        		
	        		
	        	}
	        	catch (Exception e)
	       		{
	        		//	throw e;
	    		} finally
	       		{
	    		
	    		} */     				
			    
	        	 if (GetFileAdapter() != null)
				  {
					  handler.post(new Runnable() { 
					         public void run() {
					            
					        	//  int idx =  this.mfiles.indexOf(_nowplaying);
								//  this._fileslist.setSelection(idx);
					        	//_fa.get().SendNotification();
					        	 _fa.get().SetPlayButtonFromAudioFocusHelper(); 				
					        	 _fa.get().notifyDataSetChanged();
					         }
					     });
					 	  
				  }	
		       	
		       //	_MusicPlayerSrv.DeleteNotification();
		 	
	        	 _MusicPlayerSrv.SendNotification();
	            break;

	        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	            // Lost focus for a short time, but it's ok to keep playing
	            // at an attenuated level
	           //if (_mpp.mp.isPlaying()) _mpp.mp.setVolume(0.1f, 0.1f);// raisning the volume afterwards 
	        	//did not have the same result , as changing orientation
	         	//if(_mpp.mp != null ) 
	         	//	if (_mpp.mp.isPlaying()) _mpp.mp.pause();//the player stopped many times
	            break;
	    }
		
		
	}	//onAudioFocusChange	

}
