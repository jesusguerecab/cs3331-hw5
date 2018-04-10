package sudoku.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import sudoku.model.Board;

/**
 * A dialog template for playing simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(int), numberClicked(int) and boardClicked(int,int).
 *
 * @author Yoonsik Cheon
 */
@SuppressWarnings("serial")
public class SudokuDialog extends JFrame {

	/** Default dimension of the dialog. */
	private final static Dimension DEFAULT_SIZE = new Dimension(310, 430);

	private final static String IMAGE_DIR = "/image/";

	/** Sudoku board. */
	private Board board;

	/** Special panel to display a Sudoku board. */
	private BoardPanel boardPanel;

	/** Message bar to display various messages. */
	private JLabel msgBar = new JLabel("");

	private JMenuItem solve;
	
	/** Create a new dialog. */
	public SudokuDialog() {
		this(DEFAULT_SIZE);
	}

	/** Create a new dialog of the given screen dimension. */
	public SudokuDialog(Dimension dim) {
		super("Sudoku");
		setSize(dim);
		board = new Board(9);
		boardPanel = new BoardPanel(board, this::boardClicked);
		configureUI();
		//setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		//setResizable(false);
	}

	/**
	 * Callback to be invoked when a square of the board is clicked.
	 * @param x 0-based row index of the clicked square.
	 * @param y 0-based column index of the clicked square.
	 */
	private void boardClicked(int x, int y) {
		boardPanel.selectRect(x, y);
		//showMessage(String.format("DEBUG: Board clicked: x = %d, y = %d",  x, y));
		boardPanel.repaint();
	}

	/**
	 * Callback to be invoked when a number button is clicked.
	 * @param number Clicked number (1-9), or 0 for "X".
	 */
	private void numberClicked(int number) {
		if(number <= board.size) {
			if(board.insert(number))
				playSound("Wrong.wav", "Conflicting number.");
			if(board.isSolved())
				playSound("Fiesta.wav", "Solved!");
		}
		else
			playSound("Wrong.wav", "Invalid number.");
		boardPanel.repaint();
	}

	/**
	 * CREATE COMMENT
	 */
	private void newClicked(int btnNum) {
		
	}
	
	private void newBoardMenu() {
		String[] options = new String[] {"4x4", "9x9","Cancel"};
	    int response = JOptionPane.showOptionDialog(null, "Quit the current game?\nSelect the board size.", "New Game",
	        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
	        null, options, options[0]);
	   switch(response) {
	   case 0:
			board = new Board(4);
			boardPanel.setBoard(board);
			boardPanel.repaint();
			break;
	   case 1:
		   board = new Board(9);
			boardPanel.setBoard(board);
			boardPanel.repaint();
			break;
	   }
	}

	/**
	 * Display the given string in the message bar.
	 * @param msg Message to be displayed.
	 */
	private void showMessage(String msg) {
		msgBar.setText(msg);
	}

	/** Configure the UI. */
	private void configureUI() {
		setIconImage(createImageIcon("sudoku.png").getImage());
		setLayout(new BorderLayout());

		JPanel buttons = makeControlPanel();
		// boarder: top, left, bottom, right
		buttons.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
		add(buttons, BorderLayout.NORTH);

		JPanel board = new JPanel();
		board.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
		board.setLayout(new GridLayout(1,1));
		board.add(boardPanel);
		add(board, BorderLayout.CENTER);

		msgBar.setBorder(BorderFactory.createEmptyBorder(10,16,10,0));
		add(msgBar, BorderLayout.SOUTH);
	}

	/** Create a control panel consisting of new and number buttons. */
	private JPanel makeControlPanel() {
		//menu
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("Game");
		menu.setMnemonic(KeyEvent.VK_G);
		menu.getAccessibleContext().setAccessibleDescription("Game menu");
		menuBar.add(menu);
		
		JMenuItem newGameItem = new JMenuItem("New Game", KeyEvent.VK_N);
		newGameItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ev) {
		    	newBoardMenu();
		    }
		});
	    menu.add(newGameItem);
	    
	    JMenuItem solveBoardItem = new JMenuItem("Solve Board", KeyEvent.VK_N);
	    solveBoardItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ev) {
	            board.solveBoard();
	            boardPanel.repaint();
		    }
		});
	    menu.add(solveBoardItem);
	    
	    //toolbar
	    JToolBar toolBar = new JToolBar("Sudoku");
	    JButton btn = new JButton(createImageIcon("playbutton.png"));
	    btn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ev) {
		    	newBoardMenu();
		    }
		});
	    btn.setToolTipText("Play a new game");
	    btn.setFocusPainted(false);
	    toolBar.add(btn);

	    /*
		JPanel newButtons = new JPanel(new FlowLayout());
		JButton menuButton = new JButton("1");
		JButton solvableButton = new JButton("2");
		for (JButton button: new JButton[] { menuButton, solvableButton }) {
			button.setFocusPainted(false);
			button.addActionListener(e -> {
				
			});
            button.setPreferredSize(new Dimension(70, 30));
			newButtons.add(button);
		}
		newButtons.setAlignmentX(LEFT_ALIGNMENT);
*/
		
		// buttons labeled 1, 2, ..., 9, and X.
		JPanel numberButtons = new JPanel(new FlowLayout());
		int maxNumber = board.size() + 1;
		for (int i = 1; i <= maxNumber; i++) {
			int number = i % maxNumber;
			JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
			button.setFocusPainted(false);
			button.setMargin(new Insets(0,2,0,2));
			button.addActionListener(e -> numberClicked(number));
			numberButtons.add(button);
		}
		numberButtons.setAlignmentX(LEFT_ALIGNMENT);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.add(toolBar);
		//content.add(newButtons);
		content.add(numberButtons);
		return content;
	}

	/** Create an image icon from the given image file. */
	private ImageIcon createImageIcon(String filename) {
		URL imageUrl = getClass().getResource(IMAGE_DIR + filename);
		if (imageUrl != null) {
			return new ImageIcon(imageUrl);
		}
		return null;
	}
	
	/**
	 * Plays sounds and display a message.
	 * 
	 * @param sound Name of the sound file.
	 * @param msg Message to be displayed.
	 */

	public void playSound(String sound, String msg) {
		new Thread(new Runnable() {
			public void run() {
				try {	
					Clip clip = AudioSystem.getClip();
					BufferedInputStream myStream = new BufferedInputStream(getClass().getResourceAsStream("/sound/" + sound)); 
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(myStream);
					clip.open(inputStream);
					clip.start();
					showMessage(msg); 
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			}).start();
		}

	public static void main(String[] args) {
		new SudokuDialog();
	}

}
