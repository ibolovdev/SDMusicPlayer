package com.ibo_android.sdmusicplayer;

import android.app.DialogFragment;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
 
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

 
public class EnterPlaylistNameDialog extends DialogFragment  {

	//public Editable sPlaylistName;
	//public EditText etPlaylistName;
	//private static  final String PLAYLISTNAME = "PLAYLISTNAME";   
	
	 //public EnterPlaylistNameDialog() {
		//	super();
			// TODO Auto-generated constructor stub
			
			
		//}	
	
//	 @Override
	//    public Dialog onCreateDialog(Bundle savedInstanceState) {	      
		 
		// if (savedInstanceState != null)
		 //{		    	
			  //if (savedInstanceState.get(PLAYLIST) != null)
				//  cs =  savedInstanceState.getCharSequenceArray(PLAYLIST);		    	
		  //}///(savedInstanceState != null)
		
		 
		 
		 // Use the Builder class for convenient dialog construction
	     //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	     
	     // Get the layout inflater
	     //LayoutInflater inflater =  getActivity().getLayoutInflater();

	     // Inflate and set the layout for the dialog
	     // Pass null as the parent view because its going in the dialog layout
	     //View v = inflater.inflate(R.layout.enter_playlist_name, null);
	     //etPlaylistName = (EditText) v.findViewById(R.id.etPlaylistName);
	  
	     //builder.setView(v);
	    
	      // builder.setMessage("Continue?")
	       //     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	         //       public void onClick(DialogInterface dialog, int id) {
	             	//	Toast.makeText(_con, etPlaylistName.getText(), Toast.LENGTH_LONG).show(); 
	                	
	           //     	sPlaylistName= etPlaylistName.getText();             		              			
	             		//SavePlaylist();	
	             //   	mListener.onDialogPositiveClick(EnterPlaylistNameDialog.this);	                	
	             	            		
	               // }
	            //})    
	            
	           //.setTitle("Enter playlist")
	            
	            //.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	              //  public void onClick(DialogInterface dialog, int id) {
	             	  // Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_LONG).show(); 
	                //}
	           // });
	     // Create the AlertDialog object and return it
	    // return builder.create(); 
		 
	    //}
	 
	 /* (non-Javadoc)
		 * @see android.support.v4.app.DialogFragment#onSaveInstanceState(android.os.Bundle)
		 */
		//@Override
		//public void onSaveInstanceState(Bundle outState) {
			// TODO Auto-generated method stub
			//super.onSaveInstanceState(outState);	
			
			//outState.putCharSequence(PLAYLISTNAME, etPlaylistName.getText());		
		//}
		
	 
	// Use this instance of the interface to deliver action events
	    //NoticeDialogListener mListener;
	    
	    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	    //@Override
	    //public void onAttach(Activity activity) {
	      //  super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	       // try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	         //   mListener = (NoticeDialogListener) activity;
	        //} catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	          //  throw new ClassCastException(activity.toString()
	            //        + " must implement NoticeDialogListener");
	        //}
	    //}//onAttach(Activity activity) {	
	
}
