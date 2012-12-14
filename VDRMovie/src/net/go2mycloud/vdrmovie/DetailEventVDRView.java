package net.go2mycloud.vdrmovie;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

public class DetailEventVDRView {
	private View DetailView = null;
	CustomEventAdapter EventCA;
	public DetailEventVDRView ( View view, CustomEventAdapter customAdapter ) {
		DetailView = view;
		EventCA = customAdapter;
	}
	public void setDetails(int position) {
		Cursor c = EventCA.getCursor();
		c.moveToPosition(position);
		TextView view = (TextView) DetailView.findViewById(R.id.text_title_year_detail);
		view.setText(c.getString(c.getColumnIndex(c.getColumnName(9))) + " " + c.getString(c.getColumnIndex(c.getColumnName(7))) );
		
	}
	
}
