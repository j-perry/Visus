package com.visus.main;

import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;

import android.os.Bundle;
import android.app.*;
import android.content.Intent;
import android.util.Log;
import android.view.*;

/**
 * Main entry point of the app
 * @author Jonathan Perry
 *
 */
public class MainActivity extends Activity {
	
	private User user = null;
	private UserHandler dbHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbHandler = new UserHandler(this);
				
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
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
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
	
}
