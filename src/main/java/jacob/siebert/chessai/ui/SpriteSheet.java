package jacob.siebert.chessai.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

// loads a sprite sheet and allows for lookup of any sprite 
public class SpriteSheet {
	private int columns;
	private int rows;
	private int xjump;
	private int yjump;
	private BufferedImage spriteSheet;
	
	public SpriteSheet(String filename, int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		
		try{
			spriteSheet = ImageIO.read(new File(filename));
		}
		catch(Exception e) {
			System.out.println("Spritesheet file not found or couldn't be loaded");
			e.printStackTrace();
		}
		
		xjump = spriteSheet.getWidth() / columns;
		yjump = spriteSheet.getHeight() / rows;
	}
	
	// gets the sprite based on the column and row
	// uses 0 based indexing
	protected BufferedImage getSprite(int column, int row) {
		if((column+1)*xjump <= spriteSheet.getWidth() && 
				(row+1)*yjump <= spriteSheet.getHeight()) {
			return spriteSheet.getSubimage(column * xjump, row * yjump, xjump, yjump);
		} else {
			System.out.println("Can't locate the sprite");
			return null;
		}
	}
}
