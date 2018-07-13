package rvsm.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lucene.VSMCalculator;
import utility.ContentLoader;


public class DFCalc {

	String sourceCodeDir;
	public DFCalc(String sourceCodeDir)
	{
		this.sourceCodeDir=sourceCodeDir;
	}
	
	
	public void documentFrequencyCalculator()
	{
		String content="";
		File[] sourceCodeFiles = new File(this.sourceCodeDir).listFiles();
		
		//Calculating document frequeny for all terms in all documents
		ArrayList <String>list =new ArrayList<String>();
		HashMap<String, Double> ddfMap=new HashMap<String, Double>();
		
		for (File sourceCodeFile : sourceCodeFiles) {
			String singleBugContent=ContentLoader.readContentProcessedSourceCode(sourceCodeFile
					.getAbsolutePath());
			list.add(singleBugContent);
			System.out.println(singleBugContent);
		}
		String allBugContent=utility.MiscUtility.list2Str(list);
		System.out.println("allBugContent-------------------------------------------------------");
		//System.out.println(allBugContent);
		
		VSMCalculator vcalcAll = new VSMCalculator(allBugContent);
		HashMap<String, Integer> dtfMapAll = vcalcAll.getTF();
		ddfMap = vcalcAll.getDF();
		//System.out.println(ddfMap);
		utility.MiscUtility.showResult(20, ddfMap);
		//utility.ContentWriter.writeToFileforHashMapContent("","DocumentFrequency"+"/"+"1onOct5.txt", ddfMap);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DFCalc("/Users/user/Documents/Backup/ProcessedFiles/").documentFrequencyCalculator();
	}

}
