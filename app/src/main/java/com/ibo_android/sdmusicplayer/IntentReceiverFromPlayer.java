package com.ibo_android.sdmusicplayer;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent; 
import android.os.Handler;

public class IntentReceiverFromPlayer extends BroadcastReceiver {
	
	private Handler handler = new Handler();
	private MusicPlayerService _srv;
	
	public IntentReceiverFromPlayer(MusicPlayerService srv ) 
	{
		super();		
		_srv = srv;		
	}	


	private Object GetFileAdapter()
	{

		if (_srv._fa == null) {
			return null;
		}

		if (_srv._fa.get() == null) {
			return null;
		}


		return _srv._fa.get();
	}


	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		if (intent.getAction().equals("PLAY_THIS_SONG"))
		{
			String songpath = intent.getStringExtra("song_path");		
			_srv.PlayThisSong(songpath);			
		}
		
		if (intent.getAction().equals("STOP_MUSIC"))
		{
			
			//Toast.makeText(ctx, "STOP_MUSIC from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			
	//		_fa._nowplaying.CurrentPosition=_fa._MusicSrvBinder._mp.getCurrentPosition();
      //  	if (_fa._MusicSrvBinder._mp.isPlaying()) _fa.PlayStop();  // _fa._mpp.mp.stop();
	        if(_srv._nowplaying == null)
	        	return;
        
        
			_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();

			//if (_srv._fa.get() != null)
			 if (GetFileAdapter() != null)
			  {
				  handler.post(new Runnable() { 
				         public void run() {
				        	 if (_srv._mp.isPlaying())
				        	 {
				        		 _srv._fa.get().PlayStop();	
				        		 _srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
				        	 }				        	  				
				        	
				         }
				     });
				 	  
			  }	
			 else
			 {
				 if (_srv._mp.isPlaying())
		        	{
		        		_srv._mp.stop();  // _fa._mpp.mp.stop();
		        	}
			 }
        	
    
            //_fa._mpp.mp.release();
            //_fa._mpp.mp = null;			
		}//if (intent.getAction().equals("STOP_MUSIC"))
		
		if (intent.getAction().equals("PLAY_STOP")  )
		{
			//Toast.makeText(ctx, "PLAY_STOP from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			
			//_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();


				if (GetFileAdapter() != null)
				{
					handler.post(new Runnable() {
						public void run() {
							_srv._fa.get().PlayStop();
							// _srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
						}

					});

				}
				else
				{
					if (_srv._mp.isPlaying())
						_srv._mp.stop();
					else
						_srv._mp.start();

					_srv.SendNotification();

				}
			
       	
		}//if (intent.getAction().equals("PLAY_STOP"))
		
		
		if (intent.getAction().equals("PLAY_BACK")  )
		{
			//Toast.makeText(ctx, "PLAY_BACK from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			//_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
			
			 if (GetFileAdapter() != null)
			  {
				  handler.post(new Runnable() { 
				         public void run() {				        	  
				        		 _srv._fa.get().GoBackward();	
				        		// _srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
				        	 }			        	
				         
				     });
				 	  
			  }	
			 else
			 {
				 if (_srv._mp.isPlaying())
		        	_srv._mp.stop();
				 else
					 _srv._mp.start();
		        	
			 }
		}//if (intent.getAction().equals("PLAY_BACK"))
		
		
		if (intent.getAction().equals("PLAY_FOR")  )
		{
			//Toast.makeText(ctx, "STOP_FOR from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			//_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
			
			 if (GetFileAdapter() != null)
			  {				  handler.post(new Runnable() { 
				         public void run() {				        	  
				        		 _srv._fa.get().GoForward();	
				        		 //_srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
				        	 }			        	
				         
				     });
				 	  
			  }	
			 else
			 {
				/* if (_srv._mp.isPlaying())
		        	_srv._mp.stop();
				 else
					 _srv._mp.start();*/
		        	
			 }
		}//if (intent.getAction().equals("PLAY_FOR"))
		
		
		
		if (intent.getAction().equals("PLAY_PREVIOUS")   )
		{
			//Toast.makeText(ctx, "STOP_FOR from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			//_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
			
			 if (GetFileAdapter() != null)
			  {				  handler.post(new Runnable() { 
				         public void run() {				        	  
				        		 _srv._fa.get().PlayPrevious();	
				        		// _srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
				        	 }			        	
				         
				     });
				 	  
			  }	
			 
		}//if (intent.getAction().equals("PLAY_PREVIOUS"))
		
		
		if (intent.getAction().equals("PLAY_NEXT")   )
		{
			//Toast.makeText(ctx, "STOP_FOR from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			//_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();


                if (GetFileAdapter() != null)
                {				  handler.post(new Runnable() {
							public void run() {
								_srv._fa.get().PlayNext();
								// _srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
							}

						});

                }



		}//if (intent.getAction().equals("PLAY_NEXT"))
		
		if (intent.getAction().equals("CLEAR_NOTIFICATION"))
		{
			//Toast.makeText(ctx, "STOP_FOR from IntentReceiverFromPlayer", Toast.LENGTH_LONG).show();
			//_srv._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
			// _srv.DeleteNotification();
			_srv.stopForeground(true);
			/* if (_srv._fa.get() != null)
			  {				  handler.post(new Runnable() { 
				         public void run() {
				        	 if(_srv._fa.get()._act.get() != null)
				        		 _srv._fa.get()._act.get().ExitApp();
				        		// _srv._fa.get().DeleteNotification();	
				        		// _srv._fa.get()._nowplaying.CurrentPosition = _srv._mp.getCurrentPosition();
				        	 }			        	
				         
				     });
				 	  
			  }	
			 else
			 {
				 
				 Intent i = new Intent(ctx, MusicPlayerService.class);			 
				ctx.stopService(i);	
					//finish();
					
				// _srv.DeleteNotification();
				 
			 }*/
			 
		}//if (intent.getAction().equals("CLEAR_NOTIFICATION"))
			
				
	}//onReceive
	

}
