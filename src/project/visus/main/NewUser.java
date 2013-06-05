package project.visus.main;

import project.visus.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class NewUser extends Activity {

	private String firstname;
	private Integer age;
	private String gender;
	
	public NewUser() {
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);
	}
	
	public void submit(View view) {
		// used to retrieve text
		TextView firstname,
				 age;
		Spinner  gender;
		
		firstname = (TextView) findViewById(R.id.first_name);
		age = (TextView) findViewById(R.id.age);
		gender = (Spinner) findViewById(R.id.gender);
		
		this.firstname = (String) firstname.getText();
		this.age = (Integer) Integer.valueOf( (String) age.getText());
		this.gender = (String) gender.getSelectedItem().toString();
		
		
	}
	
}
