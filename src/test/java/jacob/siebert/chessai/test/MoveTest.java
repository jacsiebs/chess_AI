//package jacob.siebert.chessai.test;
//
//import static org.junit.Assert.assertTrue;
//
//import jacob.siebert.chessai.move.EnPassant;
//import jacob.siebert.chessai.piece.Pawn;
//import jacob.siebert.chessai.piece.Piece;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//public class MoveTest {
//
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}

//	@Test
//	public void enPassantEqualsTest() {
//		Pawn p1 = new Pawn(Piece.WHITE, 3, 4, false);
//		Pawn p1_cap = new Pawn(Piece.WHITE, 3, 3, false);
//		EnPassant e1 = new EnPassant(p1, p1_cap.y - 1, p1_cap.x, p1_cap);
//		Pawn p2 = new Pawn(Piece.WHITE, 3, 4, false);
//		Pawn p2_cap = new Pawn(Piece.WHITE, 3, 3, false);
//		EnPassant e2 = new EnPassant(p2, p2_cap.y - 1, p2_cap.x, p2_cap);
//
//		assertTrue(e1.equals(e2));
//	}
//
//}
