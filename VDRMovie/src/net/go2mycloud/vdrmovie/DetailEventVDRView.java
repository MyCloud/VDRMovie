package net.go2mycloud.vdrmovie;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailEventVDRView {
	private View DetailView = null;

	public DetailEventVDRView ( View view ) {
		DetailView = view;
	}
	@SuppressWarnings("rawtypes")
	public void setDetails(int position, DatabaseConnector datasource) {
		//Cursor c = EventCA.getCursor();
		//c.moveToPosition(position);  // fragment_vdrevent_detail.xml
		Log.d("DetailEventVDRView", "Position: " + position);
		Cursor c = datasource.getCursorDetails(position+1);
        if( c == null  ) {
        	return;
        }
    	c.moveToFirst();
		//TextView textViewTitle = (TextView) DetailView.findViewById(R.id.title);
		TextView T = (TextView) DetailView.findViewById(R.id.text_title_year_detail);
		
        T.setText(c.getString(c.getColumnIndex(DatabaseOpenHelper.C_TITLE)) + " " + c.getString(c.getColumnIndex(DatabaseOpenHelper.C_STITLE))) ;
        if ( c.getInt(c.getColumnIndex(DatabaseOpenHelper.C_MOVIEM)) == 0 )
        {
        	// use the VDR info
        	T = (TextView) DetailView.findViewById(R.id.text_directors_detail);
            T.setText("Regie: " + c.getString(c.getColumnIndex(DatabaseOpenHelper.C_REGIE))) ;
        	T = (TextView) DetailView.findViewById(R.id.text_genres_detail);
            T.setText("Genre: " + c.getString(c.getColumnIndex(DatabaseOpenHelper.C_GENRE))) ;
        	T = (TextView) DetailView.findViewById(R.id.text_actors_detail);            
            T.setText("Actors: ");
        	T = (TextView) DetailView.findViewById(R.id.text_countries_detail);
            T.setText("Countries: ");
    		c = datasource.getCursorDataDetails(c.getLong(c.getColumnIndex(DatabaseOpenHelper.C_DATA_KEY)));
            if( c == null  ) {
            	return;
            }
        	c.moveToFirst();
        	T = (TextView) DetailView.findViewById(R.id.text_plot_detail);
        	T.setMovementMethod(new ScrollingMovementMethod());


            T.setText("Plot: " + c.getString(c.getColumnIndex(DatabaseOpenHelper.DATA_DETAILS))) ;
        	
        }
        else {
        	// use moviemeter info
        	Map<String, String> map = new HashMap<String, String>();

        	Uri uri= Uri.parse( "mnt/sdcard/VDR_TH_" + c.getString(c.getColumnIndex(DatabaseOpenHelper.C_MOVIEM))  + ".jpg");
            ImageView imageViewIcon = (ImageView) DetailView.findViewById(R.id.image_thump_detail);
			imageViewIcon.setImageURI(uri);
    		c = datasource.getCursorDataDetails(c.getLong(c.getColumnIndex(DatabaseOpenHelper.C_DATA_KEY)));
            if( c == null  ) {
            	return;
            }
        	c.moveToFirst();
			String str = c.getString(c.getColumnIndex(DatabaseOpenHelper.DATA_DETAILS));
			Properties props = new Properties();
			try {
				props.load(new StringReader(str.substring(1, str.length() - 1)));
//				props.load(new StringReader(str.substring(1, str.length() - 1).replace(", ", "\n")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}       
			
			Map<String, String> map2 = new HashMap<String, String>();
			for (Map.Entry<Object, Object> e : props.entrySet()) {
			    map2.put((String)e.getKey(), (String)e.getValue());
			}
        	T = (TextView) DetailView.findViewById(R.id.text_directors_detail);
            T.setText(map2.get("directors_text").toString());

            T = (TextView) DetailView.findViewById(R.id.text_actors_detail);
            T.setText(map2.get("actors_text").toString());

        	T = (TextView) DetailView.findViewById(R.id.text_countries_detail);
            T.setText(map2.get("countries_text").toString());
        	T = (TextView) DetailView.findViewById(R.id.text_genres_detail);
            T.setText(map2.get("genres_text").toString());
        	T = (TextView) DetailView.findViewById(R.id.text_plot_detail);
        	T.setMovementMethod(new ScrollingMovementMethod());
            T.setText(map2.get("plot").toString());
        	T = (TextView) DetailView.findViewById(R.id.text_rating);
            T.setText(map2.get("average").toString() + " 1-5 votes count" + map2.get("votes_count").toString() );
        	T = (TextView) DetailView.findViewById(R.id.text_year);
            T.setText(map2.get("year").toString());

        	
           // imageViewIcon.setImageURI(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
           // textViewDetails.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))) + " Regie: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
        	
        }
        	

	}
	
}

//public static final String TBL_CURSOR = "CursorTbl";
//public static final String C_CHANNELS_KEY = "ch_key";
///public static final String C_TIME = "time";
//public static final String C_DURATION = "dr";
//public static final String C_TITLE = "tt";
//public static final String C_STITLE = "st";
//public static final String C_REGIE = "rt";
//public static final String C_GENRE = "gt";
//public static final String C_MOVIEM = "mm";
//public static final String C_DATA_KEY = "data_key";
//public static final String C_E_1 = "e1"; //Watched or TimerCreated

