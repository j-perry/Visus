package com.visus.ui.settings.fragments;

import com.visus.database.SessionHandler;
import com.visus.database.UserHandler;
import com.visus.entities.User;
import com.visus.main.MainActivity;
import com.visus.main.Settings;

import android.R;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Display's content used to manipulate the UX
 * @author Jonathan Perry
 */
public class GeneralFragment extends Fragment implements OnClickListener {

	private Integer userId;
	private SessionHandler dbSession;
	private UserHandler dbUser;
	private User user;
	
	// 'personal'
	private EditText personalName;
	private Spinner  personalGender;
	private EditText personalAge;
		
	// 'sessions'
	private EditText historyTargetDay;
	private EditText historyTargetMonth;
		
	private Button	 resetMonth,
					 resetYear,
					 resetAll;
	
	// 'save'
	private Button	 save;
	private static FragmentManager fragmentManagerSupport; 
		
	public GeneralFragment() {
		super();
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
			this.userId = user.getUserId();
			Log.e("Visus", "GeneralFragment " + userId);
			Log.e("Visus", "GeneralFragment (Target Day): " + user.getTargetDay() );
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
		}
				
		// populate the setting components
//		personalName = (EditText) rootView.findViewById(com.visus.R.id.settings_personal_name);
//		personalGender = (Spinner) rootView.findViewById(com.visus.R.id.settings_personal_gender);
//		personalAge = (EditText) rootView.findViewById(com.visus.R.id.settings_personal_age);
				
		historyTargetDay = (EditText) rootView.findViewById(com.visus.R.id.settings_history_target_day);
		historyTargetMonth = (EditText) rootView.findViewById(com.visus.R.id.settings_history_target_month);
				
		resetMonth = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_month);
		resetYear = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_year);
		resetAll = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_all);
				
		save = (Button) rootView.findViewById(com.visus.R.id.settings_save_all);
				
		// set event handlers for the reset and save all buttons
		resetMonth.setOnClickListener(this);
		resetYear.setOnClickListener(this);
		resetAll.setOnClickListener(this);
		save.setOnClickListener(this);
				
		/*
		* determine whether to display either of the reset buttons, based on existing Sessions data
		*/
		int itemsMonth = 0;
		int itemsYear = 0;
		int itemsAll = 0;
				
		try {
			dbSession.open();
					
			// month btn
			itemsMonth = dbSession.getSessionsCountThisMonth(user.getUserId() );
					
			// year btn
			itemsYear = dbSession.getSessionsCountThisYear(user.getUserId() );
					
			// all sessions btn
			itemsAll = dbSession.getSessionsCountAll(user.getUserId() );
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
		
//		final String male = "Male";
//		final String female = "Female";
		
//		String [] genders = { male, female };
						
		// spinner value
//		ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),
//							         							      com.visus.R.layout.settings_spinner_gender_layout, 
//																	  genders);
		
//		genderAdapter.setDropDownViewResource(com.visus.R.layout.settings_spinner_gender_list_layout );
//		personalGender.setAdapter(genderAdapter);	
		
//		genderAdapter = (ArrayAdapter) personalGender.getAdapter();
				  		
		// NB: If they are null, it will simply display whatever is initialised in the hint property
//		personalName.setText(user.getFirstname() );
				
//		int valuePosition = genderAdapter.getPosition(user.getGender());
//		personalGender.setSelection(valuePosition);
				
//		personalAge.setText(String.valueOf(user.getAge()) );
						
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
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case com.visus.R.id.settings_history_reset_month:
				onResetMonth(view);
				break;
			case com.visus.R.id.settings_history_reset_year:
				onResetYear(view);
				break;
			case com.visus.R.id.settings_history_reset_all:
				onResetAll(view);
				break;
			case com.visus.R.id.settings_save_all:
				onSave(view);
			default:
				break;
		}
	}
	
	/**
	 * Reset all sessions made this month
	 * @param view
	 */
	public void onResetMonth(View view) {
		try {
			dbSession.open();
			dbSession.deleteSessionsThisMonth(user.getUserId() );
			
			// hide the reset buttons
			resetMonth.setVisibility(View.GONE);
			resetYear.setVisibility(View.GONE);
			resetAll.setVisibility(View.GONE);
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
			
			// hide the reset buttons
			resetMonth.setVisibility(View.GONE);
			resetYear.setVisibility(View.GONE);
			resetAll.setVisibility(View.GONE);
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
			
			// hide the reset buttons
			resetMonth.setVisibility(View.GONE);
			resetYear.setVisibility(View.GONE);
			resetAll.setVisibility(View.GONE);
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
		
		user.setUserId(this.userId);
				
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
		 * 	Update (save) user details
		 */
		Log.e("Visus", "Update Profile (Daily Target):" + user.getTargetDay() );
		Log.e("Visus", "Update Profile (Monthly Target):" + user.getTargetMonth() );
		
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
