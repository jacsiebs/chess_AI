package jacob.siebert.chessai.move;

import jacob.siebert.chessai.exception.NoSuchPieceException;
import jacob.siebert.chessai.piece.King;
import jacob.siebert.chessai.piece.Piece;
import jacob.siebert.chessai.piece.Rook;

/**
 * @author Jacob Siebert
 * 
 * A Castle Move
 *  - The King is the selected jacob.siebert.chessai.piece
 *  - The Old Rook Location is saved in the castled_xFrom vars
 *  - The New Rook Location is saved in the castled Rook
 *
 */
public class Castle extends Move {

	private Rook castled;// The castled rook
	// The castled position of the Rook
	public int castled_yto;
	public int castled_xto;
	// The old position of the castled Rook
	public int castled_yFrom;
	public int castled_xFrom;

	/* Note: The castled Rook's (y, x) and firstMove must be updated in applyMove() */
	public Castle(King k, int yto, int xto, Rook cast, int cast_yto, int cast_xto) {
		
		super(k, yto, xto);
		
		if(cast == null) {
			throw new NoSuchPieceException("Null Rook when trying to create a Castle");
		}
		
		castled = cast;
		castled_yFrom = castled.y;
		castled_xFrom = castled.x;
		
		castled_yto = cast_yto;
		castled_xto = cast_xto;
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
		if (!c.getCastled().equals(castled)) {
			return false;
		}
		if(c.castled_yto != castled_yto || c.castled_xto != castled_xto) {
			return false;
		}
		// removed jacob.siebert.chessai.piece is null for any castle moves
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
		return new Castle((King) piece.clone(), yto, xto, castled.clone(), castled_yto, castled_xto);
	}
}
