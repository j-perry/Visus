package com.visus.ui.sessions.old;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SessionsPagerAdapter extends FragmentStatePagerAdapter {

	public SessionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new ObjectFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ObjectFragment.ARG_OBJECT, i + 1);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public int getCount() {
		return 4; // no. of tabs?
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return "This " + (position + 1);
	}

}
