package sudoku.model;

/** An abstraction of Sudoku puzzle. */
public class Board {

	/** Size of this board (number of columns/rows). */
	public final int size;
	
	private int[][] sudoku;
	
	/**currently selected board square*/
	private int x, y;
	
	/** Create a new board of the given size. */
	public Board(int size) {
		this.size = size;
		createBoard();
	}

	/*
	 * Default constructor which sets the size of the board to 4 by default and creates the board.
	 */ 
	public Board() {
		this(4);
		createBoard();
	}

	/** Return the size of this board. */
	public int size() {
		return size;
	}

	/*
	 *  Return the array of this board. 
	 */
	public int[][] getArray() {
		return sudoku;
	}

	/*
	 * This method creates an empty array with the given size of the board. 
	 * It will go trough every index and set the values with 0.
	 */

	public void createBoard() {
		sudoku = new int[size][size];
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				sudoku[i][j] = 0;
			}
		}
	}

	/*
	 * This method checks if the sudoku board has been solved. 
	 * It will go trough every value and detect if there are still values with 0.
	 * If no 0 have been found, then the board has been completed.
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

	/*
	 * This method would check if there is already a similar number in
	 * the row or column that the user is trying to insert it's value.
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

	/*
	 * This method will calculate the sub grid of a position.
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

	/*
	 * This method would obtain a position and a value and define if there is an identical number already in the same square.
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
						System.out.println("repeats with " + i + ", " + j);
						return true;
					}
				}
			}
		}
		return false;
	}


	/*
	 * This method is going to check if the position where the user is trying to insert the value is empty.
	 * 
	 * @param x column of the its position
	 * @param y row of its position
	 * @return would return a boolean that would specify if the position is empty.
	 */
	public boolean isEmpty(int x, int y) {
		if(sudoku[x][y] != 0) 
			return false;
		return true;
	}

	/*
	 * This method would check if there is an error with inserting the value
	 * to the board and if not insert a value into the sudoku board which is 
	 * a 2d array.
	 * 
	 * @param number used to store the actual value of the new number.
	 * @return would return true if number wasn't allowed at position (x,y)
	 */
	 public boolean insert(int number) {
		 if(number != 0)
			 if(repeatsOnSquare(number) || repeatsColumnRow(number))
				 return true;
		 sudoku[x][y] = number;
		 return false;
	 }

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

}
