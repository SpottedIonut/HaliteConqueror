import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class BotV1 {
    public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        // debug in file called output.txt
        File file = new File("test.txt");
        FileWriter writer = new FileWriter(file);

        StreamMap streamMap = new StreamMap(gameMap, myID, writer);

        


        Networking.sendInit("MyJavaBot");

        int turn = 0;

        while (true) {
            List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);
            writer.write("Turn: " + ++turn + "\n");

            streamMap.updateAllBottomRight(gameMap, myID, writer);

            for (int y = 0; y < gameMap.height; y++) {
                for (int x = 0; x < gameMap.width; x++) {
                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();
                    if (site.owner == myID) {
                        // incep logica de decizie

                        // updatez streamul curent
                        streamMap.updateStream(location, gameMap, myID, writer);

                        // intai voi acumula cateva ture de halite
                        if (site.strength < site.production * 3) {
                            moves.add(new Move(location, Direction.STILL));
                        } else {
                            // in functie de fluxul din jurul locatiei, voi alege "curentul" cel mai puternic care este
                            // caracterizat de scorul cel mai mic
                            Direction targetDirection = streamMap.getTarget(location, streamMap, gameMap, writer);
                            try {
                                writer.write("Target direction: " + targetDirection + "\n");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            moves.add(new Move(location, targetDirection));
                        }
                    } else {
                        streamMap.map[x][y].destScore = site.strength / (site.production * streamMap.PRODUCTION_WEIGHT);
//                        writer.write(streamMap.map[x][y].destScore + " ");
                    }
                    // writer.write(streamMap.map[x][y].direction + " ");
                }
                // writer.write("\n");
            }
            Networking.sendFrame(moves);
        }
    }
}