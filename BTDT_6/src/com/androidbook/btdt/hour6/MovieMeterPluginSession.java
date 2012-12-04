package com.androidbook.btdt.hour6;



import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

import java.util.HashMap;


import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;


import android.os.Environment;
import android.util.Log;


public class MovieMeterPluginSession {

    public static String SESSION_FILENAME = "moviemeter.session";
	private XMLRPCClient client;
	private URI uri;
    
    public String MOVIEMETER_API_KEY = "zqe4bx1rmhxy5gq1z1cwgfn0k2v5y0tm";
//    private static String MOVIEMETER_API_KEY = PropertiesUtil.getProperty("API_KEY_MovieMeter");
    
    //protected static Logger logger = Logger.getLogger("moviejukebox");
    private String key;
    private Integer timestamp;
    private Integer counter;
    //private XmlRpcClientConfigImpl config;
    

    /**
    * Creates the XmlRpcClient
    */
    private void init() {

		
    	uri = URI.create("http://www.moviemeter.nl/ws");
		client = new XMLRPCClient(uri);
    }

    /**
     * Creates a new session to www.moviemeter.nl or if a session exists on disk, it is checked and resumed if valid. 
     * @throws XmlRpcException
     */
    public MovieMeterPluginSession() {
        init();

        Log.d("MovieMeterPluginSession:", "Getting stored session");
        // Read previous session
        FileReader fread;        
        try
        {
        	fread = new FileReader(Environment.getExternalStorageDirectory() + "/" + SESSION_FILENAME);

            String line = new BufferedReader(fread).readLine();

            String[] savedSession = line.split(",");
            if (savedSession.length == 3) {
                setKey(savedSession[0]);
                setTimestamp(Integer.parseInt(savedSession[1]));
                setCounter(Integer.parseInt(savedSession[2]));
            }
            fread.close();        
        } catch (IOException error) {
        }

        Log.d("MovieMeterPluginSession:" ," Stored session: " + getKey());

        if (!isValid()) {
            createNewSession(MOVIEMETER_API_KEY);
        }
    }

    /**
     * Creates a new session to www.moviemeter.nl
     * @param API_KEY
     * @throws XmlRpcException
     */
    @SuppressWarnings("rawtypes")
    private void createNewSession(String API_KEY) {
        HashMap session = null;

        //String ssn = "unknown";
        Object params = API_KEY ;

        //testObjec(params);        
        try {
            session = (HashMap) client.call("api.startSession", params);
        } catch (XMLRPCException error) {
            Log.d("MovieMeterPluginSession:"," Unable to contact website");
        }
        
        if (session != null) {
            if (session.size() > 0) {
                Log.d("MovieMeterPluginSession:"," Created new session with moviemeter.nl");
                setKey((String) session.get("session_key"));
                setTimestamp((Integer) session.get("valid_till"));
                setCounter(0);
                // use of this API is free for non-commercial use
                // see http://wiki.moviemeter.nl/index.php/API for more info
                saveSessionToFile();
            }
        }
    }
    /*
    private void testObjec( Object object)
    {
	if (object instanceof Integer || object instanceof Short || object instanceof Byte) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof Long) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof Double || object instanceof Float) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof Boolean) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof String) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof Date || object instanceof Calendar) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof byte[] ){
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof List) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof Object[]) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} else
	if (object instanceof Map) {
        Log.d("MovieMeterPluginSession:"," Integer");
	} 
}*/
    
    
    /**
     * Searches www.moviemeter.nl for the movieName 
     * @param movieName
     * @return the first summary result as a HashMap
     */
    @SuppressWarnings("rawtypes")
    public HashMap getMovieByTitle(String movieName) {

        HashMap result = null;
        Object[] films = null;
        //Object[] params = new Object[]{getKey(), movieName};
        Object params1 = getKey();
        Object params2 = movieName;
        try {
            if (!isValid()) {
                createNewSession(MOVIEMETER_API_KEY);
            }
          films = (Object[]) client.call("film.search", params1, params2);
            increaseCounter();
            if (films != null && films.length>0) {
                Log.d("MovieMeterPluginSession:", " MovieMeterPlugin: Search for " + movieName + " returned " + films.length + " results");
                for (int i=0; i<films.length; i++){
                    Log.d("Film ",  i + ": " + films[i]);
                }
                // Choose first result
                result = (HashMap) films[0];
            }
        } catch (XMLRPCException error) {
            final Writer eResult = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(eResult);
            error.printStackTrace(printWriter);
            Log.d("MovieMeterPluginSession: ",   eResult.toString());
        }

        return result;
    }
    
    @SuppressWarnings("rawtypes")
	public HashMap getMovieByTitleRegieGenre(String movieName, String regie_f,
			String regie_l, String genre) {

		HashMap result = null;
		Object[] films = null;
		Object params1 = getKey();
		Object params2 = movieName;
		int weight_best = 0;
		int index_best = 0;
		try {
			if (!isValid()) {
				createNewSession(MOVIEMETER_API_KEY);
			}
			films = (Object[]) client.call("film.search", params1, params2);
			increaseCounter();
			if (films != null && films.length > 0) {
				Log.d("MovieMeterPluginSession:", " Searching for " + movieName
						+ " returned " + films.length + " results");
				for (int i = (films.length - 1); i >= 0; i--) {
					int weight = 0;
					HashMap film = (HashMap) films[i];
					if ( !regie_l.isEmpty() & film.get("directors_text").toString().contains(regie_l))
						weight += 4;
					if ( !regie_f.isEmpty() & film.get("directors_text").toString().contains(regie_f))
						weight += 2;
					if ( !genre.isEmpty() & film.get("genres_text").toString().contains(genre))
						weight += 1;
					if (weight >= weight_best) {
						weight_best = weight;
						index_best = i;
					}
				}

				// Choose first result
				result = (HashMap) films[index_best];
				Log.d("MovieMeterPluginSession:", "Title: " + result.get("title").toString() +
						" Sim: " + result.get("similarity").toString() + " Request:" + params2.toString() + " TI:" + Integer.toString(films.length) + 
						" CUR:" + Integer.toString(index_best) + " W:"  + Integer.toString(weight_best) );

			}
		} catch (XMLRPCException error) {
			final Writer eResult = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(eResult);
			error.printStackTrace(printWriter);
			Log.d("MovieMeterPluginSession: ", eResult.toString());
		}

		return result;
	}

    /**
     * Searches www.moviemeter.nl for the movieName and matches the year. If there is no match on year, the first result is returned
     * @param movieName
     * @param year The year of the movie. If no year is known, specify null
     * @return the summary result as a HashMap
     */
    @SuppressWarnings("rawtypes")
    public HashMap getMovieByTitleAndYear(String movieName, String year) {

        HashMap result = null;
        Object[] films = null;
        Object[] params = new Object[]{getKey(), movieName};
        try {
            if (!isValid()) {
                createNewSession(MOVIEMETER_API_KEY);
            }
            films = (Object[]) client.call("film.search", params);
            increaseCounter();
            if (films != null && films.length>0) {
                Log.d("MovieMeterPluginSession:"," Searching for " + movieName + " returned " + films.length + " results");

//                if (StringTools.isValidString(year)) {
               /* if (false) {
                    for (int i=0; i<films.length; i++){
                        HashMap film = (HashMap) films[i];
                        if (film.get("year").toString().equals(year)) {
                            // Probably best match
                            return film;
                        }
                    } 
                } */
                // Choose first result
                result = (HashMap) films[0];
            }
        } catch (XMLRPCException error) {
            final Writer eResult = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(eResult);
            error.printStackTrace(printWriter);
            Log.d("MovieMeterPluginSession: " ,  eResult.toString());
        }

        return result;
    }

    /**
     * Searches www.moviemeter.nl for the movieName and matches the year. If there is no match on year, the first result is returned
     * 
     * @param movieName
     * @param year
     * @return the detailed result as a HashMap
     */
    @SuppressWarnings("rawtypes")
    public HashMap getMovieDetailsByTitleAndYear(String movieName, String year) {

        HashMap result = null;
        HashMap filmInfo = getMovieByTitleAndYear(movieName, year);

        if (filmInfo != null) {
            result = getMovieDetailsById(Integer.parseInt((String)filmInfo.get("filmId")));
        }

        return result;
    }

    /**
     * Given the moviemeterId this returns the detailed result of www.moviemeter.nl  
     * @param moviemeterId
     * @return the detailed result as a HashMap
     */
    @SuppressWarnings("rawtypes")
    public HashMap getMovieDetailsById(Integer moviemeterId) {

        HashMap result = null;
        Object[] params = new Object[]{getKey(), moviemeterId};
        try {
            if (!isValid()) {
                createNewSession(MOVIEMETER_API_KEY);
            }
            result = (HashMap) client.call("film.retrieveDetails", params);
            increaseCounter();
        } catch (XMLRPCException error) {
            final Writer eResult = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(eResult);
            error.printStackTrace(printWriter);
            Log.d("MovieMeterPluginSession: " , eResult.toString());
        }

        return result;
    }

    /**
     * Checks if the current session is valid
     * @return true of false
     */
    public boolean isValid() {
        if (getKey() == null || getKey().equals("")) {
            return false;
        }

        if ((System.currentTimeMillis() / 1000) < getTimestamp()) {
            // Timestamp still valid
            if (counter < 48) {
                return true;
            } else {
                return false;
            }
        }

        try {
        	init();
        	//XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            //config.setServerURL(new URL("http://www.moviemeter.nl/ws"));
            //XmlRpcClient client = new XmlRpcClient();
            //client.setConfig(config);
            Object params1 = getKey();
            Object params2 = "";

            //Object[] params = new Object[]{getKey(), new String("")};
            client.call("film.search", params1, params2);
            increaseCounter();

            return true;
        } catch (XMLRPCException error) {
            Log.d("MovieMeterPluginSession: " , error.getMessage());
            return false;
        } 
    }

    private void increaseCounter() {
        counter++;
        saveSessionToFile();
    }

    /**
     * Saves the session details to disk
     */
    private void saveSessionToFile() {
        FileOutputStream fout;
        try {
        	//fout = new File(Environment.getExternalStorageDirectory(), SESSION_FILENAME);
            fout = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + SESSION_FILENAME);
            new PrintStream(fout).println (getKey() + "," + getTimestamp() + "," + getCounter());
            fout.close();
        } catch (FileNotFoundException ignore) {
            Log.d("MovieMeterPluginSession: " , ignore.getMessage());
        } catch (IOException error) {
            Log.d("MovieMeterPluginSession: ", error.getMessage());
        }        
    }

    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Integer getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCounter() {
        return counter;
    }

    private void setCounter(Integer counter) {
        this.counter = counter;
    }
}