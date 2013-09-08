package com.visus.main;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.main.Sessions.SessionsPagerAdapter;


/**
 * Enables the user to configure the apps settings
 * @author Jonathan Perry
 *
 */
public class Settings extends Activity {

	private User user = null;
	private UserHandler dbUser;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		Bundle b = getIntent().getExtras();
		int activeUserId = b.getInt("ActiveUserId");
		
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
				
		dbUser = new UserHandler(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_view_settings, menu);
		return true;
	}
}