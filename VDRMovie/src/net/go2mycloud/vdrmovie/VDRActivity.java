package net.go2mycloud.vdrmovie;


import android.app.Activity;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class VDRActivity extends Activity {
//	public static final String PREFS_NAME = "MyPrefsFile";

	protected DatabaseConnector datasource;
	private int ViewType;
	private int ViewEvent;
	private int ViewState; //0 = default 1 = play recording
	private Menu menu;
	private MenuItem VolumeUp;
	private MenuItem VolumeDown;

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
		ViewState = settings.getInt( getString(R.string.pref_view_state), 0 );
//		ViewState = settings.getInt( getString(R.string.pref_view_state), R.id.menu_event_stop );

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
		Log.d("setViewState", Integer.toString(viewState) );
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

	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean fragmentInLauout = false;
		Log.d("MainVDRActivity", "onPrepareOptionsMenu");
	    DetailEventVDRFragment fragment = (DetailEventVDRFragment) getFragmentManager()
		        .findFragmentById(R.id.detailFragment);
		    if (fragment != null && fragment.isInLayout()) {
		    	fragmentInLauout = true;
			    fragment.updateEventInfo(getViewEvent(), datasource);

		    	
		    }
		    //menu.clear();
            menu.add(R.id.menu_settings);
            menu.add(R.id.menu_update);
            hideOption(R.id.menu_event_play);		            
            hideOption(R.id.menu_event_rec);		            
            hideOption(R.id.menu_event_stream);		            
            hideOption(R.id.menu_event_pause);		            
            hideOption(R.id.menu_event_fwd);		            
            hideOption(R.id.menu_event_rev);		            
            hideOption(R.id.menu_event_stop);		            
            hideOption(R.id.menu_event_start);		            
            hideOption(R.id.menu_event_end);	
            Log.d("onPrepareOptionsMenu", "Type:" + getViewType() + " State:" + getViewState() );
		    switch ( getViewType()) {
		    case 0: //now
		    	if (fragmentInLauout) {
		            showOption(R.id.menu_event_play);		            
		            showOption(R.id.menu_event_rec);		            
		            showOption(R.id.menu_event_stream);		            
		    	}		    		
		    	break;
		    case 1: //next
		    	if (fragmentInLauout) {
		    		showOption(R.id.menu_event_rec);
		            // add set timer or switch to service
		    	}		    		
		    	break;
		    case 4: // recordings
			    switch ( getViewState()) {
			    case R.id.menu_event_stop: // default
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
		    return true;
		    //return super.onPrepareOptionsMenu(menu);
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


}
