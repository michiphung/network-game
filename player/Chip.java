package player;


public class Chip {
	
	public static final int WHITE = 1;
	public static final int BLACK = 0;

	protected int x;
	protected int y;
	protected int color;

	public Chip(int color, int x, int y) {
		this.color = color;
		this.x = x;
		this.y = y;
	}
	/**
    * getNeighbors finds all neighbors of the current chip in order to identify 
    * clusters
    * @param takes in the current gameboard state and the side for which we are 
    * looking for neighbors
    * @return returns an array of chips which are neighboring "this" chip
   	**/
	public Chip[] getNeighbors(GameBoard gameboard) { 
		Chip[] neighbors = new Chip[8];

		neighbors[0] = gameboard.cellContents(x-1, y-1);
		neighbors[1] = gameboard.cellContents(x, y-1);
		neighbors[2] = gameboard.cellContents(x+1, y-1);
		neighbors[3] = gameboard.cellContents(x-1, y);
		neighbors[4] = gameboard.cellContents(x+1, y);
		neighbors[5] = gameboard.cellContents(x+1, y+1);
		neighbors[6] = gameboard.cellContents(x, y+1);
		neighbors[7] = gameboard.cellContents(x-1, y+1);

		return neighbors;
	}
	/**
   	* getConnections finds all chips that "this" chip is connected 
   	* to according to the rules of the game
   	* Unusual Conditions: Side is not either player or opponent or previous chip is not * of the same side, then expect no useful return
   	* @param takes in the current gameboard state for which it is
   	* finding connected chips and the side for which we are looking 
   	* for connected chips, previous chip to account for certain rules for network 
   	* existence
   	* @return returns an array of chips for which "this" chip is 
   	* connected to
   	**/
	public DList getConnections(GameBoard gameboard, Chip prev) throws InvalidNodeException {

		DList connectedChips = new DList();
		int x = this.x; 
		int y = this.y;
		int fromX = this.x;
		int fromY = this.y;
		int toX = this.x;
		int toY = this.y;
		if (prev != null) {

			if ((prev.x > this.x && prev.y > this.y) || (prev.x < this.x && prev.y < this.y)) {
				fromY = this.y + 1; toY = this.y - 1;
				fromX = this.x + 1; toX = this.x - 1;
			}
			else if ((prev.x < this.x && prev.y > this.y) || (prev.x > this.x && prev.y < this.y)) {
				fromY = this.y - 1; toY = this.y + 1;
				fromX = this.x + 1; toX = this.x - 1;
			}
			else if (prev.x == this.x) { toY = this.y + 1; fromY = this.y - 1; }
			else if (prev.y == this.y) { toX = this.x + 1; fromX = this.x - 1; }
		}


		for (int i = -1; i < 2; i ++) {
			inner:
			for (int j = -1; j < 2; j ++) {
				if (i == 0 && j == 0)  continue;
				while (x + i >= 0 && y + j >= 0 && x + i < GameBoard.DIMENSION && y + j < GameBoard.DIMENSION) {
					x = x + i; y = y + j;
					if ((x == fromX && y == fromY) || (x == toX && y == toY)) break;
					else if (gameboard.cellContents(x, y) != null) {
						if (gameboard.cellContents(x, y).color == this.color) {
							connectedChips.insertBack(gameboard.cellContents(x, y));
							break;
						}
						else if (gameboard.cellContents(x, y).color == (this.color+1)%2) {
							break;
						}
					}
				}
				x = this.x; y = this.y;
			}
		}

		return connectedChips;
	}

	public static void main (String[] args) {
		GameBoard testBoard = new GameBoard();
		/**testBoard.addChip(0, 1, 1);
		testBoard.addChip(0, 4, 0);
		testBoard.addChip(0, 4, 1);
		testBoard.addChip(0, 0, 2);
		testBoard.addChip(0, 5, 2);
		testBoard.addChip(0, 4, 4);
		testBoard.addChip(0, 0, 4);
		testBoard.addChip(0, 2, 6);
		testBoard.addChip(0, 2, 2); **/

		testBoard.addChip(0, 4, 4);
		testBoard.addChip(0, 3, 3);
		testBoard.addChip(0, 3, 4);
		testBoard.addChip(0, 3, 5);
		testBoard.addChip(0, 4, 3);
		testBoard.addChip(0, 4, 5);
		testBoard.addChip(0, 5, 3);
		testBoard.addChip(0, 5, 4);
		testBoard.addChip(0, 6, 5);




		try {
			DList connected = (new Chip(0, 4, 4)).getConnections(testBoard, new Chip(0, 1, 1));
			DListNode head = (DListNode) connected.front();
			for (int i = 0; i < connected.length(); i++) {
				System.out.println(((Chip)head.item()).x + " " + ((Chip)head.item()).y);
				head = (DListNode) head.next();
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}