package com.ibo_android.sdmusicplayer;

import java.io.File;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicFile implements Parcelable, Comparable<MusicFile>
{				
	String code;
	String title;
	String filepath;
	public byte bIsRootDirectory = 0;
	int CurrentPosition = 0;
	public byte bRepeat = 0;
	public byte bInError = 0;
	int seqnum = 0; 
	
	public MusicFile (String cod, String tlt, String fpath, int seqnumber)
	{	 
		code = cod;
		title = tlt;
		filepath = fpath;	
		seqnum = seqnumber;
	}
	
	public MusicFile (Parcel in) {
        readFromParcel(in);
    }
	
	  public static final Parcelable.Creator<MusicFile> CREATOR
		      = new Parcelable.Creator<MusicFile>() {
		  public MusicFile createFromParcel(Parcel in) {
		      return new MusicFile(in);
		  }
		
		  public MusicFile[] newArray(int size) {
		      return new MusicFile[size];
		  }
		};


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dst, int flags) {
		// TODO Auto-generated method stub
		 dst.writeString(code);
		 dst.writeString(title);
		 dst.writeString(filepath);
		 dst.writeInt(CurrentPosition);
		 dst.writeByte(bIsRootDirectory);
		 dst.writeByte(bRepeat);
		 dst.writeByte(bInError);
		 dst.writeInt(seqnum);
		 
		 
	} 
	
	  private void readFromParcel(Parcel in) {
		  code = in.readString();
		  title = in.readString();
		  filepath = in.readString();         
          CurrentPosition = in.readInt();
          bIsRootDirectory = in.readByte();
          bRepeat = in.readByte();
          bInError = in.readByte();
          seqnum = in.readInt();
  }	 

	/*public int compareTo(MusicFile another) {
				
		File f_this = new File(this.filepath);
		File f_another = new File(another.filepath);
		
		if (f_this.isDirectory() && !f_another.isDirectory())
			return -1;
				
		if (!f_this.isDirectory() && f_another.isDirectory())
			return 1;
			
		return this.filepath.compareTo(another.filepath);
				
	}*/


	public int compareTo(MusicFile another) {

		File f_this = new File(this.filepath);
		File f_another = new File(another.filepath);

		if (isDirectory(f_this) && !isDirectory(f_another))
			return -1;

		if (!isDirectory(f_this) && isDirectory(f_another))
			return 1;

		return this.filepath.compareTo(another.filepath);

	}


		private boolean isDirectory(File file)
		{

			FileName fn = new FileName(file.getAbsolutePath());

			if (fn.hasextension()  )
				return false;

			return true;
		}




	
	
	public static boolean Contains(ArrayList<MusicFile> al, MusicFile mf)
	{		
		for (MusicFile mfile : al ) {
			 
			if(mfile.compareTo(mfile) == 0)
			{
				return true;				
			}
			
		}	
		
		return false;
	}
	
	public static MusicFile Search(ArrayList<MusicFile> al, MusicFile mf)
	{		
		for (MusicFile mfile : al ) {
			 
			if(mfile.compareTo(mf) == 0)
			{
				return mfile;				
			}
			
		}	
		
		return null;
	}


}//MusicFile