public class Stream {
	Direction direction;
	int distance;
	float destScore;

	public Stream(Direction direction, int distance, float destScore) {
		this.direction = direction;
		this.distance = distance;
		this.destScore = destScore;
	}

	public void setStream(Direction direction, int distance, float destScore) {
		this.direction = direction;
		this.distance = distance;
		this.destScore = destScore;
	}
}
