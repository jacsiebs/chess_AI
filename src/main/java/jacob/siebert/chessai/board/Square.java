package jacob.siebert.chessai.board;

/**
 * Represents a square on the Board.
 * Can use array indexing or chessboard indexing (1-8 and a-h)
 * to specify a square.
 */
public class Square {

	private int x;
	private int y;
	
	public Square(int y, int x) {
		this.x = x;
		this.y = y;
	}

	public Square(int y, char x) {
		this.x = x - 97;// 97 is ascii for 'a'
		this.y = 8 - y;// y index is reversed
	}

	public int getChessY() { return 8 - y; }

	public char getChessX() { return (char) (x + 97); }
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean equals(Square s) {
		return s.getX() == x && s.getY() == y;
	}
	
	public boolean equals(int py, int px) {
		return py == y && px == x;
	}
}
