package net.go2mycloud.vdrmovie;


import android.app.Activity;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

public class VDRActivity extends Activity {
//	public static final String PREFS_NAME = "MyPrefsFile";

	protected DatabaseConnector datasource;
	private int ViewType;
	private int ViewEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			datasource = new DatabaseConnector(this.getBaseContext());
       	    datasource.open();

		} catch (SQLException e) {
			throw new Error("Error copying database");
		}
		SharedPreferences settings = getSharedPreferences( getString(R.string.preference_file) , 0);
	    // read last date if null than use current date
		ViewType = settings.getInt( getString(R.string.pref_view_type), 0 );
		ViewEvent = settings.getInt( getString(R.string.pref_view_event), 0 );

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		try {
			datasource = new DatabaseConnector(this.getBaseContext());
       	    datasource.open();

		} catch (SQLException e) {
			throw new Error("Error copying database");
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (datasource != null) {
			datasource.close();
		}
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_type), ViewType);
		prefEditor.putInt(getString(R.string.pref_view_event), ViewEvent);
		prefEditor.commit();

	}

	public int getViewType() {
		return ViewType;
	}

	public void setViewType(int viewType) {
		ViewType = viewType;
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_type), ViewType);
		prefEditor.commit();
	}

	public int getViewEvent() {
		return ViewEvent;
	}

	public void setViewEvent(int viewEvent) {
		ViewEvent = viewEvent;
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_event), ViewEvent);
		prefEditor.commit();
	}



	

}
