package com.visus.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.entities.sessions.Session;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Enables the user to view the activities (by all categories)
 * @author Jonathan Perry
 *
 */
public class ActivityCategories extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_categories);
		
		SessionHandler dbSession = new SessionHandler(this);
		ArrayList<String> activityTypes = new ArrayList<String>();
		int userId = 0;
		
		Bundle bundle = getIntent().getExtras();
		userId = bundle.getInt("ActiveUserId");
		
		Log.e("Visus", "Active User Id (Activities): " + userId);
		
		ListView activities = (ListView) findViewById(R.id.list_activity_types);
		
		dbSession.open();
		activityTypes = dbSession.getSessionTypes(userId);
		dbSession.close();
		
		// initialise the ListView
		List<HashMap<String, String>> adapterList = new ArrayList<HashMap<String, String>>();
		
		for(String type : activityTypes) {
			adapterList.add(createList("activities", type));
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this,
				                                  adapterList,
				                                  android.R.layout.simple_list_item_1,
				                                  new String[] { "activities" },
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
