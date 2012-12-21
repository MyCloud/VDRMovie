package net.go2mycloud.vdrmovie;



import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class MainVDRFragment extends Fragment {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	View view = inflater.inflate(R.layout.fragment_vdrlist_overview,
			        container, false);
			 		return view;
	}


	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return super.getActivity().getActionBar().getThemedContext();
		} else {
			return super.getActivity();
		}
	}

/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Toast.makeText(super.getActivity().getBaseContext(), "Menu settings", Toast.LENGTH_SHORT).show();
			// The animation has ended, transition to the Main Menu screen
			//startActivity(new Intent(QuizMenuActivity.this, QuizHelpActivity.class));
			//QuizMenuActivity.this.finish();
			break;
		case R.id.menu_update:
			Toast.makeText(super.getActivity().getBaseContext(), "Menu update", Toast.LENGTH_SHORT).show();
			pleaseWaitDialog = ProgressDialog.show(super.getActivity().getBaseContext(),
					"VDR Guid", "Downloading VDR Guid data", true, true);
			pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					Log.d("onOptionsItemSelected" , "onCancel ");
					downloader.cancel(true);
				}
			});
			if(downloader.getStatus() == AsyncTask.Status.FINISHED ) {
				downloader = new DownloadVDR(super.getActivity().getBaseContext());
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
*/
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, super.getActivity().getActionBar()
				.getSelectedNavigationIndex());
	}


	
	
}
