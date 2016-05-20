package com;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Room {
	private ArrayList<Row> rows = new ArrayList<Row>();
	private int lastUnfilled = 0;
	private int filledSeats = 0;
	
	public Room(int[] rowSizes) {
		for (int rowSize : rowSizes) 
			rows.add(new Row(rowSize));
	}
	
	public int getFilledSeats() {
		return filledSeats;
	}
	
	public boolean isFull() {
		return rows.get(rows.size() - 1).isFull() || rows.size() == 0;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Integer>[] getRowChart() {
		LinkedHashMap<String, Integer>[] rowChart = new LinkedHashMap[rows.size()];
		for (int i = 0; i < rows.size(); i++)
			rowChart[i] = rows.get(i).getSections();
		return rowChart;
	}
	
	public int fill(String teacher, int students) {
		int sum = 0;
		while (sum < students && !isFull()) {
			Row row = rows.get(lastUnfilled);
			sum += row.fill(teacher, students - sum);
			if (row.isFull())
				lastUnfilled += 1;
		}
		filledSeats += sum;
		return sum;
	}
	
	public void clear() {
		for (Row row : rows)
			row.clear();
	}
}
