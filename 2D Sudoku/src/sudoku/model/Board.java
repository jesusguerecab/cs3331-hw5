package sudoku.model;

/** An abstraction of Sudoku puzzle. */
public class Board {

	/** Size of this board (number of columns/rows). */
	public final int size;

	private int[][] sudoku;
	private boolean[][] preFilled;
	private int[][] copy;

	/**currently selected board square*/
	private int x, y;

	/** Algorithm used to try and solve sudoku*/
	public Solver sAlgorithm;

	public Board(int size, int[] in) {
		int n;
		this.size = size;
		createBoard();
		sAlgorithm = new S_Algorithm();
		for(int i = 0;i < in.length;i+=3) {
			x = in[i];
			y = in[i+1];
			n = in[i+2];
			
			sudoku[x][y] = n;
			preFilled[x][y] = true;
		}
			
	}
	
	/** Create a new board of the given size. */
	public Board(int size) {
		this.size = size;
		createBoard();
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

	/** Creates and returns a copy of the sudoku array. */
	public int[][] makeSudokuCopy(){
		copy = new int[size][size];
		for(int _x = 0;_x < size;_x++)
			for(int _y = 0;_y < size;_y++)
				copy[_x][_y] = sudoku[_x][_y];
		return copy;
	}

	/** 
	 * Solves a sudoku work by applying backtracking to a copy of current sudoku board values.
	 * 
	 * @return Returns whether a sudoku board is solvable or not.
	 */
	public boolean isSolvable() {
		for(int x = 0; x < size; x++)
			for(int y = 0; y < size; y++)
				if(copy[x][y] == 0) {
					for(int i = 0; i < size; i++) {
						int value = (int) (Math.random() * size + 1);
						setX(x);
						setY(y);
						if(insert(value, 2)) {
							copy[x][y] = value;
							if(isSolvable())
								return true;
							else
								copy[x][y] = 0;
						}
					}
					return false;
				}
		return true;
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

	/** Partially fills board according to its size.*/
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

	/** Checks if current sudoku board is solvable
	 * @return true if solvable
	 * */
	public boolean check() {
		int[][] temp = sAlgorithm.solve(this);
		if(temp != null)
			return true;
		return false;
	}

	/**
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

	/** Checks if a position was filled by program or not.
	 * 
	 * @return would return true if position was prefilled.
	 */
	public boolean isPrefilled() {
		return preFilled[x][y];
	}
	
	/** Checks if there's an error with inserting the value, if none would insert value.
	 * 
	 * @param number used to store the actual value of the new number.
	 * @return would return true if number wasn't allowed at position (x,y)
	 */
	public boolean insert(int number, int option) {
		boolean can = canInsert(number);
		if(can) {
			if(option == 1)
				sudoku[x][y] = number;
			else
				copy[x][y] = number;
		}
		return can;
	}
	
	public boolean insert(int x, int y, int n) {
		this.x = x;
		this.y = y;
		return insert(n,1);
	}

	/** Checks if there's an error with inserting the value
	 * 
	 * @param number used to store the actual value of the new number.
	 * @return would return true if number wasn't allowed at position (x,y)
	 */
	public boolean canInsert(int number) {
		if(x == -1 || y == -1) return false;
		if(isPrefilled()) 
			return false;
		if(number != 0)
			if(repeatsOnSquare(number) || repeatsColumnRow(number))
				return false;
		return true;
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

	public boolean[][] getPreFill() {
		return preFilled;
	}
}