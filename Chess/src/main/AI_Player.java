package main;

import java.util.ArrayList;

public class AI_Player implements Player {

	private final boolean human = false;
	private String name;
	private char color;
	private int maxDepth;// the max depth the min-max will search
	private char currTurn;// keeps track of which player is currently moving
	private BookSet opening_moves;

	
	/* Currently broken - will be fixed and upgraded once the chess rules are 
	 * completely tested.
	 */
	public AI_Player(char color, Board board) {
		this.color = color;
		currTurn = color;

		// set up catalog of book starts
		ArrayList<Move> bookMoves = new ArrayList<Move>();
		// temporary
		if (color == Piece.WHITE) {
			bookMoves.add(new Move(board.getPiece(1, 3), 3, 3));
			bookMoves.add(new Move(board.getPiece(1, 4), 2, 4));
		}
		opening_moves = new BookSet(bookMoves);// TODO
	}

	// Return best move for this AI.
	// First step of search only
	public Move nextMove(Board board, int maxDepth) {
		this.maxDepth = maxDepth;
		double best_sbe = -100000000.0;
		double curr_sbe;
		Move bestMove = null;
		currTurn = color;
		
		// standard ai min max
		if (opening_moves.isEmpty()) {
			// looks at the sbe values of all the root's children and returns
			// the
			// best move
			ArrayList<Move> validMoves = board.generateAllMoves(currTurn);

//			System.out.println("\n---------------------------------------------");
//			board.displayBoard();
//			System.out.println("Moves I'm considering for board:");

			for (Move m : validMoves) {
				// DELETE THIS
				System.out.println("Valid moves AI is considering:\n" + m);
				
				Board searcher = board.clone();
				// apply move, call min action unless an additional turn
				// rewarded
//				System.out.println(m);
				searcher.applyMove(m);
//				searcher.displayBoard();
				curr_sbe = minAction(searcher, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
//				System.out.println("   sbe was " + curr_sbe);
				// store best move
				if (curr_sbe > best_sbe) {
					best_sbe = curr_sbe;
					bestMove = m;
				}
			}
			System.out.println("Best Move was: " + bestMove + "\nWith an SBE of " + best_sbe);
//			System.out.println("---------------------------------------------\n");
		}
		// book moves
		else {
			bestMove = opening_moves.getNextMove();
		}
		return bestMove;
	}

	// return sbe value related to the best move for max player
	public double maxAction(Board state, int currentDepth, double alpha, double beta) {
		// if at depth limit or terminal state, return the sbe value
		if (currentDepth >= maxDepth || state.gameOver()) {
			return state.sbe();
		}
		changeTurn();
		ArrayList<Move> validMoves = state.generateAllMoves(currTurn);
		double v = -100000000.0;
		// search through successor states
		for (Move m : validMoves) {
			Board searcher = state.clone();
			// apply move, call min action unless an additional turn is
			// rewarded
			searcher.applyMove(m);
			v = Math.max(v, minAction(searcher, currentDepth + 1, alpha, beta));

//			// prune if able
//			if (v >= beta) {
//				myTurn = !myTurn;
//				return v;
//			}
			alpha = Math.max(alpha, v);
		}
		changeTurn();
		return v;
	}

	// return sbe value related to the best move for min player
	public double minAction(Board state, int currentDepth, double alpha, double beta) {
		// if at depth limit or terminal state, return the sbe value
		if (currentDepth >= maxDepth || state.gameOver()) {
//			state.displayBoard();
			return state.sbe();
		}
		changeTurn();
		double v = Double.MAX_VALUE;
		ArrayList<Move> validMoves = state.generateAllMoves(currTurn);
		for (Move m : validMoves) {
			Board searcher = state.clone();
			// apply move, call min action unless an additional turn is
			// rewarded
			searcher.applyMove(m);
			v = Math.min(v, maxAction(searcher, currentDepth + 1, alpha, beta));

//			// prune
//			if (alpha >= v) {
//				myTurn = !myTurn;
//				return v;
//			}
			beta = Math.min(beta, v);

		}
		changeTurn();
		return v;
	}

	@Override
	public boolean isHuman() {
		return human;
	}

	@Override
	public void setName(String n) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public char getColor() {
		return color;
	}
	
	private void changeTurn() {
		if(currTurn == Piece.TAN)
			currTurn = Piece.WHITE;	
		else 
			currTurn = Piece.TAN;
	}
}
