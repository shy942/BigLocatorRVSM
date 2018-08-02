package rvsm.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import lucene.TFIDFCalculator;
import utility.ContentLoader;
import utility.MiscUtility;

public class RVSMCalcManager {
	HashMap<String, Double> IDFmap;
	HashMap<String, String> sourceFileMap;
	HashMap<String, HashMap<String, Double>> rVSMScoreMap;
	HashMap<String, String> queryMap;
	HashMap<String, Integer> docLengthMap;
	int maxDocLength = 0;
	int minDocLength = 100000;

	public RVSMCalcManager(String sourceFolder, String queryFolder) {
		this.docLengthMap = new HashMap<>();
		this.sourceFileMap = getSourceFileContentMap(sourceFolder);
		this.queryMap = getDocFileContentMap(queryFolder);
		this.IDFmap = getSourceDocumentIDF();
		this.rVSMScoreMap = new HashMap<>();
	}

	protected HashMap<String, String> getDocFileContentMap(String docFolder) {
		File[] files = new File(docFolder).listFiles();
		HashMap<String, String> docMap = new HashMap<>();
		for (File file : files) {
			ArrayList<String> docLines = ContentLoader.getAllLinesOptList(file
					.getAbsolutePath());
			String docContent = MiscUtility.list2Str(docLines);
			docMap.put(file.getName(), docContent);
		}
		return docMap;
	}

	protected HashMap<String, String> getSourceFileContentMap(
			String sourceFolder) {
		File[] files = new File(sourceFolder).listFiles();
		HashMap<String, String> docMap = new HashMap<>();
		for (File file : files) {
			ArrayList<String> srcTokens = ContentLoader.getDocTokensAll(file
					.getAbsolutePath());
			// storing source file length
			this.docLengthMap.put(file.getName(), srcTokens.size());

			// getting maximum and minimum document length
			if (srcTokens.size() > maxDocLength) {
				maxDocLength = srcTokens.size();
			}
			if (srcTokens.size() < minDocLength) {
				minDocLength = srcTokens.size();
			}
			String docContent = MiscUtility.list2Str(srcTokens);
			docMap.put(file.getName(), docContent);
		}
		return docMap;
	}

	protected int getMaxDocLength() {
		return maxDocLength;
	}

	protected int getMinDocLength() {
		return minDocLength;
	}

	public HashMap<String, Double> getSourceDocumentIDF() {
		TFIDFCalculator objTFIDFCalc = new TFIDFCalculator();
		return objTFIDFCalc.calculateIDFOnly();
	}

	protected void calculatRVSM() {
		int maxDocLength = getMaxDocLength();
		int minDocLength = getMinDocLength();
		for (String queryKey : this.queryMap.keySet()) {
			String queryDocument = this.queryMap.get(queryKey);
			HashMap<String, Double> tempScoreMap = new HashMap<>();
			for (String srcFileKey : this.sourceFileMap.keySet()) {
				String sourceDocument = this.sourceFileMap.get(srcFileKey);
				RVSMCalc rcalc = new RVSMCalc(sourceDocument, this.IDFmap, this.docLengthMap.get(srcFileKey),
						queryDocument, maxDocLength, minDocLength);
				double score = rcalc.calculateRVSMScore();
				tempScoreMap.put(srcFileKey, score);
			}
			tempScoreMap = normalizeMe(tempScoreMap);
			// now store the VSM score
			this.rVSMScoreMap.put(queryKey, tempScoreMap);
			System.out.println(tempScoreMap);
			System.out.println("Done:" + queryKey);
		}
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
		// String
		// sourceInfo="/Users/user/Documents/Ph.D/2018/Data/SourceForBL/";
		// String bugInfo="/Users/user/Documents/Ph.D/2018/Data/BugData/";

		// For Windows
		// String sourceInfo="E:\\PhD\\Data\\SourceForBL\\";
		// String bugInfo="E:\\PhD\\Data\\BugDataNew\\";

		long start = System.currentTimeMillis();

		// For testing
		String sourceFolder = "./Data/SourceForBL";
		String bugReportFolder = "./Data/BugDataNew";

		RVSMCalcManager manager = new RVSMCalcManager(sourceFolder,
				bugReportFolder);
		manager.calculatRVSM();

		long end = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (end - start) / 1000 + " s");

	}

}
