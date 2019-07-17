package performance.calculator;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.MiscUtility;

public class PerformanceCalculatorPerfect {

	public HashMap<String, ArrayList<String>> gitResultsMap;
	public HashMap<String, ArrayList<String>> resultsMap;
	public String gitPath;
	public String resultPath;
	static HashMap<String, ArrayList<String>> finalRankedResult; 
	
	
	public PerformanceCalculatorPerfect(String gitPath, String resultPath)
	{
		this.gitPath=gitPath;
		this.resultPath=resultPath;
		this.resultsMap=new HashMap<>();
		this.gitResultsMap=new HashMap<>();
		this.finalRankedResult=new HashMap();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int TOP_K=5;
		PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect("E:/PhD/Repo/SWT/data/gitInfo1KBSWT.txt","E:/BugLocator/output/SWT75output.txt");		
		obj.gitResultsMap=obj.getGitOutput(obj.gitPath);
		//MiscUtility.showResult(10, obj.gitResultsMap);
		System.out.println();
		obj.resultsMap=obj.getResults(obj.resultPath); 	
		//MiscUtility.showResult(10, obj.resultsMap);
		System.out.println(obj.gitResultsMap.size());
		System.out.println(obj.resultsMap.size());
		ComputePerformancePercent(TOP_K, obj);
	}

	private static boolean IsMatched(ArrayList <String> resultList, ArrayList <String> gitList, String bugID, int TOP_K)
	{
	
		ArrayList<String> list=new ArrayList<>();
		int found=0;
		int count=0;
		for(String file:resultList){
			count++;
			if(count>TOP_K)break;
			for(String GoldFile:gitList){
				if(GoldFile.equalsIgnoreCase(file.trim())){
					found=1;
					if(finalRankedResult.containsKey(bugID)){
						list=finalRankedResult.get(bugID);
						list.add(String.valueOf(count));
					}
					else{
						list=new ArrayList<>();
						list.add(String.valueOf(count));
					}
					finalRankedResult.put(bugID, list);
				}
			}
		}
		if(found==1) return true;
		else return false;
	}
	
	private static HashMap<String, ArrayList<String>> ComputePerformancePercent(int TOP_K, PerformanceCalculatorPerfect obj) {
		// TODO Auto-generated method stub
		
		
		int no_of_bug_matched=0;
		
		int total_found=0;
	
		for(String bugID:obj.resultsMap.keySet())
		{
			
			ArrayList <String> resultList= obj.resultsMap.get(bugID); //Get the experimented results
	        if(obj.gitResultsMap.containsKey(bugID))// Truth set contains the bug
	        {
	        	ArrayList <String> gitList=obj.gitResultsMap.get(bugID);
	        	no_of_bug_matched++;
	        	
	        	if(IsMatched(resultList,gitList, bugID, TOP_K))total_found++;
	        }
	       
	    }
	    System.out.println(finalRankedResult.size());
	    System.out.println("Total bug: "+no_of_bug_matched);
	    System.out.println("Total found: "+total_found);
	    System.out.println("Top "+TOP_K+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
	    return finalRankedResult;
	    //ContentWriter.writeContent("./data/Results/test1-rankedResult.txt", finalRankedResult);
	}

	

	private HashMap<String, ArrayList<String>> getGitOutput (String gitPath) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				HashMap<String, ArrayList<String>>hm=new HashMap<>();
				ArrayList<String> lines = ContentLoader
						.readContent(gitPath);
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


	private HashMap<String, ArrayList<String>> getResults(String resultPath) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
		ArrayList <String> list =new ArrayList<String>();
		list=ContentLoader.readContent(resultPath);
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

}
