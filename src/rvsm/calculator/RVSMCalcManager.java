package rvsm.calculator;

package rvsm.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import lucene.TFIDFCalculator;
import lucene.VSMCalculator;
import simi.score.calculator.SimiScoreCalc;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class RVSMCalc {
	HashMap<String, Double> IDFmap;
	HashMap<String, HashMap<String, Double>> SourceTFInfo;
	HashMap<String, String> SourceInfoForSimiCalc;
	HashMap<String, String> QueryInfo;
	HashMap<String, Double> hm; //make it descriptive

	public RVSMCalc() {
		this.IDFmap = new HashMap<>();
		this.SourceTFInfo = new HashMap<>();
		this.SourceInfoForSimiCalc = new HashMap<>();
		this.QueryInfo = new HashMap<>();
		this.hm = new HashMap<>();
	}
	

	public void LoadSourceTFhm(String SourceCodeDir) {

		File[] sourceCodeFiles = new File(SourceCodeDir).listFiles();
		System.out.println("Total number of source code files: "
				+ sourceCodeFiles.length);
		for (File sourceCodeFile : sourceCodeFiles) {
			String sourceCodeContent = ContentLoader
					.readContentSimple(sourceCodeFile.getAbsolutePath());
			this.SourceInfoForSimiCalc.put(sourceCodeFile.getName(),
					sourceCodeContent);
			VSMCalculator objVSMCalc = new VSMCalculator(sourceCodeContent);
			HashMap<String, Double> logTF = objVSMCalc.getLogTF();
			// this.LengthList.add(String.valueOf(sourceCodeContent.length()));
			this.SourceTFInfo.put(sourceCodeFile.getName(), logTF);
		}
		// MiscUtility.showResult(10, this.SourceTFInfo);
	}

	public Double getMaxLength(ArrayList<String> LengthList) {
		Double MaxLen = (Double) 0.0;
		for (String len : LengthList) {
			Double lengthInt = Double.valueOf(len);
			if (MaxLen < lengthInt)
				MaxLen = lengthInt;
		}
		return MaxLen;
	}

	public Double getMinLength(ArrayList<String> LengthList) {
		Double MinLen = (Double) 100000.0;
		for (String len : LengthList) {
			Double lengthInt = Double.valueOf(len);
			if (MinLen > lengthInt)
				MinLen = lengthInt;
		}
		return MinLen;
	}

	public void LoadSourceIDF() {
		TFIDFCalculator objTFIDFCalc = new TFIDFCalculator();
		this.IDFmap = objTFIDFCalc.calculateIDFOnly();
		// MiscUtility.showResult(10, this.IDFmap);
	}

	public void LoadQueryInfo(String queryFolder) {
		File[] queryFiles = new File(queryFolder).listFiles();
		System.out.println("Total number of quiries: " + queryFiles.length);
		
		for (File queryFile : queryFiles) {
			String queryContent = ContentLoader.readContentSimple(queryFile
					.getAbsolutePath());
			this.QueryInfo.put(queryFile.getName(), queryContent);
		}
		// MiscUtility.showResult(10, this.QueryInfo);
	}

	protected void calculatRVSM() {

		// For simi score calculator
		SimiScoreCalc objSimCalc = new SimiScoreCalc(
				this.SourceInfoForSimiCalc, this.QueryInfo);

		int count = 0;
		ArrayList<String> totalResult = new ArrayList<>();
		for (String queryInfo : this.QueryInfo.keySet()) {
			count++;
			// if(count>100) break;

			String queryContent = this.QueryInfo.get(queryInfo);
			// Similarty Score Calculation;
			HashMap<String, Double> resultSimiScore = objSimCalc
					.SimilarityCalc(queryContent);
			HashMap<String, Double> sortedSimiResult = MiscUtility
					.sortByValues(resultSimiScore);
			// System.out.println("SimiScore Result===================");
			// MiscUtility.showResult(10, resultSimiScore);

			// rVSM score calculator
			// collect metrics for the query
			ArrayList<String> lengthList = new ArrayList<>();
			VSMCalculator vcalc = new VSMCalculator(queryContent);
			HashMap<String, Double> qtfMap = vcalc.getLogTF();

			for (String key : this.SourceTFInfo.keySet()) {

				HashMap<String, Double> dtfMap = this.SourceTFInfo.get(key);

				int dTotalTerms = this.SourceInfoForSimiCalc.get(key).length();

				double gTerms = 1 / (1 + Math.exp(-Double.valueOf(dTotalTerms)));

				// getting common words
				HashSet<String> qset = new HashSet<>(qtfMap.keySet());
				HashSet<String> dset = new HashSet<>(dtfMap.keySet());

				qset.retainAll(dset);
				HashSet<String> commonSet = new HashSet<String>(qset);

				// Calculating rVSM score
				if (commonSet.size() > 0) {
					double scoreUpperPart = calculateUpperPart(qtfMap, dtfMap,
							commonSet);
					double scooreLowerPart = calculateLowerPart(qtfMap, dtfMap);
					double score = 0.0;
					if (scoreUpperPart != 0 && scooreLowerPart != 0)
						score = (gTerms * scoreUpperPart) / scooreLowerPart;
					if (score > 0.0) {
						this.hm.put(key, score);
						lengthList.add(String.valueOf(score));
					}
				}

			}
			// MiscUtility.showResult(10, hm);
			// Sort rVSM score
			HashMap<String, Double> sortedRVSMsvoreResult = retrieveSortedTopNResult(this.hm);

			// Normalize rVSM score
			double maxLength = Double.valueOf(this.getMaxLength(lengthList));
			double minLength = Double.valueOf(this.getMinLength(lengthList));
			// System.out.println(maxLength+" "+minLength);
			for (String key : sortedRVSMsvoreResult.keySet()) {
				double score = sortedRVSMsvoreResult.get(key);

				if (score > 0.0) {
					Double N = (score - minLength) / (maxLength - minLength);
					score = N;
					sortedRVSMsvoreResult.put(key, score);
				}
			}

			// MiscUtility.showResult(10, sortedRVSMsvoreResult);

			// Now combine both RVSM and Simi socres
			HashMap<String, Double> finalSortedCombonedResult = CombinedRVSMandSimiScoreHM(
					maxLength, minLength, queryInfo, sortedSimiResult,
					sortedRVSMsvoreResult);

			ArrayList<String> tempResults = new ArrayList<String>();
			int resultCount = 0;

			for (String srcFile : finalSortedCombonedResult.keySet()) {
				String resultContent = queryInfo.substring(0,
						queryInfo.length() - 4);
				if (!tempResults.contains(srcFile)) {
					resultCount++;
					if (resultCount > 10)
						break;
					tempResults.add(srcFile);
					resultContent += "," + srcFile + ","
							+ finalSortedCombonedResult.get(srcFile);
					totalResult.add(resultContent);
				}
			}

			// MiscUtility.showResult(10, sortedResult);
			System.out.println(count);
			this.hm.clear();
			sortedRVSMsvoreResult.clear();
			sortedSimiResult.clear();
		}
		// return this.hm;
		System.out.println("Total Query: " + count);
		ContentWriter.writeContent("./Data/Results/BugLocatorAugust1st.txt",
				totalResult);
	}

	public HashMap<String, Double> CombinedRVSMandSimiScoreHM(double maxLength,
			double minLength, String queryInfo,
			HashMap<String, Double> sortedSimiResult,
			HashMap<String, Double> sortedRVSMsvoreResult) {
		double alpha = 0.2;
		// System.out.println("Sorted Simi Score=================================");
		// MiscUtility.showResult(10, sortedSimiResult);
		// System.out.println("Sorted RVSM Score=================================");
		// MiscUtility.showResult(10, sortedRVSMsvoreResult);
		HashMap<String, Double> combinedResult = new HashMap<>();

		for (String srcFile : sortedRVSMsvoreResult.keySet()) {

			String resultContent = queryInfo.substring(0,
					queryInfo.length() - 4);
			if (!combinedResult.containsKey(srcFile)) {

				double finalScore;
				if (sortedSimiResult.containsKey(srcFile)) {
					finalScore = (1 - alpha)
							* sortedRVSMsvoreResult.get(srcFile) + alpha
							* sortedSimiResult.get(srcFile);
				} else {
					finalScore = (1 - alpha)
							* sortedRVSMsvoreResult.get(srcFile);

				}
				if (finalScore > 1.0)
					System.out.println(srcFile + "-------------" + finalScore
							+ "-------------"
							+ sortedRVSMsvoreResult.get(srcFile) + "  "
							+ sortedSimiResult.get(srcFile));
				combinedResult.put(srcFile, finalScore);
			}
		}

		HashMap<String, Double> finalSortedCombonedResult = MiscUtility
				.sortByValues(combinedResult);

		return finalSortedCombonedResult;
	}

	public HashMap retrieveSortedTopNResult(HashMap hm) {
		HashMap sortedResult = new HashMap();
		sortedResult = MiscUtility.sortByValues(hm);
		// MiscUtility.showResult(20,sortedResult);
		return sortedResult;
	}

	protected double calculateUpperPart(HashMap<String, Double> qtfMap,
			HashMap<String, Double> dtfMap, HashSet<String> commons) {
		double scoreUpperPart = 0.0;
		double sum = 0;
		for (String t : commons) {
			double qLogTF = qtfMap.get(t);

			double dLogTF = dtfMap.get(t);
			double qdIDF = 0.0;
			if (this.IDFmap.containsKey(t))
				qdIDF = this.IDFmap.get(t);

			double score = qLogTF * dLogTF * qdIDF;
			sum += score;
		}
		scoreUpperPart = sum;
		return scoreUpperPart;
	}

	protected double calculateLowerPart(HashMap<String, Double> qtfMap,
			HashMap<String, Double> dtfMap) {
		double scoreLowerPart = 0.0;
		// For Query q
		double qsqrt = 0;
		double qsum = 0;
		HashSet<String> qset = new HashSet<>(qtfMap.keySet());
		for (String t : qset) {
			double qLogTF = qtfMap.get(t);

			double idf = 0.0;
			if (this.IDFmap.containsKey(t)) {
				idf = this.IDFmap.get(t);
			}
			double score = qLogTF * idf;
			double squaredScore = Math.pow(score, 2);
			qsum += squaredScore;
		}
		qsqrt = Math.sqrt(qsum);

		// For document d
		double dsqrt = 0;
		double dsum = 0;
		HashSet<String> dset = new HashSet<>(dtfMap.keySet());
		for (String t : dset) {
			// System.out.println(t);
			double dLogTF = dtfMap.get(t);

			double idf = 0.0;
			if (this.IDFmap.containsKey(t)) {
				idf = this.IDFmap.get(t);
			}
			double score = dLogTF * idf;
			double squaredScore = Math.pow(score, 2);
			dsum += squaredScore;
		}
		dsqrt = Math.sqrt(dsum);

		scoreLowerPart = qsqrt * dsqrt;
		return scoreLowerPart;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		RVSMCalc obj = new RVSMCalc();
		// For Mac
		// String
		// sourceInfo="/Users/user/Documents/Ph.D/2018/Data/SourceForBL/";
		// String bugInfo="/Users/user/Documents/Ph.D/2018/Data/BugData/";

		// For Windows
		// String sourceInfo="E:\\PhD\\Data\\SourceForBL\\";
		// String bugInfo="E:\\PhD\\Data\\BugDataNew\\";

		// For testing
		String sourceInfo = "./Data/SourceForBL/";
		String bugInfo = "./Data/BugDataNew/";

		obj.LoadSourceTFhm(sourceInfo);
		obj.LoadSourceIDF();
		obj.LoadQueryInfo(bugInfo);

		obj.calculatRVSM();
	}

}

