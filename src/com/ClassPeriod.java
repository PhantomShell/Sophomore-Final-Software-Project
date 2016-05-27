package com;

public class ClassPeriod {
	private String room;
	private int[] gradeCount9;
	private int[] gradeCount10;
	private int[] gradeCount11;
	private int[] gradeCount12;
	private String firstName;
	private String lastName;
	
	public ClassPeriod(String room, String firstName, String lastName) {
		this.room = room;
		this.firstName = firstName;
		this.lastName = lastName;
		gradeCount9 = new int[2];
		gradeCount10 = new int[2];
		gradeCount11 = new int[2];
		gradeCount12 = new int[2];
	}
	
	public String getRoom() {
		return room;
	}
	
	public int getClassSize(int grade, boolean male, boolean female) {
		switch (grade) {
			case 9: return (male ? gradeCount9[0] : 0) + (female ? gradeCount9[1] : 0);
			case 10: return (male ? gradeCount10[0] : 0) + (female ? gradeCount10[1] : 0);
			case 11: return (male ? gradeCount11[0] : 0) + (female ? gradeCount11[1] : 0);
			case 12: return (male ? gradeCount12[0] : 0) + (female ? gradeCount12[1] : 0);
			default: return 0;
		}
	}
	
	public int incrementClassSize(int grade, boolean male) {
		int index = male ? 0 : 1;
		switch (grade) {
			case 9: return gradeCount9[index]++;
			case 10: return gradeCount10[index]++;
			case 11: return gradeCount11[index]++;
			case 12: return gradeCount12[index]++;
			default: return 0;
		}
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	@Override
	public String toString() {
		return lastName + ", " + firstName;
	}
}
