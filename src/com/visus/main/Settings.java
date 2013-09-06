package com.visus.main;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.main.Sessions.SessionsPagerAdapter;
import com.visus.ui.settings.fragments.FragmentHistory;
import com.visus.ui.settings.fragments.FragmentOptions;
import com.visus.ui.settings.fragments.FragmentPersonal;


/**
 * Enables the user to configure the apps settings
 * @author Jonathan Perry
 *
 */
public class Settings extends FragmentActivity implements ActionBar.TabListener {

	private User user = null;
	private UserHandler dbUser;
	
	private ViewPager settingsPager;
	private SettingsPagerAdapter settingsPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		Bundle b = getIntent().getExtras();
		int activeUserId = b.getInt("ActiveUserId");
		
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		settingsPager = (ViewPager) findViewById(com.visus.R.id.settings_pager);
		settingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager(), activeUserId);
		
		// initialise the page view adapter
		settingsPager.setAdapter(settingsPagerAdapter);
		settingsPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					
		@Override
		public void onPageSelected(int position) {
				ab.setSelectedNavigationItem(position);
			}	
		});
		
		// create our tabs
		initTabs(ab);
		
//		dbUser = new UserHandler(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_view_settings, menu);
		return true;
	}
		
	/**
	 * Initialises new tabs
	 * @param ab
	 */
	private void initTabs(ActionBar ab) {
		final String tabPersonal = "Personal";
		final String tabSessionsHistory = "History";
		final String tabOptions = "Options";
		
		ab.addTab(ab.newTab().setText(tabPersonal).setTabListener(this));
		ab.addTab(ab.newTab().setText(tabSessionsHistory).setTabListener(this));
		ab.addTab(ab.newTab().setText(tabOptions).setTabListener(this));
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		settingsPager.setCurrentItem(tab.getPosition());			
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	public static class SettingsPagerAdapter extends FragmentPagerAdapter {
		
		private static final int NO_FRAGMENTS = 3;
		private int userId;

		public SettingsPagerAdapter(FragmentManager fm, int userId) {
			super(fm);
			this.userId = userId;
		}

		/**
		 * Get's the page item
		 */
		@Override
		public Fragment getItem(int item) {
			switch(item) {
				case 0:
					return new FragmentPersonal(userId);
				case 1:
					return new FragmentHistory(userId);
				case 2:
					return new FragmentOptions(userId);
				default:
					Fragment fragment = new Fragment();
					return fragment;
			}
		}

		/**
		 * Return's no of pages
		 */
		@Override
		public int getCount() {
			return NO_FRAGMENTS;
		}
	}
}