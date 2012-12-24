package net.go2mycloud.vdrmovie;


import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VDRActivity extends Activity implements OnNavigationListener {
//	public static final String PREFS_NAME = "MyPrefsFile";

	protected DatabaseConnector datasource;
	protected DownloadVDR downloader;
	protected SVDRPInterface svdrpInterface;
	private static int ViewType;
	private static int ViewEvent;
	private static int ViewState; //0 = default 1 = play recording
	private Menu menu;
	private MenuItem VolumeUp;
	private MenuItem VolumeDown;
	private MenuItem VolumeLongUp;
	private MenuItem VolumeLongDown;

	private boolean KeyInProgress = false;
	private boolean LongKeyInProgress = false;

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
		SharedPreferences settings = getSharedPreferences(
				getString(R.string.preference_file), 0);
		// read last date if null than use current date
		ViewType = settings.getInt(getString(R.string.pref_view_type), 0);
		ViewEvent = settings.getInt(getString(R.string.pref_view_event), 0);
		ViewState = settings.getInt(getString(R.string.pref_view_state), 0);
		// ViewState = settings.getInt( getString(R.string.pref_view_state),
		// R.id.menu_event_stop );


		Log.d("MainVDRActivity", "onCreate VieuwType " + getViewType());
		Log.d("MainVDRActivity", "onCreate ViewEvent " + getViewEvent());
		Log.d("MainVDRActivity", "onCreate VieuwStarte " + getViewState());

		// Start loading the questions in the background
		downloader = new DownloadVDR(VDRActivity.this);
		svdrpInterface = new SVDRPInterface(VDRActivity.this);
		
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);





	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) || (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
		    if ( LongKeyInProgress ) {
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
					onOptionsItemSelected( getVolumeLongUp());
					Log.d("onKeyLongPress", "up KeyInProgress:" + Boolean.toString(KeyInProgress));
				} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
					onOptionsItemSelected( getVolumeLongDown());
					Log.d("onKeyLongPress", "down KeyInProgress:" + Boolean.toString(KeyInProgress));
				}
		    } else {
		    	KeyInProgress = true;
				Log.d("onKeyDown", "KeyInProgress:" + Boolean.toString(KeyInProgress));
		    }
		    event.startTracking(); 
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) || (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			if (KeyInProgress) {
				KeyInProgress = false;
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
					onOptionsItemSelected(getVolumeUp());
				} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
					onOptionsItemSelected(getVolumeDown());
				}
			}
			if (LongKeyInProgress) {
				LongKeyInProgress = false;
			}
			return true;
	    }
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
	    	KeyInProgress = false;
	    	LongKeyInProgress = true;
			Log.d("onKeyLongPress", "up KeyInProgress:" + Boolean.toString(KeyInProgress));
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
	    	KeyInProgress = false;
	    	LongKeyInProgress = true;
			Log.d("onKeyLongPress", "down KeyInProgress:" + Boolean.toString(KeyInProgress));
			return true;
		}
	    return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_event_live:
			TuneToCurrentEvent(item.getItemId());
			break;
		case R.id.menu_event_play:
			if ( getViewState() == R.id.menu_event_stop) {
				PLayCurrentEvent(item.getItemId());
			} else {
				sendKey( item.getItemId(), item.getTitle().toString() );
			}
			break;
		case R.id.menu_event_voldown:
		case R.id.menu_event_volup:
			sendKey( item.getTitle().toString() );
			break;
		case R.id.menu_event_stop:
		case R.id.menu_event_start:
		case R.id.menu_event_end:
		case R.id.menu_event_pause:
		case R.id.menu_event_fwd:
		case R.id.menu_event_rev:
			sendKey( item.getItemId(), item.getTitle().toString() );
			break;	
		case R.id.menu_settings:
			Toast.makeText(this, "Menu settings", Toast.LENGTH_SHORT).show();
			// The animation has ended, transition to the Main Menu screen
			//startActivity(new Intent(QuizMenuActivity.this, QuizHelpActivity.class));
			//QuizMenuActivity.this.finish();
			break;
		case R.id.menu_update:
			if(downloader.getStatus() == AsyncTask.Status.FINISHED ) {
				downloader = new DownloadVDR(VDRActivity.this);
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





	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	



	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
//		try {
//			datasource = new DatabaseConnector(this.getBaseContext());
 //      	    datasource.open();
//
//		} catch (SQLException e) {
//			throw new Error("Error copying database");
//		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		if (datasource != null) {
//			datasource.close();
//		}
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_type), ViewType);
		prefEditor.putInt(getString(R.string.pref_view_event), ViewEvent);
		prefEditor.putInt(getString(R.string.pref_view_state), ViewState);
		prefEditor.commit();

	}

	public int getViewType() {
		return ViewType;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (datasource != null) {
			datasource.close();
		}
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
//		prefEditor.putInt(getString(R.string.pref_view_state), R.id.menu_event_stop);
		prefEditor.commit();

	}
	

	public void setViewType(int viewType) {
		ViewType = viewType;
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_type), ViewType);
		prefEditor.commit();
	}

	public int getViewEvent() {
		Log.d("debug", "getViewEvent:" + ViewEvent );
		return ViewEvent;
	}

	public void setViewEvent(int viewEvent) {
		ViewEvent = viewEvent;
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_event), ViewEvent);
		prefEditor.commit();
	}

	public int getViewState() {
		return ViewState;
	}

	public void setViewState(int viewState) {
		ViewState = viewState;
		Log.d("setViewState", "setViewState to:" +Integer.toString(viewState) );
		SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file), 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(getString(R.string.pref_view_state), ViewState);
		prefEditor.commit();
	}
	

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	    //MenuInflater inflater = getSupportMenuInflater();
	    setMenu(menu); 
		
		getMenuInflater().inflate(R.menu.activity_main_vdr, menu);
//		return super.onCreateOptionsMenu(menu);

		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean fragmentInLauout = false;
		Log.d("MainVDRActivity", "onPrepareOptionsMenu");
	    DetailEventVDRFragment fragment = (DetailEventVDRFragment) getFragmentManager()
		        .findFragmentById(R.id.detailFragment);
		    if (fragment != null && fragment.isInLayout()) {
		    	fragmentInLauout = true;
			    fragment.updateEventInfo(getViewEvent(), datasource);
	            Log.d("onPrepareOptionsMenu", "Update fragment event info" );

		    	
		    }
			setVolumeLongUp(R.id.menu_event_volup );
			setVolumeLongDown(R.id.menu_event_voldown );

		    //menu.clear();
            menu.add(R.id.menu_settings);
            menu.add(R.id.menu_update);
            hideOption(R.id.menu_event_play);		            
            hideOption(R.id.menu_event_rec);		            
            hideOption(R.id.menu_event_stream);		            
            hideOption(R.id.menu_event_pause);		            
            hideOption(R.id.menu_event_live);		            
            hideOption(R.id.menu_event_fwd);		            
            hideOption(R.id.menu_event_rev);		            
            hideOption(R.id.menu_event_stop);		            
            hideOption(R.id.menu_event_start);		            
            hideOption(R.id.menu_event_end);	
            Log.d("onPrepareOptionsMenu", "Type:" + getViewType() + " State:" + getViewState() );
		    switch ( getViewType()) {
		    case 0: //now
//		    	if (fragmentInLauout) {
		            showOption(R.id.menu_event_live);		            
		            showOption(R.id.menu_event_rec);		            
		            showOption(R.id.menu_event_stream);		            
//		    	}		    		
		    	break;
		    case 1: //next
//		    	if (fragmentInLauout) {
		    		showOption(R.id.menu_event_rec);
		            // add set timer or switch to service
//		    	}		    		
		    	break;
		    case 4: // recordings
			    switch ( getViewState()) {
			    case R.id.menu_event_stop: // default
			    case R.id.menu_event_live: // default
			    	showOption(R.id.menu_event_play);
			    	setVolumeUp(R.id.menu_event_play);
			    	setVolumeDown(R.id.menu_event_play);
		            break;
			    case R.id.menu_event_start: // pause mode
			    case R.id.menu_event_end: // pause mode
			    case R.id.menu_event_play: // play mode
			    	showOption(R.id.menu_event_start);		            
			    	showOption(R.id.menu_event_end);		            
			    	showOption(R.id.menu_event_pause);		            
			    	showOption(R.id.menu_event_fwd);		            
			    	showOption(R.id.menu_event_stop);		            
			    	setVolumeUp(R.id.menu_event_start);
			    	setVolumeDown(R.id.menu_event_end);
		            break;
			    case R.id.menu_event_pause: // pause mode
			    	showOption(R.id.menu_event_start);		            
			    	showOption(R.id.menu_event_rev);		            
			    	showOption(R.id.menu_event_play);		            
			    	showOption(R.id.menu_event_fwd);		            
			    	showOption(R.id.menu_event_stop);		            
			    	setVolumeUp(R.id.menu_event_start);
			    	setVolumeDown(R.id.menu_event_rev);
		            break;
			    case R.id.menu_event_rev: // pause mode
			    case R.id.menu_event_fwd: // pause mode
			    	showOption(R.id.menu_event_start);		            
			    	showOption(R.id.menu_event_rev);		            
			    	showOption(R.id.menu_event_play);		            
			    	showOption(R.id.menu_event_fwd);		            
			    	showOption(R.id.menu_event_stop);		            
			    	setVolumeUp(R.id.menu_event_start);
			    	setVolumeDown(R.id.menu_event_rev);
		            break;
			    }
		    	
		    }
		    //return true;
		    return super.onPrepareOptionsMenu(menu);
	}

	private void hideOption(int id)
	{
	    MenuItem item = menu.findItem(id);
	    item.setVisible(false);
	}

	private void showOption(int id)
	{
	    MenuItem item = menu.findItem(id);
	    item.setVisible(true);
	}

	private void setOptionTitle(int id, String title)
	{
	    MenuItem item = menu.findItem(id);
	    item.setTitle(title);
	}

	private void setOptionIcon(int id, int iconRes)
	{
	    MenuItem item = menu.findItem(id);
	    item.setIcon(iconRes);
	}

	public MenuItem getVolumeUp() {
		return VolumeUp;
	}
	public void setVolumeUp(int id ) {
		VolumeUp = getMenu().findItem(id);
	}
	public MenuItem getVolumeDown() {
		return VolumeDown;
	}
	public void setVolumeDown(int id) {
		VolumeDown = getMenu().findItem(id);
	}

	public MenuItem getVolumeLongUp() {
		return VolumeLongUp;
	}

	public void setVolumeLongUp(int id) {
		VolumeLongUp = getMenu().findItem(id);
	}

	public MenuItem getVolumeLongDown() {
		return VolumeLongDown;
	}

	public void setVolumeLongDown(int id) {
		VolumeLongDown = getMenu().findItem(id);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
	private String TuneToCurrentEvent(int ViewState) {
		Cursor c = datasource.getCursorDetails(getViewEvent() + 1);
		if (c == null) {
			return null;
		}
		c.moveToFirst();
		// TextView textViewTitle = (TextView)
		// DetailView.findViewById(R.id.title);
		String title = c.getString(c.getColumnIndex(DatabaseOpenHelper.C_CHANNELS_KEY));
		// get the channel number
		c = datasource.getOneChannel(c.getLong(c.getColumnIndex(DatabaseOpenHelper.C_CHANNELS_KEY)));
		if (c == null) {
			return null;
		}
		c.moveToFirst();
		title = c.getString(c.getColumnIndex(DatabaseOpenHelper.CHANNELS_NUM));
		title = svdrSend ("CHAN", title);
		if (title != null ) {
			Log.d("TuneToCurrentEvent", title);
			setViewState(ViewState);
		    invalidateOptionsMenu();
		}
		return title;
	}

	
	private String PLayCurrentEvent(int ViewState) {
		Cursor c = datasource.getCursorDetails(getViewEvent() + 1);
		if (c == null) {
			return null;
		}
		c.moveToFirst();
		// TextView textViewTitle = (TextView)
		// DetailView.findViewById(R.id.title);
		String title = c
				.getString(c.getColumnIndex(DatabaseOpenHelper.C_TITLE));
		title = svdrSend ("PLAY", title);
		if (title != null ) {
			Log.d("Play", title);
			setViewState(ViewState);
		    invalidateOptionsMenu();
		}
		return title;
	}	

	private String svdrSend(String command, String arg) {
		String ret = null;
		try {
			if (svdrpInterface.getStatus() == AsyncTask.Status.FINISHED) {
				svdrpInterface = new SVDRPInterface(VDRActivity.this);
				ret = svdrpInterface.execute(command, arg).get();
			}
			if (svdrpInterface.getStatus() == AsyncTask.Status.PENDING) {
				ret = svdrpInterface.execute(command, arg).get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.d("PLayCurrentEvent", "InterruptedException");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Log.d("PLayCurrentEvent", "ExecutionException");
			e.printStackTrace();
		}
		return ret;
	}

	private String sendKey(String string) {
		// TODO Auto-generated method stub
		return sendKey ( getViewState(), string );
	}

	private String sendKey( int ViewState, String key) {
		Log.d("presendKey", key);
		try {			
			if (svdrpInterface.getStatus() == AsyncTask.Status.FINISHED) {
				svdrpInterface = new SVDRPInterface(VDRActivity.this);
				key = svdrpInterface.execute("HITK", key).get();
			}
			if (svdrpInterface.getStatus() == AsyncTask.Status.PENDING) {
				key = svdrpInterface.execute("HITK", key).get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.d("sendKey", "InterruptedException");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Log.d("sendKey", "ExecutionException");
			e.printStackTrace();
		}
		if( key != null) {
			Log.d("sendKey", key);
			setViewState(ViewState);
		    invalidateOptionsMenu();
		}
		return key;
	}	


}
