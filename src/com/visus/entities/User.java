package com.visus.entities;

public class User {

	private int id;
	private int active;
//	private String firstname;
//	private String surname;
//	private String gender;
//	private int age;
	
	private int targetDay;
	private int targetMonth;
	
	public User() {
		
	}
	
	public User(int id, 
			    int active, 
			    int targetDay, 
			    int targetMonth) {
		this.id = id;
		this.active = active;
		this.targetDay = targetDay;
		this.targetMonth = targetMonth;
	}
	
	public void setUserId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return id;
	}
	
	public void setActive(int active) {
		this.active = active;
	}
	
	public int getActive() {
		return active;
	}
		
	public void setTargetDay(int targetDay) {
		this.targetDay = targetDay;
	}
	
	public int getTargetDay() {
		return targetDay;
	}
	
	public void setTargetMonth(int targetMonth) {
		this.targetMonth = targetMonth;
	}
	
	public int getTargetMonth() {
		return targetMonth;
	}
	
}
