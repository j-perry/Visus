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
	private ArrayList<Object> resultsCurrentWeek, 
							  resultsWeeksInMonth, 
							  resultsOtherMonths,
							  resultsOtherYears;
	private int activeUserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		
		Log.e("Visus", "Session onCreate");
		
		SessionHandler dbSession = new SessionHandler(this);
		dbSession.open();
		
		// get user id
		Bundle bundle = getIntent().getExtras();
		activeUserId = bundle.getInt("ActiveUserId");

		Log.e("Visus", "USER ID: " + activeUserId);
		
//		ListView sessionsList = (ListView) findViewById(R.id.previous_sessions);
				
		sessionOverview = dbSession.getOverview(activeUserId);
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
		
//		Log.e("Visus", "Session overview (hours): " + sessionOverview.getOverviewHours());
		
////		resultsCurrentWeek = dbSession.getResultsFromThisWeek(user.getUserId() );
////		resultsWeeksInMonth = dbSession.getResultsFromWeeksInMonth(user.getUserId() );
////		resultsOtherMonths = dbSession.getResultsFromOtherMonths(user.getUserId() );
////		resultsOtherYears = dbSession.getResultsFromOtherYears(user.getUserId() );
//				
//		// assign adapter items
//		List<HashMap<String, String>> adapterList = new ArrayList<HashMap<String, String>>();
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("col2", String.valueOf(sessionOverview.getOverviewHours() ));
//		map.put("col1", String.valueOf(sessionOverview.getOverviewNoSessions() ));
//		map.put("col3", String.valueOf(sessionOverview.getOverviewNoActivities() ));
//		
//		adapterList.add(map);
//		
//		SimpleAdapter adapter = new SimpleAdapter(this, 
//												  adapterList,
//				                                  android.R.layout.simple_list_item_1,
//				                                  new String[] { "overview" },
//				                                  new int[] { android.R.id.text1 });		
//		sessionsList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_prev_sessions, menu);
		return true;
	}

}
