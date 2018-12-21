package lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version; 
import org.apache.lucene.util.packed.PackedInts.Reader;



public class LuceneIndexer {

	static String indexDir;
	String docsDir;

	public LuceneIndexer(String docFolder, String indexFolder) {
		this.docsDir = docFolder;
		this.indexDir = indexFolder;
	}

	public void createIndex() throws CorruptIndexException,
			LockObtainFailedException, IOException {

		String FIELD_PATH = "path";
		String FIELD_CONTENTS = "contents";

		Analyzer analyzer = new StandardAnalyzer();
		boolean recreateIndexIfExists = true;
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());

		IndexWriter indexWriter = new IndexWriter(dir, config);
		File docs = new File(this.docsDir);
		File[] files = docs.listFiles();
		for (File file : files) {
			Document doc = new Document();

			String path = file.getName();

			indexWriter.addDocument(doc);

		    // ===================================================
			// add contents of file
			// ===================================================
			FileReader fr = new FileReader(file);
			
			doc.add(new TextField("contents", fr));
			doc.add(new StringField("path", path, Field.Store.YES));
			//doc.add(new StringField("path", file.getPath(), Field.Store.YES));
			//doc.add(new StringField("filename", file.getName(), Field.Store.YES));

			indexWriter.addDocument(doc);
			System.out.println("Added: " + file.getName());

		}

		indexWriter.close();
	}
	
	public static void searchIndex(String searchString) {

		System.out.println("Searching.... '" + searchString + "'");

		try {
			Analyzer analyzer = new StandardAnalyzer();
			FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());

			IndexReader indexReader;
			indexReader=DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(indexReader);

			
			int hitsPerPage = 200;
			QueryParser qp = new QueryParser("contents",
					analyzer);
			Query query = qp.parse(searchString); // parse the query and construct the Query object
			TopDocs docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;// run the query
			System.out.println("Found: " + hits.length);
			
          
            for(int i=0;i<hits.length;++i) {
			{
				ScoreDoc item = hits[i];
	        	Document doc = searcher.doc(item.doc);
	        	double score=item.score;
	        	System.out.println((i + 1) + ". " + doc.get("path") + "\t"+score);
			}
		
			
            }
            indexReader.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
}

	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException {
		// TODO Auto-generated method stub
		String corpus="SWT";
		String base="E:\\PhD\\Repo\\"+corpus+"\\";
		String indexFolder=base+"\\data\\Index"+corpus;
		//String docFolder="/Users/user/Documents/Ph.D/2018/Data/SourceForBL/";
		String docFolder=base+"\\methodDec21\\";
		new LuceneIndexer(docFolder, indexFolder).createIndex();
		//new LuceneIndexer(docFolder, indexFolder).searchIndex("bind perform object");
		
	}

}
