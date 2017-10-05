package jacob.siebert.chessai.ui;

import javax.swing.ImageIcon;
import jacob.siebert.chessai.piece.Piece;
import jacob.siebert.chessai.type.PieceColor;

public class IconGrabber {
	private SpriteSheet spriteSheet;
	
	public IconGrabber(SpriteSheet s) {
		spriteSheet = s;
	}
	
	// returns the icon related to the piece type
	public ImageIcon getIcon(char type, PieceColor color) {
		
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
			return new ImageIcon(spriteSheet.getSprite(5, p.getColor()));
		case 'b':
			return new ImageIcon(spriteSheet.getSprite(4, p.getColor()));
		case 'n':
			return new ImageIcon(spriteSheet.getSprite(3, p.getColor()));
		case 'r':
			return new ImageIcon(spriteSheet.getSprite(2, p.getColor()));
		case 'q':
			return new ImageIcon(spriteSheet.getSprite(1, p.getColor()));
		case 'k':
			return new ImageIcon(spriteSheet.getSprite(0, p.getColor()));
		}
		return null;
	}
}
