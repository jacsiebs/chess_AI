package jacob.siebert.chessai.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import jacob.siebert.chessai.board.Board;
import jacob.siebert.chessai.exception.NoSuchPieceException;
import jacob.siebert.chessai.move.Castle;
import jacob.siebert.chessai.move.EnPassant;
import jacob.siebert.chessai.move.Move;
import jacob.siebert.chessai.move.Promotion;
import jacob.siebert.chessai.piece.*;
import jacob.siebert.chessai.player.Basic_MinMax_AI;
import jacob.siebert.chessai.player.HumanPlayer;
import jacob.siebert.chessai.player.Player;
import jacob.siebert.chessai.type.BoardStatus;
import jacob.siebert.chessai.type.PieceColor;
import jacob.siebert.chessai.type.PlayerTypes;
import jacob.siebert.chessai.type.NewGameType;
import jacob.siebert.chessai.ui.IconGrabber;
import jacob.siebert.chessai.ui.PieceButton;
import jacob.siebert.chessai.ui.SpriteSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**TODO List:
 * 1. Test upgrades on UI
 * 2. Test everything more in depth
 * 3. Loading games from files
 * 4. Exporting games to files
 * 5. Checkmates!
 * 6. Update AI until functional (Test)
 * 7. Optimize AI
 * 8. Test AI
 * 9. AI Book Moves
 * 10. UI upgrade
 * 11. AI vs AI
 * 12. Optimize move generation
 * 13. Test rigorously
 * 14. Embed in a website
 * 15. Undoing moves
 * 16. Refactor unnecessary stuff
 * 		-firstMove replaced by timesMove = 0
 * 
 * @author Jacob Siebert
 *
 */
public class ChessBoard {

	private static Logger LOG = LoggerFactory.getLogger(ChessBoard.class);

	// FOR DEBUGGING ONLY
	private boolean debug = false;

	// frame
	private JFrame frame;
	// window
	private final static int HEIGHT = 1000;// Preferred window size
	private final static int WIDTH = 1000;
	private final static int XLOC = 400;// x location
	private final static int YLOC = 0;
	// UI vars
	private final static int TOOLBAR_BUTTON_HEIGHT = 40;
	private final static String WELCOME_MESSAGE = " Welcome! Press Start to begin a new game.";
	private final static int NUM_STATS = 5;// number of stats being displayed
	// panels
	private JPanel boardUI;
	private JPanel stats;
	private JPanel topPart;// contains toolbar and messages
	private JPanel tool_bar;
	private JPanel messages;
	// labels
	private JLabel message;
	private JLabel[] statistics;
	// game vars
	private PlayerTypes playerTypes;
	private Player player1;
	private Player player2;
	private Player currentPlayer;// holds which player is currently moving
	private Board board;// the game board used for computation
	private Stack<PieceButton> changedBorders;
	private boolean firstPress = true;// used for tracking the clicks used to make moves
	private int xPress;
	private int yPress;
	private Piece selected;// the piece clicked by the current player
	// sprites
	private IconGrabber icon_grabber;
	private PieceButton[][] buttonBoard;// defines a JButton board for the UI
	private static final int SPRITESHEET_COLS = 6;
	private static final int SPRITESHEET_ROWS = 2;
	// image locations relative to resources
	private static final String SPRITESHEET_LOC = "images/sprites.png";
	private static final String CANCEL_BUTTON_LOC = "images/cancel_button_icon.png";
	private static final String UNDO_BUTTON_LOC = "images/undo_button_icon.png";
	// stats
	private int numMoves = 0;
	private long turnTime;// Time taken for the AI to move

	public ChessBoard() {
		initUI(false);// TODO fullscreen
	}

	// used for testing - loads a gamestate into the board
	public ChessBoard(String gamestate_filename, PlayerTypes playerTypes) {
		initUI(false);// TODO fullscreen
		this.playerTypes = playerTypes;
		initGame(NewGameType.LOAD, playerTypes);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ChessBoard chessBoard = new ChessBoard();
			}
		});

	}
	
	protected Board getBoard() {
		return board;
	}

	// sets up the user interface
	protected void initUI(boolean fullscreen) {
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
		icon_grabber = new IconGrabber(new SpriteSheet(SPRITESHEET_LOC, SPRITESHEET_COLS, SPRITESHEET_ROWS));

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
			// first button press chooses which piece to move and second press
			// makes the move and executes it if valid
			public void actionPerformed(ActionEvent e) {
				// only allow button use if a human player is up
				if (currentPlayer.isHuman()) {
					pressedPieceButton((PieceButton) e.getSource());
				}
			}
		});
		return board_button;
	}
	
	/**Called when a PieceButton is selected by a current human's turn
	 * @param pb - The selected PieceButton
	 */
	private void pressedPieceButton(PieceButton pb) {
		if (firstPress) {
			selectPiece(pb);
		} 
		// secondPress
		else {
			Move chosenMove = formulateMove(pb);
			// validity check
			if (isValidMove(chosenMove)) {
				// execute the move
				move(chosenMove);
			} else {
				displayMessage("Not a valid jacob.siebert.chessai.move.");
				clearBorders();
			}
			// TODO if invalid - should firstpress be reset?
			// reset for next player's turn or new move
			firstPress = true;
		}
	}
	
	/**Called when a user clicks a PieceButton and firstPress=true.
	 * Stores the Piece (if it exists and is valid) in the selected Piece
	 * for use by formulateMove() which deals with the second press.
	 * A valid piece must exist and is the current player's color.
	 * Also sets the value of firstPress - if not a valid selected Piece
	 * firstPress remains true, if valid selection -> fristPress=false.
	 * 
	 * @param selected_button - the PieceButton clicked by the player
	 */
	private void selectPiece(PieceButton selected_button) {
		// check if a piece even exists on this button - if not ignore it
		if (selected_button.hasPiece()) {
			xPress = selected_button.getXPos();
			yPress = selected_button.getYPos();
			selected = board.getPiece(yPress, xPress);
			// player1 can only move tans
			if (currentPlayer == player1) {
				if (selected.isTan()) {
					displayMessage(player1.getName() + " moving " + ((char) (xPress + 65))
							+ (8 - yPress) + " to...");
					selected_button.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
					changedBorders.add(selected_button);
					firstPress = false;
					showPossibleMoves(yPress, xPress);// TODO temporary
					return;
				}
			}
			// player2 may only move whites
			else {
				if (board.getPiece(yPress, xPress).isWhite()) {
					displayMessage(player2.getName() + " moving " + ((char) (xPress + 65))
							+ (8 - yPress) + " to...");
					selected_button.setBorder(BorderFactory.createLineBorder(Color.RED));
					changedBorders.add(selected_button);
					firstPress = false;
					showPossibleMoves(yPress, xPress);// TODO temporary
					return;
				}
			}
			// player had selected the wrong color
			displayMessage("Cannot move opponent's pieces.");
			selected = null;
		}
	}

	/**Called when a player has pressed a second PieceButton after selecting a valid Piece to move.
	 * Generates the intended move from the selected Piece, and second PieceButton pressed.
	 * This Move can also be a Pawn Promotion, EnPassant, or Castle.
	 * The Move could be valid or invalid - must check it once returned.
	 * 
	 * @param second_selected_button - The PieceButton pressed second by the current player
	 * @return The Move formulated from the player's button presses
	 */
	private Move formulateMove(PieceButton second_selected_button) {
		// ensure that a selected piece exists
		if(selected == null) {
			throw new NoSuchPieceException("Selected Piece did not exist but formulateMove() was called.");
		}
		// secondPress - create a move
		int secondXPress = second_selected_button.getXPos();
		int secondYPress = second_selected_button.getYPos();
		Piece secondPress = board.getPiece(secondYPress, secondXPress);// can be null
	
		/* Castle Move
		 * 1. Selected Piece is a king
		 * 2. The king is attempting to move more than 1 squares away
		 * 
		 * These conditions do not necessarily mean the castle is valid but the only
		 * time a king can move 2 squares away is a castle.
		 */
		if (selected instanceof King && Math.abs(xPress - secondXPress) > 1) {
			// check king's side or queen's side castle
			if(xPress > secondXPress) {
				// Queen's side
				if(board.getPiece(secondYPress, 0) != null) {
					return new Castle((King) selected, yPress, xPress - 2,
							(Rook) board.getPiece(secondYPress, 0), secondYPress, 3);
				}
			} else {
				// king's side
				if(board.getPiece(secondYPress, 7) != null) {
					return new Castle((King) selected, selected.y, selected.x + 2,
							(Rook) board.getPiece(secondYPress, 7), secondYPress, 5);
				}
			}
		}
		/* EnPassant Move
		 * 1. Selected Piece is a Pawn in the proper row (depending on color)
		 * 2. An enemy Pawn exists below for tans (or above for whites) the second pressed button
		 * 3. The Pawn from (2) has made a double pawn forward move the previous turn
		 */
		Pawn lastDoublePawn = board.getLastDoublePawn();
		if(lastDoublePawn != null && selected instanceof Pawn && secondPress == null) {
			Piece captured;
			if(selected.getColor() == PieceColor.TAN) {
				captured = board.getPiece(second_selected_button.getYPos() + 1, second_selected_button.getXPos());
			} else {
				captured = board.getPiece(second_selected_button.getYPos() - 1, second_selected_button.getXPos());
			}
			if(captured != null && captured.equals(lastDoublePawn)) {
				return new EnPassant((Pawn) selected, secondYPress, secondXPress, (Pawn) captured);
			}

		}
		/* Promotion Pawn Move
		 * 1. Must be a pawn advancing into the last row opposite its side
		 * 
		 * Open a menu to allow the player to choose which piece to upgrade to.
		 * Only open this menu if the move is actually valid - must be able to
		 * move into the last row through a forward move or capture.
		 */
		if(selected instanceof Pawn) {
			if(selected.getColor() == PieceColor.TAN) {
				// only open the upgrade menu if it is truly valid
				if(yPress == 1 && secondYPress == 0) {
					int xdif = xPress - secondXPress;
					/* Valid if: 
					 * 1. The forward move is not blocked
					 * 2. The Pawn can capture
					 */
					if( (xdif == 0 && board.getPiece(0, selected.x) == null)
							|| (board.getPiece(0, xPress - xdif) != null 
								&& board.getPiece(0, xPress - xdif).isOpponent(selected))) {
						char type = upgradeMenu();
						return new Promotion((Pawn) selected, secondYPress, secondXPress,
								type, selected.getColor(), board.getPiece(secondYPress, secondXPress));
					}
				}
			} 
			// white
			else {
				if (yPress == 6 && secondYPress == 7) {
					// only open the upgrade menu if it is truly valid
					int xdif = xPress - secondXPress;
					/*
					 * Valid if: 
					 * 1. The forward move is not blocked
					 * 2. The Pawn can capture
					 */
					if ((xdif == 0 && board.getPiece(7, xPress) == null)
							|| (board.getPiece(7, xPress - xdif) != null
									&& board.getPiece(7, xPress - xdif).isOpponent(selected))) {
						char type = upgradeMenu();
						return new Promotion((Pawn) selected, secondYPress,
								secondXPress, type, selected.getColor(),
								board.getPiece(secondYPress, secondXPress));
					}
				}
			}
		}
		// all other moves
		return new Move(selected, secondYPress, secondXPress, board.getPiece(secondYPress, secondXPress));
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
		// load image
		URL image_url = this.getClass().getClassLoader().getResource(CANCEL_BUTTON_LOC);
		if(image_url == null) {
			LOG.error("Could not locate cancel icon image. ("+ CANCEL_BUTTON_LOC + ")");
			return null;
		}
		ImageIcon cancelIcon = new ImageIcon(image_url);
		Image img = cancelIcon.getImage();
		Image new_img = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		cancelIcon.setImage(new_img);
		cancel_button.setIcon(cancelIcon);
		cancel_button.addActionListener(new ActionListener() {
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
		// get image and scale it properly
		URL image_url = this.getClass().getClassLoader().getResource(UNDO_BUTTON_LOC);
		if(image_url == null) {
			LOG.error("Could not locate undo icon image. ("+ UNDO_BUTTON_LOC + ")");
			return null;
		}
		ImageIcon undoIcon = new ImageIcon(image_url);
		Image img = undoIcon.getImage();
		Image new_img = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		undoIcon.setImage(new_img);
		undo_button.setIcon(undoIcon);
		undo_button.addActionListener(new ActionListener() {
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
			public void mousePressed(MouseEvent e) {
				// TODO
				// are you sure popup
				Object[] options = { "Yes", "Cancel" };
				int choice = JOptionPane.showOptionDialog(frame, "Are you sure you wish to resign the match?", "Resign",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				// TODO resign button
				//if (choice == 0)
					//initGame(NewGameType.EMPTY, 0);
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
			public void mousePressed(MouseEvent e) {
				// start new game
				// new game of load game
				NewGameType type = NewGameType.EMPTY;
				Object[] options = { "New Game", "Load Game", "Cancel" };
				int choice = JOptionPane.showOptionDialog(frame, "Begin a new game or load an existing game?", "Start",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
				if (choice == 0)
					type = NewGameType.NEW;
				else if (choice == 1) {
					type = NewGameType.LOAD;
				}

				// select game mode
				Object[] _options = { "Human vs Human", "Human vs AI", "AI vs AI" };
				choice = JOptionPane.showOptionDialog(frame, "Select game type", "Game Type",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, _options, options[2]);
				if(choice == 0) {
					initGame(type, PlayerTypes.H_v_H);

				} else if(choice == 1) {
					initGame(type, PlayerTypes.H_v_AI);

				} else if(choice == 2) {
					initGame(type, PlayerTypes.AI_v_AI);
				}
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

	/**
	 * Starts the game in the desired mode, gets user info, and allocates memory.
	 * Called once a user begins a game by pressing "start"
	 *
	 * @param gameType - New game, load game, or empty game?
	 * @param playerTypes - 0: Human vs Human, 1: Human vs AI, 2: AI vs AI
	 */
	public void initGame(NewGameType gameType, PlayerTypes playerTypes) {
		changedBorders = new Stack<PieceButton>();
		board = new Board(gameType, this);

		// TODO - choose who goes first
		if (playerTypes == PlayerTypes.H_v_H) {
			player1 = new HumanPlayer(PieceColor.TAN);
			player2 = new HumanPlayer(PieceColor.WHITE);
		} else if (playerTypes == PlayerTypes.H_v_AI) {
			player1 = new HumanPlayer(PieceColor.TAN);
			player2 = new Basic_MinMax_AI(PieceColor.WHITE, board);
		} else {
			player1 = new Basic_MinMax_AI(PieceColor.TAN, board);
			player2 = new Basic_MinMax_AI(PieceColor.WHITE, board);
		}
		// new game
		if (gameType == NewGameType.NEW) {
			currentPlayer = player1;
			getPlayerInfo();
			displayMessage("New Game: " + player1.getName() + " vs " + player2.getName());
			newGamePieces();
		}
		// load game
		else if (gameType == NewGameType.LOAD) {
			// TODO file not found
			File gamestateFile = promptUserForFilename();
			loadGameFromFile(gamestateFile);
		}
		// generally just used in testing
		else if(gameType == NewGameType.EMPTY) {
			currentPlayer = player1;
			getPlayerInfo();
		}
		// return UI and game to initial setup
		else {
			clearPieces();
		}
		initStats();
	}

	public File promptUserForFilename() {
		File gamestate;
		do {
			String filename = JOptionPane.showInputDialog(frame, "Enter a filename:", "Load Game");
			gamestate = new File(filename);
		} while(!gamestate.exists());
		return gamestate;
	}

	// TODO handle errors
	private void loadGameFromFile(File gamestate) {
		Scanner sc_game;
		boolean givenNumMoves = false;
		boolean givenPlayer1Name = false;
		boolean givenPlayer2Name = false;
		boolean givenTurn = false;

		try{
			sc_game = new Scanner(gamestate);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String line;
		// process metadata
		while(sc_game.hasNext()) {
			line = sc_game.nextLine();
			if(!line.isEmpty() && line.charAt(0) != '/') {
				if(line.charAt(0) == '+') {
					// the board has begun - break and read it in
					break;
				}
				String[] tokens = line.split("=");
				// check for empty fields
				if(tokens[0] == null || tokens[1] == null) {
					throw new RuntimeException("GameState file malformed");
				}
				// store metadata
				switch(tokens[0]) {
					case "turn":
						if(tokens[1].equals("tan")) {
							currentPlayer = player1;
						} else if(tokens[1].equals("white")) {
							currentPlayer = player2;
						} else {
							throw new RuntimeException("GameState file malformed");
						}
						givenTurn = true;
						break;
					case "player1Name":
						player1.setName(tokens[1]);
						givenPlayer1Name = true;
						break;
					case "player2Name":
						player2.setName(tokens[1]);
						givenPlayer2Name = true;
						break;
					case "numMoves":
						numMoves = Integer.parseInt(tokens[1]);// TODO handle failure
						givenNumMoves = true;
						break;
					default:
						throw new RuntimeException("GameState file malformed");
				}
			}
		}
		// set default values if not provided
		if(!givenNumMoves) {
			numMoves = 0;
		}
		if(!givenPlayer1Name) {
			player1.setName("Player1");
		}
		if(!givenPlayer2Name) {
			player2.setName("Player2");
		}
		if(!givenTurn) {
			currentPlayer = player1;
		}

		String numMoves = null;
		// process the game board - first line of "+---+--"... can be skipped
		int j = 0;// the y position
		int i = 0;// the x position
		while(sc_game.hasNext()) {
			line = sc_game.nextLine();
			if(line.charAt(0) != '+') {
				StringCharacterIterator itr = new StringCharacterIterator(line);
				int end = itr.getEndIndex();
				char curr;// = itr.next();// this is the first '|' character in the line
//				if(curr != '|') {
//					throw new RuntimeException("GameState file malformed: Expected '|' and got " + curr);
//				}
				// iterate over the line, adding pieces that appear
				for(int k = 0; k <= end; k++) {
					curr = itr.next();
					if(curr == 'k' || curr == 'q' || curr == 'r' || curr == 'b' || curr == 'n'
							|| curr == 'p' || curr == 'K' || curr == 'Q' || curr == 'R'
							|| curr == 'B' || curr == 'N' || curr == 'P') {
						char type = curr;
						curr = itr.next();
						k++;
						if(curr == ' ') {
							// this piece has not moved yet
							numMoves = "0";
						} else if(Character.isDigit(curr)) {
							char first = curr;
							curr = itr.next();
							k++;
							if(Character.isDigit(curr)) {
								numMoves = Character.toString(first) + Character.toString(curr);
							} else if(curr == ' '){
								numMoves = Character.toString(first);
							} else {
								throw new RuntimeException("GameState file malformed: Expected a digit or ' '"
										+ "\nBut got " + curr);
							}
						}
						// add the piece with the specified number of moves
						switch(type) {
							case 'k':
								board.placePiece(new King(PieceColor.TAN, j, i, Integer.parseInt(numMoves)));
								break;
							case 'K':
								board.placePiece(new King(PieceColor.WHITE, j, i, Integer.parseInt(numMoves)));
								break;
							case 'q':
								board.placePiece(new Queen(PieceColor.TAN, j, i, Integer.parseInt(numMoves)));
								break;
							case 'Q':
								board.placePiece(new Queen(PieceColor.WHITE, j, i, Integer.parseInt(numMoves)));
								break;
							case 'b':
								board.placePiece(new Bishop(PieceColor.TAN, j, i, Integer.parseInt(numMoves)));
								break;
							case 'B':
								board.placePiece(new Bishop(PieceColor.WHITE, j, i, Integer.parseInt(numMoves)));
								break;
							case 'n':
								board.placePiece(new Knight(PieceColor.TAN, j, i, Integer.parseInt(numMoves)));
								break;
							case 'N':
								board.placePiece(new Knight(PieceColor.WHITE, j, i, Integer.parseInt(numMoves)));
								break;
							case 'p':
								board.placePiece(new Pawn(PieceColor.TAN, j, i, Integer.parseInt(numMoves)));
								break;
							case 'P':
								board.placePiece(new Pawn(PieceColor.WHITE, j, i, Integer.parseInt(numMoves)));
								break;
							case 'r':
								board.placePiece(new Rook(PieceColor.TAN, j, i, Integer.parseInt(numMoves)));
								break;
							case 'R':
								board.placePiece(new Rook(PieceColor.WHITE, j, i, Integer.parseInt(numMoves)));
								break;
						}
					} else if(curr == '|') {
						// go on to the next x position
						i++;
					}
				}
			} else {
				j++;
				i = 0;
			}
		}
		sc_game.close();
	}

	/*
	 * Determines is the given move is valid based on the current board
	 * 
	 */
	public boolean isValidMove(Move m) {
		// update the valid moves
		ArrayList<Move> validMoves = board.generateValidMoves(m.getSelectedPiece());

		for (Move vm : validMoves) {
			if (vm.equals(m))
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
			buttonBoard[moved.y][moved.x].setPiece(moved.type, moved.getColor());
			try {
				Piece removed = undone.getRemovedPiece();
				buttonBoard[removed.y][removed.x].setPiece(removed.type, removed.getColor());
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

		// apply the move on the game board - game logic
		board.applyMove(m);
		
		// Graphics - UI
		make_move_UI(m);

		if (currentPlayer == player1)
			displayMessage(player1.getName() + " " + m.toString());
		else
			displayMessage(player2.getName() + " " + m.toString());

		// next player's turn
		numMoves++;
		if (currentPlayer == player1) {
			currentPlayer = player2;
		}
		else {
			currentPlayer = player1;
		}

		// AI makes its move here unless next player
		if (!currentPlayer.isHuman()) {
			turnTime = makeAIMove();
		}

		// remove any border highlights from the last move
		clearBorders();
		updateStats();

		// DELETE THIS
		System.out.println("--------------------------------");
		board.printBoard();
		System.out.println("--------------------------------");
	}

	/**TODO
	 * Called by the Board once it has detected a checkmate or a stalemate.
	 * Prints game over messages and ends the current game.
	 */
	public void gameOver(BoardStatus status) {
		if(status == BoardStatus.TAN_CHECKMATED) {
			LOG.info("Game over! White has won!");
		} else if(status == BoardStatus.WHITE_CHECKMATED) {
			LOG.info("Game over! Tan has won!");
		} else if(status == BoardStatus.STALEMATE) {
			LOG.info("Game over! Stalemate!");
		} else {
			LOG.error("Game over called when the game is still in progress!");
			throw new RuntimeException("Game over called when the game is still in progress!");
		}
	}

	// TODO
	private long makeAIMove() {
		Date start = new Date();
//
//		Move AI_move = currentPlayer.nextMove(board, 3);
//		displayMessage(currentPlayer.getName() + " jacob.siebert.chessai.move: " + AI_move);// TODO make look nicer
//
//		board.applyMove(AI_move);
//
//		make_move_UI(AI_move);
//
//		// next player's turn
//		if (currentPlayer == player1)
//			currentPlayer = player2;
//		else
//			currentPlayer = player1;
//
		Date end = new Date();
		return end.getTime() - start.getTime();
	}

	/**Executes the given move on the user interface only.
	 * Does not affect any underlying game logic.
	 * Make the move on the Board prior to calling this method.
	 * @param m - The move to make on the UI
	 */
	private void make_move_UI(Move m) {
		if(m instanceof Castle) {
			Castle c = (Castle) m;
			// move king
			buttonBoard[c.yfrom][c.xfrom].removePiece();
			buttonBoard[c.yto][c.xto].setPiece(c.getSelectedPiece().type, c.getSelectedPiece().getColor());
			// move rook
			buttonBoard[c.castled_yFrom][c.castled_xFrom].removePiece();
			buttonBoard[c.castled_yto][c.castled_xto].setPiece(c.getCastled().type, c.getCastled().getColor());
		} else if(m instanceof Promotion) {
			Promotion u = (Promotion) m;
			buttonBoard[u.yfrom][u.xfrom].removePiece();
			// will overwrite a the capture piece if it exists
			buttonBoard[u.yto][u.xto].setPiece(u.getUpgraded().type, u.getUpgraded().getColor());
		} else if(m instanceof EnPassant) {
			EnPassant ep = (EnPassant) m;
			buttonBoard[ep.yfrom][ep.xfrom].removePiece();
			buttonBoard[ep.yto][ep.xto].setPiece(m.getSelectedPiece().type, m.getSelectedPiece().getColor());
			// remove captured piece
			buttonBoard[ep.getRemovedPiece().y][ep.getRemovedPiece().x].removePiece();
		} else {
			buttonBoard[m.yfrom][m.xfrom].removePiece();
			// will overwrite a the capture piece if it exists
			buttonBoard[m.yto][m.xto].setPiece(m.getSelectedPiece().type, m.getSelectedPiece().getColor());
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
		board = new Board(NewGameType.EMPTY, this);
	}

	// adds the pieces to the UI
	public void newGamePieces() {
		for (Piece p : board.getTanPieces()) {
			buttonBoard[p.y][p.x].setPiece(p.type, p.getColor());
		}
		for (Piece p : board.getWhitePieces()) {
			buttonBoard[p.y][p.x].setPiece(p.type, p.getColor());
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
	
	/**TODO
	 * Opens a menu for upgrading Pawns -- player chooses from queen, bishop, rook, or knight
	 * @return the type of the user's upgraded choice
	 */
	private char upgradeMenu() {
//		// 0=queen, 1=rook, 2=knight, 3=bishop
//		int choice = JOptionPane.showOptionDialog(frame, "Select an upgraded piece:", "Promotion",
//				JOptionPane., JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
//		switch(choice) {
//		case 0:
//			return 'q';
//		case 1:
//			return 'r';
//		case 2: 
//			return 'n';
//		case 3:
//			return 'b';
//		}
		return 'q';
	}

	/*
	 * Clears all non-black borders
	 */
	private void clearBorders() {
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
		if (p.getColor() == PieceColor.TAN)
			displayMessage("Type:" + p.type + "  Y: " + p.y + "  X:" + p.x + "  Color:Tan");
		else
			displayMessage("Type:" + p.type + "  Y: " + p.y + "  X:" + p.x + "  Color:White");
	}

	public void displayMessage(String s) {
		message.setText(s);
		messages.add(message);
	}
	
	public Player getPlayer1() {
		return player1;
	}
	
	public Player getPlayer2() {
		return player2;
	}
}
