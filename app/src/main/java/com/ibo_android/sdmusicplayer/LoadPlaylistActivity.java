package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;

import com.ibo_android.sdmusicplayer.R;

import android.os.Bundle; 
import android.app.Activity;
import android.content.Intent; 
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class LoadPlaylistActivity extends Activity {

	private static final String SELECTED_PLAYLISTS = "SELECTED_PLAYLISTS";
	private ListView playlists;
	private LoadPlaylistAdapter lpa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MainActivity.initThemeBehaviour(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_playlist);
		
		CharSequence[]  playlists_asparam = this.getIntent()
				.getCharSequenceArrayExtra("PLAYLISTS");		
		
		lpa = new LoadPlaylistAdapter(this, playlists_asparam);

		playlists = (ListView) findViewById(R.id.lvPlaylists);

		playlists.setAdapter(lpa);

		//lpa.fileslist = playlists;
		
		
		Button btOK = (Button) findViewById(R.id.btOK2);
		btOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent i = new Intent();
				i.putExtra("playlist_list", lpa.selectedplaylists);

				setResult(RESULT_OK, i);
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btCancel2);
		btCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				setResult(RESULT_CANCELED, null);
				finish();

			}
		});
		
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);

		if (!(lpa.selectedplaylists == null)) {
			outState.putStringArrayList(SELECTED_PLAYLISTS, lpa.selectedplaylists);
		}

	}// onSaveInstanceState

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			if (savedInstanceState.get(SELECTED_PLAYLISTS) != null) {
				ArrayList<String> selectedplaylists = savedInstanceState
						.getStringArrayList(SELECTED_PLAYLISTS);

				for (Object pl : selectedplaylists.toArray()) {
					this.lpa.selectedplaylists.add((String) pl);
				}

			}

		}

	}// onRestoreInstanceState
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load_playlist, menu);
		return true;
	}

}
