package jacob.siebert.chessai.piece;

import jacob.siebert.chessai.type.PieceColor;

public class Queen extends Piece {
	
	public Queen(PieceColor color, int y, int x) {
		super(color, y, x);
		value = 9;
		type = 'q';
	}

	public Queen(PieceColor color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 9;
		type = 'q';
	}
	
	@Override
	public Queen clone() {
		return new Queen(getColor(), y, x, timesMoved);
	}

	@Override
	public String getName() {
		return "Queen";
	}

	@Override
	public String toString() {
		return "Queen at (" + y + ", " + x + ")";
	}
	
	
}