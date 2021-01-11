package com.ibo_android.sdmusicplayer;

import com.ibo_android.sdmusicplayer.R;

import android.os.Bundle;
import android.preference.PreferenceActivity; 
import android.content.Intent; 

public class SettingsActivity extends PreferenceActivity  {

	//public IntentReceiverFromSettings mMessageReceiver ;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		MainActivity.initThemeBehaviour(this);
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_layout);
          
        
      //  mMessageReceiver = new IntentReceiverFromSettings();
	    
		  //  _con.registerReceiver(mMessageReceiver,
	        	//      new IntentFilter("STOP_MUSIC"));  	    
	         
       
		     //  LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
		        	 //     new IntentFilter("startDirectoryChooserActivity"));		    
       
		       
    }

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	
		
	} 

}
