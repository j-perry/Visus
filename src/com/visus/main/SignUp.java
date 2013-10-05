package com.visus.main;

// android apis
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.sqlite.SQLiteException;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;

/**
 * Registers a new user
 * @author Jonathan Perry
 *
 */
public class SignUp extends Activity {
	
	private EditText firstname;
	private EditText age;
	private Spinner genderSpinner;
	
	private AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		// hide the on-screen keyboard (set focus off)
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		final String gender = "Gender";
		final String male = "Male";
		final String female = "Female";
		
		String [] genders = { gender, male, female };
		
		genderSpinner = new Spinner(this);
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, 
	    																	R.layout.sign_up_spinner_gender_layout, 
	    																	genders);
	    
	    spinnerArrayAdapter.setDropDownViewResource( R.layout.sign_up_spinner_gender_list_layout );

	    genderSpinner = (Spinner) findViewById(R.id.gender);
	    genderSpinner.setAdapter(spinnerArrayAdapter);	    

		firstname = (EditText) findViewById(R.id.first_name);
		age = (EditText) findViewById(R.id.age);
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
		UserHandler dbHandler = new UserHandler(this);
		StringBuilder emptyFields = new StringBuilder();
		String strAge = null;
		int tmpAge = 0;
		int missingFieldItems = 0;
		
		// first name
		if(firstname.getText().toString().isEmpty() ) {
			emptyFields.append("\n");
			emptyFields.append(Html.fromHtml("&#149;"));
			emptyFields.append(" Firstname");
			missingFieldItems++;
		}
		else {
			user.setFirstname( firstname.getText().toString() );
		}
		
		// gender
		if(genderSpinner.getSelectedItem().toString().equals("Gender")) {
			emptyFields.append("\n");
			emptyFields.append(Html.fromHtml("&#149;"));
			emptyFields.append(" Gender");
			missingFieldItems++;
		} 
		else {
			user.setGender(genderSpinner.getSelectedItem().toString() );
		}
			
				
		// age
		if(age.getText().toString().isEmpty()) {
			emptyFields.append("\n");
			emptyFields.append(Html.fromHtml("&#149;"));
			emptyFields.append(" Age");
			missingFieldItems++;
		}
		else {
			strAge = age.getText().toString();
			tmpAge = Integer.parseInt(strAge);
			user.setAge(tmpAge);
		}
		
		// default targets
		user.setTargetDay(0);
		user.setTargetMonth(0);
		
		
		if(!emptyFields.toString().isEmpty()) {
			if(missingFieldItems == 1) {
				// display an AlertDialog
				String title = "Required Field Missing";
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
				alertDialogBuilder.setTitle(title);
				
				alertDialogBuilder.setMessage("The following field is missing. Please fill it in:\n" + emptyFields.toString() );
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("OK", new OkOnClickListener() );
				alertDialog = alertDialogBuilder.create();
				
				// display it
				alertDialog.show();
			}
			else {
				// display an AlertDialog
				String title = "Required Fields Missing";
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
				alertDialogBuilder.setTitle(title);
				
				alertDialogBuilder.setMessage("The following fields are missing. Please fill them in:\n" + emptyFields.toString() );
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("OK", new OkOnClickListener() );
				alertDialog = alertDialogBuilder.create();
				
				// display it
				alertDialog.show();
			}
			
		}
		else {
			try {
				dbHandler.open();
			}
			catch(SQLiteException e) {
				Log.e("Visus", "SQL Error", e);
			}
			finally {
				dbHandler.add(user);
				dbHandler.close();		
				Intent intent = new Intent(SignUp.this, MainActivity.class);
				startActivity(intent);	
			}
		}
	}
	
	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			alertDialog.dismiss();
		}
	}

}
