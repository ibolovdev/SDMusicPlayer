package com.ibo_android.sdmusicplayer;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	//Params, Progress, Result
	//Integer, Void, Bitmap
	 private final WeakReference<ImageView> imageViewReference;
	 private  WeakReference<BitmapCache> bcReference;
	    public String data = "";

	    public BitmapWorkerTask(ImageView imageView, WeakReference<BitmapCache>  bc)
	    {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	        bcReference =  bc;
	    }
	    
	    
	    public BitmapWorkerTask(ImageView imageView, BitmapCache  bc)
	    {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	        bcReference = new WeakReference<BitmapCache>(bc);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... paths) {
	       	data = paths[0];
	    	Bitmap bm =  GetMp3Picture(data);	    	
	    	
	    	if (bcReference != null && bm != null && bcReference.get() != null)
	    		bcReference.get().addBitmapToMemoryCache(data, bm);
	        
	    	return bm;
	             
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) 
	    {
	    	  if (isCancelled()) {
	              bitmap = null;
	          }

	    	  
	       /* if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            
	            
	            if (imageView != null)
	            {
	                imageView.setImageBitmap(bitmap);
	            }
	        }*/
	        
	        
	        if (imageViewReference != null && bitmap != null)
	        {
	            final ImageView imageView = imageViewReference.get();
	            final BitmapWorkerTask bitmapWorkerTask =
	                    getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null)
	            {
	                imageView.setImageBitmap(bitmap);
	               
	            }
	        }
        
	        
	    }//onPostExecute
	    
	    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
	    	   if (imageView != null) {
	    	       final Drawable drawable = imageView.getDrawable();
	    	       if (drawable instanceof AsyncDrawable) {
	    	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
	    	           return asyncDrawable.getBitmapWorkerTask();
	    	       }
	    	    }
	    	    return null;
	    	}
	    
	    public void SetMp3Picture(ImageView imgview, Bitmap bmap)
		{
			imgview.setImageBitmap(bmap); //associated cover art in bitmap
			//imgview.setAdjustViewBounds(true);
		 	//imgview.setLayoutParams(new LinearLayout.LayoutParams(500, 500));				
			
		}
		public Bitmap GetMp3Picture(String MusicFileFilepath)
		{
			
			//if(_act.get() == null)
			//{
			//	return null;
			//}
			
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) 
			  return null;
			
			//MusicFile mfile = null;
			//mfile = mfiles.get(pos);
			
										
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		
			 	
			byte [] data = null;
			try
			{
				mmr.setDataSource(MusicFileFilepath);
				data = mmr.getEmbeddedPicture();
			}
			catch(Exception e)
			{
				
			}
			
               //coverart is an Imageview object

        // convert the byte array to a bitmap
			  Bitmap bitmap = null;
        if(data != null)
        {
        	BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inJustDecodeBounds = true;

        	
              bitmap = BitmapFactory.decodeByteArray( data, 0, data.length,options);
              
            //  int imageHeight = options.outHeight;
             // int imageWidth = options.outWidth;
             // String imageType = options.outMimeType;
              
              Integer height = 36;
              Integer width = 36;
              
                height = 144;
               width = 144;
              
          	/*if (imageViewReference != null && imageViewReference.get() != null)
          	{         		
          		height = imageViewReference.get().getHeight();
                width = imageViewReference.get().getWidth();
          	}*/
              
              options.inSampleSize = calculateInSampleSize(options, height, width);
              
              if (options.inSampleSize  == 1)
              {
            	  
            	  if(true)
            	  {}
            	  
            	  
              }
              
              options.inJustDecodeBounds = false;
              bitmap = BitmapFactory.decodeByteArray( data, 0, data.length,options);

              

          //  coverart.setImageBitmap(bitmap); //associated cover art in bitmap
         //   coverart.setAdjustViewBounds(true);
         //   coverart.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
        }
        else
        {
        //    coverart.setImageResource(R.drawable.fallback_cover); //any default cover resourse folder
         //   coverart.setAdjustViewBounds(true);
         //   coverart.setLayoutParams(new LinearLayout.LayoutParams(500,500 ));
        }
			
			return bitmap;
			
		}
		
		
		public  int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight)
		{
			    // Raw height and width of image
			    final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;
		
			    if (height > reqHeight || width > reqWidth) {
		
			        final int halfHeight = height / 2;
			        final int halfWidth = width / 2;
		
			        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
			        // height and width larger than the requested height and width.
			        while ((halfHeight / inSampleSize) > reqHeight
			                && (halfWidth / inSampleSize) > reqWidth) {
			            inSampleSize *= 2;
			        }
			    }
		
			    return inSampleSize;
	}


}
