package player;

import java.lang.Math.*;

public class GameBoard {
	
	protected static final int DIMENSION = 8;

	protected Chip[][] grid;
	protected DList white_chips;
	protected DList black_chips;

	/**
   	* GameBoard() returns a new GameBoard with no chips on it.
   	* @param this method has no parameters
   	* @return this method has no return value
   	**/
	public GameBoard() {
		grid = new Chip[DIMENSION][DIMENSION];
		white_chips = new DList();
		black_chips = new DList();
	}
	/**
   	* cellContents() finds and returns the contents of the given (x,y) position on the GameBoard. 
   	* Unusual Conditions: When the x or y is outide of the board the method returns null
   	* @param x is an int representing the x position on the board of the cell to lookup
   	* y is an int representing the y position on the board of the cell to lookup
   	* @return returns a Chip that is in the given x,y position on the GameBoard or null
   	* if the given x and y are off the board.
   	**/
	public Chip cellContents(int x, int y) {
		if (x >= 0 && x < DIMENSION && y >= 0 && y < DIMENSION) { return grid[x][y]; }
		else return null;
	}

	public void addChip(int color, int x, int y) {
		if (x >= 0 && x < DIMENSION && y >= 0 && y < DIMENSION && grid[x][y] == null) {
			Chip toAdd = new Chip(color, x, y);
			grid[x][y] = toAdd;
			if (color == Chip.BLACK) {
				black_chips.insertBack(toAdd);
			}
			else if (color == Chip.WHITE) {
				white_chips.insertBack(toAdd);
			}
		}
		else { return; }
	}
	/**
   	* removeChip() remove a chip of the given color from position (x,y) on the GameBoard.
   	* @param color is the color of the player you want to remove the piece from
   	* x is the x position that you want to remove a chip from on the board
   	* y is the y position that you want to remove a chip from on the board
   	* @return no return value
   	**/
	public void removeChip(int color, int x, int y) throws InvalidNodeException {

		DList usedChips = getChipsofColor(color);
		DListNode node = (DListNode) usedChips.front();

		while (node.isValidNode()) {
			Chip nodeChip = (Chip) node.item();
			if (nodeChip.x == x && nodeChip.y == y) {
				node.remove();
				break;
			}
			else {node = (DListNode) node.next(); }
		}

		grid[x][y] = null;
	}
	/**
   	* getChipsofColor() returns all the chips of one player
   	* @param color is the color of the player whose chips are to be returned
   	* @return returns a DList of all the chips of the player with chips of the given color
   	**/
	public DList getChipsofColor(int color) throws InvalidNodeException{
		if (color == Chip.WHITE){
			return white_chips;}
		else if (color == Chip.BLACK){
			return black_chips;}
		else {
			return null;}
	}
	/**
	* isValidMove() Checks if a given Move m is valid for a side to perform. 
   	* @param side is the current player
   	* m is the move that is being checked for validity
   	* @return returns a DList of possible valid moves on the board
   	**/
	public boolean isValidMove(int side, Move m) throws InvalidNodeException {
		if (grid[m.x1][m.y1] != null || (m.x1 == 0 && m.y1 == 0) || 
				(m.x1 == DIMENSION-1 && m.y1 == 0) || (m.x1 == 0 && m.y1 == DIMENSION-1) || 
					(m.x1 == DIMENSION-1 && m.y1 == DIMENSION-1)) {

			return false;
		}

		if (side == Chip.WHITE && (m.y1 == 0 || m.y1 == DIMENSION-1)) {return false;} 
		if (side == Chip.BLACK && (m.x1 == 0 || m.x1 == DIMENSION-1)) {return false;}


		Chip old_chip = null;
		if (m.moveKind == Move.STEP) { 
			old_chip = grid[m.x2][m.y2];
			grid[m.x2][m.y2] = null;
		}

		DList friend = new DList();
		DList foe = new DList();

		Chip[] neighbors = (new Chip(side, m.x1, m.y1)).getNeighbors(this);


		for (int i = 0; i < neighbors.length; i++) { 
			if (neighbors[i] != null && neighbors[i].color == side) {friend.insertBack(neighbors[i]);}
			else if (neighbors[i]  !=null && neighbors[i].color == (side+1)%2) {foe.insertBack(neighbors[i]);}
		}

		if (friend.length() == 0) {
			if (m.moveKind == Move.STEP) {
				grid[m.x2][m.y2] = old_chip;
			}
			return true;
		}


		else if (friend.length() == 1) {
			Chip[] f_neighbors = ((Chip)friend.front().item()).getNeighbors(this);
			for (int i = 0; i < f_neighbors.length; i++) {
				if (f_neighbors[i] != null && f_neighbors[i].color == side) {friend.insertBack(f_neighbors[i]);}
				else if (f_neighbors[i] !=null && f_neighbors[i].color == (side+1)%2) {foe.insertBack(f_neighbors[i]);}
			}

			if (friend.length() <= 1) {
				if (m.moveKind == Move.STEP) {
					grid[m.x2][m.y2] = old_chip;
				}
				return true;
			}
		}

		if (m.moveKind == Move.STEP) {
			grid[m.x2][m.y2] = old_chip;
		}

		return false;
	}
	/**
   	* moveFinder() finds all possible moves on a given gameboard for * a given player ("side"). 
   	* Unusual Conditions: Side is not either player or opponent, then expect no useful
   	* return
   	* @param board is the gameboard on which moves are being found, 
   	* side is the current player
   	* @return returns a DList of possible valid moves on the board
   	**/
	public DList moveFinder(int side, int type) throws InvalidNodeException { 
		DList moves = new DList(); 
		if (type == Move.ADD) {
			for (int x = 0; x < DIMENSION; x++) {
				for (int y = 0; y < DIMENSION; y++) {
					if (grid[x][y] == null) {
						Move toAdd = new Move(x, y);
						if (isValidMove(side, toAdd)) {moves.insertBack(toAdd);}
					}
				}
			}
		}
		else if (type == Move.STEP) {
			DList playedChips =  new DList();
			if (side == Chip.BLACK) {playedChips = this.black_chips;}
			else if (side == Chip.WHITE) {playedChips = this.white_chips;}

			DListNode head = (DListNode) playedChips.front(); 
			for (int c = 0; c < playedChips.length(); c++) {
				for (int x = 0; x < DIMENSION; x++) {
					for (int y = 0; y < DIMENSION; y++) {
						if (grid[x][y] == null) {
							Move toAdd = new Move(x, y, ((Chip) head.item()).x, ((Chip) head.item()).y);
							if (isValidMove(side, toAdd)) {moves.insertBack(toAdd);}
						}
					}
				}
				head = (DListNode) head.next();
			}
		}

		return moves;
	}
	/**
   	* hasValidNetwork() checks "this" GameBoard to see if a network 
   	* exists for the given side
   	* Unusual Conditions: Side is not either player or opponent, then expect no useful
   	* return
   	* @param side is the the player (chip color) for whom we are 
   	* checking to see if a network exists
   	* @return returns true or false depending on if a network exists
   	**/
	public boolean hasValidNetwork(int side) throws InvalidNodeException {
		DList goalChips = new DList();
		DList goalEndChips = new DList();
		if (side == Chip.WHITE) {
			for (int y = 1; y < DIMENSION-1; y++) {
				if (grid[0][y] != null) {goalChips.insertBack(grid[0][y]);}
				if (grid[DIMENSION-1][y] != null) {goalEndChips.insertBack(grid[DIMENSION-1][y]);}
			}
		}
		else if (side == Chip.BLACK) {
			for (int x = 1; x < DIMENSION-1; x++) {
				if (grid[x][0] != null) {goalChips.insertBack(grid[x][0]);}
				if (grid[x][DIMENSION-1] != null) {goalEndChips.insertBack(grid[x][DIMENSION-1]);}
			}
		}

		if (goalChips.length() == 0 || goalEndChips.length() == 0) {
			return false;
		}
		else return networkHelper(side, goalChips, new DList()); 
	}
	/**
   	* networkHelper() helps isValidNetwork() by running through each chip and checking
   	* for connections and returning whether or not there is a network.
   	* Unusual Conditions: None
   	* @param side is the the player (chip color) for whom we are 
   	* checking to see if a network exists
   	* chips are the starting points of networks that we want to check. These will usually be the 
   	* chips at either goal.
   	* checked are the connectios that have already been checked. Checked starts off as an empty DList
   	* and grows as the method runs.
   	* @return returns true or false depending on if a network exists
   	**/
	private boolean networkHelper(int side, DList chips, DList checked) throws InvalidNodeException {
		DListNode head = (DListNode) chips.front();
		boolean toReturn = false;
		while (head.isValidNode()) {
			if (!(checkUsed((Chip) head.item(), checked))) {head = (DListNode) head.next(); continue;}
			checked.insertBack(head.item());
			
			if (side == Chip.WHITE && ((Chip)head.item()).x == DIMENSION-1 && checked.length() >= 6) {return true;}
			else if (side == Chip.BLACK && ((Chip)head.item()).y == DIMENSION-1 && checked.length() >= 6) {return true;}

			DList connections = new DList();
			if (checked.length() == 1) {connections = ((Chip) head.item()).getConnections(this, null);}
			else {connections = ((Chip) head.item()).getConnections(this, (Chip) checked.back().prev().item());}
			if (connections.length() == 0) {
				checked.back().remove();
				head = (DListNode) head.next();
				continue;
			}
			toReturn = networkHelper(side, connections, checked);
			if (toReturn) {break;}
			checked.back().remove();
			head = (DListNode) head.next();
		}
		return toReturn;
	} 

	/**
   	* checkUsed() is used in networkHelper() to check if a Chip has already been
   	* checked for a possible question or not. 
   	* Unusual Conditions: None
   	* @param toCheck is the Chip that is being checked 
   	* checked is a boolean representing whether or not the chip has been checked or not
   	* @return returns true or false depending on whether or not the chip has been checked
   	**/
	private boolean checkUsed(Chip toCheck, DList checked) throws InvalidNodeException {
		DListNode head = (DListNode) checked.front();
		while (head.isValidNode()) {
			Chip toCompare = (Chip) head.item();
			if (toCompare.color == Chip.BLACK) {
				if (toCompare.y == 0 && toCheck.y == 0) { return false; }
				else if (toCompare.y == DIMENSION-1 && toCheck.y == DIMENSION-1) { return false; }
			}
			else if (toCompare.color == Chip.WHITE) {
				if (toCompare.x == 0 && toCheck.x == 0) { return false; } 
				else if (toCompare.x == DIMENSION-1 && toCheck.x == DIMENSION-1) { return false; }
			}
			if (((Chip)head.item()).x == toCheck.x && ((Chip)head.item()).y == toCheck.y) return false;
			else head = (DListNode) head.next();
		}

		return true;
	}
	/**
   	* findBestMove() takes in a set of moves and finds the best one 
   	* among them based on the the evaluation of that move
   	* Unusual Conditions: Side is not either player or opponent, then expect no useful
   	* return
   	* @param moves is a list of moves for the GameTree search 
   	* algorithm to use, side is the current player whose moves are 
   	* being evaluated, and alpha/beta are the values which are being
   	* compared to
   	* @return the best Move out of all given moves 
   	**/
	protected Best findBestMove(int side, double alpha, double beta, 
				int depth, MachinePlayer player) throws InvalidNodeException {
	    Best myBest = new Best();
	    Best reply;

	    if (hasValidNetwork(side) || hasValidNetwork((side + 1) % 2)) {
	    	myBest.score = evalBoard(side, player, depth);
	    	return myBest;	
	    }

	    else if (depth >= player.searchDepth) {
	    	myBest.score = evalBoard(side, player, depth);
	    	return myBest;
	    }

	    if (side == player.color) {
	    	myBest.score = alpha;
	    }
	    else {
	    	myBest.score = beta;
	    }

	    int numChips = getChipsofColor(side).length();

	    if (numChips == 0) {
			int[][] goalPos = new int[6][2];
			if (side == Chip.BLACK) {
				int i = 0;
				for (int x = 1; x < DIMENSION - 1; x ++) {
					goalPos[i] = new int[] {x, 0};
					i ++;
				}
			}
			else if (side == Chip.WHITE) {
				int i = 0;
				for (int y = 1; y < DIMENSION - 1; y ++) {
					goalPos[i] = new int[] {0, y};
					i ++;
				}
			}

			myBest.score = 0;
			int[] randPos = goalPos[(new Double(Math.random() * goalPos.length)).intValue()];
			myBest.move = new Move(randPos[0], randPos[1]);

			return myBest;
		}
		else if (numChips == 1) {
			int[][] goalPos = new int[6][2];
			if (side == Chip.BLACK) {
				int i = 0;
				for (int x = 1; x < DIMENSION - 1; x ++) {
					goalPos[i] = new int[] {x, DIMENSION-1};
					i ++; 
				}
			}
			else if (side == Chip.WHITE) {
				int i = 0;
				for (int y = 1; y < DIMENSION - 1; y ++) {
					goalPos[i] = new int[] {DIMENSION-1, y};
					i++;
				}
			}
			myBest.score = 0;
			int[] randPos = goalPos[(new Double(Math.random() * goalPos.length)).intValue()];
			myBest.move = new Move(randPos[0], randPos[1]);

			return myBest;
		}

		DList legalMoves = (numChips == 10) ? moveFinder(side, Move.STEP) : moveFinder(side, Move.ADD);
		DListNode node = (DListNode) legalMoves.front();

		

		while (node.isValidNode()) {
			performMove(side, (Move) node.item());
			reply = findBestMove((side + 1) % 2, alpha, beta, depth + 1, player);
			undoMove(side, (Move) node.item());

			if (side == player.color && reply.score > myBest.score) {
				myBest.move = (Move) node.item();
				myBest.score = reply.score;
				alpha = reply.score;
			}
			else if (side == (player.color + 1) % 2 && reply.score < myBest.score) {
				myBest.move = (Move) node.item();
				myBest.score = reply.score;
				beta = reply.score;
			}
			if (alpha >= beta) { return myBest; }

			node = (DListNode) node.next();
		}
		return myBest;
  	}	
  	/**
   	* performMove() takes in a move and performs it for the specified side
   	* Unusual Conditions: None
   	* @param side is the side who should perform the move
   	* m is the Move which will be performed
   	* @return no return value
   	**/
  	protected void performMove(int side, Move m) throws InvalidNodeException {
  		if (m.moveKind == Move.ADD) {
  			addChip(side, m.x1, m.y1);
  		}
  		else if (m.moveKind == Move.STEP) {
  			removeChip(side, m.x2, m.y2);
  			addChip(side, m.x1, m.y1);
  		}
  	}
  	/**
   	* undoMove() this is the opposite of performMove. It takes in a move
   	* and manipulates the GameBoard to be in the same state before the move was performed
   	* Unusual Conditions: None
   	* @param side is the side who should perform the move
   	* m is the Move which will be undone
   	* @return no return value
   	**/
  	protected void undoMove(int side, Move m) throws InvalidNodeException {
  		if (m.moveKind == Move.ADD) {
  			removeChip(side, m.x1, m.y1);
  		}
  		else if (m.moveKind == Move.STEP) {
  			removeChip(side, m.x1, m.y1);
  			addChip(side, m.x2, m.y2);
  		}
  	}
  	/**
   	* evalMove() takes a move and assigns a quantitative value to it
   	* depending on how well it contributes to the player "side" 
   	* winning the game.
   	* Unusual Conditions: Side is not either player or opponent, then expect no useful
   	* return
   	* @param Move m is the move being evaluated, side is the current 
   	* player who is making the move
   	* @return a double value for how "good" the move is 
   	**/ 
  	protected double evalBoard(int side, MachinePlayer player, int depth) throws InvalidNodeException {
  		DList sideChips = getChipsofColor(side);
  		DList opponentChips = getChipsofColor((side + 1) % 2);

  		int sideScore = 0;
  		int networkScore = 1;
  		int opponentScore = 0;
  		int opponentNetworkScore = 1;

  		if (hasValidNetwork(side)) {
  			networkScore = Integer.MAX_VALUE / depth;
  		}

  		if (hasValidNetwork((side + 1) % 2)) {
  			opponentNetworkScore = Integer.MAX_VALUE / depth;
  		}

  		DListNode sideNode = (DListNode) sideChips.front();
  		while (sideNode.isValidNode()) {
  			sideScore += ((Chip) sideNode.item()).getConnections(this, null).length();
  			sideNode = (DListNode) sideNode.next();
  		}

  		DListNode opponentNode = (DListNode) opponentChips.front();
  		while (opponentNode.isValidNode()) {
  			opponentScore += ((Chip) opponentNode.item()).getConnections(this, null).length();
  			opponentNode = (DListNode) opponentNode.next();
  		}
	  	double totalScore = (networkScore - opponentNetworkScore * 1.0) + (sideScore - opponentScore);
  		return (side == player.color) ? totalScore : -1 * totalScore;
  	}


  	
	/**public static void main(String[] args) {
		GameBoard testBoard = new GameBoard();
		testBoard.addChip(0, 1, 1);
		testBoard.addChip(0, 2, 2);
		try {
			System.out.println("***************TESTING MOVE VALIDATOR AND FINDER****************");
			System.out.println("Should be false: " + testBoard.isValidMove(0, new Move(3, 3)));
			System.out.println("Should be false: " + testBoard.isValidMove(0, new Move(0, 1)));
			System.out.println("Should be true: " + testBoard.isValidMove(1, new Move(3, 3)));
			System.out.println("Should be true: " + testBoard.isValidMove(0, new Move(4, 4)));
			System.out.println("Should be true: " + testBoard.isValidMove(0, new Move(1, 2, 2, 2)));
			System.out.println("Should be false: " + testBoard.isValidMove(0, new Move(7, 7)));
			System.out.println("Should be true: " + testBoard.isValidMove(0, new Move(4, 0)));
			System.out.println("Should be true: " + testBoard.isValidMove(1, new Move(0, 5)));

			DList validMoves = testBoard.moveFinder(0, Move.ADD);
			DListNode first = (DListNode) validMoves.front();
			for (int i = 0; i < validMoves.length(); i++) {
				System.out.print(first.item() + " ");
				first = (DListNode) first.next();
			}
		}
		catch (InvalidNodeException e) {
			System.err.println(e);
		}

		try {
			GameBoard testBoard2 = new GameBoard();
			testBoard2.addChip(0, 6, 0);
			testBoard2.addChip(0, 6, 2);
			testBoard2.addChip(0, 5, 2);
			testBoard2.addChip(0, 3, 4);
			testBoard2.addChip(0, 2, 4);
			testBoard2.addChip(0, 2, 6);
			testBoard2.addChip(0, 2, 7);
			testBoard2.addChip(0, 4, 6);
			testBoard2.addChip(0, 4, 7); 

			System.out.println("***************TESTING CONNECTIONS****************");


			DList connects = testBoard2.cellContents(2, 4).getConnections(testBoard2, testBoard2.cellContents(3, 4));
			DListNode head = (DListNode) connects.front();
			while (head.isValidNode()) {
				System.out.println("\n" + ((Chip) head.item()).x + " " + ((Chip) head.item()).y);
				head = (DListNode) head.next();
			}

			System.out.println("***************TESTING NETWORK IDENTIFIER****************");

			System.out.println("Should be true: " + testBoard2.hasValidNetwork(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			GameBoard testBoard3 = new GameBoard();
			GameBoard testBoard4 = new GameBoard();
			GameBoard testBoard5 = new GameBoard();
			testBoard5.addChip(0, 2, 0); //TEST ONE True
			testBoard5.addChip(0, 2, 5);
			testBoard5.addChip(0, 3, 5);
			testBoard5.addChip(0, 3, 3);
			testBoard5.addChip(0, 5, 5);
			testBoard5.addChip(0, 4, 5);
			testBoard5.addChip(0, 3, 5);
			testBoard5.addChip(0, 5, 5);
			testBoard5.addChip(0, 6, 5);
			testBoard5.addChip(0, 2, 7);
			testBoard5.addChip(0, 4, 7); 

			testBoard4.addChip(0, 2, 0); //TEST TWO False
			testBoard4.addChip(0, 4, 2);
			testBoard4.addChip(0, 1, 5);
			testBoard4.addChip(0, 6, 5);
			testBoard4.addChip(0, 6, 2);
			testBoard4.addChip(0, 4, 7); 

			testBoard3.addChip(0, 2, 0); //TEST THREE False
			testBoard3.addChip(0, 6, 0);
			testBoard3.addChip(0, 6, 5);
			testBoard3.addChip(0, 5, 5);
			testBoard3.addChip(0, 5, 7);
			testBoard3.addChip(0, 4, 7);

			System.out.println("Should be true: " + testBoard5.hasValidNetwork(0));
			System.out.println("Should be false: " + testBoard4.hasValidNetwork(0));
			System.out.println("Should be false: " + testBoard3.hasValidNetwork(0));


			GameBoard testBoard6 = new GameBoard();
			testBoard6.addChip(1, 0, 3);
			testBoard6.addChip(1, 0, 6);
			testBoard6.addChip(1, 1, 3);
			testBoard6.addChip(1, 2, 5);
			testBoard6.addChip(1, 2, 6);
			testBoard6.addChip(1, 4, 3);
			testBoard6.addChip(1, 5, 1);
			testBoard6.addChip(1, 5, 6);
			testBoard6.addChip(1, 6, 3);
			testBoard6.addChip(1, 7, 2);

			testBoard6.addChip(0, 3, 0);
			testBoard6.addChip(0, 2, 1);
			testBoard6.addChip(0, 6, 1);
			testBoard6.addChip(0, 4, 2);
			testBoard6.addChip(0, 2, 4);
			testBoard6.addChip(0, 6, 5);
			testBoard6.addChip(0, 3, 6);
			testBoard6.addChip(0, 1, 7);
			testBoard6.addChip(0, 4, 7);
			testBoard6.addChip(0, 6, 7);
			System.out.println("Should be true: " + testBoard6.hasValidNetwork(1));
			System.out.println("Should be false: " + testBoard6.hasValidNetwork(0));

			GameBoard board3 = new GameBoard();
			board3.addChip(0,6,0);
			board3.addChip(0,2,0);
			board3.addChip(0,4,2);
			board3.addChip(0,3,3);
			board3.addChip(0,3,5);
			board3.addChip(0,5,7);
			System.out.println("false: " + board3.hasValidNetwork(0));
			GameBoard board4 = new GameBoard();
			board4.addChip(0,2,0);
			board4.addChip(0,4,2);
			board4.addChip(0,6,0);
			board4.addChip(0,6,5);
			board4.addChip(0,5,5);
			board4.addChip(0,5,7);
			System.out.println("false: " + board4.hasValidNetwork(0));
			GameBoard board5 = new GameBoard();
			board5.addChip(0,4,2);
			board5.addChip(0,4,4);
			board5.addChip(0,4,5);
			board5.addChip(1,4,6);
			board5.addChip(1,5,3);
			board5.addChip(1,5,6);
			board5.addChip(0,6,0);
			board5.addChip(0,6,1);
			board5.addChip(0,6,3);
			board5.addChip(0,6,4);
			board5.addChip(0,6,6);
			board5.addChip(0,6,7);
			//System.out.println(((Chip) (new Chip(0, 4, 2).getConnections(board5, new Chip(0, 6, 0))).back().item()).x);
			/** DList nodes = board5.hasValidNetwork2(0);
			DListNode node = (DListNode) nodes.front();
			while (nodes != null && node.isValidNode()) {
				System.out.println(((Chip) node.item).x + " " + ((Chip) node.item()).y);
				node = (DListNode) node.next();
			} **/
			/**System.out.println("false: " + board5.hasValidNetwork(0));


			GameBoard testBoard7 = new GameBoard();
			testBoard7.addChip(0, 1, 0);
			testBoard7.addChip(0, 1, 1);
			testBoard7.addChip(0, 3, 1);
			testBoard7.addChip(0, 3, 2);
			testBoard7.addChip(0, 6, 5);
			//testBoard7.addChip(0, 6, 7);

			//System.out.println("testBoard7 eval: " + testBoard7.evalBoard(0));
			System.out.println("testBoard7 findBestMove (Should be add 67 or 47): " + testBoard7.findBestMove(0,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, new MachinePlayer(0)).move);

			System.out.println(Integer.MIN_VALUE > Double.MAX_VALUE);

			GameBoard testBoard8 = new GameBoard();
			testBoard8.addChip(0, 1, 0);
			testBoard8.addChip(0, 1, 1);
			//testBoard8.addChip(0, 3, 1);
			testBoard8.addChip(0, 3, 2);
			testBoard8.addChip(0, 6, 5);
			testBoard8.addChip(0, 6, 7);
			testBoard8.addChip(1, 0, 5);
			testBoard8.addChip(1, 0, 3);

			System.out.println("testBoard8 findBestMove (Should be add 31 or 21): " + testBoard8.findBestMove(1,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, new MachinePlayer(1)).move);

			GameBoard testBoard9 = new GameBoard();
			testBoard9.addChip(0, 1, 0);
			testBoard9.addChip(1, 0, 1);
			//testBoard9.addChip(1, 4, 1);
			testBoard9.addChip(1, 6, 1);
			testBoard9.addChip(1, 2, 2);
			testBoard9.addChip(1, 2, 3);
			testBoard9.addChip(0, 3, 4);
			testBoard9.addChip(0, 6, 4);
			testBoard9.addChip(1, 7, 4);
			testBoard9.addChip(0, 1, 6);
			testBoard9.addChip(0, 6, 7);

			System.out.println("testBoard9 findBestMove (should be 15):" + testBoard9.findBestMove(1,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, new MachinePlayer(1, 2)).move);

			GameBoard testBoard10 = new GameBoard();
			testBoard10.addChip(0, 2, 0);
			testBoard10.addChip(1, 0, 1);
			//testBoard9.addChip(1, 4, 1);
			testBoard10.addChip(0, 3, 1);
			testBoard10.addChip(1, 5, 1);
			testBoard10.addChip(1, 4, 2);
			//testBoard10.addChip(0, 2, 3);
			testBoard10.addChip(0, 5, 3);
			testBoard10.addChip(1, 0, 4);
			testBoard10.addChip(1, 1, 5);
			testBoard10.addChip(0, 3, 5);
			testBoard10.addChip(0, 2, 7);

			nodes = testBoard10.hasValidNetwork2(0);
			node = (DListNode) nodes.front();
			while (nodes != null && node.isValidNode()) {
				System.out.println(((Chip) node.item).x + " " + ((Chip) node.item()).y);
				node = (DListNode) node.next();
			}

			System.out.println("testBoard10 findBestMove (should be 34):" + testBoard10.findBestMove(1,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, new MachinePlayer(1, 2)).move);

			GameBoard testBoard11 = new GameBoard();
			testBoard11.addChip(1, 0, 2);
			testBoard11.addChip(0, 1, 1);
			//testBoard9.addChip(1, 4, 1);
			testBoard11.addChip(1, 1, 2);
			testBoard11.addChip(1, 1, 5);
			testBoard11.addChip(0, 1, 6);
			//testBoard11.addChip(1, 7, 2);
			testBoard11.addChip(0, 2, 1);
			testBoard11.addChip(0, 4, 1);
			testBoard11.addChip(1, 4, 2);
			testBoard11.addChip(1, 4, 5);
			testBoard11.addChip(0, 5, 1);

			System.out.println("true: " + testBoard11.hasValidNetwork(1));

			System.out.println(testBoard11.isValidMove(1, new Move(7, 2)));

			DList validMoves = testBoard11.moveFinder(1, Move.ADD);
			DListNode first = (DListNode) validMoves.front();
			for (int i = 0; i < validMoves.length(); i++) {
				System.out.print(first.item() + " ");
				first = (DListNode) first.next();
			}

			System.out.println("testBoard11 findBestMove (should be 72 or 75):" + testBoard11.findBestMove(1,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, new MachinePlayer(1, 2)).move);
			System.out.println(testBoard11.evalBoard(1, new MachinePlayer(1), 1));



		} catch (Exception e) {
			e.printStackTrace();
		}

	}**/


}

class Best {

  protected Move move;
  protected double score;

  public Best() {
  	move = null;
  	score = 0;
  }

}