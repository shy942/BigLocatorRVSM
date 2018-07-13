package lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version; 
import org.apache.lucene.util.packed.PackedInts.Reader;



public class LuceneIndexer {

	String indexDir;
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
			Document document = new Document();

			String path = file.getCanonicalPath();

			indexWriter.addDocument(document);

			Document doc = new Document();

			// ===================================================
			// add contents of file
			// ===================================================
			FileReader fr = new FileReader(file);
			doc.add(new TextField("contents", fr));
			doc.add(new StringField("path", file.getPath(), Field.Store.YES));
			doc.add(new StringField("filename", file.getName(), Field.Store.YES));

			indexWriter.addDocument(doc);
			System.out.println("Added: " + file.getName());

		}

		indexWriter.close();
	}

	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException {
		// TODO Auto-generated method stub
		
		int year=2006;
		//String indexFolder=StaticInfo.BUGDIR+"/"+year+"-index";
		//String docFolder=StaticInfo.BUGDIR+"/"+year+"-Preprocessed";
		//new LuceneIndexer(docFolder, indexFolder).createIndex();
		String indexFolder="./Data/Index";
		String docFolder="/Users/user/Documents/Backup/ProcessedFiles/";
		new LuceneIndexer(docFolder, indexFolder).createIndex();
		
	}

}
