package jacob.siebert.chessai.piece;

import jacob.siebert.chessai.type.PieceColor;

public class Rook extends Piece {
	
	public Rook(PieceColor color, int y, int x) {
		super(color, y, x);
		value = 5;
		type = 'r';
	}
	
	public Rook(PieceColor color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 5;
		type = 'r';
	}

	@Override
	public String getName() {
		return "Rook";
	}
	
	@Override
	public Rook clone() {
		return new Rook(getColor(), y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "Rook at (" + y + ", " + x + ") -- Times Moved = " + timesMoved;
	}
}