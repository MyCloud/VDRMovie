package net.go2mycloud.vdrmovie;



import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class DetailEventVDRFragment extends Fragment {

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
