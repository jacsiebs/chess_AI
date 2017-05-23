package main;

import java.util.ArrayList;

public class Bishop extends Piece {
	
	public Bishop(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 3;
		type = 'b';
	}

	public Bishop(char color, int y, int x, ArrayList<Move> validMoves) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 3;
		type = 'b';
		this.validMoves = validMoves;
	}
	
	public String getName() {
		return "Bishop";
	}
	
	@Override
	public Bishop clone() {
		return new Bishop(color, y, x);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Bishop at (" + y + ", " + x + ")";
	}
	
}