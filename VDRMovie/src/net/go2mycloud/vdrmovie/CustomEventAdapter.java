package net.go2mycloud.vdrmovie;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;



public class CustomEventAdapter extends CursorAdapter {

	public CustomEventAdapter(Context context, Cursor c, int autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub

        TextView textViewTitle = (TextView) view.findViewById(R.id.title);

        textViewTitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        //ImageView imageViewIcon = (ImageView) view.findViewById(R.id.icon);
       // imageViewIcon.setImageURI(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

 

        TextView textViewDetails = (TextView) view.findViewById(R.id.details);
        textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
        
    }
		


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
	      LayoutInflater inflater = LayoutInflater.from(parent.getContext());

	      View retView = inflater.inflate(R.layout.event_row_item, parent, false);

	      return retView;

		
//		return null;
	}

}
