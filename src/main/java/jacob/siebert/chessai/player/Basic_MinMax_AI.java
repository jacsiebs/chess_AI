package jacob.siebert.chessai.player;

import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Move;
import jacob.siebert.chessai.type.PieceColor;

import java.util.ArrayList;

public class Basic_MinMax_AI extends Player implements MinMax_AI {

	private final static boolean HUMAN = false;
	private PieceColor currTurn;// keeps track of which player is currently moving

	/* Currently broken - will be fixed and upgraded once the chess rules are 
	 * completely tested.
	 */
	public Basic_MinMax_AI(PieceColor color, Board board) {
		super(color);
		currTurn = color;
	}

	// Return best move for this AI.
	// First step of search only
	public Move nextMove(Board board, int maxDepth) {
		double best_sbe = Double.MIN_VALUE;
		double curr_sbe;
		Move bestMove = null;
		currTurn = getColor();
		
//		// standard ai min max
//		if (opening_moves.isEmpty()) {
//			// looks at the sbe values of all the root's children and returns
//			// the
//			// best move
//			ArrayList<Move> validMoves = null;//TODO board.generateAllMoves(currTurn);
//
////			System.out.println("\n---------------------------------------------");
////			board.printBoard();
////			System.out.println("Moves I'm considering for board:");
//
//			for (Move m : validMoves) {
//				// DELETE THIS
//				System.out.println("Valid moves AI is considering:\n" + m);
//
//				Board searcher = board.clone();
//				// apply move, call min action unless an additional turn
//				// rewarded
////				System.out.println(m);
//				searcher.applyMove(m);
////				searcher.printBoard();
//				curr_sbe = minAction(searcher, 1, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
////				System.out.println("   sbe was " + curr_sbe);
//				// store best move
//				if (curr_sbe > best_sbe) {
//					best_sbe = curr_sbe;
//					bestMove = m;
//				}
//			}
//			System.out.println("Best Move was: " + bestMove + "\nWith an SBE of " + best_sbe);
////			System.out.println("---------------------------------------------\n");
//		}

		return bestMove;
	}

	// return sbe value related to the best move for max player
	private double maxAction(Board state, int currentDepth, int maxDepth, double alpha, double beta) {
		// if at depth limit or terminal state, return the sbe value
		if (currentDepth >= maxDepth || state.gameOver()) {
			return state.sbe();
		}
		changeTurn();
		ArrayList<Move> validMoves = null;//TODO state.generateAllMoves(currTurn);
		double v = -100000000.0;
		// search through successor states
		for (Move m : validMoves) {
			Board searcher = state.clone();
			// apply move, call min action unless an additional turn is
			// rewarded
			searcher.applyMove(m);
			v = Math.max(v, minAction(searcher, currentDepth + 1, maxDepth, alpha, beta));

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
	private double minAction(Board state, int currentDepth, int maxDepth, double alpha, double beta) {

		// if at depth limit or terminal state, return the sbe value
		if (currentDepth >= maxDepth || state.gameOver()) {
//			state.printBoard();
			return state.sbe();
		}
		changeTurn();
		double v = Double.MAX_VALUE;
		ArrayList<Move> validMoves = null;//TODO state.generateAllMoves(currTurn);
		for (Move m : validMoves) {
			Board searcher = state.clone();
			// apply move, call min action unless an additional turn is
			// rewarded
			searcher.applyMove(m);
			v = Math.min(v, maxAction(searcher, currentDepth + 1, maxDepth, alpha, beta));

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

	public boolean isHuman() {
		return HUMAN;
	}
	
	private void changeTurn() {
		if(currTurn == PieceColor.TAN)
			currTurn = PieceColor.WHITE;
		else 
			currTurn = PieceColor.TAN;
	}
}
