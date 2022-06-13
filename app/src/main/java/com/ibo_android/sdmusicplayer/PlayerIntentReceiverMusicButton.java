package com.ibo_android.sdmusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
 

public class PlayerIntentReceiverMusicButton extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub
		
		String fromACTION_MEDIA_BUTTON = "";
		
		/* AudioManager.
		if (Intent.Action_.equals(intent.getAction()))
		{
			 KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			 
			 if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
	         {
	         	
	         	 if ( event.getAction() != KeyEvent.ACTION_DOWN)
	         	 {
	         		 
	         		if ( (event.getFlags() & KeyEvent.FLAG_LONG_PRESS) == KeyEvent.FLAG_LONG_PRESS)
	         		{
	         			Toast.makeText(ctx, "volume up", Toast.LENGTH_LONG).show();
	         			
	         		}
	         		 
	         	 }
	         	
	         }
			
		}*/		
		 
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
		{
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);          

            if (event == null)
            {
                return;
            }

            if (KeyEvent.KEYCODE_HEADSETHOOK == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP  )
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_STOP";            	
            }
            
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_STOP";
            }
          
            if (KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_STOP";
            }
            
            if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_STOP";
            }
                
            
            if (KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_STOP";
            }
            
            if (KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_NEXT";
            }
            
            if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_PREVIOUS";
            }
                     
            
            if (KeyEvent.KEYCODE_MEDIA_FAST_FORWARD == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_FOR";
            }
            
            
            if (KeyEvent.KEYCODE_MEDIA_REWIND == event.getKeyCode() &&  event.getAction() ==  KeyEvent.ACTION_UP)
            {
            	fromACTION_MEDIA_BUTTON = "PLAY_BACK";
            }
                	 
            
            if(fromACTION_MEDIA_BUTTON != "")
            {
            	Intent in = new Intent(fromACTION_MEDIA_BUTTON);
  			  // You can also include some extra data.
  			//  in.putExtra("message", "This is my message!");  			
  			 
  			 // LocalBroadcastManager.getInstance(ctx).sendBroadcast(in);
  			 
  			 ctx.sendBroadcast(in);
      	
            }
                      
            
        }

	}//onReceive

}
