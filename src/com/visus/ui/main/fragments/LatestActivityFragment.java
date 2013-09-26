package com.visus.ui.main.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.database.SessionHandler;
import com.visus.database.UserHandler;
import com.visus.entities.User;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuAdapter;
import com.visus.ui.MainMenuListView;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class LatestActivityFragment extends Fragment {
	
	private int userId;
	private int totalSessions;
	private ArrayList<HashMap<String, String>> latestSessions;
	private Session firstSession;
	private SessionHandler dbSession;
	private UserHandler dbUser;
	
	private ListView list;
	private MainMenuAdapter adapter;
	
	public LatestActivityFragment() {
		super();
	}
		
	public LatestActivityFragment(int userId, 
								  int totalSessions, 
								  ArrayList<HashMap<String, String>> latestSessions,
								  Session firstSession) {
		this.userId = userId;
		this.totalSessions = totalSessions;
		this.latestSessions = latestSessions;
		this.firstSession = firstSession;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_main_menu_latest_activity, container, false);
		dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		dbUser = new UserHandler(getActivity() );
		
		/*
		 * No. sessions
		 */
		TextView txtVwTotalSessions = (TextView) rootView.findViewById(com.visus.R.id.main_menu_latest_activities_no_sessions_total);
		
//		totalSessions = 0; // TODO temp
		
		if(totalSessions == 0) {
			txtVwTotalSessions.setText(String.valueOf(0) + " Sessions");
		}
		else if(totalSessions == 1) {
			txtVwTotalSessions.setText(String.valueOf(totalSessions) + " Session");			
		}
		else {
			txtVwTotalSessions.setText(String.valueOf(totalSessions) + " Sessions");
		}	
		
		
		/*
		 * Get the date of the first session
		 */
		TextView txtVwFirstSession = (TextView) rootView.findViewById(com.visus.R.id.main_menu_latest_activities_no_sessions_date);
		StringBuilder strFirstSession = new StringBuilder();
				
		if(firstSession == null) {
			strFirstSession.append("Created");
		}
		else {
			strFirstSession.append("Created since ");

			// day
			strFirstSession.append(firstSession.getDay() + " ");
			
			// day no (dd)
			strFirstSession.append(String.valueOf(firstSession.getDayNo()) );
			strFirstSession.append(" ");
			
			// month (MMM)
			strFirstSession.append(firstSession.getMonth() );
			strFirstSession.append(", ");
			
			// year (YYYY)
			strFirstSession.append(firstSession.getYear() );
		}
	
		// display
		txtVwFirstSession.setText(strFirstSession.toString() );
		
		
		
		/*********************************************************************
		 * 		Check whether the user has met their daily usage target
		 */
		boolean dailyTargetMet = checkUserTargetToday();
		
		if(dailyTargetMet == true) {
			// display message (not just a Log file message!)
			Log.e("Visus", "Daily target met");
		}
		else {
			// don't do anything additional - just display what is seen normally
			Log.e("Visus", "Daily target not met");
		}
		
		
		
		/*********************************************************************
		 * 		Check whether the user has met their monthly usage target
		 */
		boolean monthlyTargetMet = checkUserTargetMonth();
		
		if(monthlyTargetMet == true) {
			// display message (not just a Log file message!)
			Log.e("Visus", "Monthly target met");
		}
		else {
			// don't do anything additional - just display what is seen normally
			Log.e("Visus", "Monthly target not met");
		}
		
		
		
		/*************************************************
		 * 		Display the user's latest sessions
		 */
		list = (ListView) rootView.findViewById(com.visus.R.id.overview_sessions_adapter);
		adapter = new MainMenuAdapter(getActivity(), latestSessions);
		
		list.setAdapter(adapter);
				
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}
			
	/**
	 * Method checks whether the user has met their monthly target
	 * @return Whether the user's target has been met
	 */
	private boolean checkUserTargetToday() {
		boolean targetMet = false;
		float dailyTarget = 0.0f;
		float accumulatedDurationToday = 0.0f;
		
		try {
			// get user's daily target
			dbUser.open();
			User user = dbUser.getActiveUser();
			dailyTarget = user.getTargetDay();
									
			// get accumulated time made today
			dbSession.open();
			accumulatedDurationToday = dbSession.getTimeAccumulatedToday(userId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
			dbSession.close();
		}
		
		Log.e("Visus", "accumulatedDurationToday = " + accumulatedDurationToday);
		Log.e("Visus", "dailyTarget = " + dailyTarget);
		
		// if daily target is empty
		if(dailyTarget != 0.0f) {
			Log.e("Visus", "dailyTarget is not empty");
			
			if(accumulatedDurationToday >= dailyTarget) {
				targetMet = true;
			}
			else {
				targetMet = false;
			}
		}
		else {
			targetMet = false;
		}
		
		return targetMet;
	}
	
	/**
	 * Method checks whether the user has met their monthly target
	 * @return Whether the user's target has been met
	 */
	private boolean checkUserTargetMonth() { 
		boolean targetMet = false;
		float monthlyTarget = 0.0f;
		float accumulatedDurationMonth = 0.0f;
		
		try {
			// get user's daily target
			dbUser.open();
			User user = dbUser.getActiveUser();
			monthlyTarget = user.getTargetMonth();
			
			// get accumulated time made today
			dbSession.open();
			accumulatedDurationMonth = dbSession.getTimeAccumulatedThisMonth(userId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
			dbSession.close();
		}
		
		Log.e("Visus", "accumulatedDurationMonth = " + accumulatedDurationMonth);
		Log.e("Visus", "monthlyTarget = " + monthlyTarget);
		
		// if daily target is empty
		if(monthlyTarget != 0.0f) {
			Log.e("Visus", "monthlyTarget is not empty");
					
			if(accumulatedDurationMonth >= monthlyTarget) {
				targetMet = true;
			}
			else {
				targetMet = false;
			}
		}
		else {
			targetMet = false;
		}
		
		return targetMet;
	}
	
}
