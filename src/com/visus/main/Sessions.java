package com.visus.main;

// core apis
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.*;
import android.app.ActionBar.Tab;
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
		
		
		sessionsPager = (ViewPager) findViewById(com.visus.R.id.sessions_pager);		
		sessionsPagerAdapter = new SessionsPagerAdapter(getSupportFragmentManager(), activeUserId );
		
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

		public SessionsPagerAdapter(FragmentManager fm, int userId) {
			super(fm);
			this.userId = userId;
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
					return new FragmentThisWeek(userId);
				case 2:
					// ... from this month
					return new FragmentThisMonth();
				case 3: 
					// ... from this year
					return new FragmentThisYear();
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
