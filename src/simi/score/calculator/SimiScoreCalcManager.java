package simi.score.calculator;

import java.util.ArrayList;
import java.util.HashMap;

public class SimiScoreCalcManager {

	HashMap<String, HashMap<String, Double>> bugContentMap;
	HashMap<Integer, ArrayList<String>> goldsetMap;

	public SimiScoreCalcManager(
			HashMap<String, HashMap<String, Double>> bugContentMap,
			HashMap<Integer, ArrayList<String>> goldsetMap) {
		this.bugContentMap = bugContentMap;
		this.goldsetMap = goldsetMap;
	}

	public HashMap<String, HashMap<String, Double>> collectSimiScoreMap() {
		HashMap<String, HashMap<String, Double>> masterSimiScoreMap = new HashMap<>();
		for (String bugReportKey : bugContentMap.keySet()) {
			SimiScoreCalc simiCalc = new SimiScoreCalc(bugReportKey,
					bugContentMap, goldsetMap);
			HashMap<String, Double> simiScoreMap = simiCalc
					.calculateSimiScoreMap();
			simiScoreMap = normalizeMe(simiScoreMap);
			masterSimiScoreMap.put(bugReportKey, simiScoreMap);
			System.out.println("SimiScore: " + bugReportKey + " : Done!");
		}
		System.out.println("Simiscore calculated successfully!");
		return masterSimiScoreMap;
	}

	protected HashMap<String, Double> normalizeMe(
			HashMap<String, Double> tempMap) {
		double max = 0;
		for (String key : tempMap.keySet()) {
			double score = tempMap.get(key);
			if (score > max) {
				max = score;
			}
		}
		for (String key : tempMap.keySet()) {
			if (max > 0) {
				double oldScore = tempMap.get(key);
				tempMap.put(key, oldScore / max);
			}
		}
		return tempMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
