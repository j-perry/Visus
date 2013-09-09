package com.visus.main;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.*;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.entities.sessions.Session;
import com.visus.main.Sessions.SessionsPagerAdapter;


/**
 * Enables the user to configure the apps settings
 * @author Jonathan Perry
 *
 */
public class Settings extends Activity {

	private int activeUserId;
	
	private User user = null;
	private Session session;
	private UserHandler dbUser;
	private SessionHandler dbSession;
	
	// personal
	private EditText personalName;
	private Spinner  personalGender;
	private EditText personalAge;
	
	// history
	private EditText historyTargetDay;
	private EditText historyTargetMonth;
	
	private Button	 resetMonth,
					 resetYear,
					 resetAll;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		Bundle b = getIntent().getExtras();
		activeUserId = b.getInt("ActiveUserId");
		
		user = new User();
		session = new Session();
		
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
				
		dbUser = new UserHandler(this);
		dbSession = new SessionHandler(this);
		
		// user
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
			
		
		// populate the setting components
		personalName = (EditText) findViewById(com.visus.R.id.settings_personal_name);
		personalGender = (Spinner) findViewById(com.visus.R.id.settings_personal_gender);
		personalAge = (EditText) findViewById(com.visus.R.id.settings_personal_age);
		
		historyTargetDay = (EditText) findViewById(com.visus.R.id.settings_history_target_day);
		historyTargetMonth = (EditText) findViewById(com.visus.R.id.settings_history_target_month);
		
		resetMonth = (Button) findViewById(com.visus.R.id.settings_history_reset_month);
		resetYear = (Button) findViewById(com.visus.R.id.settings_history_reset_year);
		resetAll = (Button) findViewById(com.visus.R.id.settings_history_reset_all);
		
		/*
		 * determine whether to display either of the reset buttons, based on existing Sessions data
		 */
		int itemsMonth = 0;
		int itemsYear = 0;
		int itemsAll = 0;
		
		try {
			dbSession.open();
			
			// month btn
			itemsMonth = dbSession.getSessionsCountThisMonth(activeUserId);
			itemsYear = dbSession.getSessionsCountThisMonth(activeUserId);
			itemsAll = dbSession.getSessionsCountAll(activeUserId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbSession.close();
		}
		
		// month
		if(itemsMonth == 0) {
			resetMonth.setVisibility(View.GONE);
		}
		else {
			resetMonth.setVisibility(View.VISIBLE);			
		}
		
		// year
		if(itemsYear == 0) {
			resetYear.setVisibility(View.GONE);
		}
		else {
			resetYear.setVisibility(View.VISIBLE);			
		}
		
		// year
		if(itemsAll == 0) {
			resetAll.setVisibility(View.GONE);
		}
		else {
			resetAll.setVisibility(View.VISIBLE);			
		}
		
		
		// spinner value
		ArrayAdapter genderAdapter = (ArrayAdapter) personalGender.getAdapter();
		
		// NB: If they are null, it will simply display whatever is initialised in the hint property
		personalName.setText(user.getFirstname() );
		
		int valuePosition = genderAdapter.getPosition(user.getGender());
		personalGender.setSelection(valuePosition);
		
		personalAge.setText(String.valueOf(user.getAge()) );
				
		// target day
		if(user.getTargetDay() == 0) {
			historyTargetDay.setText("");
		}
		else {
			historyTargetDay.setText(String.valueOf(user.getTargetDay()) );
		}
		
		// target month
		if(user.getTargetMonth() != 0) {
			historyTargetMonth.setText(String.valueOf(user.getTargetMonth()) );
		}
		else {
			historyTargetMonth.setText("");
		}
		
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
	
	/**
	 * Reset all sessions made this month
	 * @param view
	 */
	public void onResetMonth(View view) {
		try {
			dbSession.open();
			dbSession.deleteSessionsThisMonth(activeUserId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Deleted";
			Toast.makeText(this, msg, LENGTH).show();
			
			dbSession.close();
		}
	}
	
	/**
	 * Reset all sessions made this year
	 * @param view
	 */
	public void onResetYear(View view) {
		try {
			dbSession.open();
			dbSession.deleteSessionsThisYear(activeUserId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);			
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Deleted";
			Toast.makeText(this, msg, LENGTH).show();
			
			dbSession.close();
		}
	}
	
	/**
	 * Reset all sessions made
	 * @param view
	 */
	public void onResetAll(View view) {
		try {
			dbSession.open();
			dbSession.deleteAllSessions(activeUserId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);	
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Deleted";
			Toast.makeText(this, msg, LENGTH).show();
			
			dbSession.close();
		}
	}
	
	/**
	 * Saves the user's modified - or created - settings
	 * @param view
	 */
	public void onSave(View view) {
		user = new User();
		
		/*
		 * 'Personal'
		 */
		user.setUserId(activeUserId);
		user.setFirstname(personalName.getText().toString() );				// first name
		user.setGender(personalGender.getSelectedItem().toString() ) ;		// gender
		user.setAge(Integer.parseInt(personalAge.getText().toString()) );	// age
				
		
		/*
		 * 'Sessions'
		 */
		// target year
		if(historyTargetDay.getText().toString().isEmpty() ) {
			user.setTargetDay(0);
		}
		else {
			user.setTargetDay(Integer.parseInt(historyTargetDay.getText().toString() ));			
		}
		
		// target month
		if(historyTargetMonth.getText().toString().isEmpty() ) {
			user.setTargetMonth(0);
		}
		else {
			user.setTargetMonth(Integer.parseInt(historyTargetMonth.getText().toString() ));			
		}

		
		/*
		 * update (save) user details
		 */
		try {
			dbUser.open();
			dbUser.updateUser(user);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			// inform the user their profile has been saved (updated)
			final int LENGTH = 600; // ms
			String msg = "Profile Updated";
			Toast.makeText(this, msg, LENGTH).show();

			// close database connection
			dbUser.close();
			
			// return to the root menu
			Intent intent = new Intent(Settings.this, MainActivity.class);
			startActivity(intent);
		}
	}
		
}