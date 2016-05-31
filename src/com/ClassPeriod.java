package com;

/**
 * Class for storing information on individual classes by teacher.
 * @author Jagan Prem
 */
public class ClassPeriod {
	private String room;
	private int[] gradeCount9;
	private int[] gradeCount10;
	private int[] gradeCount11;
	private int[] gradeCount12;
	private String firstName;
	private String lastName;
	
	/**
	 * Sets the fields to the provided values and initializes the remainder.
	 * @param room The room number of the class.
	 * @param firstName The first name of the teacher.
	 * @param lastName The last name of the teacher.
	 */
	public ClassPeriod(String room, String firstName, String lastName) {
		this.room = room;
		this.firstName = firstName;
		this.lastName = lastName;
		gradeCount9 = new int[2];
		gradeCount10 = new int[2];
		gradeCount11 = new int[2];
		gradeCount12 = new int[2];
	}
	
	/**
	 * Gets the value of the room number.
	 * @return String The number of the room.
	 */
	public String getRoom() {
		return room;
	}
	
	/**
	 * Gets the size of the class according to specified conditions.
	 * @param grade The grades of the students to be counted.
	 * @param male Whether to count male students.
	 * @param female Whether to count female students.
	 * @return int The number of students.
	 */
	public int getClassSize(int grade, boolean male, boolean female) {
		switch (grade) {
			case 9: return (male ? gradeCount9[0] : 0) + (female ? gradeCount9[1] : 0);
			case 10: return (male ? gradeCount10[0] : 0) + (female ? gradeCount10[1] : 0);
			case 11: return (male ? gradeCount11[0] : 0) + (female ? gradeCount11[1] : 0);
			case 12: return (male ? gradeCount12[0] : 0) + (female ? gradeCount12[1] : 0);
			default: return 0;
		}
	}
	
	/**
	 * Increases a specific count of students by one.
	 * @param grade The grade to add a student to.
	 * @param male Whether the student is male.
	 * @return int The new value of the count.
	 */
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
	
	/**
	 * Gets the first name of the teacher.
	 * @return String The first name.
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * Gets the first name of the teacher.
	 * @return The last name.
	 */
	public String getLastName() {
		return lastName;
	}
	
	@Override
	public String toString() {
		return lastName + ", " + firstName;
	}
}
