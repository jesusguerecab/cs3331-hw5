package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** An abstraction of Sudoku puzzle. */
public class Board {

	/** Size of this board (number of columns/rows). */
	public final int size;

	private int[][] sudoku;
	private boolean[][] preFilled;

	/**currently selected board square*/
	private int x, y;

	/** Algorithm used to try and solve sudoku*/
	public Solver sAlgorithm;
	
	/** Create a new board of the given size. */
	public Board(int size) {
		this.size = size;
		createBoard();
		x = y = 0;
		x = y = -1;
		sAlgorithm = new S_Algorithm();
		partialFill();
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

	/** partially fills board according to its size.*/
	private void partialFill() {
		int amount = (size == 9) ?  23 : 8;
		tryToSolve();
		for(;amount >= 0;amount--) {
			do{
				x = (int) (Math.random() * size);
				y = (int) (Math.random() * size);
			}while(sudoku[x][y] == 0);
			preFilled[x][y] = true;
		}
		removeNotFilled();
	}
	
	/** Removes sudoku items not in pre filled*/
	private void removeNotFilled() {
		for(x = 0;x < size;x++)
			for(y = 0;y < size;y++)
				if(!preFilled[x][y])
					sudoku[x][y] = 0;
	}

	/* Checks if current sudoku board is solvable
	 * @return true if solvable
	 * */
	public boolean check() {
		int[][] temp = sAlgorithm.solve(this);
		if(temp != null) {return true;}
		return false;
	}
	
	/*
	 * @return false if couldn't solve
	 */
	public boolean tryToSolve() {
		int[][] temp = sAlgorithm.solve(this);
		if(temp != null) {
			sudoku = temp;
			return true;
		}
		return false;
	}
	
	/** Checks if the sudoku board has been solved.
	 * 
	 * @return returns whether the board has been solved or not.
	 */
	public boolean isSolved() {
		int total = (size == 9) ? 45 : 10;
		for(int i = 0; i < size; i++) {
			int sum = 0;
			for(int j = 0; j < size; j++) {
				sum += sudoku[i][j];
			}
			if(sum != total)
				return false;
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
					if(sudoku[j][i] == number)
						return true;
				}
			}
		}
		return false;
	}

	/** Checks if a position was filled by program or not.*/
	public boolean isPrefilled() {
		return preFilled[x][y];
	}

	/** Checks if there's an error with inserting the value, if none would insert value.
	 * 
	 * @param number used to store the actual value of the new number.
	 * @return would return true if number wasn't allowed at position (x,y)
	 */
	public boolean insert(int number) {
		if(x == -1 || y == -1) return false;
		if(isPrefilled()) 
			return true;
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
		//System.out.println("DEBUG: pos: (" + this.x + ", " + this.y +") prefilled: " + isPrefilled());
	}

	public int getY() {
		return y;
	}

	public void clearPos() {
		x = y = -1;
	}

	public boolean[][] getPreFill() {
		return preFilled;
	}
}