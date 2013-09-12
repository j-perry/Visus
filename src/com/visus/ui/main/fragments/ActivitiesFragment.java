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

public class ActivitiesFragment extends Fragment {
	
	private int userId;
	
	private ListView list;
	private MainMenuAdapter adapter;
	
	public ActivitiesFragment() {
		super();
	}
	
	public ActivitiesFragment(int userId) {
		super();
		this.userId = userId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_main_menu_activities, container, false);
		ArrayList<String> activitiesResult = new ArrayList<String>();
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<HashMap<String, String>> activities = new ArrayList<HashMap<String, String>>();
		
		Log.e("Visus", "ActivitiesFragment: " + userId);
		
		try {
			dbSession.open();
			activitiesResult = dbSession.getActivities(userId);
		}
		finally {
			dbSession.close();
		}
		
		if(activitiesResult.isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(MainMenuListView.SESSION, "None Created");			
			
			activities.add(map);
		}
		else {
			for(String activity : activitiesResult) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(MainMenuListView.SESSION, activity);			
				
				activities.add(map);
			}
		}
						
		list = (ListView) rootView.findViewById(com.visus.R.id.main_activity_activity_types);
		adapter = new MainMenuAdapter(getActivity(), activities);
		
		list.setAdapter(adapter);
						
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}
	
}
