package com.visus.main;

// core apis
import java.util.ArrayList;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.sessions.*;
import com.visus.ui.sessions.fragments.*;

/**
 * Enables the user to view previous sessions
 * @author Jonathan Perry
 *
 */
public class Sessions extends FragmentActivity implements ActionBar.TabListener {

	private int activeUserId;
	
	private ViewPager sessionsPager;
	private SessionsPagerAdapter sessionsPagerAdapter;		
	private Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SessionHandler dbSessions = new SessionHandler(this);
		ArrayList<Session> allSessions = new ArrayList<Session>();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
				
		// clear the notification
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		
		Log.e("Visus", "Session onCreate()");		
		
		// get user id
		Bundle bundle = getIntent().getExtras();
		activeUserId = bundle.getInt("ActiveUserId");
		
		boolean displayNotification = bundle.getBoolean("DisplayNotification");
		
		if(displayNotification == true) {
	        // display a notification to the user to take a break (3 - 5 minutes approx)
	        Notification.Builder notBuilder = new Notification.Builder(context);
			NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			notBuilder.setSmallIcon(com.visus.R.drawable.ic_launcher_4);
			notBuilder.setContentTitle(getResources().getString(com.visus.R.string.app_name) );
			notBuilder.setContentText(getResources().getString(com.visus.R.string.notification_new_session_finished) );
			
			notBuilder.setAutoCancel(true);
			notManager.notify(0, notBuilder.build() );
		}

		Log.e("Visus", "USER ID: " + activeUserId);
		
		try {
			dbSessions.open();
			allSessions = dbSessions.getSessionsThisYear(activeUserId);
			Log.e("Visus", "No items: " + allSessions.size());
		} catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		} finally {
			dbSessions.close();
		}
		
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		final Dialog dialog = new Dialog(context);
		Button cancel = new Button(context);
		Button ok = new Button(context);		
		
		// determine whether any sessions exist, if not display an AlertDialog asking the user if they wish
		// to create a new session. Improves the user experience.
		if(allSessions.isEmpty()) {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(R.layout.alert_dialog_previous_sessions);
			
			// buttons
			cancel = (Button) dialog.findViewById(R.id.alert_dialog_previous_sessions_btn_cancel);
			ok = (Button) dialog.findViewById(R.id.alert_dialog_previous_sessions_btn_ok);
			
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), NewSession.class);
					intent.putExtra("ActiveUserId", activeUserId);
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
		
		sessionsPager = (ViewPager) findViewById(com.visus.R.id.sessions_pager);
		sessionsPagerAdapter = new SessionsPagerAdapter(getSupportFragmentManager(), activeUserId);
			
		// initialise the page view adapter
		sessionsPager.setAdapter(sessionsPagerAdapter);
		
		// TODO - added 19/12/2013
		ArrayAdapter<CharSequence> arAdapter = ArrayAdapter.createFromResource(this, R.array.sessions_menu, R.layout.action_bar_list);
				
		/**
		 * The following two methods are crucial. Do not delete them.
		 */
		ab.setListNavigationCallbacks(arAdapter, new OnNavigationListener() {
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				sessionsPager.setCurrentItem(itemPosition);
				return true;
			}
		});
		
		sessionsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {				
			@Override
			public void onPageSelected(int position) {
				ab.setSelectedNavigationItem(position);
			}				
		});
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
				Log.e("Visus", "HOME BUTTON PRESSED");
				intent = new Intent(this, MainActivity.class);
				startActivity(intent);
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
		// TODO Auto-generated method stub
	}
	
	/**
	 * TODO - body commented out 19/12/2013
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Pager adapter launches and displays new fragments according to the tab selected
	 * @author Jonathan Perry
	 */
	public static class SessionsPagerAdapter extends FragmentPagerAdapter {
		
		// no of pages
		private static final int NO_FRAGMENTS = 4; 
		private Bundle bundle;
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
					FragmentToday fragmentToday = new FragmentToday();
					bundle = new Bundle();
					bundle.putInt("ActiveUserId", userId);
					fragmentToday.setArguments(bundle);
					return fragmentToday;
				case 1:
					// ... from this week
					FragmentThisWeek fragmentThisWeek = new FragmentThisWeek();
					bundle = new Bundle();
					bundle.putInt("ActiveUserId", userId);
					fragmentThisWeek.setArguments(bundle);
					return fragmentThisWeek;
				case 2:
					// ... from this month
					FragmentThisMonth fragmentThisMonth = new FragmentThisMonth();
					bundle = new Bundle();
					bundle.putInt("ActiveUserId", userId);
					fragmentThisMonth.setArguments(bundle);
					return fragmentThisMonth;
				case 3: 
					// ... from this year
					FragmentThisYear fragmentThisYear = new FragmentThisYear();
					bundle = new Bundle();
					bundle.putInt("ActiveUserId", userId);
					fragmentThisYear.setArguments(bundle);
					return fragmentThisYear;
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
