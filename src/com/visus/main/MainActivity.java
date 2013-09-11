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
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuAdapter;
import com.visus.ui.MainMenuListView;
import com.visus.ui.main.fragments.ActivitiesFragment;
import com.visus.ui.main.fragments.LatestActivityFragment;

/**
 * Main entry point of the app
 * @author Jonathan Perry
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private User user = null;
	private UserHandler dbHandler;
	private SessionHandler dbSession;
	private static int userId;
	
	private final String hdrLatestActivity = "Latest Activity";
	
	private ListView list;
	private MainMenuAdapter adapter;
	
	private ViewPager mainMenuPager;
	private MainMenuPagerAdapter mainMenuPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		dbHandler = new UserHandler(this);
		dbSession = new SessionHandler(this);
				
		setContentView(R.layout.activity_main);
		
		final ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		mainMenuPager = (ViewPager) findViewById(com.visus.R.id.main_menu_pager);
		mainMenuPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager(), userId);
		mainMenuPager.setAdapter(mainMenuPagerAdapter);
		
		
		ArrayAdapter<CharSequence> arAdapter = ArrayAdapter.createFromResource(this, R.array.menu, R.layout.action_bar_list);
		
		/**
		 * The following two methods are crucial. Do not delete them.
		 */
		ab.setListNavigationCallbacks(arAdapter, new OnNavigationListener() {
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				mainMenuPager.setCurrentItem(itemPosition);
				return true;
			}
		});
		
		mainMenuPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {			
			@Override
			public void onPageSelected(int position) {
				ab.setSelectedNavigationItem(position);
			}			
		});
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
				sessions = dbSession.getLatestSessions(userId);
//				sessions = dbSession.getSessionsThisYear(userId);
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
				
				int noItems = 0;
				
				// output the first five results
				for(Session session : sessions) {
					if(noItems != 5) {				
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
						
						latestSessions.add(map);
						
						noItems++;
					}
					else {
						break;
					}
				}
						
//				list = (ListView) findViewById(com.visus.R.id.overview_sessions_adapter);
//				adapter = new MainMenuAdapter(this, latestSessions);
//				
//				list.setAdapter(adapter);
			}
		}
				
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * Action bar events
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		
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
				intent = new Intent(MainActivity.this, NewSession.class);
				intent.putExtra("ActiveUserId", user.getUserId());
				startActivity(intent);
				break;
			case R.id.menu_sessions:
				intent = new Intent(MainActivity.this, Sessions.class);
				intent.putExtra("ActiveUserId", user.getUserId());
				startActivity(intent);
				break;
			case R.id.menu_settings:
				intent = new Intent(MainActivity.this, Settings.class);
				intent.putExtra("ActiveUserId", user.getUserId());
				startActivity(intent);
				break;
			default:
				break;		
		}
		
		return true;
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
	
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mainMenuPager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	/*************************************************************************
	 * 
	 * 								Event Handlers
	 * 
	 */
			
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
		intent.putExtra("ActiveUserId", user.getUserId());
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
	
	public static final class MainMenuPagerAdapter extends FragmentPagerAdapter {

		private static final int NO_FRAGMENTS = 2;
		private int userId;
		
		public MainMenuPagerAdapter(FragmentManager fm, int userId) {
			super(fm);
			this.userId = userId;
		}

		@Override
		public Fragment getItem(int item) {
			switch(item) {
				case 0:
					// display latest sessions made
					return new LatestActivityFragment(userId);
				case 1:
					// display session activity types (no. of for each type)
					return new ActivitiesFragment(userId);
				default:
					Fragment f = new Fragment();
					return f;
			}
		}

		/**
		 * No of fragment views
		 */
		@Override
		public int getCount() {
			return NO_FRAGMENTS;
		}
		
	}

}
