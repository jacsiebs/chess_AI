package jacob.siebert.chessai.assertion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.board.Square;
import jacob.siebert.chessai.move.Move;
import jacob.siebert.chessai.piece.King;
import jacob.siebert.chessai.type.PieceColor;

/**
 * @author Jacob Siebert
 *
 * A few assertion methods specific for the chess context.
 * Prints useful information in addition to pass/fail. 
 */
public class ChessAssertions {

	// Tests if 2 moves are equal
	// catches the assertion jacob.siebert.chessai.exception and prints relevant info
	public static boolean assertMoveEquals(Move expected, Move actual) {
			if(expected.equals(actual)) {
				System.out.println(" PASS -- Move: " + expected.toString());
				return true;
			} else {

				System.out.println(" FAIL -- Expected: " + expected.toString()
						+ "\n\t Actual: " + actual.toString());
				throw new AssertionError();
			}
	}
	
	// Tests if the Move exists in the validMoves list.
	// Additionally checks if any duplicate moves are in the list.
	// The equals methods of each Move subclass handles comparing a Move to one of its subclasses
	public static boolean assertMoveExists(Move expected, List<Move> validMoves) {
		int count = 0;
		
		for(Move m : validMoves) {
			if(m.equals(expected)) {
				count++;
			}
		}
		if(count == 0) {
			System.out.println(" FAIL -- Move (" + expected.toString() + ") was expected to be present.");
			throw new AssertionError();
		}
		else if(count == 1) {
			System.out.println(" PASS -- Move (" + expected.toString() + ") was present once.");
			return true;
		} else {
			System.out.println(" FAIL -- Move (" + expected.toString() + ") was present " + count + " times.");
			throw new AssertionError();
		}
	}
	
	/**
	 * @param expected
	 * @param actual - will always be provided from generateValidMoves() and thus will never be null, only empty
	 * @return
	 */
	public static boolean assertEqualsValidMoves(List<Move> expected, List<Move> actual) {
		
		if(expected == null) {
			if(!actual.isEmpty()) {
				System.out.println(" FAIL -- Expected no moves to be valid.\n  Moves that were generated:");
				for(Move m : actual) {
					System.out.println("   " + m);
				}
				throw new AssertionError();
			} else {
				System.out.println(" PASS -- No moves were expected and none were generated.");
				return true;
			}
		}
		
		boolean pass = true;
		for(Move m : expected) {
			try {
				assertMoveExists(m, actual); 
			} catch(AssertionError e) {
				pass = false;
			}
		}
		// test the length of the move lists
		if(expected.size() != actual.size()) {
			System.out.println(" Expected validMoves to contain " + expected.size()
				+ " moves.\n\t However the actual validMoves contains " + actual.size() + " moves.");
			pass = false;
		}
		// print each list
		if(!pass) {
			System.out.println(" Expected Moves:");
			for(Move m : expected) {
				System.out.println("\t " + m);
			}
			System.out.println("\n Actual Moves:");
			for(Move m : actual) {
				System.out.println("\t " + m);
			}
			throw new AssertionError();
		}
		return pass;
	}

	public static void isInCheckmate(Board board, PieceColor color) {
		if(!board.isInCheckMate(color)) {
			System.out.println("FAIL -- Expected a checkmate!");
			throw new AssertionError();
		}
	}

	public static void assertIsInCheck(Board board, King k) {
		if(!board.isInCheck(k)) {
			System.out.println("FAIL -- " + k.toString() + " was expected " +
					"to be in check.");
			throw new AssertionError();
		}
	}

	public static void assertIsNotInCheck(Board board, King k) {
		if(board.isInCheck(k)) {
			System.out.println("FAIL -- " + k.toString() + " was expected " +
					"to not be in check.");
			throw new AssertionError();
		}
	}
	
	public static void assertEqualsThreatPaths(ArrayList<ArrayList<Square>> expected,
			ArrayList<ArrayList<Square>> actual) {
		boolean pass;
		boolean match;
		
		for(ArrayList<Square> pathExpected : expected) {
			pass = false;
			for(ArrayList<Square> pathActual : actual) {
				match = true;
				Iterator<Square> itr_act = pathActual.iterator();
				for(Square ex : pathExpected) {
					if(!itr_act.hasNext() || !ex.equals(itr_act.next())) {
						match = false;
						break;
					}
				}
				if(match) {
					pass = true;
					break;
				}
			}
			if(!pass) {
				System.out.println(" FAIL -- The threat paths do not match");
				throw new AssertionError();
			}
		}
		System.out.println(" PASS -- All threat paths match.");
	}
}
