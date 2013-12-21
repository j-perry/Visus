package com.visus.main;

// core apis
import java.util.ArrayList;
import java.util.HashMap;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
// android apis
import android.os.Bundle;
import android.app.*;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuListView;
import com.visus.ui.main.fragments.ActivitiesFragment;
import com.visus.ui.main.fragments.LatestActivityFragment;

/**
 * Main entry point of the app
 * @author Jonathan Perry
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private User user; // = null;
	private UserHandler dbUser;
	private SessionHandler dbSession;
	private static int userId;
	
	private Session firstSession;
			
	private Context context = this;
	private AlertDialog alertDialog;
	
	private ViewPager mainMenuPager;
	private MainMenuPagerAdapter mainMenuPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		Log.e("Visus", "MainActivity - onCreate()");
						
		dbUser = new UserHandler(this);
		dbSession = new SessionHandler(this);
		
		user = new User();
		
		try {
			dbUser.open();
			
			if(dbUser.getActiveUser() == null) {
				dbUser.open();
				user.setTargetDay(0);
				user.setTargetMonth(0);
				dbUser.add(user);
			}
			else {
				dbUser.open();
				user = dbUser.getActiveUser();
			}
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
		}
		
		setContentView(R.layout.activity_main);
		
		// action bar
		final ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			
		if(user != null) {				
			Log.e("Visus", "USER ID: " + user.getUserId() );		
			int totalSessions = 0;
			int noActivities = 0;	
			ArrayList<HashMap<String, String>> latestSessions = new ArrayList<HashMap<String, String>>();	
			ArrayList<HashMap<String, String>> activities = new ArrayList<HashMap<String, String>>();	
			Session firstSession = new Session();
			
			try {
				dbSession.open();
				totalSessions = dbSession.getSessionsCountAll(user.getUserId() );
				latestSessions = dbSession.getLatestSessions(user);
				activities = dbSession.getActivities(user);
				noActivities = dbSession.getActivitiesCount(user.getUserId() );
				firstSession = dbSession.getFirstSession();
			}
			catch(SQLiteException e) {
				Log.e("Visus", "SQL Error");
			}
			finally {
				dbSession.close();
			}
			
			mainMenuPager = (ViewPager) findViewById(com.visus.R.id.main_menu_pager);
			mainMenuPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager(), 
					                                        user.getUserId(),						// user 
					                                        totalSessions,							// total no. sessions
					                                        latestSessions,							// latest sessions
					                                        noActivities,							// no activities
					                                        activities,								// activities
					                                        firstSession); 							// first session
						
			mainMenuPager.setAdapter(mainMenuPagerAdapter);
						
			ArrayAdapter<CharSequence> arAdapter = ArrayAdapter.createFromResource(this, R.array.menu, R.layout.action_bar_list);
				

			final Dialog dialog = new Dialog(context);
			Button cancel = new Button(context);
			Button ok = new Button(context);
			
			/*************************************************************************************
			 * 		If no user target have been created, ask the user if they would like to
			 */
			if(user.getTargetDay() == 0 && user.getTargetMonth() == 0) {
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setContentView(R.layout.alert_dialog_set_targets);
				
				// buttons
				cancel = (Button) dialog.findViewById(R.id.alert_dialog_user_targets_btn_cancel);
				ok = (Button) dialog.findViewById(R.id.alert_dialog_user_targets_btn_ok);
				
				ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), Settings.class);
						intent.putExtra("ActiveUserId", user.getUserId());
						context.startActivity(intent);
					}
				});
				
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
			            dialog.dismiss();
					}
				});
				
				dialog.show();
			}
			
			
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
		
		try {
			dbUser.open();
			user = dbUser.getActiveUser();
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
		}	
		
		if(user == null) {
			Log.e("Visus", "No user active");
		}
		else {
			userId = user.getUserId();
			Log.e("Visus", "USER ID: " + userId);
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
	
	/*************************************************************************
	 * 
	 * 								Event Handlers
	 */
			
	/**
	 * Creates a new session
	 * @param view
	 */
	public void newSession(View view) {
		Intent intent = new Intent(MainActivity.this, NewSession.class);
		intent.putExtra("ActiveUserId", user.getUserId());
		startActivity(intent);
	}
	
	/**
	 * View previous sessions
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
		
		private boolean dailyTarget;
		private boolean monthlyTarget;
		
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
					LatestActivityFragment latestActivityFragment = new LatestActivityFragment();
					latestActivityFragment.addContext(userId, 
													  totalSessions, 
													  latestSessions, 
													  firstSession);
					return latestActivityFragment;
				case 1:
					// display session activity types (no. of for each type)
					ActivitiesFragment activitiesFragment = new ActivitiesFragment();
					activitiesFragment.addContext(userId,
												  noActivities, 
												  activities, 
												  firstSession);
					return activitiesFragment;
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