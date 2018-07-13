package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MiscUtility {

	public static void WriiteMappingDatabase(String outFile, HashMap <String, ArrayList<String>> keywordFileMap)
	{
		//int i=0;
		ArrayList <String> list=new ArrayList<String>();
		for(String keyword:keywordFileMap.keySet())
		{
			//i++;
			String content=keyword;
			ArrayList <String> fileList=keywordFileMap.get(keyword);
			//int count=0;
			for(String file:fileList)
			{
				content+= ":"+file;
				//count++;
				//if(count>20)break;
			}
			list.add(content);
			//if(i>3) break;
		}
		//ContentWriter.writeFormulatedQueriesContent(StaticData.OUTPUTFOLDER+"/processedMapDatabaseForAllcontent1000.txt", list);
		ContentWriter.writeFormulatedQueriesContent(outFile, list);
	}
	public static String list2Str(ArrayList<String> list) {
		String content = new String();
		for (String word : list) {
			if(word!=" ")
				{
					if(word!="\n")
					{
						content += word+ " ";
					}
					else
					{
						content += word + "";
					}
				}
		}
		return content.trim();
	}

	public static String hashMap2Str(HashMap hm, int n) {
		String content = n + ",";
		Iterator it = hm.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			i++;
			if (i > n)
				break;
			Map.Entry pair = (Map.Entry) it.next();
			String keyword = pair.getKey().toString();
			content += keyword + ",";
		}
		return content;
	}
	
	public static String hashMap2Str(HashMap hm) {
		String content="";
		Iterator it = hm.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			i++;
			if (i > hm.size())
				break;
			Map.Entry pair = (Map.Entry) it.next();
			String key = pair.getKey().toString();
			//String value=(String) pair.getValue();
			content += key + " ";
		}
		return content;
	}

	public static String hashMap2String(HashMap hm)
	{
		String content="";
		Iterator it = hm.entrySet().iterator();
		//while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String keyword = pair.getKey().toString();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			content=pair.getValue().toString();
		//}
		
		return content;
		
	}
	
	
	public static HashMap sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	public static void showResult(int n, HashMap hm) {
		int i = 0;
		Iterator it = hm.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String keyword = pair.getKey().toString();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			i++;
			if (i > n)
				break;
		}
	}
	

	public static ArrayList<String> str2List(String itemLine) {
		String[] items = itemLine.split("\\p{Punct}+|\\d+|\\s+");
		return new ArrayList<String>(Arrays.asList(items));
	}
	
	public static String listInt2Str(ArrayList<Integer> items) {
		String temp = new String();
		for (int d : items) {
			temp += d + " ";
		}
		return temp.trim();
}
	
	
	public void convertSourcetoNumberFile(String inputfileAddress, String IDaddress, String fileName)
    {
    	ArrayList<String> lines=ContentLoader.getAllLinesList(IDaddress);
    	HashMap<String, Integer> IDmap=new HashMap<>();
    	for(String line:lines)
    	{
    		String[] spilter=line.split(":");
    		String sourceID=spilter[0];
    		String sourceFile=spilter[1];
    		IDmap.put(sourceFile.trim(), Integer.valueOf(sourceID));
    	}
    	
        lines=ContentLoader.getAllLinesList(inputfileAddress);
        ArrayList<String> saveContent=new ArrayList<>();
        for(String line:lines)
        {
        	String[] spilter=line.split(",");
        	String fileaddress=spilter[1];
        	
        	if(IDmap.containsKey(fileaddress))
        	{
        		int Sid=IDmap.get(fileaddress);
        		saveContent.add(spilter[0]+","+Sid+","+spilter[2]+","+spilter[3]);
        	}
        }
        ContentWriter.writeContent(fileName, saveContent);
    }
	
	public static void convertNumbertoSourceFile(String inputfileAddress, String IDaddress, String fileName)
	{
		ArrayList<String> lines=ContentLoader.getAllLinesList(IDaddress);
    	HashMap<Integer, String> IDmap=new HashMap<>();
    	for(String line:lines)
    	{
    		String[] spilter=line.split(":"); 
    		String sourceID=spilter[0];
    		String sourceFile=spilter[1];
    		IDmap.put( Integer.valueOf(sourceID),sourceFile.trim());
    	}
    	
        lines=ContentLoader.getAllLinesList(inputfileAddress);
        ArrayList<String> saveContent=new ArrayList<>();
        for(String line:lines)
        {
        	String[] spilter=line.split(",");
        	int Sid=Integer.valueOf(spilter[1]);
        	
        	if(IDmap.containsKey(Sid))
        	{
        		String fileAddress=IDmap.get(Sid);
        		saveContent.add(spilter[0]+","+fileAddress+","+spilter[2]);
        	}
        }
        ContentWriter.writeContent(fileName, saveContent);
	}
	
}
