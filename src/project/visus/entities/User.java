package project.visus.entities;

public class User {

	private String firstname;
	private String surname;
	private String gender;
	private int age;
	
	public User(String firstname, String surname, String gender, int age) {
		this.firstname = firstname;
		this.surname = surname;
		this.gender = gender;
		this.age = age;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getSurname() {
		return surname;
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
