

package com.ibo_android.sdmusicplayer;

        import java.io.File;
        import java.lang.ref.WeakReference;
        import java.util.ArrayList;
        import java.util.Collections;

        import com.ibo_android.sdmusicplayer.R;
        import com.ibo_android.sdmusicplayer.FilesAdapter.ViewHolder;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.preference.PreferenceManager;
        import android.provider.MediaStore;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.View.OnClickListener;
        import android.widget.BaseAdapter;
        import android.widget.ListView;
        import android.widget.RadioButton;
        import android.widget.TextView;

public class DirectoryChooserContentProviderStrategyAdapter  extends DirectoryChooserAdapter {

    private FileSystemProvider _FileSystemProvider = null;

    public DirectoryChooserContentProviderStrategyAdapter(DirectoryChooserActivity act, String rootdir )
    {
        minfl = LayoutInflater.from(act);
        mfiles = new ArrayList<MusicFile>();
        _rootdir=rootdir;

        prefs = PreferenceManager.getDefaultSharedPreferences(act);
        _act = new WeakReference<DirectoryChooserActivity>(act);
        _FileSystemProvider = new FileSystemProvider(act);
        getDirectories(_rootdir);

    }//FilesAdapter


    FileNodeComposite MfilesComp = null;
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

    public void getDirectories(String rootfolder) {

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

        //FileNode compnode = GetCompositeByFileName(rootfolder,MfilesComp);
        FileNode compnode =  _FileSystemProvider.GetCompositeByFileName(rootfolder,MfilesComp);


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
                        mfiles.add(mf);
                    }


                }//fn

            }//if (f.listFiles() != null)


        } else {

            MusicFile mf = new MusicFile("", compnode.ID(), compnode.FullPath(), 0);
            if(compnode.IsDirectory())
            {
                mf.bIsDirectory = 1;
                mfiles.add(mf);
            }

        }

        Collections.sort(mfiles);



    }//getmusicfiles


    public void getDirectories1(String rootfolder)
    {
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
            //	MusicFile mf = new  MusicFile("", "UP",f.getAbsolutePath(),0);
            //	mf.bIsRootDirectory = 1;
            //	mfiles.add(mf);

            setRootDirectory(f.getAbsolutePath());
        }

        if (f.isDirectory())
        {
            if (f.listFiles() != null)
            {
                for (File name: f.listFiles()) {

                    if (name.isDirectory())
                    {
                        MusicFile mf = new  MusicFile("", name.getName(),name.getAbsolutePath(),0);
                        mfiles.add(mf);
                    }
                }
            }//if (f.listFiles() != null)

        }

        Collections.sort(mfiles);
    }//getmusicfiles




			/*	public MusicFile PlayStop(View v)
				{
		    			  ViewHolder vh = (ViewHolder)  v.getTag();
		    			  MusicFile mf = vh.mfile;
		    		 	return mf;

				}*///PlayStop

    public void ChooseDirectory(View v)
    {
        DirectoryViewHolder vh = (DirectoryViewHolder)  v.getTag();
        //String dir = vh.DirectoryName.filepath;
        //RadioButton cb = vh.mRadio;

        if (  vh.mRadio.isChecked())
            selectedDirectory = vh.DirectoryName.filepath;
        this.notifyDataSetChanged();

    }//ChooseDirectory


    public boolean GoUp()
    {

        if(MfilesComp==null)
        {
            return false;
        }


        if (!_rootdir.equals("/")) {

            FileNode compnode = GetCompositeByFileName(_rootdir,MfilesComp);

            String root =  compnode.parent.FullPath();

            mfiles.clear();
            getDirectories(root);
            this.notifyDataSetChanged();

            return true;
        }

        return false;

    }


    public boolean GoUp1()
    {
        //if (mfiles.size() == 0)
        //	return false;

        //MusicFile mf = mfiles.get(0);

//					if (mf.title == "UP")
//					{
//						 File fc = new File(mf.filepath);
//						String root =   fc.getParent();
//						if (!root.equals(null))
//						{
//							 File froot = new File(root);
//							 mfiles.clear();
//							 getmusicfiles(root);
//							this.notifyDataSetChanged();
//						}
//						else
//						{
//							 return false;
//						}
//
//						return true;
//					}


        if (_rootdir != "")
        {
            File fc = new File(_rootdir);
            String root =   fc.getParent();
            if (root != null)
            {
                // File froot = new File(root);
                mfiles.clear();
                getDirectories(root);
                this.notifyDataSetChanged();
            }
            else
            {
                return false;
            }

            return true;
        }

        return false;

    }


    public void setRootDirectory(String rootDirectory) {
        _rootdir = rootDirectory;
        if(_act.get() != null)
            _act.get().SetRootDirectoryTextView(_rootdir);
    }

    public boolean ChangeDirectory(View v)
    {
        DirectoryViewHolder vh = (DirectoryViewHolder)  v.getTag();

        // File fc = new File(vh.mfile.filepath);
        FileNode compnode = GetCompositeByFileName(vh.DirectoryName.filepath,MfilesComp);

        if (compnode.IsDirectory()) {
            mfiles.clear();
            getDirectories(compnode.FullPath());
            this.notifyDataSetChanged();
        }

        return true;

    }//OpenFolder

    public boolean ChangeDirectory1(View v)
    {

        DirectoryViewHolder vh = (DirectoryViewHolder)  v.getTag();
        MusicFile mf = vh.DirectoryName;

        if (mf.title == "UP")
        {
            File fc = new File(mf.filepath);
            String root =   fc.getParent();
            if ( root!= null )
            {
                //File froot = new File(root);
                mfiles.clear();
                getDirectories(root);
                this.notifyDataSetChanged();

            }
            else
            {
                return false;
            }

            return true;
        }
        else
        {
            mfiles.clear();
            getDirectories(mf.filepath);
            this.notifyDataSetChanged();
            return true;
        }

    }//ChangeDirectory

}
