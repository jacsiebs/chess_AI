package main;


/**
 * @author Jacob
 *
 * Encapsulates a single player's Move, including the Piece moved, any Piece captured for
 * easy undoing, and the new location of the moved Piece. A moved Piece is cloned to preserve
 * its old location for undoing. 
 * Three Special Moves exist as subclasses:
 * 1. Castle	2. EnPassant	3. Upgrade
 */
public class Move {
	protected Piece piece;
	protected Piece removed;// The piece removed by this move, null if none. Also contains the castled piece.
	protected int xto;
	protected int yto;
	
	public Move(Piece p, int yto, int xto) {
		piece = p;
		this.xto= xto;
		this.yto= yto;
	}
	
	public Move(Piece p, int yto, int xto, Piece removed) {
		piece = p;
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
	
	public Piece getRemovedPiece() throws NoSuchPieceException {
		if(removed == null) 
			throw new NoSuchPieceException("Attempted to get a removed piece that does not exist.");
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
	 * Detects if this move is equal to another move.
	 * Recall that a instance method is always called on the actual class type of the object
	 * and therefore if m is an instance of any subclass of move, return false.
	 * @param m - the other move to compare
	 * @return are the moves equivalent?
	 */
	public boolean equals(Move m) {
		// check subclasses
		if(m instanceof Castle || m instanceof EnPassant || m instanceof Upgrade) {
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
		return new Move(piece.clone(), yto, xto, removed);
	}
	
	
}
