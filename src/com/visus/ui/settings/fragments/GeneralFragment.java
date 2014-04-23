package com.visus.ui.settings.fragments;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.database.SessionRecordsHandler;
import com.visus.database.UserHandler;
import com.visus.entities.User;
import com.visus.main.MainActivity;
import com.visus.main.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Display's content used to manipulate the UX
 * @author Jonathan Perry
 */
public class GeneralFragment extends Fragment implements OnClickListener {

	private Integer userId;
	private SessionHandler dbSession;
	private SessionRecordsHandler srHandler;
	private UserHandler dbUser;
	private User user;
			
	// 'sessions'
	private EditText historyTargetDay;
	private EditText historyTargetMonth;
		
	private Button	 resetMonth,
					 resetYear,
					 resetAll;
	
	// 'save'
	private Button	 save;
	
	private int prevTargetDay,
				prevTargetMonth;
		
	public GeneralFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_settings_general, container, false);
		dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		dbUser = new UserHandler(getActivity() );
		srHandler = new SessionRecordsHandler(getActivity() );
								
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
				
		historyTargetDay = (EditText) rootView.findViewById(com.visus.R.id.settings_history_target_day);
		historyTargetMonth = (EditText) rootView.findViewById(com.visus.R.id.settings_history_target_month);
				
		resetAll = (Button) rootView.findViewById(com.visus.R.id.settings_history_reset_all);
				
		save = (Button) rootView.findViewById(com.visus.R.id.settings_save_all);
				
		// set event handlers for the reset and save all buttons
		resetAll.setOnClickListener(this);
		save.setOnClickListener(this);
				
		// determine whether to display either of the reset buttons, based on existing Sessions data
		int itemsAll = 0;
				
		try {
			dbSession.open();					
			
			// all sessions btn
			itemsAll = dbSession.getSessionsCountAll(user.getUserId() );
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbSession.close();
		}
				
		// all
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
			this.prevTargetDay = user.getTargetDay();
		}
				
		// target month
		if(user.getTargetMonth() == 0) {
			historyTargetMonth.setText("");
		}
		else {
			historyTargetMonth.setText(String.valueOf(user.getTargetMonth()) );
			this.prevTargetMonth = user.getTargetMonth();
		}
				
		return rootView;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
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
	 * Reset all sessions made
	 * @param view
	 */
	public void onResetAll(View view) {
		try {
			dbSession.open();
			dbSession.deleteAllSessions(userId);	
			
			srHandler.open();
			srHandler.deleteAllActivities(userId);
			
			// hide the reset buttons
			resetAll.setVisibility(View.GONE);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);	
		}
		finally {
			final int LENGTH = 600; // ms
			String msg = "Sessions Deleted";
			Toast.makeText(getActivity(), msg, LENGTH).show();
			
			dbSession.close();
			srHandler.close();
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

		
		// 	Update (save) user details
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		Log.e("Visus", "back home");
		
		switch(item.getItemId()) {
			// app logo
			case android.R.id.home:
				Log.e("Visus", "back home");
				
				final Dialog dialog = new Dialog(getActivity() );
				Button ok = new Button(getActivity() );
				Button cancel = new Button(getActivity() );
				final Context context = getActivity();
				
				// if the new value is different from the previous, ask the user whether they want to
				// save the previous target for each weekday
				if(this.prevTargetDay < Integer.parseInt(historyTargetDay.getText().toString() )) {
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCanceledOnTouchOutside(false);
					dialog.setContentView(R.layout.alert_dialog_save_targets_day);
					
					// buttons
					ok = (Button) dialog.findViewById(R.id.alert_dialog_save_targets_btn_ok);
					cancel = (Button) dialog.findViewById(R.id.alert_dialog_save_targets_btn_cancel);
					
					// ok
					ok.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO
							onSave(getView() );
							
							Intent back = new Intent(getActivity(), MainActivity.class);
							back.putExtra("ActiveUserId", user.getUserId());
							context.startActivity(back);
						}
					});
					
					// cancel
					cancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
				            dialog.dismiss();
						}
					});
					
					dialog.show();
					
				}
				// do so likewise for the monthly target
				else if(this.prevTargetMonth != Integer.parseInt(historyTargetMonth.getText().toString() )) {
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCanceledOnTouchOutside(false);
					dialog.setContentView(R.layout.alert_dialog_save_targets_month);
					
					// buttons
					ok = (Button) dialog.findViewById(R.id.alert_dialog_save_targets_btn_ok);
					cancel = (Button) dialog.findViewById(R.id.alert_dialog_save_targets_btn_cancel);
					
					// ok
					ok.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO
							onSave(getView() );
							
							Intent back = new Intent(getActivity(), MainActivity.class);
							back.putExtra("ActiveUserId", user.getUserId());
							context.startActivity(back);
						}
					});
					
					// cancel
					cancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
				            dialog.dismiss();
						}
					});
					
					dialog.show();
				}			
				
	            
	            break;
	        default:
	        	break;
		}
		return true;
	}	
	
}