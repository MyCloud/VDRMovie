package com.androidbook.btdt.hour6;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class QuizEventsActivity extends QuizActivity {

	private DatabaseConnector datasource;
   private CustomEventAdapter customAdapter;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_overview);
		ActionBar actionBar = getActionBar();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			actionBar.setHomeButtonEnabled(true);
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		// open database need to make sure the context is not gone while assess
		// database
		try {
			datasource = new DatabaseConnector(this.getBaseContext());
		} catch (SQLException e) {

			throw new Error("Error copying database");

		}
      final ListView listView = (ListView) findViewById(R.id.list_events);


      

       // Database query can be a time consuming task ..
       // so its safe to call database query in another thread

//       Thread thread = new Thread() {

  //         public void run() {


         	  datasource.open();

               customAdapter = new CustomEventAdapter(QuizEventsActivity.this, datasource.getNowChannels(), CursorAdapter.NO_SELECTION);



               listView.setAdapter(customAdapter);

 //          }



  //     };



    //   thread.start();



		
		
		
		
		
		
		
		
//		    Calendar c = Calendar.getInstance();
//		    c.set(Calendar.YEAR, 2012);
//		    c.set(Calendar.MONTH, 12);
//		    c.set(Calendar.DAY_OF_MONTH,8);
//		    c.set(Calendar.HOUR, 17);
//		    c.set(Calendar.MINUTE, 2);
//		    c.set(Calendar.SECOND, 17);
//		    c.set(Calendar.MILLISECOND, 0);
		    //java.util.Date time = new java.util.Date(timeStamp);

//		    Log.d("datum", " dit dus: " + Long.toString(c.getTimeInMillis() / 1000L) );
//		    c.set(Calendar.YEAR, 1970);
//		    c.set(Calendar.MONTH, 1);
//		    c.set(Calendar.DAY_OF_MONTH,1);
//		    c.set(Calendar.HOUR, 0);
//		    c.set(Calendar.MINUTE, 0);
//		    c.set(Calendar.SECOND, 1354982537);
//		    c.set(Calendar.MILLISECOND, 0);

//		    Log.d("datum", " dit dus:" + Long.toString(c.getTimeInMillis() / 1000L) );

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// Define the contextual action mode
		setContentView(R.layout.activity_quiz_overview);

		/** Create an array adapter to populate dropdownlist */
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.view_events, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(getBaseContext(),
		// android.R.layout.simple_spinner_dropdown_item, actions);

		/** Enabling dropdown list navigation for the action bar */
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		/** Defining Navigation listener */
		OnNavigationListener navigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				Toast.makeText(getBaseContext(), "You selected : " + itemPosition,
						Toast.LENGTH_SHORT).show();
				getActionBar().setSelectedNavigationItem(itemPosition);
				return false;
			}
		};

		/**
		 * Setting dropdown items and item navigation listener for the actionbar
		 */
		getActionBar().setListNavigationCallbacks(adapter, navigationListener);
		getActionBar().setSelectedNavigationItem(1);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
