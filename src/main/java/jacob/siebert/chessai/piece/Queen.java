package jacob.siebert.chessai.piece;

public class Queen extends Piece {
	
	public Queen(char color, int y, int x) {
		super(color, y, x);
		value = 9;
		type = 'q';
	}

	public Queen(char color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 9;
		type = 'q';
	}
	
	@Override
	public Queen clone() {
		return new Queen(color, y, x, timesMoved);
	}
	
	public String getName() {
		return "Queen";
	}

	@Override
	public String toString() {
		return "Queen at (" + y + ", " + x + ")";
	}
	
	
}