package project.visus.entities;

import java.util.Date;

public class Session {

	private long time;
	private Date dt;
	
	public Session() {
		
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setDateTime(Date dt) {
		this.dt = dt;
	}
	
	public Date getDateTime() {
		return dt;
	}
	
}
