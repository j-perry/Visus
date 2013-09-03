package com.visus.ui.sessions.fragments;

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

public class FragmentThisYear extends Fragment {
	
	private int userId;
	
	public FragmentThisYear() {
		super();
	}
	
	public FragmentThisYear(int userId) {
		this.userId = userId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_sessions_this_year, container, false);
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<HashMap<String, String>> sessionsThisYear = new ArrayList<HashMap<String, String>>();
		MainMenuAdapter adapter;
		
		ListView lvYear = (ListView) rootView.findViewById(com.visus.R.id.listview_sessions_this_year);
		
		/*
		 * bind and display our content from the database!
		 */
		try {
			dbSession.open();
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQLite Exception Error", e);
		}
		finally {
			sessions = dbSession.getSessionsThisYear(userId);
			dbSession.close();
		}
		
		if(sessions.isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			String msg = "None Created This Year";
			map.put(MainMenuListView.SESSION, msg);
			sessionsThisYear.add(map);
		}
		else {
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

				sessionsThisYear.add(map);
			}
		}
				
		adapter = new MainMenuAdapter(getActivity(), sessionsThisYear);
		
		lvYear.setAdapter(adapter);		
		
		return rootView;
	}
	
	
	
}
