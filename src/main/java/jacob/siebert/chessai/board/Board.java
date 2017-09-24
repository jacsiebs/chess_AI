package jacob.siebert.chessai.board;

import jacob.siebert.chessai.exception.NoSuchPieceException;
import jacob.siebert.chessai.move.Castle;
import jacob.siebert.chessai.move.EnPassant;
import jacob.siebert.chessai.move.Move;
import jacob.siebert.chessai.move.Promotion;
import jacob.siebert.chessai.piece.*;

import java.util.ArrayList;
import java.util.Stack;

public class Board {
	public final static int NEW_GAME = 0;// default setup
	public final static int LOAD_GAME = 1;// load game from text file
	public final static int EMPTY = 2;// empty board

	// The game pieces are stored in this double array for quick lookup
	// operations and moves
	// is deep copied by using the white and tan locations arraylists
	private Piece[][] board;
	// a list of all active pieces on each side
	private ArrayList<Piece> whiteLocations;
	private ArrayList<Piece> tanLocations;
	private ArrayList<Piece> whiteThreats;
	private ArrayList<Piece> tanThreats;
	private King whiteKing;
	private King tanKing;
	private Stack<Move> moves;
	private Pawn lastDoublePawn = null;// used to determine when an en-passant
										// is allowed
	private boolean tanCheck;// true when tan king is in check
	private boolean whiteCheck;

	public double sbe() {
		int sbe = 0;
		for (Piece p : tanLocations) {
			sbe -= p.value;
		}
		for (Piece p : whiteLocations) {
			sbe += p.value;
		}
		// System.out.println(sbe);
		return sbe;
	}

	public boolean tanInCheck() {
		return tanCheck;
	}

	public boolean whiteInCheck() {
		return whiteCheck;
	}

	// board type specifics which pieces are placed and where
	public Board(int boardType) {
		board = new Piece[8][8];
		moves = new Stack<Move>();
		whiteLocations = new ArrayList<Piece>();
		tanLocations = new ArrayList<Piece>();
		
		if (NEW_GAME == boardType) {
			board[0][4] = new King(Piece.WHITE, 0, 4);
			whiteLocations.add(board[0][4]);
			whiteKing = (King) board[0][4];
			board[7][4] = new King(Piece.TAN, 7, 4);
			tanLocations.add(board[7][4]);
			tanKing = (King) board[7][4];
			
			for (int i = 0; i < 8; i++) {
				board[1][i] = new Pawn(Piece.WHITE, 1, i);
				whiteLocations.add(board[1][i]);
				board[6][i] = new Pawn(Piece.TAN, 6, i);
				tanLocations.add(board[6][i]);
			}
			board[0][0] = new Rook(Piece.WHITE, 0, 0);
			whiteLocations.add(board[0][0]);
			board[7][0] = new Rook(Piece.TAN, 7, 0);
			tanLocations.add(board[7][0]);
			board[0][1] = new Knight(Piece.WHITE, 0, 1);
			whiteLocations.add(board[0][1]);
			board[7][1] = new Knight(Piece.TAN, 7, 1);
			tanLocations.add(board[7][1]);
			board[0][2] = new Bishop(Piece.WHITE, 0, 2);
			whiteLocations.add(board[0][2]);
			board[7][2] = new Bishop(Piece.TAN, 7, 2);
			tanLocations.add(board[7][2]);
			board[7][3] = new Queen(Piece.TAN, 7, 3);
			tanLocations.add(board[7][3]);
			board[0][3] = new Queen(Piece.WHITE, 0, 3);
			whiteLocations.add(board[0][3]);
			board[0][5] = new Bishop(Piece.WHITE, 0, 5);
			whiteLocations.add(board[0][5]);
			board[7][5] = new Bishop(Piece.TAN, 7, 5);
			tanLocations.add(board[7][5]);
			board[0][6] = new Knight(Piece.WHITE, 0, 6);
			whiteLocations.add(board[0][6]);
			board[7][6] = new Knight(Piece.TAN, 7, 6);
			tanLocations.add(board[7][6]);
			board[0][7] = new Rook(Piece.WHITE, 0, 7);
			whiteLocations.add(board[0][7]);
			board[7][7] = new Rook(Piece.TAN, 7, 7);
			tanLocations.add(board[7][7]);
		} else if (LOAD_GAME == boardType) {
			// TODO: read from text file
		}
		generateAllMoves();// initialize
	}

	// both parameters must have the king listed first
	// used for cloning only
	private Board(ArrayList<Piece> tans, ArrayList<Piece> whites) {
		tanLocations = new ArrayList<Piece>();
		whiteLocations = new ArrayList<Piece>();
		board = new Piece[8][8];
		tanKing = (King) tans.get(0);
		whiteKing = (King) whites.get(0);
		
		for (Piece p : tans) {
			Piece newP = p;
			tanLocations.add(newP);
			board[newP.y][newP.x] = newP;
		}
		for (Piece p : whites) {
			Piece newP = p.clone();
			whiteLocations.add(newP);
			board[newP.y][newP.x] = newP;
		}
	}
	
	/** Removes the piece at the specified location
	 * @param ry - The y coordinate
	 * @param rx - The x coordinate
	 * @return true if there was a piece at the location to be removed
	 */
	public boolean removePiece(int ry, int rx) {
		if(board[ry][rx] == null) {
			return false;
		}
		if(board[ry][rx] instanceof King) {
			throw new RuntimeException("Cannot remove the king from play!");
		}
		Piece rm = board[ry][rx];
		board[ry][rx] = null;
		if(!removePieceFromLocationsList(rm)) {
			throw new NoSuchPieceException(rm);
		}
		return true;
	}
	
	public Piece takeAndRemovePiece(int ry, int rx) {
		if(board[ry][rx] == null) {
			throw new NoSuchPieceException(ry, rx);
		}
		if(board[ry][rx] instanceof King) {
			throw new RuntimeException("Cannot remove the king from play!");
		}
		Piece rm = board[ry][rx];
		board[ry][rx] = null;
		if(!removePieceFromLocationsList(rm)) {
			throw new NoSuchPieceException(rm);
		}
		return rm;
	}

	public Board clone() {
		return new Board(tanLocations, whiteLocations);
	}

	// TODO
	public boolean gameOver() {
		return false;
	}

	public Piece[][] getBoard() {
		return board;
	}

	public ArrayList<Piece> getWhitePieces() {
		return whiteLocations;
	}

	// returns [y][x]
	public int[][] getWhiteLocations() {
		int[][] coords = new int[whiteLocations.size()][2];
		int i = 0;
		for (Piece p : whiteLocations) {
			coords[i][0] = p.y;
			coords[i][1] = p.x;
			i++;
		}
		return coords;
	}

	public ArrayList<Piece> getTanPieces() {
		return tanLocations;
	}

	// Note: King mist always be the first piece in the locations list
	public King getWhiteKing() {
		return whiteKing;
	}

	// Note: King mist always be the first piece in the locations list
	public King getTanKing() {
		return tanKing;
	}
	
	public Pawn getLastDoublePawn() {
		return lastDoublePawn;
	}

	public int[][] getTanLocations() {
		int[][] coords = new int[tanLocations.size()][2];
		int i = 0;
		for (Piece p : tanLocations) {
			coords[i][0] = p.y;
			coords[i][1] = p.x;
			i++;
		}
		return coords;
	}
	
	/**
	 * Removes the specified Piece from the locations list (and not the board)
	 * 
	 * @param removed - The Piece to remove from the list
	 * @return True if the Piece was found
	 */
	private boolean removePieceFromLocationsList(Piece removed) {
		if (removed.color == Piece.TAN) {
			for (Piece o : tanLocations) {
				if (removed.equals(o)) {
					tanLocations.remove(o);
					return true;
				}
			}
		} else {
			for (Piece o : whiteLocations) {
				if (removed.equals(o)) {
					whiteLocations.remove(o);
					return true;
				}
			}
		}
		return false;
	}

	private void addPieceToLocationsList(Piece add) {
		if(add.color == Piece.TAN) {
			tanLocations.add(add);
		} else {
			whiteLocations.add(add);
		}
	}
	
	/**Applies the given Move to this Board. The Move is added to the move stack
	 * and can be undone by using undoMove(), which does this function
	 * in reverse using the Move info to recreate the state. 
	 * The given Move is assumed to be valid and therefore only Moves generated
	 * from generateValidMoves() should be used.
	 * 
	 * @param m - The Move to apply (Can also be any Move subclass)
	 * @return The piece removed by this move if one exists, else null
	 */
	public Piece applyMove(Move m) {
		// Note: any removed piece will be removed from the locations list at the end of this method
		Piece removed = null;
		// old piece location is preserved within the Move
		Piece moved = m.getSelectedPiece();
		
		// Clear any potential en-passant moves - only 1 chance to make this move
		lastDoublePawn = null;
		
		/* Check for Move subclasses which need special implementation.
		 * Remove the captured piece if one exists */
		if (m instanceof EnPassant) {
			// removed piece must exist for an EnPassant
			removed = m.getRemovedPiece();// removed piece is already stored in the EnPassant
			board[removed.y][removed.x] = null;
			// move capturing pawn
			moved.setYX(m.yto, m.xto);
			moved.timesMoved++;
			board[m.yfrom][m.xfrom] = null;
			board[m.yto][m.xto] = moved;
		}
		else if(m instanceof Castle) {
			Castle castledMove = (Castle) m;
			// no piece to remove but 2 pieces to move - Rook and King
			// also set both these pieces as having been moved
			Rook castled = castledMove.getCastled();
			castled.setYX(castledMove.castled_yto, castledMove.castled_xto);
			castled.timesMoved++;
			board[castledMove.castled_yto][castledMove.castled_xto] = castled;
			board[castledMove.castled_yFrom][castledMove.castled_xFrom] = null;
			
			King k = (King) moved;
			moved.timesMoved++;
			k.setYX(m.yto, m.xto);
			board[m.yto][m.xto] = k;	
			board[m.yfrom][m.xfrom] = null;
		}
		else if(m instanceof Promotion) {
			Promotion upMove = (Promotion) m;
			// check if this upgrade is also a capture and remove the enemy piece if so
			removed = m.getRemovedPiece();
			// upgraded piece already has y,x set - The Pawn does not need to be updated
			Piece upgraded = upMove.getUpgraded();
			upgraded.timesMoved = moved.timesMoved + 1;
			board[m.yto][m.xto] = upgraded;
			// remove the Pawn from the locations list and add the new upgraded piece
			removePieceFromLocationsList(moved);
			addPieceToLocationsList(upgraded);
		} 
		// All normal Moves
		else {
			// remove enemy piece if it exists
			removed = board[m.yto][m.xto];
			// store the removed Piece in the move for undoing
			if(removed != null) {
				m.setRemovedPiece(removed);
			}
			// Move piece and update its x and y
			board[m.getPieceY()][m.getPieceX()] = null;
			moved.setYX(m.yto, m.xto);
			moved.timesMoved++;
			board[m.yto][m.xto] = moved;
			// If a Rook, King, or Pawn has moved, record this
			if(moved instanceof Pawn) {
				// If this was a double forward pawn move, record it for potential en-passant moves
				if (Math.abs(m.yto - m.yfrom) > 1) {
					lastDoublePawn = (Pawn) moved;
				}
			}
		}

		// if a piece was removed by this move the piece must be removed from
		// the locations list as well 
		if (removed != null) {
			removePieceFromLocationsList(removed);
		}
		
		// update check values for both kings
		isInCheck(getWhiteKing());
		isInCheck(getTanKing());

		// push the move onto the moves stack so it can be undone
		moves.push(m);

		// update the valid moves for all pieces affected by this move
		// if the square which was moved to was empty - search for effects and
		// update move pools
		// if (removed == null) {
		// ArrayList<Piece> affected = getRelatedPieces(m.yto, m.xto);
		// for (Piece regenMoves : affected) {
		// regenMoves.setValidMoves(generateValidMoves(regenMoves));
		// }
		// }
		// // search and update pieces affected by the opening at the old
		// position
		// ArrayList<Piece> affected = getRelatedPieces(m.getPieceY(),
		// m.getPieceX());
		// for (Piece regenMoves : affected) {
		// regenMoves.setValidMoves(generateValidMoves(regenMoves));
		// }
		generateAllMoves();// TODO make this more efficient and test the
							// commented out section

		return removed;
	}

	// get all pieces which are close by or pressure this square
	// Efficiency upgrade - change this algorithm to only include relevant pieces
	// (not all ones nearby)
	private ArrayList<Piece> getRelatedPieces(int y, int x) {
		ArrayList<Piece> relatedPieces = new ArrayList<Piece>();
		// check straight paths
		// search down
		int j = y + 1;
		int i = x;
		while (j < 8 && board[j][i] == null) {
			j++;
		}
		if (j < 8) {
			if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (j <= y + 2) {
				relatedPieces.add(board[j][i]);
			}
		}
		// search left
		j = y;
		i = x - 1;
		while (i >= 0 && board[j][i] == null) {
			i--;
		}
		if (i >= 0) {
			if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (i == x - 1 && board[i][j] instanceof King) {
				relatedPieces.add(board[j][i]);
			}
		}
		// search up
		j = y - 1;
		i = x;
		while (j >= 0 && board[j][i] == null) {
			j--;
		}
		if (j >= 0) {
			if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (j >= y - 2) {
				relatedPieces.add(board[j][i]);
			}
		}
		// search right
		j = y;
		i = x + 1;
		while (i < 8 && board[j][i] == null) {
			i++;
		}
		if (i < 8) {
			if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (i == x + 1 && board[i][j] instanceof King) {
				relatedPieces.add(board[j][i]);
			}
		}

		// check diagonal paths
		// search down-right
		j = y + 1;
		i = x + 1;
		while (j < 8 && i < 8 && board[j][i] == null) {
			j++;
			i++;
		}
		if (j < 8 && i < 8) {
			if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (j == y + 1 && i == x + 1 && (board[j][i] instanceof Pawn || board[j][i] instanceof King)) {
				relatedPieces.add(board[j][i]);
			}
		}
		// search down-left
		j = y + 1;
		i = x - 1;
		while (j < 8 && i >= 0 && board[j][i] == null) {
			j++;
			i--;
		}
		if (j < 8 && i >= 0) {
			if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (j == y + 1 && i == x - 1 && (board[j][i] instanceof Pawn || board[j][i] instanceof King)) {
				relatedPieces.add(board[j][i]);
			}
		}
		// search up-left
		j = y - 1;
		i = x - 1;
		while (j >= 0 && i >= 0 && board[j][i] == null) {
			j--;
			i--;
		}
		if (j >= 0 && i >= 0) {
			if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (j == y - 1 && i == x - 1 && (board[j][i] instanceof Pawn || board[j][i] instanceof King)) {
				relatedPieces.add(board[j][i]);
			}
		}
		// search up-right
		j = y - 1;
		i = x + 1;
		while (j >= 0 && i < 8 && board[j][i] == null) {
			j--;
			i++;
		}
		if (j >= 0 && i < 8) {
			if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
				relatedPieces.add(board[j][i]);
			} else if (j == y - 1 && i == x + 1 && (board[j][i] instanceof Pawn || board[j][i] instanceof King)) {
				relatedPieces.add(board[j][i]);
			}
		}

		// check knight moves
		int g = 2;
		int h = 1;
		for (int k = 0; k < 8; k++) {
			if (y + h < 8 && y + h >= 0 && x + g < 8 && x + g >= 0) {
				if (board[y + h][x + g] instanceof Knight) {
					relatedPieces.add(board[y + h][x + g]);
				}
			}
			switch (k) {
			case 0:
				h = h * -1;
				break;
			case 1:
				g = g * -1;
				break;
			case 2:
				h = h * -1;
				break;
			case 3:
				h = h * 2;
				g = g / -2;
				break;
			case 4:
				g = g * -1;
				break;
			case 5:
				h = h * -1;
				break;
			case 6:
				g = g * -1;
				break;
			}
			// 1,2 -1,2 -1,-2 1,-2 2,1 2,-1 -2, -1 -2,1
		}
		return relatedPieces;
	}

	/*
	 * Pops the latest move from the stack and undoes it.
	 * 
	 * @return - The undone move object which can be used by the UI to undo
	 */
	public Move undoLastMove() {
		Move undo = moves.pop();
		Piece moved = undo.getSelectedPiece();
		moved.timesMoved--;
		// check for a Move subclass
		if(undo instanceof Castle) {
			Castle undo_castle = (Castle) undo;
			Rook castled = undo_castle.getCastled();
			// move rook back
			board[undo_castle.castled_yto][undo_castle.castled_xto] = null;
			board[undo_castle.castled_yFrom][undo_castle.castled_xFrom] = castled;
			castled.timesMoved--;
			// move king back
			board[undo_castle.yto][undo_castle.xto] = null;
			board[undo_castle.yfrom][undo_castle.xfrom] = null;
		} else if(undo instanceof Promotion) {
			board[undo.yto][undo.xto] = null;// remove upgraded piece
			board[undo.yfrom][undo.xfrom] = moved;// this is the original pawn
		} else if(undo instanceof EnPassant) {
			
		} 
		// all normal moves
		else {
			// remove the moved piece and replace it with the old piece if it existed
			Piece removed = undo.getRemovedPiece();
			// the destination square gets the removed piece or is cleared if it is null
			board[undo.yto][undo.xto] = removed;
			// replaced the moved piece
			board[undo.yfrom][undo.xfrom] = moved;
		}
		
		// TODO more efficient 
//		// update valid moves
//		ArrayList<Piece> affected = getRelatedPieces(undo.yto, undo.xto);
//		for (Piece regenMoves : affected) {
//			regenMoves.setValidMoves(generateValidMoves(regenMoves));
//		}
//		// search and update pieces affected by the opening at the old position
//		affected = getRelatedPieces(undo.getPieceY(), undo.getPieceX());
//		for (Piece regenMoves : affected) {
//			regenMoves.setValidMoves(generateValidMoves(regenMoves));
//		}
		generateAllMoves();
		return undo;
	}

	/**Places the new piece p on the board and adds it to the list of pieces in play
	 * @param p - The new Piece to place
	 */
	public void placePiece(Piece p) {
		board[p.y][p.x] = p;
		if(p.color == Piece.TAN) {
			tanLocations.add(p);
			if(p instanceof King) {
				if(tanKing != null) {
					throw new RuntimeException("Cannot add two kings to a game!");
				}
				tanKing = (King) p;
			}
		} else {
			whiteLocations.add(p);
			if(p instanceof King) {
				if(whiteKing != null) {
					throw new RuntimeException("Cannot add two kings to a game!");
				}
				whiteKing = (King) p;
			}
		}
	}

	/*
	 * Generates the moves for every piece in play. This method should only be
	 * used on initial setup.
	 */
	public void generateAllMoves() {
		/* Note: Enhanced for loops cannot be used here since the iterator will throw a 
		 * concurrentModificationException since generateValidMoves() calls isBlockingCheck()
		 * which temporarily removes the piece being iterated over - throwing the exception.
		 * Without the iterator, isBlockingCheck() will simply restore the state without issue.*/
		Piece p;
		// tan
		for (int i = 0; i < tanLocations.size(); i++) {
			p = tanLocations.get(i);
			p.setValidMoves(generateValidMoves(p));
		}
		// white
		for (int i = 0; i < whiteLocations.size(); i++) {
			p = whiteLocations.get(i);
			p.setValidMoves(generateValidMoves(p));
		}
	}

	// /*
	// * Ensures the move is legal by checking if the move puts the king in
	// check
	// * @param m - the move - assumed to be valid in its to and from locations
	// * @return - True if it is valid, else false
	// */
	// private boolean validate(Move m) {
	// // first check if this player's king is already in check
	//
	//
	// }

	public boolean hasPiece(int y, int x) {
		if (board[y][x] == null)
			return false;
		return true;
	}

	public Piece getPiece(int y, int x) {
		return board[y][x];
	}
	
	public Piece getPiece(Square s) {
		return board[s.getY()][s.getX()];
	}

	/*
	 * Checks if any pieces of the opposing color are threatening the specified
	 * square. Does this by conducting a search outwards from the square,
	 * checking straight, diagonal, and knight moves.
	 * 
	 * @param color - the of "this side, the other color is threatening
	 * 
	 * @param
	 */
	public boolean isThreatenedSquare(char color, int y, int x) {
		// check straight paths
		// search down
		int j = y + 1;
		int i = x;
		while (j < 8 && board[j][i] == null) {
			j++;
		}
		if (j < 8) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					return true;
				}
				if (board[j][i] instanceof King && j == y + 1) {
					return true;
				}
			}
		}
		// search left
		j = y;
		i = x - 1;
		while (i >= 0 && board[j][i] == null) {
			i--;
		}
		if (i >= 0) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					return true;
				} else if (i == x - 1 && board[j][i] instanceof King) {
					return true;
				}
			}
		}
		// search up
		j = y - 1;
		i = x;
		while (j >= 0 && board[j][i] == null) {
			j--;
		}
		if (j >= 0) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					return true;
				} else if (j == y - 1 && board[j][i] instanceof King) {
					return true;
				}
			}
		}
		// search right
		j = y;
		i = x + 1;
		while (i < 8 && board[j][i] == null) {
			i++;
		}
		if (i < 8) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					return true;
				} else if (i == x + 1 && board[j][i] instanceof King) {
					return true;
				}
			}
		}

		// check diagonal paths
		// search down-right
		j = y + 1;
		i = x + 1;
		while (j < 8 && i < 8 && board[j][i] == null) {
			j++;
			i++;
		}
		if (j < 8 && i < 8) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					return true;
				}
				if (j == y + 1 && i == x + 1) {
					// only tan pawns can threaten from below diagonally
					if (board[j][i] instanceof King || (board[j][i] instanceof Pawn && board[j][i].color == Piece.TAN))
						return true;
				}
			}
		}
		// search down-left
		j = y + 1;
		i = x - 1;
		while (j < 8 && i >= 0 && board[j][i] == null) {
			j++;
			i--;
		}
		if (j < 8 && i >= 0) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					return true;
				}
				if (j == y + 1 && i == x - 1) {
					// only tan pawns can threaten from below diagonally
					if ((board[j][i].color == Piece.TAN && board[j][i] instanceof Pawn) || board[j][i] instanceof King)
						return true;
				}
			}
		}
		// search up-left
		j = y - 1;
		i = x - 1;
		while (j >= 0 && i >= 0 && board[j][i] == null) {
			j--;
			i--;
		}
		if (j >= 0 && i >= 0) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					return true;
				}
				if (j == y - 1 && i == x - 1) {
					// only white pawns can threaten diagonally from above
					if ((board[j][i].color == Piece.WHITE && board[j][i] instanceof Pawn)
							|| board[j][i] instanceof King) {
						return true;
					}
				}
			}
		}
		// search up-right
		j = y - 1;
		i = x + 1;
		while (j >= 0 && i < 8 && board[j][i] == null) {
			j--;
			i++;
		}
		if (j >= 0 && i < 8) {
			if (board[j][i].color != color) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					return true;
				}
				if (j == y - 1 && i == x + 1) {
					if ((board[j][i].color == Piece.WHITE && board[j][i] instanceof Pawn)
							|| board[j][i] instanceof King) {
						return true;
					}
				}
			}
		}

		// check knight moves
		int g = 2;
		int h = 1;
		for (int k = 0; k < 8; k++) {
			if (y + h < 8 && y + h >= 0 && x + g < 8 && x + g >= 0) {
				if (board[y + h][x + g] != null) {
					if (board[y + h][x + g].color != color && board[y + h][x + g] instanceof Knight) {
						return true;
					}
				}
			}
			switch (k) {
			case 0:
				h = h * -1;
				break;
			case 1:
				g = g * -1;
				break;
			case 2:
				h = h * -1;
				break;
			case 3:
				h = h * 2;
				g = g / -2;
				break;
			case 4:
				g = g * -1;
				break;
			case 5:
				h = h * -1;
				break;
			case 6:
				g = g * -1;
				break;
			}
			// 1,2 -1,2 -1,-2 1,-2 2,1 2,-1 -2, -1 -2,1
		}
		return false;
	}

	// used for debugging only - displays board in console
	// prints tan pieces as lower case and white as upper case
	public void displayBoard() {
		System.out.println("+---+---+---+---+---+---+---+---+");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null) {
					if(board[i][j].color == Piece.TAN) System.out.print("| " + board[i][j].type + " ");// lower case
					else System.out.print("| " + (char)(board[i][j].type - 32) + " ");// upper case
				} else System.out.print("|   ");
			}
			System.out.println("|");
			System.out.println("+---+---+---+---+---+---+---+---+");
		}
		System.out.println("\n");
	}
	
	/**Checks if the given king is in check, also sets the respective global vars
	 * @param k - The King in question
	 * @return True if the king is in check
	 */
	public boolean isInCheck(King k) {
		ArrayList<ArrayList<Square>> threatPaths = generateThreatPaths(k);
		// if no threat paths -> not in check
		if (!threatPaths.isEmpty()) {
			if (k.color == Piece.TAN) {
				tanCheck = true;
				tanThreats = findThreats(k, threatPaths);
			} else {
				whiteCheck = true;
				whiteThreats = findThreats(k, threatPaths);
			}
			return true;
		} else {
			// reset if not in check
			if (k.color == Piece.TAN) {
				tanCheck = false;
				tanThreats = null;
			} else {
				whiteCheck = false;
				whiteThreats = null;
			}
			return false;
		}
	}
	
//	/**Locates all the pieces currently threatening this piece
//	 * @param p
//	 * @return
//	 */
//	private ArrayList<Piece> findThreats(Piece p) {
//		ArrayList<Piece> threats = new ArrayList<Piece>();
//		ArrayList<ArrayList<Square>> threatPaths = generateThreatPaths(p);
//		// The first square in every path is the location of a threatening piece
//		for(ArrayList<Square> path : threatPaths) {
//			Square threat_sq = path.get(0);
//			threats.add(getPiece(threat_sq));
//		}
//		return threats;
//	}
	
	/**Locates all the pieces currently threatening this king
	 * @param k - the king
	 * @param threatPaths - a list of threat paths to the king
	 * @return a list of pieces threatening the king
	 */
	private ArrayList<Piece> findThreats(King k, ArrayList<ArrayList<Square>> threatPaths) {
		ArrayList<Piece> threats = new ArrayList<Piece>();
		// The first square in every path is the location of a threatening piece
		for(ArrayList<Square> path : threatPaths) {
			Square threat_sq = path.get(0);
			threats.add(getPiece(threat_sq));
		}
		return threats;
	}

	/*
	 * @return - A list of lists of square indexes. Each list represents an
	 * enemy piece and the path is uses to threaten the king. For example a
	 * bishop might return a diagonal path from the bishop to the king whereas a
	 * knight's threat path only includes the knight's square. If the king
	 * cannot move, for every threat path there must be at least 1 square (or enemy piece captured)
	 * in the list which can be blocked to break the check.
	 */
	public ArrayList<ArrayList<Square>> generateThreatPaths(King king) {
		/*
		 * Check all straight, diagonal, and knight paths. If an enemy piece is
		 * found in one of these threatening positions, iterate across the path
		 * to the king, adding each index of the path to a separate list (1 per
		 * threat).
		 * Note: an enemy king can never directly threaten the king
		 */
		ArrayList<ArrayList<Square>> threatPaths = new ArrayList<ArrayList<Square>>();
		ArrayList<Square> temp = null;
		int y = king.y;
		int x = king.x;
		
		// check straight paths
		// search down
		int j = y + 1;
		int i = x;
		while (j < 8 && board[j][i] == null) {
			j++;
		}
		if (j < 8) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (j > y) {
						temp.add(new Square(j, i));
						j--;
					}
					threatPaths.add(temp);
				}
			}
		}
		// search left
		j = y;
		i = x - 1;
		while (i >= 0 && board[j][i] == null) {
			i--;
		}
		if (i >= 0) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (i < x) {
						temp.add(new Square(j, i));
						i++;
					}
					threatPaths.add(temp);
				}
			}
		}
		// search up
		j = y - 1;
		i = x;
		while (j >= 0 && board[j][i] == null) {
			j--;
		}
		if (j >= 0) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (j < y) {
						temp.add(new Square(j, i));
						j++;
					}
					threatPaths.add(temp);
				}
			}
		}
		// search right
		j = y;
		i = x + 1;
		while (i < 8 && board[j][i] == null) {
			i++;
		}
		if (i < 8) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Rook || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (i > x) {
						temp.add(new Square(j, i));
						i--;
					}
					threatPaths.add(temp);
				}
			}
		}

		// check diagonal paths
		// search down-right
		j = y + 1;
		i = x + 1;
		while (j < 8 && i < 8 && board[j][i] == null) {
			j++;
			i++;
		}
		if (j < 8 && i < 8) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (j > y) {
						temp.add(new Square(j, i));
						i--;
						j--;
					}
					threatPaths.add(temp);
				} else if (j == y + 1 && i == x + 1) {
					// only tan pawns can threaten from below diagonally
					if (board[j][i] instanceof Pawn && board[j][i].color == Piece.TAN) {
						temp = new ArrayList<Square>();
						temp.add(new Square(j, i));
						threatPaths.add(temp);
					}
				}
			}
		}
		// search down-left
		j = y + 1;
		i = x - 1;
		while (j < 8 && i >= 0 && board[j][i] == null) {
			j++;
			i--;
		}
		if (j < 8 && i >= 0) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (j > y) {
						temp.add(new Square(j, i));
						i++;
						j--;
					}
					threatPaths.add(temp);
				} else if (j == y + 1 && i == x - 1) {
					// only tan pawns can threaten from below diagonally
					if (board[j][i].color == Piece.TAN && board[j][i] instanceof Pawn) {
						temp = new ArrayList<Square>();
						temp.add(new Square(j, i));
						threatPaths.add(temp);
					}
				}
			}
		}
		// search up-left
		j = y - 1;
		i = x - 1;
		while (j >= 0 && i >= 0 && board[j][i] == null) {
			j--;
			i--;
		}
		if (j >= 0 && i >= 0) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (j < y) {
						temp.add(new Square(j, i));
						i++;
						j++;
					}
					threatPaths.add(temp);
				} else if (j == y - 1 && i == x - 1) {
					// only white pawns can threaten diagonally from above
					if (board[j][i].color == Piece.WHITE && board[j][i] instanceof Pawn) {
						temp = new ArrayList<Square>();
						temp.add(new Square(j, i));
						threatPaths.add(temp);
					}
				}
			}
		}
		// search up-right
		j = y - 1;
		i = x + 1;
		while (j >= 0 && i < 8 && board[j][i] == null) {
			j--;
			i++;
		}
		if (j >= 0 && i < 8) {
			if (board[j][i].isOpponent(king)) {
				if (board[j][i] instanceof Bishop || board[j][i] instanceof Queen) {
					temp = new ArrayList<Square>();
					while (j < y) {
						temp.add(new Square(j, i));
						i--;
						j++;
					}
					threatPaths.add(temp);
				} else if (j == y - 1 && i == x + 1) {
					if (board[j][i].color == Piece.WHITE && board[j][i] instanceof Pawn) {
						temp = new ArrayList<Square>();
						temp.add(new Square(j, i));
						threatPaths.add(temp);
					}
				}
			}
		}

		// check knight moves
		int g = 2;
		int h = 1;
		for (int k = 0; k < 8; k++) {
			if (y + h < 8 && y + h >= 0 && x + g < 8 && x + g >= 0) {
				if (board[y + h][x + g] != null) {
					if (board[y + h][x + g].isOpponent(king) && board[y + h][x + g] instanceof Knight) {
						temp = new ArrayList<Square>();
						temp.add(new Square(y + h, x + g));
						threatPaths.add(temp);
					}
				}
			}
			switch (k) {
			case 0:
				h = h * -1;
				break;
			case 1:
				g = g * -1;
				break;
			case 2:
				h = h * -1;
				break;
			case 3:
				h = h * 2;
				g = g / -2;
				break;
			case 4:
				g = g * -1;
				break;
			case 5:
				h = h * -1;
				break;
			case 6:
				g = g * -1;
				break;
			}
			// 1,2 -1,2 -1,-2 1,-2 2,1 2,-1 -2, -1 -2,1
		}
		return threatPaths;
	}

	private static final int NOT_BLOCKING_CHECK = 100;
	private static final int BLOCKING_CHECK = 101;
	private static final int CAN_ELIMINATE_CHECK = 102;
	
	/**Assumes this Piece's color is not in check. Checks should also be updated.
	 * @param p - The Piece to check this for 
	 * @return True is the Piece is stopping the king from being in check
	 */
	private int isBlockingCheck(Piece p) {
		int status;
		// check if the king is already in check
		if(p.color == Piece.TAN) {	
			if(tanCheck) {
				throw new RuntimeException("Called isBlockingCheck() when there was already a check.");
			}
		} else {
			if(whiteCheck) {
				throw new RuntimeException("Called isBlockingCheck() when there was already a check.");
			}
		}
		// Remove the piece from the board and test if it puts the king in check
		Piece removed = takeAndRemovePiece(p.y, p.x);
		if(p.color == Piece.TAN) {		
			// check if the piece is capable of capturing the threat, thus removing the check
			if(isInCheck(getTanKing())) {
				// if error here then this method was not called during a check on King k
				if(tanThreats == null) {
					throw new RuntimeException("");
				}
				Piece threat = tanThreats.get(0);
				if(canCapture(removed, threat)) {	
					if(tanThreats.size() == 1) {
						status = CAN_ELIMINATE_CHECK;
					} else {
						status = BLOCKING_CHECK;
					}
				} else {
					status = BLOCKING_CHECK;
				}
			} else {
				status = NOT_BLOCKING_CHECK;
			}
			// restore check state - TODO efficiency
			isInCheck(getWhiteKing());
		}
		// white
		else {
			// check if the piece is capable of capturing the threat, thus removing the check
			if(isInCheck(getWhiteKing())) {
				// if error here then this method was not called during a check on King k
				if(whiteThreats == null) {
					throw new RuntimeException("Called isBlockingCheck() when there was no check.");
				}
				Piece threat = whiteThreats.get(0);
				if(canCapture(removed, threat)) {	
					if(whiteThreats.size() == 1) {
						status = CAN_ELIMINATE_CHECK;
					} else {
						status = BLOCKING_CHECK;
					}
				} else {
					status = BLOCKING_CHECK;
				}
			} else {
				status = NOT_BLOCKING_CHECK;
			}
			// restore check state - TODO efficiency
			isInCheck(getWhiteKing());
		}

		// restore gamestate
		placePiece(removed);
		
		
		return status;
	}

	/**Tests if one Piece can take another - not for use with kings
	 * TODO enpassant
	 * @param taker - The Piece to capture
	 * @param taken - The piece to be captured
	 * @return True if taker can capture taken
	 */
	private boolean canCapture(Piece taker, Piece taken) {
		int i, j;
		if(!taker.isOpponent(taken)) {
			return false;
		}
		int ydif = taker.y - taken.y;
		int xdif = taker.x - taken.x;
		if(xdif == 0 && ydif == 0) {
			System.out.println("Trying to compare the same jacob.siebert.chessai.piece!");
			throw new RuntimeException();
		}
		
		// Straight captures
		if(ydif == 0 || xdif == 0) {
			// en-passant captures
			if(lastDoublePawn != null && lastDoublePawn.equals(taken)) {
				if(ydif == 0 && Math.abs(xdif) == 1) {
					return true;
				}
			}
			
			// rook and queen
			if(!(taker instanceof Rook) && !(taker instanceof Queen)) {
				return false;
			}
			if(ydif == 0 && xdif > 0) {
				i = taker.x - 1;
				while(i > 0 && board[taker.y][i] == null) {
					i--;
				}
				return i == taken.x;
			} else if(ydif == 0 && xdif < 0) {
				i = taker.x + 1;
				while(i < 7 && board[taker.y][i] == null) {
					i++;
				}
				return i == taken.x;
			} else if(xdif == 0 && ydif < 0) {
				i = taker.y + 1;
				while(i < 7 && board[i][taker.x] == null) {
					i++;
				}
				if(i == taken.y) {
					return true;
				}
				return false;
			} else if(xdif == 0 && ydif > 0) {
				i = taker.y - 1;
				while(i > 0 && board[i][taker.x] == null) {
					i--;
				}
				return i == taken.y;
			}
		}
		
		// diagonal captures
		else if(xdif == ydif) {
			// pawn captures
			if(ydif > 0) {
				if(taker.color == Piece.TAN && taker instanceof Pawn) {
					return true;
				}
			} else {// ydif < 0
				if(taker.color == Piece.WHITE && taker instanceof Pawn) {
					return true;
				}
			}
			
			// bishop and queen
			if(!(taker instanceof Queen) && !(taker instanceof Bishop)) {
				return false;
			}
			if(ydif < 0 && xdif < 0) {
				// search down right
				i = taker.x + 1;
				j = taker.y + 1;
				while(i < 7 && j < 7 && board[j][i] == null) {
					j++;
					i++;
				}
				return j == taken.y && i == taken.x;
			}
			if(ydif < 0 && xdif > 0) {
				// search down left
				i = taker.x - 1;
				j = taker.y + 1;
				while(i > 0 && j < 7 && board[j][i] == null) {
					j++;
					i--;
				}
				return j == taken.y && i == taken.x;
			}
			if(ydif > 0 && xdif > 0) {
				// search up left
				i = taker.x - 1;
				j = taker.y - 1;
				while(i > 0 && j > 0 && board[j][i] == null) {
					j--;
					i--;
				}
				return j == taken.y && i == taken.x;
			}
			if(ydif > 0 && xdif > 0) {
				// search up right
				i = taker.x + 1;
				j = taker.y - 1;
				while(i < 7 && j > 0 && board[j][i] == null) {
					j--;
					i++;
				}
				return j == taken.y && i == taken.x;
			}
		}
		
		// knight captures
		if(Math.abs(xdif) + Math.abs(ydif) == 3) {
			return taker instanceof Knight;
		}
		// cannot capture this piece
		return false;
	}
	
	public void updateChecks() {
		isInCheck(getTanKing());
		isInCheck(getWhiteKing());
	}
	
	public ArrayList<Move> generateValidMoves(Piece p) {
		// note: check is handled at the bottom of this method
		if(p == null) {
			throw new NoSuchPieceException("Generating moves for a null jacob.siebert.chessai.piece.");
		}
		ArrayList<Move> validMoves = new ArrayList<Move>();
		
		// test if the piece is blocking a check for its king
		if(!(p instanceof King)) {
			if(p.color == Piece.TAN) {
				if(!tanCheck) {
					int blockingStatus = isBlockingCheck(p);
					// If a piece is blocking a check it cannot move
					if(blockingStatus == BLOCKING_CHECK) {
						return validMoves;// empty
					}
					// ...unless the piece can attack the threatening enemy piece
					if(blockingStatus == CAN_ELIMINATE_CHECK) {
						Piece threat = tanThreats.get(0);
						validMoves.add(new Move(p, threat.y, threat.x));
						return validMoves;// if this piece chooses to move, it must attack the threat
					}
					// if NOT_BLOCKING_CHECK --> continue
				}
			}
			// white
			else {
				if(!whiteCheck) {
					int blockingStatus = isBlockingCheck(p);
					// If a piece is blocking a check it cannot move
					if(blockingStatus == BLOCKING_CHECK) {
						return validMoves;// empty
					}
					// ...unless the piece can attack the threatening enemy piece
					if(blockingStatus == CAN_ELIMINATE_CHECK) {
						Piece threat = whiteThreats.get(0);
						validMoves.add(new Move(p, threat.y, threat.x));
						return validMoves;// if this piece chooses to move, it must attack the threat
					}
					// if NOT_BLOCKING_CHECK --> continue
				}
			}
		}
		
		/* Generate the standard moves for this type of piece.
		 * If the king is in check, all moves which do not eliminate the check
		 * will be invalid. Special consideration taken into account for the king.
		 */
		
		// pawn
		if (p instanceof Pawn) {
			// player1's pawn must move up
			Pawn pawn = (Pawn) p;
			if (pawn.color == Piece.TAN) {
				// Note: Do not add pawn moves that move the pawn into the last
				// file - upgrades handle this
				if (pawn.y - 1 > 1) {
					if (board[pawn.y - 1][pawn.x] == null) {
						validMoves.add(new Move(p, pawn.y - 1, pawn.x));
						if (pawn.hasMoved()) {
							if (pawn.y - 2 >= 0) {
								if (board[p.y - 2][p.x] == null) {
									validMoves.add(new Move(p, pawn.y - 2, pawn.x));
								}
							}
						}
					}
					if (pawn.x + 1 < 8) {
						if (board[pawn.y - 1][pawn.x + 1] != null) {
							if (pawn.isOpponent(board[pawn.y - 1][pawn.x + 1]))
								validMoves.add(new Move(p, pawn.y - 1, pawn.x + 1, board[pawn.y - 1][pawn.x + 1]));
						}
					}
					if (pawn.x - 1 >= 0) {
						if (board[pawn.y - 1][pawn.x - 1] != null) {
							if (pawn.isOpponent(board[pawn.y - 1][pawn.x - 1]))
								validMoves.add(new Move(p, pawn.y - 1, pawn.x - 1, board[pawn.y - 1][pawn.x - 1]));
						}
					}
				}
				// En-Passant
				// a tan pawn must be in the 5th (3rd in array) row
				// the captured pawn must have just moved 2 squares forward
				if (lastDoublePawn != null) {
					if (pawn.y == 3) {
						if (pawn.x - 1 == lastDoublePawn.x || pawn.x + 1 == lastDoublePawn.x) {
							validMoves.add(new EnPassant(pawn, lastDoublePawn.y - 1, lastDoublePawn.x,
									lastDoublePawn));
						}
					}
				}
				// Upgrades
				if (pawn.y == 1) {
					// spawn new pieces for the upgrade
					if (board[0][pawn.x] == null) {
						validMoves.add(new Promotion(pawn, 0, pawn.x, new Queen(pawn.color, 0, pawn.x)));
						validMoves.add(new Promotion(pawn, 0, pawn.x, new Bishop(pawn.color, 0, pawn.x)));
						validMoves.add(new Promotion(pawn, 0, pawn.x, new Knight(pawn.color, 0, pawn.x)));
						validMoves.add(new Promotion(pawn, 0, pawn.x, new Rook(pawn.color, 0, pawn.x)));
					}
					// Capture and Promotion - store removed piece in the move here
					if (pawn.x > 0 && board[0][pawn.x - 1] != null && board[0][pawn.x - 1].isOpponent(pawn)) {
						validMoves.add(new Promotion(pawn, 0, pawn.x - 1, new Queen(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
						validMoves.add(new Promotion(pawn, 0, pawn.x - 1, new Bishop(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
						validMoves.add(new Promotion(pawn, 0, pawn.x - 1, new Knight(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
						validMoves.add(new Promotion(pawn, 0, pawn.x - 1, new Rook(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
					}
					if (pawn.x < 7 && board[0][pawn.x + 1] != null && board[0][pawn.x + 1].isOpponent(pawn)) {
						validMoves.add(new Promotion(pawn, 0, pawn.x + 1, new Queen(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
						validMoves.add(new Promotion(pawn, 0, pawn.x + 1, new Bishop(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
						validMoves.add(new Promotion(pawn, 0, pawn.x + 1, new Knight(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
						validMoves.add(new Promotion(pawn, 0, pawn.x + 1, new Rook(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
					}
				}
			}

			// white pawns
			// player2's pawns must move down
			else {
				if (pawn.y + 1 < 6) {
					if (board[pawn.y + 1][pawn.x] == null) {
						validMoves.add(new Move(p, pawn.y + 1, pawn.x));
						if (pawn.hasMoved()) {
							if (pawn.y + 2 < 8) {
								if (board[pawn.y + 2][pawn.x] == null) {
									validMoves.add(new Move(p, pawn.y + 2, pawn.x));
								}
							}
						}
					}
					if (pawn.x + 1 < 8) {
						if (board[pawn.y + 1][pawn.x + 1] != null) {
							if (pawn.isOpponent(board[pawn.y + 1][pawn.x + 1]))
								validMoves.add(new Move(p, pawn.y + 1, pawn.x + 1, board[pawn.y + 1][pawn.x + 1]));
						}
					}
					if (pawn.x - 1 >= 0) {
						if (board[pawn.y + 1][pawn.x - 1] != null) {
							if (pawn.isOpponent(board[pawn.y + 1][pawn.x - 1]))
								validMoves.add(new Move(p, pawn.y + 1, pawn.x - 1, board[pawn.y + 1][pawn.x - 1]));
						}
					}
				}
				// en-passants
				if (lastDoublePawn != null) {
					if (pawn.y == 4) {
						if (pawn.x - 1 == lastDoublePawn.x || pawn.x + 1 == lastDoublePawn.x) {
							validMoves.add(new EnPassant(pawn, lastDoublePawn.y + 1, lastDoublePawn.x,
									lastDoublePawn));
						}
					}
				}
				// Upgrades
				if (pawn.y == 6) {
					if (board[7][pawn.x] == null) {
						validMoves.add(new Promotion(pawn, 7, pawn.x, new Queen(pawn.color, 7, pawn.x)));
						validMoves.add(new Promotion(pawn, 7, pawn.x, new Bishop(pawn.color, 7, pawn.x)));
						validMoves.add(new Promotion(pawn, 7, pawn.x, new Knight(pawn.color, 7, pawn.x)));
						validMoves.add(new Promotion(pawn, 7, pawn.x, new Rook(pawn.color, 7, pawn.x)));
					}
					if (pawn.x > 0 && board[7][pawn.x - 1] != null && board[7][pawn.x - 1].isOpponent(pawn)) {
						validMoves.add(new Promotion(pawn, 7, pawn.x - 1, new Queen(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
						validMoves.add(new Promotion(pawn, 7, pawn.x - 1, new Bishop(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
						validMoves.add(new Promotion(pawn, 7, pawn.x - 1, new Knight(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
						validMoves.add(new Promotion(pawn, 7, pawn.x - 1, new Rook(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
					}
					if (pawn.x < 7 && board[7][pawn.x + 1] != null && board[7][pawn.x + 1].isOpponent(pawn)) {
						validMoves.add(new Promotion(pawn, 7, pawn.x + 1, new Queen(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
						validMoves.add(new Promotion(pawn, 7, pawn.x + 1, new Bishop(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
						validMoves.add(new Promotion(pawn, 7, pawn.x + 1, new Knight(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
						validMoves.add(new Promotion(pawn, 7, pawn.x + 1, new Rook(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
					}
				}
			}
		} else if (p instanceof Knight) {
			int i = 2;
			int j = 1;
			for (int k = 0; k < 8; k++) {
				if (p.y + j < 8 && p.y + j >= 0 && p.x + i < 8 && p.x + i >= 0) {
					if (board[p.y + j][p.x + i] == null) {
						validMoves.add(new Move(p, p.y + j, p.x + i));
					} else if (p.isOpponent(board[p.y + j][p.x + i])) {
						validMoves.add(new Move(p, p.y + j, p.x + i, board[p.y + j][p.x + i]));
					}
				}
				switch (k) {
				case 0:
					j = j * -1;
					break;
				case 1:
					i = i * -1;
					break;
				case 2:
					j = j * -1;
					break;
				case 3:
					j = j * 2;
					i = i / -2;
					break;
				case 4:
					i = i * -1;
					break;
				case 5:
					j = j * -1;
					break;
				case 6:
					i = i * -1;
					break;
				}
				// 1,2 -1,2 -1,-2 1,-2 2,1 2,-1 -2, -1 -2,1
			}
		} else if (p instanceof Bishop) {
			validMoves = getDiagonalMoves(p);
		} else if (p instanceof Rook) {
			validMoves = getStraightMoves(p);
		} else if (p instanceof Queen) {
			validMoves = getDiagonalMoves(p);
			validMoves.addAll(getStraightMoves(p));
		} else if (p instanceof King) {
			King kTemp = (King) p;
			// check if the king is in check
			boolean check = isInCheck(kTemp);

			/* Check if this king is able to Castle: 
			 1. King Unmoved
			 2. King not in check
			 3. Rook unmoved
			 4. Clear path between king and rook
			 5. No square that the king must cross is threatened.*/
			if (kTemp.hasMoved() && !check) {
				int i = kTemp.x - 1;
				// check if the Queen's side castle is open
				while (i >= 0 && board[kTemp.y][i] == null) {
					// only check for threats on squares where the king crosses or ends up
					if(i >= 2) {
						if(isThreatenedSquare(kTemp.color, kTemp.y, i)) {
							break;
						}
					}
					i--;
				}
				if (board[kTemp.y][i] != null) {
					if (board[kTemp.y][i] instanceof Rook) {
						Rook rook = ((Rook) board[kTemp.y][i]);
						if(rook.hasMoved()) {
							validMoves.add(new Castle(kTemp, kTemp.y, kTemp.x - 2, rook, rook.y, rook.x + 3));
						}
					}
				}
				
				// check if the King's Side castle is open
				i = kTemp.x + 1;
				while (i < 8 && board[kTemp.y][i] == null) {
					// only check for threats on squares where the king crosses or ends up
					if(i < 7) {
						if(isThreatenedSquare(kTemp.color, kTemp.y, i)) {
							break;
						}
					}
					i++;
				}
				if (board[kTemp.y][i] != null) {
					// if the rook is in the original square
					if (board[kTemp.y][i] instanceof Rook) {
						Rook rook = ((Rook) board[kTemp.y][i]);
						if (rook.hasMoved()) {
							validMoves.add(new Castle(kTemp, kTemp.y, kTemp.x + 2, rook, rook.y, rook.x - 2));
						}
					}
				}
			}

			// add normal king moves
			if (p.y + 1 < 8) {
				if (!isThreatenedSquare(p.color, p.y + 1, p.x)) {
					if (board[p.y + 1][p.x] == null) {
						validMoves.add(new Move(p, p.y + 1, p.x));
					} else if (p.isOpponent(board[p.y + 1][p.x])) {
						validMoves.add(new Move(p, p.y + 1, p.x, board[p.y + 1][p.x]));
					}
				}
				if (p.x + 1 < 8) {
					if (!isThreatenedSquare(p.color, p.y + 1, p.x + 1)) {
						if (board[p.y + 1][p.x + 1] == null) {
							validMoves.add(new Move(p, p.y + 1, p.x + 1));
						} else if (p.isOpponent(board[p.y + 1][p.x + 1])) {
							validMoves.add(new Move(p, p.y + 1, p.x + 1, board[p.y + 1][p.x + 1]));
						}
					}
				}
				if (p.x - 1 >= 0) {
					if (!isThreatenedSquare(p.color, p.y + 1, p.x - 1)) {
						if (board[p.y + 1][p.x - 1] == null) {
							validMoves.add(new Move(p, p.y + 1, p.x - 1));
						} else if (p.isOpponent(board[p.y + 1][p.x - 1])) {
							validMoves.add(new Move(p, p.y + 1, p.x - 1, board[p.y + 1][p.x - 1]));
						}
					}
				}
			}
			if (p.y - 1 >= 0) {
				if (!isThreatenedSquare(p.color, p.y - 1, p.x)) {
					if (board[p.y - 1][p.x] == null) {
						validMoves.add(new Move(p, p.y - 1, p.x));
					} else if (p.isOpponent(board[p.y - 1][p.x])) {
						validMoves.add(new Move(p, p.y - 1, p.x, board[p.y - 1][p.x]));
					}
				}
				if (p.x + 1 < 8) {
					if (!isThreatenedSquare(p.color, p.y - 1, p.x + 1)) {
						if (board[p.y - 1][p.x + 1] == null) {
							validMoves.add(new Move(p, p.y - 1, p.x + 1));
						} else if (p.isOpponent(board[p.y - 1][p.x + 1])) {
							validMoves.add(new Move(p, p.y - 1, p.x + 1, board[p.y - 1][p.x + 1]));
						}
					}
				}
				if (p.x - 1 >= 0) {
					if (!isThreatenedSquare(p.color, p.y - 1, p.x - 1)) {
						if (board[p.y - 1][p.x - 1] == null) {
							validMoves.add(new Move(p, p.y - 1, p.x - 1));
						} else if (p.isOpponent(board[p.y - 1][p.x - 1])) {
							validMoves.add(new Move(p, p.y - 1, p.x - 1, board[p.y - 1][p.x - 1]));
						}
					}
				}
			}
			if (p.x - 1 >= 0) {
				if (!isThreatenedSquare(p.color, p.y, p.x - 1)) {
					if (board[p.y][p.x - 1] == null) {
						validMoves.add(new Move(p, p.y, p.x - 1));
					} else if (p.isOpponent(board[p.y][p.x - 1])) {
						validMoves.add(new Move(p, p.y, p.x - 1, board[p.y][p.x - 1]));
					}
				}
			}
			if (p.x + 1 < 8) {
				if (!isThreatenedSquare(p.color, p.y, p.x + 1)) {
					if (board[p.y][p.x + 1] == null) {
						validMoves.add(new Move(p, p.y, p.x + 1));
					} else if (p.isOpponent(board[p.y][p.x + 1])) {
						validMoves.add(new Move(p, p.y, p.x + 1, board[p.y][p.x + 1]));
					}
				}
			}
			// check if the king is in checkmate
			// ckeckmate occurs when the kings position is threatened, the king
			// cannot move to an unthreatened square, and no blocks can be setup

		}

		// If your king is in check - must get out
		if (!(p instanceof King)) {
			/* If this color's king is in check, must get it out of check
			 * or else return no valid moves (checkmate).
			 * Search through all valid moves generated thus far and remove 
			 * any that do not break the check. */
			if (p.color == Piece.TAN) {
				if (tanCheck) {
					ArrayList<Move> getOutOfCheckMoves = new ArrayList<Move>();
					ArrayList<ArrayList<Square>> threatPaths = generateThreatPaths(getTanKing());
					// for every one of this pieces valid moves
					for (Move m : validMoves) {
						// Is the move able to block or destroy *!every!* single threat path to
						// the king?
						boolean haltsThreat = false;
						for (ArrayList<Square> path : threatPaths) {
							haltsThreat = false;
							// Does this move capture the threatening piece?
							// The first square in the threat path is where the threatening piece is located.
							// If not, check if the move can block the threat path.
							for (Square s : path) {
								if (s.equals(m.yto, m.xto)) {
									haltsThreat = true;
									break;
								}
							}

							// if it can't stop the threat, do not add this move
							if (!haltsThreat) {
								break;
							}
						}
						if (haltsThreat) {
//							System.out.println("Move added to get out of check.");
							getOutOfCheckMoves.add(m);
						}
					}
					return getOutOfCheckMoves;
				}
			} else {
				if (whiteCheck) {
					ArrayList<Move> getOutOfCheckMoves = new ArrayList<Move>();
					ArrayList<ArrayList<Square>> threatPaths = generateThreatPaths(getWhiteKing());
					for (Move m : validMoves) {
						boolean haltsThreat = false;
						for (ArrayList<Square> path : threatPaths) {
							haltsThreat = false;
							for (Square s : path) {
								if (s.equals(m.yto, m.xto)) {
									haltsThreat = true;
									break;
								}
							}
							// if it can't stop the threat, do not add this move
							if (!haltsThreat) {
								break;
							}
						}
						if (haltsThreat) {
							getOutOfCheckMoves.add(m);
						}
					}
					return getOutOfCheckMoves;
				}
			}
		}
		return validMoves;
	}

	private ArrayList<Move> getDiagonalMoves(Piece p) {
		int y = p.y;
		int x = p.x;
		ArrayList<Move> diaMoves = new ArrayList<Move>();
		// search down-right
		int j = y + 1;
		int i = x + 1;
		while (j < 8 && i < 8 && board[j][i] == null) {
			diaMoves.add(new Move(p, j, i));
			j++;
			i++;
		}
		if (j < 8 && i < 8) {
			if (p.isOpponent(board[j][i])) {
				diaMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		// search down-left
		j = y + 1;
		i = x - 1;
		while (j < 8 && i >= 0 && board[j][i] == null) {
			diaMoves.add(new Move(p, j, i));
			j++;
			i--;
		}
		if (j < 8 && i >= 0) {
			if (p.isOpponent(board[j][i])) {
				diaMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		// search up-left
		j = y - 1;
		i = x - 1;
		while (j >= 0 && i >= 0 && board[j][i] == null) {
			diaMoves.add(new Move(p, j, i));
			j--;
			i--;
		}
		if (j >= 0 && i >= 0) {
			if (p.isOpponent(board[j][i])) {
				diaMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		// search up-right
		j = y - 1;
		i = x + 1;
		while (j >= 0 && i < 8 && board[j][i] == null) {
			diaMoves.add(new Move(p, j, i));
			j--;
			i++;
		}
		if (j >= 0 && i < 8) {
			if (p.isOpponent(board[j][i])) {
				diaMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		return diaMoves;
	}

	private ArrayList<Move> getStraightMoves(Piece p) {
		int y = p.y;
		int x = p.x;
		ArrayList<Move> strMoves = new ArrayList<Move>();
		// search down
		int j = y + 1;
		int i = x;
		while (j < 8 && board[j][i] == null) {
			strMoves.add(new Move(p, j, i));
			j++;
		}
		if (j < 8) {
			if (p.isOpponent(board[j][i])) {
				strMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		// search left
		j = y;
		i = x - 1;
		while (i >= 0 && board[j][i] == null) {
			strMoves.add(new Move(p, j, i));
			i--;
		}
		if (i >= 0) {
			if (p.isOpponent(board[j][i])) {
				strMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		// search up
		j = y - 1;
		i = x;
		while (j >= 0 && board[j][i] == null) {
			strMoves.add(new Move(p, j, i));
			j--;
		}
		if (j >= 0) {
			if (p.isOpponent(board[j][i])) {
				strMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		// search right
		j = y;
		i = x + 1;
		while (i < 8 && board[j][i] == null) {
			strMoves.add(new Move(p, j, i));
			i++;
		}
		if (i < 8) {
			if (p.isOpponent(board[j][i])) {
				strMoves.add(new Move(p, j, i, board[j][i]));
			}
		}
		return strMoves;
	}

	// TODO remove all these methods and replace all instances with placePiece()
	// returns true on success (no other piece at that location already)
	public boolean setPiece(Bishop p) {
		if (board[p.y][p.x] == null) {
			board[p.y][p.x] = p;
			return true;
		}
		return false;
	}

	public boolean setPiece(Knight p) {
		if (board[p.y][p.x] == null) {
			board[p.y][p.x] = p;
			return true;
		}
		return false;
	}

	public boolean setPiece(Rook p) {
		if (board[p.y][p.x] == null) {
			board[p.y][p.x] = p;
			return true;
		}
		return false;
	}

	public boolean setPiece(Queen p) {
		if (board[p.y][p.x] == null) {
			board[p.y][p.x] = p;
			return true;
		}
		return false;
	}

	public boolean setPiece(King p) {
		if (board[p.y][p.x] == null) {
			board[p.y][p.x] = p;
			return true;
		}
		return false;
	}

	public boolean setPiece(Pawn p) {
		if (board[p.y][p.x] == null) {
			board[p.y][p.x] = p;
			return true;
		}
		return false;
	}

	// // Returns true if the king of the specified color is in check
	// public boolean check(char color) {
	// Piece king;
	// if(color == Piece.TAN) {
	// // recall that the first index in the locations list is always the king
	// king = tanLocations.get(0);
	// }
	// // white
	// else {
	// king = whiteLocations.get(0);
	// }
	// ArrayList<Move> validKingMoves = getValidMoves(king, color);
	//
	// }
	//
	// // Returns true if the king of the specified color is in checkmate
	// public boolean checkMate(char color) {
	// if(color == Piece.TAN) {
	//
	// }
	// // white
	// else {
	//
	// }
	// }

}
