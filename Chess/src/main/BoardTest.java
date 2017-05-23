package main;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A suite of tests for the board class.
 * The Board class is responsible for:
 * 1. Generating move lists
 * 2. Detecting checks/checkmates
 * 3. Making moves (must efficiently update the valid moves lists)
 * 4. Undoing moves(must efficiently reverse the valid moves lists)
 * 
 * @author Jacob Siebert
 */
public class BoardTest {

	Board sut;// system under test
	Piece[][] board;// points to the Piece[][] in the sut Board
	
	@Before
	public void setUp() throws Exception {
		sut = new Board(Board.EMPTY);
		board = sut.getBoard();
	}
	
	// remove references
	@After
	public void tearDown() throws Exception {
		sut = null;
		board = null;
	}

	@Test
	public void tanEnPassantTest() {
		System.out.println("\nTan EnPassant Test:");
		//Set up
		Pawn captured = new Pawn(Piece.WHITE, 1, 3, true);
		Pawn capturer = new Pawn(Piece.TAN, 3, 4, false);
		sut.setPiece(capturer);
		sut.setPiece(captured);	
		
		// Act - Move the captured Pawn double forward generate the capturer's moves
		Move doublePawnForward = new Move(captured, 3, 3);
		sut.applyMove(doublePawnForward);
		List<Move> actualValidMoves = sut.generateValidMoves(board[3][4]);
		
		// Test
		// EnPassant and single forward move should be valid
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new EnPassant(board[3][4].clone(), 2, 3, board[3][3].clone()));
		expectedValidMoves.add(new Move(board[3][4].clone(), 2, 4));
		
		assertTrue(ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves));
	}
	
	@Test
	public void whiteEnPassantTest() {
		System.out.println("\nWhite EnPassant Test:");
		//Set up
		Pawn captured = new Pawn(Piece.TAN, 2, 3, true);
		Pawn capturer = new Pawn(Piece.WHITE, 4, 4, false);
		sut.setPiece(capturer);
		sut.setPiece(captured);	
		
		// Act - Move the captured Pawn double forward generate the capturer's moves
		Move doublePawnForward = new Move(captured, 4, 3);
		sut.applyMove(doublePawnForward);
		List<Move> actualValidMoves = sut.generateValidMoves(board[4][4]);
		
		// Test
		// EnPassant and single forward move should be valid
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new EnPassant(board[4][4].clone(), 5, 3, board[4][3].clone()));
		expectedValidMoves.add(new Move(board[4][4].clone(), 5, 4));
		
		assertTrue(ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves));
	}

	@Test
	public void castleTest() {
		
	}
	
	@Test
	public void tanUpgradeTest() {
		System.out.println("\nTan Upgrade Test:");
		//Set up
		Pawn up = new Pawn(Piece.TAN, 1, 3, false);
		sut.setPiece(up);
		Queen q1 = new Queen(Piece.WHITE, 0, 2);
		sut.setPiece(q1);	
		
		// Act
		List<Move> actualValidMoves = sut.generateValidMoves(board[1][3]);
		
		// Test
		// Should be able to move forward by 1 and upgrade or take queen and upgrade
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 3, new Queen(up.color, 0, 3)));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 3, new Bishop(up.color, 0, 3)));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 3, new Rook(up.color, 0, 3, false)));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 3, new Knight(up.color, 0, 3)));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 2, new Queen(up.color, 0, 2), q1.clone()));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 2, new Bishop(up.color, 0, 2), q1.clone()));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 2, new Rook(up.color, 0, 2, false), q1.clone()));
		expectedValidMoves.add(new Upgrade(up.clone(), 0, 2, new Knight(up.color, 0, 2), q1.clone()));
		
		assertTrue(ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves));
	}
	
	@Test
	public void checkTest() {
		
	}
	
	@Test
	public void checkmateTest() {
		
	}
	
	@Test
	public void pinnedToKingTest() {
		
	}
}
