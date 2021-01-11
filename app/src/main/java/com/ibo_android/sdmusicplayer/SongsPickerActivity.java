package com.ibo_android.sdmusicplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.ibo_android.sdmusicplayer.R;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SongsPickerActivity extends Activity {

	private ListView fileslist;
	private SongsPickerAdapter psa;
	private String _root_dir = "";

	private static final String SONGS_CATALOG = "SONGS_CATALOG";
	private static final String ROOT_DIRECTORY = "ROOT_DIRECTORY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MainActivity.initThemeBehaviour(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs_picker);

		String MusicDir = this.getIntent().getStringExtra("root_dir");
		
		 if (savedInstanceState != null)
			 _root_dir = savedInstanceState.getString(ROOT_DIRECTORY);				
		 
		
		if (_root_dir != "" && _root_dir != null)
			MusicDir = _root_dir;
	
		ArrayList<Parcelable> selfiles_asparam = this.getIntent()
				.getParcelableArrayListExtra("SELECTED_SONGS");

		// Toast.makeText(this, sel, Toast.LENGTH_LONG).show();

		psa = new SongsPickerAdapter(MusicDir, this, selfiles_asparam);

		fileslist = (ListView) findViewById(R.id.lvIckSongList);
	//	 Collections.sort(psa.mfiles);
		fileslist.setAdapter(psa);

		psa.fileslist = fileslist;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		TextView tv = (TextView) findViewById(R.id.txtRootFolder);
		
		 int  TextSize = prefs.getInt("text_size", 12);
		 tv.setTextSize(TextSize);

		 
		 
		Button btOK = (Button) findViewById(R.id.btOK);
		btOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent i = new Intent();
				
				for (Object omf : psa.selectedfiles.toArray()) {
					MusicFile typedmf = (MusicFile) omf;
					File fu = new File(typedmf.filepath);
					if (fu.isDirectory()) {
						psa.selectedfiles.remove(typedmf);
					}
				}				
				
				if (psa.selectedfiles.size() < 1000)//it seems that there is a limit on the size that a intent can have
				{					
					i.putExtra("songs_list", psa.selectedfiles);	
					
					
				}
				else
				{
					
					//SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
				   // SharedPreferences.Editor editor = sharedPreferences.edit();
				   // editor.(key, value);
				   // editor.commit();
				    
					//Context mApplication =  getApplicationContext();
					 
					// ArrayList<MusicFile> al = new ArrayList<MusicFile>(psa.selectedfiles.subList(0, 1000));
					//i.putExtra("songs_list", al);
					
				 	MyApplicationObject mApplication = (MyApplicationObject)getApplicationContext();
					mApplication.setSelectedFiles(null);
					mApplication.setSelectedFiles(psa.selectedfiles);
					
					i.putExtra("songs_list", "MyApplicationObject");
					
					
					//WriteParcelListToFile(psa.selectedfiles);
					
				}			

				setResult(RESULT_OK, i);			
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				setResult(RESULT_CANCELED, null);
				finish();

			}
		});

		Button btCheckAll = (Button) findViewById(R.id.btCheckAll);
		btCheckAll.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				psa.SelectAll();
			}
		});

		Button btUnCheckAll = (Button) findViewById(R.id.btUnCheckAll);
		btUnCheckAll.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				psa.UnSelectAll();
			}
		});
		
		Button btUP = (Button) findViewById(R.id.btUP);
		btUP.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				psa.GoUp();
			}
		});

	}// onCreate
	
	
	private void WriteParcelListToFile(ArrayList<MusicFile> selectedfiles)
	{
		

		String FILENAME = "selected_files.dd";
		 
		 
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//fos.write(string.getBytes());
		//fos.close();
		
		
		//  fout = new FileOutputStream(file);

	        Parcel parcel = Parcel.obtain();
	        ArrayList<Object> list = new ArrayList<Object>(psa.selectedfiles);
	      //  Log.d(TAG, "write items to cache: " + items.size());
	        parcel.writeList(list);
	        
	      
	        byte[] data = parcel.marshall();
	        try {
				fos.write(data);		
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        finally
	        {
	        	try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }	
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MainActivity.GET_SONGS_FOLDER) {

			if (resultCode == RESULT_OK)// TODO should remove files also
			{
				ArrayList<Parcelable> selectedsongs = data
						.getParcelableArrayListExtra("songs_list");

				for (Object omf : selectedsongs.toArray()) {
					psa.selectedfiles.add((MusicFile) omf);
				}

				// fileslist = (ListView) findViewById(R.id.lvSongsList);
				// fileslist.setAdapter(psa);//filelist is null

				// psa._fileslist = fileslist;

			} else if (resultCode == RESULT_CANCELED) {

			}

		} // (requestCode == GET_SONGS)

	}// onActivityResult

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		psa.fileslist = null;
		psa._act = null;
		psa = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_songs_picker, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);

		if (!(psa.selectedfiles == null)) {
			outState.putParcelableArrayList(SONGS_CATALOG, psa.selectedfiles);
		}
		
		if (psa.getRootDirectory() != "") {
			outState.putString(ROOT_DIRECTORY, psa.getRootDirectory());
		}

	}// onSaveInstanceState

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			if (savedInstanceState.get(SONGS_CATALOG) != null) {
				ArrayList<Parcelable> selectedsongs = savedInstanceState
						.getParcelableArrayList(SONGS_CATALOG);

				for (Object omf : selectedsongs.toArray()) {
					this.psa.selectedfiles.add((MusicFile) omf);
				}

			}
			if (savedInstanceState.get(ROOT_DIRECTORY) != null) {
				_root_dir = savedInstanceState.getString(ROOT_DIRECTORY);				
			}		 

		}

	}// onRestoreInstanceState
	

	public void SetRootDirectoryTextView(String folder)
	{		
		TextView txtRootDirectory = (TextView) findViewById(R.id.txtRootFolder);
		txtRootDirectory.setText(folder);		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onTrimMemory(int)
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
		
		switch (level)
		{
					case  TRIM_MEMORY_UI_HIDDEN:
						if (true)
						{}
							
					break;
						
					case TRIM_MEMORY_RUNNING_MODERATE:
						break;
						
					case TRIM_MEMORY_RUNNING_LOW:
						break;							
						
					case TRIM_MEMORY_RUNNING_CRITICAL:
						break;
						
					case TRIM_MEMORY_BACKGROUND:
						break;
						
					case TRIM_MEMORY_MODERATE:
						break;
						
					case TRIM_MEMORY_COMPLETE:
						break;		
		
		}			
		
	}//onTrimMemory
	
}
