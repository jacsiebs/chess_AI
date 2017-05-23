package main;

import java.util.ArrayList;

public class Knight extends Piece {
	
	public Knight(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 3;
		type = 'n';
	}

	public Knight(char color, int y, int x, ArrayList<Move> validMoves) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 3;
		type = 'n';
		this.validMoves = validMoves;
	}
	
	public String getName() {
		return "Knight";
	}
	
	@Override
	public Knight clone() {
		return new Knight(color, y, x);
	}

	@Override
	public String toString() {
		return "Knight at (" + y + ", " + x + ")";
	}
}
