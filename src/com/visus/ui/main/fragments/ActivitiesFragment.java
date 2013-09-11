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
	
	public ActivitiesFragment() {
		super();
	}
	
	public ActivitiesFragment(int userId) {
		this.userId = userId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_main_menu_activities, container, false);
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		
		
		
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}
	
}
