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
	public HashMap<String, ArrayList<String>> gitResults;
	public HashMap<String, ArrayList<String>> ActualResultSets;
	public String gitPath;
	public String actualSetPath;
	
	
	
	public CalculateLocalizationPerformance(String gitPath, String actualSetPath)
	{
		this.gitPath=gitPath;
		this.actualSetPath=actualSetPath;
		this.ActualResultSets=new HashMap<>();
		this.gitResults=new HashMap<>();
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		CalculateLocalizationPerformance obj=new CalculateLocalizationPerformance("./data/gitInfoNew.txt","./data/Results/100-1000-rankedResult.txt");		
		obj.gitResults=obj.RetrieveTrueSetsType2(obj.gitPath);
		obj.ActualResultSets=obj.RetrieveFinalSets(obj.actualSetPath); 	
	
		
		//Compute TopK percentage
		HashMap<String, ArrayList<String>>finalRankedResult=obj.ComputePerformancePercent(10,obj);
		MiscUtility.showResult(100, finalRankedResult);
		
		//Compute MAP
		
		//Comupte MRR
	}

	
	private void ComputeMAP(HashMap<String, ArrayList<String>>finalRankedResult)
	{
		Double AP=0.0;
		for(String bugID:finalRankedResult.keySet())
		{
			Double Prec=0.0;
			ArrayList<String> rankedList=finalRankedResult.get(bugID);
			for(String rankstr:rankedList)
			{
				int rank=Integer.valueOf(rankstr);
				
			}
		}
	}
	
	
	
	private HashMap<String, ArrayList<String>> RetrieveFinalSets(
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
	
	
	

	private HashMap<String, ArrayList<String>> ComputePerformancePercent(int top_n, CalculateLocalizationPerformance obj) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> finalRankedResult=new HashMap(); 
		
		int no_of_bug_matched=0;
		
		int total_found=0;
	
		for(String bugID:obj.ActualResultSets.keySet())
		{
			int found=0;
			ArrayList <String> listFromActualResult= obj.ActualResultSets.get(bugID); //Get the experimented results
	        if(obj.gitResults.containsKey(bugID))// Truth set contains the bug
	        {
	        	ArrayList <String> listFromTrueSets=obj.gitResults.get(bugID);
	        	no_of_bug_matched++;
	        	//Look for top-K
	        	int count=0;
	        	for(int i=0;i<listFromActualResult.size();i++){
	        		count++;
	        		if(count>10) {
	        			System.out.println(count+"----------------------------------------------------");
	        			break;
	        		}
	        		
	        		String resultedFilePath=listFromActualResult.get(i);
	        			for(int j=0;j<listFromTrueSets.size();j++){
	        				String trueSetsFilePath=listFromTrueSets.get(j);
	        				if(resultedFilePath.equalsIgnoreCase(trueSetsFilePath)==true){
	        					found=1;
	        					System.out.println(bugID+" "+resultedFilePath+" "+(j+1));
	        					ArrayList<String> resultList;
	        					if(finalRankedResult.containsKey(bugID))
	        					{
	        						resultList=finalRankedResult.get(bugID);
	        						resultList.add(String.valueOf(j+1));
	        					}
	        					else
	        					{
	        						resultList=new ArrayList<>();
	        						resultList.add(String.valueOf(j+1));
	        					}
	        					finalRankedResult.put(bugID, resultList);
	        					break;
	        				}
	        			}
	        		}
	        	}
	        if(found>0)total_found++;
	    }
	    
	    System.out.println("Total bug: "+no_of_bug_matched);
	    System.out.println("Total found: "+total_found);
	    System.out.println("Top "+top_n+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
	    return finalRankedResult;
	    //ContentWriter.writeContent("./data/Results/test1-rankedResult.txt", finalRankedResult);
	}

	

	
	
	public  HashMap<String, ArrayList<String>> RetrieveTrueSetsType2(
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
