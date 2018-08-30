package LuceneBasedBL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import utility.ContentLoader;
import utility.MiscUtility;

public class BugLocatorLuceneBased {

	
	static String indexDir;
	String bugReportFolder;

	public BugLocatorLuceneBased(String indexDir, String bugReportFolder) {
		this.indexDir = indexDir;
		this.bugReportFolder = bugReportFolder;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexDir="./Data/Index";
		String bugReportFolder = "C:\\Users\\Mukta\\Workspace-2018\\QueryReformulation\\data\\testsetForBL\\test10";
		new BugLocatorLuceneBased(indexDir, bugReportFolder).getLuceneBasedScore(0.8);
	}

	public HashMap<Integer, HashMap<String, Double>> getLuceneBasedScore(Double ALPHA)
	{
		HashMap<Integer, HashMap<String, Double>>finalResult=new HashMap<>();
		HashMap<String, ArrayList<String>> docMap=getBugReportContentMap(this.bugReportFolder);
		
		for(String bugID: docMap.keySet())
		{
			int queryID=Integer.valueOf(bugID.substring(0,bugID.length()-4));
			ArrayList<String> content=docMap.get(bugID);
			System.out.println(bugID);
			String queryContent=MiscUtility.list2Str(content);
			//System.out.println(queryContent);
			HashMap<String, Double> resultPerQuery=searchIndex(queryContent, ALPHA);
			MiscUtility.showResult(10, resultPerQuery);
			finalResult.put(queryID, resultPerQuery);
		}
		
		
		return finalResult;
	}
	
	
	protected static HashMap<String, ArrayList<String>> getBugReportContentMap(
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
	
	public static HashMap<String, Double> searchIndex(String searchString, double ALPHA) {

		HashMap<String, Double> resultMap=new HashMap<>();

		try {
			Analyzer analyzer = new StandardAnalyzer();
			FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());

			IndexReader indexReader;
			indexReader=DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(indexReader);

			
			int hitsPerPage = 10;
			QueryParser qp = new QueryParser("contents",
					analyzer);
			Query query = qp.parse(searchString); // parse the query and construct the Query object
			TopDocs docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;// run the query
			
			for(int i=0;i<hits.length;++i) {
			{
				ScoreDoc item = hits[i];
	        	Document doc = searcher.doc(item.doc);
	        	double score=item.score;
	        	System.out.println((i + 1) + ". " + doc.get("path") + "\t"+score);
	        	resultMap.put(doc.get("path"), score);
			}
		
			
           }
		indexReader.close(); 
		} catch (Exception e) {
			e.printStackTrace();
	}
		
		HashMap<String, Double> resultMapNormalized=normalizeMe(resultMap, ALPHA);
		HashMap<String, Double> resultSorted=MiscUtility.sortByValues(resultMapNormalized);
		return resultSorted;
}
	
	
	protected static HashMap<String, Double> normalizeMe(
			HashMap<String, Double> tempMap, double ALPHA) {
		double max = 0;
		for (String key : tempMap.keySet()) {
			double score = tempMap.get(key);
			if (score > max) {
				max = score;
			}
		}
		for (String key : tempMap.keySet()) {
			if (max > 0) {
				double oldScore = tempMap.get(key);
				tempMap.put(key, (oldScore / max)*ALPHA);
			}
		}
		return tempMap;
	}
}
