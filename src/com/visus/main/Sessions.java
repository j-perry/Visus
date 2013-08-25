package com.visus.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.database.UserHandler;
import com.visus.entities.User;
import com.visus.entities.sessions.Overview;
import com.visus.entities.sessions.Session;
import com.visus.ui.SessionsAdapter;
import com.visus.ui.SessionsListView;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Enables the user to view previous sessions
 * @author Jonathan Perry
 *
 */
public class Sessions extends Activity {
	
	private List<HashMap<String, String>> adapterItems;
	private Session sessionOverview;
	private int activeUserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		
		Log.e("Visus", "Session onCreate()");
		
		SessionHandler dbSession = new SessionHandler(this);
		ArrayList<Session> allSessions = new ArrayList<Session>();
		
		
		// get user id
		Bundle bundle = getIntent().getExtras();
		activeUserId = bundle.getInt("ActiveUserId");

		Log.e("Visus", "USER ID: " + activeUserId);
		
				
//		ListView sessionsList = (ListView) findViewById(R.id.list_previous_sessions);
			
		dbSession.open();
		sessionOverview = dbSession.getOverview(activeUserId);
		dbSession.close();
		
		
		dbSession.open();
		allSessions = dbSession.getSessions(activeUserId);
		dbSession.close();
		
		
		if(sessionOverview != null) {
			Log.e("Visus", "session overview is not null");
			Log.e("Visus", "Session overview (hours): " + String.valueOf(sessionOverview.getOverviewHours() ));
			Log.e("Visus", "Session overview (sessions): " + String.valueOf(sessionOverview.getOverviewNoSessions() ));
			Log.e("Visus", "Session overview (activities): " + String.valueOf(sessionOverview.getOverviewNoActivities() ));
		}
		else {
			Log.e("Visus", "session overview is null");
		}
		
				
		// assign adapter items
		//List<HashMap<String, String>> adapterList = new ArrayList<HashMap<String, String>>();
		
		ArrayList<HashMap<String, String>> adapterList = new ArrayList<HashMap<String, String>>();
		
		
		
		// createList(key, name)
		// NB: param 'key' must co-ordinate with SimpleAdapters 'from' String array parameter (4th parameter) 
//		adapterList.add( createList("overview", "Overview (hours): " + String.valueOf(sessionOverview.getOverviewHours() ) ));
//		adapterList.add( createList("overview", "Overview (sessions): " + String.valueOf(sessionOverview.getOverviewNoSessions() ) ));
//		adapterList.add( createList("overview", "Overview (activities): " + String.valueOf(sessionOverview.getOverviewNoActivities() ) ));
		
		
		// TODO
		// add each session to the adapter list
//		for(Session session : allSessions) {
//			adapterList.add( createList("overview",	session.getDay() + " " +
//		                    						session.getDayNo() + " " +  
//		                    						session.getMonth() + ", " +
//		                    						session.getYear() + " - " +
//								                    session.getTimeHour() + ":" +
//								                    session.getTimeMinutes() + " " +
//								                    session.getDayPeriod() + " - " +
//								                    session.getDurationMinutes() + ":" +
//								                    session.getDurationSeconds() + " - " +
//								                    session.getType()
//						               ));
//		}
		
		for(Session session : allSessions) {
			HashMap<String, String> map = new HashMap<String, String>();
			
			map.put(SessionsListView.DATE, new StringBuilder(session.getTimeHour() + ":" + session.getTimeMinutes() + session.getDayPeriod() + "\n" + session.getDayNo() + "\n" + session.getMonth()).toString());
			map.put(SessionsListView.TIME, String.valueOf(session.getDurationMinutes()) + ":" + String.valueOf(session.getDurationSeconds()) );
			map.put(SessionsListView.ACTIVITY, session.getType());
			
			adapterList.add(map);			
		}
		
		
		
		// binds our data together before being sent to the 
		// ListView's adapter component for presentation
//		SimpleAdapter adapter = new SimpleAdapter(this, 
//												  adapterList,
//				                                  android.R.layout.simple_list_item_1,	// this is where out custom ListView layout goes. I think...
//				                                  new String[] { "overview" },
//				                                  new int[] { android.R.id.text1 });
		
		
		ListView sessionsList = (ListView) findViewById(R.id.list_previous_sessions);
		SessionsAdapter adapter = new SessionsAdapter(this, adapterList);
		
		// display the contents
		sessionsList.setAdapter(adapter);
	}
	
	/**
	 * Creates a new item in the HashMap for insertion into an ArrayList 
	 * initialised by a Session object
	 * @param key
	 * @param name
	 * @return
	 */
	private HashMap<String, String> createList(String key, String name) {
		HashMap<String, String> items = new HashMap<String, String>();
		items.put(key, name);
		
		return items;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_prev_sessions, menu);
		return true;
	}

}
