package com.visus.main;

// core apis
import java.util.ArrayList;
import java.util.HashMap;

// android apis
import android.os.Bundle;
import android.app.*;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
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
import android.widget.TextView;

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
	
	private Session firstSession;
		
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
		
		// get the current active user
		try {
			dbHandler.open();			
			user = dbHandler.getActiveUser();
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbHandler.close();			
		}
			

		// action bar
		final ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			
		if(user != null) {
			
			
			Log.e("Visus", "USER ID: " + userId);
			
			
			mainMenuPager = (ViewPager) findViewById(com.visus.R.id.main_menu_pager);
			mainMenuPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager(), 
					                                        user.getUserId(),						// user 
					                                        getSessionsCount(),						// total no. sessions
					                                        getLatestSessions(user.getUserId() ),	// latest sessions
					                                        getActivitiesCount(user.getUserId() ),	// no activities
					                                        getActivities(user.getUserId() ),		// activities
					                                        getFirstSession() );					// first session
			
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
			
						
			if(sessions.isEmpty()) {
				Log.e("Visus", "Sessions is empty");
			}
			else {
				Log.e("Visus", "Sessions is not empty");
				
				
				/*
				 * check whether the user has met their targets
				 */
				Target target = new Target(this, user.getUserId() );
				
				boolean targetDay = target.checkTargetDay();
				boolean targetMonth = target.checkTargetMonth();
				
				// if met
				if(targetDay == true) {
					// notify the user they have met their target...
					// but only display it for 1 hour
					// on the latest activities fragment
				}

				if(targetMonth == true) {
					// notify the user they have met their target...
					// but only display it for 1 hour
					// on the latest activities fragment
				}
							
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
	            
			case com.visus.R.id.new_session_menu:
				intent = new Intent(this, NewSession.class);
				intent.putExtra("ActiveUserId", userId);
				startActivity(intent);
				break;
				
			case com.visus.R.id.menu_sessions:
				intent = new Intent(this, Sessions.class);
				intent.putExtra("ActiveUserId", userId);
				startActivity(intent);
				break;
				
			case com.visus.R.id.menu_settings:
				intent = new Intent(this, Settings.class);
				intent.putExtra("ActiveUserId", userId);
				startActivity(intent);
				break;
				
			default:
				break;		
		}
		
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.activity_main, menu);		
		
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
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	private int getSessionsCount() throws SQLiteException {
		// return no. of sessions - total
		int totalSessions = 0;
		
		try {
			dbSession.open();
			totalSessions = dbSession.getSessionsCountAll(user.getUserId() );
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbSession.close();
		}
		
		if(totalSessions == 0) {
			totalSessions = 0;
			
			return totalSessions;
		}
		else {
			return totalSessions;			
		}		
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	private ArrayList<HashMap<String, String>> getLatestSessions(int userId) throws SQLiteException {
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<String> sessionTypes = new ArrayList<String>();
		ArrayList<HashMap<String, String>> latestSessions = new ArrayList<HashMap<String, String>>();
		
		Log.e("Visus", "USER ID: " + userId);
		
		try {
			dbSession.open();
			sessions = dbSession.getLatestSessions(userId);
		}
		finally {
			dbSession.close();				
		}
					
		if(sessions.isEmpty()) {
			HashMap<String, String> emptyMsg = new HashMap<String, String>();
			String msg = "None Created";
			emptyMsg.put(MainMenuListView.SESSION, msg);
			
			latestSessions.add(emptyMsg);			
			
			Log.e("Visus", "Sessions is empty");
		}
		else {
			Log.e("Visus", "Sessions is not empty");
			
			Log.e("Visus", "onCreate() - User ID is: " + userId);

			String [] data = { "Hello", "Jon", "Today", "Sunday" };
			
			int noItems = 0;
			
			// get the first session
			setFirstSession(sessions.get(0) );
			
			// output the first five results
			for(Session session : sessions) {
				if(noItems != 5) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(MainMenuListView.SESSION, session.getDay() + " " +
								    					  session.getDayNo() + " " +  
								    					  session.getMonth() + ", " +
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
		}
		
		return latestSessions;
	}
	
	/**
	 * 
	 * @param firstSession
	 */
	private void setFirstSession(Session firstSession) {
		Log.e("Visus", "First session: " + firstSession.getDay() + " "
										 + firstSession.getDayNo() + " "
										 + firstSession.getMonth() + ", "
										 + firstSession.getYear()
		     );
		
		this.firstSession = firstSession;
	}
	
	/**
	 * 
	 * @return
	 */
	private Session getFirstSession() {
		return firstSession;
	}
		
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	private int getActivitiesCount(int userId) throws SQLiteException {
		// no activities
		int noActivities = 0;		
						
		try {
			dbSession.open();
			noActivities = dbSession.getActivitiesCount(user.getUserId() );
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbSession.close();
		}
		
		return noActivities;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	private ArrayList<HashMap<String, String>> getActivities(int userId) throws SQLiteException {
		ArrayList<String> activitiesResult = new ArrayList<String>();
		SessionHandler dbSession = new SessionHandler(this); // getActivity() should do the trick!
		ArrayList<HashMap<String, String>> activities = new ArrayList<HashMap<String, String>>();
		
		Log.e("Visus", "ActivitiesFragment: " + userId);
		
		try {
			dbSession.open();
			activitiesResult = dbSession.getActivities(userId);
		}
		finally {
			dbSession.close();
		}
		
		if(activitiesResult.isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(MainMenuListView.SESSION, "None Created");			
			
			activities.add(map);
		}
		else {
			for(String activity : activitiesResult) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(MainMenuListView.SESSION, activity);			
				
				activities.add(map);
			}
		}
		
		return activities;
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
	
	
	/*************************************************************************
	 * 
	 * 						Main Menu Pager Adapter
	 * 
	 */
	public static final class MainMenuPagerAdapter extends FragmentPagerAdapter {

		private static final int NO_FRAGMENTS = 2;
		private int userId;
		private int totalSessions;
		private ArrayList<HashMap<String, String>> latestSessions;
		private int noActivities;
		private ArrayList<HashMap<String, String>> activities;
		private Session firstSession;
		
		public MainMenuPagerAdapter(FragmentManager fm,
									int userId,
									int totalSessions,
									ArrayList<HashMap<String, String>> latestSessions,
									int noActivities,
									ArrayList<HashMap<String, String>> activities,
									Session firstSession) {
			super(fm);
			this.userId = userId;
			this.totalSessions = totalSessions;
			this.latestSessions = latestSessions;
			this.noActivities = noActivities;
			this.activities = activities;
			this.firstSession = firstSession;
		}

		/**
		 * Displays the required fragment view
		 */
		@Override
		public Fragment getItem(int item) {
			switch(item) {
				case 0:
					// display latest sessions made
					return new LatestActivityFragment(userId,
													  totalSessions, 
													  latestSessions, 
													  firstSession);
				case 1:
					// display session activity types (no. of for each type)
					return new ActivitiesFragment(userId,
												  noActivities, 
												  activities, 
												  firstSession);
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