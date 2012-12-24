package net.go2mycloud.vdrmovie;


import android.R.color;
import android.R.layout;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;



public class CustomEventAdapter extends CursorAdapter {
	private volatile int Type;
	private volatile int Selected;
	private int BackGroud=0;
	
	public CustomEventAdapter(Context context, Cursor c, int autoRequery, int ListType) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		this.Type = ListType;
    	Log.d("CustomEventAdapter", "create:" + Selected  );


	}
    
	
	public int getType() {
		return Type;
	}


	public void setType(int type) {
		Type = type;
	}


	public int getSelected() {
		return Selected;
	}


	public void setSelected(int selected) {

		Selected = selected;
    	Log.d("CustomEventAdapter", "update selected position:" + Selected  );
	}



	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
        boolean focus = false;
		TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        TextView textViewDetails = (TextView) view.findViewById(R.id.details);
        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.icon);
        ImageView imageViewFocus = (ImageView) view.findViewById(R.id.focus);
        Uri uri;
//		View temp = view.findViewById(R.id.row);
        
        //if ( BackGroud == 0) {
//        Log.d("back", "color:" + temp.getBackground() );
        //}
        if( cursor.getInt(0) == (Selected +1 ) ) {
        	//textViewFocus.setText(">");
        	view.setBackgroundColor(Color.WHITE);
//        	imageViewFocus.setImageResource(R.drawable.status_rec);
//        	textViewTitle.setBackgroundColor(4000);
//        	textViewTitle.invalidate();
 //       	textViewTitle.refreshDrawableState();
 //       	imageViewFocus.invalidate();
//        	imageViewFocus.refreshDrawableState();

        } else {
        	view.setBackgroundColor(Color.LTGRAY);
        	imageViewFocus.setImageResource(R.drawable.status_green);
        	
        }
            textViewTitle.setText(cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.C_TITLE)) + " " + cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.C_STITLE))) ;
            uri= Uri.parse( "mnt/sdcard/VDR_TH_" + cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.C_MOVIEM))  + ".jpg");
			imageViewIcon.setImageURI(uri);
            textViewDetails.setText(cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.C_GENRE)) + " Regie: " + cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.C_REGIE)));
        	
    }
		
	

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
	      LayoutInflater inflater = LayoutInflater.from(parent.getContext());

	      View retView = inflater.inflate(R.layout.event_row_item, parent, false);
	      Log.d("CustomEventAdapter", "newView position:" + Selected + " cursor :" + cursor.getPosition()  + " type" + Type);

	      return retView;
	}

}
