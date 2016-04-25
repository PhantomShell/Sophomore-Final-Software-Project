import java.util.Comparator;
import java.util.HashMap;

public class ClassPeriodDistanceComparator implements Comparator<ClassPeriod> {
	private HashMap<Integer, Integer> roomDistances;
	
	public ClassPeriodDistanceComparator(HashMap<Integer, Integer> roomDistances) {
		this.roomDistances = new HashMap<Integer, Integer>(roomDistances);
	}
	
	@Override
	public int compare(ClassPeriod class1, ClassPeriod class2) {
		Integer distance1 = roomDistances.get(class1.getRoomNumber());
		Integer distance2 = roomDistances.get(class2.getRoomNumber());
		return distance1.compareTo(distance2);
	}

}
