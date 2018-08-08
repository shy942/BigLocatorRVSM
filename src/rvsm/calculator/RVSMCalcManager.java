package rvsm.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lucene.TFIDFCalculator;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.ItemSorter;
import utility.MiscUtility;

public class RVSMCalcManager {
	HashMap<String, Double> IDFmap;
	HashMap<String, HashMap<String, Double>> sourceFileLogTFMap;
	HashMap<String, HashMap<String, Double>> rVSMScoreMap;
	HashMap<String, HashMap<String, Double>> queryLogTFMap;
	HashMap<String, Integer> docLengthMap;
	int maxDocLength = 0;
	int minDocLength = 100000;

	public RVSMCalcManager(
			HashMap<String, HashMap<String, Double>> sourceFileLogTFMap,
			HashMap<String, HashMap<String, Double>> queryLogTFMap,
			HashMap<String, Double> IDFMap, int maxDocLength, int minDocLength,
			HashMap<String, Integer> docLengthMap) {
		this.docLengthMap = docLengthMap;
		this.sourceFileLogTFMap = sourceFileLogTFMap;
		this.queryLogTFMap = queryLogTFMap;
		this.IDFmap = IDFMap;
		this.rVSMScoreMap = new HashMap<>();
		this.maxDocLength = maxDocLength;
		this.minDocLength = minDocLength;
	}

	protected int getMaxDocLength() {
		return maxDocLength;
	}

	protected int getMinDocLength() {
		return minDocLength;
	}

	public HashMap<String, HashMap<String, Double>> calculatRVSMforAll(HashMap<Integer, ArrayList<String>> goldsetMap) {
		// ArrayList<String> finalResult = new ArrayList<>();
		int maxDocLength = getMaxDocLength();
		int minDocLength = getMinDocLength();
        int count =0;
		for (String queryKey : this.queryLogTFMap.keySet()) {
			String queryWOtxt=queryKey.substring(0,queryKey.length()-4);
			if(goldsetMap.containsKey(Integer.valueOf(queryWOtxt))){
				count++;
				if(count>100) break;
			HashMap<String, Double> bugReportLogTF = this.queryLogTFMap
					.get(queryKey);

			HashMap<String, Double> tempScoreMap = new HashMap<>();
			for (String srcFileKey : this.sourceFileLogTFMap.keySet()) {
				HashMap<String, Double> sourceLogTF = this.sourceFileLogTFMap
						.get(srcFileKey);
				int srcDocLength = this.docLengthMap.get(srcFileKey);

				RVSMCalc rcalc = new RVSMCalc(sourceLogTF, IDFmap,
						srcDocLength, bugReportLogTF, maxDocLength,
						minDocLength);
				double score = rcalc.calculateRVSMScore();
				tempScoreMap.put(srcFileKey, score);
			}
			tempScoreMap = normalizeMe(tempScoreMap);
			// now store the VSM score
			this.rVSMScoreMap.put(queryKey, tempScoreMap);

			System.out.println("RVSM :" + queryKey + ": Done!");
			}
		}
		System.out.println("RVSM calculation done!");
		return this.rVSMScoreMap;
	}

	protected ArrayList<String> getSortedTopKResult(int topK, String queryKey,
			HashMap<String, Double> sortedScore) {
		ArrayList<String> finalResult = new ArrayList<>();
		for (String srcID : sortedScore.keySet()) {
			finalResult.add(queryKey + "," + srcID + ","
					+ sortedScore.get(srcID));
			if (finalResult.size() == topK)
				break;
		}
		return finalResult;

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
		// For Mac

		String sourceFolder = "/Users/user/Documents/Ph.D/2018/Data/SourceForBL/";
		String bugReportFolder = "/Users/user/Documents/Ph.D/2018/Data/BugData/";

		// For Windows
		// String sourceFolder="E:\\PhD\\Data\\ProcessedSourceForBL\\";
		// String bugReportFolder="E:\\PhD\\Data\\ProcessedBugData\\";

		long start = System.currentTimeMillis();

		// For testing
		// String sourceFolder = "./Data/SourceForBL";
		// String bugReportFolder = "./Data/BugDataNew";

		// RVSMCalcManager manager = new RVSMCalcManager(sourceFolder,
		// bugReportFolder);
		// manager.calculatRVSM();

		long end = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (end - start) / 1000 + " s");

	}

}
