package net.go2mycloud.vdrmovie;



import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.ActionBar.OnNavigationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.SQLException;
import android.os.Build;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.NavUtils;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainVDRActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private DownloadVDR downloader;
    private CustomEventAdapter customAdapter;

	ProgressDialog pleaseWaitDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_vdr);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		/** Create an array adapter to populate dropdownlist */
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActionBarThemedContextCompat(), R.array.MainMenu, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		/** Enabling dropdown list navigation for the action bar */
//		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);


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
		actionBar.setListNavigationCallbacks(adapter, navigationListener);
		actionBar.setSelectedNavigationItem(2);

		// Start loading the questions in the background
		net.go2mycloud.vdrmovie.DatabaseConnector datasource;
		//downloader.execute("test", "test2");
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
      //Looper.prepare();

         	  datasource.open();

               customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getNowChannels(), CursorAdapter.NO_SELECTION);



              listView.setAdapter(customAdapter);
		
		

	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Toast.makeText(this, "Menu settings", Toast.LENGTH_SHORT).show();
			// The animation has ended, transition to the Main Menu screen
			//startActivity(new Intent(QuizMenuActivity.this, QuizHelpActivity.class));
			//QuizMenuActivity.this.finish();
			break;
		case R.id.menu_update:
			Toast.makeText(this, "Menu update", Toast.LENGTH_SHORT).show();
			pleaseWaitDialog = ProgressDialog.show(MainVDRActivity.this,
					"VDR Guid", "Downloading VDR Guid data", true, true);
			pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					Log.d("onOptionsItemSelected" , "onCancel ");
					downloader.cancel(true);
				}
			});
			if(downloader.getStatus() == AsyncTask.Status.FINISHED ) {
				downloader = new DownloadVDR(this.getBaseContext());
			}
			if(downloader.getStatus() == AsyncTask.Status.PENDING){
				downloader.execute("");
			}

			break;
		case android.R.id.home:
			//Intent intent = new Intent(this, QuizSplashActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(intent);
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_vdr, menu);
//		return super.onCreateOptionsMenu(menu);

		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}

}
