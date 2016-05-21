package com;

public class ClassPeriod {
	private String room;
	private int gradeCount9;
	private int gradeCount10;
	private int gradeCount11;
	private int gradeCount12;
	private String firstName;
	private String lastName;
	
	public ClassPeriod(String room, String firstName, String lastName) {
		this.room = room;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getRoom() {
		return room;
	}
	
	public int getClassSize(int grade) {
		switch (grade) {
			case 9: return gradeCount9;
			case 10: return gradeCount10;
			case 11: return gradeCount11;
			case 12: return gradeCount12;
			default: return 0;
		}
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public int incrementClassSize(int grade) {
		switch (grade) {
			case 9: return gradeCount9++;
			case 10: return gradeCount10++;
			case 11: return gradeCount11++;
			case 12: return gradeCount12++;
			default: return 0;
		}
	}
	
	public String toString() {
		return lastName + ", " + firstName;
	}
}
