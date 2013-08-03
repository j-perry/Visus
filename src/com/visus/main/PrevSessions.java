package com.visus.main;

import com.visus.R;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;

/**
 * Enables the user to view previous sessions
 * @author Jonathan Perry
 *
 */
public class PrevSessions extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prev_sessions);
		
		ActionBar ab = getActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_prev_sessions, menu);
		return true;
	}

}
