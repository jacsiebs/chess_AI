package main;

import java.util.ArrayList;

public class King extends Piece {
	private boolean firstMove;
	
	public King(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 1000;
		type = 'k';
		firstMove = true;
	}
	
	public King(char color, int y, int x, boolean firstMove) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 1000;
		type = 'k';
		this.firstMove = firstMove;
	}

	public King(char color, int y, int x, ArrayList<Move> validMoves) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 1000;
		type = 'k';
		this.validMoves = validMoves;
	}
	
	public String getName() {
		return "King";
	}
	
	public boolean firstMove() {
		return firstMove;
	}
	
	public void setAsMoved() {
		firstMove = false;
	}
	
	@Override
	public King clone() {
		return new King(color, y, x, firstMove);
	}

	@Override
	public String toString() {
		return "King at (" + y + ", " + x + ") -- FirstMove = " + firstMove;
	}
	
	@Override
	public boolean equals(Piece p) {
		if(y != p.y || x != p.x || type != p.type || color != p.color) {
			return false;
		}
		if(firstMove != ((King) p).firstMove()) {
			return false;
		}
		return true;
	}
	
	
}