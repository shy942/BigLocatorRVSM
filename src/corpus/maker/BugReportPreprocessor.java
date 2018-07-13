package corpus.maker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import stemmer.Stemmer;

import utility.ContentLoader;
import utility.MiscUtility;

public class BugReportPreprocessor {

	String content;
	ArrayList<String> stopwords;
	Stemmer stemmer;

	public BugReportPreprocessor(String content) {
		this.content = content;
		this.stopwords = new ArrayList<String>();
		this.loadStopWords();
		this.stemmer = new Stemmer();
	}

	protected void loadStopWords() {
		this.stopwords = ContentLoader.readContent("./Data/stop_words.txt");
	}

	protected ArrayList<String> removeStopWords(ArrayList<String> words) {
		ArrayList<String> refined = new ArrayList<String>(words);
		for (String word : words) {
			if (this.stopwords.contains(word)) {
				refined.remove(word);
			}
		}
		return refined;
	}

	protected ArrayList<String> splitContent(String content) {
		String[] words = content.split("\\s+|\\p{Punct}+|\\d+");
		return new ArrayList<String>(Arrays.asList(words));
	}

	protected String performStemming(String word) {
		// return stemmer.stripAffixes(word);
		return word;
	}

	public String performNLP() {
		// performing NLP operations
		ArrayList<String> words = splitContent(this.content);
		ArrayList<String> refined = removeStopWords(words);
		ArrayList<String> stemmed = new ArrayList<String>();
		for (String word : refined) {
			if (!word.trim().isEmpty()) {
				String stemmedWord = performStemming(word.trim());
				if (stemmedWord.length() >= 3) {
					stemmedWord=stemmedWord.toLowerCase(Locale.ENGLISH);
					stemmedWord=stemmedWord.trim();
					stemmedWord=stemmedWord.replaceAll("�", "");
					stemmedWord=stemmedWord.replaceAll("�", "");
					stemmed.add(stemmedWord);
				}
			}
		}

		return MiscUtility.list2Str(stemmed);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
