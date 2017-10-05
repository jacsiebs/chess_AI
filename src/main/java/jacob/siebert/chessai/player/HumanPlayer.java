package jacob.siebert.chessai.player;

import jacob.siebert.chessai.type.PieceColor;

public class HumanPlayer extends Player {

	private final static boolean HUMAN = true;
	
	public HumanPlayer(PieceColor color) {
		super(color);
	}

	public boolean isHuman() {
		return HUMAN;
	}
}
