package com.androidbook.btdt.hour6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String TBL_CHANNELS = "ChannelsTbl";
	public static final String TBL_ID = "_id";
	public static final String CHANNELS_NUM = "num";
	public static final String CHANNELS_NAME = "name";
	public static final String CHANNELS_SERVICE = "service";

	public static final String TBL_EVENT = "EventTbl";
	public static final String EVENT_CHANNELS_KEY = "ch_key";
	public static final String EVENT_NR = "nr";
	public static final String EVENT_TIME = "time";
	public static final String EVENT_DURATION = "dr";
	public static final String EVENT_TITLE = "tt";
	public static final String EVENT_DETAILS = "dt";
	
	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 2;

	private static final String createTblChannels = "CREATE TABLE "+ TBL_CHANNELS + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ CHANNELS_NUM + " integer unique not null, "
    		+ CHANNELS_NAME + " text not null, "
    		+ CHANNELS_SERVICE + " text unique not null);";                 

	private static final String createTblEvents = "CREATE TABLE "+ TBL_EVENT + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ EVENT_CHANNELS_KEY + " integer not null, "
    		+ EVENT_NR + " integer not null, "
    		+ EVENT_TIME + " integer not null, "
    		+ EVENT_DURATION + " integer not null, "
    		+ EVENT_TITLE + " text, "
    		+ EVENT_DETAILS + " text, "
    		+ "FOREIGN KEY(" + EVENT_CHANNELS_KEY + ") REFERENCES " + TBL_CHANNELS + "(" + TBL_ID + "));";                 

	
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
		db.execSQL(createTblChannels);		
		db.execSQL(createTblEvents);		
		
	}

	@Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DatabaseOpenHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_CHANNELS);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_EVENT);
	    onCreate(db);
	  }

}
