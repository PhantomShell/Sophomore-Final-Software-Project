import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Controller {
	private ArrayList<ClassPeriod> classes = new ArrayList<ClassPeriod>();
	private ClassPeriodDistanceComparator distanceComparator;
	private SeatingHandler seatingHandler;
	
	public Controller() {
		HashMap<Integer, Integer> roomDistances = new HashMap<Integer, Integer>();
		distanceComparator = new ClassPeriodDistanceComparator(roomDistances);
		int[][][] rowSizes =
			{
				{
					{7, 8, 8, 9, 10, 8, 11, 12, 12, 13, 14, 14, 14, 14},
					{9, 10, 10, 10, 11, 9, 12, 12, 12, 13, 14, 14, 14, 14},
					{7, 8, 8, 9, 10, 10, 11, 12, 12, 13, 14, 8, 8, 8}
				},
				{
					{9, 8, 13, 13, 12, 11, 11, 11},
					{15, 15, 15, 15, 10, 10, 5}
				}
			};
		seatingHandler = new SeatingHandler(rowSizes);
	}
	
	public void generateSeatingChart() {
		seatingHandler.clear();
		seatingHandler.fill(classes, distanceComparator);
		LinkedHashMap<String, Integer>[][][] seatingChart = seatingHandler.getSeatingChart();
	}
}