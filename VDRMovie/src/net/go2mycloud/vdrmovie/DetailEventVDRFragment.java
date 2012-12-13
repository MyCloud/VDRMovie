package net.go2mycloud.vdrmovie;



import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class DetailEventVDRFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_vdrevent_detail,
		        container, false);
		    return view;
	}

	public void updateEventInfo()
	{
		Log.d("DetailEventVDRFragment","updateEventInfo" );
	}
}
