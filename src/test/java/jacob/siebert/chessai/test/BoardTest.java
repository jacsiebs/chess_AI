package jacob.siebert.chessai.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jacob.siebert.chessai.assertion.ChessAssertions;
import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Castle;
import jacob.siebert.chessai.move.EnPassant;
import jacob.siebert.chessai.move.Move;
import jacob.siebert.chessai.move.Promotion;
import jacob.siebert.chessai.piece.*;

import jacob.siebert.chessai.util.ChessTestingUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger LOG = LoggerFactory.getLogger(BoardTest.class);

	private Board sut;// system under test
	private Piece[][] board;// points to the Piece[][] in the sut Board
	
	@Before
	public void setUp() {

	}
	
	// remove references
	@After
	public void tearDown() {
		sut = null;
		board = null;
		System.out.println("\n*******************************************************"
				+ "*******************************\n");
	}

	@Test
	public void tanEnPassantTest() {
		//Set up
		File input = ChessTestingUtil.loadGameFile("test_boards/tanEnPassantTest");
		sut = new Board(input);

		Pawn defense = (Pawn) sut.getPiece(1, 3);
		Pawn attack = (Pawn) sut.getPiece(3, 4);

		// Act - Move the captured Pawn double forward generate the attacker's moves
		Move doublePawnForward = new Move(defense, 3, 3);
		sut.applyMove(doublePawnForward);
		List<Move> actualValidMoves = sut.generateValidMoves(attack);

		// Test
		// EnPassant and single forward move should be valid
		List<Move> expectedValidMoves = new ArrayList<Move>();
		expectedValidMoves.add(new EnPassant(attack, 2, 3, defense));
		expectedValidMoves.add(new Move(attack, 2, 4));

		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
	}

//	@Test
//	public void whiteEnPassantTest() {
//		System.out.println("White EnPassant Test:");
//		//Set up
//		sut.placePiece(new King(Piece.TAN, 7, 7, false));
//		sut.placePiece(new King(Piece.WHITE, 0, 0, false));
//		Pawn captured = new Pawn(Piece.TAN, 2, 3, true);
//		Pawn capturer = new Pawn(Piece.WHITE, 4, 4, false);
//		sut.placePiece(capturer);
//		sut.placePiece(captured);
//		sut.displayBoard();
//
//		// Act - Move the captured Pawn double forward generate the capturer's moves
//		Move doublePawnForward = new Move(captured, 4, 3);
//		sut.applyMove(doublePawnForward);
//		List<Move> actualValidMoves = sut.generateValidMoves(board[4][4]);
//
//		// Test
//		// EnPassant and single forward move should be valid
//		List<Move> expectedValidMoves = new ArrayList<Move>();
//		expectedValidMoves.add(new EnPassant(capturer, 5, 3, captured));
//		expectedValidMoves.add(new Move(board[4][4], 5, 4));
//
//		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
//	}
//
	@Test
	public void castleTest() {
		// Set up
		// Test the king's side castle for white and Queen's side for tan
		// begin with a new board and remove the Pieces in the way of each castle
		File input = ChessTestingUtil.loadGameFile("test_boards/castleTestsBasic");
		sut = new Board(input);
		board = sut.getBoard();

		King tanK = (King) board[7][4];
		King whiteK = (King) board[0][4];
		Rook tanR = (Rook) board[7][0];
		Rook whiteR = (Rook) board[0][7];

		// act
		List<Move> whiteKingActual = sut.generateValidMoves(whiteK);
		List<Move> tanKingActual = sut.generateValidMoves(tanK);

		// expected
		List<Move> whiteKingExpected = new ArrayList<Move>();
		whiteKingExpected.add(new Move(whiteK, 0, 5));// Move 1 square right
		whiteKingExpected.add(new Move(whiteK, 1, 5));// Move 1 square down-right
		whiteKingExpected.add(new Move(whiteK, 1, 3));// Move 1 square down-left
		whiteKingExpected.add(new Move(whiteK, 0, 3));// Move 1 square left
		whiteKingExpected.add(new Move(whiteK, 1, 4));// Move 1 square down
		whiteKingExpected.add(new Castle(whiteK, 0, 6, whiteR, 0, 5));// castle

		List<Move> tanKingExpected = new ArrayList<Move>();
		tanKingExpected.add(new Move(tanK, 7, 3));// Move 1 square left
		tanKingExpected.add(new Move(tanK, 7, 5));// Move 1 square right
		tanKingExpected.add(new Move(tanK, 6, 3));// Move 1 square up-left
		tanKingExpected.add(new Move(tanK, 6, 5));// Move 1 square up-right
		tanKingExpected.add(new Move(tanK, 6, 4));// Move 1 square up
		tanKingExpected.add(new Castle(tanK, 7, 2, tanR, 7, 3));// castle

		// test
		ChessAssertions.assertEqualsValidMoves(whiteKingExpected, whiteKingActual);
		ChessAssertions.assertEqualsValidMoves(tanKingExpected, tanKingActual);
	}

	@Test
	public void PromotionTestsBasic() {
		// Set up
		File input = ChessTestingUtil.loadGameFile("test_boards/castleTestsBasic");
		sut = new Board(input);
		board = sut.getBoard();

		Pawn white_p1 = (Pawn) sut.getPiece(7, 0);
		Pawn white_p2 = (Pawn) sut.getPiece(7, 1);
		Pawn white_p3 = (Pawn) sut.getPiece(7, 2);
		Pawn white_p4 = (Pawn) sut.getPiece(7, 7);
		King white_k = (King) sut.getPiece(0, 0);
		Pawn tan_p1 = (Pawn) sut.getPiece(1, 0);
		Pawn tan_p2 = (Pawn) sut.getPiece(1, 3);
		Pawn tan_p3 = (Pawn) sut.getPiece(1, 5);
		King tan_k = (King) sut.getPiece(7, 7);

		// Act
		List<Move> white_p1_actualValidMoves = sut.generateValidMoves(white_p1);
		List<Move> white_p2_actualValidMoves = sut.generateValidMoves(white_p2);
		List<Move> white_p3_actualValidMoves = sut.generateValidMoves(white_p3);
		List<Move> white_p4_actualValidMoves = sut.generateValidMoves(white_p4);
		List<Move> white_k_actualValidMoves = sut.generateValidMoves(white_k);
		List<Move> tan_p1_actualValidMoves = sut.generateValidMoves(tan_p1);
		List<Move> tan_p2_actualValidMoves = sut.generateValidMoves(tan_p2);
		List<Move> tan_p3_actualValidMoves = sut.generateValidMoves(tan_p3);
		List<Move> tan_k_actualValidMoves = sut.generateValidMoves(tan_k);

		// Expected TODO
		List<Move> white_p1_expectedValidMoves = new ArrayList<>();
		List<Move> white_p2_expectedValidMoves = new ArrayList<>();
		List<Move> white_p3_expectedValidMoves = new ArrayList<>();
		List<Move> white_p4_expectedValidMoves = new ArrayList<>();
		List<Move> white_k_expectedValidMoves = new ArrayList<>();
		List<Move> tan_p1_expectedValidMoves = new ArrayList<>();
		List<Move> tan_p2_expectedValidMoves = new ArrayList<>();
		List<Move> tan_p3_expectedValidMoves = new ArrayList<>();
		List<Move> tan_k_expectedValidMoves = new ArrayList<>();

		// Test
		// Should be able to move forward by 1 and promote or take queen and promote
		List<Move> white_expectedValidMoves = new ArrayList<Move>();
		white_expectedValidMoves.add(new Promotion(up, 0, 3, new Queen(up.color, 0, 3)));
		white_expectedValidMoves.add(new Promotion(up, 0, 3, new Bishop(up.color, 0, 3)));
		white_expectedValidMoves.add(new Promotion(up, 0, 3, new Rook(up.color, 0, 3, false)));
		white_expectedValidMoves.add(new Promotion(up, 0, 3, new Knight(up.color, 0, 3)));
		white_expectedValidMoves.add(new Promotion(up, 0, 2, new Queen(up.color, 0, 2), q1));
		white_expectedValidMoves.add(new Promotion(up, 0, 2, new Bishop(up.color, 0, 2), q1));
		white_expectedValidMoves.add(new Promotion(up, 0, 2, new Rook(up.color, 0, 2, false), q1));
		white_expectedValidMoves.add(new Promotion(up, 0, 2, new Knight(up.color, 0, 2), q1));

		ChessAssertions.assertEqualsValidMoves(white_expectedValidMoves, actualValidMoves);
	}
//
//	@Test
//	public void whiteUpgradeTest() {
//		System.out.println("White Promotion Test:");
//		//Set up
//		sut.placePiece(new King(Piece.WHITE, y(8), x('h')));
//		sut.placePiece(new King(Piece.TAN, y(1), x('a')));
//		Pawn up = new Pawn(Piece.WHITE, 6, 3, false);
//		sut.placePiece(up);
//		Queen q1 = new Queen(Piece.TAN, 7, 2);
//		sut.placePiece(q1);
//		Rook r1 = new Rook(Piece.TAN, 7, 3, false);
//		sut.placePiece(r1);
//		sut.updateChecks();
//		sut.displayBoard();
//
//		// Act
//		List<Move> actualValidMoves = sut.generateValidMoves(board[6][3]);
//
//		// Test
//		// Should be able to upgrade only by taking queen, rook blocks forward move
//		List<Move> expectedValidMoves = new ArrayList<Move>();
//		expectedValidMoves.add(new Promotion(up, 7, 2, new Queen(up.color, 7, 2), q1));
//		expectedValidMoves.add(new Promotion(up, 7, 2, new Bishop(up.color, 7, 2), q1));
//		expectedValidMoves.add(new Promotion(up, 7, 2, new Rook(up.color, 7, 2, false), q1));
//		expectedValidMoves.add(new Promotion(up, 7, 2, new Knight(up.color, 7, 2), q1));
//
//		ChessAssertions.assertEqualsValidMoves(expectedValidMoves, actualValidMoves);
//	}
//
//	@Test
//	public void SimpleCheckTest() {
//		System.out.println("Simple Check Test:");
//		// set up
//		addWhiteKing();
//		King tK = new King(Piece.TAN, y(1), x('e'));
//		sut.placePiece(tK);
//		Rook tR = new Rook(Piece.TAN, y(1), x('d'));
//		sut.placePiece(tR);
//		Queen wQ = new Queen(Piece.WHITE, y(5), x('a'));
//		sut.placePiece(wQ);
//		Knight tN = new Knight(Piece.TAN, y(5), x('d'));
//		sut.placePiece(tN);
//		Queen tQ = new Queen(Piece.TAN, y(8), x('a'));
//		sut.placePiece(tQ);
//		sut.updateChecks();
//		sut.displayBoard();
//
//		// act
//		ArrayList<Move> actual = sut.generateValidMoves(tK);
//		actual.addAll(sut.generateValidMoves(tR));
//		actual.addAll(sut.generateValidMoves(tN));
//		actual.addAll(sut.generateValidMoves(tQ));
//
//		// expected
//		// Should be able to block the threat with rook or move out of the way
//		ArrayList<Move> expected = new ArrayList<Move>();
//		expected.add(new Move(tK, y(2), x('e')));
//		expected.add(new Move(tK, y(1), x('f')));
//		expected.add(new Move(tK, y(2), x('f')));
//		expected.add(new Move(tR, y(2), x('d')));
//		expected.add(new Move(tN, y(4), x('b')));
//		expected.add(new Move(tN, y(3), x('c')));
//		expected.add(new Move(tQ, y(5), x('a')));
//
//		// test
//		ChessAssertions.assertEqualsValidMoves(expected, actual);
//	}
//
//	@Test
//	public void threatPathsTest_1() {
//		System.out.println("Threat Paths Test 1:");
//		// set up
//		addWhiteKing();
//		King tK = new King(Piece.TAN, y(1), x('e'));
//		sut.placePiece(tK);
//		Rook wR = new Rook(Piece.WHITE, y(8), x('e'));
//		sut.placePiece(wR);
//		Queen wQ = new Queen(Piece.WHITE, y(5), x('a'));
//		sut.placePiece(wQ);
//		Bishop wB = new Bishop(Piece.WHITE, y(4), x('h'));
//		sut.placePiece(wB);
//		Knight wN = new Knight(Piece.WHITE, y(3), x('d'));
//		sut.placePiece(wN);
//		sut.updateChecks();
//		sut.displayBoard();
//
//		// act
//		ArrayList<ArrayList<Square>> tPathsActual = sut.generateThreatPaths(tK);
//
//		// expected
//		ArrayList<ArrayList<Square>> tPathsExpected = new ArrayList<ArrayList<Square>>();
//		ArrayList<Square> rook = new ArrayList<Square>();
//		rook.add(new Square(y(8), x('e')));
//		rook.add(new Square(y(7), x('e')));
//		rook.add(new Square(y(6), x('e')));
//		rook.add(new Square(y(5), x('e')));
//		rook.add(new Square(y(4), x('e')));
//		rook.add(new Square(y(3), x('e')));
//		rook.add(new Square(y(2), x('e')));
//		tPathsExpected.add(rook);
//		ArrayList<Square> queen = new ArrayList<Square>();
//		queen.add(new Square(y(5), x('a')));
//		queen.add(new Square(y(4), x('b')));
//		queen.add(new Square(y(3), x('c')));
//		queen.add(new Square(y(2), x('d')));
//		tPathsExpected.add(queen);
//		ArrayList<Square> bishop = new ArrayList<Square>();
//		bishop.add(new Square(y(4), x('h')));
//		bishop.add(new Square(y(3), x('g')));
//		bishop.add(new Square(y(2), x('f')));
//		tPathsExpected.add(bishop);
//		ArrayList<Square> knight = new ArrayList<Square>();
//		knight.add(new Square(y(3), x('d')));
//		tPathsExpected.add(knight);
//
//		// test
//		ChessAssertions.assertEqualsThreatPaths(tPathsExpected, tPathsActual);
//	}
//
//	@Test
//	public void checkmateTest() {
//
//	}
//
//	@Test
//	public void canCaptureTest() {
//
//	}
//
//	@Test
//	public void pinnedToKingTest() {
//		System.out.println("Pinned Test:");
//		// set up
//		addWhiteKing();
//		King tK = new King(Piece.TAN, y(1), x('e'));
//		sut.placePiece(tK);
//		Rook tR = new Rook(Piece.TAN, y(2), x('d'));
//		sut.placePiece(tR);
//		Queen wQ = new Queen(Piece.WHITE, y(5), x('a'));
//		sut.placePiece(wQ);
//		sut.updateChecks();
//		sut.displayBoard();
//
//		// act
//		ArrayList<Move> actual = sut.generateValidMoves(tR);
//
//		// expected
//		ArrayList<Move> expected = null;
//
//		// test
//		ChessAssertions.assertEqualsValidMoves(expected, actual);
//	}

	/**
	 *
	 * @param validMoves
	 * @param p
	 * @param yto
	 * @param xto
	 */
	private void addAllPromotions(List<Move> validMoves, Pawn p, int yto, int xto) {
		validMoves.add(new Promotion(p, yto, xto, new Knight(p.color, yto, xto)));
		validMoves.add(new Promotion(p, yto, xto, new Queen(p.color, yto, xto)));
		validMoves.add(new Promotion(p, yto, xto, new Rook(p.color, yto, xto)));
		validMoves.add(new Promotion(p, yto, xto, new Bishop(p.color, yto, xto)));
	}
	// with a removed piece
	private void addAllPromotions(List<Move> validMoves, Pawn p, int yto, int xto, Piece removed) {
		validMoves.add(new Promotion(p, yto, xto, new Knight(p.color, yto, xto), removed));
		validMoves.add(new Promotion(p, yto, xto, new Queen(p.color, yto, xto), removed));
		validMoves.add(new Promotion(p, yto, xto, new Rook(p.color, yto, xto), removed));
		validMoves.add(new Promotion(p, yto, xto, new Bishop(p.color, yto, xto), removed));
	}
}
