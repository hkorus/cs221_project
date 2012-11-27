
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

public class findCommonWords {
	private static int numNeighbors = 10;

	public static void main(String[] args) {
		final Map<String, Integer> wordCount = new HashMap<String, Integer>();
		final Map<String, Map<String, Integer> > neighborMap = new HashMap<String, Map<String, Integer>>();
		Vector<String> frequent = new Vector<String>();
		HashSet<String> stopWords = getStopWords();
		Queue<String> neighbors = new ArrayBlockingQueue<String>(numNeighbors);
		for (int i = 7; i <= 9 ; i++) {
			String filename = "nyt_eng_19940" + i + ".txt";


			File file = new File(filename);
			Scanner scanner = null;

			try {
				scanner = new Scanner(new BufferedReader(new FileReader(file)));
				scanner.useDelimiter("[^A-Za-z]");
				String token = null;
				int counter = 0;

				// repeat until all words are read
				while (scanner.hasNext()) {
					token = scanner.next().trim().toLowerCase(); //.replaceAll("[^A-Za-z]", "");

					if(wordCount.containsKey(token)) {
						wordCount.put(token, wordCount.get(token)+1);
					} else {
						wordCount.put(token, 1);
					}

					if(!neighbors.contains(token) && !stopWords.contains(token)){

						//update neighbors for word that you push (get all the neighbors 10 before the word)
						//and for the word that you pop (get all the neighbors 10 after the word)
						updateNeighbors(neighborMap, neighbors, token);
						if (neighbors.size() < numNeighbors) {
							neighbors.offer(token);
						} else {
							updateNeighbors(neighborMap, neighbors, neighbors.poll());
							neighbors.offer(token);
						}
					}

					if (!frequent.contains(token)){
						if(frequent.size()<40) {
							frequent.add(token);
							//printFrequent(frequent, wordCount);
						} else if(wordCount.get(token)>wordCount.get(frequent.get(frequent.size()-1))) {

							frequent.remove(frequent.size()-1);
							frequent.add(token);
							counter+=1;
							System.out.println(token);

							Collections.sort(frequent, new Comparator<String>() {
								//@Override
								public int compare(String str1, String str2) {
									return(wordCount.get(str2) - wordCount.get(str1));
								}
							});
						}
					}

				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
		printFrequent(frequent, wordCount);
		printNeighborsSortedByFrequency(neighborMap);

	}

	private static void printNeighborsSortedByFrequency(
			Map<String, Map<String, Integer>> neighborMap) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
			for (String word : neighborMap.keySet()) {
				Map<String, Integer> neighbors = neighborMap.get(word);
				List<Entry<String, Integer>> sortedNeighbors = new ArrayList<Entry<String, Integer>>(neighbors.entrySet());
				Collections.sort(sortedNeighbors, new Comparator<Entry<String, Integer>>() {

					public int compare(Entry<String, Integer> e1,
							Entry<String, Integer> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				});
				out.write(word + ": " + sortedNeighbors + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void updateNeighbors(Map<String, Map<String, Integer> > neighborMap, Queue<String> neighbors, String token) {
		if (token.equals("clinton")) {
			if (!neighborMap.containsKey(token)) {
				neighborMap.put(token, new HashMap<String, Integer>());
			}

			for (String neighbor : neighbors) {
				if (!neighbor.equals(token) && !neighbor.contains(token) && !token.contains(neighbor)) {
					if (neighborMap.get(token).containsKey(neighbor)) {
						neighborMap.get(token).put(neighbor, neighborMap.get(token).get(neighbor)+1);
					} else {
						neighborMap.get(token).put(neighbor, 1);
					}
				}
			}
		}
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
