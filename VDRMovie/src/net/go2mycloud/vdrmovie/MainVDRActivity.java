package net.go2mycloud.vdrmovie;



import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ActionBar.OnNavigationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
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
import android.view.Window;
//import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainVDRActivity extends Activity implements OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private DownloadVDR downloader;
    private CustomEventAdapter customAdapter;
	private DatabaseConnector datasource;
	ListView listView;
	
	ProgressDialog pleaseWaitDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_vdr);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


		// Set up the dropdown list navigation in the action bar.
		/** Create an array adapter to populate dropdownlist */
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActionBarThemedContextCompat(), R.array.MainMenu, android.R.layout.simple_spinner_item);
		//? adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		/** Enabling dropdown list navigation for the action bar */
//		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);


		/** Defining Navigation listener */
/*		OnNavigationListener navigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				Toast.makeText(getBaseContext(), "You selected : " + itemPosition,
						Toast.LENGTH_SHORT).show();
				getActionBar().setSelectedNavigationItem(itemPosition);
				return false;
			}
		};
*/			

		/**
		 * Setting dropdown items and item navigation listener for the actionbar
		 */
		actionBar.setListNavigationCallbacks(adapter, this);
		actionBar.setSelectedNavigationItem(0);

		// Start loading the questions in the background
		downloader = new DownloadVDR(this.getBaseContext());
		
		//downloader.execute("test", "test2");
		// open database need to make sure the context is not gone while assess
		// database
		try {
			datasource = new DatabaseConnector(this.getBaseContext());
		} catch (SQLException e) {
			throw new Error("Error copying database");
		}
		//final ListView listView = (ListView) findViewById(R.id.list_events);
		listView = (ListView) findViewById(R.id.list_events);
		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				  // open detail window with selected event
				  //Cursor c = ((SimpleCursorAdapter)l.getAdapter()).getCursor();
				  //long c = (long)parent.getItemIdAtPosition(position);
				  //(view)parent moveToPosition(position);
				  //customAdapter.getItem(position) getCursor(). setPosition();
				  
				  customAdapter.setSelected(position);
				  listView.setSelection(position);
				  //Cursor cursor = ((AdapterView) parent).getCursor();
				  //cursor.moveToPosition(position);
				  updateDetailEvent(parent, view, position, id);
//					startActivity(new Intent(MainVDRActivity.this, DetailEventVDRActivity.class));
//					MainVDRActivity.this.finish();
				  
			    //Toast.makeText(getApplicationContext(),
			    //  "Click ListItem Number " + position, Toast.LENGTH_LONG)
			    //  .show();
			  }
			}); 

      

       // Database query can be a time consuming task ..
       // so its safe to call database query in another thread

//       Thread thread = new Thread() {

  //         public void run() {
      //Looper.prepare();

         	  datasource.open();

//               customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getNowEvents(), CursorAdapter.NO_SELECTION);



  //            listView.setAdapter(customAdapter);
              //datasource.close();
  //         }
   //    };
    //   thread.start();
		

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
	  public void updateDetailEvent(AdapterView<?> parent, View view, int position, long id) {
		    DetailEventVDRFragment fragment = (DetailEventVDRFragment) getFragmentManager()
		        .findFragmentById(R.id.detailFragment);
		    if (fragment != null && fragment.isInLayout()) {
		      fragment.updateEventInfo(position);
		    } else {
			      Intent intent = new Intent(this.getApplicationContext(),
				          DetailEventVDRActivity.class);
//			      Intent intent = new Intent(getActivity().getApplicationContext(),
//				          DetailActivity.class);
		      intent.putExtra("value", "2012");
		      startActivity(intent);

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
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(getBaseContext(), "You selected : " + itemPosition,
				Toast.LENGTH_SHORT).show();
		if( customAdapter == null ) {
    		customAdapter = new CustomEventAdapter(MainVDRActivity.this, null, CursorAdapter.NO_SELECTION , itemPosition );        	
		}
		if(itemPosition == 0) {
//    		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getNowEvents(), CursorAdapter.NO_SELECTION , itemPosition );        	
            customAdapter.swapCursor(datasource.getNowEvents());
        }
        if(itemPosition == 1) {
//    		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getNextEvents(), CursorAdapter.NO_SELECTION, itemPosition);        	
            customAdapter.swapCursor(datasource.getNextEvents());
      }
        if(itemPosition == 2) {
    		//customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getSheduledEvents(), CursorAdapter.NO_SELECTION, itemPosition);      
        }
        if(itemPosition == 3) {
 //   		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getMovieEvents(), CursorAdapter.NO_SELECTION, itemPosition);      
            customAdapter.swapCursor(datasource.getMovieEvents());
        }
        if(itemPosition == 4) {
 //   		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getRecordedEvents(), CursorAdapter.NO_SELECTION, itemPosition);      
            customAdapter.swapCursor(datasource.getRecordedEvents());
        }
        customAdapter.setType(itemPosition);
        customAdapter.setSelected(0);
        if( customAdapter.getCursor() != null ) {
        	customAdapter.getCursor().moveToFirst();
        }
        listView.setAdapter(customAdapter);
		return false;
	}
}
