package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ContentLoader {

	public static ArrayList<String> readContent(String inFile) {
		// save the content
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader breader = new BufferedReader(new FileReader(inFile));
			while (breader.ready()) {
				String line = breader.readLine().trim();
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
			breader.close();

		} catch (Exception exc) {
			exc.printStackTrace();

		}
		return lines;
	}

	public static ArrayList<String> getAllLinesOptList(String fileName) {
		ArrayList<String> temp = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNextLine()) {
				temp.add(scanner.nextLine());
			}
			scanner.close();

		} catch (Exception exc) {
			// handle the exception
		}
		return temp;
	}

	public static ArrayList<String> getAllKeywords(String fileName) {
		ArrayList<String> temp = new ArrayList<>();

		try {
			BufferedReader breader = new BufferedReader(
					new FileReader(fileName));
			while (breader.ready()) {
				String line = breader.readLine().trim();
				if (!line.isEmpty()) {
					String[] spilter = line.split(" ");
					for (String token : spilter) {
						if (token.matches("\\p{Punct}+|\\d+|\\s+")
								|| token.contains(".txt"))
							continue;
						else {
							token = token.replaceAll("�", "");
							token = token.replaceAll("�", "");
							temp.add(token);
						}
					}

				}
			}
			breader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return temp;
	}

	public static String[] getAllTokens(String fileName) {
		String content = readContentProcessedSourceCode(fileName);
		String[] tokens = content.split("\\s+");
		return tokens;
	}

	public static ArrayList<String> getDocTokensAll(String fileName) {
		ArrayList<String> tokenList = new ArrayList<>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				tokenList.add(scanner.next());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scanner.close();
		return tokenList;
	}

	public static String readContentSimple(String inFile) {
		// save the content
		String content = new String();
		try {
			BufferedReader breader = new BufferedReader(new FileReader(inFile));
			while (breader.ready()) {
				String line = breader.readLine().trim();
				if (!line.isEmpty()) {
					content += line + "\n";
				}
			}
			breader.close();

		} catch (Exception exc) {
			exc.printStackTrace();

		}
		return content;

	}

	// loading all lines from a file using arraylist
	public static ArrayList<String> getAllLinesList(String fileName) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			File f = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(f));
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				lines.add(line.trim());
			}
			bufferedReader.close();
		} catch (Exception ex) {
			// handle the exception
			ex.printStackTrace();
		}
		return lines;
	}

	public static String readContentProcessedSourceCode(String inFile) {
		// save the content
		String content = new String();
		try {
			BufferedReader breader = new BufferedReader(new FileReader(inFile));
			String line = breader.readLine().trim();

			while (breader.ready()) {
				line = breader.readLine().trim();
				if (!line.isEmpty()) {
					content += line + "\n";
				}
			}
			breader.close();

		} catch (Exception exc) {
			exc.printStackTrace();

		}
		return content;
	}

	public static String returnFilePath(String inFile) {
		String filePath = "";
		String base = "/Users/user/eclipse.platform.ui/";

		try {
			BufferedReader breader = new BufferedReader(new FileReader(inFile));
			String line = breader.readLine().trim();
			filePath = line.substring(base.length());
			breader.close();

		} catch (Exception exc) {
			exc.printStackTrace();

		}
		return filePath;
	}

	public static HashMap<String, ArrayList<String>> returnHashMapFromFileContent(
			String inFile) {
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		ArrayList<String> fileContent = ContentLoader.readContent(inFile);
		for (String line : fileContent) {
			String[] spilter = line.split(":");
			String idPart = spilter[0];
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 1; i < spilter.length; i++) {
				list.add(spilter[i]);
			}
			hm.put(idPart, list);
		}
		ContentWriter
				.writeContent(
						"/Users/user/Dropbox/WorkinginHome/SCAM/Implementation/Output/test.txt",
						fileContent);
		return hm;
	}

	public static HashMap<String, ArrayList<String>> returnHashMapFromFileContentForQuery(
			String inFile) {
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		ArrayList<String> fileContent = ContentLoader.readContent(inFile);
		for (String line : fileContent) {
			String[] spilter = line.split(" ");
			String idPart = spilter[0];
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 1; i < spilter.length; i++) {
				list.add(spilter[i]);
			}
			hm.put(idPart, list);
		}
		return hm;
	}

	public static String extractFileAddress(String absolutePath) {
		// TODO Auto-generated method stub
		return null;
	}

}
