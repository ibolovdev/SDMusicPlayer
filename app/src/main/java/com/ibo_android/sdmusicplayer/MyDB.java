package com.ibo_android.sdmusicplayer;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context; 
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.content.ContentValues;
import android.database.Cursor;



public class MyDB {
	
	//private final Context con;
	private MyDBHelper dbhelper;
	private SQLiteDatabase db;
	
	public MyDB(Context c)
	{
	//	con = c;
		dbhelper = new MyDBHelper( c, "MySongs",null,2 );		
	}
	
	public void close()
	{	
		dbhelper.close();
		db.close();		
	}
	
	
	public void open()
	{
			try
			{
				db = dbhelper.getWritableDatabase();				
			}
			catch(SQLiteException ex)
			{
				db = dbhelper.getReadableDatabase();				
			}		
	}
	
	public Cursor ShowAll(String playlist)
	{				
		try
		{			 
			 String[] collist = new String[]{MyDBHelper.CODE_COL,MyDBHelper.TITLE_COL,
					 						MyDBHelper.FILEPATH_COL,MyDBHelper.PLAYLIST_COL,
					 						MyDBHelper.WASPLAYING_COL, MyDBHelper.SEQ_NUM_COL };
			
			// String selection = MyDBHelper.PLAYLIST_COL + " = '" + playlist +"'";   
			//return db.query(MyDBHelper.TABLE_NAME,collist,selection, null, null, null, null);
			
			
			 String selection = MyDBHelper.PLAYLIST_COL + " = ? ";   
				return db.query(MyDBHelper.TABLE_NAME,collist,selection, new String[] { playlist }, null, null, null);
			
			//p_query = "select * from mytable where name_field = ?";
			//mDb.rawQuery(p_query, new String[] { uvalue });
			 
		}
		catch(SQLiteException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	
	public Cursor ShowPlaylists( )
	{
	 	
		try
		{			 
			 String[] collist = new String[]{ MyDBHelper.PLAYLIST_COL};		 
			 return db.query(MyDBHelper.TABLE_NAME,collist,null, null, MyDBHelper.PLAYLIST_COL, null,  MyDBHelper.PLAYLIST_COL);
		}
		catch(SQLiteException ex)
		{
			return null;
		}
	}	
	
	public long deletePlaylist(String playlist)
	{
		try
		{		 
			//String where = MyDBHelper.PLAYLIST_COL + " = " + "'" + playlist + "'" ;			
			//return db.delete( MyDBHelper.TABLE_NAME,  where,   null) ;
			
			String where = MyDBHelper.PLAYLIST_COL + " =  ? ";			
			return db.delete( MyDBHelper.TABLE_NAME,  where,   new String[] { playlist }) ;
			
		}
		catch(Exception ex)
		{
			return -1;
		}	
		
	}	 
	
	public long deleteEntry(String code)
	{
		//try
		//{
			 
			String where = MyDBHelper.CODE_COL + " = ? "  ;			
			return db.delete( MyDBHelper.TABLE_NAME,  where,   new String[] { code }) ;
			
		//}
		//catch(Exception ex)
		//{
		//	return -1;
		//}		
		
	}	
	
	public long deleteAll()
	{
		try
		{		
			return db.delete( MyDBHelper.TABLE_NAME,    null ,   null) ;			
		}
		catch(Exception ex)
		{
			
			return -1;
		}	
		
	}	//deleteAll
	
	
	public long deletePlayLists(ArrayList<String> pls)
	{
		if ((pls == null) ||  pls.isEmpty() ) 
			return -1;
		
		String where ="";
		
		
	/*	for (String pl: pls) {
		    
			if (where == "" )
			{
				where = MyDBHelper.PLAYLIST_COL + " = " + "'" + pl + "'" ;	
			}
			else
			{
				where = where + " OR " + MyDBHelper.PLAYLIST_COL + " = " +  "'" + pl + "'" ;
			}
			
			return db.delete( MyDBHelper.TABLE_NAME,  where,   null) ;
		   	
		}*/	
		
		ArrayList<String>  vals = new ArrayList<String>(); 
		for (String pl: pls) 
		{		    
			if (where == "" )
			{
				where = MyDBHelper.PLAYLIST_COL + " = ? "   ;	
				vals.add(pl);
			}
			else
			{
				where = where + " OR " + MyDBHelper.PLAYLIST_COL + " = ? "   ;
				vals.add(pl);
			}
		   	
		}	
		
		String[] arr = vals.toArray(new String[vals.size()]);
			
		return db.delete( MyDBHelper.TABLE_NAME,  where,   arr) ;
			
	}	//deletePlayLists
	
	
	public long updateEntry(String code,String title,String filepath,String playlist,String wasplaying,String seqnum )
	{	
		
		try
		{			 
			// works only with code!!!
			//String where = " code =  " + code;
			
			String where = " code = ? " ;
		/*	if (!(code==""))
			 {
				where =   " code = "  + code;
				 
			 }
			 else if (!(title==""))
			 {
				 
				 where =   " title = " + "'" + title  + "'";
			 }
			 else
				 return -1;*/		
			
			ContentValues cvals = new ContentValues( );
		 
			cvals.put(MyDBHelper.TITLE_COL,title);
			cvals.put(MyDBHelper.FILEPATH_COL,filepath);
			cvals.put(MyDBHelper.PLAYLIST_COL,playlist);
			cvals.put(MyDBHelper.WASPLAYING_COL,wasplaying);		
			cvals.put(MyDBHelper.SEQ_NUM_COL,seqnum);
			
			return db.update(MyDBHelper.TABLE_NAME, cvals, where ,     new String[] { code }) ;
			 
		}
		catch(SQLiteException ex)
		{
			return -1;
		}
	}	
	
	public long insertEntry(String title,String filepath,String playlist,String wasplaying, String seqnum )
	{	 
			ContentValues cvals = new ContentValues( );
			cvals.put(MyDBHelper.TITLE_COL,title);
			cvals.put(MyDBHelper.FILEPATH_COL,filepath);
			cvals.put(MyDBHelper.PLAYLIST_COL,playlist);
			cvals.put(MyDBHelper.WASPLAYING_COL,wasplaying);
			cvals.put(MyDBHelper.SEQ_NUM_COL,seqnum);
		 
			return db.insert(MyDBHelper.TABLE_NAME, null, cvals);

		//String s = GetContentValues(cvals);

		//try
		//{
			//return db.insert(MyDBHelper.TABLE_NAME, null, cvals);
		//}
		//catch(Exception ex)
		//{

			//throw ex;
			//throw new Exception( GetContentValues(cvals),ex);
		//}
	}

	//to do - test it
	private String GetContentValues(ContentValues cvals)
	{
		String res="";

		java.util.Set<java.util.Map.Entry<String, Object>> s=cvals.valueSet();
		for (Map.Entry<String, Object> entry : s)
		{
				res += " " + entry.getKey() + " : " +  entry.getValue();
		}

		return res;
	}


}
