package main;

public class ThreateningUnitTest {
	
	public static void main(String[] args) {
		Board b = new Board(Board.EMPTY);
		// add pieces to the board
//		Queen p1 = new Queen(Piece.TAN, 4, 4);
//		b.placePiece(p1);
//		Bishop p2 = new Bishop(Piece.TAN, 2, 2);
//		b.placePiece(p2);
//		Rook p3 = new Rook(Piece.TAN, 1, 0);
//		b.placePiece(p3);
//		Knight p4 = new Knight(Piece.TAN, 5, 4);
//		b.placePiece(p4);
//		Pawn p5 = new Pawn(Piece.TAN, 5, 3);
//		b.placePiece(p5);
		King p6 = new King(Piece.TAN, 5, 2);
		b.placePiece(p6);
		
		b.displayBoard();
		
		System.out.println("+---+---+---+---+---+---+---+---+");
		for(int j=0; j<8; j++) {		
			for(int i=0; i<8; i++) {
				if(b.isThreatenedSquare(Piece.WHITE, j, i)) {
					System.out.print("| 0 ");
				} else {
					System.out.print("| _ ");
				}
			}
			System.out.println("|");
			System.out.println("+---+---+---+---+---+---+---+---+");
		}
	}

}
