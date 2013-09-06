package com.visus.ui.settings.fragments;

import com.visus.ui.MainMenuAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentOptions extends Fragment {
	
	private int userId;
	
	public FragmentOptions() {
		super();
	}
	
	public FragmentOptions(int userId) {
		this.userId = userId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MainMenuAdapter adapter;
		
		View rootView = inflater.inflate(com.visus.R.layout.fragment_settings_options, container, false);
		
		return rootView;
	}
	
}
