package com.visus.ui.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.*;

/**
 * Display's content about the app
 * @author Jonathan Perry
 */
public class AboutFragment extends Fragment {
	
	public AboutFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_settings_about, container, false);

		return rootView;
	}
}