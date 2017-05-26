package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * TODO current: Considerations: - We only need to generate moves that differ
 * from the last turn - keep track of which pieces are move and removed each
 * turn - must work with undoing - add a piece component to move so they can be
 * searched through - remove moves associated with lost pieces and update moves
 * of moved pieces - in addition must add moves that can occupy the empty space
 * - and must remove moves that overlap with the moved piece - and must see if
 * the king is in check and remove any moves which put it in check TODO next: -
 * Set up a basic SBE
 * 
 * TODO long-term: - resize the toolbar buttons because of something with the
 * boarders - add relevant messages - message scroll bar to see old messages -
 * add more stats TODO bug fixes: - minmax always returning 0 for sbe
 * 
 * - Kings: Each Piece contains a boolean which stores whether or not the piece
 * is currently threat Whenever a piece moves
 * 
 * 
 * 
 * 
 * @author Jacob Siebert
 *
 */
public class ChessBoard {

	// FOR DEBUGGING ONLY
	private boolean debug = false;

	// frame
	JFrame frame;
	// window
	final static int HEIGHT = 1000;// Preferred window size
	final static int WIDTH = 1000;
	final static int XLOC = 400;// x location
	final static int YLOC = 0;
	// UI vars
	final static int TOOLBAR_BUTTON_HEIGHT = 40;
	final static String WELCOME_MESSAGE = " Welcome! Press Start to begin a new game.";
	final static int NUM_STATS = 5;// number of stats being displayed
	// panels
	JPanel boardUI;
	JPanel stats;
	JPanel topPart;// contains toolbar and messages
	JPanel tool_bar;
	JPanel messages;

	JLabel message;
	JLabel[] statistics;

	private Player player1;
	private Player player2;
	private Player currentPlayer;// holds which player is currently moving

	// sprites
	private IconGrabber icon_grabber;
	private PieceButton[][] buttonBoard;// defines a JButton board for the UI
										// only, not used in AI
	private Board board;// the game board used for computation
	private Stack<PieceButton> changedBorders;

	// stats
	private int numMoves = 0;
	private long turnTime;// Time taken for the AI to move

	// used for tracking the clicks used to make moves
	boolean firstPress = true;
	int xPress;
	int yPress;

	public ChessBoard() {
		initUI(false);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ChessBoard chessBoard = new ChessBoard();
			}
		});

	}

	// sets up the user interface
	private void initUI(boolean fullscreen) {
		frame = new JFrame("Chess Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setLocation(XLOC, YLOC);
		Container contentPane = frame.getContentPane();

		// Create tool bar
		tool_bar = new JPanel();
		tool_bar.setLayout(new BorderLayout());
		tool_bar.setPreferredSize(new Dimension(1000, TOOLBAR_BUTTON_HEIGHT));

		// get buttons
		JButton start_button = getStartButton();
		JButton options_button = getOptionsButton();
		JButton resign_button = getResignButton();
		JButton quit_button = getQuitButton();
		JButton undo_button = getUndoButton();
		JButton cancel_button = getCancelButton();
		JButton debug_button = getDebugButton();// temporary

		// spacer
		JPanel buttonSpacer = new JPanel();
		// TODO CHANGE this
		buttonSpacer.setPreferredSize(new Dimension(295, TOOLBAR_BUTTON_HEIGHT));

		// add the buttons
		tool_bar.setLayout(new BoxLayout(tool_bar, BoxLayout.LINE_AXIS));
		tool_bar.add(start_button);
		tool_bar.add(options_button);
		tool_bar.add(resign_button);
		tool_bar.add(quit_button);
		tool_bar.add(buttonSpacer);
		tool_bar.add(debug_button);
		tool_bar.add(cancel_button);
		tool_bar.add(undo_button);

		// load sprites
		// top row is tan, bottom row is white
		icon_grabber = new IconGrabber(new SpriteSheet("sprites.png", 6, 2));

		// make chess board
		// Grid is 9x9 - right-most column and bottom-most row are for labels
		Character letter = 'a';
		buttonBoard = new PieceButton[8][8];
		boardUI = new JPanel(new GridLayout(9, 9));
		boardUI.setMaximumSize(new Dimension(800, 800));
		boardUI.setPreferredSize(new Dimension(800, 800));
		boardUI.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
		// add the buttons and labels to the board
		for (int i = 0; i < 9; i++) {
			if (i < 8) {
				for (int j = 0; j < 8; j++) {
					buttonBoard[i][j] = getBoardButton(i, j, icon_grabber);
					boardUI.add(buttonBoard[i][j]);
				}
			} else {
				// add the a-h labels 
				for (int j = 0; j < 8; j++) {
					String s = letter.toString();
					JLabel label = new JLabel(s);
					label.setHorizontalAlignment(JLabel.CENTER);
					label.setFont(new Font("Tahoma", Font.BOLD, 18));
					boardUI.add(label);
					letter++;
				}
			}
			// add the 1-8 labels
			if (i != 8) {
				Integer row = 8 - i;
				String lbl = row.toString();
				JLabel label = new JLabel(lbl);
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setFont(new Font("Tahoma", Font.BOLD, 18));
				boardUI.add(label);
			}
		}

		stats = getStatsPanel();
		messages = getMessagePanel();
		
		// allows the contentPane to use boarder layout while using a sub-box
		// layout
		topPart = new JPanel();
		topPart.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		topPart.setLayout(new BoxLayout(topPart, BoxLayout.PAGE_AXIS));
		topPart.add(tool_bar);
		topPart.add(messages);

		// add components
		contentPane.setLayout(new BorderLayout());
		contentPane.add(boardUI, BorderLayout.EAST);
		contentPane.add(stats, BorderLayout.WEST);
		contentPane.add(topPart, BorderLayout.NORTH);

		frame.setVisible(true);
		frame.pack();

		displayMessage(WELCOME_MESSAGE);
		
		if (fullscreen)
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

	}
	
	private JPanel getMessagePanel() {
		JPanel messages = new JPanel();
		messages.setLayout(new BorderLayout());
		messages.setPreferredSize(new Dimension(1000, 60));
		// messages.add(Box.createRigidArea(new Dimension(1000,60)));
		TitledBorder tb = BorderFactory.createTitledBorder("Messages");
		tb.setTitleFont(new Font("Tahoma", Font.BOLD, 18));
		tb.setTitlePosition(TitledBorder.DEFAULT_POSITION);
		tb.setBorder(new LineBorder(Color.BLACK));
		messages.setBorder(tb);
		// JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
		// scrollBar.setMinimum(0);
		// scrollBar.setMaximum(100);
		message = new JLabel();
		message.setFont(new Font("Tahoma", Font.PLAIN, 16));
		return messages;
	}
	
	private JPanel getStatsPanel() {
		JPanel stats = new JPanel();
		stats.setPreferredSize(new Dimension(200, 1000));
		stats.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
		stats.setLayout(new BoxLayout(stats, BoxLayout.PAGE_AXIS));
		JLabel s1 = new JLabel("Statistics:");
		s1.setFont(new Font("Tahoma", Font.BOLD, 22));
		stats.add(s1);
		return stats;
	}

	// Gets a PiceButton to place on the board
	// Assigns the proper color value
	private PieceButton getBoardButton(int i, int j, IconGrabber i_grabber) {
		
		PieceButton board_button = new PieceButton(i, j, i_grabber);
		
		// give the button the proper color
		if ((i + j) % 2 == 0) {
			board_button.setBackground(Color.BLACK);
		} else {
			board_button.setBackground(Color.WHITE);
		}
		
		// add ability to move pieces
		board_button.addActionListener(new ActionListener() {
			// first button press chooses which piece to move
			@Override
			public void actionPerformed(ActionEvent e) {
				// For debugging only
				if (debug) {
					PieceButton debug_b = (PieceButton) e.getSource();
					if (debug_b.hasPiece()) {
						debugPiece(board.getPiece(debug_b.getYPos(), debug_b.getXPos()));
					}
				} else {
					// only works if a human player is up
					if (currentPlayer.isHuman()) {
						PieceButton b = (PieceButton) e.getSource();
						// only allow player to select its own
						// pieces
						// the first click selects the piece to move
						// and
						// the next click where to move to

						if (firstPress) {
							if (b.hasPiece()) {
								xPress = b.getXPos();
								yPress = b.getYPos();

								// player1 can only move tans
								if (currentPlayer == player1) {
									if (board.getPiece(yPress, xPress).color == Piece.TAN) {
										displayMessage(player1.getName() + " moving " + ((char) (xPress + 65))
												+ (8 - yPress) + " to...");
										b.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
										changedBorders.add(b);
										firstPress = !firstPress;
										showPossibleMoves(yPress, xPress);// TODO
																			// temporary
									} else {
										displayMessage("Cannot move opponent's pieces.");
									}
								}
								// player2 may only move whites
								else {
									if (board.getPiece(yPress, xPress).color == Piece.WHITE) {
										displayMessage(player2.getName() + " moving " + ((char) (xPress + 65))
												+ (8 - yPress) + " to...");
										b.setBorder(BorderFactory.createLineBorder(Color.RED));
										changedBorders.add(b);
										firstPress = !firstPress;
										showPossibleMoves(yPress, xPress);// TODO
																			// temporary
									} else {
										displayMessage("Cannot move opponent's pieces.");
									}
								}
							}
						}
						// secondPress - create a move
						else {
//							Move m;
//							// castle
//							if (board.getPiece(yPress, xPress) instanceof King
//									&& Math.abs(b.getXPos() - board.getPiece(yPress, xPress).x) > 1) {
//								m = new Castle((King) board.getPiece(yPress, xPress), b.getYPos(), b.getXPos(),
//										(Rook) board.getPiece(b.getYPos(), b.getXPos()));
//							} else {
//								m = new Move(board.getPiece(yPress, xPress), b.getYPos(), b.getXPos());
//							}
//							// validity check
//							if (isValidMove(m)) {
//								move(m);
//								// reset for next player's turn
//								firstPress = true;
//							} else {
//								displayMessage("Not a valid move.");
//							}
						}
					}
				}
			}
		});
		return board_button;
	}

	private JButton getDebugButton() {
		JButton debug_button = new JButton();
		debug_button.setBackground(Color.white);
		debug_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		debug_button.setText("debug");
		debug_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				debug = !debug;
			}
		});
		return debug_button;
	}

	private JButton getCancelButton() {
		JButton cancel_button = new JButton();
		cancel_button.setBackground(Color.white);
		cancel_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		ImageIcon cancelIcon = new ImageIcon("cancel_button_icon.png");
		Image img = cancelIcon.getImage();
		Image newimg = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		cancelIcon.setImage(newimg);
		cancel_button.setIcon(cancelIcon);
		cancel_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// cancel any current move
				firstPress = true;
				// TODO: update player messages
				clearBorders();
			}
		});
		return cancel_button;
	}

	private JButton getUndoButton() {
		JButton undo_button = new JButton();
		undo_button.setBackground(Color.white);
		undo_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		ImageIcon undoIcon = new ImageIcon("undo_button_icon.png");
		Image img = undoIcon.getImage();
		Image newimg = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		undoIcon.setImage(newimg);
		undo_button.setIcon(undoIcon);
		undo_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undoLastMove();
			}
		});
		return undo_button;
	}

	private JButton getResignButton() {
		JButton resign_button = new JButton("Resign");
		resign_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		resign_button.setBackground(Color.white);
		resign_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		resign_button.setFont(new Font("Tahoma", Font.BOLD, 12));
		resign_button.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO
				// are you sure popup
				Object[] options = { "Yes", "Cancel" };
				int choice = JOptionPane.showOptionDialog(frame, "Are you sure you wish to resign the match?", "Resign",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if (choice == 0)
					initGame(Board.EMPTY, 0);
			}

			// unused
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
		return resign_button;
	}

	private JButton getOptionsButton() {
		JButton options_button = new JButton("Options");
		options_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		options_button.setBackground(Color.white);
		options_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		options_button.setFont(new Font("Tahoma", Font.BOLD, 12));
		options_button.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				// options tooltip
			}

			// unused
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
		return options_button;
	}

	private JButton getStartButton() {
		JButton start_button = new JButton("Start");
		start_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		start_button.setBackground(Color.white);
		start_button.setFont(new Font("Tahoma", Font.BOLD, 12));
		start_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		start_button.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				// start new game
				// new game of load game
				int type = -1;
				Object[] options = { "New Game", "Load Game", "Cancel" };
				int choice = JOptionPane.showOptionDialog(frame, "Begin a new game or load an existing game?", "Start",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
				if (choice == 0)
					type = Board.NEW_GAME;
				else if (choice == 1) {
					// TODO
					type = Board.LOAD_GAME;
				}

				// select game mode
				Object[] _options = { "Human vs Human", "Human vs AI", "AI vs AI" };
				choice = JOptionPane.showOptionDialog(frame, "Select game type", "Game Type",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, _options, options[2]);
				initGame(type, choice);
			}

			// unused
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
		return start_button;
	}

	private JButton getQuitButton() {
		JButton quit_button = new JButton("Quit");
		quit_button.setPreferredSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
		quit_button.setBackground(Color.white);
		quit_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		quit_button.setFont(new Font("Tahoma", Font.BOLD, 12));

		quit_button.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				// are you sure popup
				Object[] options = { "Yes", "Cancel" };
				int choice = JOptionPane.showOptionDialog(frame,
						"Are you sure you wish to quit?\nGame will not be saved.", "Quit?", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if (choice == 0)
					System.exit(0);
			}

			// unused
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});

		return quit_button;
	}

	/*
	 * Starts the game in the desired mode, gets user info, and allocates memory
	 * 
	 * @param gameMode - 0: Human vs Human, 1: Human vs AI, 2: AI vs AI
	 */
	public void initGame(int gameType, int gameMode) {
		changedBorders = new Stack<PieceButton>();
		board = new Board(gameType);

		// TODO - choose who goes first
		if (gameMode == 0) {
			player1 = new HumanPlayer(Piece.TAN);
			player2 = new HumanPlayer(Piece.WHITE);
		} else if (gameMode == 1) {
			player1 = new HumanPlayer(Piece.TAN);
			player2 = new AI_Player(Piece.WHITE, board);
		} else {
			player1 = new AI_Player(Piece.TAN, board);
			player2 = new AI_Player(Piece.WHITE, board);
		}
		// new game
		if (gameType == Board.NEW_GAME) {
			currentPlayer = player1;
			getPlayerInfo();
			displayMessage("New Game: " + player1.getName() + " vs " + player2.getName());
			newGamePieces();
		}
		// TODO: load game
		else if (gameType == Board.LOAD_GAME) {

		}
		// return UI and game to initial setup
		else {
			clearPieces();
		}
		initStats();
	}

	/*
	 * Determines is the given move is valid based on the current board
	 * 
	 */
	public boolean isValidMove(Move m) {
		// update the valid moves
		ArrayList<Move> validMoves = board.generateValidMoves(m.getSelectedPiece());

		for (Move vm : validMoves) {
			if (m.equals(vm))
				return true;
		}
		return false;
	}

	public void undoLastMove() {
		// undo last move
		if (numMoves > 0) {
			// undo it on the board
			Move undone = board.undoLastMove();
			// undo it on the UI
			Piece moved = undone.getSelectedPiece();
			buttonBoard[moved.y][moved.x].setPiece(moved.type, moved.color);
			try {
				Piece removed = undone.getRemovedPiece();
				buttonBoard[removed.y][removed.x].setPiece(removed.type, removed.color);
			} catch (NoSuchPieceException e1) {
				buttonBoard[undone.yto][undone.xto].removePiece();
			}

			// switch the turn
			if (currentPlayer == player1) {
				currentPlayer = player2;
			} else {
				currentPlayer = player1;
			}

			numMoves--;
		} else {
			displayMessage("No moves to be undone!");
		}
	}

	/*
	 * Makes the specified move on the UI and on the game board, if the next
	 * player is an AI this method will call the AI move function. This method
	 * also disable the pawn's double forward move once it has been moved
	 * 
	 * @param move - the move to be made, MOVE ASSUMED TO BE VALID
	 * 
	 */
	public void move(Move m) {

		// apply the move on the game board
		board.applyMove(m);
		
		// Graphics
		make_move_UI(m);

		numMoves++;
		if (currentPlayer == player1)
			displayMessage(player1.getName() + " " + m.toString());
		else
			displayMessage(player2.getName() + " " + m.toString());

		// next player's turn
		if (currentPlayer == player1)
			currentPlayer = player2;
		else
			currentPlayer = player1;

		// AI makes its move here unless next player is human
		if (!currentPlayer.isHuman()) {
			turnTime = makeAIMove();
		}

		// remove any border highlights from the last move
		clearBorders();
		updateStats();

		// DELETE THIS
		// System.out.println("--------------------------------");
		//board.displayBoard();
		// System.out.println("--------------------------------");
	}
	
	private long makeAIMove() {
		Date start = new Date();

		Move AI_move = currentPlayer.nextMove(board, 3);
		displayMessage(currentPlayer.getName() + " move: " + AI_move);// TODO make look nicer

		board.applyMove(AI_move);

		make_move_UI(AI_move);

		// next player's turn
		if (currentPlayer == player1)
			currentPlayer = player2;
		else
			currentPlayer = player1;

		Date end = new Date();
		return end.getTime() - start.getTime();
	}

	// makes the move on the UI but not on the game board
	// always make the move on the board prior to the UI
	private void make_move_UI(Move m) {
		if(m instanceof Castle) {
			Castle c = (Castle) m;
			// move king
			buttonBoard[c.yfrom][c.xfrom].removePiece();
			buttonBoard[c.yto][c.xto].setPiece(c.getSelectedPiece().type, c.getSelectedPiece().color);
			// move rook
			buttonBoard[c.castled_yFrom][c.castled_xFrom].removePiece();
			buttonBoard[c.castled_yto][c.castled_xto].setPiece(c.getCastled().type, c.getCastled().color);
		} else if(m instanceof Upgrade) {
			Upgrade u = (Upgrade) m;
			buttonBoard[u.yfrom][u.xfrom].removePiece();
			buttonBoard[u.yto][u.xto].setPiece(u.getUpgraded().type, u.getUpgraded().color);
		} else if(m instanceof EnPassant) {
			EnPassant ep = (EnPassant) m;
			buttonBoard[ep.yfrom][ep.xfrom].removePiece();
			buttonBoard[ep.yto][ep.xto].setPiece(m.getSelectedPiece().type, m.getSelectedPiece().color);
			// remove captured piece
			buttonBoard[ep.getRemovedPiece().y][ep.getRemovedPiece().x].removePiece();
		} else {
			buttonBoard[m.yfrom][m.xfrom].removePiece();
			buttonBoard[m.yto][m.xto].setPiece(m.getSelectedPiece().type, m.getSelectedPiece().color);
		}
	}

	public void clearPieces() {
		// clear UI
		int[][] whites = board.getWhiteLocations();
		int[][] tans = board.getTanLocations();
		for (int i = 0; i < whites.length; i++) {
			buttonBoard[whites[i][0]][whites[i][1]].clearIcon();
		}
		for (int i = 0; i < tans.length; i++) {
			buttonBoard[tans[i][0]][tans[i][1]].clearIcon();
		}
		// clear game board
		board = new Board(Board.EMPTY);
	}

	// adds the pieces to the UI
	public void newGamePieces() {
		for (Piece p : board.getTanPieces()) {
			buttonBoard[p.y][p.x].setPiece(p.type, p.color);
		}
		for (Piece p : board.getWhitePieces()) {
			buttonBoard[p.y][p.x].setPiece(p.type, p.color);
		}
	}

	/*
	 * Highlights the possible moves of the piece at [j][i] on the UI
	 */
	public void showPossibleMoves(int j, int i) {
		ArrayList<Move> validMoves = board.generateValidMoves(board.getPiece(j, i));
		for (Move m : validMoves) {
			buttonBoard[m.yto][m.xto].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 8));
			changedBorders.push(buttonBoard[m.yto][m.xto]);
		}
	}

	/*
	 * Clears all non-black borders
	 */
	public void clearBorders() {
		while (!changedBorders.isEmpty()) {
			changedBorders.pop().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
	}

	// gets the name of each player
	public void getPlayerInfo() {
		// TODO
		JTextField p1 = new JTextField(15);
		JTextField p2 = new JTextField(15);

		JPanel name_popup = new JPanel();
		name_popup.add(new JLabel("Player 1:"));
		name_popup.add(p1);
		name_popup.add(Box.createHorizontalStrut(15)); // spacer
		name_popup.add(new JLabel("Player 2:"));
		name_popup.add(p2);

		int result = JOptionPane.showConfirmDialog(null, name_popup, "Please Enter Player Names",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			player1.setName(p1.getText());
			player2.setName(p2.getText());
		}
		if (player1.getName().isEmpty())
			player1.setName("Player 1");
		if (player2.getName().isEmpty())
			player2.setName("Player 2");
	}

	public void initStats() {
		// TODO
		statistics = new JLabel[NUM_STATS];
		for (int i = 0; i < NUM_STATS; i++) {
			statistics[i] = new JLabel();
		}
		statistics[0].setText(" Player 1 Name: " + player1.getName());
		statistics[1].setText(" Player 2 Name: " + player2.getName());
		statistics[2].setText(" Turn: " + currentPlayer.getName());
		statistics[3].setText(" # of moves: " + numMoves);
		for (int i = 0; i < NUM_STATS; i++) {
			stats.add(statistics[i]);
		}
	}

	public void updateStats() {
		// TODO
		// Display if anyone is in check
		if (board.tanInCheck()) {
			displayMessage("Tan's King is in Check.");
		}
		if (board.whiteInCheck()) {
			displayMessage("White's King is in Check.");
		}

		// current turn
		if (currentPlayer == player1) {
			statistics[2].setText(" Turn: " + player1.getName());

		} else {
			statistics[2].setText(" Turn: " + player2.getName());
		}
		// number of moves
		statistics[3].setText(" # of moves: " + numMoves);
		statistics[4].setText("AI turn Time: " + turnTime + " ms");
	}

	public void debugPiece(Piece p) {
		if (p.color == Piece.TAN)
			displayMessage("Type:" + p.type + "  Y: " + p.y + "  X:" + p.x + "  Color:Tan");
		else
			displayMessage("Type:" + p.type + "  Y: " + p.y + "  X:" + p.x + "  Color:White");
	}

	public void displayMessage(String s) {
		message.setText(s);
		messages.add(message);
	}
}
