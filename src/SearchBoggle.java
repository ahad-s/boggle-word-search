import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**

 * NOTES:
 :::use the key below for movement:::
 can_visit = [i+1][c], [i-1][c], --> vertical
 [i][c+1], [i][c-1], --> horizontal
 [i+1][c-1], [i-1][c-1] --> diagonal left
 [i+1][c+1], [i-1][c+1] --> diagonal right
 :::in most of the program, the letter "i" represents rows, and the letter "c" represents columns:::
 */

public class SearchBoggle {

    // global variables that can be accessed by all methods and from outside the scope of this class
    public static boolean CORRECT = false;
    public static String[][] wordGrid;

    // 1:1 of i:c movement in both arrays, meaning that visitOffsetsI[0] and visitOffsetsC[0] would be one movement
    public static int[] visitOffsetsI = {1, -1, 0, 0, 1, -1, 1, -1};
    public static int[] visitOffsetsC = {0, 0, 1, -1, -1, -1, 1, 1};
    // D, U, R, L, DLD, DLU, DRD, DRU
    // DOWN, UP, RIGHT, LEFT, DIAGONAL-LEFT-DOWN, DIAGONAL-LEFT-UP, DIAGONAL-LEFT-UP, DIAGONAL-RIGHT-DOWN, DIAGONAL-RIGHT-UP

    // constructor makes a new global grid for all methods to access when class is created, therefore one instance of class can only search for a set of letters once
    SearchBoggle(String[] grid) {
	wordGrid = get2DGrid(grid);
    }


    // returns a copy of given 2D array since there are no methods to do this in Java already
    public static String[][] getArrayCopy(String[][] array) // this is to copy 2D arrays instead of changing pointers
    {
	String[][] tempArray = new String[array.length][array[0].length];

	for (int i = 0; i < array.length; i++)
	{
//            tempArray[i] = Arrays.copyOf(array[i], array[i].length); // Ready's JDK is too old to use this :(
	    System.arraycopy(array[i], 0, tempArray[i], 0, array.length);
	}
	return tempArray;
    }

    // this runs wordSearchStart() 8 times, one for each direction given by visitOffsetsC and visitOffsetsI
    public static void wordSearch(int i, int c, String word, String[][] localGrid)
    {
	// goes through each possible move and calls wordSearch on it (8 possible moves)
	for (int x = 0; x < visitOffsetsI.length; x++)
	{
	    boolean FOUND = wordSearchStart(i, c, word, localGrid, visitOffsetsI[x], visitOffsetsC[x]);
	    if (FOUND){
		CORRECT = true;
		break;
	    }
	}
    }


    static boolean onceReset = true; // this makes sure that the first character is not updated unintentionally in local grid

    // BOGGLE SEARCH! SEARCHES IN EVERY DIRECTION FOR EACH SEARCH
    // parameters are rows, colums, the word being searched, and a temporary local grid
    public static boolean boggleSearch(int i, int c, String word, String[][] localGrid) {

        // these booleans make sure that global grid is not updated unintentionally
        boolean onceU = false;
        boolean onceD = false;
        boolean onceR = false;
        boolean onceL = false;
        boolean onceDLD = false;
        boolean onceDRD = false;
        boolean onceDRU = false;
        boolean onceDLU = false;

        word = word.substring(1, word.length()); // for each call, the first letter of the word is popped off

        // the base case, returns true if all letters in word have been popped off, meaning that word is found
        if (word.length() == 0) {
            localGrid[i][c] = "0"; // the very last visited letter must be replaced with "0" to indicate that it's "gone"
            CORRECT = true; // public global variable is true, it's accessed outside the class for prompt
            return true; // this true value shows when the word is found
        }

        // one statement is just repeated 7 times for all other possible moves in this method (i.e. right, left, etc.), all begin with a try statement

        // try statement just ends the call when movement is out of board, implies that word won't be found
        try {
            if (!localGrid[i - 1][c].equals("0") && localGrid[i - 1][c].charAt(0) == word.charAt(0)) { // confirm that next space has not been visited already and that it's the next letter of word that we are searching
                onceU = true; // this is needed as a check so that grid does not reset every time this try statement is called without moving on to inside the if statement
                localGrid[i][c] = "0"; // last checked location is set to "0" to mark is as "visited", so you can't cross over boards
                boolean temp = boggleSearch(i - 1, c, word, localGrid); // this is saved as a variable so that if call fails, it doesn't exit, but rather continues in place to next possible move
                if (temp)
                    return true; // if word is found, return/end the method
                else if (!temp && !onceReset) // the else if and else make sure that grid is updated when the recursive call 'backtracks'
                    localGrid = getArrayCopy(wordGrid); // if board has reached the end and has not found word, reset the board to last known setting
                else
                    onceReset = false;
            }

        } catch (IndexOutOfBoundsException e) { // this is called if the next letter to search is outside of grid
            if (onceU) { // if the call reached inside the if statement, it means something was potentially updated from this statement yet failed to finish, therefore must return grid back to previous state
                localGrid = getArrayCopy(wordGrid); // if board has reached the end and has not found word, reset the board to last known setting
            }
        }



        try {
            if (!localGrid[i + 1][c].equals("0") && localGrid[i + 1][c].charAt(0) == word.charAt(0)) { // down
                onceD = true;
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i + 1, c, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset){
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }

        } catch (IndexOutOfBoundsException e) {
            if (onceD)
                localGrid = getArrayCopy(wordGrid);
        }


        try {
            if (!localGrid[i][c + 1].equals("0") && localGrid[i][c + 1].charAt(0) == word.charAt(0)) { // right
                onceR = true;
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i, c + 1, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset)
                {
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }

        } catch (IndexOutOfBoundsException e) {
            if (onceR)
                localGrid = getArrayCopy(wordGrid);
        }

        try {
            if (!localGrid[i][c - 1].equals("0") && localGrid[i][c - 1].charAt(0) == word.charAt(0)) { // left
                onceL = true;
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i, c - 1, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset)
                {
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }

        } catch (IndexOutOfBoundsException e) {
            if (onceL)
                localGrid = getArrayCopy(wordGrid);
        }

        try {
            if (!localGrid[i + 1][c + 1].equals("0") && localGrid[i + 1][c + 1].charAt(0) == word.charAt(0)) { // diag right down
                onceDRD = true;
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i + 1, c + 1, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset)
                {
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }

        } catch (IndexOutOfBoundsException e) {
            if (onceDRD)
                localGrid = getArrayCopy(wordGrid);
        }




        try {
            if (!localGrid[i - 1][c + 1].equals("0") && localGrid[i - 1][c + 1].charAt(0) == word.charAt(0)) { // diag right up
                onceDRU = true;
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i - 1, c + 1, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset)
                {
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }

        } catch (IndexOutOfBoundsException e) {
            if (onceDRU)
                localGrid = getArrayCopy(wordGrid);
        }

        try {
            if (!localGrid[i - 1][c - 1].equals("0") && localGrid[i - 1][c - 1].charAt(0) == word.charAt(0)) { // diag left up
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i - 1, c - 1, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset)
                {
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }
            else{
            }
        } catch (IndexOutOfBoundsException e) { // no need to reset localGrid since if the statement reaches here, then it shouldn't matter anyway since no new recursive call
            if (onceDLU)
                localGrid = getArrayCopy(wordGrid);
        }

        try {
            if (!localGrid[i + 1][c - 1].equals("0") && localGrid[i + 1][c - 1].charAt(0) == word.charAt(0)) { // diag left down
                onceDLD = true;
                localGrid[i][c] = "0";
                boolean temp = boggleSearch(i + 1, c - 1, word, localGrid);
                if (temp)
                    return true;
                else if (!temp && !onceReset)
                {
                    localGrid[i][c] = wordGrid[i][c];
                }
                else
                    onceReset = false;
            }
        } catch (IndexOutOfBoundsException e) {
            if (onceDLD)
                localGrid = getArrayCopy(wordGrid);
        }


        return false;

    }

    // WORD SEARCH! SEARCHES IN ONE DIRECTION UNTIL WORD IS FOUND OR DOES NOT EXIST
    // similar logic to boggleSearch(), but this one goes over a single move (moveI, moveC) until that move's call ends
    // moveI and moveC determine the direction in which the search goes, and it searches in each direction at least once
    public static boolean wordSearchStart(int i, int c, String word, String[][] localGrid, int moveI, int moveC){
	word = word.substring(1, word.length());

	// base case, similar to boggleSearch()
	if (word.length() == 0){
	    localGrid[i][c] = "0";
	    return true;
	}

	try {
	    if (!localGrid[i + moveI][c + moveC].equals("0") && localGrid[i + moveI][c + moveC].charAt(0) == word.charAt(0)) {
		localGrid[i][c] = "0";
		return wordSearchStart(i + moveI, c + moveC, word, localGrid, moveI, moveC);
	    }

	} catch (IndexOutOfBoundsException e) { // if board has reached the end and has not found word, just continue and move on to next iteration of for loop
	    // no need to reset localGrid since a "fresh" copy will automatically be called in next iteration
	}

	return false;
    }

    // returns 2D version of a grid with a string of length 1 of each element
    public static String[][] get2DGrid (String[] grid)
    {
	String[][] finalGrid = new String[grid.length][grid[0].length()];
	for (int i = 0; i < grid.length; i++) {
	    for (int c = 0; c < grid[0].length(); c++) {
		finalGrid[i][c] = Character.toString(grid[i].charAt(c));
	    }
	}
	return finalGrid;
    }

    // prints the grid one row at a time
    public static void printGrid(String[][] wordGrid)
    {
	for (int i = 0; i < wordGrid.length; i++)
	{
	    StringBuffer word = new StringBuffer();
	    for (int j = 0; j < wordGrid[0].length; j++) {
		word.append(wordGrid[i][j]);
	    }
	    System.out.println(word);
	}
	System.out.println();
    }


    public static void main(String[] args) throws IOException {

	// input from file
	BufferedReader br = new BufferedReader(new FileReader("input.txt"));

	// gets total number of grids
	int numGrids = Integer.parseInt(br.readLine());

	for (int c = 0; c < numGrids; c++) { // iterates through all possible grids

	    // the following statements gather basic data from input file such as size, words, etc. for each grid

	    int sizeGrid = Integer.parseInt(br.readLine());
	    String[] grid = new String[sizeGrid];

	    for (int i = 0; i < sizeGrid; i++) {
		grid[i] = br.readLine();
	    }

	    int numWordsToSearch = Integer.parseInt(br.readLine());
	    String[] wordsToSearch = new String[numWordsToSearch];


	    for (int i = 0; i < numWordsToSearch; i++) {
		wordsToSearch[i] = br.readLine();
	    }

	    SearchBoggle search = new SearchBoggle(grid); // instantiates a new class to be used for all searches

	    System.out.println("Grid #"  + (c+1) + ":");
	    search.printGrid(search.wordGrid);


	    for (int x = 0; x < wordsToSearch.length; x++) { // goes through all list of words to find

		// variables defined to be used by search methods, these should stay the same until the end
		String finalWord = wordsToSearch[x]; // word that is currently being searched for in crossword
		final String[][] gridToTest = getArrayCopy(search.wordGrid); // default grid in case the search doesn't return FOUND
		boolean FOUND = false;

//                System.out.println("Looking for..." + finalWord); // uncomment to allow for more prompting

		for (int y = 0; y < gridToTest.length; y++) { // goes through all possible rows

		    for (int z = 0; z < gridToTest[0].length; z++) { // goes through all possible columns

			if (search.wordGrid[y][z].equals(Character.toString(finalWord.charAt(0)))) { // if current letter is the first letter of the word, this lowers inefficiency
			    try {
                    String[][] tempGrid = search.getArrayCopy(search.wordGrid); // the array copy must be held in this variable so it can be edited before being passed
                    tempGrid[y][z] = "0";
                    search.boggleSearch(y, z, finalWord, search.getArrayCopy(tempGrid));
                    search.onceReset = true;

			    } catch (Exception e) { // should never run since all important exceptions are caught earlier on
//                                e.printStackTrace(); // uncomment for debugging
			    }

			    if (search.CORRECT) { // breaks out of loop if word is found
				break;
			    }
			}
		    }

		    // exits loop and moves on to next word when current word is found or if current word cannot be found on board
		    if (search.CORRECT) {
			System.out.println(finalWord + " was FOUND!");
			search.CORRECT = false;
			FOUND = true;
			break;
		    }
		}
		// if word has not been found after searching all of grid, then move on to next word after prompt
		if (!FOUND) {
		    System.out.println(finalWord + " was NOT FOUND!");
		}

	    }

	    System.out.println(); // formatting

	}


    }
}

