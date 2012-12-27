package net.go2mycloud.vdrmovie;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class TimerXml {

	private XmlPullParserFactory factory;
	private XmlPullParser xpp;
	public static final String XML_epgsearch = "epgsearch";
	public static final String XML_channel = "channel";
	public static final String XML_update = "update";
	public static final String XML_eventid = "eventid";
	public static final String XML_bstart = "bstart";
	public static final String XML_bstop = "bstop";
	public static final String XML_start = "start";
	public static final String XML_stop = "stop";

	private String epgsearch = "";
	private String channel = "";
	private String update = "";
	private String eventid = "";
	private String bstart = "";
	private String bstop = "";
	private String start = "";
	private String stop = "";	
	
	
	TimerXml(){
	    try {
			factory = XmlPullParserFactory.newInstance();
		    factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean setInput( String xmlData ){
		
		try {
			xpp.setInput(new StringReader (xmlData));
			return true;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String getTag(String tag) {
		boolean startT = false;
		try {
			int eventType;
			eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
					//System.out.println("Start document");
				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					//System.out.println("End document");
				} else if (eventType == XmlPullParser.START_TAG) {
					//System.out.println("Start tag " + xpp.getName());
					if (xpp.getName().contains(tag)) {
						startT = true; 
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					//System.out.println("End tag " + xpp.getName());
				} else if (eventType == XmlPullParser.TEXT && startT) {
					return xpp.getText();
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}


	public void setEpgsearch(String epgsearch) {
		this.epgsearch = epgsearch;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public void setBstart(String bstart) {
		this.bstart = bstart;
	}

	public void setBstop(String bstop) {
		this.bstop = bstop;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}
	

	
}
