package simi.score.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class CosineSimilarityMeasure {

	double cosine_measure = 0;

	// Hash sets
	Set<String> set1;
	Set<String> set2;

	// Array Lists
	ArrayList<String> list1;
	ArrayList<String> list2;

	// Hash Maps
	HashMap<String, Integer> map1;
	HashMap<String, Integer> map2;

	public CosineSimilarityMeasure(ArrayList<String> list1,
			ArrayList<String> list2) {
		// getting cosine similarity measure
		this.list1 = list1;
		this.list2 = list2;
		// instantiating sets
		set1 = new HashSet<String>();
		set2 = new HashSet<String>();
		// instantiating the Hash maps
		map1 = new HashMap<String, Integer>();
		map2 = new HashMap<String, Integer>();
	}

	public double getCosineSimilarityScore() {
		try {
			for (String str : this.list1) {

				if (!str.isEmpty()) {
					set1.add(str);
					if (!map1.containsKey(str))
						map1.put(str, new Integer(1));
					else {
						int count = Integer.parseInt(map1.get(str).toString());
						count++;
						map1.put(str, new Integer(count));
					}
				}
			}

			for (String str : this.list2) {

				if (!str.isEmpty()) {
					set2.add(str);
					if (!map2.containsKey(str))
						map2.put(str, new Integer(1));
					else {
						int count = Integer.parseInt(map2.get(str).toString());
						count++;
						map2.put(str, new Integer(count));
					}
				}
			}

			// converting to Hash Map
			HashSet<String> hset1 = new HashSet<String>(set1);
			HashSet<String> hset2 = new HashSet<String>(set2);

			double sqr1 = 0;
			for (int i = 0; i < hset1.size(); i++) {
				int val = Integer
						.parseInt(map1.get(hset1.toArray()[i]) != null ? map1
								.get(hset1.toArray()[i]).toString() : "0");
				sqr1 += val * val;
			}

			double sqr2 = 0;
			for (int i = 0; i < hset2.size(); i++) {
				int val = Integer
						.parseInt(map2.get(hset2.toArray()[i]) != null ? map2
								.get(hset2.toArray()[i]).toString() : "0");
				sqr2 += val * val;
			}

			// now calculate the similarity
			double top_part = 0;
			for (int i = 0; i < hset1.size(); i++) {
				String key = (String) set1.toArray()[i];
				double val1 = Double.parseDouble(map1.get(key).toString());
				double val2 = Double.parseDouble(map2.get(key) != null ? map2
						.get(key).toString() : "0");
				top_part += val1 * val2;
			}

			double cosine_ratio = 0;
			try {
				cosine_ratio = top_part / (Math.sqrt(sqr1) * Math.sqrt(sqr2));
			} catch (Exception exc) {
				cosine_ratio = 0;
			}
			// System.out.println(cosine_ratio);
			this.cosine_measure = cosine_ratio;

		} catch (Exception exc) {
			// exc.printStackTrace();
		}
		// returning cosine ration
		return cosine_measure;

	}

	protected void show_extracted_tokens(Set s) {
		// code for showing extracted tokens
		for (int i = 0; i < s.size(); i++) {
			System.out.print(s.toArray()[i] + "\t");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
