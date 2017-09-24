package jacob.siebert.chessai.piece;

public class Pawn extends Piece {

	public Pawn(char color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = 1;
		type = 'p';
	}
	
	public Pawn(char color, int y, int x) {
		super(color, y, x);
		value = 1;
		type = 'p';
	}
	
	public String getName() {
		return "Pawn";
	}
	
	@Override
	public Pawn clone() {
		return new Pawn(color, y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "Pawn at (" + y + ", " + x + ") -- Times Moved = " + timesMoved;
	}
}
