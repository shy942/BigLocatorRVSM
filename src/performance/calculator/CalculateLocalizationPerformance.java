package performance.calculator;

import java.util.ArrayList;
import java.util.HashMap;



import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class CalculateLocalizationPerformance {

	/**
	 * @param args
	 */
	public static HashMap<String, ArrayList<String>> TrueSetResults;
	public static HashMap<String, ArrayList<String>> ActualResultSets;
	
	
	//public CalculateLocalizationPerformance(HashMap<String, ArrayList<String>> retTrueSetResultsyList)
	//{
		//this.retActualResultSets=retActualResultSets;
	//}
	
	public CalculateLocalizationPerformance(HashMap<String, ArrayList<String>> retTrueSetResults, HashMap<String, ArrayList<String>> retActualResultSets)
	{
		this.ActualResultSets=retActualResultSets;
		this.TrueSetResults=retTrueSetResults;
	}
	
	public CalculateLocalizationPerformance(HashMap<String, ArrayList<String>> retTrueSetResults)
	{
		this.TrueSetResults=retTrueSetResults;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CalculateLocalizationPerformance obj=new CalculateLocalizationPerformance(TrueSetResults,ActualResultSets);		
		
		obj.TrueSetResults=obj.RetrieveTrueSetsType2("./data/gitInfoNew.txt");
		//MiscUtility.showResult(10, TrueSetResults);
		
		//MiscUtility.convertNumbertoSourceFile("./data/Results/finalResultTest1.txt","./data/changeset-pointer/ID-SourceFile.txt","./data/Results/FinalResultSidTest1.txt");
		
		
		obj.ActualResultSets=RetrieveFinalSets("./data/Results/BugLocatorJuly30-night-Avg.txt"); 	
	
		
		int top_n=5;
		//ComputePerformant(1);
		//ComputePerformant(5);
		ComputePerformant(10);
	
	}

	public void ComputeBugLocatorPerformance(String goldSetFile, String resultFromBugLocator)
	{
		ArrayList<String> goldfSet=ContentLoader.getAllLinesList(goldSetFile);
	}
	
	
	private void readBugLocatorResult(String filePath)
	{
		ArrayList<String> resultList=ContentLoader.getAllLinesList(filePath);
		
		for(int i=0;i<resultList.size();i++)
		{
			String line=resultList.get(i);
			String [] spilter=line.split(",");
			String bugID=spilter[0];
	
		}
	}
	
	private static HashMap<String, ArrayList<String>> RetrieveFinalSets(
			String inFile) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
		ArrayList <String> list =new ArrayList<String>();
		list=ContentLoader.readContent(inFile);
	    for(String line: list)
	    {
	    	//System.out.println(line);
	    	String [] spilter=line.split(",");
	    	String bugID=spilter[0];
	    	String file=spilter[1];
	    	ArrayList<String> fileAddress=new ArrayList<String>();
	    	if(hm.containsKey(bugID))
	    	{
	    		fileAddress=hm.get(bugID);
	    		fileAddress.add(file);
	    	}
	    	else
	    	{
	    		fileAddress.add(file);
	    	}
	    	hm.put(bugID, fileAddress);
	    }
		return hm;
	}
	
	
	
	public static int nthOccurrence(String s, char c, int occurrence) {
	    return nthOccurrence(s, 0, c, 0, occurrence);
	}

	public static int nthOccurrence(String s, int from, char c, int curr, int expected) {
	    final int index = s.indexOf(c, from);
	    if(index == -1) return -1;
	    return (curr + 1 == expected) ? index : 
	        nthOccurrence(s, index + 1, c, curr + 1, expected);
	}

	private static void ComputePerformant(int top_n) {
		// TODO Auto-generated method stub
		ArrayList<String> finalRankedResult=new ArrayList<>(); 
		
		int no_of_bug_matched=0;
		double rank_i=0;
		double MRR_i=0;
		double MRR=0;
		
		double MAP=0.0;
		double AP_sum=0.0;
		int found=0;
		int total_found=0;
	
		for(String key:ActualResultSets.keySet())
		{
			String bugIDfromRetActualResultSets=key;//Get the bugID
	        ArrayList <String> listFromActualResult= ActualResultSets.get(key); //Get the experimented results
	        
	        
	        if(TrueSetResults.containsKey(bugIDfromRetActualResultSets))// Truth set contains the bug
	        {
	        	ArrayList <String> listFromTrueSets=TrueSetResults.get(bugIDfromRetActualResultSets);
	        	//if(listFromTrueSets.size()>=10)
	        	{
	        		int found_at_least1=0;
	        	    no_of_bug_matched++;
	        	    //Now work on the results and find top N, AP
	        	    Double AP=0.0; //Calculate avareage precision
	        	    int indexBugRank=0;;
	        	    found=0;
	        	    
	        	    //Loop for Top 1 and Top5
	        	   for(int i=0;i<10;i++)
	        		//for(int i=0;i<listFromActualResult.size()-1;i++)
	        		{
	        			String resultedFilePath=listFromActualResult.get(i);
	        			//if(found_at_least1>0)break;
	        			
	        			for(int j=0;j<listFromTrueSets.size();j++)
	        			{
	        				String trueSetsFilePath=listFromTrueSets.get(j);
	        				if(resultedFilePath.equalsIgnoreCase(trueSetsFilePath)==true)
	        				{
	        					indexBugRank++;
	        					found_at_least1++;
	        					found=1;
	        					AP+=Double.valueOf(indexBugRank)/(Double.valueOf(i)+1.0);
	        					MRR_i=MRR_i+(1.0/Double.valueOf(i+1));
	        					//System.out.println((i+1)+" "+MRR_i);
	        					finalRankedResult.add(bugIDfromRetActualResultSets+","+trueSetsFilePath+","+i+","+0.0);
	        					break;
	        				}
	        			}
	        		
	        		}
	        		if(AP>0)AP_sum+=AP/Double.valueOf(indexBugRank);
	        		
	        		if(found==1) 
	        		{
	        			total_found+=1;
	        		}
	        		else
	        		{
	        			finalRankedResult.add(bugIDfromRetActualResultSets+","+"N/A"+","+"-1"+","+0.0);
	        		}
	        	}
	        }
	        
	    }
	    System.out.println("==================================");
	    MAP=AP_sum/no_of_bug_matched;
	  
	    System.out.println("Total bug: "+no_of_bug_matched);
	    System.out.println("Total found: "+total_found);
	    System.out.println("Top "+top_n+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
	    System.out.println("MAP: "+MAP+"----------"+"MRR: "+MRR_i/no_of_bug_matched);
	    ContentWriter.writeContent("./data/Results/test1-rankedResult.txt", finalRankedResult);
	}

	private static HashMap<String, ArrayList<String>> RetrieveTrueSets(
			String inFile) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
		ArrayList <String> list =new ArrayList<String>();
		list=ContentLoader.readContent(inFile);
	    for(String line: list)
	    {
	    	//System.out.println(line);
	    	String [] spilter=line.split(":");
	    	String bugID=spilter[0];
	    	String noOfFile="";
			if(spilter.length>=2) noOfFile=spilter[1];
			if(noOfFile.equalsIgnoreCase("0"))System.out.println("############### "+bugID+": "+noOfFile);
	    	ArrayList<String> fileAddress=new ArrayList<String>();
	    	int t=0;
	    	if(spilter.length>2)
	    	{
	    		
	    		for(int i=2;i<spilter.length;i++)
	    		{
	    			fileAddress.add(spilter[i].toLowerCase().trim());
	    		}
	    		t++;
	    	}
	    	if(t>0)hm.put(bugID, fileAddress);
	    	
	    }
		return hm;
	}

	
	
	public static HashMap<String, ArrayList<String>> RetrieveTrueSetsType2(
			String inFile) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>>hm=new HashMap<>();
		ArrayList<String> lines = ContentLoader
				.readContent(inFile);
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
				hm.put(bugID, tempList);
				}
				// now update the counter
				i = i + filecount;
				i++;
			}
		}
		System.out.println("Changeset reloaded successfully for :"
				+ hm.size());
		return hm;
	}
}
