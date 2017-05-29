package main;

/**
 * @author Jacob Siebert
 *
 * Upgrade encapsulates a Move where a Pawn has reached the opponents last file,
 * allowing the player to upgrade the pawn into a knight, rook, queen, or bishop.
 */
public class Upgrade extends Move {
	
	private Piece upgraded;
	
	// capture and upgrade
	public Upgrade(Pawn p, int yto, int xto, Piece upgraded, Piece removed) {
		super(p, yto, xto, removed);
		this.upgraded = upgraded;
	}

	// capture and upgrade - create new upgraded piece given type and color
	public Upgrade(Pawn p, int yto, int xto, char up_type, char up_color, Piece removed) {
		super(p, yto, xto, removed);
		switch(up_type) {
		case 'q':
			upgraded = new Queen(up_color, yto, xto);
			break;
		case 'r':
			upgraded = new Rook(up_color, yto, xto);
			break;
		case 'n':
			upgraded = new Knight(up_color, yto, xto);
			break;
		case 'b':
			upgraded = new Bishop(up_color, yto, xto);
			break;
		}
	}

	// no piece removed 
	public Upgrade(Pawn p, int yto, int xto, Piece upgraded) {
		super(p, yto, xto);
		this.upgraded = upgraded;
	}
	
	@Override
	public boolean equals(Move m) {
		if(!(m instanceof Upgrade)) {
			return false;
		}
		if(xto != m.xto || yto != m.yto) {
			return false;
		}
		if(!piece.equals(m.getSelectedPiece())) {
			return false;
		}
		if(!((Upgrade) m).upgraded.equals(upgraded)) {
			return false;
		}
		if (removed != null) {
			if (m.getRemovedPiece() == null) {
				return false;
			} else if (!m.getRemovedPiece().equals(removed)) {
				return false;
			}
		}
		return true;
	}
	
	public Piece getUpgraded() {
		return upgraded;
	}

	@Override
	public String toString() {
		int y1 = 8 - piece.y;
		int y2 = 8 - yto;
		char x1 = (char) (piece.x + 65);
		char x2 = (char) (xto + 65);
		if(piece.color == Piece.WHITE)
			return "Upgrade: White moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 + ".";
		else
			return "Upgrade: Tan moves " + piece.getName() + " " + x1+y1 + " to " + x2+y2 
						+ " and becomes " + upgraded.getName() + ".";
	}
	
	@Override
	public Upgrade clone() {
		if(removed == null) {
			return new Upgrade(((Pawn) piece).clone(), yto, xto, upgraded.clone(), null);
		}
		return new Upgrade(((Pawn) piece).clone(), yto, xto, upgraded.clone(), removed.clone());
	}
}
