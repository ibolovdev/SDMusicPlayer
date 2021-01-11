package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;

public class PlaylistSelectorDialog extends DialogFragment {

	
	private MyDB _dba;
	private  Context _con;
	public CharSequence[]  cs = null;
	private FilesAdapter _fa;
	private static  final String PLAYLIST = "PLAYLIST";   
	
	 public PlaylistSelectorDialog(MyDB dba, Context con, FilesAdapter fa) {
		super();
		// TODO Auto-generated constructor stub
		_dba = dba;
		_con = con;
		_fa = fa;
	}
	 
	 public PlaylistSelectorDialog() {
			super();
			// TODO Auto-generated constructor stub
			 
		}
	 
	 

	 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	      
		 if (savedInstanceState != null)
		 {		    	
			  if (savedInstanceState.get(PLAYLIST) != null)
				  cs =  savedInstanceState.getCharSequenceArray(PLAYLIST);		    	
		  }//(savedInstanceState != null)
		
	   if (cs == null)
	   {
			List<String> pls = new ArrayList<String>();			
			Cursor c = _dba.ShowPlaylists();
			
			if(c.moveToFirst())
			{
				do
				{			 
					String playlist = c.getString(c.getColumnIndex(MyDBHelper.PLAYLIST_COL));
					pls.add(playlist);				
				} while(c.moveToNext());
				
			}		  
			 
			cs = pls.toArray(new CharSequence[pls.size()]); 
	   }
		 
	     // Use the Builder class for convenient dialog construction
	     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // builder.setMessage("Continue?")
	         //   builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	         //       public void onClick(DialogInterface dialog, int id) {
	          //   		Toast.makeText(_con, "Yes", Toast.LENGTH_LONG).show(); 
	             		
	          //   		     }
	          //  });
	            
	     
	            builder.setItems(cs, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            // The 'which' argument contains the index position
	            // of the selected item
	         	  // Toast.makeText(_con, cs[which].toString(), Toast.LENGTH_LONG).show(); 
	         	   
	         	//  _fa.getmusicfilesFromPlaylist(cs[which].toString());
	         	   
	            	mListener.onDialogItemClick(PlaylistSelectorDialog.this,which);
	         	   
	            	}
	            });	            
	            
	            builder.setTitle("Select playlist");
	            
	           // builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	           //     public void onClick(DialogInterface dialog, int id) {
	           //  	   Toast.makeText(_con, "No", Toast.LENGTH_LONG).show(); 
	           //     }
	          //  });
	     // Create the AlertDialog object and return it
	     return builder.create();
	  
	 }


	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);	
		
		outState.putCharSequenceArray(PLAYLIST, cs);		
	}
	
	// Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }//onAttach(Activity activity) {
     
}
	
 
