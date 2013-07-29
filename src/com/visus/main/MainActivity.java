package com.visus.main;

import com.visus.R;
import com.visus.database.UserHandler;
import com.visus.entities.User;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.*;

public class MainActivity extends Activity {
	
	private User user = null;
	private UserHandler dbUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbUser = new UserHandler(this);
		
		ActionBar ab = getActionBar();
		
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Log.e("Visus", "onResume()");
		user = dbUser.getActiveUser();
		
		if(user == null) {
			Log.e("Visus", "No user active");
			Intent intent = new Intent(MainActivity.this, NewUser.class);
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
		Intent intent = new Intent(MainActivity.this, PrevSessions.class);
		startActivity(intent);
	}
	
	/**
	 * view app settings
	 * @param view
	 */
	public void viewSettings(View view) {
		Intent intent = new Intent(MainActivity.this, ViewSettings.class);
		startActivity(intent);
	}

}
