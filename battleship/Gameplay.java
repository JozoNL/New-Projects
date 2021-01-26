package battleship;

/**
 * Class used for playing the game.
 */
public class Gameplay {
    public static void main(String[] args) {
        Field[] players = {new Field("Player 1"), new Field("Player 2")};
        for (Field player : players) {
            player.initShips(player.getName());
            Field.makeSpace();
        }
        boolean valid;
        int turn = 1;

        loop: while (true) {
            for (Field player : players) {
                player.printFields();
                System.out.printf("%s, it's your turn:\n", player.getName());
                do {
                    valid = (turn % 2 == 1) ? players[0].shoot(players[1].getOur())
                            : players[1].shoot(players[0].getOur());
                } while (!valid);
                if (player.getAlive() == 0) {
                    break loop;
                }
                Field.makeSpace();
                turn++;
            }
        }
    }
}
