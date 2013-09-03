package com.visus.ui.sessions.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.database.SessionHandler;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuAdapter;
import com.visus.ui.MainMenuListView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentThisWeek extends Fragment {
	
	private int userId;
	
	public FragmentThisWeek() {
		super();
	}
	
	public FragmentThisWeek(int userId) {
		this.userId = userId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_sessions_this_week, container, false);
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<HashMap<String, String>> sessionsThisWeek = new ArrayList<HashMap<String, String>>();
		MainMenuAdapter adapter;
		
		ListView lvToday = (ListView) rootView.findViewById(com.visus.R.id.listview_sessions_this_week);
		
		/*
		 * bind and display our content from the database!
		 */
		try {
			dbSession.open();
		}
		finally {
			sessions = dbSession.getLatestSessions(userId);
			dbSession.close();
		}
		
		// retrieve sessions
		for(Session session : sessions) {
			HashMap<String, String> map = new HashMap<String, String>();
			
			map.put(MainMenuListView.SESSION, session.getDay() + " " +
											  session.getDayNo() + " " +  
											  session.getMonth() + ", " +
											  session.getYear() + " - " +
							                  session.getTimeHour() + ":" +
							                  session.getTimeMinutes() + " " +
							                  session.getDayPeriod() + " - " +
							                  session.getDurationMinutes() + ":" +
							                  session.getDurationSeconds() + " - " +
							                  session.getType()
					);

			sessionsThisWeek.add(map);
		}
		
		adapter = new MainMenuAdapter(getActivity(), sessionsThisWeek);
		
		lvToday.setAdapter(adapter);
		
		return rootView;
	}

}
