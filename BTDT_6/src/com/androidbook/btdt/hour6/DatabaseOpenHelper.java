package com.androidbook.btdt.hour6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String TBL_CHANNELS = "tblChannels";
	public static final String TBL_ID = "_id";
	public static final String CHANNELS_NUM = "num";
	public static final String CHANNELS_NAME = "name";
	public static final String CHANNELS_SERVICE = "service";
	
	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 1;

	private static final String createTblChannels = "CREATE TABLE "+ TBL_CHANNELS + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ CHANNELS_NUM + " integer unique not null, "
    		+ CHANNELS_NAME + " name text not null, "
    		+ CHANNELS_SERVICE + " text unique not null);";                 

	
	public DatabaseOpenHelper(Context context ) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* public SQLiteDatabase getWritableDatabase() {
		// TODO Auto-generated method stub
		return null;
	} */

	@Override
	public void onCreate(SQLiteDatabase db) {		
		// TODO Auto-generated method stub
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_CHANNELS);
		db.execSQL(createTblChannels);		
		
	}

	@Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DatabaseOpenHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_CHANNELS);
	    onCreate(db);
	  }

}
