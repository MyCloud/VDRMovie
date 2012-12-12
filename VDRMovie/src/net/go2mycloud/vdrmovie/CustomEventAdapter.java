package net.go2mycloud.vdrmovie;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;



public class CustomEventAdapter extends CursorAdapter {
	private int Type;
	
	public CustomEventAdapter(Context context, Cursor c, int autoRequery, int selected) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		this.Type = selected;
	}

	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
        TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        TextView textViewDetails = (TextView) view.findViewById(R.id.details);
        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.icon);
        Uri uri;
        switch(Type){
        case 3: // Scheduled movies 
            textViewTitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(9))) + " " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(7))) );
            uri= Uri.parse( "mnt/sdcard/VDR_TH_" + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)))  + ".jpg");
			imageViewIcon.setImageURI(uri);
           // imageViewIcon.setImageURI(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
            textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))) + " Regie: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
            break;
        case 4: // Recorded movies 
            textViewTitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(9))) + " " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(7))) );
            uri= Uri.parse( "mnt/sdcard/VDR_TH_" + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)))  + ".jpg");
			imageViewIcon.setImageURI(uri);
           // imageViewIcon.setImageURI(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
            textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))) + " Regie: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
            break;
        default:
        case 0: // recording cursor
            textViewTitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(9))) + " " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(7))) );
            //uri= Uri.parse( "mnt/sdcard/VDR_TH_" + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)))  + ".jpg");
			//imageViewIcon.setImageURI(uri);
            		//"http://www.moviemeter.nl/images/covers/thumbs/62000/62576.jpg");
            textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))) + "                  Regie: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
            break;
        }
    }
		


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
	      LayoutInflater inflater = LayoutInflater.from(parent.getContext());

	      View retView = inflater.inflate(R.layout.event_row_item, parent, false);

	      return retView;
	}

}
