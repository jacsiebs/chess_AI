package main;

public class HumanPlayer implements Player {
	private final boolean human = true;
	private String name;
	private char color;
	
	HumanPlayer(char color) {
		this.color = color;
	}
	@Override
	public boolean isHuman() {
		// TODO Auto-generated method stub
		return human;
	}
	
	@Override
	public void setName(String n) {
		name = n;	
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public char getColor() {
		return color;
	}
	@Override
	public Move nextMove(Board board, int maxDepth) {
		// unimplemented - handled by the button presses
		return null;
	}
}
