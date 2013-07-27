package com.visus.entities;

public class User {

	private int id;
	private int active;
	private String firstname;
	private String surname;
	private String gender;
	private int age;
	
	public User() {
		
	}
	
	public User(int id, int active, String firstname, String gender, int age) {
		this.id = id;
		this.active = active;
		this.firstname = firstname;
		this.gender = gender;
		this.age = age;
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
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setSurname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getSurname() {
		return firstname;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public int getAge() {
		return age;
	}
	
}
