package jacob.siebert.chessai.piece;

import jacob.siebert.chessai.type.PieceColor;

public class Bishop extends Piece {
	
	public Bishop(PieceColor color, int y, int x) {
		super(color, y, x);
		value = 3;
		type = 'b';
	}

	public Bishop(PieceColor color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 3;
		type = 'b';
	}

	@Override
	public String getName() {
		return "Bishop";
	}
	
	@Override
	public Bishop clone() {
		return new Bishop(getColor(), y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "Bishop at (" + y + ", " + x + ")";
	}
	
}