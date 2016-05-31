package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Class for handling filling in any class and creating a seating
 * chart.
 * @author Jagan Prem
 * @see Room
 */
public class SeatingHandler {
	private Room[][] rooms;
	private int row = 0;
	
	/**
	 * Creates a 2D array of rooms based on the given row sizes.
	 * @param rowSizes The sizes of each of the rows in each room.
	 * @see Room#Room(int[])
	 */
	public SeatingHandler(int[][][] rowSizes) {
		rooms = new Room[rowSizes.length][];
		for (int i = 0; i < rowSizes.length; i++) {
			int[][] seatingRow = rowSizes[i];
			rooms[i] = new Room[seatingRow.length];
			for (int j = 0; j < seatingRow.length; j++)
				rooms[i][j] = new Room(seatingRow[j]);
		}
	}
	
	/**
	 * Generates the seating chart for the auditorium based on the
	 * individual arrangements of all the rooms within.
	 * @return LinkedHashMap&lt;String, Integer&gt;[][][] The seating chart.
	 * @see Room#getRowChart()
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Integer>[][][] getSeatingChart() {
		LinkedHashMap<String, Integer>[][][] seatingChart = new LinkedHashMap[rooms.length][][];
		for (int i = 0; i < rooms.length; i++) {
			seatingChart[i] = new LinkedHashMap[rooms[i].length][];
			for (int j = 0; j < rooms[i].length; j++)
				seatingChart[i][j] = rooms[i][j].getRowChart();
		}
		return seatingChart;
	}
	
	/**
	 * Fills subsequent rooms until there is no more left to fill
	 * or the auditorium becomes full.
	 * @param teacher The teacher whose class is filling in.
	 * @param students The number of students to fill in.
	 * @see Room#fill(String, int)
	 */
	public void fill(String teacher, int students) {
		int sum = 0;
		while (sum < students) {
			ArrayList<Room> seatingRow = new ArrayList<Room>(Arrays.asList(rooms[row]));
			Collections.sort(seatingRow, new RoomFillComparator());
			Room roomToFill = seatingRow.get(0);
			while (roomToFill.isFull()) {
				row += 1;
				seatingRow = new ArrayList<Room>(Arrays.asList(rooms[row]));
				Collections.sort(seatingRow, new RoomFillComparator());
				roomToFill = seatingRow.get(0);
			}
			sum += roomToFill.fill(teacher, students - sum);
		}
	}
	
	/**
	 * Fills the auditorium with the provided list of classes under the
	 * specified restrictions.
	 * @param classes The list of ClassPeriod objects to fill in.
	 * @param grades The list of grades of students.
	 * @param male Whether to include male students.
	 * @param female Whether to include female students.
	 * @see ClassPeriod#getClassSize(int, boolean, boolean)
	 * @see #fill(String, int)
	 */
	public void fill(ArrayList<ClassPeriod> classes, ArrayList<Integer> grades, boolean male, boolean female) {
		ArrayList<ClassPeriod> copy = new ArrayList<ClassPeriod>(classes);
		for (ClassPeriod period : copy) {
			String teacher = period.getLastName();
			int seats = 1;
			for (int grade: grades)
				seats += period.getClassSize(grade, male, female);
			fill(teacher, seats);
		}
	}
	
	/**
	 * Fills the auditorium with the provided list of classes under the
	 * specified restrictions, while sorting by distance
	 * @param classes The list of ClassPeriod objects to fill in.
	 * @param grades The list of grades of students.
	 * @param male Whether to include male students.
	 * @param female Whether to include female students.
	 * @param comparator The comparator to sort rooms by.
	 * @see ClassPeriodDistanceComparator
	 * @see #fill(ArrayList, ArrayList, boolean, boolean)
	 */
	public void fill(ArrayList<ClassPeriod> classes, ArrayList<Integer> grades, boolean male, boolean female, ClassPeriodDistanceComparator comparator) {
		ArrayList<ClassPeriod> copy = new ArrayList<ClassPeriod>(classes);
		Collections.sort(copy, comparator);
		fill(copy, grades, male, female);
	}
	
	/**
	 * Clears all filled seats in the auditorium.
	 * @see Room#clear()
	 */
	public void clear() {
		for (int i = 0; i < rooms.length; i++)
			for (int j = 0; j < rooms[i].length; j++)
				rooms[i][j].clear();
	}
	
	/**
	 * Gets the maximum capacity for the auditorium.
	 * @return int The number of seats in the auditorium.
	 * @see Room#capacity()
	 */
	public int capacity() {
		int sum = 0;
		for (Room[] row : rooms)
			for (Room room : row)
				sum += room.capacity();
		return sum;
	}
}
