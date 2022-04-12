package com.ibo_android.sdmusicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
 


import java.util.Locale;

import com.ibo_android.sdmusicplayer.R;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;

//public class MainActivity extends android.support.v4.app.FragmentActivity implements NoticeDialogListener {
public class MainActivity extends Activity implements NoticeDialogListener, android.media.audiofx.Visualizer.OnDataCaptureListener {
	// fragmentactivity is for the dialogs

	private static final int GET_DIRECTORY = 1;
	private static final int GET_SONGS = 2;
	public static final int GET_SONGS_FOLDER = 3;
	private static final int SHOW_PREFERENCES = 4;
	private static final int GET_PLAYLIST = 5;
	private static final int SEARCH_SONGS = 6;
	private static final int DELETE_PLAYLISTS = 7;
	
	private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 8;
	private static final int MY_PERMISSIONS_PERMISSIONS_MULTIPLE_REQUEST = 9;


	private static final String CURRENT_SONG = "CURRENT_SONG";
	private static final String CURRENT_SONG_POSITION = "CURRENT_SONG_POSITION";
	private static final String CURRENT_SONG_ISPLAYING = "CURRENT_SONG_ISPLAYING";
	private static final String SONGS_CATALOG = "SONGS_CATALOG";

	private static final String LATEST_PLAYLIST = "LATEST_PLAYLIST";
	private static final String LATEST_PLAYLIST_SONG = "LATEST_PLAYLIST_SONG";
	private static final String LATEST_PLAYLIST_SONG_POSITION = "LATEST_PLAYLIST_SONG_POSITION";

	public static final String MYPREFS = "MYPREFS";
	public static final String REPEAT_SONG = "REPEAT_SONG";
	private static final int CONFIRMATION_DIALOG = 10;
	private static final int VOICE_RECOGNITION = 0;

	//private String _LatestPlaylistLoaded = "";
	private ArrayList<String> _alLatestPlaylistLoaded = null;
 

	private ListView fileslist;
	private FilesAdapter fa;
	// private MediaPlayerProxy mpp ;
	private MyDB _dba;

	private String MUSIC_DIR = "";

	Handler seekHandler = new Handler();
	TextView txtCurrent;
	TextView txtTotal;
	SeekBar SeekBarSong;
	private boolean mBoundSrv = false;
	private MusicFile mfToPlayWhenServIsReady = null;
	private MusicPlayerService MusicSrvBinder = null;
	private FileSystemProvider _FileSystemProvider = null;

	private ServiceConnection MusicSrvConn = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicSrvBinder = ((MusicPlayerService.MyBinder) service).getService();
			if ( fa == null )
			{
				return;			
			}
				
			WeakReference<FilesAdapter> _fa = new WeakReference<FilesAdapter>(fa);
			MusicSrvBinder._fa = _fa;
			MusicSrvBinder._afh._fa = _fa;
			_fa.get()._MusicSrvBinder = MusicSrvBinder;
			
			MusicSrvBinder.mfiles = fa.mfiles;
			mBoundSrv = true;
			updateSeekBar(false);//for updating a paused song during orientation changing
			updateSeekBar(true);
			_fa.get().restartVisualizer();
			
			if (MusicSrvBinder._mp.isPlaying())
			{
				_fa.get()._act.get().SetPaused();
			}
			else
			{
				_fa.get()._act.get().SetPlayed();
			}
			
			if (mfToPlayWhenServIsReady != null)
			{
				if (!MusicSrvBinder._mp.isPlaying())
					_fa.get().PlayStop(null, mfToPlayWhenServIsReady,true);
				mfToPlayWhenServIsReady = null;
			}
			
			_fa.get().notifyDataSetChanged();
				
		}

		public void onServiceDisconnected(ComponentName name) {
			MusicSrvBinder = null;
			fa._MusicSrvBinder = null;
			mBoundSrv = false;
		}

	};

	@Override
	public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate)
	{
		if (waveformView != null) {
			waveformView.setWaveform(waveform);
		}
	}


	@Override
	public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate)
	{

	}


	private Visualizer visualiser;
	public WaveformView waveformView;
	private static final int CAPTURE_SIZE = 256;

/*	private void play()
	{

		Uri uri = Uri.parse(  Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Music/queen/10 Greatest Hits I (1981)/01 - Bohemian Rhapsody.mp3");
		MediaPlayer _mp = new MediaPlayer();
		try {
			_mp.setDataSource(this, uri);
		} catch (IOException e) {
			e.printStackTrace();
		}
		_mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try {
			_mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		_mp.start();


		visualiser = new Visualizer(_mp.getAudioSessionId());
		visualiser.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, false);
		visualiser.setCaptureSize(CAPTURE_SIZE);
		visualiser.setEnabled(true);

	}*/

	/*public void startVisualiser(int AudioSessID)
	{
        visualiser = new Visualizer(AudioSessID);
		visualiser.setEnabled(false);
        visualiser.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, false);
        visualiser.setCaptureSize(CAPTURE_SIZE);
        visualiser.setEnabled(true);
	}*/

	/*@Override
	protected void onPause() {
		if (visualiser != null) {
			visualiser.setEnabled(false);
			visualiser.release();
			visualiser.setDataCaptureListener(null, 0, false, false);
		}
		super.onPause();
	}*/

	/*public void pauseVisualiser()
	{

		if (visualiser != null) {
			visualiser.setEnabled(false);
			visualiser.release();
			visualiser.setDataCaptureListener(null, 0, false, false);
		}

	}*/


	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		initThemeBehaviour(this);
		//setTheme(android.R.style.);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
			
		//String MusicDir = this.getIntent().getStringExtra("root_dir");
		// InitMainActivity();

		// final SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(this);
		// boolean hasBeenShown = prefs.getBoolean(ShowEULA.EULA_PREFIX, false);
		// // hasBeenShown = false;
		// if(hasBeenShown == false){
		// ShowEULA eula = new ShowEULA(this);
		// eula.show();
		// }
		// else
		// {
		// InitMainActivity();
		// }

		// ShowEULA eula = new ShowEULA(this);
		// eula.show();
		// InitMainActivity();

		// _con = this;
		// String MUSIC_DIR = "" ;//= "/music/comp/";
		
		//ActionBar ab = this.getActionBar();

		_FileSystemProvider = new FileSystemProvider(this);
		RequestPermissions();
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
 
		MUSIC_DIR = prefs.getString("init_dir", "");
		if (MUSIC_DIR == "" )
		{
			MUSIC_DIR = "/music/";
			MUSIC_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + MUSIC_DIR;

		//	MUSIC_DIR = this.getExternalFilesDir(null).getAbsolutePath();


		//	MUSIC_DIR = "/mnt/sdcard";
		}
		else
		{
			File f = new File(MUSIC_DIR);
			 
			if (!f.exists())//in case where sdcard removed
			{		
				MUSIC_DIR = "/music/";
				MUSIC_DIR = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + MUSIC_DIR;
			}
		}
		
		SharedPreferences priv_prefs = getPreferences(MODE_PRIVATE);

		String latest_playlist = priv_prefs.getString(LATEST_PLAYLIST, "");		
		
		int latest_playlist_song = priv_prefs.getInt(LATEST_PLAYLIST_SONG, 0);
		int latest_playlist_song_pos = priv_prefs.getInt(LATEST_PLAYLIST_SONG_POSITION, 0);

		
		String[] latest_playlistArr = latest_playlist.split(","); 
		
		
		_alLatestPlaylistLoaded = new ArrayList<String> (Arrays.asList( latest_playlistArr)) ;
		 
		_dba = new MyDB(this);
		_dba.open();
		
		Intent i = new Intent(this, MusicPlayerService.class);
		bindService(i, MusicSrvConn, Context.BIND_AUTO_CREATE);
		startService(i);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);


	
		boolean bSD_HANDLED = this.getIntent().getBooleanExtra("SD_HANDLED",false);
		if (Intent.ACTION_VIEW.equals(getIntent().getAction()) && getIntent().getData() != null)//when someone selects a file from another app like file explorer
		{			
			 
			/*	String path = getIntent().getData().getPath();
				File f = new File(path);		
			
				MUSIC_DIR = f.getParent();
				latest_playlist = "";*/

			String path = getIntent().getData().getPath();

			String lastpath =  getIntent().getData().getLastPathSegment();

			path = GetFile(path,lastpath);

			//BitmapCache bc = new BitmapCache();
			FileNode fn = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
			{
				FileNodeComposite MfilesComp = _FileSystemProvider.getmusicfilesFromContentProvider();
				if(MfilesComp!= null)
				{
					if (fn!=null)
					{
						fn = _FileSystemProvider.GetCompositeByFileName(path,MfilesComp);
						//File f = new File(path);

						//fa = new FilesAdapter(this, fn.parent.FullPath(), null, "", _dba,MusicSrvBinder);

						MUSIC_DIR = fn.parent.FullPath();
						latest_playlist = "";

					}//if (fn!=null)

				}//if(MfilesComp!= null)

			}
			else
			{

				File f = new File(path);

				MUSIC_DIR = f.getParent();
				latest_playlist = "";
			}
			 
					
		}//if (this.getIntent().getAction() == "android.intent.action.VIEW")
			
		
		//BitmapCache bc = new BitmapCache();
		fa = new FilesAdapter(this, MUSIC_DIR, null, latest_playlist, _dba,	MusicSrvBinder);
		
		RetainFragment retainFragment=null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
		{
			  retainFragment = RetainFragment.findOrCreateRetainFragment(getFragmentManager());
			  BitmapCache mMemoryCache = retainFragment.mRetainedCache;
				  	if (mMemoryCache != null)
				  		fa.bc = mMemoryCache; 			
			
		}
	  	


/*if (mMemoryCache == null)
{
    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        
    }*/

   // retainFragment.mRetainedCache = fa.bc;
//} 
		
		fileslist = (ListView) findViewById(R.id.lvSongsList);
		fileslist.setAdapter(fa);

		registerForContextMenu(fileslist);
		fa._fileslist = fileslist;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
		  retainFragment.mRetainedCache = fa.bc;
		

		if (Intent.ACTION_VIEW.equals(getIntent().getAction()) && getIntent().getData() != null)//when someone selects a file from another app like file explorer
		{			
			if(!bSD_HANDLED)
			{

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
				{

					String path = getIntent().getData().getPath();
					String lastpath =  getIntent().getData().getLastPathSegment();
					path = GetFile(path,lastpath);

					FileNodeComposite MfilesComp = _FileSystemProvider.getmusicfilesFromContentProvider();
					if (MfilesComp!= null)
					{

						FileNode fn = _FileSystemProvider.GetCompositeByFileName(path,MfilesComp);
						if (fn != null)
						{
							ArrayList<MusicFile>  mfs  = fa.SearchFiles(fn.Display(), false, true);

							if (mfs.size() > 0)
							{
								mfToPlayWhenServIsReady =  mfs.get(0);
								getIntent().putExtra("SD_HANDLED", true);
							}

						}//if (fn != null)

					}//	if (MfilesComp!= null)

				}
				else
				{
					String path = getIntent().getData().getPath();
					File f = new File(path);

					ArrayList<MusicFile>  mfs = fa.SearchFiles(f.getName(), false, true);

					if (mfs.size() > 0)
					{
						mfToPlayWhenServIsReady =  mfs.get(0);
						getIntent().putExtra("SD_HANDLED", true);
					}
				}//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)

			}//if(!bSD_HANDLED)
				
		}//if (this.getIntent().getAction() == "android.intent.action.VIEW")

		if (latest_playlist != "") {
			if (fa.mfiles.size() > latest_playlist_song)
				if (latest_playlist_song >= 0) {
					
					//fa.SetSong(latest_playlist_song, latest_playlist_song_pos,false);
					MusicFile mfile = fa.mfiles.get(latest_playlist_song);					 
					mfile.CurrentPosition = latest_playlist_song_pos;
					//mfToPlayWhenServIsReady = mfile;
				 
				}
		}		 
	 
		fileslist.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				// TODO Auto-generated method stub
				fa.PlayStop(v, pos, id);
			}
		});

		/*
		 * if (savedInstanceState != null) {
		 * 
		 * if (savedInstanceState.get(CURRENT_SONG) != null) { int pos =
		 * savedInstanceState.getInt(CURRENT_SONG);
		 * fa.HandleViewItem(pos,ViewAppearanceMode.STOPPED ); }
		 * 
		 * }
		 */

		Button btPlay = (Button) findViewById(R.id.btPlay);
		btPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					fa.PlayStop();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		/*
		 * Button btShow = (Button) findViewById(R.id.btChooseDir);
		 * btShow.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { Intent i = new Intent(MainActivity.this,
		 * DirectoryChooserActivity.class);
		 * startActivityForResult(i,GET_DIRECTORY); } } );
		 */

		Button btPrev = (Button) findViewById(R.id.btPrevious);
		btPrev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fa.PlayPrevious();
			//	updateSeekBar(false);//went to prepared
			}
		});

		Button btNext = (Button) findViewById(R.id.btNext);
		btNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fa.PlayNext();
			//	updateSeekBar(false);//went to prepared

			//	int i = MusicSrvBinder.getRandomNumber();

				//Toast.makeText(getApplicationContext(),
						//" from MainActivity " + i, Toast.LENGTH_LONG).show();
			}
		});

		Button btBackward = (Button) findViewById(R.id.btBackward);
		if (btBackward != null) {
			btBackward.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					fa.GoBackward();
					updateSeekBar(false);

					// unbindService(MusicSrvConn);
				}
			});

			btBackward.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					fa.GoToTheStart();
					updateSeekBar(false);
					return false;
				}
			});

		}// if (btBackward != null)

		Button btForward = (Button) findViewById(R.id.btForward);
		if (btForward != null) {
			btForward.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					// String locale =
					// _con.getResources().getConfiguration().locale.getDisplayName();
					// String lan =
					// _con.getResources().getConfiguration().locale.getLanguage();
					// String coun =
					// _con.getResources().getConfiguration().locale.getCountry();
					// Toast.makeText(_con, lan + "  " + coun,
					// Toast.LENGTH_LONG).show();

					fa.GoForward();
					updateSeekBar(false);
					
				// 	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					// Specify free form input
					/*intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "or forever hold your peace");
					intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
					startActivityForResult(intent, VOICE_RECOGNITION); */ 
					
					/*	if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK) {
					ArrayList<String> results;
					results =
					data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					float[] confidence;
					String confidenceExtra = RecognizerIntent.EXTRA_CONFIDENCE_SCORES;
					confidence =
					data.getFloatArrayExtra(confidenceExtra);
					// TODO Do something with the recognized voice strings
					}*/
					
					//   <!-- uses-permission android:name="android.permission.RECORD_AUDIO" /-->
				 	/* SpeechRecognizer sr = 	android.speech.SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
					
					sr.setRecognitionListener(new RecognitionListener() {

						public void onReadyForSpeech(Bundle params) {
							// TODO Auto-generated method stub
							if(true)
							{
								
							}
						}

						public void onBeginningOfSpeech() {
							// TODO Auto-generated method stub
							if(true)
							{
								
							}
						}

						public void onRmsChanged(float rmsdB) {
							// TODO Auto-generated method stub
							
						}

						public void onBufferReceived(byte[] buffer) {
							// TODO Auto-generated method stub
							
						}

						public void onEndOfSpeech() {
							// TODO Auto-generated method stub
							
							if(true)
							{
								
							}
							
						}

						public void onError(int error) {
							// TODO Auto-generated method stub
							
							if(true)
							{
								
							}
						}

						public void onResults(Bundle results) {
							// TODO Auto-generated method stub
							
							ArrayList<String>  res=	results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
							
							if(res != null)
							{
								String re = res.get(0);
								Toast.makeText(getApplicationContext(),re ,Toast.LENGTH_LONG).show();
							}
								
								
							
						}

						public void onPartialResults(Bundle partialResults) {
							// TODO Auto-generated method stub
							
						}

						public void onEvent(int eventType, Bundle params) {
							// TODO Auto-generated method stub
							
						}
						 
						
					});
					
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					// Specify free form input
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					
					intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
					//intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
					intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
					
				
					sr.startListening(intent); */
					//sr.stopListening();*/
					
					
								
					// Intent i = new
					// Intent(getApplicationContext(),MusicPlayerService.class);
					// bindService( i,MusicSrvConn, Context.BIND_AUTO_CREATE);
				}
			});

		}// if (btForward != null)

		Button btMenu = (Button) findViewById(R.id.btMenu);

		// btMenu.setCompoundDrawablesWithIntrinsicBounds(_con.getResources().getDrawable(android.R.drawable.ic_menu_view),
		// null, null, null);

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		// btMenu.setVisibility(Button.INVISIBLE);
		// else
		btMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openOptionsMenu();
			}
		});

		txtCurrent = (TextView) findViewById(R.id.txtCurrent);
		txtTotal = (TextView) findViewById(R.id.txtTotal);
		SeekBarSong = (SeekBar) findViewById(R.id.seekBarSong);

		if (SeekBarSong != null) {

			SeekBarSong.setMax(1000);
			SeekBarSong.setProgress(10);
			SeekBarSong.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							
							if (fromUser)
								if (MusicSrvBinder._mp != null) {
									MusicSrvBinder._mp.seekTo(progress);
									MusicSrvBinder._nowplaying.CurrentPosition = progress;
									seekBar.setProgress(progress);
									updateSeekBar(progress);
								}
						}

						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

					});
			
		
			updateSeekBar(true);
			initScreenBehaviour();

		}// if (SeekBarSong != null)

		/*
		 * if (this.getLastCustomNonConfigurationInstance() != null)//the
		 * updating of the dialog does not work, the code in the thrads work but
		 * i do not see any update { ArrayList<Object> al = (ArrayList<Object>)
		 * getLastCustomNonConfigurationInstance();
		 * 
		 * if ( al.get(0) instanceof Thread) { this.savingthread_f =(Thread)
		 * al.get(0);
		 * 
		 * if (savingthread_f.isAlive()) { //Dialog dlg =
		 * CreateSavePlaylistProgressBarDialog(); //dlg.show(); //progressBarDlg
		 * = dlg; // mProgress.setMax(fa.mfiles.size()); }
		 * 
		 * }
		 * 
		 * }
		 */

		// ActivityManager am = (ActivityManager)
		// getApplicationContext().getSystemService(ACTIVITY_SERVICE);
		// int mem = am.getMemoryClass();

		boolean ShowVisualizer = prefs.getBoolean("ShowVisualizer", true);
		 waveformView = (WaveformView) findViewById(R.id.wfvMain);

		 if(!ShowVisualizer)
		 {
		 	LinearLayout llWaveForm = (LinearLayout) findViewById(R.id.llWaveForm);
			 llWaveForm.setVisibility(View.GONE);
		 }

	//	WebformRendererFactory rendererFactory = new WebformRendererFactory();
		//waveformView.setRenderer(rendererFactory.createSimpleWaveformRenderer(Color.BLUE, Color.WHITE));




	} // onCreate

	private void RequestPermissionsOld()
	{
		if (Build.VERSION.SDK_INT < 23)
		{
			return;
		}


		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}


	}//RequestPermissions
	
	//DIFFERENT API APK
	private void RequestPermissions()
	{		
		if (Build.VERSION.SDK_INT < 23)
		{
			return;			
		}


		boolean bWriteExStorageNotGranted = ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED;

		boolean breadExStorageNotGranted = ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED;

		boolean bRecordAudioNotGranted = ContextCompat.checkSelfPermission(this,
				Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED;


		// Here, thisActivity is the current activity
		if (bWriteExStorageNotGranted || bRecordAudioNotGranted || breadExStorageNotGranted ) {

		    // Should we show an explanation?
		    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
		            Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
					ActivityCompat.shouldShowRequestPermissionRationale(this,
							Manifest.permission.READ_EXTERNAL_STORAGE) ||
					ActivityCompat.shouldShowRequestPermissionRationale(this,
							Manifest.permission.RECORD_AUDIO)    ) {

		        // Show an expanation to the user *asynchronously* -- don't block
		        // this thread waiting for the user's response! After the user
		        // sees the explanation, try again to request the permission.

			/*	Snackbar.make(getActivity().findViewById(android.R.id.content),
						"Please Grant Permissions to upload profile photo",
						Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								requestPermissions(
										new String[]{Manifest.permission
												.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
										PERMISSIONS_MULTIPLE_REQUEST);
							}
						}).show();*/



		    } else {

		        // No explanation needed, we can request the permission.

		        ActivityCompat.requestPermissions(this,
		                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO },
		                MY_PERMISSIONS_PERMISSIONS_MULTIPLE_REQUEST);

		        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
		        // app-defined int constant. The callback method gets the
		        // result of the request.
		    }
		}

	/*	if (Build.VERSION.SDK_INT > 29)
		{
			if (Environment.isExternalStorageManager()) {
				//todo when permission is granted
			} else {
				//request for the permission
				Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
				Uri uri = Uri.fromParts("package", getPackageName(), null);
				intent.setData(uri);
				startActivity(intent);
			}
		}*/
			
		
	}//RequestPermissions
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRequestPermissionsResult(int, java.lang.String[], int[])
	 */
	/*@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		 switch (requestCode) {
	        case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
	            // If request is cancelled, the result arrays are empty.
	            if (grantResults.length > 0
	                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

	                // permission was granted, yay! Do the
	                // contacts-related task you need to do.

	            } else {

	                // permission denied, boo! Disable the
	                // functionality that depends on this permission.
	            }
	            return;
	        }

	        // other 'case' lines to check for other
	        // permissions this app might request
	    }
	}*/

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case MY_PERMISSIONS_PERMISSIONS_MULTIPLE_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0) {

					boolean bWriteExStorageGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
					boolean bRecordAudioGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}


	public static String GetThemeBehaviour(Context con)
	{  	 
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) 
			return "";
		
			 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);			        
			 String  selectedTheme = prefs.getString( "settings_theme", "");
			   				
			String[]  selectedThemeArr =    selectedTheme.split(",");  
			if (selectedThemeArr != null)
					if (selectedThemeArr.length > 0)
						selectedTheme = selectedThemeArr[0];
				
			 if (selectedTheme.equals("0"))	
				return "white";
			
			 if (selectedTheme.equals("1"))				 				
				 return "black";
			 
			return "";
			  
	}	
	
	public static void initThemeBehaviour(Context con)
	{  	 
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) 
			return;
		
			 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);			        
			 String  selectedTheme = prefs.getString( "settings_theme", "");
			   				
			String[]  selectedThemeArr =    selectedTheme.split(",");  
			if (selectedThemeArr != null)
					if (selectedThemeArr.length > 0)
						selectedTheme = selectedThemeArr[0];
				
			 if (selectedTheme.equals("0"))	
				 con.setTheme(R.style.AppTheme);
			
			 if (selectedTheme.equals("1"))				 				
				 con.setTheme(R.style.AppThemeBlack);	
			  
	}	
	
	
	public static void ApplyTextSize(Context con, TextView tv) 
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
		
		 int  TextSize = prefs.getInt("text_size", 12);
		 tv.setTextSize(TextSize);
	}
	
	public static void ApplySelectorsSize(Context con, CompoundButton mCheck) throws Exception
	{
		
		 String SelectorType = "";
		 if (mCheck instanceof CheckBox )
		 {
			 SelectorType = "check";
		 }
		 else if (mCheck instanceof RadioButton)
		 {
			 SelectorType = "radio";
		 }
		 else
		 {			 
			 throw new Exception("invalid selector type");
		 }
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
		
		
		//problem when nothing was entered
		String  SelectorSizeString = prefs.getString("settings_selectorSize", "");
		String[]  selectedThemeArr =    SelectorSizeString.split(",");  
		if (selectedThemeArr != null)
				if (selectedThemeArr.length > 0)
					SelectorSizeString = selectedThemeArr[0];
		
		int  SelectorSize =Integer.parseInt(SelectorSizeString);
		
		
		if (MainActivity.GetThemeBehaviour(con) == "black")
		{								
			
			 switch (SelectorSize) {
	            case 1:  
	            	
	            	if  (SelectorType == "check")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.checkbox_small_black);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_small_black);
	            	}
	            	
	            	
	                break;
	                     
	            case 2: 
	            	
	            	if  (SelectorType == "check")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.checkbox_medium_black);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_medium_black);
	            	}
	            	
	            	
	                break;
	                     
	            case 3:
	            	
	            	
	            	if  (SelectorType == "check")
	            	{
	            	 	mCheck.setButtonDrawable(R.drawable.checkbox_large_black);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_large_black);
	            	}
	            	
	           
	                break;
	                     
	            case 4: 
	            	
	            	if  (SelectorType == "check")
	            	{
	                 	mCheck.setButtonDrawable(R.drawable.checkbox_extralarge_black);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_extralarge_black);
	            	}
	       
	                break;
	          
	     
	            default:
	            	
	            	
	            	//mCheck.setButtonDrawable(R.drawable.checkbox_extralarge_black);
	                break;
	                
	        }				
			
			//holder.mCheck.setButtonDrawable(R.drawable.checkbox_extralarge_black);
			
					
		}
		else
		{
			
			 switch (SelectorSize) {
	            case 1:  
	            	
	            	if  (SelectorType == "check")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.checkbox_small);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_small);
	            	}
	            	
	            	
	                break;
	                     
	            case 2: 
	            	
	            	if  (SelectorType == "check")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.checkbox_medium);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_medium);
	            	}
	            	
	            
	                break;
	                     
	            case 3:
	            	
	            	if  (SelectorType == "check")
	            	{
	            	  	mCheck.setButtonDrawable(R.drawable.checkbox_large);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_large);
	            	}
	            	
	          
	                break;
	                     
	            case 4: 
	            	
	            	if  (SelectorType == "check")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.checkbox_extralarge);
	            	}
	            	else if  (SelectorType == "radio")
	            	{
	            		mCheck.setButtonDrawable(R.drawable.radiobutton_extralarge);
	            	}
	            	
	                break;
	          
	     
	            default:
	            	
	            	//holder.mCheck.setButtonDrawable(R.drawable.checkbox_extralarge);
	                break;
	                
	        }
		}
			
			//holder.mCheck.setButtonDrawable(R.drawable.checkbox_extralarge);
		
		//holder.mCheck.setScaleX(2f);
		//holder.mCheck.setScaleY(2f);
		
		 // Get the margins of Flex CheckBox
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mCheck.getLayoutParams();

        // Set left, top, right and bottom margins of Flex CheckBox
        mlp.setMargins(25,15,10,10);
        mCheck.setLayoutParams(mlp);

        // Apply right padding of Flex CheckBox
        mCheck.setPadding(0,0,50,0);		
		
		
		
	}

	public void initScreenBehaviour( )
	{  
		 
		 //<uses-permission android:name="android.permission.WAKE_LOCK" />
		// _mpp.mp.setWakeMode(_con, PowerManager.PARTIAL_WAKE_LOCK);//needs permission, throws exception at runtime
		// _mpp.mp.setWakeMode(_con, PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime			 
				 
			 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);			        
			   String  ScreenBeh = prefs.getString( "settings_screen", "");
			   		  
			   Window w = this.getWindow(); // in Activity's onCreate() for instance
			   w.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
			   //_mpp.mp.(_con,  PowerManager.ACQUIRE_CAUSES_WAKEUP);//needs permission, throws exception at runtime
			//_mpp.mp.setWakeMode(_con,  PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime   
		 
		   				
			String[]  ScreenBehArr =    ScreenBeh.split(",");  
			if (ScreenBehArr != null)
					if (ScreenBehArr.length > 0)
						ScreenBeh = ScreenBehArr[0];
						
			 if (ScreenBeh.equals("2")  )				 				
				 w.setFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  
				         WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);					
							 
			// if (ScreenBeh == "1")				  
			//	_mp.setWakeMode(this,  PowerManager.SCREEN_DIM_WAKE_LOCK);//needs permission, throws exception at runtime
				 
	}		
	
	Runnable run = new Runnable() {

		public void run() {
			updateSeekBar(true);
		}
	};

	
	
	private void updateSeekBarFallBack()
	{
		try
		{	
			if (MusicSrvBinder != null)
			{
				if  (MusicSrvBinder._nowplaying != null) 
				{
					if ( MusicSrvBinder._nowplaying.bInError != 1 )
					{
						SeekBarSong.setMax(MusicSrvBinder._mp.getDuration());
						SeekBarSong.setProgress(MusicSrvBinder._mp.getCurrentPosition());					
					}								
				}				
			}
			
			    
				DecimalFormat SecondsFormatter = new DecimalFormat("00");

				int current = 0;
				int curpos = 0;
				//getformat fucks the app completely!!!				
				//MusicSrvBinder._mp.getTrackInfo()[0].getFormat()//throws an exception
				//if the music file is not music (pdf) and crashes the application
				if (MusicSrvBinder != null)
				{
					if   (MusicSrvBinder._nowplaying != null) 
					{
						if ( MusicSrvBinder._nowplaying.bInError != 1 )
						{
							curpos = MusicSrvBinder._mp.getCurrentPosition();	
						}					
					}	
				}
				
				
				//if (curpos != 0)//0/1000 does not return 0
				//{
					current = curpos / 1000;
				//}
					
				
				int curmin = current / 60;
				int cursec = current % 60;

				String textCurrent = Integer.toString(curmin) + ":"
						+ SecondsFormatter.format(cursec);			 

				int total = 0;
				int dur = 0;
				
				if (MusicSrvBinder != null)
				{
					if (MusicSrvBinder._nowplaying != null) 
					{
						if ( MusicSrvBinder._nowplaying.bInError != 1 )
						{
							dur = MusicSrvBinder._mp.getDuration() ;	
						}
							
					}	
				}
				
				
				//if (dur != 0)
				//{
					total = dur / 1000;
			//	}
					
				
				int totalmin = total / 60;
				int totalsec = total % 60;

				String textTotal = Integer.toString(totalmin) + ":"
						+ SecondsFormatter.format(totalsec);
				
				txtCurrent.setText(textCurrent);
				txtTotal.setText(textTotal);
										
		}
		catch(Exception ex)
		{				
			try
			{
				ClearSeekBar();

			}
			catch( Exception ex1)
			{
				
			}
		}	
		
	}//updateSeekBarFallBack 
	
	public void updateSeekBar(int progress)
	{
		//SeekBarSong.setMax(MusicSrvBinder._mp.getDuration());
		//SeekBarSong.setProgress(MusicSrvBinder._mp.getCurrentPosition());

		DecimalFormat SecondsFormatter = new DecimalFormat("00");

		int current = progress / 1000;
		int curmin = current / 60;
		int cursec = current % 60;

		String textCurrent = Integer.toString(curmin) + ":"
				+ SecondsFormatter.format(cursec);			 

		//int total = MusicSrvBinder._mp.getDuration() / 1000;
		//int totalmin = total / 60;
		//int totalsec = total % 60;

		//String textTotal = Integer.toString(totalmin) + ":"
				//+ SecondsFormatter.format(totalsec);
		
		txtCurrent.setText(textCurrent);
		//txtTotal.setText(textTotal);
		
		
		
	}
	
	public void ClearSeekBar()
	{
		SeekBarSong.setMax(0);
		SeekBarSong.setProgress(0);

		txtCurrent.setText("0:00");
		txtTotal.setText("0:00");		
	}



	public void updateSeekBar(boolean bRecursive)  
	{

		if (SeekBarSong != null)
		{
			try
			{		
				if(MusicSrvBinder != null)
				{
					
					boolean bGoOn = false;
					boolean bClearValues = false;
					if(bRecursive)
					{
						if ( MusicSrvBinder._mp != null && MusicSrvBinder._mp.isPlaying())
						{
							bGoOn = true;
						}
						else
						{
							
						}
					}
					else
					{//looking for paused
						
						
						if (MusicSrvBinder._nowplaying == null)
						{
							bClearValues = true;							
						}					
						else if ( MusicSrvBinder._mp != null
								&& MusicSrvBinder._nowplaying.bInError == 0) //fucks the media player//attempt to call getduration without a valid media player
						{
							bGoOn = true;
						}
						else//in error
						{
							bClearValues = true;
						}
					}
					
					
					if (bGoOn)
					{
				
						SeekBarSong.setMax(MusicSrvBinder._mp.getDuration());
						SeekBarSong.setProgress(MusicSrvBinder._mp.getCurrentPosition());

						DecimalFormat SecondsFormatter = new DecimalFormat("00");

						int current = MusicSrvBinder._mp.getCurrentPosition() / 1000;
						int curmin = current / 60;
						int cursec = current % 60;

						String textCurrent = Integer.toString(curmin) + ":"
								+ SecondsFormatter.format(cursec);			 

						int total = MusicSrvBinder._mp.getDuration() / 1000;
						int totalmin = total / 60;
						int totalsec = total % 60;

						String textTotal = Integer.toString(totalmin) + ":"
								+ SecondsFormatter.format(totalsec);
						
						txtCurrent.setText(textCurrent);
						txtTotal.setText(textTotal);
					}
					else if(bClearValues)
					{
						//updateSeekBarFallBack();
						
					
					}
				}
				else
				{
					ClearSeekBar();
				}
				
				
			}
			catch(Exception ex2)
			{			 
				try
				{
				//	updateSeekBarFallBack();	
				}
				catch(Exception exf)
				{
					
				}
				
			}		 
			 
				
			//}//(MusicSrvBinder != null && MusicSrvBinder._mp != null

			if (bRecursive)
				seekHandler.postDelayed(run, 1000);
		}//(SeekBarSong != null) {

	}

	private void handleIntent(Intent intent)  {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			ArrayList<MusicFile> mfiles = fa.SearchFiles(query, true, false);

			if(mfiles != null)
				if ( mfiles.size() > 0)
				{
					Intent i = new Intent(MainActivity.this,
							ResultsPickerActivity.class);
					i.putParcelableArrayListExtra("search_results", mfiles);
					startActivityForResult(i, SEARCH_SONGS);
				}// if (mfiles != null)

		}// if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		
		//if (this.getIntent().getAction() == "android.intent.action.VIEW")//this does not work on 2.3.3
		if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null)
		{

			/*
			api 29 - /document/100A-3102:Music/110.20-02-22.mp3

			api30 - /external/audio/media/5520   samsung
			 */

			/*
			api 29 - /document/100A-3102:Music/110.20-02-22.mp3

			api30 - /external/audio/media/5520   samsung

			api30 - /document/15FA-4506:Music/skai/27.12-07-20.mp3  emulator - work

			api31 - /document/audio:59   emulator - work

			same machine
			api32 - /document/1015-1E1C:Music/16 - We Will Rock You.mp3 - work
			api32 - /document/audio:33

			Api28
			/document/audio:46
			/document/primary:Music/01 - A Kind Of Magic.mp3

			 */

			String path = intent.getData().getPath();
			String lastpath = intent.getData().getLastPathSegment();
			//String path = getIntent().getData().getPath();

		//	File filePath = new File(this.getContentResolver().openFileDescriptor(getIntent().getData(), "r"));
			path = GetFile(path,lastpath);
			
			//BitmapCache bc = new BitmapCache();
			FileNode fn = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
			{
				FileNodeComposite MfilesComp = _FileSystemProvider.getmusicfilesFromContentProvider();
				if(MfilesComp==null)
					return;

				  fn = _FileSystemProvider.GetCompositeByFileName(path,MfilesComp);
				  if(fn==null)
				  	return;

				//File f = new File(path);
				fa = new FilesAdapter(this, fn.parent.FullPath(), null, "", _dba,MusicSrvBinder);

			}
			else
			{
				File f = new File(path);
				fa = new FilesAdapter(this, f.getParent(), null, "", _dba,MusicSrvBinder);
			}


			//fa = new FilesAdapter(this, f.getParent(), null, "", _dba,MusicSrvBinder);
			fileslist = (ListView) findViewById(R.id.lvSongsList);
			fileslist.setAdapter(fa);
		

			fa._fileslist = fileslist;
			ArrayList<MusicFile>  mfs = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
			{
				  mfs = fa.SearchFiles(fn.Display(), false, true);
			}
			else
			{
				File f = new File(path);
		  		mfs = fa.SearchFiles(f.getName(), false, true);
			}

			if (mfs.size() > 0)
			{						
				//int idx =  fa.mfiles.indexOf(mfs.get(0));
				//fa._fileslist.setSelection(idx);
				//fa._nowplaying = mfs.get(0);
				//MusicSrvBinder._nowplaying = mfs.get(0); 
				//fa.PlayStop();
				fa.PlayStop(null, mfs.get(0),true);
				
				fa.notifyDataSetChanged();
				intent.putExtra("SD_HANDLED", true);
			}
				
		}//if (this.getIntent().getAction() == "android.intent.action.VIEW")		
		
		
	}// handleIntent

	private String GetFile_WithOutColon(String path, String lastpath)
	{
		File f = new File(path);

		if (f.exists())
 				return path;

			String selection = "_id=?";
			String[] selectionArgs = new String[]{
					lastpath};


			Cursor cursor = this.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null,
					selection,
					selectionArgs,
					null);

			while (cursor.moveToNext())
			{
				String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

				String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

				String kk = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

				return fullpath;

			}

		return "";
	}


	private String GetFile(String path,String lastpath)
	{

		if (!path.contains(":"))
			return GetFile_WithOutColon(path, lastpath);

		String[] split = path.split(":");


		String selectionID = "_id=?";
		String[] selectionArgsID = new String[]{
				split[1]};


		Cursor cursorID = this.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null,
				selectionID,
				selectionArgsID,
				null);

		while (cursorID.moveToNext())
		{
			String title = cursorID.getString(cursorID.getColumnIndex(MediaStore.Audio.Media.TITLE));

			String fullpath = cursorID.getString(cursorID.getColumnIndex(MediaStore.Audio.Media.DATA));

			String kk = cursorID.getString(cursorID.getColumnIndex(MediaStore.Audio.Media._ID));

			return fullpath;

		}


		String selectionPath = "_data Like ? ";
		String[] selectionArgsPath = new String[]{
				"%" + split[1] + "%"};


		Cursor cursorPath = this.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null,
				selectionPath,
				selectionArgsPath,
				null);

		while (cursorPath.moveToNext())
		{
			String title = cursorPath.getString(cursorPath.getColumnIndex(MediaStore.Audio.Media.TITLE));

			String fullpath = cursorPath.getString(cursorPath.getColumnIndex(MediaStore.Audio.Media.DATA));

			String kk = cursorPath.getString(cursorPath.getColumnIndex(MediaStore.Audio.Media._ID));

			return fullpath;

		}

		return "";
	}

	private String GetFile_Old(String path)
	{
		String[] split = path.split(":");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
		{


			String selection = "_id=?";
			String[] selectionArgs = new String[]{
					split[1]};


			Cursor cursor = this.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null,
					selection,
					selectionArgs,
					null);

			while (cursor.moveToNext())
			{
				String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

				String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

				String kk = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

				return fullpath;

			}

		}
		else
		{
			return path;
		}

		return "";
	}


	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fa.notifyDataSetChanged();
		// fa.HandleViewItem(_prevpos,ViewAppearanceMode.STOPPED );
	}

	@Override
	protected void onStart() {
		super.onStart();
			
		/*if (this.getIntent().getAction() == "android.intent.action.VIEW")
		{			
			String path = getIntent().getData().getPath();
			File f = new File(path);			
			
			fa = new FilesAdapter(this, f.getParent(), null, "", _dba,
					MusicSrvBinder);
			fileslist = (ListView) findViewById(R.id.lvSongsList);
			fileslist.setAdapter(fa);
		
			fa._fileslist = fileslist;
			
			ArrayList<MusicFile>  mfs = fa.SearchFiles(f.getName(), false, true);
			if (mfs.size() > 0)
			{						
				//int idx =  fa.mfiles.indexOf(mfs.get(0));
				//fa._fileslist.setSelection(idx);
				fa._nowplaying = mfs.get(0);
				MusicSrvBinder._nowplaying = mfs.get(0); 
				fa.PlayStop();
			}
				
		}*/	
		
		updateSeekBar(true);
		
		if (fa._nowplaying != null)
		{
			int idx =  fa.mfiles.indexOf(fa._nowplaying);
			fa._fileslist.setSelection(idx);			
		}
		
		fa.notifyDataSetChanged();


		/*if (!mBoundSrv) {
			Intent i = new Intent(this, MusicPlayerService.class);
			bindService(i, MusicSrvConn, Context.BIND_AUTO_CREATE);
			startService(i);	
			
			mBoundSrv = true;
		}*/
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		if (mBoundSrv  ) {
			 
			unbindService(MusicSrvConn);
			mBoundSrv = false;
			
			Intent i = new Intent(this, MusicPlayerService.class);			 
			stopService(i);	
		
		}
		
		
	}

	@Override
	protected void onStop() {
		super.onStop();

		/*if (mBoundSrv  ) {
			 
				unbindService(MusicSrvConn);
				mBoundSrv = false;	 
			
		}*/
			
		
		/*if (mBoundSrv && MusicSrvBinder != null && MusicSrvBinder._mp != null) {
			if (MusicSrvBinder._mp.isPlaying())
			{
				unbindService(MusicSrvConn);
				mBoundSrv = false;	
			}
			else
			{
				fa._nowplaying = MusicSrvBinder._nowplaying;
				//fa._nowplaying.CurrentPosition = MusicSrvBinder._mp.getCurrentPosition();
						
				unbindService(MusicSrvConn);
				Intent i = new Intent(this, MusicPlayerService.class);			 
				stopService(i);				
				mBoundSrv = false;
			}
			
		}*/
		
		if (seekHandler != null)
		{
			seekHandler.removeCallbacks(run);
		}
		
	}

	/*@Override
	protected void onPause() {
		super.onPause();
	}*/

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
	 */
	
	
	
	/*@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 
			
		 
		 switch(keyCode){
		  
		   case KeyEvent.KEYCODE_VOLUME_UP:
		    // event.startTracking();
			fa.PlayNext();
		     return false;
		   case KeyEvent.KEYCODE_VOLUME_DOWN:
			   fa.PlayPrevious();
		     return false;
		 }
		 
		return super.onKeyLongPress(keyCode, event);
	}*/

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		 switch(keyCode){
		  
		   case KeyEvent.KEYCODE_VOLUME_UP:
		    // event.startTracking();
			fa.PlayNext();
		     return false;
		   case KeyEvent.KEYCODE_VOLUME_DOWN:
			   fa.PlayPrevious();
		     return false;
		 }
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SHOW_PREFERENCES)
		{
			 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			 {
				 recreate();
			 }
			
		 }// (requestCode == SHOW_PREFERENCES) {
		
		if (requestCode == GET_DIRECTORY) {
			if (resultCode == RESULT_OK) {
				String sel = data.getStringExtra("singer");

				Toast.makeText(this, sel, Toast.LENGTH_LONG).show();

			} else if (resultCode == RESULT_CANCELED) {

			}
		}// (requestCode == GET_DIRECTORY) {

		if (requestCode == GET_SONGS) {

			if (resultCode == RESULT_OK) {
					
							
				ArrayList<Parcelable> selectedsongs = data
						.getParcelableArrayListExtra("songs_list");

				
				if (selectedsongs != null)
				{
					for (Object omf : selectedsongs.toArray()) {
						MusicFile typedmf = (MusicFile) omf;
						File fu = new File(typedmf.filepath);
						if (!fu.isDirectory()) {
							fa.mfiles.add(typedmf);
						}

					}				
					
				}
				else
				{
					
					
					MyApplicationObject mApp = (MyApplicationObject)getApplicationContext();					
					ArrayList<MusicFile> selectedsongsFromCache  = mApp.getSelectedFiles();
					
				//	ArrayList<MusicFile> selectedsongsFromCache  = GetSelectedFilesFromFile();
					
					
						for (Object omf : selectedsongsFromCache.toArray()) {
							MusicFile typedmf = (MusicFile) omf;

						if (typedmf != null)
						{
							if(typedmf.filepath != null)
							{
								File fu = new File(typedmf.filepath);
								if (!fu.isDirectory()) {
									fa.mfiles.add(typedmf);
								}
							}//if(typedmf.filepath != null)
						}//	if (typedmf != null)

					}		
					
					//mApp.setSelectedFiles(null);
					
				}			
				
				Collections.sort(fa.mfiles);
 
				fileslist = (ListView) findViewById(R.id.lvSongsList);
				fileslist.setAdapter(fa);
				fa._fileslist = fileslist;

			} else if (resultCode == RESULT_CANCELED) {

			}

		} // (requestCode == GET_SONGS)

		if (requestCode == GET_PLAYLIST) {//2104978481

			if (resultCode == RESULT_OK) {
				ArrayList<String> selectedplaylists = data
						.getStringArrayListExtra("playlist_list");

				if (!(selectedplaylists == null))
					if (selectedplaylists.size() > 0) {
						fa.getmusicfilesFromPlaylist(selectedplaylists);
						StoreLatestPLaylistLoaded(selectedplaylists);
					}

			} else if (resultCode == RESULT_CANCELED) {

			}

		} // (requestCode == GET_SONGS)

		if (requestCode == SEARCH_SONGS) {

			if (resultCode == RESULT_OK) {
				MusicFile mf = data.getParcelableExtra("chosen_song");

				if (!(mf == null))
					fa.SearchFiles(mf.filepath, true, true);

			} else if (resultCode == RESULT_CANCELED) {

			}

		} // (requestCode == SEARCH_SONGS)

		if (requestCode == DELETE_PLAYLISTS) {

			if (resultCode == RESULT_OK) {
				ArrayList<String> pls = data
						.getStringArrayListExtra("playlist_list");
				fa.DeletePlaylists(pls);
			} else if (resultCode == RESULT_CANCELED) {

			}

		} // (requestCode == DELETE_PLAYLISTS)
		
	/*	if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)
		{
				ArrayList<String> results;
				results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				float[] confidence;
				String confidenceExtra = RecognizerIntent.EXTRA_CONFIDENCE_SCORES;
				confidence = data.getFloatArrayExtra(confidenceExtra);
				
				
	
		} */

	}// onActivityResult

	
	@SuppressWarnings("resource")
	private ArrayList<MusicFile> GetSelectedFilesFromFile()
	{
		
		FileInputStream fin = null;
		File file = new File("selected_files.dd");
		try {
			fin = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] data = new byte[(int) file.length()];
		
		
		try {
			fin.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.d(TAG, "file size: " + file.length());

		//fin = new FileInputStream(file);
		//byte[] data = new byte[(int) file.length()];
		
		
		
		Parcel parcel = Parcel.obtain();
		parcel.unmarshall(data, 0, data.length);

		ArrayList<MusicFile> rawList = parcel.readArrayList(MusicFile.class.getClassLoader());
		
		
		return rawList;
		
	//	return null;	
		
	}

	
	private void StoreLatestPLaylistLoaded(ArrayList<String> playlist)
	{
		_alLatestPlaylistLoaded = playlist;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		/*
		 * if ( (mpp != null) && !(mpp.mp == null)) { mpp.mp.release();
		 * mpp.mp=null; }
		 */
		super.onDestroy();
		
		int cur_pos = 0;
		if(MusicSrvBinder != null)
		{
			if (MusicSrvBinder._mp != null)
			{
				if (MusicSrvBinder._mp.isPlaying())
				{
					cur_pos = MusicSrvBinder._mp.getCurrentPosition();									
				}					
			}							
		}
	
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Editor ed = prefs.edit();

		
		//ed.putString(LATEST_PLAYLIST, this._LatestPlaylistLoaded);
		
		
		String LatestPlaylistLoaded = "";
		
		 for(int i=0; i<_alLatestPlaylistLoaded.size(); i++)
		 {			 
			if ( i == 0)
				 LatestPlaylistLoaded =  _alLatestPlaylistLoaded.get(i);
			else 
				 LatestPlaylistLoaded = LatestPlaylistLoaded +  ","  + _alLatestPlaylistLoaded.get(i) ;
			
		 }
		 
		ed.putString(LATEST_PLAYLIST, LatestPlaylistLoaded);
		
		
		
		
		if ((fa != null) && this.fa._nowplaying != null)
		{
			ed.putInt(LATEST_PLAYLIST_SONG,
					this.fa.mfiles.indexOf(this.fa._nowplaying));
			
			ed.putInt(LATEST_PLAYLIST_SONG_POSITION,
					cur_pos);
		}

		ed.commit();		
		
		if (mBoundSrv) {
			unbindService(MusicSrvConn);
			mBoundSrv = false;
		}

		MusicSrvBinder = null;
		fa._MusicSrvBinder = null;

		//if (fa != null)
		//	unregisterReceiver(fa.mMessageReceiver);

		// LocalBroadcastManager.getInstance(this).unregisterReceiver(fa.mMessageReceiver);
		// _prevpos=-1;
		if (fa != null)
			fa.DeleteNotification();
		 

		if (progressBarDlg != null)
			progressBarDlg.dismiss();

		

		fa._fileslist = null;
		fa = null;
		fileslist = null;

		seekHandler.removeCallbacks(run);
		seekHandler = null;

		SeekBarSong = null;
		txtCurrent = null;
		txtTotal = null;

		_dba.close();




	} // onDestroy

	public void SetPaused() {
		Button btPlay = (Button) findViewById(R.id.btPlay);
		btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.pause), null, null, null);
		
		
		
		
		
	}
	
	
public void SetPlayed2() {
	
		
		
		
		
		//LinearLayout ll = (LinearLayout) findViewById(R.id.llButtons);
		//ll.setw
		
		
		Button btPlay = (Button) findViewById(R.id.btPlay);
		//btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.play_128_t,null), null, null, null);
		//btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.play_128_t), null, null, null);
		btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.play), null, null, null);
		
		
		
		Button btNext = (Button) findViewById(R.id.btNext);
	//	btNext.setCompoundDrawablesWithIntrinsicBounds(getResources()
			//	.getDrawable(R.drawable.next_128,null), null, null, null);
	//	btNext.setVisibility(View.GONE);  
		
		
		
		
		
		
		Button btPrevious = (Button) findViewById(R.id.btPrevious);
		//btPrevious.setCompoundDrawablesWithIntrinsicBounds(getResources()
		//		.getDrawable(R.drawable.previous_128,null), null, null, null);
		
		
		
		Button btForward = (Button) findViewById(R.id.btForward);
	//	btForward.setCompoundDrawablesWithIntrinsicBounds(getResources()
		//		.getDrawable(R.drawable.previous_128,null), null, null, null);

	}


	public void SetPlayButtonFromAudioFocusHelper() {
		Button btPlay = (Button) findViewById(R.id.btPlay);
		btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.play), null, null, null);

	}// SetUIFromAudioFocusHelper
	
	
	 
	public void SetPauseButtonFromAudioFocusHelper() {
		Button btPlay = (Button) findViewById(R.id.btPlay);
		btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.pause), null, null, null);

	}//SetPauseButtonFromAudioFocusHelper

	
	public void SetPlayed() {
		Button btPlay = (Button) findViewById(R.id.btPlay);
		btPlay.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.play), null, null, null);
		
		
		
		
		

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// useful in changing orientation
		// using the back it will not be called
		super.onSaveInstanceState(outState);

		if ((fa != null) && (!(fa._nowplaying == null)))
		{
					
			int item = fa.GetMusicFileIndex(fa._nowplaying);
			
			int item_pos = 0 ;
			  
			if ( (!(MusicSrvBinder == null)) && (!(MusicSrvBinder._mp == null)) )
			{
				item_pos = MusicSrvBinder._mp.getCurrentPosition();
			//	Boolean item_playing = MusicSrvBinder._mp.isPlaying();
			
			}	
		

			//fa._nowplaying.
			outState.putInt(CURRENT_SONG, item);
			outState.putInt(MainActivity.CURRENT_SONG_POSITION, item_pos);
			//outState.putBoolean(MainActivity.CURRENT_SONG_ISPLAYING,
				//	item_playing);

			 
		}

		if ((fa != null) && !(fa.mfiles == null)) {
			outState.putParcelableArrayList(SONGS_CATALOG, fa.mfiles);
		}

	}// onSaveInstanceStatez

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {

			if (savedInstanceState.get(SONGS_CATALOG) != null) {
				ArrayList<Parcelable> selectedsongs = savedInstanceState
						.getParcelableArrayList(SONGS_CATALOG);
				// if (!(selectedsongs.size() == 0))
				// {
				this.fa.ChangeDatasource(selectedsongs);
				// }

			}

			if (savedInstanceState.get(CURRENT_SONG) != null) {
				int pos = savedInstanceState.getInt(CURRENT_SONG);
				int item_pos = savedInstanceState
						.getInt(MainActivity.CURRENT_SONG_POSITION);
				Boolean item_playing = savedInstanceState
						.getBoolean(MainActivity.CURRENT_SONG_ISPLAYING);

				this.fa.SetSongInUI(pos, item_pos, item_playing);
			}



		}

	}// onRestoreInstanceState

	@Override
	public boolean onSearchRequested() {

		return super.onSearchRequested();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);	 
		return true;
	}// onCreateOptionsMenu

	private void ClearScreen() {
		fa.ClearScreen();
		ClearActivityVariables();
	}// RemoveAllSongs

	private void ClearActivityVariables() {
		_alLatestPlaylistLoaded.clear(); 
	}

	private void AddSongs() {
		// String MUSIC_DIR1 = "/music/comp/";

		// String MusicDir1 = Environment.getExternalStorageDirectory()
		// .getAbsolutePath() + MUSIC_DIR1;

		Intent i = new Intent(MainActivity.this, SongsPickerActivity.class);
		i.putExtra("root_dir", MUSIC_DIR);

		startActivityForResult(i, GET_SONGS);

	}// AddSongs

	private void LoadPlaylistWithActivities() {

		List<String> pls = new ArrayList<String>();

		Cursor c = _dba.ShowPlaylists();

		if (c.moveToFirst()) {
			do {
				String playlist = c.getString(c
						.getColumnIndex(MyDBHelper.PLAYLIST_COL));
				pls.add(playlist);
			} while (c.moveToNext());

		}

		CharSequence[] csPlaylists = pls.toArray(new CharSequence[pls.size()]);
		Intent i = new Intent(MainActivity.this, LoadPlaylistActivity.class);
		i.putExtra("PLAYLISTS", csPlaylists);

		startActivityForResult(i, GET_PLAYLIST);

	}

	private void LoadPlaylist() {
		// String playlist = "PLAYLIST0";

		Dialog dlg = CreatePlaylistSelectorDialog();
		dlg.show(); // this works

		// DialogFragment newFragment = new PlaylistSelectorDialog(_dba, _con,
		// fa);
		// newFragment.show(getSupportFragmentManager(), "LoadPlaylist");

		// fa.getmusicfilesFromPlaylist(playlist);

		// fa.mfiles = new ArrayList<MusicFile>();
		// fileslist = (ListView) findViewById(R.id.lvSongsList);

		// fileslist.setAdapter(fa);
		// fa._fileslist = fileslist;
	}

	// CharSequence[] g = new CharSequence[]{"p1","p2"};
	CharSequence[] cs;

	public Dialog CreatePlaylistSelectorDialog() {

		// ArrayList<String> pls = new ArrayList<String>();
		List<String> pls = new ArrayList<String>();

		Cursor c = _dba.ShowPlaylists();

		if (c.moveToFirst()) {
			do {
				String playlist = c.getString(c
						.getColumnIndex(MyDBHelper.PLAYLIST_COL));
				pls.add(playlist);
			} while (c.moveToNext());

		}

		cs = pls.toArray(new CharSequence[pls.size()]);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setMessage("Continue?")
		// builder.setPositiveButton("Yes", new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// Toast.makeText(_con, "Yes", Toast.LENGTH_LONG).show();

		// }
		// });

		builder.setItems(cs, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				// Toast.makeText(_con, cs[which].toString(),
				// Toast.LENGTH_LONG).show();

				fa.getmusicfilesFromPlaylist(cs[which].toString());

			}
		});

		builder.setTitle("Select playlist");

		// builder.setNegativeButton("No", new DialogInterface.OnClickListener()
		// {
		// public void onClick(DialogInterface dialog, int id) {
		// Toast.makeText(_con, "No", Toast.LENGTH_LONG).show();
		// }
		// });
		// Create the AlertDialog object and return it
		return builder.create();

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.Activity#onCreateDialog(int)
		 */
		/*
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override protected Dialog onCreateDialog(int id) { // TODO
		 * Auto-generated method stub
		 * 
		 * switch (id) { case CONFIRMATION_DIALOG: // Create out AlterDialog /*
		 * Builder builder = new AlertDialog.Builder(this);
		 * builder.setMessage("This will end the activity");
		 * builder.setCancelable(true); builder.setPositiveButton("I agree", new
		 * OkOnClickListener()); builder.setNegativeButton("No, no", new
		 * CancelOnClickListener()); AlertDialog dialog = builder.create();
		 * dialog.show();
		 */
		// Dialog dlg = CreateConfirmationDialog();
		// dlg.show();
		// }*/
		// return super.onCreateDialog(id);
	}

	public Dialog CreateConfirmationDialog() {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.ContinueQuestion)
				.setPositiveButton(R.string.Yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								// Toast.makeText(_con, "Yes",
								// Toast.LENGTH_LONG).show();
							}
						})

				.setTitle(R.string.Confirm)

				.setNegativeButton(R.string.No,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Toast.makeText(_con, "No",
								// Toast.LENGTH_LONG).show();
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	Editable sPlaylistName;
	EditText etPlaylistName;

	public Dialog CreateEnterPlaylistNameDialog() {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.enter_playlist_name, null);
		etPlaylistName = (EditText) v.findViewById(R.id.etPlaylistName);

		builder.setView(v);

		// builder.setMessage("Continue?")
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Toast.makeText(_con, etPlaylistName.getText(),
						// Toast.LENGTH_LONG).show();

						sPlaylistName = etPlaylistName.getText();
						SavePlaylist();
					}
				})

				.setTitle(R.string.EnterPlaylist)

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Toast.makeText(_con, "Cancel",
								// Toast.LENGTH_LONG).show();
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	private ProgressBar mProgress;
	// private Integer mProgressStatus = 0;
	private int mProgressMax = 0;
	private Dialog progressBarDlg;
	private SavePlaylistProgressBarDialog progressBarDlg_f;

	public Dialog CreateSavePlaylistProgressBarDialog() {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.progress_bar_dialog, null);
		mProgress = (ProgressBar) v.findViewById(R.id.pBar);

		builder.setView(v);
		builder.setTitle("Saving playlist");

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						if (savingthread != null) {
							savingthread.interrupt();
							progressBarDlg.dismiss();
						}

						if (savingthread_f != null) {
							savingthread_f.interrupt();
							progressBarDlg.dismiss();
						}
						// Toast.makeText(_con, "Cancel",
						// Toast.LENGTH_LONG).show();

					}
				});

		// Create the AlertDialog object and return it
		return builder.create();
	}

	private CharSequence[] GetAllPlaylists() {
		List<String> pls = new ArrayList<String>();

		Cursor c = _dba.ShowPlaylists();

		if (c.moveToFirst()) {
			do {
				String playlist = c.getString(c
						.getColumnIndex(MyDBHelper.PLAYLIST_COL));
				pls.add(playlist);
			} while (c.moveToNext());

		}

		CharSequence[] csPlaylists = pls.toArray(new CharSequence[pls.size()]);

		return csPlaylists;
	}

	private void PromptAndSavePlaylistWithActivities() {
		CharSequence[] csPlaylists = GetAllPlaylists();

		Intent i = new Intent(MainActivity.this, SavePlaylistActivity.class);

		//i.putExtra("songs_list", fa.mfiles);
		//i.putExtra("PLAYLISTS", csPlaylists);
		//startActivity(i);

		if (fa.mfiles.size() < 1000)//it seems that there is a limit on the size that a intent can have
		{
			//i.putExtra("songs_list", psa.selectedfiles);

			i.putExtra("songs_list", fa.mfiles);
			i.putExtra("PLAYLISTS", csPlaylists);


		}
		else
		{

			MyApplicationObject mApplication = (MyApplicationObject)getApplicationContext();
			mApplication.setSelectedFiles(null);
			mApplication.setSelectedFiles(fa.mfiles);

			i.putExtra("songs_list", "MyApplicationObject");
			i.putExtra("PLAYLISTS", csPlaylists);
		}

		startActivity(i);
	}

	private void PromptAndSavePlaylist() {
		Dialog dlgEnterPlaylist = CreateEnterPlaylistNameDialog();
		dlgEnterPlaylist.show(); // this works

		// DialogFragment newFragment = new EnterPlaylistNameDialog( );
		// newFragment.show(getSupportFragmentManager(),
		// "PromptAndSavePlaylist");

	}// PromptAndSavePlaylist

	private void PromptAndSavePlaylistWithFragments() {
		// Dialog dlgEnterPlaylist = CreateEnterPlaylistNameDialog();
		// dlgEnterPlaylist.show(); //this works

		// DialogFragment newFragment = new EnterPlaylistNameDialog( );
		// newFragment.show(getSupportFragmentManager(),
		// "PromptAndSavePlaylist");

	}// PromptAndSavePlaylist

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#getLastNonConfigurationInstance()
	 */
	/*
	 * @Override public Object getLastNonConfigurationInstance() { // TODO
	 * Auto-generated method stub return
	 * super.getLastNonConfigurationInstance(); }
	 * 
	 * @Override public Object onRetainNonConfigurationInstance() { }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#
	 * onRetainCustomNonConfigurationInstance()
	 */
	/*
	 * @Override public Object onRetainCustomNonConfigurationInstance() { //
	 * TODO Auto-generated method stub
	 * 
	 * ArrayList<Object> al = new ArrayList<Object>(); if (savingthread_f !=
	 * null) if (savingthread_f.isAlive()) al.add(savingthread_f);
	 * 
	 * 
	 * if (al.size() > 0) return al;
	 * 
	 * return super.onRetainCustomNonConfigurationInstance();
	 * 
	 * }
	 */

	Thread savingthread_f;

	private void SavePlaylistWithFragments() throws Exception {

		try {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			SavePlaylistWithFragmentsInner();
		} catch (Exception e) {
			throw e;
		} finally {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);// did
																			// nor
																			// work,
																			// the
																			// ui
																			// thread
																			// launches
																			// the
																			// thread
																			// and
																			// returns
			// work has to take place in the thread, sending a message or sth
			// like that
		}

	}

	Integer mProgressStatus1 = 0;

	private void SavePlaylistWithFragmentsInner()  {

		// Fragmentdialog does not compile
		/*
		 * this.getFragmentManager() FragmentManager fragmentManager =
		 * getSupportFragmentManager();
		 * 
		 * DialogFragment dlg = new ConfirmationDialog();
		 * dlg.show(fragmentManager, "CONFIRMATION_DLG");
		 */

		// Dialog dlg = CreateConfirmationDialog(); //the dialog appears after
		// the savelist, after the menu exits!!!
		// dlg.show();

		// showDialog(CONFIRMATION_DIALOG); // deprecated method

		// String playlist = "PLAYLIST0";
		// fa.SavePlaylist(playlist);
		if (sPlaylistName.toString().equals(""))
			return;

		// check what happens with the back button
		// Dialog dlg = CreateSavePlaylistProgressBarDialog();
		// dlg.show();

		// DialogFragment newFragment = new SavePlaylistProgressBarDialog();
		// newFragment.show(getSupportFragmentManager(), "SavePlaylist");

		// progressBarDlg_f = (SavePlaylistProgressBarDialog) newFragment;
		// progressBarDlg = dlg;
		// newFragment.show(getSupportFragmentManager(), "SavePlaylist");

		// mProgress.setMax(fa.mfiles.size());

		// newFragment.show(); //this works

		// Start lengthy operation in a background thread
		savingthread_f = new Thread(new Runnable() {
			public void run() {

				// String playlist = "PLAYLIST0";
				// fa.SavePlaylist(playlist);
				mProgressStatus1 = 0;

				for (MusicFile mf : fa.mfiles) {
					mProgressStatus1 = mProgressStatus1 + 1;
					fa._dba.insertEntry(mf.title, mf.filepath,
							sPlaylistName.toString(), "",
							mProgressStatus1.toString());

					runOnUiThread(new Thread(new Runnable() {
						public void run() {

							/*
							 * if (mProgress==null) { Dialog dlg =
							 * CreateSavePlaylistProgressBarDialog();
							 * progressBarDlg_f = dlg;
							 * mProgress.setMax(fa.mfiles.size()); dlg.show();
							 * //this works
							 * mProgress.setProgress(mProgressStatus);
							 * 
							 * } else { mProgress.setProgress(mProgressStatus);
							 * }
							 */
							if (mProgress != null) {
								mProgress.setMax(fa.mfiles.size());
								mProgress.setProgress(mProgressStatus1);
								// if (progressBarDlg.isShowing())

							}

						}
					}));
				}

				runOnUiThread(new Thread(new Runnable() {
					public void run() {

						if (progressBarDlg_f != null)
							progressBarDlg_f.dismiss();

						if (progressBarDlg != null)
							progressBarDlg.dismiss();

					}
				}));

			}
		});
		savingthread_f.start();

	}// SavePlaylistWithFragments

	Thread savingthread;
	Integer mProgressStatus2 = 0;

	private void SavePlaylist() {
		// Fragmentdialog does not compile
		/*
		 * this.getFragmentManager() FragmentManager fragmentManager =
		 * getSupportFragmentManager();
		 * 
		 * DialogFragment dlg = new ConfirmationDialog();
		 * dlg.show(fragmentManager, "CONFIRMATION_DLG");
		 */

		// Dialog dlg = CreateConfirmationDialog(); //the dialog appears after
		// the savelist, after the menu exits!!!
		// dlg.show();

		// showDialog(CONFIRMATION_DIALOG); // deprecated method

		// String playlist = "PLAYLIST0";
		// fa.SavePlaylist(playlist);
		if (sPlaylistName.toString().equals(""))
			return;

		// check what happens with the back button
		Dialog dlg = CreateSavePlaylistProgressBarDialog();
		dlg.show();
		// DialogFragment newFragment = new SavePlaylistProgressBarDialog();
		// newFragment.show(getSupportFragmentManager(), "SavePlaylist");

		// progressBarDlg = (SavePlaylistProgressBarDialog) newFragment;
		progressBarDlg = dlg;
		// newFragment.show(getSupportFragmentManager(), "SavePlaylist");

		mProgress.setMax(fa.mfiles.size());

		// newFragment.show(); //this works

		// Start lengthy operation in a background thread
		savingthread = new Thread(new Runnable() {
			public void run() {

				// String playlist = "PLAYLIST0";
				// fa.SavePlaylist(playlist);
				mProgressStatus2 = 0;
				for (MusicFile mf : fa.mfiles) {
					mProgressStatus2++;


						fa._dba.insertEntry(mf.title, mf.filepath,
								sPlaylistName.toString(), "",
								mProgressStatus2.toString());



					runOnUiThread(new Thread(new Runnable() {
						public void run() {

							if (mProgress == null) {
								Dialog dlg = CreateSavePlaylistProgressBarDialog();
								progressBarDlg = dlg;
								mProgress.setMax(fa.mfiles.size());
								dlg.show(); // this works
								mProgress.setProgress(mProgressStatus2);

							} else {
								mProgress.setProgress(mProgressStatus2);
							}
							// if (mProgress != null)
							// mProgress.setProgress(mProgressStatus);

						}
					}));
				}

				runOnUiThread(new Thread(new Runnable() {
					public void run() {

						progressBarDlg.dismiss();

					}
				}));

			}
		});
		savingthread.start();
	}// SavePlaylist

	private void DeletePlaylists()
	{
		Intent i = new Intent(MainActivity.this, DeletePlaylistsActivity.class);
		i.putExtra("PLAYLISTS", GetAllPlaylists());
		startActivityForResult(i, DELETE_PLAYLISTS);
	}

	private void ShowSettings()
	{
		Intent i = new Intent(this, SettingsActivity.class);
		startActivityForResult(i, SHOW_PREFERENCES);
		
	}

	private void SearchSongs() 
	{
		onSearchRequested();
	}
	
	public void ExitApp() {
		Intent i = new Intent(this, MusicPlayerService.class);			 
		stopService(i);	
		finish();	
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.add_songs:
			AddSongs();
			return true;
		case R.id.clear_screen:
			ClearScreen();
			return true;
		case R.id.load_playlist:
			// LoadPlaylist();
			LoadPlaylistWithActivities();
			return true;
		case R.id.save_playlist:
			PromptAndSavePlaylistWithActivities();
			// PromptAndSavePlaylist();
			// PromptAndSavePlaylistWithFragments();
			return true;
		case R.id.delete_playlist:
			DeletePlaylists();
			return true;
		case R.id.menu_settings:
			ShowSettings();
			return true;
		case R.id.search_songs:
			SearchSongs();
			return true;
		case R.id.exit:
			ExitApp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}// onOptionsItemSelected

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// view is the listview
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actions_context_menu, menu);

	//	MenuItem iDelete = menu.findItem(R.id.cmenu_delete);
		MenuItem iRemove = menu.findItem(R.id.cmenu_removefromlist);
		MenuItem iRepeat = menu.findItem(R.id.cmenu_repeat);

		MenuItem iBookmark = menu.findItem(R.id.cmenu_bookmark_position);
		MenuItem iBookmark_go = menu.findItem(R.id.cmenu_bookmark_go);
		MenuItem iBookmark_delete = menu.findItem(R.id.cmenu_bookmark_delete);
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		MusicFile mf = fa.mfiles.get(info.position);
		if (!(mf == null)) {

			if (mf.equals(fa._nowplaying))
			{
				//iDelete.setVisible(false);
				iRemove.setVisible(false);

				if (fa._nowplaying.bRepeat == 0) {
					iRepeat.setTitle(R.string.repeatON);
				} else {
					iRepeat.setTitle(R.string.repeatOFF);
				}
				
				if (BookmarkExists(info))
				{
					
				}
				else
				{					
					iBookmark_go.setVisible(false);
					iBookmark_delete.setVisible(false);
				}				

			}
			else 
			{
				iRepeat.setVisible(false);
				iBookmark.setVisible(false);
				iBookmark_go.setVisible(false);
				iBookmark_delete.setVisible(false);
			}

		}
	}// onCreateContextMenu

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		
				//case R.id.cmenu_delete:
				//	cmenuDeleteFile(info);
				//	return true;
				case R.id.cmenu_removefromlist:
					cmenuRemoveFromList(info);
					return true;
				case R.id.cmenu_repeat:
					cmenuRepeat(item);
					return true;
				case R.id.cmenu_info:
					cmenuInfo(info);
					return true;					
				case R.id.cmenu_bookmark_position:
					cmenuBookmarkPosition(info);
					return true;
				case R.id.cmenu_bookmark_go:
					cmenuBookmarkGo(info);
					return true;					
				case R.id.cmenu_bookmark_delete:
					cmenuBookmarkDelete(info);
					return true;
				default:
					return super.onContextItemSelected(item);
		}
	}

	private void cmenuDeleteFile(AdapterContextMenuInfo info) {
		// Toast.makeText(_con, "cmenuDelete " + info.position,
		// Toast.LENGTH_LONG).show();
		fa.DeleteFile(info.position);
	}

	private void cmenuRemoveFromList(AdapterContextMenuInfo info) {
		// Toast.makeText(_con, "cmenuGetProperties " + info.position,
		// Toast.LENGTH_LONG).show();
		fa.RemoveFromList(info.position);
	}

	private void cmenuInfo(AdapterContextMenuInfo info) {
		// Toast.makeText(_con, "cmenuGetProperties " + info.position,
		// Toast.LENGTH_LONG).show();
		fa.GetInfo(info.position);
	}
	
	public boolean PlayerIsPlayingOrIsPaused()
	{		
		boolean bGoOn = false;
				
		if(MusicSrvBinder != null)
		{
			 
				if ( MusicSrvBinder._mp != null && MusicSrvBinder._mp.isPlaying())
				{
					bGoOn = true;
				}
				else
				{
					if ( MusicSrvBinder._mp != null
							&& MusicSrvBinder._nowplaying.bInError == 0) //fucks the media player//attempt to call getduration without a valid media player
					{
						bGoOn = true;
					}					
				}
		}		
		
		return bGoOn;
		
	}

	private void cmenuBookmarkPosition(AdapterContextMenuInfo info) {
		
		MusicFile mfile = null;
		mfile = fa.mfiles.get(info.position);
		
		if(MusicSrvBinder._nowplaying != mfile)
			return;
		
		//if (MusicSrvBinder != null)
		//{
			if (PlayerIsPlayingOrIsPaused())
			{				
				int cur_pos = MusicSrvBinder._mp.getCurrentPosition();
				String name = MusicSrvBinder._nowplaying.title;
				
				SharedPreferences prefs = getPreferences(MODE_PRIVATE);
				Editor ed = prefs.edit();
	
				ed.putInt(name, cur_pos);
				ed.commit();
			
			}	//	if (MusicSrvBinder._mp.isPlaying())			
		//}//if (MusicSrvBinder != null)
				
	}	
	
	private boolean BookmarkExists(AdapterContextMenuInfo info)
	{
		MusicFile mfile = null;
		mfile = fa.mfiles.get(info.position);		
		
		SharedPreferences priv_prefs = getPreferences(MODE_PRIVATE);

		int cur_pos = priv_prefs.getInt(mfile.title, 0);
		
		if (cur_pos == 0)
			return false;

		if (priv_prefs.contains(mfile.title))
			return true;
	
		
		return false;
	}
	
private void cmenuBookmarkGo(AdapterContextMenuInfo info) {
		
		MusicFile mfile = null;
		mfile = fa.mfiles.get(info.position);		
		
		if(MusicSrvBinder._nowplaying != mfile)
			return;
		
		SharedPreferences priv_prefs = getPreferences(MODE_PRIVATE);
 
		String name = mfile.title;
		int cur_pos = priv_prefs.getInt(name, 0);
				
		if (cur_pos == 0)
			return;
		
		//if (MusicSrvBinder != null)
		//{
			if (PlayerIsPlayingOrIsPaused())
			{						
				if (!(MusicSrvBinder._nowplaying == null))
				{						 
					MusicSrvBinder._mp.seekTo(cur_pos);						 							
				}				
			}	//	if (MusicSrvBinder._mp.isPlaying())			
		//}//if (MusicSrvBinder != null)
		
		updateSeekBar(false);
				
	}	 


	private void cmenuBookmarkDelete(AdapterContextMenuInfo info) {
	
		MusicFile mfile = null;
		mfile = fa.mfiles.get(info.position);		
		
		if(MusicSrvBinder._nowplaying != mfile)
			return;	
	 
		String name = mfile.title;
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Editor ed = prefs.edit();
				
		ed.putInt(name, 0);//there is no way to clear a single entry
		ed.commit();

	}	
	
	private void cmenuRepeat(MenuItem item) {
		// AdapterContextMenuInfo info = (AdapterContextMenuInfo)
		// item.getMenuInfo();
		if(MusicSrvBinder != null)
		{
			if (MusicSrvBinder._nowplaying.bRepeat == 0) {
				MusicSrvBinder._nowplaying.bRepeat = 1;
			} else {
				MusicSrvBinder._nowplaying.bRepeat = 0;
			}	
		}
		

		
		/*if (fa._nowplaying.bRepeat == 0) {
			fa._nowplaying.bRepeat = 1;
		} else {
			fa._nowplaying.bRepeat = 0;
		}*/

		/*
		 * SharedPreferences prefs = getSharedPreferences(MYPREFS,
		 * MODE_PRIVATE);
		 * 
		 * Editor ed = prefs.edit(); ed.putInt(REPEAT_SONG, info.position);
		 * ed.commit();
		 */

	}

	public void onDialogPositiveClick(DialogFragment dialog) {
		//if (dialog instanceof EnterPlaylistNameDialog) {
			//EnterPlaylistNameDialog dlg = (EnterPlaylistNameDialog) dialog;
			// sPlaylistName= dlg.etPlaylistName.getText();
			//SavePlaylistWithFragmentsInner();

			/*
			 * try { this.SavePlaylistWithFragments(); } catch (Exception e) {
			 * 
			 * e.printStackTrace(); }
			 */
		//}

	}// onDialogPositiveClick

	public void onDialogNegativeClick(DialogFragment dialog) {

		if (dialog instanceof SavePlaylistProgressBarDialog) {
			SavePlaylistProgressBarDialog dlg = (SavePlaylistProgressBarDialog) dialog;

			if (savingthread_f != null) {
				savingthread_f.interrupt();
				dlg.dismiss();
				// progressBarDlg.dismiss();
			}
		}

	}// onDialogNegativeClick

	public void onDialogItemClick(DialogFragment dialog, int which) {
		if (dialog instanceof PlaylistSelectorDialog) {
			PlaylistSelectorDialog dlg = (PlaylistSelectorDialog) dialog;
			CharSequence[] cs = dlg.cs;
			fa.getmusicfilesFromPlaylist(cs[which].toString());

			Toast.makeText(this, "number of songs: " + fa.mfiles.size(),
					Toast.LENGTH_LONG).show();
		}

	}// onDialogItemClick

	public void onDialogCreated(DialogFragment dialog) {
		if (dialog instanceof SavePlaylistProgressBarDialog) {
			SavePlaylistProgressBarDialog dlg = (SavePlaylistProgressBarDialog) dialog;

			progressBarDlg_f = dlg;
			mProgress = dlg.mProgress;
			mProgress.setMax(fa.mfiles.size());
		}

	}// onDialogCreated

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTrimMemory(int)
	 */
	@Override
	public void onTrimMemory(int level) {
		 
		super.onTrimMemory(level);

		switch (level) {
			case TRIM_MEMORY_UI_HIDDEN:
				if (true) {
				}
	
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

	}// onTrimMemory

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

}
