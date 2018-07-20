package lucene;

import java.util.ArrayList;
import java.util.HashMap;

import corpus.maker.BugReportPreprocessor;

public class VSMCalculator {

	String content;
	BugReportPreprocessor bpp;
	HashMap<String, Integer> tfMap = new HashMap<String, Integer>();
	
	
	//int year = 2011;
	int totalTerms=0;

	public VSMCalculator(String content) {
		this.content = content;
		this.bpp = new BugReportPreprocessor(content);
		this.totalTerms=0;
	}

	public  HashMap<String, Integer> getTF() {
		String preprocessed = bpp.performNLP();
		String[] words = preprocessed.split(" ");
		for (String token : words) {
			this.totalTerms++;
			if (tfMap.containsKey(token)) {
				int count = tfMap.get(token) + 1;
				this.tfMap.put(token, count);
			} else {
				this.tfMap.put(token, 1);
			}
		}
		//System.out.println("From VSMCalculator: "+totalTerms);
		return tfMap;
	}
	
	public HashMap<String, Double> getLogTF()
	{
		HashMap<String, Double> logTFmap=new HashMap<>();
		this.tfMap=this.getTF();
		for(String key: tfMap.keySet())
		{
			int tf=tfMap.get(key);
			Double logTF=Math.log(Double.valueOf(tf))+1;
			logTFmap.put(key, logTF);
		}
		return logTFmap;
	}

	public int getTotalNoTerms(){
		return this.totalTerms;
	}
	public HashMap<String, Double> getDF() {
		// calculate IDF
		HashMap<String, Double> dfMap = new HashMap<String, Double>();
		try {
			ArrayList<String> tokens = new ArrayList<String>(tfMap.keySet());
			TFIDFCalculator calc = new TFIDFCalculator();
			dfMap = calc.getDF(tokens);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return dfMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
}