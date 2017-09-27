package jacob.siebert.chessai.util;

import jacob.siebert.chessai.app.ChessBoard;
import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.piece.King;
import jacob.siebert.chessai.piece.Piece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

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
        URL url = ChessBoard.class.getClassLoader().getResource("/resource/test.txt");
        // TODO: clean up
        try {
            file = new File(url.toURI());
        } catch(NullPointerException e) {
            LOG.debug(e.toString());
            return null;
        } catch(URISyntaxException e) {
            LOG.debug(e.toString());
            return null;
        }
        return file;
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
        sut.placePiece(new King(Piece.TAN, 7, 7));
    }

    /**
     * Adds a generic white king at (0,0) - top left corner
     */
    public static void addWhiteKing(Board sut) {
        sut.placePiece(new King(Piece.WHITE, 0, 0));
    }
}
