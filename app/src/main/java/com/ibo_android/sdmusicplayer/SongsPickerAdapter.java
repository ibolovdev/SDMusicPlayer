package com.ibo_android.sdmusicplayer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import android.widget.TextView;


public class SongsPickerAdapter extends BaseAdapter {

    public LayoutInflater minfl;
    //private Context _con;

    public ArrayList<MusicFile> mfiles;
    private String _rootdir;
    public    ArrayList<MusicFile> selectedfiles;
    public WeakReference<SongsPickerActivity> _act;
    private ArrayList<String> _allowableFormats;

    private String _RootDirectory = "";
    public ListView fileslist;
    SharedPreferences prefs;

    public String getRootDirectory() {
        return _RootDirectory;
    }


    public void setRootDirectory(String rootDirectory) {
        _RootDirectory = rootDirectory;
        if (_act.get() != null)
            _act.get().SetRootDirectoryTextView(_RootDirectory);
    }

    public class PickSongViewHolder {
        MusicFile mfile;
        CheckBox mCheck;
        TextView mTitle;
    }


    public SongsPickerAdapter(String rootdir, SongsPickerActivity act, ArrayList<Parcelable> selfiles_asparam) {

        minfl = LayoutInflater.from(act);
        mfiles = new ArrayList<MusicFile>();
        selectedfiles = new ArrayList<MusicFile>();

        if (!(selfiles_asparam == null)) {
            for (Object omf : selfiles_asparam.toArray()) {
                MusicFile typedmf = (MusicFile) omf;
                selectedfiles.add(typedmf);
            }
        }

        _rootdir = rootdir;

        _act = new WeakReference<SongsPickerActivity>(act);

        _allowableFormats = new ArrayList<String>();
        _allowableFormats.add("mp3");

        getmusicfiles(_rootdir);

        Collections.sort(mfiles);

        prefs = PreferenceManager.getDefaultSharedPreferences(act);

    }//FilesAdapter

    public void getmusicfiles(String rootfolder) {

        //mfiles.add(object);
        //this is for 1.7
		/*Iterable<Path> dirs =
			    FileSystems.getDefault().getRootDirectories();
			for (Path name: dirs) {
			    System.err.println(name);
			}*/


        File f = new File(rootfolder);

        if (f.isDirectory())//first file is the root directory
        {
//			MusicFile mf = new  MusicFile("", "UP",f.getAbsolutePath()); 
//			mf.bIsRootDirectory = 1;
//			mfiles.add(mf);	

            setRootDirectory(f.getAbsolutePath());
        }

        if (f.isDirectory()) {
            if (f.listFiles() != null) {
                for (File name : f.listFiles()) {

                    if (CheckFile(name)) {
                        MusicFile mf = new MusicFile("", name.getName(), name.getAbsolutePath(), 0);
                        mfiles.add(mf);
                    }

                }

            }//if (f.listFiles() != null)


        } else {
            if (CheckFile(f)) {
                MusicFile mf = new MusicFile("", f.getName(), f.getAbsolutePath(), 0);
                mfiles.add(mf);
            }

        }

        Collections.sort(mfiles);

    }//getmusicfiles

    private boolean CheckFile(File f) {

        if (!f.isDirectory()) {
            FileName fn = new FileName(f.getAbsolutePath());

            for (String format : _allowableFormats) {
                if (format.toUpperCase().equals(fn.extension().toUpperCase())) {
                    return true;
                }
            }
			
			/*if (_allowableFormats.contains(fn.extension()))
			{
				return true;	
			}*/

            return false;
        }

        return true;

    }//CheckFile


    public int getCount() {
        // TODO Auto-generated method stub
        return mfiles.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mfiles.get(arg0);
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public View getView(int pos, View v, ViewGroup vg) {
        PickSongViewHolder holder = null;

        if (v == null || v.getTag() == null) {
            if (mfiles.get(pos).title == "UP") {
                v = minfl.inflate(R.layout.activity_songs_picker_item, null);
                holder = new PickSongViewHolder();
                holder.mTitle = (TextView) v.findViewById(R.id.txtTitle);

                holder.mCheck = (CheckBox) v.findViewById(R.id.cbCheck);
                //holder.mCheck.setTag(holder);
                v.setTag(holder);
                //	v = minfl.inflate(R.layout.file_view, null);
                //	holder = new PickSongViewHolder();
                //	holder.mTitle = (TextView)v.findViewById(R.id.txtMusicFile);


                //holder.mCheck = (CheckBox)v.findViewById(R.id.cbCheck);
                //holder.mCheck.setTag(holder);
                //	v.setTag(holder);
            } else {
                v = minfl.inflate(R.layout.activity_songs_picker_item, null);

                v.setBackgroundResource(R.drawable.list_item_appearances);

                holder = new PickSongViewHolder();
                holder.mTitle = (TextView) v.findViewById(R.id.txtTitle);

                holder.mCheck = (CheckBox) v.findViewById(R.id.cbCheck);
                //holder.mCheck.setTag(holder);
                v.setTag(holder);
            }

            //v = minfl.inflate(R.layout.pick_item_view, null);
            //holder = new PickSongViewHolder();
            //holder.mTitle = (TextView)v.findViewById(R.id.txtTitle);

            //holder.mCheck = (CheckBox)v.findViewById(R.id.cbCheck);
            //holder.mCheck.setTag(holder);
            //v.setTag(holder);
        } else {
            holder = (PickSongViewHolder) v.getTag();
        }
        //holder.mTitle.setFocusable(true);
        //holder.mCheck.setFocusable(false);


        if (mfiles.get(pos).title == "UP") {


            holder.mfile = mfiles.get(pos);
            holder.mTitle.setText(holder.mfile.title);

            v.setTag(holder);
            //holder.mCheck = null;
            //holder.mCheck.setVisibility(View.INVISIBLE);
            v.setOnClickListener(new OnClickListener() {
                                     public void onClick(View v) {
                                         GoUp(v);
                                     }
                                 }
            );

            //return v;

        } else {

            v.setTag(holder);
            //holder.mCheck = null;
            //holder.mCheck.setVisibility(View.INVISIBLE);
            v.setOnClickListener(new OnClickListener() {
                                     public void onClick(View v) {
                                         OpenFolder(v);
                                     }
                                 }
            );

        }// ( mfiles.get(pos).bIsRootDirectory == 1)

        holder.mfile = mfiles.get(pos);
        if (mfiles.get(pos).title == "UP")
            holder.mTitle.setText(holder.mfile.filepath);
        else {
            holder.mTitle.setText(holder.mfile.title);
            //holder.mCheck.setText(holder.mfile.title);
            //int  TextSize = prefs.getInt("text_size", 12);
            // holder.mCheck.setTextSize(TextSize);


            try {
                MainActivity.ApplySelectorsSize(_act.get(), holder.mCheck);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //	holder.mCheck.setBackgroundResource(R.drawable.checkbox);

            ///holder.mCheck.setButtonDrawable(R.drawable.checkbox_medium);

            //v.setBackgroundResource(R.drawable.list_item_appearances);


            //holder.mCheck.setScaleX(2f);
            //holder.mCheck.setScaleY(2f);

            // Get the margins of Flex CheckBox
            ///  ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.mCheck.getLayoutParams();

            // Set left, top, right and bottom margins of Flex CheckBox
            ///    mlp.setMargins(25,15,10,10);
            ///    holder.mCheck.setLayoutParams(mlp);

            // Apply right padding of Flex CheckBox
            ///    holder.mCheck.setPadding(0,0,50,0);


        }
        int TextSize = prefs.getInt("text_size", 12);
        holder.mTitle.setTextSize(TextSize);

        v.setTag(holder);

        ///	if (holder != null)
        //	holder.mCheck.setTag(holder);//check if this good from a performance point of view


        if (ContainsElement(selectedfiles, holder.mfile)) {
            if (holder != null)
                holder.mCheck.setChecked(true);
        } else {
            if (holder != null)
                holder.mCheck.setChecked(false);
        }

        if (holder != null)
            holder.mCheck.setOnClickListener(new OnClickListener() {
                                                 public void onClick(View v) {
                                                     ChooseSongRecursive(v);
                                                     //Toast.makeText(_act, "ChooseSongRecursive ended",  Toast.LENGTH_LONG).show();
                                                 }
                                             }
            );


        v.setOnLongClickListener(new OnLongClickListener() {

                                     public boolean onLongClick(View v) {
                                         PlaySong(v);
                                         return false;
                                     }
                                 }
        );

        return v;
    }//getView


    private void PlaySong(View v) {
        String action = "PLAY_THIS_SONG";
        Intent in = new Intent(action);


        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();
        File fc = new File(vh.mfile.filepath);

        if (!fc.isDirectory()) {
            in.putExtra("song_path", vh.mfile.filepath);

            if (_act.get() != null)
                _act.get().sendBroadcast(in);
        }

    }


    private synchronized boolean ContainsElement(ArrayList<MusicFile> al1, MusicFile mf) {
        for (MusicFile sf : al1) {
            if (sf.filepath.equals(mf.filepath)) {
                return true;
            }
        }

        return false;

    }//ContainsElement

    public void OpenFolder(View v) {

        //open a new pick song activity
        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();
        //MusicFile mf = vh.mfile;
        //  CheckBox cb = vh.mCheck;
        //Toast.makeText(_con, "OpenFolder", Toast.LENGTH_LONG).show();

        File fc = new File(vh.mfile.filepath);

        if (fc.isDirectory()) {
            mfiles.clear();
            getmusicfiles(fc.getAbsolutePath());
            this.notifyDataSetChanged();
				
				
			/*	Intent i = new Intent(_con, SongsPickerActivity.class);
    		 	i.putExtra("root_dir", mf.filepath);
    			i.putExtra("SELECTED_SONGS", selectedfiles);
    		 	i.putExtra("OPEN_FOLDER", true);*/

            //_act.startActivityForResult(intent, requestCode)
            // _con.startActivity(i);
            //_act.startActivityForResult(i,MainActivity.GET_SONGS_FOLDER);
            //_con.s


            //String action = "STOP_MUSIC";
            //Toast.makeText(ctx, "AUDIO_BECOMING_NOISY", Toast.LENGTH_LONG).show();

            // Intent in = new Intent("PICK_SONG_INSIDE_FOLDER");
            //in.putExtra("root_dir", mf.filepath);

            // You can also include some extra data.
            //  in.putExtra("message", "This is my message!");


            //  LocalBroadcastManager.getInstance(_con).sendBroadcast(in);

            // ctx.sendBroadcast(in);
        }


    }//OpenFolder

    private void TraverseDirectoryAdd_WithoutThreads(File fc, MusicFile mf) {
        FileName fn = new FileName(fc);

        if (fn.isDirectory()) {//if (fc.isDirectory()) {  caused ANRs, after calling TraverseDirectoryAdd four times
            selectedfiles.add(mf);
            //maybe JDK-4803836 : File.listFiles() returns null for 'System Volume Information' / unreadable dir

            File[] fs = fc.listFiles();

            if (fs != null)//this normally should not happen, only if it a file returns null,
            {//There is no guarantee that the name strings in the resulting array will appear in any specific order; they are not, in particular, guaranteed to appear in alphabetical order.

                for (File name : fs)// listFiles() caused ANRs, after calling TraverseDirectoryAdd four times
                {
                    MusicFile mf_in = new MusicFile("", name.getName(), name.getAbsolutePath(), 0);
                    //selectedfiles.add(mf_in);
                    TraverseDirectoryAdd(name, mf_in);
                }//name
            }//if (fc.listFiles() != null)


        } else {
            selectedfiles.add(mf);
        }
    }//TraverseDirectoryAdd

    File fc_thread = null;
    MusicFile mf_thread = null;
    boolean SelectedListInUse = false;
    private void TraverseDirectoryAdd(File fc, MusicFile mf) {

       // if (fc_thread != null)
         //   return;

        Thread savingthread;
        fc_thread = fc;
        mf_thread = mf;

        try
        {
            savingthread = new Thread(new Runnable( ) {
                public void run() {

                    try
                    {


                    }
                    finally
                    {


                    }
                    TraverseDirectoryAddInner(fc_thread,mf_thread);

                   /* runOnUiThread (new Thread(new Runnable() {
                        public void run() {

                            if (mProgress==null)
                            {

                                mProgress.setMax(selfiles_asparam.size());
                                //dlg.show(); //this works
                                mProgress.setProgress(mProgressStatus);

                            }
                            else
                            {
                                mProgress.setProgress(mProgressStatus);
                            }
                            //if (mProgress != null)
                            //	 mProgress.setProgress(mProgressStatus);

                        }
                    }));*/
                }
            });
            savingthread.start();

        }
        finally
        {
           // fc_thread = null;
          //  mf_thread = null;
        }

    }

    public synchronized void AddToSelected( MusicFile mf)
    {
        selectedfiles.add(mf);
    }

    public synchronized void ClearSelected()
    {
        selectedfiles.clear();
    }

    public synchronized void RemoveFromSelected( MusicFile mf)
    {
        selectedfiles.remove(mf);
    }

    void TraverseDirectoryAddInner(File fc, MusicFile mf ) {
        FileName fn = new FileName(fc);

       // if (fn.isDirectory()) {//if (fc.isDirectory()) {  caused ANRs, after calling TraverseDirectoryAdd four times
        if (fc.isDirectory()) {
            AddToSelected(mf);
             //selectedfiles.add(mf);
            //maybe JDK-4803836 : File.listFiles() returns null for 'System Volume Information' / unreadable dir

            File[] fs = fc.listFiles();

            if (fs != null)//this normally should not happen, only if it a file returns null,
            {//There is no guarantee that the name strings in the resulting array will appear in any specific order; they are not, in particular, guaranteed to appear in alphabetical order.

                for (File name : fs)// listFiles() caused ANRs, after calling TraverseDirectoryAdd four times
                {
                    MusicFile mf_in = new MusicFile("", name.getName(), name.getAbsolutePath(), 0);
                    //selectedfiles.add(mf_in);
                    TraverseDirectoryAddInner(name, mf_in);
                }//name
            }//if (fc.listFiles() != null)


        } else {
            AddToSelected(mf);
          //  selectedfiles.add(mf);
        }



    }//TraverseDirectoryAdd


    private void TraverseDirectoryRemove(File fc, MusicFile mf) {
        if (fc.isDirectory()) {
           // selectedfiles.remove(mf);
            RemoveFromSelected(mf);
            for (Object omf : selectedfiles.toArray()) {
                MusicFile mf_in = (MusicFile) omf;

                if (mf_in.filepath.contains(fc.getPath())) {
                    RemoveFromSelected(mf);
                   // selectedfiles.remove(mf_in);
                }
				
			/*	for (File name: fc.listFiles())//takes too much time
				 {									
						if (mf_in.filepath.equals(name.getAbsolutePath()))
						{									
							 //selectedfiles.remove(mf_in);
							TraverseDirectoryRemove(name,mf_in);
							 continue;
						}								 
				}*/
            }    //omf

        } else {
            RemoveFromSelected(mf);
            //selectedfiles.remove(mf);
        }


    }

    public boolean GoUp() {
        //if (mfiles.size() == 0)
        //	return false;

        //MusicFile mf = mfiles.get(0);

//		if (mf.title == "UP")
//		{
//			 File fc = new File(mf.filepath);
//			String root =   fc.getParent();
//			if (!root.equals(null))
//			{
//				 File froot = new File(root);
//				 mfiles.clear();
//				 getmusicfiles(root);
//				this.notifyDataSetChanged();
//			}
//			else
//			{
//				 return false;				
//			}
//			
//			return true;
//		}		


        if (_RootDirectory != "") {
            File fc = new File(_RootDirectory);
            if (!HandleRoot(_RootDirectory))
            {
                String root = fc.getParent();
                if (root != null) {
                    // File froot = new File(root);
                    if (!HandleRoot(root))
                    {
                        mfiles.clear();
                        getmusicfiles(root);
                        this.notifyDataSetChanged();
                    }

                } else {
                    return false;
                }
            }

            return true;
        }

        return false;
    }


    public boolean HandleRoot(String root)
    {
       // return false;

      if (!root.equals( "/"))
           return false;

       String sdcard = "mnt";
       String internalstorage = "storage";


        mfiles.clear();

        MusicFile mfcard = new MusicFile("", sdcard,"/" +  sdcard, 0);
        mfiles.add(mfcard);

        MusicFile mfin = new MusicFile("", internalstorage, "/" + internalstorage, 0);
        mfiles.add(mfin);

        Collections.sort(mfiles);


        this.notifyDataSetChanged();

        return true;
    }


    public boolean GoUp(View v) {

        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();
        MusicFile mf = vh.mfile;
        //CheckBox cb = vh.mCheck;

        if (mf.title == "UP") {
            File fc = new File(mf.filepath);
            String root = fc.getParent();
            if (root != null) {
                //File froot = new File(root);
                mfiles.clear();
                getmusicfiles(root);
                this.notifyDataSetChanged();
            } else {
                return false;
            }

            return true;
        }

        return false;

    }//GoUp

    public void ChooseSongRecursive(View v) {

        PickSongViewHolder vh = (PickSongViewHolder) ((View) v.getParent()).getTag();
        //MusicFile mf = vh.mfile;
        //CheckBox cb = vh.mCheck;

        if (vh.mCheck.isChecked()) {
            File fc = new File(vh.mfile.filepath);
            TraverseDirectoryAdd(fc, vh.mfile);
        } else {
            File fu = new File(vh.mfile.filepath);
            TraverseDirectoryRemove(fu, vh.mfile);
        }


    }//ChooseSongRecursive

    public void ChooseSong(View v) {

        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();
        MusicFile mf = vh.mfile;
        CheckBox cb = vh.mCheck;

        if (cb.isChecked()) {
            File fc = new File(mf.filepath);

            if (fc.isDirectory()) {
                //selectedfiles.add(mf);
                AddToSelected(mf);
                if (fc.listFiles() != null)
                {
                    for (File name : fc.listFiles()) {
                        MusicFile mf_in = new MusicFile("", name.getName(), name.getAbsolutePath(), 0);
                        //selectedfiles.add(mf_in);
                        AddToSelected(mf_in);
                    }
                }//if (fc.listFiles() != null)


            } else {
                //selectedfiles.add(mf);
                AddToSelected(mf);
            }

        } else {
            File fu = new File(mf.filepath);
            if (fu.isDirectory()) {
               // selectedfiles.remove(mf);
                RemoveFromSelected(mf);
                for (Object omf : selectedfiles.toArray()) {
                    MusicFile mf_in = (MusicFile) omf;

                    if (fu.listFiles() != null)
                    {
                        for (File name : fu.listFiles()) {
                            if (mf_in.filepath.equals(name.getAbsolutePath())) {
                                //selectedfiles.remove(mf_in);
                                RemoveFromSelected(mf_in);
                                continue;
                            }
                        }
                    }//if (fu.listFiles() != null)

                }    //omf

            } else {
                //selectedfiles.remove(mf);
                RemoveFromSelected(mf);
            }

        }


    }//ChooseSong


    public void SelectAll_Old() {
        // selectedfiles.clear();
        ClearSelected();

        for (Object omf : mfiles.toArray()) {
            MusicFile mf_in = (MusicFile) omf;


            //int idx = mfiles.indexOf(mf_in);
				/* if (!(  fileslist.getChildAt(idx) == null))//for the visible ones
				 {
					 View v = (View)   fileslist.getChildAt(idx);
					 PickSongViewHolder vh = (PickSongViewHolder)  v.getTag();
					 vh.mCheck.setChecked(true);
				 }*/

            File fc = new File(mf_in.filepath);
            TraverseDirectoryAdd(fc, mf_in);

        }    //omf


        this.notifyDataSetChanged();////for the visible ones, for everything
        //Notifies the attached observers that the underlying data has been changed
        //and any View reflecting the data set should refresh itself.
        //ArrayAdapter - Control whether methods that change the list (add(T), insert(T, int), remove(T), clear()) automatically call notifyDataSetChanged().
        //this.registerDataSetObserver(observer)

    } //SelectAll

    public void SelectAll() {

        Thread selectthread;
        final SongsPickerAdapter adter = this;

        try
        {

            selectthread = new Thread(new Runnable( ) {
                public void run() {

                    // selectedfiles.clear();
                    ClearSelected();

                    for (Object omf : mfiles.toArray()) {
                        MusicFile mf_in = (MusicFile) omf;

                        File fc = new File(mf_in.filepath);
                        TraverseDirectoryAddInner(fc, mf_in);

                    }    //omf

                    if (_act != null)
                    {
                        if (_act.get() != null)
                        {
                            _act.get().runOnUiThread (new Thread(new Runnable() {
                                public void run() {

                                    adter.notifyDataSetChanged();////for the visible ones, for everything
                                    //Notifies the attached observers that the underlying data has been changed
                                    //and any View reflecting the data set should refresh itself.
                                    //ArrayAdapter - Control whether methods that change the list (add(T), insert(T, int), remove(T), clear()) automatically call notifyDataSetChanged().
                                    //this.registerDataSetObserver(observer)

                                }
                            }));

                        }//if (_act.get() != null)

                    }// if (_act != null)


                }
            });
            selectthread.start();

        }
        catch(Exception ex)
        {

        }
        finally
        {


        }



    } //SelectAll

    public void UnSelectAll() {
       // selectedfiles.clear();
        ClearSelected();
        this.notifyDataSetChanged();
    }//UnSelectAll

}
