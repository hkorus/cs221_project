public class NeighborDistance {
	private final double distance;
	private final String neighbor;

	public NeighborDistance(double distance, String neighbor) {
		this.distance = distance;
		this.neighbor = neighbor;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public String getNeighbor() {
		return neighbor;
	}
}