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

public class LatestActivityFragment extends Fragment {
	
	private int userId;
	private SessionHandler dbSession;
	
	private ListView list;
	private MainMenuAdapter adapter;
	
	public LatestActivityFragment() {
		super();
	}
	
	public LatestActivityFragment(int userId) {
		this.userId = userId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_main_menu_latest_activity, container, false);
		dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
			
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<String> sessionTypes = new ArrayList<String>();
		ArrayList<HashMap<String, String>> latestSessions = new ArrayList<HashMap<String, String>>();
		
		Log.e("Visus", "USER ID: " + userId);
		
		try {
			dbSession.open();
			sessions = dbSession.getLatestSessions(userId);
//			sessions = dbSession.getSessionsThisYear(userId);
//			sessionTypes = dbSession.getSessionTypes(user.getUserId());
		}
		finally {
			dbSession.close();				
		}
					
		if(sessions.isEmpty()) {
			HashMap<String, String> emptyMsg = new HashMap<String, String>();
			String msg = "None Created";
			emptyMsg.put(MainMenuListView.SESSION, msg);
			
			latestSessions.add(emptyMsg);			
			
			Log.e("Visus", "Sessions is empty");
		}
		else {
			Log.e("Visus", "Sessions is not empty");

			
			
			Log.e("Visus", "onCreate() - User ID is: " + userId);

			String [] data = { "Hello", "Jon", "Today", "Sunday" };
			
			int noItems = 0;
			
			// output the first five results
			for(Session session : sessions) {
				if(noItems != 5) {				
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(MainMenuListView.SESSION, session.getDay() + " " +
							    					  session.getDayNo() + " " +  
							    					  session.getMonth() + ", " +
							    					  session.getDate() + ", " +
							    					  session.getYear() + " - " +
									                  session.getTimeHour() + ":" +
									                  session.getTimeMinutes() + " " +
									                  session.getDayPeriod() + " - " +
									                  session.getDurationMinutes() + ":" +
									                  session.getDurationSeconds() + " - " +
									                  session.getType()
					       );
					
					latestSessions.add(map);
					
					noItems++;
				}
				else {
					break;
				}
			}		
		}	
		
		list = (ListView) rootView.findViewById(com.visus.R.id.overview_sessions_adapter);
		adapter = new MainMenuAdapter(getActivity(), latestSessions);
		
		list.setAdapter(adapter);
		
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}
	
}
