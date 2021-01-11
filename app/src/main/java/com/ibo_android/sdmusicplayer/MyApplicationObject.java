package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;

import android.app.Application;


public class MyApplicationObject extends Application {

	 ArrayList<MusicFile> al;

	 ArrayList<MusicFile> getSelectedFiles() {
	        return al;
	    }

	    void setSelectedFiles(ArrayList<MusicFile> selectedfiles) {
	        this.al = selectedfiles;
	    }
	
	
	    private static MyApplicationObject singleton;
		
		public MyApplicationObject getInstance(){
			return singleton;
		}
		@Override
		public void onCreate() {
			super.onCreate();
			singleton = this;
		}
		
		
		
		
	
}
