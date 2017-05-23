package main;

import java.util.ArrayList;

public class BookSet {
	private ArrayList<Move> bookMoves;
	
	public BookSet(ArrayList<Move> bookMoves) {
		this.bookMoves = bookMoves;
	}
	
	public boolean isEmpty() {
		return bookMoves.size() <= 0;
	}
	
	public Move getNextMove() {
		return bookMoves.remove(0);
	}
}
