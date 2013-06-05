package project.visus.main;

import project.visus.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// if no user exists display the new registrant view
		// setContentView(R.layout.activity_new_user);

		setContentView(R.layout.activity_main);
	}

	/**
	 * Creates a new options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Creates a new session view
	 * @param view
	 */
	public void newSession(View view) {
		Intent intent = new Intent(MainActivity.this, NewSession.class);
		startActivity(intent);
	}
	
	/**
	 * Creates a new previous sessions view
	 * @param view
	 */
	public void prevSessions(View view) {
		Intent intent = new Intent(MainActivity.this, PrevSessions.class);
		startActivity(intent);
	}
	
	/**
	 * Creates a new settings view
	 * @param view
	 */
	public void viewSettings(View view) {
		Intent intent = new Intent(MainActivity.this, ViewSettings.class);
		startActivity(intent);
	}

}
