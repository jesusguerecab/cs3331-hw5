package sudoku.model;

public interface Solver {

	/** Solves a sudoku board.
	 * 
	 * @return returns whether the board was solved or not.
	 */
	int[][] solve(Board b);
}

class S_Algorithm implements Solver{

	private Board board;

	/**
	 * Would solve method recursively and return true if it was able or false otherwise.
	 * 
	 * @return boolean Returns whether sudoku board was solvable or not.
	 */
	private boolean solveRec() {
		int[][] sudoku = board.getArray();
		for(int x = 0; x < board.size; x++)
			for(int y = 0; y < board.size; y++)
				if(sudoku[x][y] == 0) {
					for(int i = 0; i < board.size; i++) {
						int value = (int) (Math.random() * board.size + 1);
						board.setX(x);
						board.setY(y);
						if(!board.insert(value)) {
							sudoku[x][y] = value;
							if(solveRec())
								return true;
							else
								sudoku[x][y] = 0;
						}
					}
					return false;
				}
		return true;
	}

	/**
	 * Will solve a sudoku board and return the array.
	 * 
	 * @param b board where sudoku is.
	 * @return int[][] a solved sudoku 2d array.
	 */
	public int[][] solve(Board b) {
		board = b;
		boolean solvable = solveRec();
		return solvable?board.getArray():null;
	}
}
