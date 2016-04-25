import java.util.LinkedHashMap;

public class Row {
	private int size;
	private LinkedHashMap<String, Integer> sections = new LinkedHashMap<String, Integer>();
	private int filledSeats = 0;
	
	public Row(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	private int remainingSpace() {
		return getSize() - filledSeats;
	}
	
	public boolean isFull() {
		return remainingSpace() == 0;
	}
	
	public LinkedHashMap<String, Integer> getSections() {
		return new LinkedHashMap<String, Integer>(sections);
	}
	
	public int fill(String teacher, int students) {
		if (students > remainingSpace())
			return fill(teacher, remainingSpace());
		else {
			sections.put(teacher, students);
			filledSeats += students;
			return students;
		}
	}
	
	public void clear() {
		filledSeats = 0;
		sections = new LinkedHashMap<String, Integer>();
	}
}
