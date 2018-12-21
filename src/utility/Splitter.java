package utility;

import java.util.ArrayList;

public class Splitter {
	public static String[] splitNatureLanguage(String natureLanguage) {
		ArrayList<String> wordList = new ArrayList();
		StringBuffer wordBuffer = new StringBuffer();
		char[] var6;
		int var5 = (var6 = natureLanguage.toCharArray()).length;

		for (int var4 = 0; var4 < var5; ++var4) {
			char c = var6[var4];
			if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9') && c != '\'') {
				String word = wordBuffer.toString();
				if (!word.equals("")) {
					wordList.add(word);
				}

				wordBuffer = new StringBuffer();
			} else {
				wordBuffer.append(c);
			}
		}

		if (wordBuffer.length() != 0) {
			String word = wordBuffer.toString();
			if (!word.equals("")) {
				wordList.add(word);
			}

			new StringBuffer();
		}

		return (String[]) wordList.toArray(new String[wordList.size()]);
	}

	public static String[] splitSourceCode(String sourceCode) {
		StringBuffer contentBuf = new StringBuffer();
		StringBuffer wordBuf = new StringBuffer();
		sourceCode = sourceCode + "$";
		char[] var6;
		int var5 = (var6 = sourceCode.toCharArray()).length;

		int i;
		for (i = 0; i < var5; ++i) {
			char c = var6[i];
			if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
				int length = wordBuf.length();
				if (length != 0) {
					int k = 0;
					//int i = 0;

					for (int j = 1; i < length - 1; ++j) {
						char first = wordBuf.charAt(i);
						char second = wordBuf.charAt(j);
						if (first >= 'A' && first <= 'Z' && second >= 'a' && second <= 'z') {
							contentBuf.append(wordBuf.substring(k, i));
							contentBuf.append(' ');
							k = i;
						} else if (first >= 'a' && first <= 'z' && second >= 'A' && second <= 'Z') {
							contentBuf.append(wordBuf.substring(k, j));
							contentBuf.append(' ');
							k = j;
						}

						++i;
					}

					if (k < length) {
						contentBuf.append(wordBuf.substring(k));
						contentBuf.append(" ");
					}

					wordBuf = new StringBuffer();
				}
			} else {
				wordBuf.append(c);
			}
		}

		String[] words = contentBuf.toString().split(" ");
		contentBuf = new StringBuffer();

		for (i = 0; i < words.length; ++i) {
			if (!words[i].trim().equals("") && words[i].length() >= 2) {
				contentBuf.append(words[i] + " ");
			}
		}

		return contentBuf.toString().trim().split(" ");
	}
}