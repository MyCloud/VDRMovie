package net.go2mycloud.vdrmovie;


import android.R.color;
import android.content.Context;
import android.database.Cursor;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);
	     View view = super.getView(position, convertView, parent);

	     Log.d("CustomEventAdapter", "getView Selected:" + Selected + " position :" + position + " type" + Type);
	     
	     //if ( position == Selected) {
	    //	 TextView textViewTitle = (TextView) view.findViewById(R.id.title);
	    //	 textViewTitle.setBackgroundColor(color.background_dark);
	    //	 textViewTitle.refreshDrawableState();
	     //      
	     //}
	     return view;

	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
        boolean focus = false;
		TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        TextView textViewDetails = (TextView) view.findViewById(R.id.details);
        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.icon);
        Uri uri;
        //if ( BackGroud == 0) {
        //	textViewDetails.getBackground();
        //}
        if( cursor.getInt(0) == (Selected +1 ) ) {
        	focus = true;
        }	
        //imageViewIcon.setBackgroundColor(color);
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
            textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))) + " Regie: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
            break;
        }
        if ( focus ) {
//        	textViewTitle.setBackgroundColor(color.background_light);
 //       	imageViewIcon.setImageState(new int[] { android.R.attr.state_checked }, true);
        	Log.d("CustomEventAdapter", "focus position:" + Selected + " cursor :" + cursor.getPosition() + " type" + Type);
        } else {
//        	textViewTitle.setBackgroundColor(color.background_dark);
//        	imageViewIcon.setImageState(new int[] {}, true);     	
        	Log.d("CustomEventAdapter", "no focus position:" + Selected + " cursor :" + cursor.getPosition()  + " type" + Type);
        }
//        textViewTitle.refreshDrawableState();
        	
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
