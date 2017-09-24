package jacob.siebert.chessai.piece;

public class Bishop extends Piece {
	
	public Bishop(char color, int y, int x) {
		super(color, y, x);
		value = 3;
		type = 'b';
	}

	public Bishop(char color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 3;
		type = 'b';
	}
	
	public String getName() {
		return "Bishop";
	}
	
	@Override
	public Bishop clone() {
		return new Bishop(color, y, x, timesMoved);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Bishop at (" + y + ", " + x + ")";
	}
	
}