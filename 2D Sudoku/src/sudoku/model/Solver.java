package sudoku.model;

public interface Solver {
	
	/** Solves a sudoku board.
	 * 
	 * @return returns whether the board was solved or not.
	 */
	Board solve(Board b);
}

class S_Algorithm implements Solver{
	
	private Board b;
	
	private boolean solveRec() {
		int[][] sudoku = b.getArray();
		for(int x = 0; x < b.size; x++) {
			for(int y = 0; y < b.size; y++) {
				if(sudoku[x][y] == 0) {
					for(int i = 0; i < b.size; i++) {
						int value = (int) (Math.random() * b.size + 1);
						b.setX(x);
						b.setY(y);
						if(!b.insert(value)) {
							sudoku[x][y] = value;
							if(solveRec()) {
								return true;
							} else {
								sudoku[x][y] = 0;
							}
						}
					}
					return false;
				}
			}
		}
		return true;
}
	
	public Board solve(Board b) {
		this.b = b;
		solveRec();
		return this.b;
	}
}
