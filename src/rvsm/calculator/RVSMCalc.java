package rvsm.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.rmi.CORBA.Util;

import lucene.TFIDFCalculator;
import lucene.VSMCalculator;
import simi.score.calculator.SimiScoreCalc;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;


public class RVSMCalc {

	
	
	
	
	HashMap<String, Double> IDFmap;
	HashMap<String, HashMap<String, Double>> SourceTFInfo;
	HashMap<String, String> QueryInfo;
	HashMap<String, Double> hm;
    ArrayList<String> LengthList;
	
	public RVSMCalc() {
		
		
		this.IDFmap=new HashMap<>();
		this.SourceTFInfo=new HashMap<>();
		this.QueryInfo=new HashMap<>();
		this.hm=new HashMap<>();
		this.LengthList=new ArrayList<>();
	}
	
	
	public void LoadSourceTFhm(String SourceCodeDir)
	{
		
		File[] sourceCodeFiles = new File(SourceCodeDir).listFiles();
		System.out.println("Total number of source code files: "+sourceCodeFiles.length);
		int noOfTotalDocument=sourceCodeFiles.length;
		
		for (File sourceCodeFile : sourceCodeFiles) {
			String sourceCodeContent = ContentLoader.readContentSimple(sourceCodeFile
				.getAbsolutePath());
			VSMCalculator objVSMCalc =new VSMCalculator(sourceCodeContent);
			HashMap<String, Double> logTF=objVSMCalc.getLogTF();
			this.LengthList.add(String.valueOf(objVSMCalc.getTotalNoTerms()));
			this.SourceTFInfo.put(sourceCodeFile.getName(), logTF);
			
		}
		//MiscUtility.showResult(10, this.SourceTFInfo);
	}
	
	public Long getMaxLength()
	{
		Long MaxLen=(long) 0;
		for(String len:this.LengthList)
		{
			Long lengthInt=Long.valueOf(len);
			if(MaxLen<lengthInt)MaxLen=lengthInt;
		}
		return MaxLen;
	}
	
	public Long getMinLength()
	{
		Long MinLen=(long) 100000;
		for(String len:this.LengthList)
		{
			Long lengthInt=Long.valueOf(len);
			if(MinLen>lengthInt)MinLen=lengthInt;
		}
		return MinLen;
	}
	
	public void LoadSourceIDF()
	{
		TFIDFCalculator objTFIDFCalc=new TFIDFCalculator();
		this.IDFmap=objTFIDFCalc.calculateIDFOnly();
		//MiscUtility.showResult(10, this.IDFmap);
	}
	
	public void LoadQueryInfo(String queryFolder)
	{
		File[] queryFiles = new File(queryFolder).listFiles();
		System.out.println("Total number of quiries: "+queryFiles.length);
		int noOfTotalDocument=queryFiles.length;
		
		for (File queryFile : queryFiles) {
			String queryContent = ContentLoader.readContentSimple(queryFile
					.getAbsolutePath());
			this.QueryInfo.put(queryFile.getName(), queryContent);
		}
		//MiscUtility.showResult(10, this.QueryInfo);
	}
	
	public HashMap<String, String> modifySoureInfoHM(HashMap<String, HashMap<String, Double>> SourceTFInfo)
	{
		HashMap<String, String> modifyHM=new HashMap<>();
		for(String sourceID:SourceTFInfo.keySet())
		{
			HashMap<String, Double> hm=SourceTFInfo.get(sourceID);
			String str=MiscUtility.hashMap2Str(hm);
			modifyHM.put(sourceID, str);
		}
		return modifyHM;
	}
	protected void calculatRVSM() {
	
		HashMap<String, String> modifyForSimCalcHM=this.modifySoureInfoHM(this.SourceTFInfo);
		SimiScoreCalc objSimCalc = new SimiScoreCalc(modifyForSimCalcHM,this.QueryInfo);
		
		
		
		double maxLength=Double.valueOf(this.getMaxLength());
		double minLength=Double.valueOf(this.getMinLength());
		System.out.println(maxLength+" "+minLength);
		
		int count=0;
		ArrayList<String> totalResult=new ArrayList<>();
		for(String queryInfo : this.QueryInfo.keySet())
		{
			count++;
			//if(count>10) break;
			
			String queryContent=this.QueryInfo.get(queryInfo);
			//Similarty Score Calculation;
			HashMap<String, Double> resultSimiScore=objSimCalc.SimilarityCalc(queryContent);
			System.out.println("SimiScore Result===================");
			MiscUtility.showResult(10, resultSimiScore);
		
			// collect metrics for the query
		    
			VSMCalculator vcalc = new VSMCalculator(queryContent);
			HashMap<String, Double> qtfMap = vcalc.getLogTF();
			
			for (String key : this.SourceTFInfo.keySet()) 
			{
				
				HashMap<String, Double> dtfMap = this.SourceTFInfo.get(key);
				
				int dTotalTerms=this.SourceTFInfo.get(key).size();
			
				//Calculate Nx
				double Nx=(dTotalTerms-minLength)/(maxLength-minLength);
				//Calculate gTerms
				double gTerms=1/(1+Math.exp(-Nx));
			
			
				// getting common words
				HashSet<String> qset = new HashSet<>(qtfMap.keySet());
				HashSet<String> dset = new HashSet<>(dtfMap.keySet());

				qset.retainAll(dset);
				HashSet<String> commonSet = new HashSet<String>(qset);
			
			
				//Calculating rVSM score
				if(commonSet.size() > 0)
				{		
					double scoreUpperPart=calculateUpperPart(qtfMap,dtfMap,commonSet);
					double scooreLowerPart=calculateLowerPart(qtfMap,dtfMap);
					double score=0.0;
					if(scoreUpperPart!=0&&scooreLowerPart!=0) score=(gTerms*scoreUpperPart)/scooreLowerPart;
					//if(scoreUpperPart!=0&&scooreLowerPart!=0) score=(scoreUpperPart)/scooreLowerPart;
					this.hm.put(key, score);
				}
			
			}
			HashMap<String, Double> sortedResult=retrieveSortedTopNResult(hm);
			
			ArrayList<String> tempResults=new ArrayList<String>();
			int resultCount=0;
			
			for(String srcFile: sortedResult.keySet())
			{
				String resultContent=queryInfo.substring(0, queryInfo.length()-4);
				if(!tempResults.contains(srcFile))
				{
					resultCount++;
					if(resultCount>10)break;
					tempResults.add(srcFile);
					resultContent+=","+srcFile+","+sortedResult.get(srcFile);
					totalResult.add(resultContent);
				}
			}
			
			//MiscUtility.showResult(10, sortedResult);
			System.out.println(count);
			hm.clear();
		}
		//return this.hm;
		System.out.println("Total Query: "+count);
		ContentWriter.writeContent("./Data/Results/July21-1.txt", totalResult);
	}
	
	public HashMap retrieveSortedTopNResult(HashMap hm)
	{
		HashMap sortedResult=new HashMap();
		sortedResult=MiscUtility.sortByValues(hm);
		//MiscUtility.showResult(20,sortedResult);
		return sortedResult;
	}
	
	
	
	
	public String retrieveMaxMinLength()
	{
		String content="";
		String inFile="MaxMinLength.txt";
		content=utility.ContentLoader.readContentSimple(inFile);
		return content;
	}
	
	protected double calculateUpperPart(HashMap<String, Double> qtfMap, HashMap<String, Double> dtfMap,
		 HashSet<String> commons)
	{
		double scoreUpperPart=0.0;
		double sum=0;
		for(String t:commons){
			double qLogTF=qtfMap.get(t);
			
			double dLogTF=dtfMap.get(t);
			double qdIDF=0.0;
			if(this.IDFmap.containsKey(t)) qdIDF=this.IDFmap.get(t); 
			 
			double score=qLogTF*dLogTF*qdIDF;
			sum+=score;
		}
		scoreUpperPart=sum;
		return scoreUpperPart;
	}
	
	protected double calculateLowerPart( HashMap<String,Double> qtfMap, HashMap<String,Double> dtfMap)
	{
		double scoreLowerPart=0.0;
		//For Query q
		double qsqrt=0;
		double qsum=0;
		HashSet<String> qset = new HashSet<>(qtfMap.keySet());
		for(String t:qset)
		{
			double qLogTF=qtfMap.get(t);
			
			double idf=0.0;
			if(this.IDFmap.containsKey(t))
			{
				idf=this.IDFmap.get(t);
			}
			double score=qLogTF*idf;
			double squaredScore=Math.pow(score, 2);
			qsum+=squaredScore;
		}
		qsqrt=Math.sqrt(qsum);
		
		//For document d
		double dsqrt=0;
		double dsum=0;
		HashSet<String> dset=new HashSet<>(dtfMap.keySet());
		for(String t:dset)
		{
			//System.out.println(t);
			double dLogTF=dtfMap.get(t);
			
			double idf=0.0;
			if(this.IDFmap.containsKey(t))
			{
				idf=this.IDFmap.get(t);
			}
			double score=dLogTF*idf;
			double squaredScore=Math.pow(score, 2);
			dsum+=squaredScore;
		}
		dsqrt=Math.sqrt(dsum);
		
		scoreLowerPart=qsqrt*dsqrt;
		return scoreLowerPart;
		
	}
	
	
	

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	   
		RVSMCalc obj=new RVSMCalc();
		String soureInfo="/Users/user/Documents/Ph.D/2018/Data/SourceForBL/";
		String bugInfo="/Users/user/Documents/Ph.D/2018/Data/BugData/";
		
		obj.LoadSourceTFhm(soureInfo);
		obj.LoadSourceIDF();
		obj.LoadQueryInfo(bugInfo);
		
		
		
		
		obj.calculatRVSM();
		
		
		
	}

}
