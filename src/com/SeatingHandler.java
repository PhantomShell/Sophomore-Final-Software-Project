package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

public class SeatingHandler {
	private Room[][] rooms;
	private int row = 0;
	
	public SeatingHandler(int[][][] rowSizes) {
		rooms = new Room[rowSizes.length][];
		for (int i = 0; i < rowSizes.length; i++) {
			int[][] seatingRow = rowSizes[i];
			rooms[i] = new Room[seatingRow.length];
			for (int j = 0; j < seatingRow.length; j++)
				rooms[i][j] = new Room(seatingRow[j]);
		}
	}
	
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
	
	public void fill(ArrayList<ClassPeriod> classes, ArrayList<Integer> grades, boolean male, boolean female, ClassPeriodDistanceComparator comparator) {
		ArrayList<ClassPeriod> copy = new ArrayList<ClassPeriod>(classes);
		Collections.sort(copy, comparator);
		fill(copy, grades, male, female);
	}
	
	public void clear() {
		for (int i = 0; i < rooms.length; i++)
			for (int j = 0; j < rooms[i].length; j++)
				rooms[i][j].clear();
	}
	
	public int capacity() {
		int sum = 0;
		for (Room[] row : rooms)
			for (Room room : row)
				sum += room.capacity();
		return sum;
	}
}
