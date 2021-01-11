package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;

public class ResultsPickerActivity extends Activity {
	
	private ListView resultslist;
	private ResultsPickerAdapter ra;
	ArrayList<MusicFile>  mfiles = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MainActivity.initThemeBehaviour(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results_picker);
		
		   mfiles = this.getIntent().getParcelableArrayListExtra("search_results");
		
		 if (savedInstanceState != null)
			 mfiles = savedInstanceState.getParcelableArrayList("search_results");	
		
		ra = new ResultsPickerAdapter(this , mfiles ,this);   		
		resultslist = (ListView) findViewById(R.id.lvSearchResults);		 
		resultslist.setAdapter(ra);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results_picker, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);
 
		
		if (mfiles != null) {
			outState.putParcelableArrayList("search_results", mfiles);
		}

	}// onSaveInstanceState
	
	public void ReturnResult(MusicFile mf)
	{		
		Intent i = new Intent();
		i.putExtra("chosen_song", mf);

		setResult(RESULT_OK, i);
		finish();
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		ra._act = null;
		ra = null;
		
	}
}
