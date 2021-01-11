package com.ibo_android.sdmusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
 
public class PlayerIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub
		
		
		//Toast.makeText(ctx, "AUDIO_BECOMING_NOISY", Toast.LENGTH_LONG).show();
		// use google api vm
		//add adb to PATH
		//C:\Users\sifis\Documents\Eclipse_AndroidStudio\SDMusicPlayer>adb root
		//restarting adbd as root

		//C:\Users\sifis\Documents\Eclipse_AndroidStudio\SDMusicPlayer>adb shell am broadcast -a android.media.AUDIO_BECOMING_NOISY
		//Broadcasting: Intent { act=android.media.AUDIO_BECOMING_NOISY flg=0x400000 }
		//Broadcast completed: result=0

		//C:\Users\sifis\Documents\Eclipse_AndroidStudio\SDMusicPlayer>adb shell am broadcast -a KeyEvent.KEYCODE_MEDIA_STOP
		//Broadcasting: Intent { act=KeyEvent.KEYCODE_MEDIA_STOP flg=0x400000 }
		//Broadcast completed: result=0


//adb shell input keyevent KeyEvent.KEYCODE_MEDIA_STOP


		if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY"))
		{
			
			String action = "STOP_MUSIC";
			//Toast.makeText(ctx, "AUDIO_BECOMING_NOISY", Toast.LENGTH_LONG).show();
			
			 Intent in = new Intent(action);
			  // You can also include some extra data.
			//  in.putExtra("message", "This is my message!");
			
			 
			 // LocalBroadcastManager.getInstance(ctx).sendBroadcast(in);
			 
			 ctx.sendBroadcast(in);
			
		}		

	}

}
