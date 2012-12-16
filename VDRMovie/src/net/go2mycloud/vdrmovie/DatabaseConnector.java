package net.go2mycloud.vdrmovie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseConnector {

	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;

	public DatabaseConnector(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context);
	}

	public void open() throws SQLException {
		// open database in reading/writing mode
		database = dbOpenHelper.getWritableDatabase();
		  Log.d(" DatabaseConnector", "open" );
	}

	public void close() {
		if (database != null) {
			  Log.d(" DatabaseConnector", "close" );
			database.close();
		}
	}

	public long insertChannel(int num, String name, String service) {
		

		// first check if same num is in database
		Cursor c = getOneChannelNum(num);
		if ( c != null ) {
			if ( c.getCount() < 1) {
				// channel num not found
				// now check is same service in in table
				c = getOneChannelService(service);
				if ( c != null & c.getCount() < 1) {
					// new record
					ContentValues newChannel = new ContentValues();
					newChannel.put(DatabaseOpenHelper.CHANNELS_NUM, num);
					newChannel.put(DatabaseOpenHelper.CHANNELS_NAME, name);
					newChannel.put(DatabaseOpenHelper.CHANNELS_SERVICE, service);
					return database.insertOrThrow(DatabaseOpenHelper.TBL_CHANNELS,
							null, newChannel);
				}
				c.moveToFirst();
				
			} else {
				// there is a channel with same num
				if (c.moveToFirst() ) {
					// if name or service is different
					if( service.contentEquals(c.getString(c.getColumnIndex(DatabaseOpenHelper.CHANNELS_SERVICE)))) {
						// same service
						if( name.contentEquals(c.getString(c.getColumnIndex(DatabaseOpenHelper.CHANNELS_NAME)))) {
							// and same name return channel id
							return c.getLong(c.getColumnIndex(DatabaseOpenHelper.TBL_ID));
						}
					}
				}
			}
		}
		// delete found id
		deleteChannelId( c.getLong(c.getColumnIndex(DatabaseOpenHelper.TBL_ID)));
		// insert new channel recursive
		return insertChannel(num, name, service );
	}

	private void cleanCursorTbl()
	{
		//database.execSQL("TRUNCATE TABLE DatabaseOpenHelper.TBL_CURSOR" );
		database.execSQL("drop table " + DatabaseOpenHelper.TBL_CURSOR );
		database.execSQL(DatabaseOpenHelper.createTblCursor );

	}
	/*
	 *    		+ C_CHANNELS_KEY + " integer not null, "
    		+ C_TIME + " integer not null, "
    		+ C_DURATION + " integer not null, "
    		+ C_TITLE + " text, "
    		+ C_STITLE + " text, "
    		+ C_REGIE + " text, " 
    		+ C_GENRE + " text, " 
    		+ C_MOVIEM + " integer default 0, " 
    		+ C_DATA_KEY + " integer not null, "
    		+ C_E_1 + " integert default 0 " 
    		+ ");";                 

	 */
	public Cursor getCursor() {
		String buildSQL = "select * from " + DatabaseOpenHelper.TBL_CURSOR ;

		return database.rawQuery(buildSQL, null );
		
	}
	
	
	public void setCursorNowEvents () 
	{
		cleanCursorTbl();
		long unixTime = System.currentTimeMillis() / 1000L;
		String buildSQL = 
				"insert into CursorTbl ( ch_key, time, dr, tt, st, rt, gt, mm, data_key ) " +
				"select EventTbl.ch_key, EventTbl.time, EventTbl.dr, EventTbl.tt, EventTbl.st, EventTbl.rt, EventTbl.gt, DataTbl.nr, DataTbl._id " +  
				"from EventTbl, HashTbl, DataTbl where time = ( select max(time) " +
				"from EventTbl as f where f.ch_key == EventTbl.ch_key and f.time < ? ) " + 
				"and EventTbl.hsh_key = HashTbl._id and HashTbl.data_key = DataTbl._id"; 
		//database.rawQuery(buildSQL, null );
		Log.d(" DatabaseConnector", "getNextEvents unixtime:" + unixTime );
		Log.d(" DatabaseConnector", "sql:" + buildSQL );

		database.execSQL(buildSQL, new String [] { Long.toString(unixTime)} );
   }
	public Cursor getNextEvents() {
		long unixTime = System.currentTimeMillis() / 1000L;
		  String buildSQL = "select ch_key _id, ch_key, dr, gt, nr, hsh_key, rt, st, time, tt "
				  + "from EventTbl where time = ( select min(time) from EventTbl as f " 
				  + "where f.ch_key = EventTbl.ch_key and f.time >= " + Long.toString(unixTime) + " ); ";
		  Log.d(" DatabaseConnector", "getNextEvents unixtime:" + unixTime );
		  
		  return database.rawQuery(buildSQL, null);
	}
	public Cursor getMovieEvents() {

		String buildSQL = "select EventTbl.ch_key _id, EventTbl.ch_key, EventTbl.dr, EventTbl.gt, EventTbl.nr, EventTbl.hsh_key, EventTbl.rt, EventTbl.st, EventTbl.time, " + 
	  "EventTbl.tt, DataTbl.nr " +
	  "from EventTbl, HashTbl ,DataTbl " +
	  "where EventTbl.hsh_key = HashTbl._id and HashTbl.data_key = DataTbl._id and DataTbl.nr > 0";
		  return database.rawQuery(buildSQL, null);

	}	
	
	public Cursor getRecordedEvents() {
		String buildSQL = "select RecordingsTbl._id, RecordingsTbl.ch_key, RecordingsTbl.dr, RecordingsTbl.gt, RecordingsTbl.nr, RecordingsTbl.hsh_key, RecordingsTbl.rt, RecordingsTbl.st, RecordingsTbl.time, " +  
				  "RecordingsTbl.tt, DataTbl.nr "+ 
				  "from RecordingsTbl, HashTbl ,DataTbl " +
				  "where RecordingsTbl.hsh_key = HashTbl._id and HashTbl.data_key = DataTbl._id and DataTbl.nr > 0 order by RecordingsTbl.tt";	  

		
		//String buildSQL = "select * from RecordingsTbl ORDER BY tt";		  
		  return database.rawQuery(buildSQL, null);
	}

	
	public Cursor getNowChannels () 
	{
		Cursor c;   
		String buildSQL = "select * from ChannelsTbl";

		c = database.rawQuery(buildSQL, null);
		Log.d("testing", "aantal rows: " + c.getCount() );
		return c;
   }
//	select ch_key, dr, gt, nr, hsh_key, rt, st, time, tt 
//	  from EventTbl 
//	  where time = (
//	      select max(time) 
//	        from EventTbl as f 
//	        where f.ch_key = EventTbl.ch_key and
//	        f.time < 1354908900
//	  );
	
	
	
	
	public void deleteAllChannels() {
		//open();
		database.execSQL("DELETE FROM " + DatabaseOpenHelper.TBL_CHANNELS);
		//close();
	}
	public void deleteAllEvents() {
		//open();
		database.execSQL("DELETE FROM " + DatabaseOpenHelper.TBL_EVENT);
		//close();
	}
	public void deleteAllHash() {
		//open();
		database.execSQL("DELETE FROM " + DatabaseOpenHelper.TBL_HASH);
		//close();
	}

	public void updateContact(long id, String name, String cap, String code) {
		ContentValues editCon = new ContentValues();
		editCon.put("name", name);
		editCon.put("cap", cap);
		editCon.put("code", code);

		open();
		database.update("country", editCon, "_id=" + id, null);
		close();
	}

	public Cursor getAllContacts() {
		return database.query("country", new String[] { "_id", "name" }, null,
				null, null, null, "name");
	}

	public Cursor getOneContact(long id) {
		return database.query("country", null, "_id=" + id, null, null, null,
				null);
	}

	public void deleteChannelId(long id) {
		// delete all events related to this channel id
		deleteEventsChannelId(id);
		database.delete(DatabaseOpenHelper.TBL_CHANNELS, "_id=" + id, null);
		
	}
	
	private void deleteEventsChannelId(long id) {
		// de
		// delete all events that have this channel id
		database.delete(DatabaseOpenHelper.TBL_EVENT, DatabaseOpenHelper.EVENT_CHANNELS_KEY+ "=" + id, null);		
	}

	public long insertEvent(long ev_ch_key, int ev_nr, long ev_time, int ev_dr,
			String ev_tt, String ev_st, String ev_gt,String ev_rt, long ev_hsh_key) {

		// first check if same event is in database
		Cursor c = getOneEventChKeyEvNr( ev_ch_key, ev_nr );
		if ( c != null ) {
			if ( c.getCount() < 1) {
				// new event
				return insertEventNoCheck ( ev_ch_key, ev_nr, ev_time, ev_dr,
							ev_tt, ev_st, ev_gt,ev_rt, ev_hsh_key);
			} else {
				if (c.moveToFirst() ) {
					// return current HASH					
					return c.getLong(c.getColumnIndex(DatabaseOpenHelper.EVENT_HASH_KEY));				
				}
			}
		}
		return -1;
	}

	public long insertEventNoCheck(long ev_ch_key, int ev_nr, long ev_time, int ev_dr,
			String ev_tt, String ev_st, String ev_gt,String ev_rt, long ev_hsh_key) {
		// new event
		ContentValues newEvent = new ContentValues();
		newEvent.put(DatabaseOpenHelper.EVENT_CHANNELS_KEY , ev_ch_key);
		newEvent.put(DatabaseOpenHelper.EVENT_NR, ev_nr);
		newEvent.put(DatabaseOpenHelper.EVENT_TIME, ev_time);
		newEvent.put(DatabaseOpenHelper.EVENT_DURATION, ev_dr);
		newEvent.put(DatabaseOpenHelper.EVENT_TITLE, ev_tt);
		newEvent.put(DatabaseOpenHelper.EVENT_STITLE, ev_st);
		newEvent.put(DatabaseOpenHelper.EVENT_GENRE, ev_gt);
		newEvent.put(DatabaseOpenHelper.EVENT_REGIE, ev_rt);
		newEvent.put(DatabaseOpenHelper.EVENT_HASH_KEY, ev_hsh_key);
		return database.insertOrThrow(DatabaseOpenHelper.TBL_EVENT,
				null, newEvent);
	}

	public long insertRecNoCheck(long ev_ch_key, int ev_nr, long ev_time, int ev_dr,
			String ev_tt, String ev_st, String ev_gt,String ev_rt, int ev_wt, long ev_hsh_key) {
		// new event
		ContentValues newEvent = new ContentValues();
		newEvent.put(DatabaseOpenHelper.REC_CHANNELS_KEY , ev_ch_key);
		newEvent.put(DatabaseOpenHelper.REC_E_NR, ev_nr);
		newEvent.put(DatabaseOpenHelper.REC_E_TIME, ev_time);
		newEvent.put(DatabaseOpenHelper.REC_E_DURATION, ev_dr);
		newEvent.put(DatabaseOpenHelper.REC_E_TITLE, ev_tt);
		newEvent.put(DatabaseOpenHelper.REC_E_STITLE, ev_st);
		newEvent.put(DatabaseOpenHelper.REC_E_GENRE, ev_gt);
		newEvent.put(DatabaseOpenHelper.REC_E_REGIE, ev_rt);
		newEvent.put(DatabaseOpenHelper.REC_WATCH, ev_wt);
		newEvent.put(DatabaseOpenHelper.REC_HASH_KEY, ev_hsh_key);
		return database.insertOrThrow(DatabaseOpenHelper.TBL_REC,
				null, newEvent);
	}

	private Cursor getOneEventChKeyEvNr(long ev_ch_key, int ev_nr) {
		Cursor c = database.query(DatabaseOpenHelper.TBL_EVENT, null, 
				DatabaseOpenHelper.EVENT_CHANNELS_KEY + "=" + Long.toString(ev_ch_key) + " AND " +
				DatabaseOpenHelper.EVENT_NR + "=" + Integer.toString(ev_nr), null, null, null,
				null);
		return c;
	}

	public long insertHash( long ha_da_key, long ha_ha) {
		ContentValues newHash = new ContentValues();
		newHash.put(DatabaseOpenHelper.HASH_DATA_KEY , ha_da_key);
		newHash.put(DatabaseOpenHelper.HASH, ha_ha);

		//open();
		long rowId = database.insertOrThrow(DatabaseOpenHelper.TBL_HASH,
				null, newHash);
		//close();
		return rowId;
	}
	
	public long insertData( long da_nr, String da_dt) {
		ContentValues newData = new ContentValues();
		newData.put(DatabaseOpenHelper.DATA_NR , da_nr);
		newData.put(DatabaseOpenHelper.DATA_DETAILS, da_dt);

		//open();
		long rowId = database.insertOrThrow(DatabaseOpenHelper.TBL_DATA,
				null, newData);
		//close();
		return rowId;
	}

	public Cursor getOneHash(long value) {
		//open();

		Cursor c = database.query(DatabaseOpenHelper.TBL_HASH, null, 
				DatabaseOpenHelper.HASH + "=" + Long.toString(value), null, null, null,
				null);
		//close();
		return c;
	}

	public Cursor getOneChannelNum(int num) {
		// TODO Auto-generated method stub
		Cursor c = database.query(DatabaseOpenHelper.TBL_CHANNELS, null, 
				DatabaseOpenHelper.CHANNELS_NUM + "=" + Integer.toString(num), null, null, null,
				null);
		return c;
	}

	public Cursor getOneChannelService( String service) {
		// TODO Auto-generated method stub
		Cursor c = database.query(DatabaseOpenHelper.TBL_CHANNELS, null, 
				DatabaseOpenHelper.CHANNELS_SERVICE + "='" + service.toString() + "'", null, null, null,
				null);
		return c;
	}

	public void deleteChannelNum(int num) {
		// TODO Auto-generated method stub
		
	}

	public long findHashKeyEvent(long ev_ch_key, int ev_nr) {
		Cursor c = getOneEventChKeyEvNr( ev_ch_key, ev_nr );
		if ( c != null ) {
			if ( c.getCount() == 1) {
				if (c.moveToFirst() ) {
					// return current HASH					
					return c.getLong(c.getColumnIndex(DatabaseOpenHelper.EVENT_HASH_KEY));				
				}
			}
		}
		return -1;
	}

	public long getCannelIdService(String service) {
		Cursor c = database.query(DatabaseOpenHelper.TBL_CHANNELS, null, 
				DatabaseOpenHelper.CHANNELS_SERVICE + "='" + service + "'", null, null, null,
				null);
		if ( c != null ) {
			if ( c.getCount() == 1) {
				if (c.moveToFirst() ) {
					// return current HASH					
					return c.getLong(c.getColumnIndex(DatabaseOpenHelper.TBL_ID));				
				}
			}
		}
		return -1;
		
	}

	public long findHashKeyRec(long ev_ch_key, int ev_nr) {
		Cursor c = getOneRecChKeyEvNr( ev_ch_key, ev_nr );
		if ( c != null ) {
			if ( c.getCount() == 1) {
				if (c.moveToFirst() ) {
					// return current HASH					
					return c.getLong(c.getColumnIndex(DatabaseOpenHelper.EVENT_HASH_KEY));				
				}
			}
		}
		return -1;
	}

	private Cursor getOneRecChKeyEvNr(long ev_ch_key, int ev_nr) {
		Cursor c = database.query(DatabaseOpenHelper.TBL_REC, null, 
				DatabaseOpenHelper.REC_CHANNELS_KEY + "=" + Long.toString(ev_ch_key) + " AND " +
				DatabaseOpenHelper.REC_E_NR + "=" + Integer.toString(ev_nr), null, null, null,
				null);
		return c;
	}

	public Cursor getCursorDetails(int position) {
		// TODO Auto-generated method stub
		return database.query(DatabaseOpenHelper.TBL_CURSOR, null, 
				DatabaseOpenHelper.TBL_ID + "=" + position, null, null, null,
				null);
		
	}

	public Cursor getCursorDataDetails(long position) {
		return database.query(DatabaseOpenHelper.TBL_DATA, null, 
				DatabaseOpenHelper.TBL_ID + "=" + position, null, null, null,
				null);
	}

}



