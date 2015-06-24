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

 File must be input in this order:
 # OF GRIDS
 GRID SIZE (INTEGER VALUE, WILL BE NxN)
 GRID
 ...
 GRID
 # WORDS
 WORDS
 ...
 WORDS
 GRID SIZE (INTEGER VALUE, WILL BE NxN)
 ... REPEAT

 */

public class SearchWordStandard {

    // global variables that can be accessed by all methods and from outside the scope of this class
    public static boolean CORRECT = false;
    public static String[][] wordGrid;

    // 1:1 of i:c movement in both arrays, meaning that visitOffsetsI[0] and visitOffsetsC[0] would be one movement
    public static int[] visitOffsetsI = {1, -1, 0, 0, 1, -1, 1, -1};
    public static int[] visitOffsetsC = {0, 0, 1, -1, -1, -1, 1, 1};
    // D, U, R, L, DLD, DLU, DRD, DRU
    // DOWN, UP, RIGHT, LEFT, DIAGONAL-LEFT-DOWN, DIAGONAL-LEFT-UP, DIAGONAL-LEFT-UP, DIAGONAL-RIGHT-DOWN, DIAGONAL-RIGHT-UP

    // constructor makes a new global grid for all methods to access when class is created, therefore one instance of class can only search for a set of letters once
    SearchWordStandard(String[] grid) {
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

            SearchWordStandard search = new SearchWordStandard(grid); // instantiates a new class to be used for all searches

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
                                search.wordSearch(y, z, finalWord, search.getArrayCopy(search.wordGrid));

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

