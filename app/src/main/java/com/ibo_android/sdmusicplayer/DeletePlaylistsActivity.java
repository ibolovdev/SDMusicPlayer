package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle; 
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class DeletePlaylistsActivity extends Activity {

	private static final String SELECTED_PLAYLISTS = "SELECTED_PLAYLISTS";
	private ListView playlists;
	private DeletePlayListsAdapter dpa;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MainActivity.initThemeBehaviour(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_playlists);
		
		CharSequence[]  playlists_asparam = this.getIntent()
				.getCharSequenceArrayExtra("PLAYLISTS");		
		
		dpa = new DeletePlayListsAdapter( this, playlists_asparam);

		playlists = (ListView) findViewById(R.id.lvPlayListsToDelete);

		playlists.setAdapter(dpa);
		
		
		Button btOK = (Button) findViewById(R.id.btDeletePlaylistsOK);
		btOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent i = new Intent();
				i.putExtra("playlist_list", dpa.selectedplaylists);

				setResult(RESULT_OK, i);
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btDeletePlayListCancel);
		btCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				setResult(RESULT_CANCELED, null);
				finish();

			}
		});
		
	}//onCreate
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);

		if (!(dpa.selectedplaylists == null)) {
			outState.putStringArrayList(SELECTED_PLAYLISTS, dpa.selectedplaylists);
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
					this.dpa.selectedplaylists.add((String) pl);
				}

			}

		}

	}// onRestoreInstanceState
	
}
