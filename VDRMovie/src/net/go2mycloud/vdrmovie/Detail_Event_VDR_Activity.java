package net.go2mycloud.vdrmovie;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Detail_Event_VDR_Activity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		setContentView(R.layout.activity_detail_vdr);
		actionBar.setDisplayHomeAsUpEnabled(true);


	}

}
