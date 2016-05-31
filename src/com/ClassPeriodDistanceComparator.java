package com;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Comparator for comparing the distances between two
 * ClassPeriod and the auditorium.
 * @author Jagan Prem
 * @see Comparator
 * @see ClassPeriod
 */
public class ClassPeriodDistanceComparator implements Comparator<ClassPeriod> {
	
	private HashMap<String, Integer> roomDistances;
	
	/**
	 * Takes a HashMap providing the distance to each room.
	 * @param roomDistances The distances from each of the rooms to the auditorium.
	 * @see HashMap
	 */
	public ClassPeriodDistanceComparator(HashMap<String, Integer> roomDistances) {
		this.roomDistances = new HashMap<String, Integer>(roomDistances);
	}
	
	@Override
	public int compare(ClassPeriod class1, ClassPeriod class2) {
		Integer distance1 = roomDistances.get(class1.getRoom());
		Integer distance2 = roomDistances.get(class2.getRoom());
		if (distance1 == null)
			distance1 = Integer.MAX_VALUE;
		if (distance2 == null)
			distance2 = Integer.MAX_VALUE;
		return distance1.compareTo(distance2);
	}

}
