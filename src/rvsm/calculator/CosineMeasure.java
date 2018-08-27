package rvsm.calculator;

import java.util.ArrayList;

public class CosineMeasure {

	public static double getCosineSimilarity(int[] list1, int[] list2) {
		double cosmeasure = 0;
		double mode1 = getMode(list1);
		double mode2 = getMode(list2);
		double upper = 0;
		for (int i = 0; i < list1.length; i++) {
			upper += list1[i] * list2[i];
		}
		if (mode1 > 0 && mode2 > 0) {
			cosmeasure = upper / (mode1 * mode2);
		}
		return cosmeasure;
	}

	
	
	public static double getCosineSimilarity(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		double cosmeasure = 0;
		int[] listArray1 = new int[20000];
		int[] listArray2 = new int[20000];
		for(int i=0;i<list1.size();i++)
		{
			int item=list1.get(i);
			listArray1[i]=item;
		}
		for(int i=0;i<list2.size();i++)
		{
			int item=list2.get(i);
			listArray1[i]=item;
		}
		cosmeasure=getCosineSimilarity(listArray1, listArray2);
		return cosmeasure;
	}
	
	protected static double getMode(int[] list) {
		double sum = 0;
		for (int i : list) {
			sum += i * i;
		}
		return Math.sqrt(sum);
	}

	public static double getCosineSimilarity(double[] list1, double[] list2) {
		double cosmeasure = 0;
		double mode1 = getMode(list1);
		double mode2 = getMode(list2);
		double upper = 0;
		for (int i = 0; i < list1.length; i++) {
			upper += list1[i] * list2[i];
		}
		if (mode1 > 0 && mode2 > 0) {
			cosmeasure = upper / (mode1 * mode2);
		}
		return cosmeasure;
	}

	protected static double getMode(double[] list) {
		double sum = 0;
		for (double i : list) {
			sum += i * i;
		}
		return Math.sqrt(sum);
	}

}
