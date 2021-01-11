package com.ibo_android.sdmusicplayer;

import android.support.v4.app.DialogFragment;

public interface NoticeDialogListener {

	public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
    public void onDialogItemClick(DialogFragment dialog, int which);
    public void onDialogCreated(DialogFragment dialog);

}
