package com.ibo_android.sdmusicplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class ConfirmationDialog extends DialogFragment {
	
	
	
	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Continue?")
	               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                		Toast.makeText(getActivity(), "Yes", Toast.LENGTH_LONG).show(); 
	                   }
	               })
	               
	              .setTitle("Confirm")
	               
	               .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   Toast.makeText(getActivity(), "No", Toast.LENGTH_LONG).show(); 
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }	

}
