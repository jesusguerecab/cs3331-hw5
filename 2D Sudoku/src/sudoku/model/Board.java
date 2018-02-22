package sudoku.model;

/** An abstraction of Sudoku puzzle. */
public class Board {

	/** Size of this board (number of columns/rows). */
	public final int size;
	private int[][] sudoku;

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
	
	private void createBoard() {
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
	 * This method would check if there is already a similar value in
	 * the row or column that the user is trying to insert it's value.
	 * 
	 * @param col used to store the column from where the value is.
	 * @param row used to store the row from where the value is.
	 * @param value used to store the value that the user is trying to insert.
	 * @return would return a boolean that would specify if there is a value in the row or column.
	 */
	public boolean repeatsColumnRow(int col, int row, int value) {
		for(int i = 0; i < sudoku.length; i++) {
			if(sudoku[row-1][i] == value) {
				return true;
			}
		}
		for(int i = 0; i < sudoku.length; i++) {
			if(sudoku[i][col-1] == value) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * This method its going to take a position and return the square which that position is from.
	 * 
	 * @param col index of the column where the position is from.
	 * @param row index of the row where the position is from.
	 * @return Integer that defines the square where the position is from.
	 */
	@SuppressWarnings("unused")
	private int getSquarePosition(int col, int row) {
		int square = 0;
		if(size == 4) {
			if(col <= 2) {
				if(row <= 2) {
					square = 1;
				} else {
					square = 3;
				}
			} else {
				if(row <= 2) {
					square = 2;
				} else {
					square = 4;
				}
			}
		} else if(size == 9) {
			if(col <= 3) {
				if(row <= 3) {
					square = 1;
				}else if(row > 3 && row <= 6) {
					square = 4;
				}else if(row > 6) {
					square = 7;
				}
			}else if(col > 3 && col <= 6) {
				if(row <= 3) {
					square = 2;
				}else if(row > 3 && row <= 6) {
					square = 5;
				}else if(row > 6) {
					square = 8;
				}
			}else if(col > 6) {
				if(row <= 3) {
					square = 3;
				}else if(row > 3 && row <= 6) {
					square = 6;
				}else if(row > 6) {
					square = 9;
				}
			}
		}
		return square;
	}
	
	/*
	 * This method would obtain a position and a value and define if there is an identical value already in the same square.
	 * 
	 * @param col used to store the column from where the value is.
	 * @param row used to store the row from where the value is.
	 * @param value used to store the value that the user is trying to insert.
	 * @return would return a boolean that would specify if there is already a value in the same square.
	 */
	public boolean repeatsOnSquare(int col, int row, int value) {
		int square = getSquarePosition(col, row);
		for(int i = 1; i <= sudoku.length; i++) {
			for(int j = 1; j <= sudoku.length; j++) {
				if(getSquarePosition(i, j) == square) {
					if(sudoku[j-1][i-1] == value) {
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
	 * @param col used to store the column from where the value is.
	 * @param row used to store the row from where the value is.
	 * @param value used to store the value that the user is trying to insert.
	 * @return would return a boolean that would specify if the position is empty.
	 */
	public boolean isEmpty(int col, int row, int v) {
		if(sudoku[row-1][col-1] != 0) 
			return false;
		return true;
	}

	/*
	 * This method would insert a value into the sudoku board which is a 2d array.
	 * 
	 * @param col used to store the column for the new value.
	 * @param row used to store the row for the new value.
	 * @param value used to store the actual value of the new value.
	 */
	public void insert(int col, int row, int value) {
		sudoku[row-1][col-1] = value;
	}

}
