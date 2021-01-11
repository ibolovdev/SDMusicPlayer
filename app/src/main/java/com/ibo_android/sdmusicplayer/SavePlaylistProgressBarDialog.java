package com.ibo_android.sdmusicplayer;
 
import com.ibo_android.sdmusicplayer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class SavePlaylistProgressBarDialog extends DialogFragment {

	public ProgressBar mProgress;
	int progress = 0;
	int max = 0;
	 public SavePlaylistProgressBarDialog() {
			super();
			// TODO Auto-generated constructor stub
		}
	
	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	      
		 if (savedInstanceState != null)
		 {		    	
			  if (savedInstanceState.get("STEP") != null)
				  progress =  savedInstanceState.getInt("STEP");
			  
			  if (savedInstanceState.get("MAX") != null)
				  max =  savedInstanceState.getInt("MAX");
			  
		  }//(savedInstanceState != null)
		 
		  // Use the Builder class for convenient dialog construction
	     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	     
	     // Get the layout inflater
	     LayoutInflater inflater =  getActivity().getLayoutInflater();

	     // Inflate and set the layout for the dialog
	     // Pass null as the parent view because its going in the dialog layout
	     View v = inflater.inflate(R.layout.progress_bar_dialog, null);
	     mProgress = (ProgressBar) v.findViewById(R.id.pBar);

	     builder.setView(v);	            
	     builder.setTitle(R.string.Savingplaylist);
	 	       
	     builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
           	   
            	 mListener.onDialogNegativeClick(SavePlaylistProgressBarDialog.this);
            	 
            	 
            	/* if (!savingthread.equals(null))
            	 {
            		 savingthread.interrupt();
            		 progressBarDlg.dismiss();			        	
            	 }*/
            	// Toast.makeText(_con, "Cancel", Toast.LENGTH_LONG).show(); 
            	            	 
              }
          });
	     
	     if (savedInstanceState != null)
	     {
	    	 mProgress.setProgress(progress);
		     mProgress.setMax(max);
		     	 
	     }
	     
	     mListener.onDialogCreated(SavePlaylistProgressBarDialog.this);
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
			
			 outState.putInt("STEP", mProgress.getProgress());	
			 outState.putInt("MAX", mProgress.getMax());	
			 
			 
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
