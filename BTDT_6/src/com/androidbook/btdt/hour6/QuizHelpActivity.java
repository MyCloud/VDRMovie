package com.androidbook.btdt.hour6;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class QuizHelpActivity extends QuizActivity {
	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_help);
		// Read raw file into string and populate TextView
		ActionBar actionBar = getActionBar();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		    actionBar.setHomeButtonEnabled(true);
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

        InputStream iFile = getResources().openRawResource(R.raw.data1);
        try {
        	TextView helpText = (TextView) findViewById(R.id.TextView_HelpText);
            String strFile = inputStreamToString(iFile);
            helpText.setText(strFile);
        } catch (Exception e) {
            Log.e("testung", "InputStreamToString failure", e);
        }
		
	}
	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
	    case R.id.menu_settings:
	      Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT).show();
	      showDialog(DATE_DIALOG_ID);
	      break;
	    case R.id.help_settings:
	      Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT)
	          .show();
	      break;
	    case android.R.id.home:
	    	  Intent intent = new Intent(this, QuizSplashActivity.class);
	    	  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	  startActivity(intent);
	    	  break; 
	    default:
	      break;
	    }


		return super.onOptionsItemSelected(item);
	}

    /**
     * Converts an input stream to a string
     * 
     * @param is
     *            The {@code InputStream} object to read from
     * @return A {@code String} object representing the string for of the input
     * @throws IOException
     *             Thrown on read failure from the input
     */
    @SuppressWarnings("deprecation")
	public String inputStreamToString(InputStream is) throws IOException {
        StringBuffer sBuffer = new StringBuffer();
        DataInputStream dataIO = new DataInputStream(is);
        String strLine = null;

        while ((strLine = dataIO.readLine()) != null) {
            sBuffer.append(strLine + "\n");
        }

        dataIO.close();
        is.close();

        return sBuffer.toString();
    }
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
	       switch (id) {
	        case DATE_DIALOG_ID:
	            final TextView dob = (TextView) findViewById(R.id.TextView_DOB_Info);
	            Calendar now = Calendar.getInstance(); 
	            
	            DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
	                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	                    
	                	Time dateOfBirth = new Time();
	                    dateOfBirth.set(dayOfMonth, monthOfYear, year);
	                    long dtDob = dateOfBirth.toMillis(true);
	                    dob.setText(DateFormat.format("MMMM dd, yyyy", dtDob));
	                    
	                    //Editor editor = mGameSettings.edit();
	                    //editor.putLong(GAME_PREFERENCES_DOB, dtDob);
	                    //editor.commit();
	                }
	            },  now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
	            return dateDialog;
	       }
	
		return super.onCreateDialog(id, args);
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
        switch (id) {
        case DATE_DIALOG_ID:
            // Handle any DatePickerDialog initialization here
            DatePickerDialog dateDialog = (DatePickerDialog) dialog;
            int iDay,
            iMonth,
            iYear;
            // Check for date of birth preference
            Calendar cal = Calendar.getInstance();
            // Today's date fields
            iDay = cal.get(Calendar.DAY_OF_MONTH);
            iMonth = cal.get(Calendar.MONTH);
            iYear = cal.get(Calendar.YEAR);
            // Set the date in the DatePicker to the date of birth OR to the
            // current date
            dateDialog.updateDate(iYear, iMonth, iDay);
            return;
        }
	}

}
