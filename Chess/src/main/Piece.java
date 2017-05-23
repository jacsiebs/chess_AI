package main;

import java.util.ArrayList;

public abstract class Piece {
	public final static char WHITE = 1;
	public final static char TAN = 0;
	// protected boolean isThreatened;
	// 0 = white; 1 = tan
	protected char color;
	// counts (y,x) from top left corner
	protected int x;
	protected int y;
	protected int value;
	protected ArrayList<Move> validMoves;
	/* Types:
	 * k - King, q - Queen, p - Pawn, r -Rook, b - Bishop, n - Knight
	 */
	protected char type;

	public boolean isOpponent(Piece opp) {
		if (opp.color == color)
			return false;
		return true;
	}
	
	public char getType() {
		return type;
	}
	
	public abstract String getName();

	public void setYX(int newy, int newx) {
		y = newy;
		x = newx;
	}

	/*
	 * Returns this piece's valid moves assuming the list remains valid. Be sure
	 * the valid moves have been updated before calling this method.
	 */
	public ArrayList<Move> getValidMoves() {
		return validMoves;
	}

	public void setValidMoves(ArrayList<Move> vMoves) {
		validMoves = vMoves;
	}

	// returns a copy of this Piece
	public abstract Piece clone();

	public abstract String toString();
	
	// overridden for pieces where the firstMove boolean must be compared as well
	// does not check the pieces current valid moves for equality
	public boolean equals(Piece p) {
		if(y != p.y || x != p.x || type != p.type || color != p.color) {
			return false;
		}
		return true;
	}
	

}
