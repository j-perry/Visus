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
	private Overview overview;
	private ArrayList<Object> resultsCurrentWeek, 
							  resultsWeeksInMonth, 
							  resultsOtherMonths,
							  resultsOtherYears;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		
//		SessionHandler sessionHandler = new SessionHandler(this);
//		UserHandler presentUser = new UserHandler(this);
//		User user = presentUser.getActiveUser();

		//ListView sessionsList = (ListView) findViewById(R.id.previous_sessions);
		
//		Log.e("Visus", "sdfsdjfnh: " + String.valueOf(user.getUserId()) );
		
//		overview = sessionHandler.getOverview(user.getUserId() );
//		resultsCurrentWeek = sessionHandler.getResultsFromThisWeek(user.getUserId() );
//		resultsWeeksInMonth = sessionHandler.getResultsFromWeeksInMonth(user.getUserId() );
//		resultsOtherMonths = sessionHandler.getResultsFromOtherMonths(user.getUserId() );
//		resultsOtherYears = sessionHandler.getResultsFromOtherYears(user.getUserId() );
				
		// assign adapter items
//		List<HashMap<String, String>> adapterList = new ArrayList<HashMap<String, String>>();
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("col1", String.valueOf(overview.getSessionNos()));
//		map.put("col2", String.valueOf(overview.getHours()));
//		map.put("col3", String.valueOf(overview.getActivitiesNos()));
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
