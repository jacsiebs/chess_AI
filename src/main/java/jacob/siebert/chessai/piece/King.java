package jacob.siebert.chessai.piece;

import jacob.siebert.chessai.type.PieceColor;

public class King extends Piece {
	
	// timesMoved assumed to be 0
	public King(PieceColor color, int y, int x) {
		super(color, y, x);
		value = Integer.MAX_VALUE;
		type = 'k';
	}
	
	public King(PieceColor color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = Integer.MAX_VALUE;
		type = 'k';
	}

	@Override
	public String getName() {
		return "King";
	}
	
	@Override
	public King clone() {
		return new King(getColor(), y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "King at (" + y + ", " + x + ") -- Times Moved = " + timesMoved;
	}
}