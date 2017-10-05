package jacob.siebert.chessai.util;

import jacob.siebert.chessai.app.ChessBoard;
import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Move;
import jacob.siebert.chessai.move.Promotion;
import jacob.siebert.chessai.piece.*;
import jacob.siebert.chessai.type.PieceColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides tests with a suite of function to make writing tests simpler
 */
public class ChessTestingUtil {

    private static Logger LOG = LoggerFactory.getLogger(ChessTestingUtil.class);

    /**
     * Opens the filename specifying the game state file to open
     * @return the opened file
     */
    public static File loadGameFile(String filename) {
        File file;
        // Get file from resources folder
        URL url = ChessBoard.class.getClassLoader().getResource(filename);
        if(url == null) {
            LOG.debug("Game File not found for file: " + filename + "\n");
            return null;
        }
        // TODO: clean up
        try {
            file = new File(url.toURI());
        } catch(Exception e) {
            LOG.debug("Game File Load FAILED for file: " + filename + "\n" + e.getMessage());
            return null;
        }
        return file;
    }

    public static List<Move> generateActualMoves(List<Piece> pieces, Board board) {
        List<Move> actual_moves = new LinkedList<>();
        for(Piece p : pieces) {
            actual_moves.addAll(board.generateValidMoves(p));
        }
        return actual_moves;
    }

    /**
     *
     * @param validMoves
     * @param p
     * @param yto
     * @param xto
     */
    public static void addAllPromotions(List<Move> validMoves, Pawn p, int yto, int xto) {
        validMoves.add(new Promotion(p, yto, xto, new Knight(p.getColor(), yto, xto)));
        validMoves.add(new Promotion(p, yto, xto, new Queen(p.getColor(), yto, xto)));
        validMoves.add(new Promotion(p, yto, xto, new Rook(p.getColor(), yto, xto)));
        validMoves.add(new Promotion(p, yto, xto, new Bishop(p.getColor(), yto, xto)));
    }
    // with a removed piece
    public static void addAllPromotions(List<Move> validMoves, Pawn p, int yto, int xto, Piece removed) {
        validMoves.add(new Promotion(p, yto, xto, new Knight(p.getColor(), yto, xto), removed));
        validMoves.add(new Promotion(p, yto, xto, new Queen(p.getColor(), yto, xto), removed));
        validMoves.add(new Promotion(p, yto, xto, new Rook(p.getColor(), yto, xto), removed));
        validMoves.add(new Promotion(p, yto, xto, new Bishop(p.getColor(), yto, xto), removed));
    }

    /**
     * @param chessBoardY - The Y coordinate on a standard chess board
     * @return The y index of the board array
     */
    public static int y(int chessBoardY) {
        return 8 - chessBoardY;
    }

    /**
     * @param chessBoardX - The character representing x coordinate on a standard chess board
     * @return The x coordinate of the board array
     */
    public static int x(char chessBoardX) {
        return ((int) chessBoardX) - 97;// 97 is ascii for 'a'
    }

    /**
     * Adds a generic tan king at (7,7) - bottom right corner
     */
    public static void addTanKing(Board sut) {
        sut.placePiece(new King(PieceColor.TAN, 7, 7));
    }

    /**
     * Adds a generic white king at (0,0) - top left corner
     */
    public static void addWhiteKing(Board sut) {
        sut.placePiece(new King(PieceColor.WHITE, 0, 0));
    }
}
