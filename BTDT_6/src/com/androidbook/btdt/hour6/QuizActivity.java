package com.androidbook.btdt.hour6;

import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class QuizActivity extends Activity {
	public static final String PREFS_NAME = "MyPrefsFile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Date now = new Date();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_splash);
		SharedPreferences settings = getSharedPreferences( getString(R.string.pref_file) , 0);
	    // read last date if null than use current date
		String last = settings.getString("lastDate", now.toString());
	    //Log.i(PREFS_NAME, last );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quiz_splash, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Date now = new Date();
		// save all settings
		SharedPreferences settings = getSharedPreferences(getString(R.string.pref_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putString("LastDate", now.toString());
		prefEditor.commit();
		super.onPause();
	}

}
