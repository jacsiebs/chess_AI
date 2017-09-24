package jacob.siebert.chessai.util;

import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.piece.King;
import jacob.siebert.chessai.piece.Piece;

/**
 * Provides tests with a suite of function to make writing tests simpler
 */
public class ChessTestingUtil {

    /**
     * @param chessBoardY - The Y coordinate on a standard chess board
     * @return The y index of the board array
     */
    private static int y(int chessBoardY) {
        return 8 - chessBoardY;
    }

    /**
     * @param chessBoardX - The character representing x coordinate on a standard chess board
     * @return The x coordinate of the board array
     */
    private static int x(char chessBoardX) {
        return ((int) chessBoardX) - 97;// 97 is ascii for 'a'
    }

    /**
     * Adds a generic tan king at (7,7) - bottom right corner
     */
    private static void addTanKing(Board sut) {
        sut.placePiece(new King(Piece.TAN, 7, 7));
    }

    /**
     * Adds a generic white king at (0,0) - top left corner
     */
    private static void addWhiteKing(Board sut) {
        sut.placePiece(new King(Piece.WHITE, 0, 0));
    }
}
