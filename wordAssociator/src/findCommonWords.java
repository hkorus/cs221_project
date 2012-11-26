
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class findCommonWords {

	
	  public static void main(String[] args) {
	        File file = new File("Middlemarch.txt");
	        StringBuffer contents = new StringBuffer();
	        BufferedReader reader = null;
	 
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String text = null;
	 
                Map<String, Integer> wordCount = new HashMap<String, Integer>();

	            
	            // repeat until all lines is read
	            while ((text = reader.readLine()) != null) {
	            	//System.out.println(text);
	                contents.append(text)
	                        .append(System.getProperty(
	                                "line.separator"));
	                
	                if(wordCount.containsKey(text)) {
	                	wordCount.put(text, wordCount.get(text)+1);
	                } else {
	                	wordCount.put(text, 1);
	                }
	                
	            }
	            
	            int test = Collections.max(wordCount.values());
	            System.out.println(test);
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (reader != null) {
	                    reader.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	 
	        // show file contents here
	        //System.out.println(contents.toString());
	    }
	
	
}
