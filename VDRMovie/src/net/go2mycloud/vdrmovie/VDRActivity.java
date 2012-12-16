package net.go2mycloud.vdrmovie;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;

public class VDRActivity extends Activity {
	protected DatabaseConnector datasource;

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
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (datasource != null) {
			datasource.close();
		}
	}



	

}
