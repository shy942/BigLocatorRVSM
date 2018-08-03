package utility;

import java.util.ArrayList;

public class MathUtil {

	public static double getMean(ArrayList<Double> items) {
		double sum = 0;
		for (double item : items) {
			sum += item;
		}
		return sum / items.size();
	}
}
