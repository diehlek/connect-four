
public class Board {
	private static String myBoard[][];
	private static final int ROW = 6;
	private static final int COL = 7;
	private static final int TO_WIN = 4;

	public Board(){
		System.out.println("New board created");
		myBoard = new String [ROW][COL];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				myBoard[i][j] = "";
			}
		}
	}

	public void setPlace(int row, int col, String value){
		myBoard[row][col] = value;
	}

	public static String getPlace(int row, int col) {
		return myBoard[row][col];
	}
	
	public static String[][] getBoard(){
		return myBoard;
	}

	//for the Observers, so when they join, the board shows up
	public void updateAll(){
	
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j < COL; j++){
				if(!myBoard[i][j].equals("")){
					int row = i+1;
					int col = j+1;
					String place = "p" +row  +col;
					Game.broadcastMessage("Server", "", place, myBoard[i][j]);
				}
			}
		}
	}
	//returns the next open spot on a given column for a user to mark
	//if the column is full, the user is told to make a new selection
	public String getNextOpenPlace(int givenCol, String color){
		int temp = givenCol-1;

		for(int i = 0; i < ROW; i++) {
			if(myBoard[i][temp].equals("")){
				myBoard[i][temp] = color;
				i++;
				return ("p"+i+givenCol);
			}
		}

		return "";
	}

	public boolean checkWin(String userColor){
		return (checkVert(userColor)||checkHor(userColor)||checkdiag(userColor));
	}

	//determines whether there is a full board.
	public boolean fullBoard() {
		int count = 0;
		for(int i = 0; i < ROW; i++){
			for (int j = 0; j < COL; j++) {
				if(!myBoard[i][j].equals("")){
					count++;
				}
			}
		}

		return (count == (ROW*COL));
	}

	//checks diagonal for a winner
	private static boolean checkdiag(String color) {
		boolean win = false;
		//up and right: 1-3, 1-4
		for(int row = 0; row < 3; row++){
			for (int col = 0; col < 4; col++){
				if(myBoard[row][col].equals(color) &&
						myBoard[row+1][col+1].equals(color) &&
						myBoard[row+2][col+2].equals(color) &&
						myBoard[row+3][col+3].equals(color)){
					win = true;
				}
			}
		}

		//down and right: 4-6, 1-4
		if (!win){
			for(int row = 3; row < 6; row++){
				for (int col = 0; col < 4; col++){
					if(myBoard[row][col].equals(color) &&
							myBoard[row-1][col+1].equals(color) &&
							myBoard[row-2][col+2].equals(color) &&
							myBoard[row-3][col+3].equals(color)){
						win = true;
					}
				}
			}
		}

		return win;
	}

	//checks for horizontal winner
	private static boolean checkHor(String color) {
		int count = 0;
		String cur;

		for(int i = 0; i < ROW; i++){
			for (int j = 0; j < COL; j++){
				cur = myBoard[i][j];

				if(cur.equals(color)){
					count++;

					if (count == TO_WIN){
						return true;
					}
				}else{
					count = 0;
				}
			}
		}
		return false;
	}

	//checks for vertical winner
	private static boolean checkVert(String color) {
		int count = 0;
		String cur;

		for(int i = 0; i < COL; i++){
			for (int j = 0; j < ROW; j++){
				cur = myBoard[j][i];

				if(cur.equals(color)){
					count++;

					if (count == TO_WIN){
						return true;
					}
				} else{
					count = 0;
				}
			}
		}
		return false;
	}
}
