package jacob.siebert.chessai.player;

import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Move;

/**
 * Guarantees that implementers can determine a move to make given a Board instance.
 * This AI uses a min max search over the game tree to a given depth
 */
public interface MinMax_AI {

    Move nextMove(Board board, int maxDepth);

}
