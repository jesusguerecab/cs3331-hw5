package sudoku.model;

import java.util.Random;

/** An abstraction of Sudoku puzzle. */
public class Board {

	Random r = new Random();

	/** Size of this board (number of columns/rows). */
	public final int size;

	private int[][] sudoku;
	private boolean[][] preFilled;

	/**currently selected board square*/
	private int x, y;

	/** Create a new board of the given size. */
	public Board(int size) {
		this.size = size;
		createBoard();
		fillBoard();
		x = y = -1;
	}

	/** Default constructor which sets the size of the board to 4 by default and creates the board. */ 
	public Board() {
		this(4);
	}

	/** Return the size of this board. */
	public int size() {
		return size;
	}

	/**  Return the array of this board. */
	public int[][] getArray() {
		return sudoku;
	}

	/** Creates an empty array with the given size of the board. */
	public void createBoard() {
		sudoku = new int[size][size];
		preFilled = new boolean[size][size];
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				sudoku[i][j] = 0;
				preFilled[i][j] = false;
			}
		}
	}

	/** Pre-fills board according to its size.*/
	public void fillBoard() {
		int ammount = (size == 9) ? 23 : 7;
		for(int i = 0; i < ammount; i++) {
			boolean valid = false;
			do {
				x = (int) (Math.random() * size);
				y = (int) (Math.random() * size);
				int value = (int) (Math.random() * size+1);
				if(sudoku[x][y] == 0) {
					if(insert(value)) {
						valid = true;
						preFilled[x][y] = true;
					}
				}
			} while (!valid);
		}
	}



	/** Checks if the sudoku board has been solved.
	 * 
	 * @return returns whether the board has been solved or not.
	 */
	public boolean isSolved() {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(sudoku[i][j] == 0)
					return false;
			}
		}
		return true;
	}

	/** Checks if number repeats on row and column.
	 * 
	 * @param number used to store the value that the user is trying to insert.
	 * @return would return a boolean that would specify if there is a value in the row or column.
	 */
	public boolean repeatsColumnRow(int number) {
		for(int i = 0; i < sudoku.length; i++) {
			if(sudoku[x][i] == number) {
				return true;
			}
		}
		for(int i = 0; i < sudoku.length; i++) {
			if(sudoku[i][y] == number) {
				return true;
			}
		}
		return false;
	}

	/** Will calculate the sub grid of a position.
	 * 
	 * @param x used to store the column from where the value is.
	 * @param y used to store the row from where the value is.
	 * @return would return the sub grid where the number is.
	 */
	private int getSquarePosition(int x, int y) {
		int square = 0;
		int sqrSize = (int) Math.sqrt(size);
		square = (int) (((y) / sqrSize) + ((x) / sqrSize) * sqrSize + 1);
		return square;
	}

	/** Obtains a position and a value and determines if there is an identical number already in the same square.
	 * 
	 * @param number used to store the number that the user is trying to insert.
	 * @return would return a boolean that would specify if there is already a value in the same square.
	 */
	public boolean repeatsOnSquare(int number) {
		int square = getSquarePosition(y, x);
		for(int i = 0; i < sudoku.length; i++) {
			for(int j = 0; j < sudoku.length; j++) {
				if(getSquarePosition(i, j) == square) {
					if(sudoku[j][i] == number) {
						//System.out.println(Debug: "repeats with " + i + ", " + j);
						return true;
					}
				}
			}
		}
		return false;
	}

	/** Checks if there's an error with inserting the value, if none would insert value.
	 * 
	 * @param number used to store the actual value of the new number.
	 * @return would return true if number wasn't allowed at position (x,y)
	 */
	public boolean insert(int number) {
		if(x == -1 || y == -1) return false; 
		if(number != 0)
			if(repeatsOnSquare(number) || repeatsColumnRow(number))
				return true;
		sudoku[x][y] = number;
		return false;
	}

	/** Getters and Setters*/ 
	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void clearPos() {
		x = y = -1;
	}
}