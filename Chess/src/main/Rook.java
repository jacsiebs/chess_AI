package main;

import java.util.ArrayList;

public class Rook extends Piece {
	
	private boolean firstMove;
	
	// assume firstMove = true
	public Rook(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 5;
		type = 'r';
		firstMove = true;
	}
	
	public Rook(char color, int y, int x, boolean firstMove) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 5;
		type = 'r';
		this.firstMove = firstMove;
	}

	public Rook(char color, int y, int x, boolean firstMove, ArrayList<Move> validMoves) {
		this.color = color;
		this.y = y;
		this.x = x;
		value = 5;
		type = 'r';
		this.validMoves = validMoves;
		this.firstMove = firstMove;
	}
	
	public boolean firstMove() {
		return firstMove;
	}
	
	public void setAsMoved() {
		firstMove = false;
	}
	
	public String getName() {
		return "Rook";
	}
	
	@Override
	public Rook clone() {
		return new Rook(color, y, x, firstMove);
	}

	@Override
	public String toString() {
		return "Rook at (" + y + ", " + x + ") -- FirstMove = " + firstMove;
	}
	
	@Override
	public boolean equals(Piece p) {
		if(y != p.y || x != p.x || type != p.type || color != p.color) {
			return false;
		}
		if(firstMove != ((Rook) p).firstMove()) {
			return false;
		}
		return true;
	}
}