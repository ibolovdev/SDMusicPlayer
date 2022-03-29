package com.ibo_android.sdmusicplayer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

//SongsPickerAdapter
public class SongsPickerContentProviderStrategy extends SongsPickerAdapter implements SongsPickerSetRootDirectory
{
    private FileSystemProvider _FileSystemProvider = null;

    public SongsPickerContentProviderStrategy(String rootdir, SongsPickerActivity act, ArrayList<Parcelable> selfiles_asparam) {
        //super(rootdir, act, selfiles_asparam);

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

        _FileSystemProvider = new FileSystemProvider(act);

        getmusicfiles(_rootdir);

        Collections.sort(mfiles);

        prefs = PreferenceManager.getDefaultSharedPreferences(act);
    }


    @Override
    public void getmusicfiles(String rootfolder) {

        //mfiles.add(object);
        //this is for 1.7
		/*Iterable<Path> dirs =
			    FileSystems.getDefault().getRootDirectories();
			for (Path name: dirs) {
			    System.err.println(name);
			}*/

        // getmusicfilesFromContentProvider(rootfolder);

        if (MfilesComp == null)
        {
           // getmusicfilesFromContentProvider();

            MfilesComp = _FileSystemProvider.getmusicfilesFromContentProvider();
            if(MfilesComp==null)
                return;
        }



       // FileNode compnode = GetCompositeByFileName(rootfolder,MfilesComp);
        FileNode compnode = _FileSystemProvider.GetCompositeByFileName(rootfolder,MfilesComp);

        if (compnode == null)
        {
            compnode = MfilesComp;
        }

        if (compnode.IsDirectory())//first file is the root directory
        {
            setRootDirectory(compnode.FullPath());
        }

        if (compnode.IsDirectory()) {
            if (compnode.GetChildren() != null) {
                for (FileNode fn : compnode.GetChildren() )
                {
                    MusicFile mf = new MusicFile("", fn.ID(), fn.FullPath(), 0);

                    if(fn.IsDirectory())
                    {
                        mf.bIsDirectory = 1;
                    }
                    mfiles.add(mf);

                }//fn

            }//if (f.listFiles() != null)


        } else {

            MusicFile mf = new MusicFile("", compnode.ID(), compnode.FullPath(), 0);
            if(compnode.IsDirectory())
            {
                mf.bIsDirectory = 1;
            }
            mfiles.add(mf);
        }

        Collections.sort(mfiles);

    }//getmusicfiles



    public void getmusicfilesFromContentProvider( )
    {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
      /*  Cursor cursor = _act.get().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");*/

        Cursor cursor = _act.get().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null);

        ArrayList<String> files;
        files = new ArrayList<String>();


        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

            String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

            String kk = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

            files.add(fullpath);

            // Whatever else you need
        }

        if (files.isEmpty())
        {

            //show message
        }
        else
        {
            for (String fl : files ) {

                AddFile(fl);

            }//fl

        }//if (files.isEmpty())

        if (MfilesComp == null)
        {}

    }

    FileNodeComposite MfilesComp = null;
    public void AddFile(String fl)
    {
        String[] parts = fl.split("/");

        for (String part : parts ) {

        }

        FileNode prevcomp = null;

        for (int i = 0; i < parts.length; i++)
        {

           // if ("".equals(parts[i]))
            //    continue;

            if (i== parts.length - 1)
            {
                FileNodeLeaf leaf = new FileNodeLeaf(parts[i],fl);
                prevcomp.Add(leaf);
                // leaf.parent = prevcomp;
            }
            else
            {

                if (MfilesComp == null)
                {
                    MfilesComp = new FileNodeComposite( parts[i]);
                    prevcomp = MfilesComp;
                }
                else
                {
                    FileNode fnode = GetComposite(parts[i],MfilesComp);

                    if (fnode == null)
                    {
                        FileNodeComposite comp = new FileNodeComposite(parts[i]);
                        // comp.parent = prevcomp;

                        prevcomp.Add(comp);
                        prevcomp = comp;
                    }
                    else
                    {
                        prevcomp = fnode;

                    }// if (fnode == null)

                }// if (MfilesComp == null)


            }// if (i== parts.length - 1)

        }//i

    }//AddFile

    public FileNode GetComposite(String part,FileNode fnode)
    {
        if (part.equals(fnode.ID()))
        {
            return fnode;
        }
        else
        {
            if (fnode.GetChildren() != null)
            {
                for (FileNode fn : fnode.GetChildren() )
                {
                    FileNode fnchild = GetComposite(part,fn);
                    if(fnchild != null)
                        return fnchild;

                }//fnode
            }// if (fnode.GetChildren() != null)

        }

        return null;
    }


    public FileNode GetCompositeByFileName(String part,FileNode fnode)
    {
        if (part.toLowerCase().equals(fnode.FullPath().toLowerCase()))
        {
            return fnode;
        }
        else
        {
            if(fnode.IsDirectory())
            {
                for (FileNode fn : fnode.GetChildren() )
                {
                    FileNode fnchild = GetCompositeByFileName(part,fn);
                    if(fnchild != null)
                        return fnchild;

                }//fnode

            }//  if(fnode.IsDirectory())

        }

        return null;
    }



    protected void PlaySong(View v) {
        String action = "PLAY_THIS_SONG";
        Intent in = new Intent(action);

        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();
        //File fc = new File(vh.mfile.filepath);

        if (vh.mfile.bIsDirectory != 1) {
            in.putExtra("song_path", vh.mfile.filepath);

            if (_act.get() != null)
                _act.get().sendBroadcast(in);
        }

    }



    public boolean GoUp() {

        if(MfilesComp==null)
        {
            return false;
        }


        if (!_RootDirectory.equals("/")) {

            //  File fc = new File(_RootDirectory);

            FileNode compnode = GetCompositeByFileName(_RootDirectory,MfilesComp);

            String root =  compnode.parent.FullPath();


            mfiles.clear();
            getmusicfiles(root);
            this.notifyDataSetChanged();

            return true;
        }

        return false;
    }

    public void OpenFolder(View v) {


        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();

        // File fc = new File(vh.mfile.filepath);
        FileNode compnode = GetCompositeByFileName(vh.mfile.filepath,MfilesComp);

        if (compnode.IsDirectory()) {
            mfiles.clear();
            getmusicfiles(compnode.FullPath());
            this.notifyDataSetChanged();
        }


    }//OpenFolder



    public void ChooseSong(View v) {

        PickSongViewHolder vh = (PickSongViewHolder) v.getTag();
        MusicFile mf = vh.mfile;
        CheckBox cb = vh.mCheck;
        FileNode compnode = GetCompositeByFileName(mf.filepath,MfilesComp);

        if (cb.isChecked()) {

            if (mf.bIsDirectory == 1) {

                AddToSelected(mf);
                if (compnode.GetChildren() != null)
                {
                    for (FileNode fn  : compnode.GetChildren()) {

                        MusicFile mf_in = new MusicFile("", fn.ID(), fn.FullPath(), 0);

                        if(fn.IsDirectory())
                        {
                            mf_in.bIsDirectory = 1;
                        }
                        AddToSelected(mf_in);
                    }
                }//if (compnode.GetChildren() != null)


            } else {

                AddToSelected(mf);
            }

        } else {


            if (mf.bIsDirectory == 1) {

                RemoveFromSelected(mf);
                for (Object omf : selectedfiles.toArray()) {

                    MusicFile mf_in = (MusicFile) omf;

                    if (compnode.GetChildren() != null)
                    {
                        for (FileNode fn  : compnode.GetChildren()) {
                            if (mf_in.filepath.equals(fn.FullPath())) {
                                RemoveFromSelected(mf_in);
                                continue;
                            }
                        }

                    }  // if (compnode.GetChildren() != null)

                }    //omf

            } else {

                RemoveFromSelected(mf);
            }

        }


    }//ChooseSong

    public void SelectAll() {

        ClearSelected();

        for (Object omf : mfiles.toArray())
        {
            MusicFile mf_in = (MusicFile) omf;

            TraverseDirectoryAddInner( mf_in);

        }//omf

        notifyDataSetChanged();////for the visible ones, for everything
        //Notifies the attached observers that the underlying data has been changed
        //and any View reflecting the data set should refresh itself.
        //ArrayAdapter - Control whether methods that change the list (add(T), insert(T, int), remove(T), clear()) automatically call notifyDataSetChanged().
        //this.registerDataSetObserver(observer)

    } //SelectAll

    void TraverseDirectoryAddInner( MusicFile mf ) {
        // FileName fn = new FileName(fc);
        FileNode compnode = GetCompositeByFileName(mf.filepath,MfilesComp);

        if (compnode.IsDirectory()) {
            AddToSelected(mf);

            if (compnode.GetChildren() != null)
            {
                for (FileNode fn  : compnode.GetChildren())
                {
                    MusicFile mf_in = new MusicFile("", fn.ID(), fn.FullPath(), 0);
                    TraverseDirectoryAddInner( mf_in);
                }

            }  // if (compnode.GetChildren() != null)

        }
        else
        {
            AddToSelected(mf);
        }

        notifyDataSetChanged();

    }//TraverseDirectoryAdd


    public void ChooseSongRecursive(View v) {

        PickSongViewHolder vh = (PickSongViewHolder) ((View) v.getParent()).getTag();

        if (vh.mCheck.isChecked()) {
            TraverseDirectoryAddInner(vh.mfile);
        } else {
            TraverseDirectoryRemove( vh.mfile);
        }

    }//ChooseSongRecursive


    private void TraverseDirectoryRemove( MusicFile mf) {

        FileNode compnode = GetCompositeByFileName(mf.filepath,MfilesComp);

        if (compnode.IsDirectory()) {

            RemoveFromSelected(mf);

            for (Object omf : selectedfiles.toArray()) {
                MusicFile mf_in = (MusicFile) omf;

                if (mf_in.filepath.contains(compnode.FullPath())) {
                    RemoveFromSelected(mf);
                }

            }    //omf

        } else {
            RemoveFromSelected(mf);
            //selectedfiles.remove(mf);
        }

    }//TraverseDirectoryRemove


}
