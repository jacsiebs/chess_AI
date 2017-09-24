package jacob.siebert.chessai.piece;

public class Knight extends Piece {
	
	public Knight(char color, int y, int x) {
		super(color, y, x);
		value = 3;
		type = 'n';
	}

	public Knight(char color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 3;
		type = 'n';
	}
	
	public String getName() {
		return "Knight";
	}
	
	@Override
	public Knight clone() {
		return new Knight(color, y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "Knight at (" + y + ", " + x + ")";
	}
}
