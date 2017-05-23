package main;

public class Castle extends Move {

	private Rook castled;

	// castle move constructor
	public Castle(Piece p, int yto, int xto, Rook cast) {
		// ROMOVE TODO
		super(p, yto, xto);

		if (cast == null) {
			throw new NoSuchPieceException("Castle piece is null");
		}

		castled = cast.clone();
		// set the rook position based on which side it comes from
		if (cast.x == 7) {
			removed.setYX(cast.y, xto - 1);
		} else if (cast.x == 0) {
			removed.setYX(cast.y, xto + 1);
		} else {
			// DELETE THIS TODO
			System.out.println("This should not be here. A castle move which is not correct.");
		}
	}

	public Rook getCastled() {
		return castled;
	}

	@Override
	public boolean equals(Move m) {
		if (!(m instanceof Castle)) {
			return false;
		}
		Castle c = (Castle) m;
		
		if (xto != m.xto || yto != m.yto) {
			return false;
		}
		if (!piece.equals(m.getSelectedPiece())) {
			return false;
		}
		if (!c.castled.equals(castled)) {
			return false;
		}
		// removed piece is null for any castle moves
		return true;
	}
	
	@Override
	public String toString() {
		int y1 = 8 - piece.y;
		int y2 = 8 - yto;
		char x1 = (char) (piece.x + 65);
		char x2 = (char) (xto + 65);
		if(piece.color == Piece.WHITE)
			return "Castle: White moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
		else
			return "Castle: Tan moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
	}
	
	@Override
	public Castle clone() {
		return new Castle(piece.clone(), yto, xto, castled.clone());
	}
}
