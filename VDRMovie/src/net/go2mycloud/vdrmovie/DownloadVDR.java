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
import java.util.HashMap;
import java.util.zip.CRC32;




import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;



public class DownloadVDR extends android.os.AsyncTask<Object, String, Boolean> {
	private static final String DEBUG_TAG = "MainVDR$DownloadVDR";
	private DatabaseConnector datasource;
	Context mContext;
	   
	
	DownloadVDR(Context context){
        super();
        this.mContext = context;
    }
    
	private void downloadFromUrl(String imageURL, String fileName) {
	    try {
	            // Connect to the URL
	            URL myImageURL = new URL(imageURL);
	            HttpURLConnection connection = (HttpURLConnection)myImageURL.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();

	            // Get the bitmap
	            Bitmap myBitmap = BitmapFactory.decodeStream(input);

	            // Save the bitmap to the file
	            String path = Environment.getExternalStorageDirectory().toString();

	            OutputStream fOut = null;
	            File file = new File(path, fileName);
	            Log.i("help", file.getAbsolutePath());
	            Log.i("help", file.toString());
	            Log.i("help", file.length() + "");
	            //System.out.println("THIS IS A TEST OF SYSTEM.OUT.PRINTLN()");
	            fOut = new FileOutputStream(file);

	            myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
	            fOut.flush();
	            fOut.close();
	            System.out.println("file Path: " + file.getAbsolutePath());
	            Log.i("help2", file.getAbsolutePath());
	            Log.i("help2", file.toString());
	            Log.i("help2", file.length() + "");

	        } catch (IOException e) {}
	}
	
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if ( datasource != null ){
			datasource.close();
		}
			
	}


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		Log.d(DEBUG_TAG, "onPreExecute -- dialog");
		
		super.onPreExecute();
		try {
			datasource = new DatabaseConnector(mContext);
		} catch (SQLException e) {

			throw new Error("Error open database");

		}
		datasource.open();

	}


	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}


	@Override
	protected Boolean doInBackground(Object... arg0) {
		
		Log.d(DEBUG_TAG, "doInBackground" );
		MovieMeterPluginSession session = null;
		boolean result = false;
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

		session = new MovieMeterPluginSession();
		try {
			//datasource.open();
			// datasource.deleteAllChannels();
			// datasource.deleteAllEvents();
			// datasource.deleteAllHash();
			int type;
			int toChannel = 19;
			Boolean endOfSession = false;
			String data = new String();
			CRC32 checkSum = new CRC32();
			String sendSting = new String();
			// Socket s = new Socket("192.168.2.13", 6419);
			// OutputStream os = s.getOutputStream();
			// InputStream is = s.getInputStream();
			// DataInputStream dis = new DataInputStream(is);
			// DataOutputStream dos = new DataOutputStream(os);
			byte[] rl = new byte[] { 13, 10 };
			byte[] buffer = new byte[250];
			// delete all channels for now the easy way.

			// datasource.deleteAllChannels();
			for (int channel = 5; channel <= toChannel; channel++) {
				// for all channel that need to be collected
				// for now its the first 30 channels

				long cur_Id = -1;
				Socket s = new Socket("192.168.2.13", 6419);

				OutputStream os = s.getOutputStream();
				InputStream is = s.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				DataOutputStream dos = new DataOutputStream(os);

				publishProgress(Integer
						.toString((int) ((channel / (float) toChannel) * 100)));
				// sendSting = "LSTE " + channel ; //+ " NOW"; // currently

				sendSting = "LSTE " + channel; // + " NOW"; // currently
															// only
															// the
															// now
															// event
															// data
				dos.write(sendSting.getBytes());
				dos.write(rl);
				// clear data
				cur_Id = -1;
				Ev_time = -1;
				Ev_dr = -1;
				endOfSession = false;
				Log.d(DEBUG_TAG, "TI: channel " + Integer.toString(channel));

				do {
					try {
						data = dis.readLine();
						data.getBytes(0, 2, buffer, 0);
						type = Integer.parseInt(data.substring(0, 3));
						switch (type) {
						case 214: // Help message
							break;
						case 215: // EPG data record
							String dataObj[] = data.split(" ", 3);

							if (dataObj[0].contentEquals("215-C")) {
								// new channel record store in database
								cur_Id = datasource.insertChannel(channel,
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
													Log.d(DEBUG_TAG,
																"NEW FILM "
																		+ String.valueOf(checkSum
																				.getValue()));
													idFilm = filmInfo.get("filmId").toString();
													filmInfo = session.getMovieDetailsById( Integer.parseInt(idFilm));
													downloadFromUrl(filmInfo.get("thumbnail").toString(), "VDR_TH_" + idFilm + ".jpg");
													//idFilm = filmInfo.get("filmId").toString();
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
												Data_Id = datasource.insertData(Integer.parseInt(idFilm), filmInfo.toString());
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
												.insertEventNoCheck(Ev_ch_key, Ev_nr,
														Ev_time, Ev_dr, Ev_tt, Ev_st,
														Ev_gt, Ev_rft + " " + Ev_rlt,
														Ev_hsh_key);

									}
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
							Log.d(DEBUG_TAG, data.toString());
							endOfSession = true;
							break;

						case 500: // Syntax error, command unrecognized
						case 501: // Syntax error in parameters or arguments
							dos.write(sendSting.getBytes());
							dos.write(rl);
							Log.d(DEBUG_TAG,
									"Try again same command " + data.toString());

							break;
						default:
							Log.d(DEBUG_TAG, "Default case " + data.toString());
							break;
						}
						// Log.d(DEBUG_TAG, data.toString());

					} catch (Exception NumberFormatException) {
						// TODO: handle exception
						Log.d(DEBUG_TAG,
								data.toString() + NumberFormatException.getMessage());
						endOfSession = true;
						// break out of while loop and go no next channel
						break;
					}

				} while (!endOfSession);
				sendSting = "QUIT";
				dos.write(sendSting.getBytes());
				dos.write(rl);
				data = dis.readLine();
				s.close();

			}

			// for all recordings in database
			boolean endRecordings = false;
			endOfSession = false;
			int recNr = 1;
			int Ev_wt = -1;

			long cur_Id = -1;
			Socket s = new Socket("192.168.2.13", 6419);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);

			sendSting = "LSTR " + recNr++; // all recordings
			dos.write(sendSting.getBytes());
			dos.write(rl);
			// clear data
			cur_Id = -1;
			Ev_time = -1;
			Ev_dr = -1;

			do {
				try {
					// recording = dis.readLine();
					// sendSting = "LSTR " + recNr++; // all recordings
					// dos.write(sendSting.getBytes());
					// dos.write(rl);
					sendSting = "LSTR " + recNr++; // all recordings
					dos.write(sendSting.getBytes());
					dos.write(rl);
					// clear data
					cur_Id = -1;
					Ev_time = -1;
					Ev_dr = -1;
					endOfSession = false;

					do {
						data = dis.readLine();

						type = Integer.parseInt(data.substring(0, 3));
						switch (type) {
						case 214: // Help message
							break;
						case 215: // EPG data record
							String dataObj[] = data.split(" ", 3);

							if (dataObj[0].contentEquals("215-C")) {
								// search channel id related to this
								// service

								cur_Id = datasource.getCannelIdService(dataObj[1]);
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
													Log.d(DEBUG_TAG,
																"NEW FILM "
																		+ String.valueOf(checkSum
																				.getValue()));
													idFilm = filmInfo.get("filmId").toString();
													filmInfo = session.getMovieDetailsById( Integer.parseInt(idFilm));
													downloadFromUrl(filmInfo.get("thumbnail").toString(), "VDR_TH_" + idFilm + ".jpg");
													//idFilm = filmInfo.get("filmId").toString();
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
												Data_Id = datasource.insertData(Integer.parseInt(idFilm), filmInfo.toString());
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
							Log.d(DEBUG_TAG, data.toString());
							endOfSession = true;
							endRecordings = true;
							break;

						case 500: // Syntax error, command unrecognized
						case 501: // Syntax error in parameters or
										// arguments
							dos.write(sendSting.getBytes());
							dos.write(rl);
							Log.d(DEBUG_TAG,
									"Try again same command " + data.toString());

							break;
						default:
							Log.d(DEBUG_TAG, "Default case " + data.toString());
							break;
						}
						// Log.d(DEBUG_TAG, data.toString());
					} while (!endOfSession);

				} catch (Exception NumberFormatException) {
					// TODO: handle exception
					Log.d(DEBUG_TAG,
							data.toString() + NumberFormatException.getMessage());
					endOfSession = true;
					// break out of while loop and go no next channel
					break;
				}

			} while (!endRecordings);
			sendSting = "QUIT";
			dos.write(sendSting.getBytes());
			dos.write(rl);
			data = dis.readLine();
			s.close();

			// sendSting = "QUIT";
			// dos.write(sendSting.getBytes());
			// dos.write(rl);
			// data = dis.readLine();
			// Log.d(DEBUG_TAG, data.toString());
			// s.close();

		} catch (Exception e) {
			Log.e(DEBUG_TAG, "Unexpected failure in collecting event data", e);

		}

		//datasource.close();

		return true;
	}

}
