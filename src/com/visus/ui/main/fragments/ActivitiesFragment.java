package com.visus.ui.main.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.database.SessionHandler;
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

public class ActivitiesFragment extends Fragment {
	
	private int userId;
	private int noActivities;
	private Session firstSession;
	private ArrayList<HashMap<String, String>> activities;
	
	private ListView list;
	private MainMenuAdapter adapter;
	
	public ActivitiesFragment() {
		super();
	}
	
	public ActivitiesFragment(int userId, 
							  int noActivities, 
							  ArrayList<HashMap<String, String>> activities,
							  Session firstSession) {
		super();
		this.userId = userId;
		this.noActivities = noActivities;
		this.activities = activities;
		this.firstSession = firstSession;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_main_menu_activities, container, false);
		
		// display no. of sessions - total
		TextView txtVwTotalSessions = (TextView) rootView.findViewById(com.visus.R.id.main_menu_no_activities);
		String tmpTotalActivities = " Activities";
		
//		noActivities = 0; // TODO temp 
		
		if(noActivities == 0) {
			txtVwTotalSessions.setText(String.valueOf(0) + tmpTotalActivities);			
		}
		else if(noActivities == 1) {
			txtVwTotalSessions.setText(String.valueOf(noActivities) + " Activity" );			
		}
		else {
			txtVwTotalSessions.setText(String.valueOf(noActivities) + tmpTotalActivities );
		}		
		
		
		/*
		 * Get the date of the first session (activity)
		 */
		TextView txtVwFirstSession = (TextView) rootView.findViewById(com.visus.R.id.main_menu_activities_date_account_created);
		StringBuilder strFirstSessionActivity = new StringBuilder();
		
		if(firstSession == null) {
			strFirstSessionActivity.append("Created");
		}
		else {
			strFirstSessionActivity.append("Created since ");

			// day
			strFirstSessionActivity.append(firstSession.getDay() + " ");
			
			// day no (dd)
			strFirstSessionActivity.append(String.valueOf(firstSession.getDayNo()) );
			strFirstSessionActivity.append(" ");
			
			// month (MMM)
			strFirstSessionActivity.append(firstSession.getMonth() );
			strFirstSessionActivity.append(", ");
			
			// year (YYYY)
			strFirstSessionActivity.append(firstSession.getYear() );
		}
				
		// display
		txtVwFirstSession.setText(strFirstSessionActivity.toString() );
		
		ArrayList<HashMap<String, String>> activityResults = new ArrayList<HashMap<String, String>>();		
		activityResults = activities;
		
		// display activity categories
		list = (ListView) rootView.findViewById(com.visus.R.id.main_activity_activity_types);
		adapter = new MainMenuAdapter(getActivity(), activityResults);
						
		list.setAdapter(adapter);
						
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}	
}
