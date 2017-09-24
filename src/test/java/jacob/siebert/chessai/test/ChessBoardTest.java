package jacob.siebert.chessai.test;

import jacob.siebert.chessai.app.ChessBoard;
import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Promotion;
import jacob.siebert.chessai.piece.King;
import jacob.siebert.chessai.piece.Pawn;
import jacob.siebert.chessai.piece.Piece;
import jacob.siebert.chessai.piece.Queen;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChessBoardTest {

	ChessBoard sut;
	Board sut_board;
	
	/**
	 * @param chessBoardY - The Y coordinate on a standard chess jacob.siebert.chessai.board
	 * @return The y index of the jacob.siebert.chessai.board array
	 */
	private int y(int chessBoardY) {
		return 8 - chessBoardY;
	}
	
	/**
	 * @param chessBoardX - The character representing x coordinate on a standard chess jacob.siebert.chessai.board
	 * @return The x coordinate of the jacob.siebert.chessai.board array
	 */
	private int x(char chessBoardX) {
		return ((int) chessBoardX) - 97;// 97 is ascii for 'a'
	}
	
	// adds a generic tan king at (7,7) - bottom right corner
	private void addTanKing() {
		sut_board.placePiece(new King(Piece.TAN, 7, 7, false));
	}
	
	// adds a generic white king at (0,0) - top left corner
	private void addWhiteKing() {
		sut_board.placePiece(new King(Piece.WHITE, 0, 0, false));
	}
	
	@Before
	public void setUp() throws Exception {
		sut = new ChessBoard();
		sut.initUI(false);
		// empty jacob.siebert.chessai.board, no AI
		sut.initGame(Board.EMPTY, ChessBoard.H_v_H);
		sut_board = sut.getBoard();
	}

	@After
	public void tearDown() throws Exception {
		sut_board = null;
		sut = null;
	}

	@Test
	public void upgradeTest() {
		// set-up
		addTanKing();
		addWhiteKing();
		Pawn toBeUpgraded = new Pawn(Piece.TAN, y(7), x('d'), false);
		sut_board.placePiece(toBeUpgraded);
		Queen canBeCaptured = new Queen(Piece.WHITE, y(8), x('c'));
		sut_board.placePiece(canBeCaptured);
		sut_board.displayBoard();
		
		// act
	    Promotion up = new Promotion(toBeUpgraded, 0, 2, new Queen(toBeUpgraded.color, 0, 2), canBeCaptured);
		sut.move(up);
		
		// test
		
	}

}
