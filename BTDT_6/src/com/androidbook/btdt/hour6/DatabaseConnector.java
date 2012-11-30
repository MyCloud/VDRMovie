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

		open();
		long rowId = database.insertOrThrow(DatabaseOpenHelper.TBL_CHANNELS,
				null, newChannel);
		close();
		return rowId;
	}

	public void deleteAllChannels() {
		open();
		database.execSQL("DELETE FROM " + DatabaseOpenHelper.TBL_CHANNELS);
		close();
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
}
