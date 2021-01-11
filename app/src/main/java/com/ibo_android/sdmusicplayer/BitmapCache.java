package com.ibo_android.sdmusicplayer;

import android.graphics.Bitmap;
import android.os.Build;

public class BitmapCache
{
	BitmapLruCache lrucache ;
	public BitmapCache() 
	{
		super();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) 
			lrucache = new BitmapLruCache (); 
	}
	
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) 
	{
	   if (lrucache != null )
		   lrucache.addBitmapToMemoryCache(key, bitmap);
	}
	
	public Bitmap getBitmapFromMemCache(String key)
	{
		 if (lrucache != null )
			 return lrucache.getBitmapFromMemCache(key);
		 else
			 return null;
	}

}
