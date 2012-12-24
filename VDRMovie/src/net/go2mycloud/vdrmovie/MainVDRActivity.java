package net.go2mycloud.vdrmovie;



import java.util.concurrent.ExecutionException;

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
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
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

public class MainVDRActivity extends VDRActivity  {


	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private CustomEventAdapter customAdapter;
	//private DatabaseConnector datasource;
	private DetailEventVDRView  detailEventVDRView;

	ListView listView;
//	Menu menu;
//	ProgressDialog pleaseWaitDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_main_vdr);
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		// Set up the dropdown list navigation in the action bar.
		/** Create an array adapter to populate dropdownlist */
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActionBarThemedContextCompat(), R.array.MainMenu, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
		/**
		 * Setting dropdown items and item navigation listener for the actionbar
		 */
		actionBar.setListNavigationCallbacks(adapter, this);
		actionBar.setSelectedNavigationItem( getViewType());

		
		Log.d("MainVDRActivity", "onCreate VieuwType " + getViewType() );
		

		listView = (ListView) findViewById(R.id.list_events);
		listView.setItemsCanFocus(true);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				  
				  customAdapter.setSelected(position);
				  listView.setItemChecked(position, true);

				  setViewEvent(position);
								  
				  updateDetailEvent(parent, view, position, id);
			  }
			}); 
		

	}


	  public void updateDetailEvent(AdapterView<?> parent, View view, int position, long id) {
		    DetailEventVDRFragment fragment = (DetailEventVDRFragment) getFragmentManager()
		        .findFragmentById(R.id.detailFragment);
			
		    if (fragment != null && fragment.isInLayout()) {
		      fragment.updateEventInfo(position, datasource);
		    } else {
			  Intent intent = new Intent(this.getApplicationContext(),
				          DetailEventVDRActivity.class);
		      intent.putExtra("position", Integer.toString(position) );
		      startActivity(intent);

		    }
		  }
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */


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
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		//Toast.makeText(getBaseContext(), "You selected : " + itemPosition,
		//		Toast.LENGTH_SHORT).show();
		
		if( customAdapter == null ) {
    		customAdapter = new CustomEventAdapter(MainVDRActivity.this, null, CursorAdapter.IGNORE_ITEM_VIEW_TYPE , itemPosition );        	
		}
		if (itemPosition != getViewType()) {
			setViewType(itemPosition);
		    invalidateOptionsMenu();

			if(itemPosition == 0 ) {
	            datasource.setCursorNowEvents();
	        }
	        if(itemPosition == 1) {
	            datasource.setCursorNextEvents();
	//    		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getNextEvents(), CursorAdapter.NO_SELECTION, itemPosition);        	
	            //customAdapter.swapCursor(datasource.getNextEvents());
	      }
	        if(itemPosition == 2) {
	            datasource.setCursorMovieEvents();
	    		//customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getSheduledEvents(), CursorAdapter.NO_SELECTION, itemPosition);      
	        }
	        if(itemPosition == 3) {
	//        	SVDRPInterface SVDRP;
	        	//svdrpInterface = new SVDRPInterface(MainVDRActivity.this);
	//        	svdrpInterface.doInBackground()
//				if(svdrpInterface.getStatus() == AsyncTask.Status.FINISHED ) {
//					svdrpInterface = new SVDRPInterface(MainVDRActivity.this);
//					svdrpInterface.execute("PLAY","Sint");
//				}
//				if (svdrpInterface.getStatus() == AsyncTask.Status.PENDING){
//		        	svdrpInterface.execute("PLAY","Sint");				
//				}
	 //   		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getMovieEvents(), CursorAdapter.NO_SELECTION, itemPosition);      
	            //customAdapter.swapCursor(datasource.getMovieEvents());
	        }
	        if(itemPosition == 4) {
	//			if(svdrpInterface.getStatus() == AsyncTask.Status.FINISHED ) {
	//				downloader = new DownloadVDR(MainVDRActivity.this);
	//			}
	//			if(downloader.getStatus() == AsyncTask.Status.PENDING){
	//				downloader.execute("");
	//			}
	
	        	
	        	datasource.setRecordedEvents();
	 //   		customAdapter = new CustomEventAdapter(MainVDRActivity.this, datasource.getRecordedEvents(), CursorAdapter.NO_SELECTION, itemPosition);      
	            //customAdapter.swapCursor(datasource.getRecordedEvents());
	        }
		}
        customAdapter.changeCursor(datasource.getCursor());
        customAdapter.setType(itemPosition);
        setViewEvent(itemPosition);
        customAdapter.setSelected(0);
        if( customAdapter.getCursor() != null ) {
        	customAdapter.getCursor().moveToFirst();
        }
        listView.setAdapter(customAdapter);
        
		return false;
	}
	public String getCursorDetails() {
		
		
		return null;
		
	}

	public CustomEventAdapter getCustomAdapter() {
		return customAdapter;
	}

	public DatabaseConnector getDatasource() {
		return datasource;
	}

	public DetailEventVDRView getDetailEventVDRView() {
		return detailEventVDRView;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        Log.d("onResume", "Done" );
	    invalidateOptionsMenu();

	}



	
	
}
