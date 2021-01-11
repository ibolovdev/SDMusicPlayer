package com.ibo_android.sdmusicplayer;

 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

//sourcesafe???
//remove playlist0
//implement buttons, ok
//orientation check
//interaction with other sound sources,ok
//exception handling

//prepareasync in media player +errors, ok
//run as service
//running as a foreground service
//use threads
//run asynchronously
//tasks
//notification starts a new activity, i just want to  show the old one, ok

//handle mp releasing, ondestroy does not work, activity lifetime, ok
//create a notification when loosing ui focus, notifications have changed in 4, ok
//pressing the home and back buttons, ok

//Add files - checkbox list, ok
//if checks folder -> automatically all files are chosen, recursive
//if picks folder -> a new activity opens , showing the contents of the folder
//					-> with the ok button returns the results
//					-> if the same folder is picked again then all the contents 
//					should be displayed checked based on the previous selections

//save to playlist -> choose name , ok
//save playlist -> if i change orientation the progress bar disapperars, maybe i should use notification
//loadplaylist -> pick name, ok

//use files from a content provider,
//create and use playlists, content providers, sqllite, ok
//export playlists, ok
//handle folders, ok

//settings screen, PreferenceScreen and PreferenceActivity, ok
//building a custom preference for directory chooser, ok

//he AUDIO_BECOMING_NOISY Intent, broadcast receivers, see ANR, ok
//intents and intent filters
//decoupling audio focus, java references problem, ok
//use wake locks, ok

//check supported media files
//UI of playback
//icons???

//AppInstallLocation
//extra actions, like play in random, ok
//extra actions button, menu button, ok
//handle ActionBar for android 4
//remove, try to use adapter.notifyondatasetchange, ok

//Dialogs, ok

//icons in android, 
//a smiley little person with headphones, ok

//ActivityBar, ActivityCicle -> ProgressBars, ok
//ability to cancel the procedure, ok
//implement the up button in the directory screens, ok

//reduction of memory footprint,ok
//context menu -> repeat, ok

//context menu -> delete does not work, ok
//dialogs do not work on orientation changing, done but savingplaylist has problems, i loose the reference to savingthread
//too much usage of the menu hardware button- > use action bar-> put a button -> ok
//in random the focus should go to the song playing , ok
//retrieving information from a mp3 file, ok
//directory picker, problem on how to show it from settings, ok continue with  implementation

//error when playing wma files
//check what files i load, type , etc
//sometimes it starts immediatelly

//settings -> which buttons do i want to be visible?
//setting -> change the color, font and size
//how a blind person could use it?

//when a call is coming there is an exception
//audio focus does not work, how do i use audio focus for previous versions? ( reflection?)

//Resources management
//saving list -> thread management
//multiple select -> sometimes it hungs, use thread; open system folders

//songs selector -> fit all buttons
//remove the square?? for the up button

//translations new, ok
//add search
//information about the duration of the song -> current place + total time
//saving playlist -> if you provide a name that already exists -> produce an error

//images, ok all
//		play buttons -> chnage colour to blue only
//		application icon
//		menu icon

//sorting of list based on
//file name, artist name etc...
//sorting of the list, appearance of what text to show
//wake lock

//supporting different devices
//		different languages
//		different screens
//		different platform versions
//		different capabilities

//NEW 7-Dec-2013
//implement seek bar and show duration, OK BUT has problems when user tries to change it, does not respond
//ordering of songs -> they are displayed in reverse order, ok
//select songs  -> first the folders, ok
//wake lock

//implement as a service (foreground??)
//wake the ui when the app becomes visible - connect with the service etc.

//implement search  - content providers
//manually place songs, drag and drop
//insert seqnum field in db

//ui enhancement
//performance monitor, various tools
//Application Quality - criteria
//best practices
//translation program

//4 may 14
//saving playlist - ui and functionality improvement
//playlist deletion - another acivity
//problem wiith repeat
//focus to song in non sshufle mode
//notifications
//forward/backward , number of seconds , make it a setting
//implement debug mode, memory activity etc.

//control sdplayer from a distance, phonetic commands etc.
//to appear on the default apps for music
//save playlist in a file
//show picture files
//when i choose songs hungs, or when i uncheck them,ok 
//handsfree mode - car mode

//2-8-14
//app after a while when i run other apps, suddenly stops, it is detached from the debugger,ok
//when i check a folder and then try to uncheck them app hungs,ok
//try to fix the emulator,ok
//einai fores pou xanei to mpousoula

//implement service, ok
//use dev tools, ok
//testing fw
//guidelines

//sourcesafe???
//AppInstallLocation
//setting -> change the color, font and size
//how a blind person could use it?
//forward/backward , number of seconds , make it a setting

//control sdplayer from a distance, phonetic commands etc.
//handsfree mode - car mode - Speech Recognition 439  - maybe it needs libraries from google
//to appear on the default apps for music  - 184 - ok
//equilizer  - AudioEffect subclasses
//ActionBar

//Theme ( Blak/White), ok
//Lock screen controls
//exceptions with other sound sources
//next/previous with volume up/down, ok
//fa null at the service
//proguard, ok
//na paizei apo epilogh fakelon,ok
//use the system equilizer
//add song pictures,ok
//in search show what file has chosen, ok
//displaying bitmaps efficiently, ok
//promote your app

//22-11-14
//bitmap cache in orientation change,ok
//ui improvement (bitmap size ) when font size gets bigger, ok for now
//implement seek button duration, ok
//french translation, ok
//voice recognition, no it cannot be done

//26-12-14
//read managing bitmap memory
//examine  	ARGB_8888 
//examine lrucache
//read java - Convert ArrayList String  to String [] - Stack Overflow
//retain fragment -> how it works
//general refactoring
//performance-memory examination
//testing


//when you open the app from notification , sometimes  does not do refresh to the current song
//use equilizer
//search the folders too
//Expandable listview, problem with large fonts
//bugs -> orientation change with empty musiclist,ok there are some other places
//bug -> music player starts to play 
//bug-> when opened from notification sometimes app does not wake up

//implement debug mode, memory activity etc.
//manually place songs, drag and drop
//insert seqnum field in db

//
//dialogs do not work on orientation changing, done but savingplaylist has problems, i loose the reference to savingthread
//implement ActionBar
//single song mode, show picture file at large
//ApInstallLocation
//long click in back/forward goes to the start/end. make the user understand it

//save the previous position in list
//the list should remain after i close the app
//delete-> folder
//create virtual configurations and backup
//compress list when transferring to another activity
//bookmarks in the song
//analyze communication between activities
//call an indepedent application to show pdf



//10/10/15

//accessibility
//custom components
// looses the ball
//playlists current song and save
//web radio
//advertisement like an item list
//icons in android, change icon
//ui improvement (bitmap size ) when font size gets bigger
//setting -> change the color, font and size
//sourcesafe
//promotion - like sto facebook, etc...
//allow changing the order of the songs, using the hand like drag and drop
//add seek bar, transparently,  on top of each play item
//testing
//how can i raise the the volume in low-volume songs

//read managing bitmap memory
//examine  	ARGB_8888 
//examine lrucache
//read java - Convert ArrayList String  to String [] - Stack Overflow
//retain fragment -> how it works
//equilizer  - AudioEffect subclasses
//german translation
//play phonetic command - only
//export playlists
//intents and intent filters
//open file location - using a different app
//rename file

//drag'n'drop in order to change the sequence of the songs, there are db implications in that

//modify size in:
//checkboxes-radiobuttons, ok
//buttons
//font size
//font type
//number of lines
//image, put it in the center for extra large
//create icons
//ApplySelectorsSize to everywhere,ok
//getdrawable obsolete


//25-11-16
// Apply MaterialDesign
//modify size in:
//buttons
//font size
//font type
//number of lines
//image, put it in the center for extra large
//drag'n'drop in order to change the sequence of the songs, there are db implications in that, see ClipBoard
// Implement Accessibility features
//read managing bitmap memory
//examine  	ARGB_8888 
//examine lrucache
//read java - Convert ArrayList String  to String [] - Stack Overflow
//retain fragment -> how it works
//equilizer  - AudioEffect subclasses
//german translation
//export playlists

//web radio
//advertisement like an item list
//testing, debug, sourcesafe, development tools  
//add seek bar, transparently,  on top of each play item
//how can i raise the the volume in low-volume songs
//long click in back/forward goes to the start/end. make the user understand it

//28-2-18
//can't bind to local 8600 for debug android
//how can i change the ui inside an android service
// debug through wifi
//testing
//perfomance - cpu - memory - graphics
//version control
//sql inspector
//icons
//graphic design
// Could not open Selected VM debug port (8700). Make sure you do not have another instance of DDMS or of the eclipse plugin running. If it's being used by something else, choose a new port number in the preferences.
//if the file is an image show it
//insert notes about the content of the file in the mp3 tag

//14-6-19
//connection with youtube - audio only
//mp3 tag editor
//mark as readed - change colour
//show level in name eg. CD1-01.mp3
//delete file does not work
//better random file - play all songs, backward
//problem in resume

//24-11-19
// does not show all the files - see history of science, ok
//playback cursor does not stay in the same place after resuming
//media buttons do not work - registerMediaButtonEventReceiver deprecated
//New implementation of ListFiles, ok

public class ShowEULA {
		
	public static final  String EULA_PREFIX = "eula_";
    private MainActivity mActivity;
    private static final String ASSET_EULA = "EULA_ASSET.txt";
    
    public ShowEULA(MainActivity context) {
        mActivity = context;
    }
 
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
             pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }
 
     public void show() {
        PackageInfo versionInfo = getPackageInfo();
 
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
       //final String eulaKey = EULA_PREFIX;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(EULA_PREFIX, false);
       // hasBeenShown = false;
        if(hasBeenShown == false){
 
            // Show the Eula
            //String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;
        	   String title = "SDMusicPlayer" + " v" + versionInfo.versionName;
            //Includes the updates as well so users know what changed.
          //  String message = mActivity.getString("") + "\n\n" + mActivity.getString("");
            
          //c mActivity.getString(R.string.EULA_MSG) + "\n\n" + "";
        	   CharSequence message =readEula(mActivity);
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
 
                       // @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read.
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(EULA_PREFIX, true);
                            editor.commit();
                            dialogInterface.dismiss();
                            //mActivity.InitMainActivity();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
 
                       // @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            mActivity.finish();
                        }
 
                    });
            builder.create().show();
        }
    }
 
     private static CharSequence readEula(Activity activity) {
         BufferedReader in = null;
         try {
             in = new BufferedReader(new InputStreamReader(activity.getAssets().open(ASSET_EULA)));
             String line;
             StringBuilder buffer = new StringBuilder();
             while ((line = in.readLine()) != null) buffer.append(line).append('\n');
             return buffer;
         } catch (IOException e) {
             return "";
         } finally {
        	       	 
        	 if (in != null) {
                 try {
                     in.close();
                 } catch (IOException e) {
                     // Ignore
                 }
             }
         }
     }
	
	

}
