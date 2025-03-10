import java.io.FileWriter;

public class StreamMap {
	public Stream[][] map;
	public int width, height;

	// trebuiesc testate pentru a determina valorile optime
	final float PRODUCTION_WEIGHT = 0.65f;
	final float DISTANCE_WEIGHT = 1.5f;


	public StreamMap(GameMap gameMap, int myID, FileWriter writer) {
		width = gameMap.width;
		height = gameMap.height;
		map = new Stream[width][height];

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if (gameMap.getLocation(x, y).getSite().owner == myID) {
					map[x][y] = new Stream(Direction.STILL, 0, Float.MAX_VALUE);
				} else {
					if (gameMap.getLocation(x, y).getSite().production == 0) {
						map[x][y] = new Stream(Direction.STILL, 0, gameMap.getLocation(x, y).getSite().strength);
					} else {
						map[x][y] = new Stream(Direction.STILL, 0, gameMap.getLocation(x, y).getSite().strength / (gameMap.getLocation(x, y).getSite().production * PRODUCTION_WEIGHT));
					}
				}
			}
		}
	}

	// va stabili care este curentul din jurul locatiei, si ii va da locatiei curentul mai puternic cu distanta actualizata
	public void updateStream (Location location, GameMap gameMap, int myID, FileWriter writer) {
		Stream currentStream = map[location.getX()][location.getY()];
	
		// determin scorul fiecarei directii:
		// ordinea va fi: NORTH, EAST, SOUTH, WEST
		if (location.getSite().owner == myID) {
			Stream[] choices = getChoices(location, gameMap);
			Stream bestChoice = choices[0];
			float[] scores = adjustScores(choices);

			// aleg curentul
			int pos = 0;
			for (int i = 1; i < 4; i++) {
				if (scores[i] < scores[pos]) {
					pos = i;
					bestChoice = choices[i];
				}
			}
			switch (pos) {
				case 0:
					currentStream.direction = Direction.NORTH;
					break;
				case 1:
					currentStream.direction = Direction.EAST;
					break;
				case 2:
					currentStream.direction = Direction.SOUTH;
					break;
				case 3:
					currentStream.direction = Direction.WEST;
					break;
			}
			currentStream.distance = bestChoice.distance + 1;
			currentStream.destScore = bestChoice.destScore;
			
		} else {
			if (location.getSite().production != 0) {
				currentStream.destScore = location.getSite().strength / (location.getSite().production * PRODUCTION_WEIGHT);
			} else {
				currentStream.destScore = location.getSite().strength;
			}
			currentStream.distance = 0;
			currentStream.direction = Direction.STILL;
		}
		map[location.getX()][location.getY()].setStream(currentStream.direction, currentStream.distance, currentStream.destScore);
	}

	private Stream[] getChoices(Location location, GameMap gameMap) {
		Stream[] choices = new Stream[4];
		// preiau valorile tinand cont de faptul ca harta este circulara
		choices[0] = map[location.getX()][location.getY() == 0 ? gameMap.height - 1 : location.getY() - 1];
		choices[1] = map[location.getX() == gameMap.width - 1 ? 0 : location.getX() + 1][location.getY()];
		choices[2] = map[location.getX()][location.getY() == gameMap.height - 1 ? 0 : location.getY() + 1];
		choices[3] = map[location.getX() == 0 ? gameMap.width - 1 : location.getX() - 1][location.getY()];
		return choices;
	}

	private float[] adjustScores(Stream[] choices) {
		float[] scores = new float[4];
		for(int i = 0; i < 4; i++) {
			scores[i] = choices[i].destScore * (choices[i].distance + 1) * DISTANCE_WEIGHT;
		}
		return scores;
	}

	public Direction getTarget(Location location, StreamMap streamMap, GameMap gameMap, FileWriter writer) {
		Stream currentStream = streamMap.map[location.getX()][location.getY()];

		// daca distanta este 1, inseamna ca site-ul catre care ma indrept, nu este detinut de mine asa ca astept pana
		// il ocup
		if (currentStream.distance == 1) {
			Site targetSite = gameMap.getLocation(location, currentStream.direction).getSite();
			if (targetSite.strength >= location.getSite().strength) {
				return Direction.STILL;
			}
		}

		return currentStream.direction;
	}

	public void updateAllBottomRight(GameMap gameMap, int myID, FileWriter writer) {
		for (int y = gameMap.height - 1; y >= 0; y--) {
			for (int x = gameMap.width - 1; x >= 0; x--) {
				if (gameMap.getLocation(x, y).getSite().owner == myID) {
					Stream south = map[x][y == gameMap.height - 1 ? 0 : y + 1];
					Stream east = map[x == gameMap.width - 1 ? 0 : x + 1][y];
					float southScore = south.destScore * (south.distance + 1) * DISTANCE_WEIGHT;
					if (southScore == 0) {
						southScore = Float.MAX_VALUE;
					}
					float eastScore = east.destScore * (east.distance + 1) * DISTANCE_WEIGHT;
					if (eastScore == 0) {
						eastScore = Float.MAX_VALUE;
					}
					if (southScore < eastScore) {
						map[x][y].setStream(Direction.SOUTH, south.distance + 1, south.destScore);
					} else {
						map[x][y].setStream(Direction.EAST, east.distance + 1, east.destScore);
					}
				} else {
					Site tmpSite = gameMap.getLocation(x, y).getSite();
					if (tmpSite.production != 0) {
						map[x][y].destScore = tmpSite.strength / (tmpSite.production * PRODUCTION_WEIGHT);
					} else {
						map[x][y].destScore = tmpSite.strength;
					}
					map[x][y].distance = 0;
				}
			}
		}
	}
}