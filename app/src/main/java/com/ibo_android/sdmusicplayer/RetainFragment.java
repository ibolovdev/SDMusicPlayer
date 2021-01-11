package com.ibo_android.sdmusicplayer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class RetainFragment extends Fragment {
	
	
	 private static final String TAG = "RetainFragment";
	    public BitmapCache mRetainedCache;

	    public RetainFragment() {}

	    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
	        RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
	        if (fragment == null) {
	            fragment = new RetainFragment();
	            fm.beginTransaction().add(fragment, TAG).commit();
	        }
	        return fragment;
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setRetainInstance(true);
	    }

	
	

}
