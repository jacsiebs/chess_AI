package main;

import static org.junit.Assert.assertTrue;

import java.util.List;

/**
 * @author Jacob Siebert
 *
 * A few assertion methods specific for the chess context.
 * Prints useful information in addition to pass/fail. 
 */
public class ChessAssertions {

	// Tests if 2 moves are equal
	// catches the assertion exception and prints relevant info
	public static boolean assertMoveEquals(Move expected, Move actual) {
		try {
			assertTrue(expected.equals(actual));
			System.out.println("PASS -- Move: " + expected.toString());
			return true;

		} catch(AssertionError e) {
			System.out.println("FAIL -- Expected: " + expected.toString()
					+ "\n\tActual: " + actual.toString());
			return false;
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
			System.out.println("FAIL -- Move (" + expected.toString() + ") was expected to be present.");
			return false;
		}
		else if(count == 1) {
			System.out.println("PASS -- Move (" + expected.toString() + ") was present once.");
			return true;
		} else {
			System.out.println("FAIL -- Move (" + expected.toString() + ") was present " + count + " times.");
			return false;
		}
	}
	
	public static boolean assertEqualsValidMoves(List<Move> expected, List<Move> actual) {
		boolean pass = true;
		for(Move m : expected) {
			pass &= assertMoveExists(m, actual);
		}
		// test the length of the move lists
		if(expected.size() != actual.size()) {
			System.out.println("Expected validMoves to contain " + expected.size()
				+ " moves.\n\tHowever the actual validMoves contains " + actual.size() + " moves.");
			pass = false;
		}
		// print each list
		if(!pass) {
			System.out.println("Expected Moves:");
			for(Move m : expected) {
				System.out.println("\t" + m);
			}
			System.out.println("\nActual Moves:");
			for(Move m : actual) {
				System.out.println("\t" + m);
			}
		}
		return pass;
	}
}
