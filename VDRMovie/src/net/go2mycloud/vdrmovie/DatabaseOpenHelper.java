package net.go2mycloud.vdrmovie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private Context context;
	public static final String TBL_CHANNELS = "ChannelsTbl";
	public static final String TBL_ID = "_id";
	public static final String CHANNELS_DB = "db";
	public static final String CHANNELS_NUM = "num";
	public static final String CHANNELS_NAME = "name";
	public static final String CHANNELS_SERVICE = "service";

	public static final String TBL_EVENT = "EventTbl";
	public static final String EVENT_ID = "rowid";
	public static final String EVENT_CHANNELS_KEY = "ch_key";
	public static final String EVENT_NR = "nr";
	public static final String EVENT_DB = "db";
	public static final String EVENT_TIME = "time";
	public static final String EVENT_DURATION = "dr";
	public static final String EVENT_TITLE = "tt";
	public static final String EVENT_STITLE = "st";
	public static final String EVENT_REGIE = "rt";
	public static final String EVENT_GENRE = "gt";
	public static final String EVENT_TYPE = "gr";
	public static final String EVENT_HASH_KEY = "hsh_key";

	public static final String TBL_REC = "RecordingsTbl";
	public static final String REC_CHANNELS_KEY = "ch_key";
	public static final String REC_DB = "db";
	public static final String REC_E_NR = "nr";
	public static final String REC_E_TIME = "time";
	public static final String REC_E_DURATION = "dr";
	public static final String REC_E_TITLE = "tt";
	public static final String REC_E_STITLE = "st";
	public static final String REC_E_REGIE = "rt";
	public static final String REC_E_GENRE = "gt";
	public static final String REC_WATCH = "wt";	
	public static final String REC_HASH_KEY = "hsh_key";
	
	public static final String TBL_TIM = "TimerTbl";
	public static final String TIM_DB = "db";
	public static final String TIM_STATUS = "status";
	public static final String TIM_CHANNELS_KEY = "ch_key";
	public static final String TIM_EVENT_KEY = "event_key";
	public static final String TIM_NR = "t_nr";
	public static final String TIM_EVENT_NR = "e_nr";
	public static final String TIM_DATE = "date";
	public static final String TIM_START = "start_t";
	public static final String TIM_STOP = "stop_t";
	public static final String TIM_PRI = "pri";
	public static final String TIM_REM = "rem";
	public static final String TIM_DIR = "dir";
	public static final String TIM_TITLE = "tt";
	public static final String TIM_ST = "start_s";
	public static final String TIM_SP = "stop_s";

	
	public static final String TBL_HASH = "HashTbl";
	public static final String IDX_HASH = "HashTblIdx";
	public static final String HASH_DB = "db";
	public static final String HASH = "hsh";
	public static final String HASH_DATA_KEY = "data_key";

	public static final String TBL_DATA = "DataTbl";
	public static final String DATA_NR = "nr";
	public static final String DATA_DETAILS = "Dt";
	
	public static final String TBL_CURSOR = "CursorTbl";
	public static final String C_CHANNELS_KEY = "ch_key";
	public static final String C_TIME = "time";
	public static final String C_DURATION = "dr";
	public static final String C_TITLE = "tt";
	public static final String C_STITLE = "st";
	public static final String C_REGIE = "rt";
	public static final String C_GENRE = "gt";
	public static final String C_MOVIEM = "mm";
	public static final String C_DATA_KEY = "data_key";
	public static final String C_E_1 = "e1"; //Watched or TimerCreated
	
	
	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 14;

	public static final String createTblCursor = "CREATE TABLE "+ TBL_CURSOR + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ C_CHANNELS_KEY + " integer not null, "
    		+ C_TIME + " integer not null, "
    		+ C_DURATION + " integer not null, "
    		+ C_TITLE + " text, "
    		+ C_STITLE + " text, "
    		+ C_REGIE + " text, " 
    		+ C_GENRE + " text, " 
    		+ C_MOVIEM + " integer default 0, " 
    		+ C_DATA_KEY + " integer not null, "
    		+ C_E_1 + " integer default 0 " 
    		+ ");";                 

	
	private static final String createTblChannels = "CREATE TABLE "+ TBL_CHANNELS + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ CHANNELS_NUM + " integer unique not null, "
    		+ CHANNELS_NAME + " text not null, "
    		+ CHANNELS_SERVICE + " text unique not null);";                 

	private static final String createTblEvents = "CREATE TABLE "+ TBL_EVENT + "( " 
//    		+ TBL_ID + " integer primary key auto increment, " 
    		+ EVENT_CHANNELS_KEY + " integer not null, "
    		+ EVENT_NR + " integer not null, "
    		+ EVENT_DB + " integer default 0, "
    		+ EVENT_TIME + " integer not null, "
    		+ EVENT_DURATION + " integer not null, "
    		+ EVENT_TITLE + " text, "
    		+ EVENT_STITLE + " text, "
    		+ EVENT_REGIE + " text, " 
    		+ EVENT_GENRE + " text, " 
    		+ EVENT_TYPE + " integer default 0, " 
    		+ EVENT_HASH_KEY + " integer not null, "
    		+ "FOREIGN KEY(" + EVENT_CHANNELS_KEY + ") REFERENCES " + TBL_CHANNELS + "(" + TBL_ID + "), "
    		+ "FOREIGN KEY(" + EVENT_HASH_KEY + ") REFERENCES " + TBL_HASH + "(" + TBL_ID + "), "
    		+ "primary key ( " + EVENT_CHANNELS_KEY + ", " + EVENT_NR + " ) "
    		+ ");";                 

	private static final String createTblRec = "CREATE TABLE "+ TBL_REC + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ REC_CHANNELS_KEY + " integer not null, "
    		+ REC_DB + " integer default 0, "
    		+ REC_E_NR + " integer not null, "
    		+ REC_E_TIME + " integer not null, "
    		+ REC_E_DURATION + " integer not null, "
    		+ REC_E_TITLE + " text, "
    		+ REC_E_STITLE + " text, "
    		+ REC_E_REGIE + " text, " 
    		+ REC_E_GENRE + " text, " 
    		+ REC_WATCH + " integert default 0, " 
    		+ REC_HASH_KEY + " integer not null, "
    		+ "FOREIGN KEY(" + REC_CHANNELS_KEY + ") REFERENCES " + TBL_CHANNELS + "(" + TBL_ID + ") "
    		+ ");";                 

	private static final String createTblHash = "CREATE TABLE "+ TBL_HASH + "( " 
    		+ TBL_ID + " integer primary key autoincrement, "
    		+ HASH_DB + " integer default 0, "
    		+ HASH + " integer unique not null, "
    		+ HASH_DATA_KEY + " integer, "
	        + "FOREIGN KEY(" + HASH_DATA_KEY + ") REFERENCES " + TBL_DATA + "(" + TBL_ID + "));";                 
	private static final String createIndexTblHash = "CREATE UNIQUE INDEX " + IDX_HASH + " ON "+ TBL_HASH + "(" + HASH + ");";
	
	private static final String createTblData = "CREATE TABLE "+ TBL_DATA + "( " 
    		+ TBL_ID + " integer primary key autoincrement, "
    		+ DATA_NR + " integer , "
    		+ DATA_DETAILS + " text not null);";                 

	private static final String createTblTimers = "CREATE TABLE "+ TBL_TIM + "( " 
    		+ TBL_ID + " integer primary key autoincrement, " 
    		+ TIM_DB + " integer default 0, "
    		+ TIM_STATUS + " integer not null, "
    		+ TIM_CHANNELS_KEY + " integer not null, "
    		+ TIM_EVENT_KEY + " integer not null, "
    		+ TIM_NR + " integer not null, "
    		+ TIM_EVENT_NR + " integer not null, "
    		+ TIM_DATE + " text, "
    		+ TIM_START + " text, "
    		+ TIM_STOP + " text, "
    		+ TIM_PRI + " integer, " 
    		+ TIM_DIR + " integer, " 
    		+ TIM_TITLE + " text not null, "
    		+ TIM_ST + " integer not null, "
    		+ TIM_SP + " integer not null, "
    		+ "FOREIGN KEY(" + TIM_EVENT_KEY + ") REFERENCES " + TBL_EVENT + "(" + EVENT_ID + "), "
    		+ "FOREIGN KEY(" + TIM_CHANNELS_KEY + ") REFERENCES " + TBL_CHANNELS + "(" + TBL_ID + ") "
    		+ ");";                 

	
	
	public DatabaseOpenHelper(Context context ) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		//Open your local db as the input stream
		try {
			    String inFileName = "/data/data/net.go2mycloud.vdrmovie/databases/events.db";
			    File dbFile = new File(inFileName);
			    FileInputStream fis;
					fis = new FileInputStream(dbFile);

			    String outFileName = Environment.getExternalStorageDirectory()+"/"+ DATABASE_VERSION  + "_events";
			    //Open the empty db as the output stream
			    OutputStream output;
					output = new FileOutputStream(outFileName);
			    //transfer bytes from the inputfile to the outputfile
			    byte[] buffer = new byte[1024];
			    int length;
			    while ((length = fis.read(buffer))>0){
			        output.write(buffer, 0, length);
			    }
			    //Close the streams
			    output.flush();
			    output.close();
			    fis.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		
		
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
		db.execSQL(createTblCursor);
		db.execSQL(createTblTimers);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onUpgrade(db, oldVersion, newVersion);
		//super.onDowngrade(db, oldVersion, newVersion);
	}

	@Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DatabaseOpenHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_TIM);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_CURSOR);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_EVENT);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_CHANNELS);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_HASH);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_DATA);
	    db.execSQL("DROP TABLE IF EXISTS " + TBL_REC);
	    onCreate(db);
	  }
	
	private void executeSQLScript(SQLiteDatabase database, String dbname ) { 		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
			try{
		inputStream = assetManager.open(dbname);
		while ((len = inputStream.read(buf)) != -1) {
		outputStream.write(buf, 0, len);
		}
		outputStream.close();
		inputStream.close();
		
		String[] createScript = outputStream.toString().split(";");
		for (int i = 0; i < createScript.length; i++) {
			String sqlStatement = createScript[i].trim();
			if (sqlStatement.length() > 0) {
				database.execSQL(sqlStatement + ";");
			}
		}
		} catch (IOException e){
		} catch (SQLException e) {
		}
	}
	
}
