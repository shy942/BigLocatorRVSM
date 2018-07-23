package simi.score.calculator;

import java.io.File;
import java.util.HashMap;

import lucene.VSMCalculator;
import utility.ContentLoader;
import utility.MiscUtility;

public class SimiScoreCalc {

	public String queryContent;
	public String sourceCodeFile;
	public HashMap<String, String> sourceContentHM;
	
	public SimiScoreCalc(String sourceCodeFile, String queryContent)
	{
		this.sourceCodeFile=sourceCodeFile;
		this.queryContent=queryContent;
		this.sourceContentHM=new HashMap<>();
	}
	
	public void LoadSourceCodeFiles()
	{
		File[] sourceCodeFiles = new File(this.sourceCodeFile).listFiles();
		System.out.println("Total number of source code files: "+sourceCodeFiles.length);
		int noOfTotalDocument=sourceCodeFiles.length;
		
		for (File sourceCodeFile : sourceCodeFiles) {
			String sourceCodeContent = ContentLoader.readContentSimple(sourceCodeFile
				.getAbsolutePath());
			this.sourceContentHM.put(sourceCodeFile.getName(), sourceCodeContent);
			
		}
		MiscUtility.showResult(10, this.sourceContentHM);
	}
	 
	public void cosineSimiCalculator() {
			for (String key : this.sourceContentHM.keySet()) {
			
				String str1=this.queryContent;
				String str2=this.sourceContentHM.get(key);
				double cosineSimilarity = new CosineSimilarity().getSimilarity(
						str1,str2);
						//queryContent, bugContent);
				//System.out.println(cosineSimilarity);
				if (cosineSimilarity > 0) {
					String bugFileID = bugFile.getName();
					String bugID = bugFileID.substring(0,
							bugFileID.length() - 4);
					//System.out.println(bugID+" "+cosineSimilarity);
					this.hm.put(bugID, cosineSimilarity);
					//System.out.println(bugID+" "+cosineSimilarity);
				}
				//System.out.println(bugFile+" is done");
			}
			String content=simiScoreCalculator();
			String queryID=queryFile.getName();
			
			
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
}
