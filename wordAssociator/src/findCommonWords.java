import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

public class findCommonWords {
	private static int numNeighbors = 10;

	public static void main(String[] args) {
		final Map<String, Integer> wordCount = new HashMap<String, Integer>();
		final Map<String, Map<String, Integer>> neighborMap = new HashMap<String, Map<String, Integer>>();


		HashSet<String> stopWords = getStopWords();
		Queue<String> neighbors = new ArrayBlockingQueue<String>(numNeighbors);

		Map<String, Integer> listCount = new HashMap<String, Integer>();

		Vector<String> wordsOfInterest = new Vector<String>();
		wordsOfInterest.add("mother");
		wordsOfInterest.add("angry");
		wordsOfInterest.add("face");
		wordsOfInterest.add("bright");
		wordsOfInterest.add("wood");
		wordsOfInterest.add("flower");
		wordsOfInterest.add("theory");
		wordsOfInterest.add("book");
		wordsOfInterest.add("history");
		wordsOfInterest.add("transparent");
		wordsOfInterest.add("death");
		wordsOfInterest.add("count");
		wordsOfInterest.add("knowledge");
		wordsOfInterest.add("sweat");
		wordsOfInterest.add("challenge");
		wordsOfInterest.add("orange");
		wordsOfInterest.add("hostage");
		wordsOfInterest.add("war");
		wordsOfInterest.add("routine");
		wordsOfInterest.add("end");

		getWordStats(wordCount, neighborMap, stopWords, neighbors, listCount, wordsOfInterest);

		//printNeighborsSortedByFrequency(neighborMap);
		sortByTFILF(neighborMap, wordCount, listCount);
		printNeighborsSortedByFrequency(neighborMap);
		knn(neighborMap, wordCount, wordsOfInterest);
	}





	private static void getWordStats(Map<String, Integer> wordCount, Map<String, Map<String, Integer>> neighborMap, HashSet<String> stopWords, Queue<String> neighbors, Map<String, Integer> listCount, Vector<String> wordsOfInterest){
		for (int i = 1; i <=1; i++) {
			//String filename = "text" + i + ".txt";
			String filename = "wikipedia2text-extracted.txt";
			File file = new File(filename);
			Scanner scanner = null;

			try {
				scanner = new Scanner(new BufferedReader(new FileReader(file)));
				scanner.useDelimiter("[^A-Za-z]");
				String token = null;

				// repeat until all words are read
				while (scanner.hasNext()) {
					token = scanner.next().trim().toLowerCase(); //.replaceAll("[^A-Za-z]", "");
					if(!stopWords.contains(token) && token.trim().length() > 0){
						//System.out.println(token);
						if(wordCount.containsKey(token)) {
							wordCount.put(token, wordCount.get(token)+1);
						} else {
							wordCount.put(token, 1);
						}
						//update neighbors for word that you push (get all the neighbors 10 before the word)
						//and for the word that you pop (get all the neighbors 10 after the word)
						updateNeighbors(neighborMap, neighbors, token, listCount, wordsOfInterest);
						if (neighbors.size() < numNeighbors) {
							neighbors.offer(token);
						} else {
							updateNeighbors(neighborMap, neighbors, neighbors.poll(), listCount, wordsOfInterest);
							neighbors.offer(token);
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
	}

	private static void knn(Map<String, Map<String, Integer>> neighborMap,
			Map<String, Integer> wordCount, Vector<String> wordsOfInterest) {
		Map<String, List<NeighborDistance> > nnMap = new HashMap<String, List<NeighborDistance> >();

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("knn_out.txt"));


			//for (String wordA : neighborMap.keySet()) {
			for (String wordA : wordsOfInterest) {
				//String wordA = "clinton";
				//for(String wordA : wordsOfInterest){
				ArrayList<NeighborDistance> neighborDistances = new ArrayList<NeighborDistance>();
				for (String wordB : neighborMap.keySet()) {
					if (!wordA.equals(wordB)) {
						double dist = getCosDist(neighborMap.get(wordA), neighborMap.get(wordB));
						neighborDistances.add(new NeighborDistance(dist, wordB));
					}
				}

				Collections.sort(neighborDistances, new Comparator<NeighborDistance>() {

					public int compare(NeighborDistance n1, NeighborDistance n2) {
						if (n2.getDistance() - n1.getDistance() > 0) return 1;
						if (n2.getDistance() - n1.getDistance() < 0) return -1;
						return 0;
					}
				});
				int k = Math.min(20,neighborDistances.size());
				//nnMap.put(wordA, new ArrayList<NeighborDistance>(neighborDistances.subList(0, k)));

				List<NeighborDistance> kNearest = neighborDistances.subList(0, k);
				String neighbors = "";
				for (int i = 0; i < kNearest.size(); i++) {
					neighbors += kNearest.get(i).getNeighbor() + "=" + kNearest.get(i).getDistance() + " ";
				}
				out.write(wordA + ": " + neighbors + "\n");
			}
			out.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static double getDist(Map<String, Integer> neighborsA,
			Map<String, Integer> neighborsB) {
		double dist = 0.0;
		double sumA = getSum(neighborsA.values());
		double sumB = getSum(neighborsB.values());
		int numWordsInCommon = 0;
		for (String neighbor : neighborsA.keySet()) {
			if (neighborsB.containsKey(neighbor)) {
				numWordsInCommon++;
				double weightInA = neighborsA.get(neighbor)/sumA;
				double weightInB = neighborsB.get(neighbor)/sumB;

				dist = (weightInA - weightInB)*(weightInA - weightInB);

				//dist += (neighborsA.get(neighbor) + neighborsB.get(neighbor)) /2.0;
			}

			dist = (1)*dist+(0)*numWordsInCommon;
		}
		return dist;
	}

	private static double getCosDist(Map<String, Integer> neighborsA,
			Map<String, Integer> neighborsB) {
		double sumA = getSum(neighborsA.values());
		double sumB = getSum(neighborsB.values());
		double numerator = 0.0;
		double denominatorA = 0.0;
		double denominatorB = 0.0;
		for (String neighbor : neighborsA.keySet()) {
			if (neighborsB.containsKey(neighbor)) {
				numerator += neighborsA.get(neighbor) * neighborsB.get(neighbor);
			}
			denominatorA += neighborsA.get(neighbor)*neighborsA.get(neighbor);
		}
		for (String neighbor : neighborsB.keySet()) {
			denominatorB += neighborsB.get(neighbor)*neighborsB.get(neighbor);
		}
		return (numerator/Math.sqrt(denominatorA*denominatorB));
	}

	private static int getSum(Collection<Integer> values) {
		int sum = 0;
		for (Integer value : values) {
			sum += value;
		}
		return sum;
	}

	private static void printNeighborsSortedByFrequency(Map<String, Map<String, Integer>> neighborMap) {
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
				out.write(word + ": " + sortedNeighbors.subList(0, Math.min(sortedNeighbors.size()-1, 19)) + "\n");
				//System.out.println(word + ": " + sortedNeighbors + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void updateNeighbors(Map<String, Map<String, Integer> > neighborMap, Queue<String> neighbors, String token, Map<String, Integer> listCount, Vector<String> wordsOfInterest) {
		//if(wordsOfInterest.contains(token)) {
			if (!neighborMap.containsKey(token)) {
				neighborMap.put(token, new HashMap<String, Integer>());
			}

			for (String neighbor : neighbors) {
				if (!neighbor.equals(token) && !neighbor.contains(token) && !token.contains(neighbor)) {
					if (neighborMap.get(token).containsKey(neighbor)) {
						neighborMap.get(token).put(neighbor, neighborMap.get(token).get(neighbor)+1);
					} else {
						if (neighborMap.get(token).size() < 100) {
							neighborMap.get(token).put(neighbor, 1);
							if (listCount.containsKey(neighbor)){
								listCount.put(neighbor, listCount.get(neighbor) + 1);
							} else{
								listCount.put(neighbor, 1);
							}
						}

					}
				}
			}
		//}
	}


	private static void sortByTFILF(Map<String, Map<String, Integer>> neighborMap, Map<String, Integer> wordCount, Map<String, Integer> listCount){
		for (String word : neighborMap.keySet()){
			Map<String, Integer> list = neighborMap.get(word);
			for (String reWeight : list.keySet()){
				int tfilf = (int) (list.get(reWeight)*Math.log(neighborMap.keySet().size() / listCount.get(reWeight)));
				neighborMap.get(word).put(reWeight, tfilf);
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

}
