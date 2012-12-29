package net.go2mycloud.vdrmovie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

import org.xmlrpc.android.XMLRPCException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SVDRPInterface extends android.os.AsyncTask<String, Integer, String>{
	
	private static final String D_TAG = "MainVDR$SVDRPInterface";
	private ProgressDialog pleaseWaitDialog;
	private String host="192.168.2.13";
	private int port = 6419;
	private DatabaseConnector datasource;
	private MovieMeterPluginSession session = null;

	private Context mContext;
	   
	
	SVDRPInterface(Context context){
        super();
        this.mContext = context;
	}
	
	SVDRPInterface( Context context, String host, int port) {
        super();
        this.mContext = context;
		this.host = host;
		this.port = port;
	}

	public SVDRPInterface(MainVDRActivity mainVDRActivity) {
		mContext = mainVDRActivity;
		// TODO Auto-generated constructor stub
	}
	
	private boolean isNetworkAvailable() {
		// Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private String playRecordingByName(String recName) {
		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		String data = new String();
		String playrec = null;
		int type;
		boolean moreData = true;
		Socket s;
		try {
			s = new Socket(host, port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);

			String sendSting = "LSTR";
			dos.write(sendSting.getBytes());
			dos.write(rl);
			do {
				try {
					data = dis.readLine();
					data.getBytes(0, 2, buffer, 0);
					type = Integer.parseInt(data.substring(0, 3));
					switch (type) {
					case 220:
						break;
					case 221: // VDR service closing transmission
						moreData = false;
						Log.d(D_TAG, "Done " + data);
						break;
					case 250:
						String dataObj[] = data.split("[ -]", 6);
						//Log.d(D_TAG, "D:" + dataObj[0]);
						//Log.d(D_TAG, "D:" + dataObj[1]);
						//Log.d(D_TAG, "D:" + dataObj[2]);
						//Log.d(D_TAG, "D:" + dataObj[3]);
						//Log.d(D_TAG, "D:" + dataObj[4]);
						//Log.d(D_TAG, "D:" + dataObj[5]);
						//do the things
						if (dataObj[5].contains(recName)) {
							//found recording
							playrec = dataObj[1];
						}
						if (data.substring(3, 4).contains(" ") ) {
							// last record
							sendSting = "QUIT";
							dos.write(sendSting.getBytes());
							dos.write(rl);
						}						
						break;
					default:
						Log.d(D_TAG, "Defailt case " + data);
						break;
					}
				} catch (Exception NumberFormatException) {
					Log.d("exception", data.toString() + NumberFormatException.getMessage());
					break;
				}
			} while (moreData);
			s.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (playrec != null ) {
    		return sendCommand("PLAY", playrec);
			
		}
		return null;
	}



	@SuppressWarnings("deprecation")
	private String sendCommand(String Command, String key) {
		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		String data = new String();
		String responce = null;
		Socket s;
		try {
			s = new Socket(host, port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);

			String sendSting = Command + " " + key;
			Log.d(D_TAG, "send  " + sendSting);
			dos.write(sendSting.getBytes());
			dos.write(rl);
			data = dis.readLine();
			data.getBytes(0, 2, buffer, 0);
			int type = Integer.parseInt(data.substring(0, 3));

			if (type == 220) {
				data = dis.readLine();
				type = Integer.parseInt(data.substring(0, 3));
				if (type == 250 ) {
					// last record
					Log.d(D_TAG, "read  " + data);
					responce = data;
					sendSting = "QUIT";
					dos.write(sendSting.getBytes());
					dos.write(rl);					
					data = dis.readLine();
					Log.d(D_TAG, Command + key + data);
				}
			}
			Log.d(D_TAG, "send end  " + data);
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responce;
	}

	private int scanChannels(String channels) {
		int numChannels = 0;
		String channelsObj[] = channels.split(",");

		try {
			datasource = new DatabaseConnector(mContext);
			datasource.open();
			session = new MovieMeterPluginSession();
		} catch (SQLException eSQL) {

			throw new Error("Error open database");
		}

//		} catch (XMLRPCException e) {
//			throw new Error("Error open MovieMeter session");
//		}
		
		
		for (int serie = 0; serie < channelsObj.length; serie++) {
			try {
				if (channelsObj[serie].contains("-")) {
					String serieObj[] = channelsObj[serie].split("-", 2);
					if (serieObj.length != 2) {
						Log.d(D_TAG, "Error in serie " + channels + " section"
								+ channelsObj[serie]);
						break;
					}
					int from = Integer.parseInt(serieObj[0]);
					int to = Integer.parseInt(serieObj[1]);
					Log.d(D_TAG, "scan Channels " + from + " to " + to);
					for (int channel = from; channel <= to; channel++) {
						numChannels++;
						scanChannel(channel);
					}
				} else {
					numChannels++;
					scanChannel(Integer.parseInt(channelsObj[0]));
				}
			} catch (NumberFormatException eNum) {
				Log.d(D_TAG, "Error in serie " + channels + " section"
						+ channelsObj[serie] + eNum.getMessage());
			}
		}
		datasource.close();
		return numChannels;
	}
	
	
	@SuppressWarnings("rawtypes")
	private int scanChannel( int channelNr) {
		int numEvewnts=0;
		long Ev_ch_key = 0;
		int Ev_nr = 0;
		long Ev_time = 0;
		int Ev_dr = 0;
		String Ev_tt = "";
		String Ev_gt = "";
		String Ev_rft = "";
		String Ev_rlt = "";
		String Ev_st = "";
		String Ev_dt = "";
		long Ev_hsh_key = 0;
		Socket s=null;

		int type;
		Boolean endOfSession = false;
		String data = new String();
		CRC32 checkSum = new CRC32();
		String sendString = new String();

		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		long cur_Id = -1;
		try {
			s = new Socket(host, port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			s.setSoTimeout(2000);
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);


			sendString = "LSTE " + channelNr; // + " NOW"; // currently
														// only
														// the
														// now
														// event
														// data
			dos.write(sendString.getBytes());
			dos.write(rl);
			// clear data
			cur_Id = -1;
			Ev_time = -1;
			Ev_dr = -1;
			endOfSession = false;
			Log.d(D_TAG, "TI: channel " + Integer.toString(channelNr));

			do {
					data = dis.readLine();
					data.getBytes(0, 2, buffer, 0);
					type = Integer.parseInt(data.substring(0, 3));
					switch (type) {
					case 215: // EPG data record
						String dataObj[] = data.split(" ", 3);

						if (dataObj[0].contentEquals("215-C")) {
							// new channel record store in database
							cur_Id = datasource.insertChannel(channelNr,
									dataObj[2], dataObj[1]);
							break;
						} else if (dataObj[0].contentEquals("215-E")
								& cur_Id >= 0) {
							// Event info
							Ev_ch_key = cur_Id;
							Ev_nr = Integer.parseInt(dataObj[1]);
							String eventObj[] = dataObj[2].split(" ", 4);
							Ev_time = Long.parseLong(eventObj[0]);
							Ev_dr = Integer.parseInt(eventObj[1]);
							Ev_tt = "";
							Ev_st = "";
							Ev_gt = "";
							Ev_rft = "";
							Ev_rlt = "";
							Ev_dt = "";
							Ev_hsh_key = 0;

						} else if (dataObj[0].contentEquals("215-T")
								& Ev_time > 0 & Ev_dr > 0) {
							// Title info
							if (dataObj.length < 3) {
								Ev_tt = dataObj[1];

							} else {
								Ev_tt = dataObj[1] + " " + dataObj[2]; // cat
								// together
							}
						} else if (dataObj[0].contentEquals("215-S")
								& Ev_time > 0 & Ev_dr > 0) {
							// Sub Title info
							Ev_st = dataObj[1];
						} else if (dataObj[0].contentEquals("215-D")
								& !Ev_tt.isEmpty()) {
							// Title info
							// genre is mostely the first word
							// regie is mostely the first 2 words behind Regie:

							int regie = 0;
							if (dataObj.length < 3) {
								Ev_dt = dataObj[1];

							} else {
								Ev_dt = dataObj[1] + " " + dataObj[2]; // cat
								// together
							}
							Ev_gt = dataObj[1]; // genre
							dataObj[1] = dataObj[1].replaceAll("Film|film|\\.",
									"");

							if (dataObj[1].length() > 2) {
								Ev_gt = Character.toUpperCase(dataObj[1].charAt(0))
										+ dataObj[1].substring(1);

								String eventObj[] = dataObj[dataObj.length - 1]
										.split("[ \\.]");
								for (int i = 0; eventObj.length > i; i++) {
									if (eventObj[i].equals("Regie:")) {
										regie = 2;
										continue;
									}
									if (regie > 1)
										Ev_rft = eventObj[i];
									if (regie > 0)
										Ev_rlt = eventObj[i];
									regie--;
								}
							}
						} else if (dataObj[0].contentEquals("215-e")
								& !Ev_tt.isEmpty() 
								& (!Ev_st.isEmpty() | !Ev_gt.isEmpty())  ) {
							// write event data
							Ev_hsh_key = datasource.findHashKeyEvent(Ev_ch_key,
									Ev_nr);
							if (Ev_hsh_key <= 0) {
								if (Ev_rlt.isEmpty()){
									Ev_gt = "";
								}
								checkSum.reset();
								checkSum.update(Ev_tt.getBytes());
								checkSum.update(Ev_rft.getBytes());
								checkSum.update(Ev_rlt.getBytes());
								checkSum.update(Ev_gt.getBytes());

								Cursor c = datasource.getOneHash(checkSum
										.getValue());
								if (c != null) {
									if (c.getCount() < 1) {
										// hash not found
										HashMap filmInfo = null;
										String idFilm = "0";
										long Data_Id = 0;
										// session.getMovieDetailsByTitleAndYear(Ev_tt
										// , "");

										if (!Ev_rlt.isEmpty()) {
											Log.d(D_TAG, "getMovieByTitleRegieGenre " + Ev_tt + Ev_tt + Ev_rlt + Ev_gt );
											filmInfo = session
													.getMovieByTitleRegieGenre(Ev_tt,
															Ev_rft, Ev_rlt, Ev_gt);
											if (filmInfo != null) {
												Log.d(D_TAG,
															"NEW FILM "
																	+ String.valueOf(checkSum
																			.getValue()));
												idFilm = filmInfo.get("filmId").toString();
												Log.d(D_TAG, "idFilm " + idFilm );
												try {
													filmInfo = session.getMovieDetailsById( Integer.parseInt(idFilm));
													Log.d(D_TAG, "getMovieDetailsById " + filmInfo.get("thumbnail").toString() );
													downloadFromUrl(filmInfo.get("thumbnail").toString(), "VDR_TH_" + idFilm + ".jpg");
													Log.d(D_TAG, "downloadFromUrl " );
												} catch (Exception e ) {
													Log.d(D_TAG, "catch all get details:" +
															data.toString() + e.getMessage());													
												}
											
												//idFilm = filmInfo.get("filmId").toString();
											}

										}

										if (idFilm.contentEquals("0")) {
											// no movie found use the data from VDR
											Data_Id = datasource.insertData(0, Ev_dt);
										} else {
											Data_Id = datasource.insertData(Integer.parseInt(idFilm), MyToString(filmInfo));
										}
										
										Ev_hsh_key = datasource.insertHash(Data_Id,
												checkSum.getValue());
									} else {
										if (c.moveToFirst()) {
											Ev_hsh_key = c
													.getLong(c
															.getColumnIndex(DatabaseOpenHelper.TBL_ID));
										}
									}
									numEvewnts++;
									datasource
											.insertEventNoCheck(Ev_ch_key, Ev_nr,
													Ev_time, Ev_dr, Ev_tt, Ev_st,
													Ev_gt, Ev_rft + " " + Ev_rlt,
													Ev_hsh_key);

								} else {
									//same event 
								}
							} else {
								//Log.d(D_TAG, "Event already in database" + data.toString());								
							}
						}
						if (dataObj[0].contentEquals("215")) {
							// Last event data record quit connection
							endOfSession = true;
							break;
						}
						break;
					case 220: // VDR service ready
						break;
					case 221: // VDR service closing transmission
									// channel
						endOfSession = true;
						break;
					case 554: // Transaction failed
					case 550: // Requested action not taken
					case 250: // Requested VDR action okay, completed
					case 354: // Start sending EPG data
					case 451: // Requested action aborted: local error
									// in processing
					case 502: // Command not implemented
					case 504: // Command parameter not implemented
						Log.d(D_TAG, "need investigation" + data.toString());
						endOfSession = true;
						break;

					case 500: // Syntax error, command unrecognized
					case 501: // Syntax error in parameters or arguments
						dos.write(sendString.getBytes());
						dos.write(rl);
						Log.d(D_TAG,
								"Try again same command " + data.toString());

						break;
					default:
						Log.d(D_TAG, "Default case " + data.toString());
						break;
					}
					//Log.d(DEBUG_TAG, Integer.toString(type));


			} while (!endOfSession);
			sendString = "QUIT";
			dos.write(sendString.getBytes());
			dos.write(rl);
			data = dis.readLine();
			s.close();
		} catch (Exception e) {
			Log.d(D_TAG, "catch all scanChannel:" +
					data.toString() + e.getMessage());
			try {
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		Log.d(D_TAG, "Scan done fond new events: " + Integer.toString(numEvewnts));
		return numEvewnts;
	}
	
	private int scanRecordings(String channels) {
		int numChannels = 0;
		String channelsObj[] = channels.split(",");

		try {
			datasource = new DatabaseConnector(mContext);
			datasource.open();
			session = new MovieMeterPluginSession();
		} catch (SQLException eSQL) {

			throw new Error("Error open database");
		}

//		} catch (XMLRPCException e) {
//			throw new Error("Error open MovieMeter session");
//		}
		datasource.deleteAllRecords();
		// make hash table for all records
		@SuppressWarnings("rawtypes")
		HashMap RecInfo = null;
		ArrayList<Boolean> recInfo = getRecInfo();
		
		for (int serie = 0; serie < channelsObj.length; serie++) {
			try {
				if (channelsObj[serie].contains("-")) {
					String serieObj[] = channelsObj[serie].split("-", 2);
					if (serieObj.length != 2) {
						Log.d(D_TAG, "Error in serie " + channels + " section"
								+ channelsObj[serie]);
						break;
					}
					int from = Integer.parseInt(serieObj[0]);
					int to = Integer.parseInt(serieObj[1]);
					Log.d(D_TAG, "scan Recordings " + from + " to " + to);
					for (int channel = from; channel <= to; channel++) {
						if ( scanRecord(channel) < 0 ) {
							break;
						}
						numChannels++;							
					}
				} else {
					if (scanRecord(Integer.parseInt(channelsObj[0])) > 0) {
						numChannels++;						
					}
				}
			} catch (NumberFormatException eNum) {
				Log.d(D_TAG, "Error in serie " + channels + " section"
						+ channelsObj[serie] + eNum.getMessage());
			}
		}
		
		datasource.close();
		return numChannels;
	}
	
	
	private ArrayList<Boolean> getRecInfo() {
		boolean endRecordings = false;
		//TimerTbl";
//		int db;
		int status=0;
		long ch_key=0;
		long event_key=0;
		int t_nr=0;
		int e_nr=0;
		String date="";
		String start_t="";
		String stop_t="";
		int pri=50;
		int rem=99;
		String dir="VDR_Movie";
		String tt="";
		long start_s=0;
		long stop_s=0;
		Socket s=null;
		int type;
		Boolean endOfSession = false;
		String data = new String();
		String sendString = new String();
		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		ArrayList<Boolean> recInfo = new ArrayList<Boolean>();
		try {
			s = new Socket(host, port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			s.setSoTimeout(2000);
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);


			sendString = "LSTR";
			dos.write(sendString.getBytes());
			dos.write(rl);
			endOfSession = false;
			Log.d(D_TAG, "Recording list: ");
			
			do {
					data = dis.readLine();
					data.getBytes(0, 2, buffer, 0);
					type = Integer.parseInt(data.substring(0, 3));
					// 2 1:1:2012-12-27:2000:2027:50:99:
					switch (type) {
					case 220:
						break;
					case 501:
					case 221:
						endRecordings = true;
						endOfSession = true;						
						break;
					case 250:
						String dataObj[] = data.split("[ -]", 6);
						// 250 1 1
						if ( dataObj.length != 6 ) {
							Log.d(D_TAG, "Bad data1: " + data.toString());							
							break;
						}
						String partObj[] = dataObj[6].split("~", 3);
						dir=partObj[partObj.length-2];

						if ( partObj.length != 3 ) {
							Log.d(D_TAG, "Bad data2: " + dataObj[0].toString());							
							break;
						}
						t_nr = Integer.parseInt(partObj[1].toString());
						status = Integer.parseInt(partObj[2].toString());
						ch_key = datasource.getCannelId(Integer.parseInt(dataObj[1]));
						date= dataObj[2];
						start_t=dataObj[3];
						stop_t=dataObj[4];
						pri=Integer.parseInt(dataObj[5].toString());
						rem=Integer.parseInt(dataObj[6].toString());
						partObj = dataObj[7].split("~");
						if ( partObj.length == 2 ) {
							dir=partObj[partObj.length-2];
						}
						tt=partObj[partObj.length-1];
						TimerXml xmlTimer = new TimerXml();
						xmlTimer.setInput(dataObj[8]);
						start_s=Long.parseLong(xmlTimer.getTag(TimerXml.XML_start));
						stop_s=Long.parseLong(xmlTimer.getTag(TimerXml.XML_stop));
						e_nr=Integer.parseInt(xmlTimer.getTag(TimerXml.XML_eventid));
						event_key = datasource.getEventId(ch_key, e_nr);
						datasource.insertTimerNoCheck(status, ch_key, event_key,
								t_nr,e_nr,date,start_t, stop_t,
								pri,rem, dir, tt, start_s, stop_s) ;
						endOfSession = true;
						break;
					default:
						Log.d(D_TAG, "Default case " + data.toString());
						break;
					}
					//Log.d(DEBUG_TAG, Integer.toString(type));

			} while (!endOfSession);
			sendString = "QUIT";
			dos.write(sendString.getBytes());
			dos.write(rl);
			data = dis.readLine();
			s.close();
		} catch (Exception e) {
			Log.d(D_TAG, "catch all scanTimer:" +
					data.toString() + e.getMessage());
			try {
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (endRecordings) {
			Log.d(D_TAG, "Scan Timer done fond no nr");
			return null;
		}
		Log.d(D_TAG, "Scan Timer done fond nr");
		return null;
}

	@SuppressWarnings("rawtypes")
	private int scanRecord( int recNr) {
		boolean endRecordings = false;
		boolean sameRecording = false;
		int numEvewnts=0;
		long Ev_ch_key = 0;
		int Ev_nr = 0;
		long Ev_time = 0;
		int Ev_dr = 0;
		String Ev_tt = "";
		String Ev_gt = "";
		String Ev_rft = "";
		String Ev_rlt = "";
		String Ev_st = "";
		String Ev_dt = "";
		int Ev_wt = -1;
		long Ev_hsh_key = 0;
		Socket s=null;

		int type;
		Boolean endOfSession = false;
		String data = new String();
		CRC32 checkSum = new CRC32();
		String sendString = new String();

		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		long cur_Id = -1;
		try {
			s = new Socket(host, port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			s.setSoTimeout(2000);
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);


			sendString = "LSTR " + recNr; // + " NOW"; // currently
														// only
														// the
														// now
														// event
														// data
			dos.write(sendString.getBytes());
			dos.write(rl);
			// clear data
			cur_Id = -1;
			Ev_time = -1;
			Ev_dr = -1;
			endOfSession = false;
			Log.d(D_TAG, "Rec: " + Integer.toString(recNr));

			do {
					data = dis.readLine();
					data.getBytes(0, 2, buffer, 0);
					type = Integer.parseInt(data.substring(0, 3));
					switch (type) {
					case 214: // Help message
						break;
					case 215: // EPG data record
						String dataObj[] = data.split(" ", 3);

						if (dataObj[0].contentEquals("215-C")) {
							// search channel id related to this
							// service

							cur_Id = datasource.getCannelId(dataObj[1]);
							break;
						} else if (dataObj[0].contentEquals("215-E")
								& cur_Id >= 0) {
							// Event info
							Ev_ch_key = cur_Id;
							Ev_nr = Integer.parseInt(dataObj[1]);
							String eventObj[] = dataObj[2].split(" ", 4);
							Ev_time = Long.parseLong(eventObj[0]);
							Ev_dr = Integer.parseInt(eventObj[1]);
							Ev_tt = "";
							Ev_st = "";
							Ev_gt = "";
							Ev_rft = "";
							Ev_rlt = "";
							Ev_dt = "";
							Ev_wt = -1;
							Ev_hsh_key = 0;

						} else if (dataObj[0].contentEquals("215-T")
								& Ev_time > 0 & Ev_dr > 0) {
							// Title info
							if (dataObj.length < 3) {
								Ev_tt = dataObj[1];

							} else {
								Ev_tt = dataObj[1] + " " + dataObj[2]; // cat
																					// together
							}

						} else if (dataObj[0].contentEquals("215-S")
								& Ev_time > 0 & Ev_dr > 0) {
							// Sub Title info
							Ev_st = dataObj[1];
						} else if (dataObj[0].contentEquals("215-D")
								& !Ev_tt.isEmpty()) {
							// Title info
							// genre is mostely the first word
							// regie is mostely the first 2 words
							// behind Regie:

							int regie = 0;
							if (dataObj.length < 3) {
								Ev_dt = dataObj[1];

							} else {
								Ev_dt = dataObj[1] + " " + dataObj[2]; // cat
								// together
							}

							Ev_gt = dataObj[1]; // genre
							dataObj[1] = dataObj[1].replaceAll("Film|film|\\.",
									"");

							if (dataObj[1].length() > 2) {
								Ev_gt = Character.toUpperCase(dataObj[1].charAt(0))
										+ dataObj[1].substring(1);

								String eventObj[] = dataObj[dataObj.length - 1]
										.split("[ \\.]");
								for (int i = 0; eventObj.length > i; i++) {
									if (eventObj[i].equals("Regie:")) {
										regie = 2;
										continue;
									}
									if (regie > 1)
										Ev_rft = eventObj[i];
									if (regie > 0)
										Ev_rlt = eventObj[i];
									regie--;
								}
							}
						} else if (dataObj[0].contentEquals("215")
								& !Ev_tt.isEmpty() 
								& (!Ev_st.isEmpty() | !Ev_gt.isEmpty())  ) {
							// write event data in rec file
							Ev_hsh_key = datasource.findHashKeyRec(Ev_ch_key,
									Ev_nr);
							if (Ev_hsh_key <= 0) {
								if (Ev_rlt.isEmpty()){
									Ev_gt = "";
								}

								checkSum.reset();
								checkSum.update(Ev_tt.getBytes());
								checkSum.update(Ev_rft.getBytes());
								checkSum.update(Ev_rlt.getBytes());
								checkSum.update(Ev_gt.getBytes());


								Cursor c = datasource.getOneHash(checkSum
										.getValue());
								if (c != null) {
									if (c.getCount() < 1) {
										// hash not found
										HashMap filmInfo = null;
										String idFilm = "0";
										long Data_Id = 0;
										// session.getMovieDetailsByTitleAndYear(Ev_tt
										// , "");

										if (!Ev_rlt.isEmpty()) {
											filmInfo = session
													.getMovieByTitleRegieGenre(Ev_tt,
															Ev_rft, Ev_rlt, Ev_gt);
											if (filmInfo != null) {
												Log.d(D_TAG,
															"NEW FILM "
																	+ String.valueOf(checkSum
																			.getValue()));
												idFilm = filmInfo.get("filmId").toString();
												try {
													filmInfo = session.getMovieDetailsById( Integer.parseInt(idFilm));
													Log.d(D_TAG, "getMovieDetailsById " + filmInfo.get("thumbnail").toString() );
													downloadFromUrl(filmInfo.get("thumbnail").toString(), "VDR_TH_" + idFilm + ".jpg");
													Log.d(D_TAG, "downloadFromUrl " );
												} catch (Exception e ) {
													Log.d(D_TAG, "catch all get details:" +
															data.toString() + e.getMessage());													
												}
											}

										}
										
										// session.getMovieByTitle(Ev_tt);
										// ////session.getMovieByTitle("Fame");
										// Log.d(DEBUG_TAG, "NEW HASH " +
										// String.valueOf(checkSum.getValue()) );
										if (idFilm.contentEquals("0")) {
											// no movie found use the data from VDR
											Data_Id = datasource.insertData(0, Ev_dt);
										} else {
											Data_Id = datasource.insertData(Integer.parseInt(idFilm), MyToString(filmInfo));
										}
										
										Ev_hsh_key = datasource.insertHash(Data_Id,
												checkSum.getValue());
									} else {
										if (c.moveToFirst()) {
											Ev_hsh_key = c
													.getLong(c
															.getColumnIndex(DatabaseOpenHelper.TBL_ID));
										}
									}

									datasource
											.insertRecNoCheck(Ev_ch_key, Ev_nr,
													Ev_time, Ev_dr, Ev_tt, Ev_st,
													Ev_gt, Ev_rft + " " + Ev_rlt, Ev_wt,
													Ev_hsh_key);

								}
							} else {
								sameRecording = true;
							}
								
						}
						if (dataObj[0].contentEquals("215")) {
							// Last event data record quit
							// connection
							endOfSession = true;
							break;
						}
						break;
					case 220: // VDR service ready
						break;
					case 221: // VDR service closing transmission
									// channel
						endOfSession = true;
						break;
					case 554: // Transaction failed
					case 550: // Requested action not taken
					case 250: // Requested VDR action okay,
									// completed
					case 354: // Start sending EPG data
					case 451: // Requested action aborted: local
									// error
									// in processing
					case 502: // Command not implemented
					case 504: // Command parameter not implemented
						Log.d(D_TAG, data.toString());
						endOfSession = true;
						endRecordings = true;
						break;

					case 500: // Syntax error, command unrecognized
					case 501: // Syntax error in parameters or
									// arguments
						dos.write(sendString.getBytes());
						dos.write(rl);
						Log.d(D_TAG,
								"Try again same command " + data.toString());

						break;
					default:
						Log.d(D_TAG, "Default case " + data.toString());
						break;
					}
					//Log.d(DEBUG_TAG, Integer.toString(type));


			} while (!endOfSession);
			sendString = "QUIT";
			dos.write(sendString.getBytes());
			dos.write(rl);
			data = dis.readLine();
			s.close();
		} catch (Exception e) {
			Log.d(D_TAG, "catch all scanChannel:" +
					data.toString() + e.getMessage());
			try {
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (endRecordings) {
			Log.d(D_TAG, "Scan recording done fond no recording nr" + Integer.toString(recNr));
			return -1;
		}
		Log.d(D_TAG, "Scan recording done fond new recording nr" + Integer.toString(recNr));
		return 1;
	}	
	
	private int scanTimers(String channels) {
		int numChannels = 0;
		boolean endTimers = false;
		String channelsObj[] = channels.split(",");

		try {
			datasource = new DatabaseConnector(mContext);
			datasource.open();
			session = new MovieMeterPluginSession();
		} catch (SQLException eSQL) {

			throw new Error("Error open database");
		}

//		} catch (XMLRPCException e) {
//			throw new Error("Error open MovieMeter session");
//		}
		
		datasource.deleteAllTimers();
		for (int serie = 0; serie < channelsObj.length; serie++) {
			try {
				if (channelsObj[serie].contains("-")) {
					String serieObj[] = channelsObj[serie].split("-", 2);
					if (serieObj.length != 2) {
						Log.d(D_TAG, "Error in serie " + channels + " section"
								+ channelsObj[serie]);
						break;
					}
					int from = Integer.parseInt(serieObj[0]);
					int to = Integer.parseInt(serieObj[1]);
					Log.d(D_TAG, "scan Timers " + from + " to " + to);
					for (int channel = from; channel <= to; channel++) {
						if ( scanTimer(channel) < 0 ) {
							endTimers = true;
							break;
						}
						numChannels++;							
					}
				} else {
					if (scanTimer(Integer.parseInt(channelsObj[0])) > 0) {
						numChannels++;						
					}
					else {
						endTimers = true;						
					}
						
				}
			} catch (NumberFormatException eNum) {
				Log.d(D_TAG, "Error in serie " + channels + " section"
						+ channelsObj[serie] + eNum.getMessage());
			}
		}
		datasource.close();
		return numChannels;
	}
	
	
//	@SuppressWarnings("rawtypes")
	private int scanTimer( int recNr) {
		boolean endRecordings = false;
		//TimerTbl";
//		int db;
		int status=0;
		long ch_key=0;
		long event_key=0;
		int t_nr=0;
		int e_nr=0;
		String date="";
		String start_t="";
		String stop_t="";
		int pri=50;
		int rem=99;
		String dir="VDR_Movie";
		String tt="";
		long start_s=0;
		long stop_s=0;
		Socket s=null;
		int type;
		Boolean endOfSession = false;
		String data = new String();
		String sendString = new String();

		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		try {
			s = new Socket(host, port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			s.setSoTimeout(2000);
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);


			sendString = "LSTT " + recNr; // + " NOW"; // currently
														// only
														// the
														// now
														// event
														// data
			dos.write(sendString.getBytes());
			dos.write(rl);
			// clear data
//			Ev_time = -1;
//			Ev_dr = -1;
			endOfSession = false;
			Log.d(D_TAG, "Timer: " + Integer.toString(recNr));

			do {
					data = dis.readLine();
					data.getBytes(0, 2, buffer, 0);
					type = Integer.parseInt(data.substring(0, 3));
					// 2 1:1:2012-12-27:2000:2027:50:99:
					switch (type) {
					case 220:
						break;
					case 501:
					case 221:
						endRecordings = true;
						endOfSession = true;						
						break;
					case 250:
						String dataObj[] = data.split(":", 9);
						// 250 1 1
						if ( dataObj.length != 9 ) {
							Log.d(D_TAG, "Bad data1: " + data.toString());							
							break;
						}
						String partObj[] = dataObj[0].split(" ", 3);
						if ( partObj.length != 3 ) {
							Log.d(D_TAG, "Bad data2: " + dataObj[0].toString());							
							break;
						}
						t_nr = Integer.parseInt(partObj[1].toString());
						status = Integer.parseInt(partObj[2].toString());
						ch_key = datasource.getCannelId(Integer.parseInt(dataObj[1]));
						date= dataObj[2];
						start_t=dataObj[3];
						stop_t=dataObj[4];
						pri=Integer.parseInt(dataObj[5].toString());
						rem=Integer.parseInt(dataObj[6].toString());
						partObj = dataObj[7].split("~");
						if ( partObj.length == 2 ) {
							dir=partObj[partObj.length-2];
						}
						tt=partObj[partObj.length-1];
						TimerXml xmlTimer = new TimerXml();
						xmlTimer.setInput(dataObj[8]);
						start_s=Long.parseLong(xmlTimer.getTag(TimerXml.XML_start));
						stop_s=Long.parseLong(xmlTimer.getTag(TimerXml.XML_stop));
						e_nr=Integer.parseInt(xmlTimer.getTag(TimerXml.XML_eventid));
						event_key = datasource.getEventId(ch_key, e_nr);
						datasource.insertTimerNoCheck(status, ch_key, event_key,
								t_nr,e_nr,date,start_t, stop_t,
								pri,rem, dir, tt, start_s, stop_s) ;
						endOfSession = true;
						break;
					default:
						Log.d(D_TAG, "Default case " + data.toString());
						break;
					}
					//Log.d(DEBUG_TAG, Integer.toString(type));

			} while (!endOfSession);
			sendString = "QUIT";
			dos.write(sendString.getBytes());
			dos.write(rl);
			data = dis.readLine();
			s.close();
		} catch (Exception e) {
			Log.d(D_TAG, "catch all scanTimer:" +
					data.toString() + e.getMessage());
			try {
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (endRecordings) {
			Log.d(D_TAG, "Scan Timer done fond no nr" + Integer.toString(recNr));
			return -1;
		}
		Log.d(D_TAG, "Scan Timer done fond nr" + Integer.toString(recNr));
		return 1;
	}	
	
	
	
	@Override
	protected String doInBackground(String... svdrpC ) {
	    int count = svdrpC.length;
	    if ( !isNetworkAvailable() )
	    {
			Toast.makeText(mContext, "No network connection ", Toast.LENGTH_SHORT).show();
	    	return null;
	    }
        // This will download stuff from each URL passed in
        for (int i = 0; i < count - 1; i++) {
        	if( svdrpC[i].contains("PLAY") ) {
        		return playRecordingByName(svdrpC[i+1]);
        	}
        	if( svdrpC[i].contains("HITK") ) {
        		return sendCommand(svdrpC[i], svdrpC[i+1]);
//        		return hitKey(svdrpC[1]);
        	}
        	if( svdrpC[i].contains("CHAN") ) {
        		return sendCommand(svdrpC[i], svdrpC[i+1]);
        	}
        	if( svdrpC[i].contains("LSTE") ) {
        		return Integer.toString(scanChannels(svdrpC[i+1]));
        	}
        	if( svdrpC[i].contains("LSTR") ) {
        		return Integer.toString(scanRecordings(svdrpC[i+1]));
        	}
        	if( svdrpC[i].contains("LSTT") ) {
        		return Integer.toString(scanTimers(svdrpC[i+1]));
        	}
        }
        
		// TODO Auto-generated method stub
		return null;
	}





@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
//		pleaseWaitDialog.dismiss();
		
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
//		pleaseWaitDialog = ProgressDialog.show(mContext,
//				"VDR Guid", "Downloading VDR Guid data", true, true);
//		pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {
//			public void onCancel(DialogInterface dialog) {
//				Log.d("onOptionsItemSelected" , "onCancel ");
//				cancel(true);
//			}
//		});
	}

@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

/*
 * 
 * HITK
 * 

Up
Down
Menu
Ok
Back
Left
Right
Red
Green
Yellow
Blue
0
1
2
3
4
5
6
7
8
9
Info
Play
Pause
Stop
Record
FastFwd
FastRew
Next
Prev
Power
Channel+
Channel-
PrevChannel
Volume+
Volume-
Mute
Audio
Subtitles
Schedule
Channels
Timers
Recordings
Setup
Commands
User0
User1
User2
User3
User4
User5
User6
User7
User8
User9

*/
private void downloadFromUrl(String imageURL, String fileName) {
    try {
            // Connect to the URL
            URL myImageURL = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection)myImageURL.openConnection();
            connection.setDoInput(true);
            connection.setReadTimeout(300);
            connection.setConnectTimeout(1500);
            //Log.i("help", " 0");
            connection.connect();
            //connection.getErrorStream();
            //Log.i("help", " 1");
            InputStream input = connection.getInputStream();
//            URLConnection ucon = url.openConnection();

            //Log.i("help", " 2");

            // Get the bitmap
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //Log.i("help", " 3");

            // Save the bitmap to the file
            String path = Environment.getExternalStorageDirectory().toString();

            OutputStream fOut = null;
            File file = new File(path, fileName);
            //Log.i("help", file.getAbsolutePath());
            //Log.i("help", file.toString());
            //Log.i("help", file.length() + "");
            //System.out.println("THIS IS A TEST OF SYSTEM.OUT.PRINTLN()");
            fOut = new FileOutputStream(file);
            //Log.i("help", " 4");

            myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            Log.d(D_TAG, "saved " + fileName );

            //System.out.println("file Path: " + file.getAbsolutePath());
            //Log.i("help2", file.getAbsolutePath());
            //Log.i("help2", file.toString());
            //Log.i("help2", file.length() + "");

//    } catch (Exception e) {
    } catch (Throwable e) {
            Log.d("downloadFromUrl", e.getMessage());
			e.printStackTrace();
        }
    
}
	@SuppressWarnings("rawtypes")
	private String MyToString ( HashMap h ) {
		// Get a set of the entries 
		String hs = "";
		Set set = h.entrySet(); 
		// Get an iterator 
		Iterator i = set.iterator(); 
		// Display elements 
		hs += "{";
		while(i.hasNext()) { 
			Map.Entry me = (Map.Entry)i.next(); 
			hs += me.getKey();
			hs += "=";
			hs += me.getValue();
			hs += "\n";
		} 
		hs += "}";
		return hs;
	}

}
