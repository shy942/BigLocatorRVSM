package bug.locator.provide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import rvsm.calculator.RVSMCalcManager;
import simi.score.calculator.SimiScoreCalcManager;
import utility.ContentWriter;
import utility.ItemSorter;

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
	final double ALPHA = 0.8;
	final int TOPK_SIZE = 10;

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

	protected ArrayList<String> getRankedResults(String bugReportKey,
			HashMap<String, Double> vsmScoreMap,
			HashMap<String, Double> simiScoreMap) {
		HashSet<String> candidates = new HashSet<>();
		candidates.addAll(vsmScoreMap.keySet());
		candidates.addAll(simiScoreMap.keySet());
		int bugID = Integer.parseInt(bugReportKey.split("\\.")[0]);
		HashMap<String, Double> srcFileScoreMap = new HashMap<>();
		for (String srcFileKey : candidates) {
			double myScore = 0;
			if (vsmScoreMap.containsKey(srcFileKey)) {
				myScore = vsmScoreMap.get(srcFileKey) * ALPHA;
			}
			if (simiScoreMap.containsKey(srcFileKey)) {
				myScore += simiScoreMap.get(srcFileKey) * (1 - ALPHA);
			}
			srcFileScoreMap.put(srcFileKey, myScore);
		}
		// now do the sorting
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(srcFileScoreMap);
		ArrayList<String> rankedResults = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			String line = bugID + "," + entry.getKey() + "," + entry.getValue();
			rankedResults.add(line);
			if (rankedResults.size() == TOPK_SIZE)
				break;
		}
		return rankedResults;
	}

	protected void produceBugLocatorResults() {
		// produce bug locator results
		HashMap<String, HashMap<String, Double>> rVSMMapAll = collectRVSMScoreMap();
		HashMap<String, HashMap<String, Double>> simiMapAll = collectSimiScoreMap();
		ArrayList<String> masterResultList = new ArrayList<>();
		for (String bugReportKey : rVSMMapAll.keySet()) {
			HashMap<String, Double> rVSM = rVSMMapAll.get(bugReportKey);
			HashMap<String, Double> simi = simiMapAll.get(bugReportKey);
			ArrayList<String> rankedResults = getRankedResults(bugReportKey,
					rVSM, simi);
			masterResultList.addAll(rankedResults); 
		}
		String bugLocatorResultFile = "./Data/Results/Bug-Locator-August02.txt";
		ContentWriter.writeContent(bugLocatorResultFile, masterResultList);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis(); 
		String bugReportFolder = "/Users/user/Documents/Ph.D/2018/Data/ProcessedBugData/";
		String sourceFolder = "/Users/user/Documents/Ph.D/2018/Data/ProcessedSourceForBL/";
		String goldsetFile = "./Data/gitInfoNew.txt";
		new MasterBLScoreProvider(sourceFolder, bugReportFolder, goldsetFile)
				.produceBugLocatorResults();
		long end = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (end - start) / 1000 + " s");
	}
}
