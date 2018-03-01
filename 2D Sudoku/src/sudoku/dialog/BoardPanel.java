package sudoku.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import sudoku.model.Board;

/**
 * A special panel class to display a Sudoku board modeled by the
 * {@link sudoku.model.Board} class. You need to write code for
 * the paint() method.
 *
 * @see sudoku.model.Board
 * @author Yoonsik Cheon
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
	
	public interface ClickListener {

		/** Callback to notify clicking of a square. 
		 * 
		 * @param x 0-based column index of the clicked square
		 * @param y 0-based row index of the clicked square
		 * @return 
		 */
		void clicked(int x, int y);
	}

    /** Background color of the board. */
	private static final Color boardColor = new Color(175, 250, 250);
	
	/** Color of selected square. */
	private static final Color squareColor = new Color(250, 175, 175);

	/** Board to be displayed. */
	private Board board;

	/** Width and height of a square in pixels. */
	private int squareSize;

	/** Create a new board panel to display the given board. */
	public BoardPanel(Board board, ClickListener listener) {
		this.board = board;
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int xy = locateSquaree(e.getX(), e.getY());
				if (xy >= 0) {
					listener.clicked(xy / 100, xy % 100);
				}
			}
		});
	}
	
    /** Set the board to be displayed. */
    public void setBoard(Board board) {
    	this.board = board;
    }
    
    /**
     * Given a screen coordinate, return the indexes of the corresponding square
     * or -1 if there is no square.
     * The indexes are encoded and returned as x*100 + y, 
     * where x and y are 0-based column/row indexes.
     */
    private int locateSquaree(int x, int y) {
    	if (x < 0 || x > board.size * squareSize
    			|| y < 0 || y > board.size * squareSize) {
    		return -1;
    	}
    	int xx = x / squareSize;
    	int yy = y / squareSize;
    	return xx * 100 + yy;
    }
    
    /** Column Index of square to be highlighted*/
    private Integer x = -1;
    
    /** Row Index of square to be highlighted*/
    private Integer y = -1;
    
    /** Given a screen coordinate, updates/initializes private values*/
    public void selectRect(int x, int y) {
    	if(this.x == x && this.y == y) 
    		this.x = this.y = -1;
    	else {
    		this.x = x;
			this.y = y;
    	}
    }
    
	/** Update board with values*/
	public void updateValues(Graphics g) {
		int[][] sudoku = board.getArray();
		int centerX = (board.size==9)?12:30;
		int centerY = (board.size==9)?20:30;
        for(int i = 0;i < board.size;i++)
            for(int j = 0;j < board.size;j++)
            	if(sudoku[i][j] != 0)
            		g.drawString(String.valueOf(sudoku[i][j]), i * squareSize + centerX, j * squareSize + centerY);
	}
    
    /** Draws the lines that separate the squares on the board*/
    private void drawGridLines(Graphics g) {
    	int sqrtSize = (int) Math.sqrt(board.size);
        g.setColor(Color.GRAY);
        for(int i = 0;i < board.size;i++) {
            g.fillRect(i*squareSize, 0, 1, board.size * squareSize);
            g.fillRect(0, i*squareSize, board.size * squareSize, 1);
        }
        for(int i = 0;i < board.size;i++) {
	        g.setColor(Color.BLACK);
	        g.fillRect(sqrtSize*i*squareSize, 0, 1, board.size * squareSize);
	        g.fillRect(0, sqrtSize*i*squareSize, board.size * squareSize, 1);
        }
    }
    
	/** Draw the associated board. */
	@Override
	public void paint(Graphics g) {
		super.paint(g); 

		// determine the square size
		Dimension dim = getSize();
		squareSize = Math.min(dim.width, dim.height) / board.size;

		// draw background
        final Color oldColor = g.getColor();
        g.setColor(boardColor);
        g.fillRect(0, 0, squareSize * board.size, squareSize * board.size);
        if(x != -1 && y != -1) {
	        g.setColor(squareColor);
	        g.fillRect(x * squareSize + 2, y * squareSize + 2, squareSize - 3, squareSize - 3);
        }
        drawGridLines(g);
        updateValues(g);
    }
}
