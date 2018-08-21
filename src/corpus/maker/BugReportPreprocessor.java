package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.englishStemmer;



import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugReportPreprocessor {

	String inputFolder;
	ArrayList<String> stopwords;
	

	public BugReportPreprocessor(String inputFolder) {
		this.inputFolder = inputFolder;
		
		
		
	
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
		
			SnowballStemmer snowballStemmer = new englishStemmer();
		    snowballStemmer.setCurrent(word);
		    snowballStemmer.stem();
		    String result = snowballStemmer.getCurrent();

		    return result;
	}

	public String performNLP(String content) {
		// performing NLP operations
		ArrayList<String> words = splitContent(content);
		//Already done
		//ArrayList<String> refined = removeStopWords(words);
		SnowballStemmer snowballStemmer = new englishStemmer();
		ArrayList<String> stemmed = new ArrayList<String>();
		for (String word : words) {
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
			//String afterStopWordRemoval=this.StopWordRemover(filecontent);
			//System.out.println(afterStopWordRemoval);
			
			String ProcessedContent=this.performNLP(filecontent);
			System.out.println(ProcessedContent);
			
			ContentWriter.writeContent(outputFolder+f.getName(), ProcessedContent);
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFolder="E:\\PhD\\Data\\BugDataNew\\";
		String outputFolder="E:\\PhD\\Data\\ProcessedBugDataSnowballStem\\";
		BugReportPreprocessor obj=new BugReportPreprocessor(inputFolder);
		obj.PerformStemming(outputFolder); 
	}

}
