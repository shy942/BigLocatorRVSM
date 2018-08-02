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
	HashMap<String, Long> tfMap;
	HashMap<String, Double> dfMap;
	HashMap<String, Double> idfMap;
	private long totalTermFreqCorpus;
	private ArrayList<String> keys;
	public static String FIELD_CONTENTS = "contents";

	public TFIDFCalculator(String indexDir) {
		this.indexDir = indexDir;
		this.idfMap = new HashMap<String, Double>();
		this.tfMap = new HashMap<String, Long>();
		this.dfMap = new HashMap<String, Double>();
	}

	public TFIDFCalculator() {
		// this.indexDir = StaticInfo.BUGDIR + "/" + year + "-index";
		this.indexDir = "./Data/Index";
		this.idfMap = new HashMap<String, Double>();
		this.tfMap = new HashMap<String, Long>();
		this.dfMap = new HashMap<String, Double>();
		this.FIELD_CONTENTS = "contents";
		this.keys = new ArrayList<>();
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

			// int numDoc=reader.numDocs(); //Compute total number of documents

			if (tokens.contains(termText)) {

				if (!dfMap.containsKey(termText)) {
					dfMap.put(termText, new Double(indexDf));
				}
			}
			//System.out.println(termText + "\t" + indexTf + "\t" + indexDf+ "\t");
		}
		//MiscUtility.showResult(10, this.dfMap);
		return dfMap;
	}

	public HashMap<String, Long> calculateTF() {
		// HashMap<String, Long> termFreqMap = new HashMap<>();
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
				if (!this.tfMap.containsKey(term)) {
					this.tfMap.put(term, totalTermFreq);
					totalTermFreqCorpus += totalTermFreq;
				}
			}
		} catch (Exception exc) {
			// handle the exception
		}

		// MiscUtility.showResult(10, tfMap);
		return tfMap;
	}

	protected double getIDF(int N, int DF) {
		// getting the IDF
		if (DF == 0)
			return 0;
		return Math.log(1 + (double) N / DF);
	}

	public HashMap<String, Double> calculateIDFOnly() {
		IndexReader reader = null;
		// HashMap<String, Double> inverseDFMap = new HashMap<>();
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(
					this.indexDir).toPath()));
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
			// now go for the IDF
			int N = reader.numDocs();
			double maxIDF = 0;
			for (String term : this.keys) {
				Term t = new Term(FIELD_CONTENTS, term);
				int docFreq = reader.docFreq(t);
				double idf = getIDF(N, docFreq);
				if (!this.idfMap.containsKey(term)) {
					this.idfMap.put(term, idf);
					if (idf > maxIDF) {
						maxIDF = idf;
					}
				}
			}
			// now normalize the IDF scores
			for (String term : this.keys) {
				double idf = this.idfMap.get(term);
				idf = idf / maxIDF;
				this.idfMap.put(term, idf);
			}
		} catch (Exception exc) {
			// handle the exception
		}
		//MiscUtility.showResult(10, this.idfMap);
		return this.idfMap;
	}

	public static void main(String[] args) {

		TFIDFCalculator obj = new TFIDFCalculator();
		//obj.calculateTF();
		// obj.getDF(obj.keys);
		System.out.println(obj.calculateIDFOnly());
	}

}
