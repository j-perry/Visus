package com.visus.main;

// core apis
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

// android apis
import android.os.Bundle;
import android.app.*;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.ListView;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuAdapter;
import com.visus.ui.MainMenuListView;

/**
 * Main entry point of the app
 * @author Jonathan Perry
 *
 */
public class MainActivity extends Activity {
	
	private User user = null;
	private UserHandler dbHandler;
	private SessionHandler dbSession;
	private static int userId;
	
	private final String hdrLatestActivity = "Latest Activity";
	
	private ListView list;
	private MainMenuAdapter adapter;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		dbHandler = new UserHandler(this);
		dbSession = new SessionHandler(this);
				
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Log.e("Visus", "onResume()");
		dbHandler.open();
		user = dbHandler.getActiveUser();
		dbHandler.close();
		
		if(user == null) {
			Log.e("Visus", "No user active");
			Intent intent = new Intent(MainActivity.this, SignUp.class);
			startActivity(intent);
		}		
		else {
			this.userId = user.getUserId();
			ArrayList<Session> sessions = new ArrayList<Session>();
			ArrayList<String> sessionTypes = new ArrayList<String>();
			Log.e("Visus", "USER ID: " + userId);
			
			try {
				dbSession.open();
				sessions = dbSession.getLatestSessions(user.getUserId());
//				sessionTypes = dbSession.getSessionTypes(user.getUserId());
			}
			finally {
				dbSession.close();				
			}
						
			if(sessions.isEmpty()) {
				Log.e("Visus", "Sessions is empty");
			}
			else {
				Log.e("Visus", "Sessions is not empty");

				
				ArrayList<HashMap<String, String>> latestSessions = new ArrayList<HashMap<String, String>>();
				
				Log.e("Visus", "onCreate() - User ID is: " + userId);

				String [] data = { "Hello", "Jon", "Today", "Sunday" };
				
				for(Session session : sessions) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(MainMenuListView.SESSION, session.getDay() + " " +
							    					  session.getDayNo() + " " +  
							    					  session.getMonth() + ", " +
							    					  session.getDate() + ", " +
							    					  session.getYear() + " - " +
									                  session.getTimeHour() + ":" +
									                  session.getTimeMinutes() + " " +
									                  session.getDayPeriod() + " - " +
									                  session.getDurationMinutes() + ":" +
									                  session.getDurationSeconds() + " - " +
									                  session.getType()
					       );
//					
					latestSessions.add(map);
					
					
				}
						
				list = (ListView) findViewById(com.visus.R.id.overview_sessions_adapter);
				adapter = new MainMenuAdapter(this, latestSessions);
				
				list.setAdapter(adapter);				
			}
		}
				
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.new_session_menu:
				Intent intent = new Intent(MainActivity.this, NewSession.class);
				intent.putExtra("ActiveUserId", user.getUserId());
				startActivity(intent);
				break;
			default:
				break;		
		}
		
		return true;
	}
	*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.activity_main, menu);
		
//		MenuItem m = menu.findItem(R.drawable.ic_launcher);
//		m.setVisible(false);
		
		return true;
	}
			
	/**
	 * creates a new session
	 * @param view
	 */
	public void newSession(View view) {
		Intent intent = new Intent(MainActivity.this, NewSession.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}
	
	/**
	 * view previous sessions
	 * @param view
	 */
	public void prevSessions(View view) {
		Intent intent = new Intent(MainActivity.this, Sessions.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}
	
	/**
	 * view app settings
	 * @param view
	 */
	public void viewSettings(View view) {
		Intent intent = new Intent(MainActivity.this, Settings.class);
		startActivity(intent);
	}
	
	/**
	 * view latest user activity (date-filtered)
	 * @param view
	 */
	public void viewUserActivity(View view) {
		Intent intent = new Intent(MainActivity.this, LatestActivity.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}
		
	/**
	 * view latest user activity (across all categories)
	 * @param view
	 */
	public void viewUserActivities(View view) {
		Intent intent = new Intent(MainActivity.this, ActivityCategories.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}
	
	/**
	 * view latest user activity (across all categories)
	 * @param view
	 */
	public void viewLatestUserActivities(View view) {
		Intent intent = new Intent(MainActivity.this, LatestActivity.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}

	/**
	 * view activity category selected
	 * @param view
	 */
	public void viewUserActivityCategory(View view) {
		Intent intent = new Intent(MainActivity.this, ActivityCategory.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}
	
	private void test() {
//		int dayNo = 0;
//		
//		String target = null;
//		String day = null;
//		int saturdayDayNo = 0;
//
//		
//		while(!target.contains("Sun")) {
//			dayNo = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()) );
//			day = new SimpleDateFormat("EEE").format(dayNo);
//			
//			if(day == target) {
//				saturdayDayNo = dayNo;
//				Log.e("Visus", "Day found");
//			}
//			else {
//				dayNo--;
//			}
//		}
	}
	
}
