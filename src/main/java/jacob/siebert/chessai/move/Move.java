package jacob.siebert.chessai.move;

import jacob.siebert.chessai.piece.Piece;

/**
 * @author Jacob Siebert
 *
 * Encapsulates a single player's Move, including the Piece moved, any Piece captured for
 * easy undoing, and the new location of the moved Piece. A moved Piece is cloned to preserve
 * its old location for undoing. 
 * Three Special Moves exist as subclasses:
 * 1. Castle	2. EnPassant	3. Promotion
 */
public class Move {
	public Piece piece;
	public Piece removed;// The piece removed by this move, null if none. Also contains the castled piece.
	// the location the piece will be moved to
	public int xto;
	public int yto;
	// used for undo operations since the piece's coordinates change - set in constructor
	public int xfrom;
	public int yfrom;
	
	// The removed piece is assumed to be null
	public Move(Piece p, int yto, int xto) {
		piece = p;
		xfrom = p.x;
		yfrom = p.y;
		this.xto= xto;
		this.yto= yto;
	}
	
	// With removed piece specified - can be null if a removed piece does not exist
	public Move(Piece p, int yto, int xto, Piece removed) {
		piece = p;
		xfrom = p.x;
		yfrom = p.y;
		this.xto= xto;
		this.yto= yto;
		this.removed = removed;
	}
	
	public Piece getSelectedPiece() {
		return piece;
	}
	
	public void setRemovedPiece(Piece p) {
		removed = p;
	}
	
	// can be a null Piece
	public Piece getRemovedPiece() {
		return removed;
	}
	
	public int getPieceX() {
		return piece.x;
	}
	
	public int getPieceY() {
		return piece.y;
	}
	
	public String toString() {
		int y1 = 8 - piece.y;
		int y2 = 8 - yto;
		char x1 = (char) (piece.x + 65);
		char x2 = (char) (xto + 65);
		if(piece.isWhite())
			return "White moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
		else
			return "Tan moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
	}
	
	/**
	 * Detects if this move is equal to another move.
	 * Recall that a instance method is always called on the actual class type of the object
	 * and therefore if m is an instance of any subclass of move, return false.
	 * @param m - the other move to compare
	 * @return are the moves equivalent?
	 */
	public boolean equals(Move m) {
		// check subclasses
		if(m instanceof Castle || m instanceof EnPassant || m instanceof Promotion) {
			return false;
		}
		// Check normal move info
		if(xto != m.xto || yto != m.yto){
			return false;
		}
		if(!piece.equals(m.getSelectedPiece())) {
			return false;
		}
		// check the piece removed by performing the moves if it exists
		if(removed != null) {
			if(m.getRemovedPiece() == null) {
				return false;
			} else if(!m.getRemovedPiece().equals(removed)){
				return false;
			}
		}		
		return true;
	}
	
	// returns a new move with all pieces cloned
	public Move clone() {
		if(removed == null) {
			return new Move(piece.clone(), yto, xto);
		}
		return new Move(piece.clone(), yto, xto, removed.clone());
	}
	
	
}
