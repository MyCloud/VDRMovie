package net.go2mycloud.vdrmovie;



import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


public class DetailEventVDRActivity extends VDRActivity {

	private DetailEventVDRView detailEventVDRView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}
		ActionBar actionBar = getActionBar();
		setContentView(R.layout.activity_detail_vdr);
		actionBar.setDisplayHomeAsUpEnabled(true);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String s = extras.getString("position");
			
			//View view = (View) this.findViewById(R.id.detailFragment);

		    //view = inflater.inflate(R.layout.fragment_vdrevent_detail,
			//        container, false);

			detailEventVDRView = new DetailEventVDRView( this.findViewById(R.id.detailFragment) );
			detailEventVDRView.setDetails(Integer.parseInt(s), datasource);
			
			//TextView view = (TextView) findViewById(R.id.text_title_year_detail);
			//view.setText(s);
			//this.getApplicationContext().
			//this.findViewById(android.R.id.content);

		}
	}
	
}
