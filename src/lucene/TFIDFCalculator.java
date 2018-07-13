package lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.PackedInts.Writer;



public class TFIDFCalculator {

	String indexDir;
	HashMap<String, Double> tfMap;
	HashMap<String, Double> dfMap;
	HashMap<String, Double> tfidfMap;

	public TFIDFCalculator(String indexDir) {
		this.indexDir = indexDir;
		this.tfidfMap = new HashMap<String, Double>();
		this.tfMap = new HashMap<String, Double>();
		this.dfMap = new HashMap<String, Double>();
	}

	public TFIDFCalculator() {
		//this.indexDir = StaticInfo.BUGDIR + "/" + year + "-index";
		this.indexDir = "./Data/Index";
		this.tfidfMap = new HashMap<String, Double>();
		this.tfMap = new HashMap<String, Double>();
		this.dfMap = new HashMap<String, Double>();
	}

	public HashMap<String, Double> getDF(ArrayList<String> tokens)
			throws IOException {

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				indexDir).toPath()));
		String field = "contents";
		Bits liveDocs = MultiFields.getLiveDocs(reader);
		TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator();
		BytesRef term = null;
		TFIDFSimilarity tfidfSim = new DefaultSimilarity();
		int docCount = reader.numDocs();

		while ((term = termEnum.next()) != null) {
			String termText = term.utf8ToString();
			Term termInstance = new Term(field, term);
			// term and doc frequency in all documents

			long indexTf = reader.totalTermFreq(termInstance);

			long indexDf = reader.docFreq(termInstance);

			if (tokens.contains(termText)) {

				if (!dfMap.containsKey(termText)) {
					dfMap.put(termText, new Double(indexDf));
				}
			}

			 System.out.println(termText+"\t"+indexTf+"\t"+indexDf);
		}
		return dfMap;
	}

	

	public static void main(String[] args) {
		
		
	}

}
