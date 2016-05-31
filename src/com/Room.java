package com;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class for storing seat information of room with rows of seating.
 * @author Jagan Prem
 * @see Row
 */
public class Room {
	private ArrayList<Row> rows;
	private int lastUnfilled;
	private int filledSeats;
	
	/**
	 * Creates an array of rows based on the specified row sizes.
	 * @param rowSizes The sizes of the rows to create.
	 * @see Row#Row(int)
	 */
	public Room(int[] rowSizes) {
		rows = new ArrayList<Row>();
		for (int rowSize : rowSizes) 
			rows.add(new Row(rowSize));
		lastUnfilled = 0;
		filledSeats = 0;
	}
	
	/**
	 * Gets the number of seats in the room currently full.
	 * @return int The number of filled seats.
	 */
	public int getFilledSeats() {
		return filledSeats;
	}
	
	/**
	 * Returns whether or not the Room is currently full.
	 * @return boolean Whether the Room is full.
	 */
	public boolean isFull() {
		return rows.get(rows.size() - 1).isFull() || rows.size() == 0;
	}
	
	/**
	 * Returns a chart of how the rows contained are filled.
	 * @return LinkedHashMap<String, Integer>[] The arrangement of seats between the rows.
	 * @see Row#getSections()
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Integer>[] getRowChart() {
		LinkedHashMap<String, Integer>[] rowChart = new LinkedHashMap[rows.size()];
		for (int i = 0; i < rows.size(); i++)
			rowChart[i] = rows.get(i).getSections();
		return rowChart;
	}
	
	/**
	 * Fills subsequent rows until there is no more left to fill
	 * or the Room becomes full.
	 * @param teacher The teacher whose class is filling in.
	 * @param seats The number of seats to fill in.
	 * @return int The number of seats filled.
	 * @see #lastUnfilled, #filledSeats, Row#fill(String, int)
	 */
	public int fill(String teacher, int seats) {
		int sum = 0;
		while (sum < seats && !isFull()) {
			Row row = rows.get(lastUnfilled);
			sum += row.fill(teacher, seats - sum);
			if (row.isFull())
				lastUnfilled += 1;
		}
		filledSeats += sum;
		return sum;
	}
	
	/**
	 * Clears the room of all filled seats.
	 * @see Row#clear(), #filledSeats, #lastUnfilled
	 */
	public void clear() {
		for (Row row : rows)
			row.clear();
		filledSeats = 0;
		lastUnfilled = 0;
	}
	
	/**
	 * Gets the maximum capacity for the Room.
	 * @return int The number of seats in the Room.
	 * @see Row#getSize()
	 */
	public int capacity() {
		int sum = 0;
		for (Row row : rows)
			sum += row.getSize();
		return sum;
	}
}
