package com.visus.ui.settings.fragments;

import com.visus.database.SessionHandler;
import com.visus.database.UserHandler;
import com.visus.entities.User;
import com.visus.main.MainActivity;
import com.visus.main.Settings;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class GeneralFragment extends Fragment {

	private int userId;
	private SessionHandler dbSession;
	private UserHandler dbUser;
	private User user;
	
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
	
	public GeneralFragment() {
		super();
	}
	
	public GeneralFragment(int userId) {
		this.userId = userId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_settings_general, container, false);
		dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		dbUser = new UserHandler(getActivity() );
		
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
				personalName = (EditText) rootView.findViewById(com.visus.R.id.settings_personal_name);
				personalGender = (Spinner) rootView.findViewById(com.visus.R.id.settings_personal_gender);
				personalAge = (EditText) rootView.findViewById(com.visus.R.id.settings_personal_age);
				
				historyTargetDay = (EditText) rootView.findViewById(com.visus.R.id.settings_history_target_day);
				historyTargetMonth = (EditText) rootView.findViewById(com.visus.R.id.settings_history_target_month);
				
				resetMonth = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_month);
				resetYear = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_year);
				resetAll = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_all);
				
				/*
				 * determine whether to display either of the reset buttons, based on existing Sessions data
				 */
				int itemsMonth = 0;
				int itemsYear = 0;
				int itemsAll = 0;
				
				try {
					dbSession.open();
					
					// month btn
					itemsMonth = dbSession.getSessionsCountThisMonth(userId);
					
					// year btn
					itemsYear = dbSession.getSessionsCountThisMonth(userId);
					
					// all sessions btn
					itemsAll = dbSession.getSessionsCountAll(userId);
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
		
		
		return rootView;
	}
	
	/**
	 * Reset all sessions made this month
	 * @param view
	 */
	public void onResetMonth(View view) {
		try {
			dbSession.open();
			dbSession.deleteSessionsThisMonth(userId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Deleted";
			Toast.makeText(getActivity(), msg, LENGTH).show();
			
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
			dbSession.deleteSessionsThisYear(userId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);			
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Deleted";
			Toast.makeText(getActivity(), msg, LENGTH).show();
			
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
			dbSession.deleteAllSessions(userId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);	
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Deleted";
			Toast.makeText(getActivity(), msg, LENGTH).show();
			
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
		user.setUserId(userId);
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
			Toast.makeText(getActivity(), msg, LENGTH).show();

			// close database connection
			dbUser.close();
			
			// return to the root menu
			Intent intent = new Intent(getActivity(), MainActivity.class);
			startActivity(intent);
		}
	}

}
