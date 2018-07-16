package lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
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

import utility.MiscUtility;



public class TFIDFCalculator {

	String indexDir;
	HashMap<String, Double> tfMap;
	HashMap<String, Double> dfMap;
	HashMap<String, Double> tfidfMap;
	private long totalTermFreqCorpus;
	private ArrayList<String> keys;
	public static String FIELD_CONTENTS = "contents";
	
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
		this.FIELD_CONTENTS="contents";
		this.keys=new ArrayList<>();
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
	

	public HashMap<String, Long> calculateTF() {
			HashMap<String, Long> termFreqMap = new HashMap<>();
			try {
				IndexReader reader = DirectoryReader.open(FSDirectory
						.open(new File(indexDir).toPath()));
				// String targetTerm = "breakpoint";

				Fields fields = MultiFields.getFields(reader);
				for (String field : fields) {
					Terms terms = fields.terms(field);
					TermsEnum termsEnum = terms.iterator();
					BytesRef bytesRef;
					while ((bytesRef = termsEnum.next()) != null) {
						if (termsEnum.seekExact(bytesRef)) {
							String term = bytesRef.utf8ToString();
							this.keys.add(term);
						}
					}
				}

				for (String term : this.keys) {
					Term t = new Term(FIELD_CONTENTS, term);
					// calculating the TF
					long totalTermFreq = reader.totalTermFreq(t);
					if (!termFreqMap.containsKey(term)) {
						termFreqMap.put(term, totalTermFreq);
						totalTermFreqCorpus += totalTermFreq;
					}
				}
			} catch (Exception exc) {
				// handle the exception
			}
			
			MiscUtility.showResult(10, termFreqMap);
			return termFreqMap;
	}
	public static void main(String[] args) {
		
		TFIDFCalculator obj=new TFIDFCalculator();
		obj.calculateTF();
		try {
			obj.getDF(obj.keys);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//new TFIDFCalculator().calculateTF();
	}

}
