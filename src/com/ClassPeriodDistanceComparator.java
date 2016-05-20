package com;

import java.util.Comparator;
import java.util.HashMap;

public class ClassPeriodDistanceComparator implements Comparator<ClassPeriod> {
	private HashMap<String, Integer> roomDistances;
	
	public ClassPeriodDistanceComparator(HashMap<String, Integer> roomDistances) {
		this.roomDistances = new HashMap<String, Integer>(roomDistances);
	}
	
	@Override
	public int compare(ClassPeriod class1, ClassPeriod class2) {
		Integer distance1 = roomDistances.get(class1.getRoom());
		Integer distance2 = roomDistances.get(class2.getRoom());
		return distance1.compareTo(distance2);
	}

}
