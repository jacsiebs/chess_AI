package jacob.siebert.chessai.player;

import jacob.siebert.chessai.type.PieceColor;

public abstract class Player {

	private String name;
	private PieceColor color;

	public Player(PieceColor color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PieceColor getColor() {
		return color;
	}

	public void setColor(PieceColor color) {
		this.color = color;
	}

	public abstract boolean isHuman();

}
