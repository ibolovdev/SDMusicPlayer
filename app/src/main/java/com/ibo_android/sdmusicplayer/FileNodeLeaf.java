package com.ibo_android.sdmusicplayer;


import java.util.ArrayList;

public class FileNodeLeaf extends FileNode {



    public FileNodeLeaf(String id,String fullpath) {
        super(id);
        _fullpath = fullpath;
    }



    @Override
    public void Add(FileNode file) {

    }

    @Override
    public void Remove(FileNode file) {

    }

    @Override
    public FileNode GetParent(FileNode file) {
        return parent;
    }

    @Override
    public ArrayList<FileNode> GetChildren() {
        return null;
    }

    @Override
    public boolean IsFile() {
        return true;
    }

    @Override
    public boolean IsDirectory() {
        return false;
    }

    public String FullPath( )
    {
        return _fullpath;

    }
}




