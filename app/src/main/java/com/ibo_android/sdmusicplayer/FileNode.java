package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;

public abstract class FileNode
{
    protected String _id = "";

    protected String _fullpath = "";

    public FileNode(String id)
    {
        this._id = id;
    }

    protected FileNode parent = null;



    public String Display( )
    {

        if (parent == null)
        {
            return _id;
        }
        else
        {
            return _id.replace(parent.ID(),"");
        }

    }

    public String ID( )
    {
        return _id;

    }

    public String FullPath( )
    {
        if (parent == null)
        {
            //return "/" + _id + "/";
            return  _id + "/";
        }
        else
        {
            return parent.FullPath() +  _id + "/";
        }

    }

    public abstract  void Add(FileNode file);
    public abstract  void Remove(FileNode file);
    public abstract  FileNode GetParent(FileNode file);

    public abstract ArrayList<FileNode> GetChildren();
    public abstract boolean IsFile();
    public abstract boolean IsDirectory();
}








