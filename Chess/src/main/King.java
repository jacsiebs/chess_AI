package main;

public class King extends Piece {
	
	// timesMoved assumed to be 0
	public King(char color, int y, int x) {
		super(color, y, x);
		value = Integer.MAX_VALUE;
		type = 'k';
	}
	
	public King(char color, int y, int x, int timesMoved) {
		super(color, y, x, timesMoved);
		value = Integer.MAX_VALUE;
		type = 'k';
	}
	
	public String getName() {
		return "King";
	}
	
	@Override
	public King clone() {
		return new King(color, y, x, timesMoved);
	}

	@Override
	public String toString() {
		return "King at (" + y + ", " + x + ") -- Times Moved = " + timesMoved;
	}
}