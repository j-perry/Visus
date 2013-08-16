package com.visus.main;

import java.util.*;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.entities.sessions.Session;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

public class LatestActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_latest_activity);

		SessionHandler dbSession = new SessionHandler(this);
		ArrayList<Session> latestActivity = new ArrayList<Session>();
		int userId = 0;
		
		Bundle bundle = getIntent().getExtras();
		userId = bundle.getInt("ActiveUserId");
		
		Log.e("Visus", "Active User Id (Latest Activity): " + userId);
		
		ListView activities = (ListView) findViewById(R.id.list_latest_activity);
		
		dbSession.open();
		latestActivity = dbSession.getSessions(userId);
		dbSession.close();
		
		// initialise the ListView
		List<HashMap<String, String>> adapterList = new ArrayList<HashMap<String, String>>();
		
		for(Session activity : latestActivity) {
			adapterList.add(createList("latest_activities", activity.getDay() + " " +
															activity.getDayNo() + " " +  
															activity.getMonth() + ", " +
															activity.getYear() + " - " +
															activity.getTimeHour() + ":" +
															activity.getTimeMinutes() + " " +
															activity.getDayPeriod() + " - " +
															activity.getDurationMinutes() + ":" +
															activity.getDurationSeconds() + " - " +
															activity.getType()
					                  ));
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this,
				                                  adapterList,
				                                  android.R.layout.simple_list_item_1,
				                                  new String[] { "latest_activities" },
				                                  new int[] { android.R.id.text1 });
		activities.setAdapter(adapter);		
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
	
}
