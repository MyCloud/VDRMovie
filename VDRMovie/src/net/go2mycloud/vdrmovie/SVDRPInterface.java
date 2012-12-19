package net.go2mycloud.vdrmovie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
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
		// TODO Auto-generated constructor stub
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
						Log.d(D_TAG, "D:" + dataObj[0]);
						Log.d(D_TAG, "D:" + dataObj[1]);
						Log.d(D_TAG, "D:" + dataObj[2]);
						Log.d(D_TAG, "D:" + dataObj[3]);
						Log.d(D_TAG, "D:" + dataObj[4]);
						Log.d(D_TAG, "D:" + dataObj[5]);
						//do the things
						if (dataObj[5].contains(recName)) {
							//found recording
							playrec = dataObj[1];
						}
						if (data.substring(3, 4).contains(" ") | playrec != null ) {
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
					// Log.d(DEBUG_TAG,
					// data.toString() + NumberFormatException.getMessage());
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
		return "";
	}

	@SuppressWarnings("deprecation")
	private String playRecordingByNum(String recNum) {
		byte[] rl = new byte[] { 13, 10 };
		byte[] buffer = new byte[250];
		String data = new String();
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
			if (buffer.equals("220")) {
				data = dis.readLine();
				if (buffer.equals("250")) {
					// last record
					sendSting = "QUIT";
					dos.write(sendSting.getBytes());
					dos.write(rl);					
					data = dis.readLine();
					recNum = data.toString();
				}
			}
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recNum;

	}

	@Override
	protected String doInBackground(String... svdrpC ) {
	    int count = svdrpC.length;

        // This will download stuff from each URL passed in
        for (int i = 0; i < count; i++) {
        	if( svdrpC[i].contains("PLAY") ) {
        		return playRecordingByName(svdrpC[1]);
        	}
        }
    
		// TODO Auto-generated method stub
		return null;
	}


}
