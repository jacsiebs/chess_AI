package main;

import java.util.ArrayList;

public class Queen extends Piece {
	
	public Queen(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 9;
		type = 'q';
	}

	public Queen(char color, int y, int x, ArrayList<Move> validMoves) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 9;
		type = 'q';
		this.validMoves = validMoves;
	}
	
	@Override
	public Queen clone() {
		return new Queen(color, y, x);
	}
	
	public String getName() {
		return "Queen";
	}

	@Override
	public String toString() {
		return "Queen at (" + y + ", " + x + ")";
	}
	
	
}