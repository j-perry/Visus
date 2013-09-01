package com.visus.ui.sessions.old;

import com.visus.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.TextView;

public class ObjectFragment extends Fragment {
	public static final String ARG_OBJECT = "object";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
		Bundle bundle = getArguments();
		((TextView) rootView.findViewById(android.R.id.text1)).setText(Integer.toString(bundle.getInt(ARG_OBJECT)));
		return rootView;
	}
	
}
