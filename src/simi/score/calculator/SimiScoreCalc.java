package simi.score.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lucene.VSMCalculator;
import utility.ContentLoader;
import utility.MiscUtility;

public class SimiScoreCalc {


	public String sourceCodeFile;
	public String bugFile;
	public HashMap<String, String> sourceContentHM;
	public HashMap<String, String> bugContentHM;
	public HashMap<String, Double> cosineScoreHM;
	public HashMap<String, Double> similarityScoreHM;
	HashMap<Integer, ArrayList<String>> goldMap;
 	
	/*public SimiScoreCalc(String sourceCodeFile, String queryContent, String bugFile)
	{
		this.sourceCodeFile=sourceCodeFile;
		this.queryContent=queryContent;
		this.bugFile=bugFile;
		this.sourceContentHM=new HashMap<>();
		this.bugContentHM=new HashMap<>();
		this.cosineScoreHM=new HashMap<>();
		this.similarityScoreHM=new HashMap<>();
	}*/
	
	public SimiScoreCalc(String sourceCodeFile, String bugFile)
	{
		this.sourceCodeFile=sourceCodeFile;
		//this.queryContent=queryContent;
		this.bugFile=bugFile;
		this.sourceContentHM=new HashMap<>();
		this.bugContentHM=new HashMap<>();
		this.cosineScoreHM=new HashMap<>();
		this.similarityScoreHM=new HashMap<>();
	}
	
	protected HashMap<Integer, ArrayList<String>> loadGoldsetMap(String goldFile) {
		HashMap<Integer, ArrayList<String>> goldMap = new HashMap<>();
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(goldFile);
		for (int i = 0; i < lines.size();) {
			String[] parts = lines.get(i).split("\\s+");
			if (parts.length == 2) {
				int bugID = Integer.parseInt(parts[0].trim());
				int bugCount = Integer.parseInt(parts[1].trim());
				for (int j = i + 1; j <= i + bugCount; j++) {
					if (goldMap.containsKey(bugID)) {
						ArrayList<String> temp = goldMap.get(bugID);
						temp.add(lines.get(j).trim());
						goldMap.put(bugID, temp);
					} else {
						ArrayList<String> temp = new ArrayList<>();
						temp.add(lines.get(j).trim());
						goldMap.put(bugID, temp);
					}
				}
				i = i + bugCount + 1;
			}
		}
		return goldMap;
	}
	
	
	public HashMap<String, String> LoadFiles(String filePath)
	{
		HashMap<String, String> hm=new HashMap<>();
		File[] sourceCodeFiles = new File(filePath).listFiles();
		System.out.println("Total number of source code files: "+sourceCodeFiles.length);
		int noOfTotalDocument=sourceCodeFiles.length;
		
		for (File sourceCodeFile : sourceCodeFiles) {
			String sourceCodeContent = ContentLoader.readContentSimple(sourceCodeFile
				.getAbsolutePath());
			hm.put(sourceCodeFile.getName(), sourceCodeContent);
			
		}
		//MiscUtility.showResult(10, hm);
		return hm;
	}
	 
	public void cosineSimiCalculator(String queryContent) {
			for (String bugID : this.bugContentHM.keySet()) {
			
				String str1=queryContent;
				String str2=this.bugContentHM.get(bugID);
				double cosineSimilarity = new CosineSimilarity().similarity(
						str1,str2);
						//queryContent, bugContent);
				//System.out.println(cosineSimilarity);
				if (cosineSimilarity > 0) {
					String bugIDfound = bugID;
					
					//System.out.println(bugIDfound+" "+cosineSimilarity);
					this.cosineScoreHM.put(bugIDfound, cosineSimilarity);
				}
			}
	}
	
	
	public void SimilarityCalc()
	{
		for(String bugID:this.cosineScoreHM.keySet())
		{ 
			//System.out.println(bugID);
			if(bugID.equalsIgnoreCase(".DS_Store")==false){
			int ID=Integer.valueOf(bugID.substring(0, bugID.length()-4));
			if(this.goldMap.containsKey(ID)){
				//System.out.println(this.goldMap.get(ID));
				int n_i=this.goldMap.get(ID).size();
				for(String file:this.goldMap.get(ID)){
					if(this.similarityScoreHM.containsKey(file))
					{
						double simiScoreOld=this.similarityScoreHM.get(file);
						double simiScoreSi=simiScoreOld+this.cosineScoreHM.get(bugID)/Double.valueOf(n_i);
						this.similarityScoreHM.put(file, simiScoreSi);
					
					}
					else
					{
						double simiScoreSi=this.cosineScoreHM.get(bugID)/Double.valueOf(n_i);
						this.similarityScoreHM.put(file, simiScoreSi);
					}
				}
			}
		}
	}
		MiscUtility.showResult(20, MiscUtility.sortByValues(this.similarityScoreHM));
}
	
	public void modifySoureInfoHM(HashMap<String, HashMap<String, Double>> SourceTFInfo)
	{
		for(String sourceID:SourceTFInfo.keySet())
		{
			HashMap<String, Double> hm=SourceTFInfo.get(sourceID);
			String str=MiscUtility.hashMap2Str(hm);
			System.out.println(str);
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimiScoreCalc obj=new SimiScoreCalc("/Users/user/Documents/Ph.D/2018/Data/SourceForBL/", "/Users/user/Documents/Ph.D/2018/Data/BugData/");
		obj.sourceContentHM=obj.LoadFiles(obj.sourceCodeFile);
		obj.bugContentHM=obj.LoadFiles(obj.bugFile);
		obj.goldMap=obj.loadGoldsetMap("./Data/gitInfoNew.txt");
		obj.cosineSimiCalculator("");
		obj.SimilarityCalc();
	}

	
	
	
	
}
