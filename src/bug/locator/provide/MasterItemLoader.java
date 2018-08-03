package bug.locator.provide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lucene.TFIDFCalculator;
import lucene.VSMCalculator;
import utility.ContentLoader;

public class MasterItemLoader {

	String sourceFolder;
	String bugReportFolder;
	String goldsetFile;
	int maxDocLength = 0;
	int minDocLength = 100000;
	HashMap<String, Integer> docLengthMap;
	HashMap<String, HashMap<String, Double>> masterSourceLogTFMap;
	HashMap<String, HashMap<String, Double>> masterBugReportLogTFMap;

	public MasterItemLoader(String sourceFolder, String bugReportFolder,
			String goldsetFile) {
		this.sourceFolder = sourceFolder;
		this.bugReportFolder = bugReportFolder;
		this.goldsetFile = goldsetFile;
		this.docLengthMap = new HashMap<>();
		this.masterSourceLogTFMap = new HashMap<>();
		this.masterBugReportLogTFMap = new HashMap<>();
	}

	protected HashMap<String, Double> getDocumentLogTFMap(
			ArrayList<String> tokens) {
		VSMCalculator objVSMCalc = new VSMCalculator(tokens, true);
		return objVSMCalc.getLogTF();
	}

	public HashMap<String, HashMap<String, Double>> getSourceLogTFAll(
			String sourceFolder) {
		HashMap<String, ArrayList<String>> sourceFileTokenMap = getSourceFileContentMap(sourceFolder);
		for (String srcFile : sourceFileTokenMap.keySet()) {
			HashMap<String, Double> sourceLogTF = getDocumentLogTFMap(sourceFileTokenMap
					.get(srcFile));
			this.masterSourceLogTFMap.put(srcFile, sourceLogTF);
		}
		System.out.println("Loaded source code files successfully!");
		return this.masterSourceLogTFMap;
	}

	public HashMap<String, HashMap<String, Double>> getBugReportLogTFAll(
			String bugReportFolder) {
		HashMap<String, ArrayList<String>> bugReportTokenMap = getBugReportContentMap(bugReportFolder);
		for (String bugReportFile : bugReportTokenMap.keySet()) {
			HashMap<String, Double> bugReportLogTF = getDocumentLogTFMap(bugReportTokenMap
					.get(bugReportFile));
			this.masterBugReportLogTFMap.put(bugReportFile, bugReportLogTF);
		}
		System.out.println("Loaded the bug reports successfully!");
		return this.masterBugReportLogTFMap;
	}

	protected HashMap<String, ArrayList<String>> getSourceFileContentMap(
			String sourceFolder) {
		File[] files = new File(sourceFolder).listFiles();
		HashMap<String, ArrayList<String>> docMap = new HashMap<>();
		for (File file : files) {
			ArrayList<String> srcTokens = ContentLoader.getDocTokensAll(file
					.getAbsolutePath());
			// storing source file length
			this.docLengthMap.put(file.getName(), srcTokens.size());

			// getting maximum and minimum document length
			if (srcTokens.size() > maxDocLength) {
				maxDocLength = srcTokens.size();
			}
			if (srcTokens.size() < minDocLength) {
				minDocLength = srcTokens.size();
			}
			docMap.put(file.getName(), srcTokens);
		}
		return docMap;
	}

	protected HashMap<String, ArrayList<String>> getBugReportContentMap(
			String bugReportFolder) {
		File[] files = new File(bugReportFolder).listFiles();
		HashMap<String, ArrayList<String>> docMap = new HashMap<>();
		for (File file : files) {
			ArrayList<String> docTokens = ContentLoader.getDocTokensAll(file
					.getAbsolutePath());

			docMap.put(file.getName(), docTokens);
		}
		return docMap;
	}

	public int getMaxSourceDocLength() {
		return this.maxDocLength;
	}

	public int getMinSourceDocLength() {
		return this.minDocLength;
	}

	public HashMap<String, Integer> getSourceDocLength() {
		return this.docLengthMap;
	}

	public HashMap<String, Double> getSourceDocumentIDF() {
		TFIDFCalculator objTFIDFCalc = new TFIDFCalculator();
		return objTFIDFCalc.calculateIDFOnly();
	}

	public HashMap<Integer, ArrayList<String>> loadGoldsetMap(String goldsetFile) {
		HashMap<Integer, ArrayList<String>> goldMap = new HashMap<>();
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(goldsetFile);
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
		System.out.println("Loaded the goldset successfully!");
		return goldMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bugReportFolder = "./Data/ProcessedBugData";
		String sourceFolder = "./Data/ProcessedSourceForBL";
		String goldsetFile = "./Data/gitInfoNew.txt";
		MasterItemLoader master = new MasterItemLoader(sourceFolder,
				bugReportFolder, goldsetFile);
		System.out.println(master.getSourceLogTFAll(sourceFolder));
		System.out.println(master.getBugReportLogTFAll(bugReportFolder));
		System.out.println(master.loadGoldsetMap(goldsetFile));
	}
}
