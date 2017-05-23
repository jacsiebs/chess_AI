package main;

import java.util.ArrayList;

public class Pawn extends Piece {
	private boolean firstMove;// true if this pawn has not moved
	
	public Pawn(char color, int y, int x, boolean firstMove) {
		this.color = color;
		this.y = y;
		this.x = x;
		this.firstMove = firstMove;
		value = 1;
		type = 'p';
	}
	
	// firstmove assumed to be true
	public Pawn(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		firstMove = true;
		value = 1;
		type = 'p';
	}
	
	public Pawn(char color, int y, int x, boolean firstMove, ArrayList<Move> validMoves) {
		this.color = color;
		this.y = y;
		this.x = x;
		this.firstMove = firstMove;
		value = 1;
		type = 'p';
		this.validMoves = validMoves;
	}
	
	public boolean firstMove() {
		return firstMove;
	}
	// Marks this pawn as having moved
	public void setAsMoved() {
		firstMove = false;
	}

	public String getName() {
		return "Pawn";
	}
	
	@Override
	public Pawn clone() {
		return new Pawn(color, y, x, firstMove);
	}

	@Override
	public String toString() {
		return "Pawn at (" + y + ", " + x + ") -- FirstMove = " + firstMove;
	}
	
	@Override
	public boolean equals(Piece p) {
		if(y != p.y || x != p.x || type != p.type || color != p.color) {
			return false;
		}
		if(firstMove != ((Pawn) p).firstMove()) {
			return false;
		}
		return true;
	}
}
