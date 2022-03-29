package com.ibo_android.sdmusicplayer;


import java.util.ArrayList;

public class FileNodeComposite extends FileNode
{

    public ArrayList<FileNode> children;

    public FileNodeComposite(String id) {
        super(id);
        children = new ArrayList<>();
    }


    @Override
    public void Add(FileNode file)
    {
        children.add(file);
        file.parent = this;
    }

    @Override
    public void Remove(FileNode file)
    {
        children.remove(file);
        file.parent = null;
    }

    @Override
    public FileNode GetParent(FileNode file) {
        return parent;
    }

    @Override
    public ArrayList<FileNode> GetChildren()
    {
        return children;
    }

    @Override
    public boolean IsFile() {
        return false;
    }

    @Override
    public boolean IsDirectory() {
        return true;
    }
}

