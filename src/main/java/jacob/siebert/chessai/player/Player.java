package jacob.siebert.chessai.player;

import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Move;

public interface Player {

	public boolean isHuman();
	public void setName(String name);
	public String getName();
	public void setColor(char color);
	public char getColor();
	public Move nextMove(Board board, int maxDepth);
}
