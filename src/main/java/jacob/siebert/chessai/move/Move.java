package jacob.siebert.chessai.move;

import jacob.siebert.chessai.piece.Piece;

/**
 * @author Jacob
 *
 * Encapsulates a single jacob.siebert.chessai.player's Move, including the Piece moved, any Piece captured for
 * easy undoing, and the new location of the moved Piece. A moved Piece is cloned to preserve
 * its old location for undoing. 
 * Three Special Moves exist as subclasses:
 * 1. Castle	2. EnPassant	3. Promotion
 */
public class Move {
	public Piece piece;
	public Piece removed;// The jacob.siebert.chessai.piece removed by this jacob.siebert.chessai.move, null if none. Also contains the castled jacob.siebert.chessai.piece.
	// the location the jacob.siebert.chessai.piece will be moved to
	public int xto;
	public int yto;
	// used for undo operations since the jacob.siebert.chessai.piece's coordinates change - set in constructor
	public int xfrom;
	public int yfrom;
	
	// The removed jacob.siebert.chessai.piece is assumed to be null
	public Move(Piece p, int yto, int xto) {
		piece = p;
		xfrom = p.x;
		yfrom = p.y;
		this.xto= xto;
		this.yto= yto;
	}
	
	// With removed jacob.siebert.chessai.piece specified - can be null if a removed jacob.siebert.chessai.piece does not exist
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
		if(piece.color == Piece.WHITE)
			return "White moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
		else
			return "Tan moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
	}
	
	/**
	 * Detects if this jacob.siebert.chessai.move is equal to another jacob.siebert.chessai.move.
	 * Recall that a instance method is always called on the actual class type of the object
	 * and therefore if m is an instance of any subclass of jacob.siebert.chessai.move, return false.
	 * @param m - the other jacob.siebert.chessai.move to compare
	 * @return are the moves equivalent?
	 */
	public boolean equals(Move m) {
		// check subclasses
		if(m instanceof Castle || m instanceof EnPassant || m instanceof Promotion) {
			return false;
		}
		// Check normal jacob.siebert.chessai.move info
		if(xto != m.xto || yto != m.yto){
			return false;
		}
		if(!piece.equals(m.getSelectedPiece())) {
			return false;
		}
		// check the jacob.siebert.chessai.piece removed by performing the moves if it exists
		if(removed != null) {
			if(m.getRemovedPiece() == null) {
				return false;
			} else if(!m.getRemovedPiece().equals(removed)){
				return false;
			}
		}		
		return true;
	}
	
	// returns a new jacob.siebert.chessai.move with all pieces cloned
	public Move clone() {
		if(removed == null) {
			return new Move(piece.clone(), yto, xto);
		}
		return new Move(piece.clone(), yto, xto, removed.clone());
	}
	
	
}
