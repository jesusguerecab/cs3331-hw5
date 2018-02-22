package sudoku.model;

//JesusWazHere
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

}
