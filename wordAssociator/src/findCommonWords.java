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
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class findCommonWords {
	private static int numNeighbors = 10;

	public static void main(String[] args) {
		final Map<String, Integer> wordCount = new HashMap<String, Integer>();
		final Map<String, Map<String, Integer> > neighborMap = new HashMap<String, Map<String, Integer>>();

		HashSet<String> stopWords = getStopWords();
		Queue<String> neighbors = new ArrayBlockingQueue<String>(numNeighbors);

		Map<String, Integer> listCount = new HashMap<String, Integer>();
		for (int i = 1; i <= 6; i++) {
			String filename = "text" + i + ".txt";

			File file = new File(filename);
			Scanner scanner = null;

			try {
				scanner = new Scanner(new BufferedReader(new FileReader(file)));
				scanner.useDelimiter("[^A-Za-z]");
				String token = null;

				// repeat until all words are read
				while (scanner.hasNext()) {
					token = scanner.next().trim().toLowerCase(); //.replaceAll("[^A-Za-z]", "");
					if(!stopWords.contains(token)){
						if(wordCount.containsKey(token)) {
							wordCount.put(token, wordCount.get(token)+1);
						} else {
							wordCount.put(token, 1);
						}



						//update neighbors for word that you push (get all the neighbors 10 before the word)
						//and for the word that you pop (get all the neighbors 10 after the word)
						updateNeighbors(neighborMap, neighbors, token, listCount);
						if (neighbors.size() < numNeighbors) {
							neighbors.offer(token);
						} else {
							updateNeighbors(neighborMap, neighbors, neighbors.poll(), listCount);
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

		printNeighborsSortedByFrequency(neighborMap);
		sortByTFILF(neighborMap, wordCount, listCount);
		knn(neighborMap, wordCount);

	}

	private static void knn(Map<String, Map<String, Integer>> neighborMap,
			Map<String, Integer> wordCount) {
		Map<String, List<NeighborDistance> > nnMap = new HashMap<String, List<NeighborDistance> >();

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("knn_out.txt"));

			String wordA = "mother";
			//for (String wordA : neighborMap.keySet()) {
			ArrayList<NeighborDistance> neighborDistances = new ArrayList<NeighborDistance>();
			for (String wordB : neighborMap.keySet()) {
				if (!wordA.equals(wordB)) {
					double dist = getDist(neighborMap.get(wordA), neighborMap.get(wordB));
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
			int k = Math.min(numNeighbors,neighborDistances.size());
			//nnMap.put(wordA, new ArrayList<NeighborDistance>(neighborDistances.subList(0, k)));

			List<NeighborDistance> kNearest = neighborDistances.subList(0, k);
			String neighbors = "";
			for (int i = 0; i < kNearest.size(); i++) {
				neighbors += kNearest.get(i).getNeighbor() + "=" + kNearest.get(i).getDistance() + " ";
			}
			out.write(wordA + ": " + neighbors + "\n");
			//}
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
		for (String neighbor : neighborsA.keySet()) {
			if (neighborsB.containsKey(neighbor)) {
				dist += (neighborsA.get(neighbor) + neighborsB.get(neighbor)) /2.0;
			}
		}
		return dist;
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
				out.write(word + ": " + sortedNeighbors + "\n");
				//System.out.println(word + ": " + sortedNeighbors + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void updateNeighbors(Map<String, Map<String, Integer> > neighborMap, Queue<String> neighbors, String token, Map<String, Integer> listCount) {
		if (token.equals("mother")) {
			if (!neighborMap.containsKey(token)) {
				neighborMap.put(token, new HashMap<String, Integer>());
			}

			for (String neighbor : neighbors) {
				if (!neighbor.equals(token) && !neighbor.contains(token) && !token.contains(neighbor)) {
					if (neighborMap.get(token).containsKey(neighbor)) {
						neighborMap.get(token).put(neighbor, neighborMap.get(token).get(neighbor)+1);
					} else {
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
