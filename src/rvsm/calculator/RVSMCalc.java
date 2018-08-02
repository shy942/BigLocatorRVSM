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
	HashMap<String, Double> SourceTFMap;
	HashMap<String, Double> queryTFMap;
	String sourceContent;
	String queryContent;
	int maxDocLength;
	int minDocLength;
	int currentDocLength;

	public RVSMCalc(String sourceContent, HashMap<String, Double> IDFMap, int srcDocLength,
			String queryContent, int maxDocLength, int minDocLength) {
		this.IDFmap = IDFMap;
		this.sourceContent = sourceContent;
		this.SourceTFMap = getSourceLogTFMap();
		this.queryContent = queryContent;
		this.queryTFMap = getQueryLogTFMap();
		this.maxDocLength = maxDocLength;
		this.minDocLength = minDocLength;
		this.currentDocLength=srcDocLength;
	}

	protected HashMap<String, Double> getSourceLogTFMap() {
		VSMCalculator objVSMCalc = new VSMCalculator(this.sourceContent);
		return objVSMCalc.getLogTF();
	}

	protected HashMap<String, Double> getQueryLogTFMap() {
		VSMCalculator objVSMCalc = new VSMCalculator(this.queryContent);
		return objVSMCalc.getLogTF();
	}

	protected HashMap<String, Double> getSourceTFIDF() {
		HashMap<String, Double> tfIDFMap = new HashMap<>();
		for (String term : this.SourceTFMap.keySet()) {
			double tfLog = this.SourceTFMap.get(term);
			if (this.IDFmap.containsKey(term)) {
				double idf = this.IDFmap.get(term);
				double score = tfLog * idf;
				tfIDFMap.put(term, score);
			}
		}
		return tfIDFMap;
	}

	protected double getNormalizationFactor(int currentDocLength) {
		double normDocLength = (double) (currentDocLength - minDocLength)
				/ (maxDocLength - minDocLength);
		return 1.0 / (1.0 + Math.exp(-1 * normDocLength));
	}

	protected HashSet<String> getCombinedVocabulary() {
		HashSet<String> vocabulary = new HashSet<>();
		vocabulary.addAll(this.SourceTFMap.keySet());
		vocabulary.addAll(this.queryTFMap.keySet());
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

	public double calculateRVSMScore() {
		double gFactor = getNormalizationFactor(this.currentDocLength);
		HashSet<String> vocabulary = getCombinedVocabulary();
		HashMap<String, Double> sourceTFIDF = getSourceTFIDF();
		double[] sourceDocVector = getVSMRepresentation(new ArrayList<>(
				vocabulary), sourceTFIDF);
		double[] queryVector = getVSMRepresentation(
				new ArrayList<>(vocabulary), this.queryTFMap);
		double cos = CosineMeasure.getCosineSimilarity(sourceDocVector,
				queryVector);
		return gFactor * cos;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String query = "face text reconcil thread editor open host workspac browser string string tooltip java editor open stack dump reveal reconcil thread";
		String sourceDoc = "support overrid web browser browser style string browser string string tooltip excep null overrid web browser browser string browser";

		int maxLen = 100;
		int minLen = 5;
		int docLength=18;
		

		HashMap<String, Double> IDFMap = new TFIDFCalculator()
				.calculateIDFOnly();

		RVSMCalc vsmCalc = new RVSMCalc(sourceDoc, IDFMap, docLength, query, maxLen,
				minLen);
		System.out.println(vsmCalc.calculateRVSMScore());


		// For Mac
		// String
		// sourceInfo="/Users/user/Documents/Ph.D/2018/Data/SourceForBL/";
		// String bugInfo="/Users/user/Documents/Ph.D/2018/Data/BugData/";

		// For Windows
		// String sourceInfo="E:\\PhD\\Data\\SourceForBL\\";
		// String bugInfo="E:\\PhD\\Data\\BugDataNew\\";

		// For testing
	}
}
