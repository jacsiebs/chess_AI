package jacob.siebert.chessai.piece;

import jacob.siebert.chessai.type.PieceColor;

public class Knight extends Piece {
	
	public Knight(PieceColor color, int y, int x) {
		super(color, y, x);
		value = 3;
		type = 'n';
	}

	public Knight(PieceColor color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 3;
		type = 'n';
	}

	@Override
	public String getName() {
		return "Knight";
	}
	
	@Override
	public Knight clone() {
		return new Knight(getColor(), y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "Knight at (" + y + ", " + x + ")";
	}
}
