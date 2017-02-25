package com.visus.main;

// android apis
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.*;

// core program packages
import com.visus.R;
import com.visus.ui.settings.fragments.AboutFragment;
import com.visus.ui.settings.fragments.GeneralFragment;


/**
 * Enables the user to configure the apps settings
 * @author Jonathan Perry
 */
public class Settings extends FragmentActivity implements ActionBar.TabListener {

	private static int activeUserId;
		
	// pager
	private ViewPager settingsViewPager;
	private SettingsPagerAdapter settingsPagerAdapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		// hide the on-screen keyboard (set focus off)
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Bundle b = getIntent().getExtras();
		activeUserId = b.getInt("ActiveUserId");
				
		final ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		settingsViewPager = (ViewPager) findViewById(com.visus.R.id.settings_pager);
		settingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager(), activeUserId);
		settingsViewPager.setAdapter(settingsPagerAdapter);
				
		ArrayAdapter<CharSequence> arAdapter = ArrayAdapter.createFromResource(this, R.array.settings_list, R.layout.action_bar_list);
		
		/**
		 * The following two methods are crucial. Do not delete them.
		 */
		ab.setListNavigationCallbacks(arAdapter, new OnNavigationListener() {
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				settingsViewPager.setCurrentItem(itemPosition);
				return true;
			}
		});
		
		settingsViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {			
			@Override
			public void onPageSelected(int position) {
				ab.setSelectedNavigationItem(position);
			}			
		});		
		
	}
			
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		//Log.e("Visus", "back home");
		
		switch(item.getItemId()) {
			// app logo
			case android.R.id.home:
				Intent upIntent = new Intent(this, MainActivity.class);
	            if(NavUtils.shouldUpRecreateTask(this, upIntent) ) {
	                // This activity is not part of the application's task, so create a new task
	                // with a synthesized back stack.
	                TaskStackBuilder.from(this)
	                        // If there are ancestor activities, they should be added here.
	                        .addNextIntent(upIntent)
	                        .startActivities();
	                finish();
	            } else {
	                // This activity is part of the application's task, so simply
	                // navigate up to the hierarchical parent activity.
	                NavUtils.navigateUpTo(this, upIntent);
	            }
	            break;
	        default:
	        	break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_view_settings, menu);
		return true;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		settingsViewPager.setCurrentItem(tab.getPosition());		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
		
	public static final class SettingsPagerAdapter extends FragmentPagerAdapter {

		private int userId;
		private Bundle bundle;
		private int NO_FRAGMENTS = 2;
		
		public SettingsPagerAdapter(FragmentManager fm, int userId) {
			super(fm);
			this.userId = userId;
		}

		@Override
		public Fragment getItem(int item) {
			switch(item) {
				case 0:
					bundle = new Bundle();
					bundle.putInt("userId", userId);
					return new GeneralFragment();
				case 1:
					return new AboutFragment();
				default:
					Fragment f = new Fragment();
					return f;
			}
		}

		@Override
		public int getCount() {
			return NO_FRAGMENTS;
		}
	}			
}