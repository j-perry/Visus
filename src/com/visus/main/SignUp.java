package com.visus.main;

import com.visus.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;

import com.visus.database.*;
import com.visus.entities.*;

/**
 * Registers a new user
 * @author Jonathan Perry
 *
 */
public class SignUp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_new_user, menu);
		return true;
	}
	
	/**
	 * registers a new user
	 * @param view
	 */
	public void registerUser(View view) {
		
		User user = new User();
		//UserHandler dbUser = new UserHandler(this);
		DatabaseHandler db = new DatabaseHandler(this);
		
		// first name
		EditText firstname = (EditText) findViewById(R.id.first_name);
		user.setFirstname(firstname.getText().toString() );
		
		// gender
		Spinner gender = (Spinner) findViewById(R.id.gender);
		user.setGender(gender.getSelectedItem().toString() );
		
		// age
		EditText age = (EditText) findViewById(R.id.age);
		user.setAge(Integer.valueOf(age.getText().toString() ));
		
		db.addUser(user);		
		
		// display the main menu
		Intent intent = new Intent(SignUp.this, MainActivity.class);
		startActivity(intent);
	}

}
