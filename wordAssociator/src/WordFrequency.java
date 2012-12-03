public class WordFrequency {
	
	private final String word;
	private int frequency;

	public WordFrequency(String word) {
		this.word = word;
		frequency = 1;
	}
	
	public void incrementFreqCount() {
		frequency++;
	}
	
	public String getWord() {
		return word;
	}
	
	public int getFrequency() {
		return frequency;
	}
}