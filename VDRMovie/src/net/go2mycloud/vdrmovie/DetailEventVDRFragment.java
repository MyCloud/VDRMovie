package net.go2mycloud.vdrmovie;



import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;



public class DetailEventVDRFragment extends Fragment {

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
//		super.onCreateOptionsMenu(menu, inflater);
	      menu.add(0, R.menu.activity_main_vdr, 0, 
	                getString(R.string.menu_event_play)).setIcon(android.R.drawable.ic_menu_edit) ;
	      	Log.d(" DetailEventVDRFragment", "onPrepareOptionsMenu");
//		super.onCreateOptionsMenu(menu, inflater);
	}


	private View view;
//	private CustomEventAdapter viewCA;
	private DetailEventVDRView detailEventVDRView = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.fragment_vdrevent_detail,
		        container, false);
			detailEventVDRView = new DetailEventVDRView( view );	    	
		    return view;
	}

	public void updateEventInfo(int position, DatabaseConnector datasource  )
	{
		if (detailEventVDRView != null ) {
			detailEventVDRView.setDetails(position, datasource);
			Log.d("DetailEventVDRFragment","updateEventInfo position" + position  );
		}
	}
	
}
