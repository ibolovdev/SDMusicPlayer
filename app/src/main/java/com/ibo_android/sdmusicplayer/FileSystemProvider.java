package com.ibo_android.sdmusicplayer;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FileSystemProvider {

    public WeakReference<Activity> _act;
    FileNodeComposite MfilesComp = null;

        public FileSystemProvider(Activity act)
        {
            _act = new WeakReference<Activity>(act);
        }



    public FileNodeComposite getmusicfilesFromContentProvider( )
    {

        if (MfilesComp!=null)
            return MfilesComp;


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
        {


        }

        return MfilesComp;

    }


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



}
