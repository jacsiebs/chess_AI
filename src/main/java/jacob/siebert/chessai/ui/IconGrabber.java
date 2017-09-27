package jacob.siebert.chessai.ui;

import javax.swing.ImageIcon;
import jacob.siebert.chessai.piece.Piece;

public class IconGrabber {
	private SpriteSheet spriteSheet;
	
	public IconGrabber(SpriteSheet s) {
		spriteSheet = s;
	}
	
	// returns the icon related to the piece type
	public ImageIcon getIcon(char type, char color) {
		
		switch(type) {
		case 'p':
			return new ImageIcon(spriteSheet.getSprite(5, color));
		case 'b':
			return new ImageIcon(spriteSheet.getSprite(4, color));
		case 'n':
			return new ImageIcon(spriteSheet.getSprite(3, color));
		case 'r':
			return new ImageIcon(spriteSheet.getSprite(2, color));
		case 'q':
			return new ImageIcon(spriteSheet.getSprite(1, color));
		case 'k':
			return new ImageIcon(spriteSheet.getSprite(0, color));
		}
		return null;
	}

	// returns the icon related to the piece type
	public ImageIcon getIcon(Piece p) {
		switch(p.type) {
		case 'p':
			return new ImageIcon(spriteSheet.getSprite(5, p.color));
		case 'b':
			return new ImageIcon(spriteSheet.getSprite(4, p.color));
		case 'n':
			return new ImageIcon(spriteSheet.getSprite(3, p.color));
		case 'r':
			return new ImageIcon(spriteSheet.getSprite(2, p.color));
		case 'q':
			return new ImageIcon(spriteSheet.getSprite(1, p.color));
		case 'k':
			return new ImageIcon(spriteSheet.getSprite(0, p.color));
		}
		return null;
	}
}
