/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  protected int color;
  protected int searchDepth;
  protected GameBoard gameboard;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.color = color;
    this.searchDepth = 2;
    this.gameboard = new GameBoard();
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    this.color = color;
    this.searchDepth = searchDepth;
    this.gameboard = new GameBoard();
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    try {
      Best bestMove = gameboard.findBestMove(color, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 
          0, this);
      gameboard.performMove(color, bestMove.move);
      //System.out.println("Bestmove Score " + bestMove.score);
      return bestMove.move;
    } catch (InvalidNodeException e) {
      e.printStackTrace();
    }
    return null;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    try {
      if (gameboard.isValidMove((color + 1) % 2, m)) {
        gameboard.performMove((color + 1) % 2, m);
        return true;
      }
      return false;
    } catch (InvalidNodeException e) {
      e.printStackTrace();
    }
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    try {
      if (gameboard.isValidMove(color, m)) {
        gameboard.performMove(color, m);
        return true;
      }
      return false;
    } catch (InvalidNodeException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static void main (String[] args) {
    MachinePlayer machine0 = new MachinePlayer(0, 1);
    machine0.gameboard.addChip(0, 1, 0);
    machine0.gameboard.addChip(0, 1, 1);
    machine0.gameboard.addChip(0, 3, 1);
    machine0.gameboard.addChip(0, 3, 2);
    machine0.gameboard.addChip(0, 6, 5);

    System.out.println(machine0.chooseMove());
  }


}


