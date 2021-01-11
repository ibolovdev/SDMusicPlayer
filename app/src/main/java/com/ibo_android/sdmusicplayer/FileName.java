package com.ibo_android.sdmusicplayer;

import java.io.File;

public class FileName {
    private String fullPath;
    private char pathSeparator = '/';
    private char extensionSeparator = '.';
    private File _FileObject;

    public FileName(String filename) {
        fullPath = filename;
    }

    public FileName(File filename) {
        _FileObject = filename;
        fullPath = filename.getAbsolutePath();
    }


    public String extension() {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }


    public boolean hasextension() {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return dot != -1;
    }

    public File FileObject() {

        return _FileObject;
    }

    // gets filename without extension
    public String filename() {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }

    public String path() {
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(0, sep);
    }



    public boolean isDirectory()
    {

        FileName fn = new FileName(_FileObject.getAbsolutePath());

        if (fn.hasextension()  )
            return false;

        return true;
    }
}
