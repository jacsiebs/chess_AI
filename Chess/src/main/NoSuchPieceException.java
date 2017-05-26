package main;

public class NoSuchPieceException extends RuntimeException {
	
	public NoSuchPieceException(int y, int x) {
        super("Could not locate the piece at y: " + y + ", x: " + x);
    }
	
	public NoSuchPieceException(String message) {
        super(message);
    }
	
	public NoSuchPieceException(Piece p) {
        super("Could not find: " + p.toString());
    }
}
