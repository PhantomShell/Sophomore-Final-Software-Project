package com;

import java.util.LinkedHashMap;

/**
 * Class for storing information on a single seating row.
 * @author Jagan Prem
 */
public class Row {
	private int size;
	private LinkedHashMap<String, Integer> sections;
	private int filledSeats = 0;
	
	/**
	 * Takes the size for the Row to be.
	 * @param size The number of seats in the Row.
	 * @see #size
	 */
	public Row(int size) {
		this.size = size;
		sections = new LinkedHashMap<String, Integer>();
	}
	
	/**
	 * Gets the size of the Row.
	 * @return int The number of seats in the Row.
	 * @see #size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Gets the amount of seats yet unfilled.
	 * @return int The number of unfilled seats.
	 * @see #getSize()
	 * @see #filledSeats
	 */
	private int remainingSpace() {
		return getSize() - filledSeats;
	}
	
	/**
	 * Returns whether or not the Row is full.
	 * @return boolean Whether the Row has any space left.
	 * @see #remainingSpace()
	 */
	public boolean isFull() {
		return remainingSpace() == 0;
	}
	
	/**
	 * Returns a copy of the seating arrangement of the rows.
	 * @return LinkedHashMap&lt;String, Integer&gt; The seating arrangement.
	 * @see #sections
	 */
	public LinkedHashMap<String, Integer> getSections() {
		return new LinkedHashMap<String, Integer>(sections);
	}
	
	/**
	 * Fills the Row with a class until full or there are no more
	 * students to fill in.
	 * @param teacher The name of the teacher whose class is filling in.
	 * @param seats The number of seats to fill in.
	 * @return int The number of seats filled.
	 */
	public int fill(String teacher, int seats) {
		if (seats > remainingSpace())
			return fill(teacher, remainingSpace());
		else {
			sections.put(teacher, seats);
			filledSeats += seats;
			return seats;
		}
	}
	
	/**
	 * Clears the Row of all filled seats.
	 * @see #filledSeats
	 * @see #sections
	 */
	public void clear() {
		filledSeats = 0;
		sections = new LinkedHashMap<String, Integer>();
	}
}
