package jacob.siebert.chessai.ui;

import jacob.siebert.chessai.type.PieceColor;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;

// The game pieces for the UI - actual board calculations not done using this data structure
public class PieceButton extends JButton {
	// the button's coordinates on the grid
	// WARNING: Jbutton already has variables x and y so do not change these names
	private int xPos;
	private int yPos;
	private boolean hasPiece;
	private IconGrabber icon_grabber;// used to get piece icons as needed

	public PieceButton(int y, int x, IconGrabber icon_grabber) {
		yPos = y;
		xPos = x;
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.icon_grabber = icon_grabber;
	}
	
	// stores the piece on this square and updates the icon
	public void setPiece(char newType, PieceColor newColor) {
		hasPiece = true;
		this.setIcon(icon_grabber.getIcon(newType, newColor));
	}
	
	// removes and the piece icon on this square
	public void removePiece() {
		hasPiece = false;
		this.setIcon(null);
	}
	
	// WARNING: Jbutton has a method called getY() so do not change this name
	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public void clearIcon() {
		this.setIcon(null);
	}
	
	public boolean hasPiece() {
		return hasPiece;
	}
}
