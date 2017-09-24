package jacob.siebert.chessai.player;

import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.move.Move;

public class HumanPlayer implements Player {

	private final static boolean HUMAN = true;
	private String name;
	private char color;
	
	public HumanPlayer(char color) {
		this.color = color;
	}

	public boolean isHuman() {
		// TODO Auto-generated method stub
		return HUMAN;
	}

	public void setName(String n) {
		name = n;	
	}
	public String getName() {
		return name;
	}
	
	public void setColor(char color) { this.color = color; }
	public char getColor() {
		return color;
	}

	public Move nextMove(Board board, int maxDepth) {
		// unimplemented - handled by the button presses
		return null;
	}
}
