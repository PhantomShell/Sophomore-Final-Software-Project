package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Class to fill ClassPeriod objects based on data from a
 * given .MER file.
 * @author Jagan Prem, Mark Estep
 */
public class ClassPeriodFiller {
	
	static Map<String, Integer> field;
	static ArrayList<ArrayList<String>> studentData;
	static File outFile;
	
	/**
	 * Prepares for filling the classes by loading in the students'
	 * data and the field names and indices.
	 * @author Mark Estep
	 * @param path The path to the .MER file.
	 * @throws FileNotFoundException When the .MER file is not found.
	 */
	public ClassPeriodFiller(String path) throws FileNotFoundException {
		if (path == null || !path.endsWith(".mer"))
			throw new FileNotFoundException();
		Scanner file = new Scanner(new File(path));
		
		//create Map of Fields (line 1)
		field = new HashMap<String,Integer>();
		int i = 0;
		for(String fieldName: file.nextLine().split(",")){
			field.put(fieldName.trim(), i++);
		}
		
		//Load in student data
		studentData = new ArrayList<ArrayList<String>>();
		while(file.hasNext()){
			ArrayList<String> student = new ArrayList<String>();
			for(String studentField: file.nextLine().replaceAll("\"$|^\"", "").split("\",\"")){
				student.add(studentField.trim());
			}
			studentData.add(student);
		}
		file.close();
	}
	
	/**
	 * Creates a filled list of periods based on the student data.
	 * @return ArrayList<ArrayList<ClassPeriod>> The periods of classes.
	 * @throws Exception
	 * @see #studentData, #field
	 */
	public ArrayList<ArrayList<ClassPeriod>> fillPeriods() throws Exception {
		ArrayList<ArrayList<ClassPeriod>> periods = new ArrayList<ArrayList<ClassPeriod>>();
		for (int i = 0; i < 8; i++) {
			periods.add(new ArrayList<ClassPeriod>());
		}
		for (ArrayList<String> student : studentData) {
			int period = Integer.parseInt(student.get(field.get("PD")));
			String room = student.get(field.get("RMNO"));
			String firstName = student.get(field.get("TCHF"));
			String lastName = student.get(field.get("TCHL"));
			int grade = Integer.parseInt(student.get(field.get("GR")));
			boolean male = student.get(field.get("G")).equals("M");
			ArrayList<ClassPeriod> classPeriods = periods.get(period - 1);
			boolean found = false;
			for (ClassPeriod classPeriod : classPeriods)
				if (classPeriod.toString().equals(lastName + ", " + firstName)) {
					classPeriod.incrementClassSize(grade, male);
					found = true;
					break;
				}
			if (!found) {
				ClassPeriod newClass = new ClassPeriod(room, firstName, lastName);
				newClass.incrementClassSize(grade, male);
				classPeriods.add(newClass);
			}
		}
		return periods;
	}
	
}