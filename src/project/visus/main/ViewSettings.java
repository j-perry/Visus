package project.visus.main;

import project.visus.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ViewSettings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_view_settings, menu);
		return true;
	}

}
