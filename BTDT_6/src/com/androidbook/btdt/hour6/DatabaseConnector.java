package com.androidbook.btdt.hour6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseConnector {

	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;

	public DatabaseConnector(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context);
	}

	public void open() throws SQLException {
		// open database in reading/writing mode
		database = dbOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null)
			database.close();
	}

	public long insertChannel(int num, String name, String service) {
		ContentValues newChannel = new ContentValues();
		newChannel.put(DatabaseOpenHelper.CHANNELS_NUM, num);
		newChannel.put(DatabaseOpenHelper.CHANNELS_NAME, name);
		newChannel.put(DatabaseOpenHelper.CHANNELS_SERVICE, service);

		//open();
		long rowId = database.insertOrThrow(DatabaseOpenHelper.TBL_CHANNELS,
				null, newChannel);
		//close();
		return rowId;
	}

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

	public void deleteContact(long id) {
		open();
		database.delete("country", "_id=" + id, null);
		close();
	}

	public long insertEvent(long ev_ch_key, int ev_nr, long ev_time, int ev_dr,
			String ev_tt, String ev_gt,String ev_rt, long ev_hsh_key) {
		ContentValues newEvent = new ContentValues();
		newEvent.put(DatabaseOpenHelper.EVENT_CHANNELS_KEY , ev_ch_key);
		newEvent.put(DatabaseOpenHelper.EVENT_NR, ev_nr);
		newEvent.put(DatabaseOpenHelper.EVENT_TIME, ev_time);
		newEvent.put(DatabaseOpenHelper.EVENT_DURATION, ev_dr);
		newEvent.put(DatabaseOpenHelper.EVENT_TITLE, ev_tt);
		newEvent.put(DatabaseOpenHelper.EVENT_GENRE, ev_gt);
		newEvent.put(DatabaseOpenHelper.EVENT_REGIE, ev_rt);
		newEvent.put(DatabaseOpenHelper.EVENT_HASH_KEY, ev_hsh_key);

		//open();
		long rowId = database.insertOrThrow(DatabaseOpenHelper.TBL_EVENT,
				null, newEvent);
		//close();
		return rowId;
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

}
