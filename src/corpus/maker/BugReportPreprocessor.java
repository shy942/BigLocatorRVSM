package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import stemmer.Stemmer;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugReportPreprocessor {

	String inputFolder;
	ArrayList<String> stopwords;
	Stemmer stemmer;

	public BugReportPreprocessor(String inputFolder) {
		this.inputFolder = inputFolder;
		this.stopwords = new ArrayList<String>();
		this.loadStopWords();
		this.stemmer = new Stemmer();
		this.loadStopWords();
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

	protected String StopWordRemover(String content)
	{
		String processedContent="";
		String[] spilter=content.split(" ");
		for(String word:spilter)
		{
			if(this.stopwords.contains(word))
			{
				//do nothing
			}
			else
			{
				processedContent+=word+" ";
			}
		}
		
		return processedContent;
	}
	
	protected ArrayList<String> splitContent(String content) {
		String[] words = content.split("\\s+|\\p{Punct}+|\\d+");
		return new ArrayList<String>(Arrays.asList(words));
	}

	protected String performStemming(String word) {
		return stemmer.stripAffixes(word);
		//return word;
	}

	public String performNLP(String content) {
		// performing NLP operations
		ArrayList<String> words = splitContent(content);
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

	public void PerformStemming(String outputFolder)
	{
		File[] conentfiles = new File(this.inputFolder).listFiles();
		System.out.println("Total number of source code files: "+conentfiles.length);
		int noOfTotalDocument=conentfiles.length;
		for (File f : conentfiles) {
			String filecontent = ContentLoader.readContentSimple(f
				.getAbsolutePath());
			
			System.out.println(filecontent);
			String afterStopWordRemoval=this.StopWordRemover(filecontent);
			System.out.println(afterStopWordRemoval);
			
			String ProcessedContent=this.performNLP(afterStopWordRemoval);
			System.out.println(ProcessedContent);
			
			ContentWriter.writeContent(outputFolder+f.getName(), ProcessedContent);
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFolder="/Users/user/Documents/Ph.D/2018/Data/BugData/";
		String outputFolder="/Users/user/Documents/Ph.D/2018/Data/ProcessedBugData/";
		BugReportPreprocessor obj=new BugReportPreprocessor(inputFolder);
		obj.PerformStemming(outputFolder); 
	}

}
