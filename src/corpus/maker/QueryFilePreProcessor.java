package corpus.maker;

import java.io.File;

import utility.ContentLoader;
import utility.ContentWriter;

public class QueryFilePreProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//For Mac
		//new QueryFilePreProcessor().nameChangedandContent("/Users/user/Documents/Backup/QueryData/", "/Users/user/Documents/Ph.D/2018/Data/BugData/");
		//For windows
		new QueryFilePreProcessor().nameChangedandContentWindows("F:\\BigDataProject\\QueriesFolder\\", "F:\\PhD\\Data\\BugDataNew\\");
	}

	private void nameChangedandContentWindows(String queryFilePath, String outFolder) {
		// TODO Auto-generated method stub
		File[] bugFiles = new File(queryFilePath).listFiles();
		System.out.println("Total number of query files: "+bugFiles.length);
		int noOfTotalDocument=bugFiles.length;
		for (File bugFile : bugFiles) {
			String queryContent = ContentLoader.readContentSimple(bugFile
				.getAbsolutePath());
			//System.out.println(sourceCodeContent);
			String[] spilter=queryContent.split(" ");
			if(spilter.length>=2)
			{
				String content="";
				for(int i=1;i<spilter.length;i++)
				{
				 	content+=spilter[i]+" ";
				}
				ContentWriter.writeContent(outFolder+bugFile.getName(),content);
			}
		}
	}

	public void nameChangedandContent(String queryFilePath, String outFolder)
	{
		File[] bugFiles = new File(queryFilePath).listFiles();
		System.out.println("Total number of query files: "+bugFiles.length);
		int noOfTotalDocument=bugFiles.length;
		for (File bugFile : bugFiles) {
			String queryContent = ContentLoader.readContentSimple(bugFile
				.getAbsolutePath());
			//System.out.println(sourceCodeContent);
			String[] spilter=queryContent.split(" ");
			if(spilter.length>=2)
			{
				String content="";
				for(int i=1;i<spilter.length;i++)
				{
				 	content+=spilter[i]+" ";
				}
				ContentWriter.writeContent(outFolder+bugFile.getName(),content);
			}
		}
	}
	
}
