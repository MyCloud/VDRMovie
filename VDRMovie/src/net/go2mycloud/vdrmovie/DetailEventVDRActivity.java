package net.go2mycloud.vdrmovie;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;


public class DetailEventVDRActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		setContentView(R.layout.activity_detail_vdr);
		actionBar.setDisplayHomeAsUpEnabled(true);


	}

}
