package main;

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
	// the king is always the first piece in the list
	private ArrayList<Piece> whiteLocations;
	private ArrayList<Piece> tanLocations;
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
		// List order:
		// King
		// Pawns
		// Else
		// set up pieces
		if (NEW_GAME == boardType) {
			board[0][3] = new King(Piece.WHITE, 0, 3);
			whiteLocations.add(board[0][3]);
			board[7][4] = new King(Piece.TAN, 7, 3);
			tanLocations.add(board[7][4]);

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
			board[7][3] = new Queen(Piece.TAN, 7, 4);
			tanLocations.add(board[7][3]);
			board[0][4] = new Queen(Piece.WHITE, 0, 4);
			whiteLocations.add(board[0][4]);
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
		generateAllMoves();
	}

	// both parameters must have the king listed first
	// used for cloning only
	private Board(ArrayList<Piece> tans, ArrayList<Piece> whites) {
		tanLocations = new ArrayList<Piece>();
		whiteLocations = new ArrayList<Piece>();
		board = new Piece[8][8];

		for (Piece p : tans) {
			Piece newP = p.clone();
			tanLocations.add(newP);
			board[newP.y][newP.x] = newP;
		}
		for (Piece p : whites) {
			Piece newP = p.clone();
			whiteLocations.add(newP);
			board[newP.y][newP.x] = newP;
		}
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
		return (King) whiteLocations.get(0);
	}

	// Note: King mist always be the first piece in the locations list
	public King getTanKing() {
		return (King) tanLocations.get(0);
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

	// applies the move and returns the piece that was removed as a result, else
	// null
	public Piece applyMove(Move m) {
		// remove the piece and return it so it can be saved for undo operations
		// clone it too to preserve piece location for undoing
		Piece moved = m.getSelectedPiece().clone();
		Piece removed = board[m.yto][m.xto];
		board[m.getPieceY()][m.getPieceX()] = null;

		board[m.yto][m.xto] = moved;
		// update the piece's x and y
		board[m.yto][m.xto].setYX(m.yto, m.xto);

		// clear any potential en passants - only 1 chance to make this move
		lastDoublePawn = null;

		// if this was a double forward pawn move, record it for potential
		// enpassants
		if (moved instanceof Pawn) {
			// label this piece as having been moved
			((Pawn) moved).setAsMoved();
			if (Math.abs(m.yto - m.getPieceY()) > 1) {
				lastDoublePawn = ((Pawn) moved).clone();
			}
		}

		// label the moved piece as having been moved for Rooks and Kings as
		// well (needed for castling)
		if (moved instanceof King) {
			((King) moved).setAsMoved();
		}
		if (moved instanceof Rook) {
			((Rook) moved).setAsMoved();
		}

		// check for en passant move
		if (m instanceof EnPassant) {
			removed = m.getRemovedPiece();
			board[removed.y][removed.x] = null;
		}

		// if a piece was removed by this move the piece must be removed from
		// the locations list
		if (removed != null) {
			m.setRemovedPiece(removed);// store the removed Piece in the move
			if (removed.color == Piece.TAN) {
				int i = 0;
				for (Piece o : tanLocations) {
					if (removed.equals(o)) {
						tanLocations.remove(i);
						break;
					}
					i++;
				}
			} else {
				int i = 0;
				for (Piece o : whiteLocations) {
					if (removed.equals(o)) {
						whiteLocations.remove(i);
						break;
					}
					i++;
				}
			}
		}
		// push the move onto the moves stack
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
	// Efficiency upgrade - change this algo to only include relevant pieces
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
		// remove the moved piece and replace it with the old piece if it
		// existed
		try {
			board[undo.yto][undo.xto] = undo.getRemovedPiece();
		} catch (NoSuchPieceException e) {
			board[undo.yto][undo.xto] = null;
		}
		// replaced the moved piece
		board[undo.getPieceY()][undo.getPieceX()] = undo.getSelectedPiece();

		// update valid moves
		ArrayList<Piece> affected = getRelatedPieces(undo.yto, undo.xto);
		for (Piece regenMoves : affected) {
			regenMoves.setValidMoves(generateValidMoves(regenMoves));
		}
		// search and update pieces affected by the opening at the old position
		affected = getRelatedPieces(undo.getPieceY(), undo.getPieceX());
		for (Piece regenMoves : affected) {
			regenMoves.setValidMoves(generateValidMoves(regenMoves));
		}
		return undo;
	}

	public void placePiece(Piece p) {
		board[p.y][p.x] = p;
	}

	/*
	 * Generates the moves for every piece in play. This method should only be
	 * used on initial setup.
	 */
	public void generateAllMoves() {
		// tan
		for (Piece p : tanLocations) {
			p.setValidMoves(generateValidMoves(p));
		}
		// white
		for (Piece p : whiteLocations) {
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
	public void displayBoard() {
		System.out.println("+---+---+---+---+---+---+---+---+");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null)
					System.out.print("| " + board[i][j].type + " ");
				else
					System.out.print("|   ");
			}
			System.out.println("|");
			System.out.println("+---+---+---+---+---+---+---+---+");
		}
		System.out.println("\n");
	}

	/*
	 * 
	 * @return - A list of lists of square indexes. Each list represents an
	 * enemy piece and the path is uses to threaten the king. For example a
	 * bishop might return a diagonal path from the bishop to the king whereas a
	 * knight's threat path only includes the knight's square. If the king
	 * cannot move, for every threat path there must be at least 1 square in the
	 * list which can be blocked to break the mate.
	 */
	public ArrayList<ArrayList<Square>> generateThreatPaths(King king) {
		/*
		 * Check all straight, diagonal, and knight paths. If an enemy piece is
		 * found in one of these threatening positions, iterate across the path
		 * to the king, adding each index of the path to a separate list (1 per
		 * threat)
		 */
		ArrayList<ArrayList<Square>> threatPaths = new ArrayList<ArrayList<Square>>();
		ArrayList<Square> temp = null;
		int y = king.y;
		int x = king.x;
		char color = king.color;
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
					temp = new ArrayList<Square>();
					while (j > y) {
						temp.add(new Square(j, i));
						j--;
					}
					threatPaths.add(temp);
				}
				// Note: an enemy king can never directly threaten the king
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
			if (board[j][i].color != color) {
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
			if (board[j][i].color != color) {
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
			if (board[j][i].color != color) {
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
			if (board[j][i].color != color) {
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
			if (board[j][i].color != color) {
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
			if (board[j][i].color != color) {
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
					if (board[y + h][x + g].color != color && board[y + h][x + g] instanceof Knight) {
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

	// TODO take away first move from pawns
	// TODO: when is the king put into check - how to efficiently do this??
	// TODO get rid of try catch when able
	// is there a better way to generate valid moves? lots of if statements make
	// this slow
	public ArrayList<Move> generateValidMoves(Piece p) {
		ArrayList<Move> validMoves = new ArrayList<Move>();
		// note: check is handled at the bottom of this method
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
						if (pawn.firstMove()) {
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
								validMoves.add(new Move(p, pawn.y - 1, pawn.x + 1));
						}
					}
					if (pawn.x - 1 >= 0) {
						if (board[pawn.y - 1][pawn.x - 1] != null) {
							if (pawn.isOpponent(board[pawn.y - 1][pawn.x - 1]))
								validMoves.add(new Move(p, pawn.y - 1, pawn.x - 1));
						}
					}
				}
				// En-Passant
				// a tan pawn must be in the 5th (3rd in array) row
				// the captured pawn must have just moved 2 squares forward
				if (lastDoublePawn != null) {
					if (pawn.y == 3) {
						if (pawn.x - 1 == lastDoublePawn.x || pawn.x + 1 == lastDoublePawn.x) {
							validMoves.add(new EnPassant(pawn.clone(), lastDoublePawn.y - 1, lastDoublePawn.x,
									lastDoublePawn.clone()));
						}
					}
				}
				// Upgrades
				if (pawn.y == 1) {
					if (board[0][pawn.x] == null) {
						validMoves.add(new Upgrade(pawn, 0, pawn.x, new Queen(pawn.color, 0, pawn.x)));
						validMoves.add(new Upgrade(pawn, 0, pawn.x, new Bishop(pawn.color, 0, pawn.x)));
						validMoves.add(new Upgrade(pawn, 0, pawn.x, new Knight(pawn.color, 0, pawn.x)));
						validMoves.add(new Upgrade(pawn, 0, pawn.x, new Rook(pawn.color, 0, pawn.x, false)));
					}
					if (pawn.x > 0 && board[0][pawn.x - 1] != null && board[0][pawn.x - 1].color != pawn.color) {
						validMoves.add(new Upgrade(pawn, 0, pawn.x - 1, new Queen(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
						validMoves.add(new Upgrade(pawn, 0, pawn.x - 1, new Bishop(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
						validMoves.add(new Upgrade(pawn, 0, pawn.x - 1, new Knight(pawn.color, 0, pawn.x - 1),
								board[0][pawn.x - 1]));
						validMoves.add(new Upgrade(pawn, 0, pawn.x - 1, new Rook(pawn.color, 0, pawn.x - 1, false),
								board[0][pawn.x - 1]));
					}
					if (pawn.x < 7 && board[0][pawn.x + 1] != null && board[0][pawn.x + 1].color != pawn.color) {
						validMoves.add(new Upgrade(pawn, 0, pawn.x + 1, new Queen(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
						validMoves.add(new Upgrade(pawn, 0, pawn.x + 1, new Bishop(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
						validMoves.add(new Upgrade(pawn, 0, pawn.x + 1, new Knight(pawn.color, 0, pawn.x + 1),
								board[0][pawn.x + 1]));
						validMoves.add(new Upgrade(pawn, 0, pawn.x + 1, new Rook(pawn.color, 0, pawn.x + 1, false),
								board[0][pawn.x + 1]));
					}
				}
			}

			// white
			// player2's pawns must move down
			else {
				if (pawn.y + 1 < 6) {
					if (board[pawn.y + 1][pawn.x] == null) {
						validMoves.add(new Move(p, pawn.y + 1, pawn.x));
						if (pawn.firstMove()) {
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
								validMoves.add(new Move(p, pawn.y + 1, pawn.x + 1));
						}
					}
					if (pawn.x - 1 >= 0) {
						if (board[pawn.y + 1][pawn.x - 1] != null) {
							if (pawn.isOpponent(board[pawn.y + 1][pawn.x - 1]))
								validMoves.add(new Move(p, pawn.y + 1, pawn.x - 1));
						}
					}
				}
				// en-passants
				if (lastDoublePawn != null) {
					if (pawn.y == 4) {
						if (pawn.x - 1 == lastDoublePawn.x || pawn.x + 1 == lastDoublePawn.x) {
							validMoves.add(new EnPassant(pawn.clone(), lastDoublePawn.y + 1, lastDoublePawn.x,
									lastDoublePawn.clone()));
						}
					}
				}
				// Upgrades
				if (pawn.y == 6) {
					if (board[7][pawn.x] == null) {
						validMoves.add(new Upgrade(pawn, 7, pawn.x, new Queen(pawn.color, 7, pawn.x)));
						validMoves.add(new Upgrade(pawn, 7, pawn.x, new Bishop(pawn.color, 7, pawn.x)));
						validMoves.add(new Upgrade(pawn, 7, pawn.x, new Knight(pawn.color, 7, pawn.x)));
						validMoves.add(new Upgrade(pawn, 7, pawn.x, new Rook(pawn.color, 7, pawn.x, false)));
					}
					if (pawn.x > 0 && board[7][pawn.x - 1] != null && board[7][pawn.x - 1].color != pawn.color) {
						validMoves.add(new Upgrade(pawn, 7, pawn.x - 1, new Queen(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
						validMoves.add(new Upgrade(pawn, 7, pawn.x - 1, new Bishop(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
						validMoves.add(new Upgrade(pawn, 7, pawn.x - 1, new Knight(pawn.color, 7, pawn.x - 1),
								board[7][pawn.x - 1]));
						validMoves.add(new Upgrade(pawn, 7, pawn.x - 1, new Rook(pawn.color, 7, pawn.x - 1, false),
								board[7][pawn.x - 1]));
					}
					if (pawn.x < 7 && board[7][pawn.x + 1] != null && board[7][pawn.x + 1].color != pawn.color) {
						validMoves.add(new Upgrade(pawn, 7, pawn.x + 1, new Queen(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
						validMoves.add(new Upgrade(pawn, 7, pawn.x + 1, new Bishop(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
						validMoves.add(new Upgrade(pawn, 7, pawn.x + 1, new Knight(pawn.color, 7, pawn.x + 1),
								board[7][pawn.x + 1]));
						validMoves.add(new Upgrade(pawn, 7, pawn.x + 1, new Rook(pawn.color, 7, pawn.x + 1, false),
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
						validMoves.add(new Move(p, p.y + j, p.x + i));
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
			// check if the king is in check (its square is threatened)
			if (isThreatenedSquare(p.color, p.y, p.x)) {
				// This forces the king to move or a path blocked - implemented
				// in _______?
				if (p.color == Piece.TAN) {
					tanCheck = true;
				} else {
					whiteCheck = true;
				}
			} else {
				// reset if not in check
				if (p.color == Piece.TAN) {
					tanCheck = false;
				} else {
					whiteCheck = false;
				}
			}

			// check if this king is able to castle
			// King must be: Unmoved, not in check, rook is in original
			// square,
			// clear path to rook, no square in path is threatened.
			if (kTemp.firstMove()) {
				int i = kTemp.x - 1;
				// check if the left castle is open
				while (i >= 0 && board[kTemp.y][i] == null) {
					i--;
				}
				if (board[kTemp.y][i] != null) {
					// if the rook is in the original square
					// TODO how does chess work? can you move a rook and
					// still castle with it??
					if (i == 0) {
						Piece rook = board[kTemp.y][i].clone();
						if (board[kTemp.y][i] instanceof Rook) {
							// is this move correct?
							if (rook == null) {
								System.out.println("THis is null??");
							}
							validMoves.add(new Castle(kTemp, kTemp.y, kTemp.x - 3, (Rook) rook));
						}

					}
				}
				// check if the right castle is open
				i = kTemp.x + 1;
				// check if the left castle is open
				while (i < 8 && board[kTemp.y][i] == null) {
					i++;
				}
				if (board[kTemp.y][i] != null) {
					// if the rook is in the original square
					if (i == 7) {
						Piece rook = board[kTemp.y][i].clone();
						if (rook instanceof Rook) {
							// is this move correct?
							validMoves.add(new Castle(kTemp, kTemp.y, kTemp.x + 2, (Rook) rook));
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
						validMoves.add(new Move(p, p.y + 1, p.x));
					}
				}
				if (p.x + 1 < 8) {
					if (!isThreatenedSquare(p.color, p.y + 1, p.x + 1)) {
						if (board[p.y + 1][p.x + 1] == null) {
							validMoves.add(new Move(p, p.y + 1, p.x + 1));
						} else if (p.isOpponent(board[p.y + 1][p.x + 1])) {
							validMoves.add(new Move(p, p.y + 1, p.x + 1));
						}
					}
				}
				if (p.x - 1 >= 0) {
					if (!isThreatenedSquare(p.color, p.y + 1, p.x - 1)) {
						if (board[p.y + 1][p.x - 1] == null) {
							validMoves.add(new Move(p, p.y + 1, p.x - 1));
						} else if (p.isOpponent(board[p.y + 1][p.x - 1])) {
							validMoves.add(new Move(p, p.y + 1, p.x - 1));
						}
					}
				}
			}
			if (p.y - 1 >= 0) {
				if (!isThreatenedSquare(p.color, p.y - 1, p.x)) {
					if (board[p.y - 1][p.x] == null) {
						validMoves.add(new Move(p, p.y - 1, p.x));
					} else if (p.isOpponent(board[p.y - 1][p.x])) {
						validMoves.add(new Move(p, p.y - 1, p.x));
					}
				}
				if (p.x + 1 < 8) {
					if (!isThreatenedSquare(p.color, p.y - 1, p.x + 1)) {
						if (board[p.y - 1][p.x + 1] == null) {
							validMoves.add(new Move(p, p.y - 1, p.x + 1));
						} else if (p.isOpponent(board[p.y - 1][p.x + 1])) {
							validMoves.add(new Move(p, p.y - 1, p.x + 1));
						}
					}
				}
				if (p.x - 1 >= 0) {
					if (!isThreatenedSquare(p.color, p.y - 1, p.x - 1)) {
						if (board[p.y - 1][p.x - 1] == null) {
							validMoves.add(new Move(p, p.y - 1, p.x - 1));
						} else if (p.isOpponent(board[p.y - 1][p.x - 1])) {
							validMoves.add(new Move(p, p.y - 1, p.x - 1));
						}
					}
				}
			}
			if (p.x - 1 >= 0) {
				if (!isThreatenedSquare(p.color, p.y, p.x - 1)) {
					if (board[p.y][p.x - 1] == null) {
						validMoves.add(new Move(p, p.y, p.x - 1));
					} else if (p.isOpponent(board[p.y][p.x - 1])) {
						validMoves.add(new Move(p, p.y, p.x - 1));
					}
				}
			}
			if (p.x + 1 < 8) {
				if (!isThreatenedSquare(p.color, p.y, p.x + 1)) {
					if (board[p.y][p.x + 1] == null) {
						validMoves.add(new Move(p, p.y, p.x + 1));
					} else if (p.isOpponent(board[p.y][p.x + 1])) {
						validMoves.add(new Move(p, p.y, p.x + 1));
					}
				}
			}
			// check if the king is in checkmate
			// ckeckmate occurs when the kings position is threatened, the king
			// cannot move to an unthreatened square, and no blocks can be setup

		}

		// if in check - must get out
		if (!(p instanceof King)) {

			// if this color's king is in check, must get it out of check
			// or else return no valid moves
			if (p.color == Piece.TAN) {
				if (tanCheck) {
					ArrayList<Move> getOutOfCheckMoves = new ArrayList<Move>();
					ArrayList<ArrayList<Square>> threatPaths = generateThreatPaths(getTanKing());
					// for every one of this pieces valid moves
					for (Move m : validMoves) {
						// is the move able to block every single threat path to
						// the king?
						boolean haltsThreat = false;
						for (ArrayList<Square> path : threatPaths) {
							haltsThreat = false;
							// check each square in the path
							for (Square s : path) {
								// can this piece block the path by moving to
								// this square
								if (m.xto == s.getX() && m.yto == s.getY()) {
									haltsThreat = true;
								}
							}
							// if it can't stop the threat, do not add this move
							if (!haltsThreat) {
								break;
							}
						}
						if (haltsThreat) {
							System.out.println("Move added to get out of check.");
							getOutOfCheckMoves.add(m);
						}
					}
					return getOutOfCheckMoves;
				}
			} else {
				if (whiteCheck) {
					ArrayList<Move> getOutOfCheckMoves = new ArrayList<Move>();
					ArrayList<ArrayList<Square>> threatPaths = generateThreatPaths(getWhiteKing());
					// for every one of this pieces valid moves
					for (Move m : validMoves) {
						// is the move able to block every single threat path to
						// the king?
						boolean haltsThreat = false;
						for (ArrayList<Square> path : threatPaths) {
							haltsThreat = false;
							// check each square in the path
							for (Square s : path) {
								// can this piece block the path by moving to
								// this square
								if (m.xto == s.getX() && m.yto == s.getY()) {
									haltsThreat = true;
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
				diaMoves.add(new Move(p, j, i));
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
				diaMoves.add(new Move(p, j, i));
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
				diaMoves.add(new Move(p, j, i));
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
				diaMoves.add(new Move(p, j, i));
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
				strMoves.add(new Move(p, j, i));
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
				strMoves.add(new Move(p, j, i));
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
				strMoves.add(new Move(p, j, i));
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
				strMoves.add(new Move(p, j, i));
			}
		}
		return strMoves;
	}

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
