package corpus.maker;

import java.io.File;

import utility.ContentLoader;
import utility.ContentWriter;

public class SourceCodePreProcessor {
	
	private String sourceCodeDir;
	
	public SourceCodePreProcessor(String sourceCodeDir)
	{
		this.sourceCodeDir=sourceCodeDir;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SourceCodePreProcessor("/Users/user/Documents/Backup/ProcessedFiles/").nameChangedandContent("/Users/user/Documents/Ph.D/2018/Data/SourceForBL/");
	}
	
	
	
	public void nameChangedandContent(String outFolder)
	{
		File[] sourceCodeFiles = new File(this.sourceCodeDir).listFiles();
		System.out.println("Total number of source code files: "+sourceCodeFiles.length);
		int noOfTotalDocument=sourceCodeFiles.length;
		for (File sourceCodeFile : sourceCodeFiles) {
			String sourceCodeContent = ContentLoader.readContentSimple(sourceCodeFile
				.getAbsolutePath());
			//System.out.println(sourceCodeContent);
			String[] spilter=sourceCodeContent.split("\n");
			if(spilter.length==2)
			{
				String filePathOld=spilter[0];
				String content=spilter[1];
				System.out.println(filePathOld);
				int index = nthOccurrence(filePathOld, '/', 7);
				 
				filePathOld = filePathOld.substring(index+1, filePathOld.length());
				String filePathNew=filePathOld.replaceAll("/", ".");
				
				System.out.println(filePathNew);
				ContentWriter.writeContent(outFolder+filePathNew, content);
			}
		}
	}
	
	
	public static int nthOccurrence(String s, char c, int occurrence) {
	    return nthOccurrence(s, 0, c, 0, occurrence);
	}

	public static int nthOccurrence(String s, int from, char c, int curr, int expected) {
	    final int index = s.indexOf(c, from);
	    if(index == -1) return -1;
	    return (curr + 1 == expected) ? index : 
	        nthOccurrence(s, index + 1, c, curr + 1, expected);
	}

}
