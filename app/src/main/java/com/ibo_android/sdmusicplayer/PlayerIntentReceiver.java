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


		//24-3-22
		/*

				1 MESSAGE FOR VERSION CODE 38
				error
				Implicit Internal Intent
				Your app contains an Implicit Internal Intent vulnerability. Please see this Google Help Center article for details.

				com.ibo_android.sdmusicplayer.PlayerIntentReceiver.onReceive

Remediation of Implicit Internal Intent Vulnerability
This information is intended for developers with app(s) that use Implicit Intents to reach one of their internal components.

What’s happening
One or more of your apps contain an Implicit Internal Intent issue. Implicit Intents used to reach an internal component allow attackers to intercept the message and either drop it, read its contents, or even replace its contents. Location(s) of the Implicit Intent usage(s) in your app can be found in the Play Console notification for your app.

How to fix “Implicit Internal Intent” alerts
Review your app for the location where an Implicit Intent is used. For example the following code uses Implicit Intents to reach an internal component:

//The app has a component that registers MY_CUSTOM_ACTION, which is only

//registered by this app, indicating that the dev intends for this Intent

//to be delivered to the internal component safely.

Intent intent = new Intent("MY_CUSTOM_ACTION");

//Add potentially sensitive content to 'intent'

intent.putExtra("message", sensitive_content);

startActivity(intent);

Google recommends that developers use Explicit Intents to reach their internal components either by:

Use Intent.setComponent to explicitly set the component to handle the Intent.
Use Intent.setClass or Intent.setClassName to explicitly set the target component.
Use Intent.setPackage to limit the components this Intent will resolve to.
Next Steps
Update your app using the steps highlighted above.
Sign in to your Play Console and submit an updated version of your app.
During this time your new app or app update will be in a in review status until your request is reviewed. If the app has not been updated correctly, you will still see the warning.

We’re here to help
If you have technical questions about the vulnerability, you can post to Stack Overflow and use the tag “android-security.” For clarification on steps you need to take to resolve this issue, you can contact our support team.

		 */




		if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY"))
		{
			
			String action = "STOP_MUSIC";
			//Toast.makeText(ctx, "AUDIO_BECOMING_NOISY", Toast.LENGTH_LONG).show();
			
			 Intent in = new Intent(action);
			  // You can also include some extra data.
			//  in.putExtra("message", "This is my message!");
			in.setPackage("com.ibo_android.sdmusicplayer");
			 
			 // LocalBroadcastManager.getInstance(ctx).sendBroadcast(in);
			 
			 ctx.sendBroadcast(in);
			
		}		

	}

}
