package net.go2mycloud.vdrmovie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
	public static final String EVENT_STITLE = "st";
	public static final String EVENT_REGIE = "rt";
	public static final String EVENT_GENRE = "gt";
	public static final String EVENT_HASH_KEY = "hsh_key";

	public static final String TBL_REC = "RecordingsTbl";
	public static final String REC_CHANNELS_KEY = "ch_key";
	public static final String REC_E_NR = "nr";
	public static final String REC_E_TIME = "time";
	public static final String REC_E_DURATION = "dr";
	public static final String REC_E_TITLE = "tt";
	public static final String REC_E_STITLE = "st";
	public static final String REC_E_REGIE = "rt";
	public static final String REC_E_GENRE = "gt";
	public static final String REC_WATCH = "wt";	
	public static final String REC_HASH_KEY = "hsh_key";

	
	public static final String TBL_HASH = "HashTbl";
	public static final String IDX_HASH = "HashTblIdx";
	public static final String HASH = "hsh";
	public static final String HASH_DATA_KEY = "data_key";

	public static final String TBL_DATA = "DataTbl";
	public static final String DATA_NR = "nr";
	public static final String DATA_DETAILS = "Dt";
	
	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 2;

	private static final String createTblChannels = "CREATE TABLE "+ TBL_CHANNELS + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ CHANNELS_NUM + " integer unique not null, "
    		+ CHANNELS_NAME + " text not null, "
    		+ CHANNELS_SERVICE + " text unique not null);";                 

	private static final String createTblEvents = "CREATE TABLE "+ TBL_EVENT + "( " 
//    		+ TBL_ID + " integer primary key auto increment, " 
    		+ EVENT_CHANNELS_KEY + " integer not null, "
    		+ EVENT_NR + " integer not null, "
    		+ EVENT_TIME + " integer not null, "
    		+ EVENT_DURATION + " integer not null, "
    		+ EVENT_TITLE + " text, "
    		+ EVENT_STITLE + " text, "
    		+ EVENT_REGIE + " text, " 
    		+ EVENT_GENRE + " text, " 
    		+ EVENT_HASH_KEY + " integer not null, "
    		+ "FOREIGN KEY(" + EVENT_CHANNELS_KEY + ") REFERENCES " + TBL_CHANNELS + "(" + TBL_ID + "), "
    		+ "FOREIGN KEY(" + EVENT_HASH_KEY + ") REFERENCES " + TBL_HASH + "(" + TBL_ID + "), "
    		+ "primary key ( " + EVENT_CHANNELS_KEY + ", " + EVENT_NR + " ) "
    		+ ");";                 

	private static final String createTblRec = "CREATE TABLE "+ TBL_REC + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ REC_CHANNELS_KEY + " integer not null, "
    		+ REC_E_NR + " integer not null, "
    		+ REC_E_TIME + " integer not null, "
    		+ REC_E_DURATION + " integer not null, "
    		+ REC_E_TITLE + " text, "
    		+ REC_E_STITLE + " text, "
    		+ REC_E_REGIE + " text, " 
    		+ REC_E_GENRE + " text, " 
    		+ REC_WATCH + " integert, " 
    		+ REC_HASH_KEY + " integer not null, "
    		+ "FOREIGN KEY(" + REC_CHANNELS_KEY + ") REFERENCES " + TBL_CHANNELS + "(" + TBL_ID + ") "
    		+ ");";                 

	private static final String createTblHash = "CREATE TABLE "+ TBL_HASH + "( " 
    		+ TBL_ID + " integer primary key autoincrement, "
    		+ HASH + " integer unique not null, "
    		+ HASH_DATA_KEY + " integer, "
	        + "FOREIGN KEY(" + HASH_DATA_KEY + ") REFERENCES " + TBL_DATA + "(" + TBL_ID + "));";                 
	private static final String createIndexTblHash = "CREATE UNIQUE INDEX " + IDX_HASH + " ON "+ TBL_HASH + "(" + HASH + ");";
	
	private static final String createTblData = "CREATE TABLE "+ TBL_DATA + "( " 
    		+ TBL_ID + " integer primary key autoincrement, "
    		+ DATA_NR + " integer , "
    		+ DATA_DETAILS + " text not null);";                 

	
	
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
		db.execSQL(createTblData);			
		db.execSQL(createTblHash);		
		db.execSQL(createIndexTblHash);
		db.execSQL(createTblRec);
	}

	@Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DatabaseOpenHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_CHANNELS);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_EVENT);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_HASH);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_DATA);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_REC);
	    onCreate(db);
	  }

}
