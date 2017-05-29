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
 * For the Board algorithms to function - there must always be a single white and a 
 * single tan king on the board. Therefore each test must add each king even if they are 
 * not used. 
 * 
 * @author Jacob Siebert
 */
public class BoardTest {

	Board sut;// system under test
	Piece[][] board;// points to the Piece[][] in the sut Board
	
	// adds a generic tan king at (7,7) - bottom right corner
	private void addTanKing() {
		sut.placePiece(new King(Piece.TAN, 7, 7));
	}
	
	// adds a generic white king at (0,0) - top left corner
	private void addWhiteKing() {
		sut.placePiece(new King(Piece.WHITE, 0, 0));
	}
	
	/**
	 * @param chessBoardY - The Y coordinate on a standard chess board
	 * @return The y index of the board array
	 */
	private int y(int chessBoardY) {
		return 8 - chessBoardY;
	}
	
	/**
	 * @param chessBoardX - The character representing x coordinate on a standard chess board
	 * @return The x coordinate of the board array
	 */
	private int x(char chessBoardX) {
		return ((int) chessBoardX) - 97;// 97 is ascii for 'a'
	}
	
	@Before
	public void setUp() {
		sut = new Board(Board.EMPTY);
		board = sut.getBoard();
	}
	
	// remove references
	@After
	public void tearDown() {
//		sut.displayBoard();
		sut = null;
		board = null;
		System.out.println("\n*******************************************************"
				+ "*******************************\n");
	}

	@Test
	public void tanEnPassantTest() {
		System.out.println("Tan EnPassant Test:");
		//Set up
		sut.placePiece(new King(Piece.TAN, 7, 7, false));
		sut.placePiece(new King(Piece.WHITE, 0, 0, false));
		Pawn captured = new Pawn(Piece.WHITE, 1, 3, true);
		Pawn capturer = new Pawn(Piece.TAN, 3, 4, false);
		sut.placePiece(capturer);
		sut.placePiece(captured);	
		sut.updateChecks();
		sut.displayBoard();
		
		// Act - Move the captured Pawn double forward generate the capturer's moves
		Move doublePawnForward = new Move(captured, 3, 3);
		sut.applyMove(doublePawnForward);
		List<Move> actualValidMoves = sut.generateValidMoves(board[3][4]);
		
		// Test
		// EnPassant and single forward move should be valid
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new EnPassant(capturer, 2, 3, captured));
		expectedValidMoves.add(new Move(board[3][4], 2, 4));
		
		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
	}
	
	@Test
	public void whiteEnPassantTest() {
		System.out.println("White EnPassant Test:");
		//Set up
		sut.placePiece(new King(Piece.TAN, 7, 7, false));
		sut.placePiece(new King(Piece.WHITE, 0, 0, false));
		Pawn captured = new Pawn(Piece.TAN, 2, 3, true);
		Pawn capturer = new Pawn(Piece.WHITE, 4, 4, false);
		sut.placePiece(capturer);
		sut.placePiece(captured);	
		sut.displayBoard();
		
		// Act - Move the captured Pawn double forward generate the capturer's moves
		Move doublePawnForward = new Move(captured, 4, 3);
		sut.applyMove(doublePawnForward);
		List<Move> actualValidMoves = sut.generateValidMoves(board[4][4]);
		
		// Test
		// EnPassant and single forward move should be valid
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new EnPassant(capturer, 5, 3, captured));
		expectedValidMoves.add(new Move(board[4][4], 5, 4));
		
		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
	}

	@Test
	public void castleTest() {
		System.out.println("Castle Tests:");
		// Set up
		// Test the king's side castle for white and Queen's side for tan
		// begin with a new board and remove the Pieces in the way of each castle
		sut = new Board(Board.NEW_GAME);
		board = sut.getBoard();
		
		if(!sut.removePiece(0, 6)) throw new NoSuchPieceException(0, 6);// white knight
		if(!sut.removePiece(0, 5)) throw new NoSuchPieceException(0, 5);// white bishop
		if(!sut.removePiece(7, 1)) throw new NoSuchPieceException(7, 1);// tan knight
		if(!sut.removePiece(7, 2)) throw new NoSuchPieceException(7, 2);// tan bishop
		if(!sut.removePiece(7, 3)) throw new NoSuchPieceException(7, 3);// tan queen
		King tanK = (King) board[7][4];
		King whiteK = (King) board[0][4];
		Rook tanR = (Rook) board[7][0];
		Rook whiteR = (Rook) board[0][7];
		sut.updateChecks();
		sut.displayBoard();
		
		// act
		List<Move> whiteKingActual = sut.generateValidMoves(whiteK);
		List<Move> tanKingActual = sut.generateValidMoves(tanK);
		
		// expected
		List<Move> whiteKingExpected = new ArrayList<Move>();
		whiteKingExpected.add(new Move(whiteK, 0, 5));// Move 1 square right
		whiteKingExpected.add(new Castle(whiteK, 0, 6, whiteR, 0, 5));// castle
		List<Move> tanKingExpected = new ArrayList<Move>();
		tanKingExpected.add(new Move(tanK, 7, 3));// Move 1 square left
		tanKingExpected.add(new Castle(tanK, 7, 2, tanR, 7, 3));// castle
		
		// test
		System.out.println("King's Side Castle:");
		ChessAssertions.assertEqualsValidMoves(whiteKingExpected, whiteKingActual);
		System.out.println("Queen's Side Castle:");
		ChessAssertions.assertEqualsValidMoves(tanKingExpected, tanKingActual);
	}
	
	@Test
	public void tanUpgradeTest() {
		System.out.println("Tan Upgrade Test:");
		//Set up
		sut.placePiece(new King(Piece.WHITE, y(8), x('h')));
		sut.placePiece(new King(Piece.TAN, y(1), x('h')));
		Pawn up = new Pawn(Piece.TAN, 1, 3, false);
		sut.placePiece(up);
		Queen q1 = new Queen(Piece.WHITE, 0, 2);
		sut.placePiece(q1);	
		sut.updateChecks();
		sut.displayBoard();
		
		// Act
		List<Move> actualValidMoves = sut.generateValidMoves(board[1][3]);
		
		// Test
		// Should be able to move forward by 1 and upgrade or take queen and upgrade
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new Upgrade(up, 0, 3, new Queen(up.color, 0, 3)));
		expectedValidMoves.add(new Upgrade(up, 0, 3, new Bishop(up.color, 0, 3)));
		expectedValidMoves.add(new Upgrade(up, 0, 3, new Rook(up.color, 0, 3, false)));
		expectedValidMoves.add(new Upgrade(up, 0, 3, new Knight(up.color, 0, 3)));
		expectedValidMoves.add(new Upgrade(up, 0, 2, new Queen(up.color, 0, 2), q1));
		expectedValidMoves.add(new Upgrade(up, 0, 2, new Bishop(up.color, 0, 2), q1));
		expectedValidMoves.add(new Upgrade(up, 0, 2, new Rook(up.color, 0, 2, false), q1));
		expectedValidMoves.add(new Upgrade(up, 0, 2, new Knight(up.color, 0, 2), q1));
		
		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
	}
	
	@Test
	public void whiteUpgradeTest() {
		System.out.println("White Upgrade Test:");
		//Set up
		sut.placePiece(new King(Piece.WHITE, y(8), x('h')));
		sut.placePiece(new King(Piece.TAN, y(1), x('a')));
		Pawn up = new Pawn(Piece.WHITE, 6, 3, false);
		sut.placePiece(up);
		Queen q1 = new Queen(Piece.TAN, 7, 2);
		sut.placePiece(q1);	
		Rook r1 = new Rook(Piece.TAN, 7, 3, false);
		sut.placePiece(r1);
		sut.updateChecks();
		sut.displayBoard();
		
		// Act
		List<Move> actualValidMoves = sut.generateValidMoves(board[6][3]);
		
		// Test
		// Should be able to upgrade only by taking queen, rook blocks forward move
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new Upgrade(up, 7, 2, new Queen(up.color, 7, 2), q1));
		expectedValidMoves.add(new Upgrade(up, 7, 2, new Bishop(up.color, 7, 2), q1));
		expectedValidMoves.add(new Upgrade(up, 7, 2, new Rook(up.color, 7, 2, false), q1));
		expectedValidMoves.add(new Upgrade(up, 7, 2, new Knight(up.color, 7, 2), q1));
		
		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
	}
	
	@Test
	public void SimpleCheckTest() {
		System.out.println("Simple Check Test:");
		// set up
		addWhiteKing();
		King tK = new King(Piece.TAN, y(1), x('e'));
		sut.placePiece(tK);
		Rook tR = new Rook(Piece.TAN, y(1), x('d'));
		sut.placePiece(tR);
		Queen wQ = new Queen(Piece.WHITE, y(5), x('a'));
		sut.placePiece(wQ);
		Knight tN = new Knight(Piece.TAN, y(5), x('d'));
		sut.placePiece(tN);
		Queen tQ = new Queen(Piece.TAN, y(8), x('a'));
		sut.placePiece(tQ);
		sut.updateChecks();
		sut.displayBoard(); 
		
		// act
		ArrayList<Move> actual = sut.generateValidMoves(tK);
		actual.addAll(sut.generateValidMoves(tR));
		actual.addAll(sut.generateValidMoves(tN));
		actual.addAll(sut.generateValidMoves(tQ));
		
		// expected
		// Should be able to block the threat with rook or move out of the way
		ArrayList<Move> expected = new ArrayList<Move>();
		expected.add(new Move(tK, y(2), x('e')));
		expected.add(new Move(tK, y(1), x('f')));
		expected.add(new Move(tK, y(2), x('f')));
		expected.add(new Move(tR, y(2), x('d')));
		expected.add(new Move(tN, y(4), x('b')));
		expected.add(new Move(tN, y(3), x('c')));
		expected.add(new Move(tQ, y(5), x('a')));
		
		// test
		ChessAssertions.assertEqualsValidMoves(expected, actual);
	}
	
	@Test
	public void threatPathsTest_1() {
		System.out.println("Threat Paths Test 1:");
		// set up
		addWhiteKing();
		King tK = new King(Piece.TAN, y(1), x('e'));
		sut.placePiece(tK);
		Rook wR = new Rook(Piece.WHITE, y(8), x('e'));
		sut.placePiece(wR);
		Queen wQ = new Queen(Piece.WHITE, y(5), x('a'));
		sut.placePiece(wQ);
		Bishop wB = new Bishop(Piece.WHITE, y(4), x('h'));
		sut.placePiece(wB);
		Knight wN = new Knight(Piece.WHITE, y(3), x('d'));
		sut.placePiece(wN);
		sut.updateChecks();
		sut.displayBoard();
		
		// act
		ArrayList<ArrayList<Square>> tPathsActual = sut.generateThreatPaths(tK);
		
		// expected
		ArrayList<ArrayList<Square>> tPathsExpected = new ArrayList<ArrayList<Square>>();
		ArrayList<Square> rook = new ArrayList<Square>();
		rook.add(new Square(y(8), x('e')));
		rook.add(new Square(y(7), x('e')));
		rook.add(new Square(y(6), x('e')));
		rook.add(new Square(y(5), x('e')));
		rook.add(new Square(y(4), x('e')));
		rook.add(new Square(y(3), x('e')));
		rook.add(new Square(y(2), x('e')));
		tPathsExpected.add(rook);
		ArrayList<Square> queen = new ArrayList<Square>();
		queen.add(new Square(y(5), x('a')));
		queen.add(new Square(y(4), x('b')));
		queen.add(new Square(y(3), x('c')));
		queen.add(new Square(y(2), x('d')));
		tPathsExpected.add(queen);
		ArrayList<Square> bishop = new ArrayList<Square>();
		bishop.add(new Square(y(4), x('h')));
		bishop.add(new Square(y(3), x('g')));
		bishop.add(new Square(y(2), x('f')));
		tPathsExpected.add(bishop);
		ArrayList<Square> knight = new ArrayList<Square>();
		knight.add(new Square(y(3), x('d')));
		tPathsExpected.add(knight);
		
		// test
		ChessAssertions.assertEqualsThreatPaths(tPathsExpected, tPathsActual);
	}
	
	@Test
	public void checkmateTest() {
		
	}
	
	@Test
	public void canCaptureTest() {
		
	}
	
	@Test
	public void pinnedToKingTest() {
		System.out.println("Pinned Test:");
		// set up
		addWhiteKing();
		King tK = new King(Piece.TAN, y(1), x('e'));
		sut.placePiece(tK);
		Rook tR = new Rook(Piece.TAN, y(2), x('d'));
		sut.placePiece(tR);
		Queen wQ = new Queen(Piece.WHITE, y(5), x('a'));
		sut.placePiece(wQ);
		sut.updateChecks();
		sut.displayBoard();
		
		// act
		ArrayList<Move> actual = sut.generateValidMoves(tR);
		
		// expected
		ArrayList<Move> expected = null;
		
		// test
		ChessAssertions.assertEqualsValidMoves(expected, actual);	
	}
}
