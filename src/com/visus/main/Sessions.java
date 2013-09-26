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
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
	
	private int noItems;
	
	private ViewPager sessionsPager;
	private SessionsPagerAdapter sessionsPagerAdapter;
		
	private AlertDialog alertDialog;
	private Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SessionHandler dbSessions = new SessionHandler(this);
		ArrayList<Session> allSessions = new ArrayList<Session>();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		
		noItems = 0;
		
		Log.e("Visus", "Session onCreate()");
		
		// get user id
		Bundle bundle = getIntent().getExtras();
		activeUserId = bundle.getInt("ActiveUserId");

		Log.e("Visus", "USER ID: " + activeUserId);
		
		
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		try {
			dbSessions.open();
			allSessions = dbSessions.getSessionsThisYear(activeUserId);
			Log.e("Visus", "No items: " + allSessions.size());
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbSessions.close();
		}
		
		
		// determine whether any sessions exist, if not display an AlertDialog asking the user if they wish
		// to create a new session. Improves UX.
		if(allSessions.isEmpty()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle("Create Session");
			
			alertDialogBuilder.setMessage("No sessions exist. Do you wish to create a new session now?"); 
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton("Yes Please!", new OkOnClickListener());
			alertDialogBuilder.setNegativeButton("No Thanks", new CancelOnClickListener());
			alertDialog = alertDialogBuilder.create();
			
			// show it
			alertDialog.show();
		}
		
		sessionsPager = (ViewPager) findViewById(com.visus.R.id.sessions_pager);
		sessionsPagerAdapter = new SessionsPagerAdapter(getSupportFragmentManager(), activeUserId);
			
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
	 * AlertDialog event handler that displays a new session view
	 * @author Jonathan Perry
	 *
	 */
	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(getApplicationContext(), NewSession.class);
			intent.putExtra("ActiveUserId", activeUserId);
			context.startActivity(intent);
		}
	}
		
	/**
	 * AlertDialog event handler that dismisses itself
	 * @author Jonathan Perry
	 *
	 */
	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			alertDialog.dismiss();
		}
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
			
	
	/**
	 * Returns to the root menu
	 */
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

	/**
	 * Inflates the options menu
	 */
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
		Intent intent = null;
		
		switch(item.getItemId()) {
			// app logo
			case android.R.id.home:
				intent = new Intent(this, MainActivity.class);
	            if (NavUtils.shouldUpRecreateTask(this, intent)) {
	                // This activity is not part of the application's task, so create a new task
	                // with a synthesized back stack.
	                TaskStackBuilder.from(this)
	                        // If there are ancestor activities, they should be added here.
	                        .addNextIntent(intent)
	                        .startActivities();
	                finish();
	            } else {
	                // This activity is part of the application's task, so simply
	                // navigate up to the hierarchical parent activity.
	                NavUtils.navigateUpTo(this, intent);
	            }
	            break;
            // new session
			case R.id.new_session_menu:
				intent = new Intent(this, NewSession.class);
				intent.putExtra("ActiveUserId", activeUserId);
	            startActivity(intent);
	            break;
	        // settings
			case R.id.menu_settings:
				intent = new Intent(Sessions.this, Settings.class);
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
