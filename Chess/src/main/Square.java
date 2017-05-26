package main;

public class Square {

	private int x;
	private int y;
	
	public Square(int y, int x) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean equals(Square s) {
		if(x != s.getX()|| y != s.getY()) {
			return false;
		}
		return true;
	}
	
	public boolean equals(int py, int px) {
		if(x != px || y != py) {
			return false;
		}
		return true;
	}
}
