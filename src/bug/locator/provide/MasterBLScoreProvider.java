package bug.locator.provide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import rvsm.calculator.RVSMCalcManager;
import simi.score.calculator.SimiScoreCalcManager;
import utility.ContentWriter;
import utility.ItemSorter;
import utility.MiscUtility;

public class MasterBLScoreProvider {

	String srcFolder;
	String bugReportFolder;
	String goldsetFile;

	int maxDocLength = 0;
	int minDocLength = 100000;
	HashMap<String, Integer> docLengthMap;
	HashMap<String, HashMap<String, Double>> masterSourceLogTFMap;
	HashMap<String, HashMap<String, Double>> masterBugReportLogTFMap;
	HashMap<Integer, ArrayList<String>> goldsetMap;
	HashMap<String, Double> idfMap;
	
	//final int TOPK_SIZE = 10;

	public MasterBLScoreProvider(String srcFolder, String bugReportFolder,
			String goldsetFile) {
		this.srcFolder = srcFolder;
		this.bugReportFolder = bugReportFolder;
		this.goldsetFile = goldsetFile;
		this.loadMasterItems();
	}

	protected void loadMasterItems() {
		// loading the corpus level items for only once
		MasterItemLoader masterLoader = new MasterItemLoader(srcFolder,
				bugReportFolder, goldsetFile);
		this.masterSourceLogTFMap = masterLoader.getSourceLogTFAll(srcFolder);
		this.masterBugReportLogTFMap = masterLoader
				.getBugReportLogTFAll(bugReportFolder);
		this.goldsetMap = masterLoader.loadGoldsetMap(goldsetFile);
		this.idfMap = masterLoader.getSourceDocumentIDF();
		this.maxDocLength = masterLoader.getMaxSourceDocLength();
		this.minDocLength = masterLoader.getMinSourceDocLength();
		this.docLengthMap = masterLoader.getSourceDocLength();
	}

	protected HashMap<String, HashMap<String, Double>> collectRVSMScoreMap() {
		// collect RVSM scores
		RVSMCalcManager vsmManager = new RVSMCalcManager(
				this.masterSourceLogTFMap, this.masterBugReportLogTFMap,
				this.idfMap, this.maxDocLength, this.minDocLength,
				this.docLengthMap);
		return vsmManager.calculatRVSMforAll(this.goldsetMap);
	}

	protected HashMap<String, HashMap<String, Double>> collectSimiScoreMap() {
		SimiScoreCalcManager simiManager = new SimiScoreCalcManager(
				this.masterBugReportLogTFMap, goldsetMap);
		return simiManager.collectSimiScoreMap();
	}

	protected HashMap<String, Double> getRankedResults(String bugReportKey,
			HashMap<String, Double> vsmScoreMap,
			HashMap<String, Double> simiScoreMap, double ALPHA, double BETA, int TOPK_SIZE) {
		HashSet<String> candidates = new HashSet<>();
		candidates.addAll(vsmScoreMap.keySet());
		candidates.addAll(simiScoreMap.keySet());
		int bugID = Integer.parseInt(bugReportKey.split("\\.")[0]);
		HashMap<String, Double> srcFileScoreMap = new HashMap<>();
		for (String srcFileKey : candidates) {
			double myScore = 0;
			if (vsmScoreMap.containsKey(srcFileKey)) {
				myScore = vsmScoreMap.get(srcFileKey) *ALPHA;
			}
			if (simiScoreMap.containsKey(srcFileKey)) {
				myScore += simiScoreMap.get(srcFileKey)*BETA;
			}
			srcFileScoreMap.put(srcFileKey, myScore);
		}
		// now do the sorting
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(srcFileScoreMap);
		HashMap<String, Double> rankedResults = new HashMap<String, Double>();
		for (Map.Entry<String, Double> entry : sorted) {
			//String line = bugID + "," + entry.getKey() + "," + entry.getValue();
			rankedResults.put(entry.getKey(), entry.getValue());
			if (rankedResults.size() == TOPK_SIZE)
				break;
		}
		return rankedResults;
	}

	protected ArrayList<String> getRankedResultsInList(String bugReportKey,
			HashMap<String, Double> vsmScoreMap,
			HashMap<String, Double> simiScoreMap, double ALPHA, double BETA, int TOPK_SIZE) {
		HashSet<String> candidates = new HashSet<>();
		candidates.addAll(vsmScoreMap.keySet());
		candidates.addAll(simiScoreMap.keySet());
		int bugID = Integer.parseInt(bugReportKey.split("\\.")[0]);
		HashMap<String, Double> srcFileScoreMap = new HashMap<>();
		for (String srcFileKey : candidates) {
			double myScore = 0;
			if (vsmScoreMap.containsKey(srcFileKey)) {
				myScore = vsmScoreMap.get(srcFileKey) *ALPHA;
			}
			if (simiScoreMap.containsKey(srcFileKey)) {
				myScore += simiScoreMap.get(srcFileKey) * BETA;
			}
			srcFileScoreMap.put(srcFileKey, myScore);
		}
		// now do the sorting
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(srcFileScoreMap);
		ArrayList<String> rankedResults = new ArrayList();
		for (Map.Entry<String, Double> entry : sorted) {
			String line = bugID + "," + entry.getKey() + "," + entry.getValue();
			rankedResults.add(line);
			if (rankedResults.size() == TOPK_SIZE)
				break;
		}
		return rankedResults;
	}
	
	public void produceBugLocatorResults(String outputFilePath, double ALPHA, double BETA, int TOPK_SIZE) {
		// produce bug locator results
		HashMap<Integer, HashMap<String, Double>> FINALRESULT=new HashMap<Integer, HashMap<String, Double>>();
		HashMap<String, HashMap<String, Double>> rVSMMapAll = collectRVSMScoreMap();
		HashMap<String, HashMap<String, Double>> simiMapAll = collectSimiScoreMap();
		ArrayList<String> masterResultList = new ArrayList<>();
		//int i=0;
		for (String bugReportKey : rVSMMapAll.keySet()) {
			//i++;
			//if(i>100)break;
			HashMap<String, Double> rVSM = rVSMMapAll.get(bugReportKey);
			HashMap<String, Double> simi = simiMapAll.get(bugReportKey);
			ArrayList<String> rankedResults = getRankedResultsInList(bugReportKey,
					rVSM, simi, ALPHA, BETA, TOPK_SIZE);
			masterResultList.addAll(rankedResults); 
			
			
		}
		
		ContentWriter.writeContent(outputFilePath, masterResultList);
		
	}
	
	public HashMap<Integer, HashMap<String, Double>> produceBugLocatorResultsForMyTool(double ALPHA, double BETA, int TOPK_SIZE) {
		// produce bug locator results
		HashMap<Integer, HashMap<String, Double>> FINALRESULT=new HashMap<Integer, HashMap<String, Double>>();
		HashMap<String, HashMap<String, Double>> rVSMMapAll = collectRVSMScoreMap();
		HashMap<String, HashMap<String, Double>> simiMapAll = collectSimiScoreMap();
		
		
		for (String bugReportKey : rVSMMapAll.keySet()) {
			
			HashMap<String, Double> rVSM = rVSMMapAll.get(bugReportKey);
			HashMap<String, Double> simi = simiMapAll.get(bugReportKey);
			HashMap<String, Double> rankedResults = getRankedResults(bugReportKey,
					rVSM, simi, ALPHA, BETA, TOPK_SIZE);
			 
			int bugId=Integer.valueOf(bugReportKey.substring(0, bugReportKey.length()-4));
			FINALRESULT.put(bugId, rankedResults);
		}
		
		MiscUtility.showResult(10, FINALRESULT);
		return FINALRESULT;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis(); 
		int test=9;
		//For mac
		//String bugReportFolder = "/Users/user/Documents/workspace-2016/QueryReformulation/Data/testsetForBL/test1/";
		//String sourceFolder = "/Users/user/Documents/Ph.D/2018/Data/ProcessedSourceForBL/";
		//For Windows
		//String bugReportFolder = "C:\\Users\\Mukta\\Workspace-2018\\QueryReformulation\\Data\\testsetForBL\\test1\\";
		String bugReportFolder = "C:\\Users\\Mukta\\Workspace-2018\\QueryReformulation\\data\\testsetForBL\\test"+test+"\\";
		String sourceFolder = "E:\\PhD\\Data\\NotProcessedSourceMethodLevel\\";
		String goldsetFile = "./Data/gitInfoNew.txt";
		String outputFilePath="./Data/Results/Bug-Locator-August18-test"+test+".txt";
		double ALPHA=0.8;
		double BETA=0.2;
		int TOPK_SIZE = 10;
		new MasterBLScoreProvider(sourceFolder, bugReportFolder, goldsetFile)
				.produceBugLocatorResults(outputFilePath, ALPHA, BETA, TOPK_SIZE);
		long end = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (end - start) / 1000 + " s");
	}
}
