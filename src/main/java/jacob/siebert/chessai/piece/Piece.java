package jacob.siebert.chessai.piece;

import jacob.siebert.chessai.move.Move;

import java.util.ArrayList;

public abstract class Piece {
	public final static char WHITE = 1;
	public final static char TAN = 0;
	// protected boolean isThreatened;
	// 0 = white; 1 = tan
	public char color;
	public int timesMoved;
	// counts (y,x) from top left corner
	public int x;
	public int y;
	public int value;
	private ArrayList<Move> validMoves;
	/* Types:
	 * k - King, q - Queen, p - Pawn, r -Rook, b - Bishop, n - Knight
	 */
	public char type;
	
	// timesMoved assumed to be 0
	public Piece(char color, int y, int x) {
		this.color = color;
		this.y = y;
		this.x = x;
		timesMoved = 0;
	}
	
	public Piece(char color, int y, int x, int timesMoved) {
		this.color = color;
		this.y = y;
		this.x = x;
		this.timesMoved = timesMoved;
	}

	public boolean isOpponent(Piece opp) {
		if (opp.color == color)
			return false;
		return true;
	}
	
	public boolean hasMoved() {
		if(timesMoved == 0) {
			return true;
		}
		return false;
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
	 * Returns this jacob.siebert.chessai.piece's valid moves assuming the list remains valid. Be sure
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
		if(y != p.y || x != p.x || type != p.type || color != p.color || timesMoved != p.timesMoved) {
			return false;
		}
		return true;
	}
	
	// used for testing
	public boolean equalsIgnoreTimesMoved(Piece p) {
		if(y != p.y || x != p.x || type != p.type || color != p.color) {
			return false;
		}
		return true;
	}
}
