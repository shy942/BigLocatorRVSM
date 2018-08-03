package simi.score.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import lucene.VSMCalculator;
import rvsm.calculator.CosineMeasure;
import sun.security.util.Length;
import utility.ContentLoader;
import utility.MathUtil;
import utility.MiscUtility;

public class SimiScoreCalc {

	public HashMap<String, HashMap<String, Double>> bugContentMap;
	String currentBugRepotFileKey;
	HashMap<String, Double> currentBugReportMap;
	HashMap<Integer, ArrayList<String>> goldsetMap;

	public SimiScoreCalc(String currentBugReportFileKey,
			HashMap<String, HashMap<String, Double>> bugContentMap,
			HashMap<Integer, ArrayList<String>> goldsetMap) {
		this.currentBugRepotFileKey = currentBugReportFileKey;
		this.bugContentMap = bugContentMap;
		// this is the new bug report content
		this.currentBugReportMap = this.bugContentMap
				.get(currentBugReportFileKey);
		this.goldsetMap = goldsetMap;
	}

	protected HashSet<String> getCombinedVocabulary(
			HashSet<String> currentBugVocabulary,
			HashSet<String> pastBugVocabulary) {
		HashSet<String> vocabulary = new HashSet<>();
		vocabulary.addAll(currentBugVocabulary);
		vocabulary.addAll(pastBugVocabulary);
		return vocabulary;
	}

	protected double[] getVSMRepresentation(ArrayList<String> vocabulary,
			HashMap<String, Double> scoreMap) {
		double[] vector = new double[vocabulary.size()];
		for (int index = 0; index < vocabulary.size(); index++) {
			String term = vocabulary.get(index);
			if (scoreMap.containsKey(term)) {
				vector[index] = scoreMap.get(term);
			} else {
				vector[index] = 0;
			}
		}
		return vector;
	}

	protected HashMap<String, Double> getSimilarBugReportKeyNScore() {
		HashMap<String, Double> simBugReportMap = new HashMap<>();
		int currentBugID = Integer
				.parseInt(currentBugRepotFileKey.split("\\.")[0]);
		HashMap<String, Double> currentBugContentMap = this.bugContentMap
				.get(currentBugRepotFileKey);
		HashSet<String> currentVocabulary = new HashSet<>(
				currentBugContentMap.keySet());
		for (String bugReportKey : this.bugContentMap.keySet()) {
			int bugID = Integer.parseInt(bugReportKey.split("\\.")[0]);
			if (currentBugID > bugID) {
				HashMap<String, Double> pastBugContentMap = this.bugContentMap
						.get(bugReportKey);
				HashSet<String> pastVocabulary = new HashSet<>(
						pastBugContentMap.keySet());
				HashSet<String> combined = getCombinedVocabulary(
						currentVocabulary, pastVocabulary);
				double[] currentVector = getVSMRepresentation(
						new ArrayList<String>(combined), currentBugContentMap);
				double[] pastVector = getVSMRepresentation(
						new ArrayList<String>(combined), pastBugContentMap);
				double cosMeasure = CosineMeasure.getCosineSimilarity(
						currentVector, pastVector);
				if (cosMeasure > 0) {
					simBugReportMap.put(bugReportKey, cosMeasure);
				}
			}
		}
		return simBugReportMap;
	}

	public HashMap<String, Double> calculateSimiScoreMap() {
		HashMap<String, Double> simBugReportMap = getSimilarBugReportKeyNScore();
		HashMap<String, ArrayList<Double>> simiScoreAll = new HashMap<>();
		HashMap<String, Double> simiScoreMap = new HashMap<>();
		for (String bugReportKey : simBugReportMap.keySet()) {
			int bugID = Integer.parseInt(bugReportKey.split("\\.")[0].trim());
			if (this.goldsetMap.containsKey(bugID)) {
				ArrayList<String> changeset = this.goldsetMap.get(bugID);
				double cosScore = simBugReportMap.get(bugReportKey);
				for (String srcFile : changeset) {
					if (simiScoreAll.containsKey(srcFile)) {
						ArrayList<Double> temp = simiScoreAll.get(srcFile);
						temp.add(cosScore);
						simiScoreAll.put(srcFile, temp);
					} else {
						ArrayList<Double> temp = new ArrayList<>();
						temp.add(cosScore);
						simiScoreAll.put(srcFile, temp);
					}
				}
			}
		}
		// now calculate the similarity score
		for (String srcFile : simiScoreAll.keySet()) {
			double meanSimi = MathUtil.getMean(simiScoreAll.get(srcFile));
			simiScoreMap.put(srcFile, meanSimi);
		}
		return simiScoreMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/********
		 * Always design a function that has an input and an output, do not use
		 * void, this is confusing
		 *********/

	}
}
