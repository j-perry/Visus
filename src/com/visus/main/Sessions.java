package com.visus.main;

// core apis
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.*;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.Week;
import com.visus.entities.User;
import com.visus.entities.sessions.*;
import com.visus.ui.*;
import com.visus.ui.sessions.fragments.*;

/**
 * Enables the user to view previous sessions
 * @author Jonathan Perry
 *
 */
public class Sessions extends FragmentActivity implements ActionBar.TabListener {
	
	private List<HashMap<String, String>> adapterItems;
	private Session sessionOverview;
	private int activeUserId;
	
	private ViewPager sessionsPager;
	private SessionsPagerAdapter sessionsPagerAdapter;
	
	private Week wkBeginning;
	private Week wkEnd;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SessionHandler dbSession = new SessionHandler(this);
		ArrayList<Session> allSessions = new ArrayList<Session>();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		
		Log.e("Visus", "Session onCreate()");
		
		// get user id
		Bundle bundle = getIntent().getExtras();
		activeUserId = bundle.getInt("ActiveUserId");

		Log.e("Visus", "USER ID: " + activeUserId);
		
		
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// find the beginning and end of the present week
		wkBeginning = findBeginningOfWeek();
		wkEnd = findEndOfWeek();		
		
		sessionsPager = (ViewPager) findViewById(com.visus.R.id.sessions_pager);		
		sessionsPagerAdapter = new SessionsPagerAdapter(getSupportFragmentManager(), activeUserId, wkBeginning, wkEnd );
		
		// initialise the page view adapter
		sessionsPager.setAdapter(sessionsPagerAdapter);
		sessionsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				ab.setSelectedNavigationItem(position);
			}
			
		});
		
				
		// create our tabs
		initTabs(ab);
        					
				
//		ListView sessionsList = (ListView) findViewById(R.id.list_previous_sessions);
			
		// TODO - 26/08/2013
//		dbSession.open();
//		sessionOverview = dbSession.getOverview(activeUserId);
//		dbSession.close();
		
		
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
		
		
		// retrieve each returned Session entry and format them as required...
		for(Session session : allSessions) {
			// maps each session entry to HashMap for insertion into adapter
			HashMap<String, String> map = new HashMap<String, String>();
			
			// DATE
			String timeHours = null;
			String timeMins = null;
			String dayPeriod = null;
			
			// TIME
			String durationMins = null;
			String durationSecs = null;
			
			
			// Get the time (DATE)...			
			// ... hour
			timeHours = String.valueOf(session.getTimeHour());
			
			// ... minutes
			if(session.getTimeMinutes() < 10) {
				timeMins = String.valueOf(0) + String.valueOf(session.getTimeMinutes());
			}
			else {
				timeMins = String.valueOf(session.getTimeMinutes());
			}
			
			dayPeriod = session.getDayPeriod();
			
			
			// Get the time (TIME)
			durationMins = String.valueOf(session.getDurationMinutes());
			
			if(session.getDurationSeconds() < 10) {
				durationSecs = String.valueOf(0) + String.valueOf(session.getDurationSeconds());
			}
			else {
				durationSecs = String.valueOf(session.getDurationSeconds());
			}
			
			// map data to ListView
			map.put(SessionsListView.DATE, new StringBuilder(timeHours + ":" + timeMins + dayPeriod + "\n" + session.getDayNo() + "\n" + session.getMonth()).toString());
			map.put(SessionsListView.TIME, new StringBuilder(durationMins + ":" + durationSecs).toString());
			map.put(SessionsListView.ACTIVITY, session.getType());
			
//			adapterList.add(map);
		}
		
		
		
		// binds our data together before being sent to the 
		// ListView's adapter component for presentation
//		SimpleAdapter adapter = new SimpleAdapter(this, 
//												  adapterList,
//				                                  android.R.layout.simple_list_item_1,	// this is where out custom ListView layout goes. I think...
//				                                  new String[] { "overview" },
//				                                  new int[] { android.R.id.text1 });
		
		
		
		
		
//		TODO
//
//		ListView sessionsList = (ListView) findViewById(R.id.list_previous_sessions);
//		SessionsAdapter adapter = new SessionsAdapter(this, adapterList);
//		
//		// display the contents
//		sessionsList.setAdapter(adapter);				
	}
	
	/**
	 * Find beginning of the week returning the day no. in the month it is associated with.
	 * Returns the date
	 */
	private Week findBeginningOfWeek() {
		Week wkBeginning = new Week();
		String day = null;
		int dayNoResult = 0;
		int monthResult = 0;
		int yearResult = 0;
		String monthResultStr = null;
		boolean firstDayFound = false;
		
		
		// convert dayNo to day (String) rep
		// first get the current month
		
		Log.e("Visus", "---------------------");
		Log.e("Visus", "findBeginningOfWeek()");
		Log.e("Visus", "---------------------");
		
		// get the current date
		Calendar cal = Calendar.getInstance();
		// set the time
		cal.setTime(new Date() );
				
		do {				
			// return today's day no. in the present month
			DateFormat dfDayStr = new SimpleDateFormat("EEE");		// Sun, e.g.
			Date dt = cal.getTime();								// get the time
			
			// then get the day based on the month		
			day = dfDayStr.format(dt).toString();					// get day String (EEE)
			
			Log.e("Visus", "Day: " + day);
			
			// if it it not Sat-urday - the beginning of the week
			if(!day.contains("Sat")) {
				// go back a day (or month and/or year)
				cal.add(Calendar.DATE, -1);		//
			}
			else {
				DateFormat dfMonth = new SimpleDateFormat("MMM");
				dt = cal.getTime();
				
				// assign results
				dayNoResult = cal.get(Calendar.DAY_OF_MONTH);	// get day of month - i.e., 31st
				monthResult = (cal.get(Calendar.MONTH) + 1);
				monthResultStr = dfMonth.format(dt).toString();	// get month - i.e., Sep
				yearResult = cal.get(Calendar.YEAR);			// get year - i.e., 2013
				
				wkBeginning.setDayNo(dayNoResult);
				wkBeginning.setMonth(monthResult);
				wkBeginning.setYear(yearResult);
				
				// output results to log
				Log.e("Visus", "------------------");
				Log.e("Visus", "Sat-day found!");
				Log.e("Visus", "dayNoResult: " + dayNoResult);
				Log.e("Visus", "monthResult: " + monthResult);
				Log.e("Visus", "yearResult: " + yearResult);
				
				Log.e("Visus", "Month No. (Beginning): " + (cal.get(Calendar.MONTH) + 1) );
				
				Log.e("Visus", "Beginning of the week: " + dayNoResult + "-" + monthResult + "-" + yearResult);
				
				// terminate
				firstDayFound = true;
			}			
		} while(firstDayFound != true);
		
		Log.e("Visus", "Loop terminated");
		
		return wkBeginning;
	}
	
	/**
	 * 
	 */
	private Week findEndOfWeek() {
		Week wkEnd = new Week();		
		String day = null;
		int dayNoResult = 0;
		int monthResult = 0;
		int yearResult = 0;
		String monthResultStr = null;
		boolean lastDayFound = false;
		
		
		// convert dayNo to day (String) rep
		// first get the current month
		
		Log.e("Visus", "---------------------");
		Log.e("Visus", "findEndOfWeek()");
		Log.e("Visus", "---------------------");
		
		// get the current date
		Calendar cal = Calendar.getInstance();
		// set the time
		cal.setTime(new Date() );
				
		do {				
			// return today's day no. in the present month
			DateFormat dfDayStr = new SimpleDateFormat("EEE");		// Sun, e.g.
			Date dt = cal.getTime();								// get the time
			
			// then get the day based on the month		
			day = dfDayStr.format(dt).toString();					// get day String (EEE)
			
			Log.e("Visus", "Day: " + day);
			
			// if it it not Sat-urday - the beginning of the week
			if(!day.contains("Fri")) {
				// go back a day (or month and/or year)
				cal.add(Calendar.DATE, +1);		//
			}
			else {
				DateFormat dfMonth = new SimpleDateFormat("MMM");
				dt = cal.getTime();
				
				// assign results
				dayNoResult = cal.get(Calendar.DAY_OF_MONTH);	// get day of month - i.e., 31st
				monthResult = (cal.get(Calendar.MONTH) + 1);
				monthResultStr = dfMonth.format(dt).toString();	// get month - i.e., Sep
				yearResult = cal.get(Calendar.YEAR);			// get year - i.e., 2013
				
				wkEnd.setDayNo(dayNoResult);
				wkEnd.setMonth(monthResult);
				wkEnd.setYear(yearResult);
				
				// output results to log
				Log.e("Visus", "------------------");
				Log.e("Visus", "Fri-day found!");
				Log.e("Visus", "dayNoResult: " + dayNoResult);
				Log.e("Visus", "monthResult: " + monthResult);
				Log.e("Visus", "yearResult: " + yearResult);
				
				Log.e("Visus", "Month No. (End): " + (cal.get(Calendar.MONTH) + 1 ));
				
				Log.e("Visus", "End of the week: " + dayNoResult + "-" + monthResult + "-" + yearResult);
				
				// terminate
				lastDayFound = true;
			}
		} while(lastDayFound != true);
		
		Log.e("Visus", "Loop terminated");
		
		return wkEnd;
	}
	
	/**
	 * Initialises new tabs
	 * @param ab
	 */
	private void initTabs(ActionBar ab) {		
		ab.addTab(ab.newTab().setText("TODAY").setTabListener(this));
        ab.addTab(ab.newTab().setText("THIS WEEK").setTabListener(this));
        ab.addTab(ab.newTab().setText("THIS MONTH").setTabListener(this));
        ab.addTab(ab.newTab().setText("THIS YEAR").setTabListener(this));
	}
			
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Sessions.this, MainActivity.class);
		startActivity(intent);
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
		getMenuInflater().inflate(R.menu.activity_prev_sessions, menu);
		return true;
	}
	
	/**
	 * Action bar events
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			// app logo
			case android.R.id.home:
				Intent upIntent = new Intent(this, MainActivity.class);
	            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	                // This activity is not part of the application's task, so create a new task
	                // with a synthesized back stack.
	                TaskStackBuilder.from(this)
	                        // If there are ancestor activities, they should be added here.
	                        .addNextIntent(upIntent)
	                        .startActivities();
	                finish();
	            } else {
	                // This activity is part of the application's task, so simply
	                // navigate up to the hierarchical parent activity.
	                NavUtils.navigateUpTo(this, upIntent);
	            }
	            break;
            // new session
			case R.id.new_session_menu:
				Intent intent = new Intent(Sessions.this, NewSession.class);
				intent.putExtra("ActiveUserId", activeUserId);
				startActivity(intent);
				break;
			default:
				break;		
		}
		
		return true;
	}


	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction arg1) {
		
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		sessionsPager.setCurrentItem(tab.getPosition());		
	}


	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub		
	}
	
	/**
	 * Pager adapter launches and displays new fragments according to the tab selected
	 * @author Jonathan Perry
	 *
	 */
	public static class SessionsPagerAdapter extends FragmentPagerAdapter {
		
		// no of pages!!
		private static final int NO_FRAGMENTS = 4; 
		private int userId;
		private Week wkBeginning;
		private Week wkEnd;

		public SessionsPagerAdapter(FragmentManager fm, int userId, Week beginning, Week end) {
			super(fm);
			this.userId = userId;
			this.wkBeginning = beginning;
			this.wkEnd = end;
		}

		/**
		 * Get's the page item
		 */
		@Override
		public Fragment getItem(int item) {
			switch(item) {
				case 0:
					// displays sessions from today ... if there are any!
					return new FragmentToday(userId);
				case 1:
					// ... from this week
					return new FragmentThisWeek(userId, 
												wkBeginning, // contains the date of the week beginning
												wkEnd);		 // contains the date of the week ending
				case 2:
					// ... from this month
					return new FragmentThisMonth(userId);
				case 3: 
					// ... from this year
					return new FragmentThisYear(userId);
				default:
					Fragment fragment = new Fragment();
					return fragment;
			}
		}

		/**
		 * Return's no of pages
		 */
		@Override
		public int getCount() {
			return NO_FRAGMENTS;
		}
	}
}
