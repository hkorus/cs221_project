
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class findCommonWords {


	public static void main(String[] args) {
		File file = new File("Middlemarch.txt");
		HashSet<String> stopWords = getStopWords();
		StringBuffer contents = new StringBuffer();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			String token = null;
			String mostCommon = null;
			final Map<String, Integer> wordCount = new HashMap<String, Integer>();

			//List<String> frequent = new ArrayList<String>();
			Vector<String> frequent = new Vector<String>();
			int counter = 0;
			// repeat until all lines is read
			while (scanner.hasNext()) {
				token = scanner.next().trim().toLowerCase().replaceAll("[^A-Za-z]", "");
				//System.out.println(text);
				contents.append(token)
				.append(System.getProperty(
						"line.separator"));


				if(mostCommon==null) mostCommon = token;
				if(wordCount.containsKey(token)) {
					wordCount.put(token, wordCount.get(token)+1);
				} else {
					wordCount.put(token, 1);
				}
				if(wordCount.get(token)>wordCount.get(mostCommon)) mostCommon = token;
				if(!frequent.contains(token) && !stopWords.contains(token)){
					if(frequent.size()<40) {
						frequent.add(token);
						printFrequent(frequent, wordCount);
					} else if(wordCount.get(token)>wordCount.get(frequent.get(frequent.size()-1))) {

						frequent.remove(frequent.size()-1);
						frequent.add(token);
						counter+=1;
						System.out.println(counter);

						Collections.sort(frequent, new Comparator<String>() {
							@Override
							public int compare(String str1, String str2) {
								return(wordCount.get(str2) - wordCount.get(str1));
							}
						});
					}
				}
			}

			int test = Collections.max(wordCount.values());
			//System.out.println(mostCommon);
			//System.out.println(wordCount.get(mostCommon));

			printFrequent(frequent, wordCount);
			System.out.println("HI");


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		// show file contents here
		//System.out.println(contents.toString());
	}

	private static HashSet<String> getStopWords() {
		HashSet<String> stopWords = new HashSet<String>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader("english.stop"));
			
			String line = reader.readLine();
			while (line != null) {
				stopWords.add(line.trim());
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stopWords;
	}

	private static void printFrequent(Vector<String> frequent, Map<String, Integer> wordCount) {
		System.out.println("printing frequent");
		for(int i =0; i<frequent.size(); i++){
			System.out.println(frequent.get(i) + " count: " + wordCount.get(frequent.get(i)));
		}

	}


}
