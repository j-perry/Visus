package project.visus.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import project.visus.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

/**
 * Creates a new session
 * @author Jonathan Perry
 * @version 1.0
 */
@SuppressLint("SimpleDateFormat")
public class NewSession extends Activity {
	
	private TextView time;
	private Date date;
	private SimpleDateFormat df;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
		
		// format the current time
		df = new SimpleDateFormat("HH:mm aa");
		date = new Date();
		
		// set the current time
		time = (TextView) findViewById(R.id.clock_time);
		time.setText(df.format(date));
	}

	/**
	 * Creates a new options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_view, menu);
		return true;
	}

}
