package com.androidbook.btdt.hour6;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomEventAdapter extends CursorAdapter {

	public CustomEventAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {


        TextView textViewTitle = (TextView) view.findViewById(R.id.tv_person_name);

        textViewTitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        //ImageView imageViewIcon = (ImageView) view.findViewById(R.id.icon);
       // imageViewIcon.setImageURI(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

 

        TextView textViewDetails = (TextView) view.findViewById(R.id.tv_person_pin);
        textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
        
    }
		

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());

      View retView = inflater.inflate(R.layout.single_row_item, parent, false);

      return retView;
   }
}
