package performance.calculator;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class TestingPerformanceCalculator {

	public String goldSetFile;
	public String testimgResultFile;
	HashMap<String, ArrayList<String>> goldStMap;
	HashMap<String, ArrayList<String>> finalResult;
	public TestingPerformanceCalculator(String goldSetFile, String testimgResultFile)
	{
		this.goldSetFile=goldSetFile;
		this.testimgResultFile=testimgResultFile;
		this.goldStMap=new HashMap<>();
		this.finalResult=new HashMap<>();
	}
	
	public HashMap<String, ArrayList<String>> LoadGoldSet()
	{
		HashMap<String, ArrayList<String>> goldStMap=new HashMap<>();
		ArrayList<String> lines=ContentLoader.getAllLinesList(this.goldSetFile);
		for (int i = 0; i < lines.size();) {
			String currentLine = lines.get(i);
			String[] items = currentLine.split("\\s+");
			if (items.length == 2) {
				String bugID = items[0].trim();
				int filecount = Integer.parseInt(items[1].trim());
				if(filecount>0)
				{
				ArrayList<String> tempList = new ArrayList<>();
				for (int currIndex = i + 1; currIndex <= i + filecount; currIndex++) {
					if(!tempList.contains(lines.get(currIndex)))tempList.add(lines.get(currIndex));
				}
				// now store the change set to bug
				goldStMap.put(bugID, tempList);
				}
				// now update the counter
				i = i + filecount;
				i++;
			}
		}
		return goldStMap;
	}
	public HashMap<String, ArrayList<String>> LoadTestingResult()
	{
		HashMap<String, ArrayList<String>> finalTestingResult=new HashMap<>();
		ArrayList<String> lines=ContentLoader.getAllLinesList(this.testimgResultFile);
		
		for(String line:lines)
		{
			String[] spilter=line.split(",");
			String bugID=spilter[0];
			String Sid=spilter[1];
			String score=spilter[2];
			if(finalTestingResult.containsKey(bugID))
			{
				ArrayList<String> tempList=finalTestingResult.get(bugID);
				tempList.add(Sid+","+score);
				finalTestingResult.put(bugID, tempList);
			}
			else
			{
				ArrayList<String> tempList=new ArrayList<>();
				tempList.add(Sid+","+score);
				finalTestingResult.put(bugID, tempList);
			}
		}
		
		
		return finalTestingResult;
	}
	
	public ArrayList<String> produceRankedResult(int topK)
	{
		ArrayList<String> rankedResult=new ArrayList<>();
		
		for(String queryID:this.finalResult.keySet())
		{
			ArrayList<String> finalList=this.finalResult.get(queryID);
			if(this.goldStMap.containsKey(queryID.trim()))
			{
				//System.out.println(this.goldStMap.get(queryID));
				//For 10 final result file 
				for(String file:finalList)
				{
					String resultFile=file.split(",")[0];
					ArrayList<String> goldResultFiles=this.goldStMap.get(queryID);
					for(int k=0;k<goldResultFiles.size();k++)
					{
						 //if(k>10) break;
						 String goldFile=goldResultFiles.get(k);
						 if(resultFile.equalsIgnoreCase(goldFile))
						 {
							 System.out.println(queryID+"    "+"F.............."+k);
							 rankedResult.add(queryID+","+file.split(",")[0]+","+k+","+file.split(",")[1]);
							 break;
						 }
					}
				}
			}
		}
		return rankedResult;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestingPerformanceCalculator obj= new TestingPerformanceCalculator("./data/gitInfoNew.txt", "data/Results/FinalResultSidTest1.txt");
		//Read Gold set
		obj.goldStMap=obj.LoadGoldSet();
		//MiscUtility.showResult(10, obj.goldStMap);
		//Resd output file/ test result file
		obj.finalResult=obj.LoadTestingResult();
		//MiscUtility.showResult(20, obj.finalResult);
		
		
		//Create a ranked list
		//obj.produceRankedResult(10);
		ArrayList<String> rankedFinalTestingResult=obj.produceRankedResult(10);
		ContentWriter.writeContent("./data/testing1RankedResult.txt", rankedFinalTestingResult);
		//call another class BLPerformanceCalc to compute
		
		
	}

}
