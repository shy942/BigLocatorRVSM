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
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;


public class RVSMCalc {

	
	
	
	
	HashMap<String, Double> IDFmap;
	HashMap<String, HashMap<String, Double>> SourceTFInfo;
	HashMap<String, String> QueryInfo;
    
	public RVSMCalc() {
		
		
		this.IDFmap=new HashMap<>();
		this.SourceTFInfo=new HashMap<>();
		this.QueryInfo=new HashMap<>();
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
			this.SourceTFInfo.put(sourceCodeFile.getName(), logTF);
			
		}
		//MiscUtility.showResult(10, this.SourceTFInfo);
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
		MiscUtility.showResult(10, this.QueryInfo);
	}
	
	
	protected void calculatRVSM() {
	

		/*
		
		String getMaxMin=retrieveMaxMinLength();
		String[] spilter=getMaxMin.split(",");
		double maxLength=Double.valueOf(spilter[0]);
		double minLength=Double.valueOf(spilter[1]);
		//System.out.println(maxLength+" "+minLength);
		
		int no_of_query_done=0;
		//ArrayList<String> totalResult=new ArrayList<>();
		//File[] queryFiles = new File(this.queryDir).listFiles();
		//System.out.println("Total no. of query: "+queryFiles.length);
		for(String queryFileSingle : this.QueryInfo.keySet()){
			
			
			
			Name().length()-4);
			this.queryContent= ContentLoader.readContentSimple(queryPath);
			// collect metrics for the query
		
			VSMCalculator vcalc = new VSMCalculator(this.queryContent);
			HashMap<String, Integer> qtfMap = vcalc.getTF();
			
			
		    File[] sourceCodeFiles = new File(this.sourceCodeDir).listFiles();
			System.out.println("Total number of source code files: "+sourceCodeFiles.length);
			int noOfTotalDocument=sourceCodeFiles.length;
			//for (File sourceCodeFile : sourceCodeFiles) {
				//String sourceCodeContent = ContentLoader.readContentProcessedSourceCode(sourceCodeFile
					//.getAbsolutePath());
				//String acrualFilePath=ContentLoader.returnFilePath(sourceCodeFile.getAbsolutePath());
				//String acrualFilePath=ContentLoader.extractFileAddress(sourceCodeFile.getAbsolutePath());
				//System.out.println("acrualFilePath	"+acrualFilePath);
			
				VSMCalculator vcalc2 = new VSMCalculator(sourceCodeContent);
				HashMap<String, Integer> dtfMap = vcalc2.getTF();
				//HashMap<String, Double> ddfMap = vcalc2.getDF();
				int dTotalTerms=vcalc2.getTotalNoTerms();
			
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
					double scoreUpperPart=calculateUpperPart(noOfTotalDocument,qtfMap,dtfMap,ddfMap,commonSet);
					double scooreLowerPart=calculateLowerPart(noOfTotalDocument,qtfMap,dtfMap,ddfMap);
					double score=0.0;
					if(scoreUpperPart!=0&&scooreLowerPart!=0) score=(gTerms*scoreUpperPart)/scooreLowerPart;
				
					this.hm.put(acrualFilePath, score);
				}
			
			//}
			HashMap<String, Double> sortedResult=retrieveSortedTopNResult(hm);
			
			ArrayList<String> tempResults=new ArrayList<String>();
			int resultCount=0;
			String resultContent=queryFileSingle.getName().substring(0, queryFileSingle.getName().length()-4);
			for(String srcFile: sortedResult.keySet())
			{
				if(!tempResults.contains(srcFile))
				{
					tempResults.add(srcFile);
					resultContent+=":"+srcFile;
					resultCount++;
					if(resultCount>10)break;
				}
			}
			
			hm.clear();
		}
		//return this.hm;
		
		ContentWriter.writeContent("F:/BigDataProject/ResultOutput/Oct06resultFor2011.txt", totalResult);*/
	}
	
	public HashMap retrieveSortedTopNResult(HashMap hm)
	{
		HashMap sortedResult=new HashMap();
		sortedResult=MiscUtility.sortByValues(hm);
		//MiscUtility.showResult(20,sortedResult);
		return sortedResult;
	}
	
	
	public HashMap retrieveDocumentFrequency()
	{
		HashMap<String, Double> hm=new HashMap<String, Double>();
		String inFile="DocumentFrequency"+"/"+"1onOct5.txt";
		
		String content=utility.ContentLoader.readContentSimple(inFile);
		String [] spilter=content.split("\n");
		for(int i=0;i<spilter.length;i++)
		{
			String line=spilter[i];
			String[] spilter2=line.split(",");
			String key=spilter2[0];
			Double DFvalue=Double.valueOf(spilter2[1]);
			if(!hm.containsKey(key))
			{
				hm.put(key, DFvalue);
			}
		}
		
		return hm;
	}
	
	public String retrieveMaxMinLength()
	{
		String content="";
		String inFile="MaxMinLength.txt";
		content=utility.ContentLoader.readContentSimple(inFile);
		return content;
	}
	
	protected double calculateUpperPart(int noOfTotalDocument, HashMap<String,Integer> qtfMap, HashMap<String,Integer> dtfMap,
		HashMap<String,Double> ddfMap, HashSet<String> commons)
	{
		double scoreUpperPart=0.0;
		double sum=0;
		for(String t:commons){
			double qTF=(double)qtfMap.get(t);
			double first=Math.log(qTF)+1;
			double dTF=(double)dtfMap.get(t);
			double second=Math.log(dTF)+1;
			double third=0;
			
			if(ddfMap.containsKey(t))third=2*Math.log(noOfTotalDocument/ddfMap.get(t)); 
			 
			double score=first*second*third;
			sum+=score;
		}
		scoreUpperPart=sum;
		return scoreUpperPart;
	}
	
	protected double calculateLowerPart(int noOfTotalDocument, HashMap<String,Integer> qtfMap, HashMap<String,Integer> dtfMap,
			HashMap<String,Double> ddfMap)
	{
		double scoreLowerPart=0.0;
		//For Query q
		double qsqrt=0;
		double qsum=0;
		HashSet<String> qset = new HashSet<>(qtfMap.keySet());
		for(String t:qset)
		{
			double qTF=(double)qtfMap.get(t);
			double first=Math.log(qTF)+1;
			double second=0.0;
			if(ddfMap.containsKey(t))
			{
				second=Math.log(noOfTotalDocument/ddfMap.get(t));
			}
			double score=first*second;
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
			double dTF=(double)dtfMap.get(t);
			double first=Math.log(dTF)+1;
			double second=0.0;
			if(ddfMap.containsKey(t))
			{
				second=Math.log(noOfTotalDocument/ddfMap.get(t));
			}
			double score=first*second;
			double squaredScore=Math.pow(score, 2);
			dsum+=squaredScore;
		}
		dsqrt=Math.sqrt(dsum);
		
		scoreLowerPart=qsqrt*dsqrt;
		return scoreLowerPart;
		
	}
	
	public void writeToFile(HashMap hm, String queryID)
	{
		String content="";
		Iterator it = hm.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String filePath=pair.getKey().toString();
	        String rVSMscore=pair.getValue().toString();
	        content+=filePath+","+rVSMscore+"\r\n";
	    }
	   
	}
	

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	
		RVSMCalc obj=new RVSMCalc();
		//obj.calculatRVSM();
		obj.LoadSourceTFhm("/Users/user/Documents/Ph.D/2018/Data/SourceForBL/");
		obj.LoadSourceIDF();
		obj.LoadQueryInfo("/Users/user/Documents/Ph.D/2018/Data/QueryData/");
	}

}
