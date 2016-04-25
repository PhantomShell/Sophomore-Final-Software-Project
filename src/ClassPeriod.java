
public class ClassPeriod {
	private int roomNumber;
	private int classSize;
	private String teacher;
	
	public ClassPeriod(int roomNumber, int classSize, String teacher) {
		this.roomNumber = roomNumber;
		this.classSize = classSize;
		this.teacher = teacher;
	}
	
	public int getRoomNumber() {
		return roomNumber;
	}
	
	public int getClassSize() {
		return classSize;
	}
	
	public String getTeacher() {
		return teacher;
	}
}
