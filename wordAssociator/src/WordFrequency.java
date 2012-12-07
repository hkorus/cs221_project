public class WordFrequency {
	
	private final String word;
	private int frequency;

	public WordFrequency(String word) {
		this.word = word;
		frequency = 1;
	}
	
	public WordFrequency(String word, int frequency) {
		this.word = word;
		this.frequency = frequency;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordFrequency other = (WordFrequency) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	
}