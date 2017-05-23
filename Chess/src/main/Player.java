package main;

public interface Player {

	public boolean isHuman();
	public void setName(String n);
	public String getName();
	public char getColor();
	public Move nextMove(Board board, int maxDepth);
}
