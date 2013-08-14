package com.visus.entities.sessions;

public class Overview {
	
	private int totalNoOfSessions;
	private int totalSessionHours;
	private int activitiesNos;
	
	public Overview() {
		super();
	}
	
	public void setNoOfSessions(int sessionNos) {
		this.totalNoOfSessions = sessionNos;
	}
	
	public int getSessionNos() {
		return totalNoOfSessions;
	}
	
	public void setHours(int hours) {
		this.totalSessionHours = hours;
	}
	
	public int getHours() {
		return totalSessionHours;
	}
	
	public void setActivitiesNos(int activitiesNo) {
		this.activitiesNos = activitiesNo;
	}
	
	public int getActivitiesNos() {
		return activitiesNos;
	}
	
	@Override
	public String toString() {
		return new String();
	}

}
