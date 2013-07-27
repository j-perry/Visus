package com.visus.main;

import com.visus.R;
import com.visus.database.UserHandler;
import com.visus.entities.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class ViewSettings extends Activity {

	private User user = null;
	private UserHandler dbUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_settings);
		
		dbUser = new UserHandler(this);
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
		getMenuInflater().inflate(R.menu.activity_view_settings, menu);
		return true;
	}
		
	public void deleteAccount(View view) {
	
		//Log.e("Visus", String.valueOf(user.getUserId()) );
		//user = dbUser.getActiveUser();
			
		dbUser.deleteUser();
		
//		if(dbUser.getActiveUser() == null) {
//			Intent intent = new Intent(ViewSettings.this, NewUser.class);
//			startActivity(intent);
//		}
		
//		dbUser.deleteUser(user);
		
//		Intent intent = new Intent(ViewSettings.this, NewUser.class);
//		startActivity(intent);
		
//		
//		if(dbUser.getActiveUser() == null) {
//			Intent intent = new Intent(ViewSettings.this, MainActivity.class);
//			startActivity(intent);
//		}
	}

}
