package net.go2mycloud.vdrmovie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SVDRPInterface extends android.os.AsyncTask<String, Integer, String>{
	
	private static final String D_TAG = "MainVDR$SVDRPInterface";
	private String host="192.168.2.13";
	private int port = 6419;

	Context mContext;
	   
	
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
	
	public boolean isNetworkAvailable() {
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
					// TODO: handle exception
					Log.d("exception", data.toString() + NumberFormatException.getMessage());
					// moreData = false;
					// break out of while loop and go no next channel
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
			return playRecordingByNum(playrec);
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private String playRecordingByNum(String recNum) {
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

			String sendSting = "PLAY " + recNum;
			recNum="";
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
					Log.d(D_TAG, "play  " + data);
					recNum = data.toString();
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

	private String hitKey(String key) {
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

			String sendSting = "HITK " + key;
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
					Log.d(D_TAG, "play  " + data);
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

	@Override
	protected String doInBackground(String... svdrpC ) {
	    int count = svdrpC.length;
	    if ( !isNetworkAvailable() )
	    {
	    	return null;
	    }
        // This will download stuff from each URL passed in
        for (int i = 0; i < count - 1; i++) {
        	if( svdrpC[i].contains("PLAY") ) {
        		return playRecordingByName(svdrpC[1]);
        	}
        	if( svdrpC[i].contains("HITK") ) {
        		return hitKey(svdrpC[1]);
        	}
        	if( svdrpC[i].contains("CHAN") ) {
        		return sendCommand(svdrpC[i], svdrpC[1]);
        	}
        }
    
        
		// TODO Auto-generated method stub
		return null;
	}



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