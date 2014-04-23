package com.visus.ui.main.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.entities.sessions.Session;
import com.visus.ui.ListViewAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitiesFragment extends Fragment {
	
	@SuppressWarnings("unused")
	private int userId;
	private int noActivities;
	@SuppressWarnings("unused")
	private Session firstSession;
	private ArrayList<HashMap<String, String>> activities;
	
	private ListView list;
	private ListViewAdapter adapter;
	
	public ActivitiesFragment() {
		super();
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_main_menu_activities, container, false);
		
		// display no. of sessions - total
		TextView txtVwTotalSessions = (TextView) rootView.findViewById(com.visus.R.id.main_menu_no_activities);
		String tmpTotalActivities = " Activities";
				
		if(noActivities == 0) {
			txtVwTotalSessions.setText(String.valueOf(0) + tmpTotalActivities);			
		}
		else if(noActivities == 1) {
			txtVwTotalSessions.setText(String.valueOf(noActivities) + " Activity" );			
		}
		else {
			txtVwTotalSessions.setText(String.valueOf(noActivities) + tmpTotalActivities );
		}		
					
		ArrayList<HashMap<String, String>> activityResults = new ArrayList<HashMap<String, String>>();		
		activityResults = activities;
		
		// display activity categories
		list = (ListView) rootView.findViewById(com.visus.R.id.main_activity_activity_types);
		adapter = new ListViewAdapter(getActivity(), activityResults);
						
		list.setAdapter(adapter);
						
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}	
	
	public void addContext(int userId, 
			  			   int noActivities, 
			  			   ArrayList<HashMap<String, String>> activities,
			  			   Session firstSession) {
		this.userId = userId;
		this.noActivities = noActivities;
		this.activities = activities;
		this.firstSession = firstSession;
	}
}
