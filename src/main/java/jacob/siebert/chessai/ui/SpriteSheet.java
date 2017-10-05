package jacob.siebert.chessai.ui;

import jacob.siebert.chessai.type.PieceColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

// loads a sprite sheet and allows for lookup of any sprite 
public class SpriteSheet {

	private static Logger LOG = LoggerFactory.getLogger(SpriteSheet.class);

	private int columns;
	private int rows;
	private int xjump;
	private int yjump;
	private BufferedImage spriteSheet;
	
	public SpriteSheet(String filename, int columns, int rows) {
		this.columns = columns;
		this.rows = rows;

		InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
		
		try{
			spriteSheet = ImageIO.read(in);
		}
		catch(Exception e) {
			LOG.error("Spritesheet file not found or couldn't be loaded. (" + filename + ")");
		}
		
		xjump = spriteSheet.getWidth() / columns;
		yjump = spriteSheet.getHeight() / rows;
	}

	// gets the sprite based on the column and row
	// uses 0 based indexing
	public BufferedImage getSprite(int column, PieceColor color) {
		int row = getRowOfColor(color);
		if((column+1)*xjump <= spriteSheet.getWidth() && 
				(row+1)*yjump <= spriteSheet.getHeight()) {
			return spriteSheet.getSubimage(column * xjump, row * yjump, xjump, yjump);
		} else {
			System.out.println("Can't locate the sprite");
			return null;
		}
	}

	private int getRowOfColor(PieceColor color) {
		if(color == PieceColor.TAN) {
			return 0;
		} else {
			// white
			return 1;
		}
	}
}
