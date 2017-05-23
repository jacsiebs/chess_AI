package main;

public class EnPassant extends Move {
	
	public EnPassant(Piece p, int yto, int xto, Piece captured) {
		super(p, yto, xto);
		removed = captured.clone();
	}
	
	@Override
	public boolean equals(Move m) {
		if(!(m instanceof EnPassant)) {
			return false;
		}

		if (xto != m.xto || yto != m.yto) {
			return false;
		}
		if (!piece.equals(m.getSelectedPiece())) {
			return false;
		}
		// check the piece removed by performing the moves if it exists
		if (removed != null) {
			if (m.getRemovedPiece() == null) {
				return false;
			} else if (!m.getRemovedPiece().equals(removed)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		int y1 = 8 - piece.y;
		int y2 = 8 - yto;
		char x1 = (char) (piece.x + 65);
		char x2 = (char) (xto + 65);
		if(piece.color == Piece.WHITE)
			return "EnPassant: White moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
		else
			return "EnPassant: Tan moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
	}
	
	@Override
	public EnPassant clone() {
		return new EnPassant(piece.clone(), yto, xto, removed.clone());
	}
}
