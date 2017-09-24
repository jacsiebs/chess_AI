package jacob.siebert.chessai.test;

import java.board.Board;
import java.board.Square;
import java.piece.*;
import java.util.ArrayList;

public class ThreatPathsUnitTest {
	public static void main(String[] args) {
		Board b = new Board(Board.EMPTY);
		King k1 = new King(Piece.TAN, 0, 0);
		b.placePiece(k1);
		Queen b2 = new Queen(Piece.WHITE, 7, 7);
		b.placePiece(b2);
		Knight n2 = new Knight(Piece.WHITE, 1, 2);
		b.placePiece(n2);
		Rook r2 = new Rook(Piece.WHITE, 0, 7);
		b.placePiece(r2);

		b.displayBoard();

		System.out.println("+---+---+---+---+---+---+---+---+");
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				boolean isThreatPath = false;
				ArrayList<ArrayList<Square>> threatPaths = b.generateThreatPaths(k1);
				for(ArrayList<Square> a: threatPaths) {
					for(Square d: a) {
						if(d.getX() == i && d.getY() == j) {
							isThreatPath = true;
							break;
						}
					}
				}
				if(isThreatPath) System.out.print("| 0 ");
				else System.out.print("| _ ");
				
			}
			System.out.println("|");
			System.out.println("+---+---+---+---+---+---+---+---+");
		}
	}
}
