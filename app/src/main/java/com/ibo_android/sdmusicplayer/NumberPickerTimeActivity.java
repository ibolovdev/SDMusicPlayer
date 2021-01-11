package com.ibo_android.sdmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberPickerTimeActivity extends Activity {

	SharedPreferences prefs;
	int selected_time = 10;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.number_picker_time_activity);		
		
		 prefs = PreferenceManager.getDefaultSharedPreferences(this);		    
		    
		 NumberPicker np = (NumberPicker)findViewById(R.id.number_time_picker_numberpicker);
		 int  TextSize = prefs.getInt("SEEK_DURATION", 10);//be carefull seek_duration is different from SEEK_DURATION
			  
			 
			    np.setMinValue(0);// restricted number to minimum value i.e 1
			    np.setMaxValue(59);// restricked number to maximum value i.e. 31
			    np.setWrapSelectorWheel(true); 
			    np.setValue(TextSize);
			    selected_time = TextSize;
			    
			    np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
			    {
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						
						selected_time = newVal;
						 
					}
			   
			    });
			
			    Button btOK = (Button) findViewById(R.id.bt_number_time_picker_ok);	 
			    
				 btOK.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							 
								Intent i = new Intent();						
								SharedPreferences.Editor editor = prefs.edit();
								editor.putInt("SEEK_DURATION",selected_time) ;
								editor.commit();
														
								setResult(RESULT_OK,i);
								finish();
						}
					}); 		 
				 
				 
				 Button btCancel = (Button) findViewById(R.id.bt_number_picker_time_back);	        
				 btCancel.setOnClickListener(new View.OnClickListener() {
								
								public void onClick(View v) {
									 
									setResult(RESULT_CANCELED,null);
									finish();
									
								}
							}); 		
		
	}//onCreate
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);

		outState.putInt("SEEK_DURATION", selected_time);
	
	}// onSaveInstanceState

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {

			if (savedInstanceState.get("SEEK_DURATION") != null) {
				selected_time = savedInstanceState.getInt("SEEK_DURATION");
				 NumberPicker np = (NumberPicker)findViewById(R.id.number_time_picker_numberpicker);
				 np.setValue(selected_time);
				 
			} 

		}

	}// onRestoreInstanceState
}
