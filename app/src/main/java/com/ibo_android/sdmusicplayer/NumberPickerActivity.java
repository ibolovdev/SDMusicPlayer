package com.ibo_android.sdmusicplayer;

 

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
 
import android.preference.PreferenceManager;
 
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberPickerActivity extends Activity {

	SharedPreferences prefs;
	int selected_size = 10;
	TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_picker);
		
	    prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
	    tv = (TextView)findViewById(R.id.mainactivity_listitem_title);
	    
	    
		 NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker1);
		 int  TextSize = prefs.getInt("text_size", 12);
		 
		 tv.setTextSize(TextSize);
		 
		    np.setMinValue(10);// restricted number to minimum value i.e 1
		    np.setMaxValue(31);// restricked number to maximum value i.e. 31
		    np.setWrapSelectorWheel(true); 
		    np.setValue(TextSize);
		    selected_size = TextSize;
		    
		    np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
		    {
				public void onValueChange(NumberPicker picker, int oldVal,
						int newVal) {
					
					selected_size = newVal;
					tv.setTextSize(selected_size);
				}
		   
		    });
		
		    Button btOK = (Button) findViewById(R.id.btNumberPickerOKi);	 
		    
			 btOK.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						 
							Intent i = new Intent();						
							SharedPreferences.Editor editor = prefs.edit();
							editor.putInt("text_size",selected_size) ;
							editor.commit();
													
							setResult(RESULT_OK,i);
							finish();
					}
				}); 		 
			 
			 
			 Button btCancel = (Button) findViewById(R.id.btNumberPickerBac);	        
			 btCancel.setOnClickListener(new View.OnClickListener() {
							
							public void onClick(View v) {
								 
								setResult(RESULT_CANCELED,null);
								finish();
								
							}
						}); 	
		
	}	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);

		outState.putInt("SELECTED_SIZE", selected_size);
	
	}// onSaveInstanceState

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {

			if (savedInstanceState.get("SELECTED_SIZE") != null) {
				selected_size = savedInstanceState.getInt("SELECTED_SIZE");
				 NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker1);
				 np.setValue(selected_size);
				 tv.setTextSize(selected_size);
			} 

		}

	}// onRestoreInstanceState
}
