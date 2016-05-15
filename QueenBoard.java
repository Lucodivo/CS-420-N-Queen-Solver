/*
Connor Haskins
Bronco ID# 010215400
May 14th, 2016
CS 420.01 w/ Dr. Fang Tang
 */

// Random is used to create an initial random board
import java.util.Random;

/*
This class creates a N-Queen board with N being specified in the BOARD_WIDTH constant
- You can use the empty constructor for a random board or send an array of ints for a specified board state
- The steepest hill climb algorithm is embedded in the class to find a local maximum
    - You may also restore to the initial state after the hill climb is performed
*/
class QueenBoard {

    // BOARD_WIDTH represents the N in N-Queen problem and the size of the board
    // N can be any number greater than 1
    public static final int BOARD_WIDTH = 18;
    // BOARD_SIZE is simply the number of squares on the board or N*N
    public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;

    // StringBuffer to hold the state of the PRINTED board
    // Not used for calculations
    private StringBuffer printedBoard;

    private BoardCoord[] qCoords;
    private int[] initIndex;

    // int to hold the current number of collisions
    int numCollisions;

    // CONSTRUCTORS
    /*
    Empty constructor:
    INPUT: void
    OUTPUT: random initial state QueenBoard w/ calculated collisions
    */
    public QueenBoard() {
        this.randomizeBoard();
    }

    /*
    Int array constructor:
    INPUT: int [] qPos - an array of all the queen positions
            int index in q pos represents column, int value represents row
    OUTPUT: random initial state QueenBoard w/ calculated collisions
    */
    public QueenBoard(int[] qPos) {
        this.specifyBoard(qPos);
    }

    /*
    function to set the QueenBoard with specific queen positions
    INPUT: int [] qPos - an array of all the queen positions
            int index in q pos represents column, int value represents row
    OUTPUT: void, sets specific coordinates of the queens on the QueenBoard
            and calculates the number of collisions
    */
    private void specifyBoard(int[] qPos) {
        // initialize the qCoords array
        this.qCoords = new BoardCoord[BOARD_WIDTH];
        // initialize the initial indexes array
        this.initIndex = qPos;

        // for every column
        for (int i = 0; i < BOARD_WIDTH; i++) {
            // create a new BoordCoord for the positions specified in qPos
            // and save them in qCoords
            this.qCoords[i] = new BoardCoord(i, initIndex[i]);
        }
        
        // calculate number of collisions
        this.calcNumCollisions();
    }

    /*
    INPUT: void
    OUTPUT: void, randomizes the queens coordinates on the QueenBoard and
            calculates the number of collisions
    */
    private void randomizeBoard() {
        // create a new Random object
        Random rand = new Random();
        // initialize the qCoords array
        this.qCoords = new BoardCoord[BOARD_WIDTH];
        // initialize the initial indexes array
        this.initIndex = new int[BOARD_WIDTH];

        // add queens at random row locations, one for each column
        // rand is seeded with time in main
        for (int i = 0; i < BOARD_WIDTH; i++) {
            // generate random number in the range [0,BOARD_SIZE), save in initIndex array
            this.initIndex[i] = rand.nextInt(BOARD_WIDTH);
            // create a random BoardCoord for each queen
            this.qCoords[i] = new BoardCoord(i, initIndex[i]);
        }

        // calculate number of collisions
        this.calcNumCollisions();
    }

    /*
    INPUT: int [] qPos - an array of all the queen positions
            int index in q pos represents column, int value represents row
    OUTPUT: void, sets the board with queens at specified locations
    */
    public void setBoard(int[] qPos) {
        // save thew new values in the initIndex
        this.initIndex = qPos;
        // for every column
        for (int i = 0; i < BOARD_WIDTH; i++) {
            // create a new BoordCoord for the positions specified in qPos
            // and save them in qCoords
            this.qCoords[i] = new BoardCoord(i, initIndex[i]);
        }
        
        // re-calculate the number of collisions
        this.calcNumCollisions();
    }

    /*
    function to restore initial state if one needs to return from the local maximum
    INPUT: void
    OUTPUT: void, restores the state using this.initIndex
    */
    public void restoreInitialState() {
        this.setBoard(this.initIndex);
    }

    /*
    INPUT: void
    OUTPUT: void, calculates the number of collisions that exist in the queen board
    */
    private void calcNumCollisions() {
        // set number of collisions to 0
        this.numCollisions = 0;
        // for every queen...
        for (int i = 0; i < BOARD_WIDTH; i++) {
            // calculate the number of collions
            this.qCoords[i].calcCollisions(qCoords);
            // add it to the total number of collisions on the board
            this.numCollisions += this.qCoords[i].getCollisions();
        }
        // a collision between A and B will be counted by both A AND B
        // divide total number of collisions by 2 to get exact value
        this.numCollisions /= 2;
    }

    /*
    function that perform a steepest hill climb to find the local maximum
    INPUT: void
    OUTPUT: void, the QueenBoard is transformed into a local maximum
    */
    public void steepestHillClimb() {
        // current improvements is the best possible move
        // initialize to -1 to enter the while loop
        int currentImprovements = -1;
        while (currentImprovements != 0) {
            // set the best possible move to the origin
            BoardCoord bestCoord = new BoardCoord(0, 0);
            // calculate the number of collisions this move would have
            bestCoord.calcCollisions(qCoords);
            // for every coordinate on the board...
            for (int i = 1; i < BOARD_SIZE; i++) {
                // initialize the coordinate
                BoardCoord bCoord = new BoardCoord(i % BOARD_WIDTH, i / BOARD_WIDTH);
                // calculate it's collisions
                bCoord.calcCollisions(qCoords);
                // if it is a better move than the current best move
                if (bCoord.getImprovement() > bestCoord.getImprovement()) {
                    // set this coordinate as the best possible move
                    bestCoord = bCoord;
                }
            }
            
            currentImprovements = bestCoord.getImprovement();
            
            // reduce total number of collisions by the improvement of the best move
            this.numCollisions -= currentImprovements;
            
            // use the best move by moving the queen
            this.qCoords[bestCoord.getX()].setYPos(bestCoord.getY());

            // CONSOLE OUTPUT CAN BE REMOVED. ONLY USED TO DEMONSTRATE THE ALGORITHM'S
            // CHOICES BY PRINTING TO CONSOLE.
            // if there are any improvements to be made
            if (currentImprovements != 0) {
                // tell user the best moves that are being made
                System.out.println("The steepest move is: Queen at (" + bestCoord.getX()
                        + "," + this.qCoords[bestCoord.getX()].getY() + ") to ("
                        + bestCoord.getX() + "," + bestCoord.getY() + ").");
            }
            else {
                // inform user the local min has been found
                System.out.println("Local min.");
            }

            // re-calculate all collisions for all queens
            for (int i = 0; i < BOARD_WIDTH; i++) {
                this.qCoords[i].calcCollisions(qCoords);
            }
        }
    }

    /*
    INPUT: void
    OUTPUT: boolean value representing if the current board is an optimal solution.
     */
    public boolean isOptimal() {
        // return true if there are no collisions
        return this.numCollisions == 0;
    }

    // GETTERS
    // get the initial indexes of the queen board
    public int[] getInitIndex() {
        return this.initIndex;
    }

    // get the number of collisions of the queen board
    public int getNumCollisions() {
        return this.numCollisions;
    }

    /*
    Only used to create a pretty board for the console
    INPUT: void
    output: void, called by printBoard() to help print an BOARD_WIDTH*BOARD*WIDTH
            size board that looks like a chess board.
    */
    private void createPrintedBoard() {
        this.printedBoard = new StringBuffer(BOARD_SIZE);

        // clear the current board
        // in order to handle the checkerboard, we need to determine if the board is even or odd length
        boolean isEven = (BOARD_WIDTH % 2) == 0;
        // if even
        if (isEven) {
            // create a toggle that will change the offset for each row
            boolean rowToggle = false;
            // for every row
            for (int i = 0; i < BOARD_SIZE; i += BOARD_WIDTH) {
                // if rowToggle == true, start with '%'
                if (rowToggle) {
                    // for every two columns
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        // use a percent symbol for black squares
                        this.printedBoard.append('%');
                        // increment index
                        j++;
                        // use a space for white squares
                        this.printedBoard.append(' ');
                    }
                }
                // if rowToggle == false, start with ' '
                else {
                    // for every two columns
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        // use a percent symbol for black squares
                        this.printedBoard.append(' ');
                        // increment index
                        j++;
                        // use a space for white squares
                        this.printedBoard.append('%');
                    }
                }
                // toggle the row toggle
                rowToggle = !rowToggle;
            }
        }

        // if odd, we can just choose '%' or ' ' depending on if the index is odd or even
        else {
            // for every square
            for (int i = 0; i < BOARD_SIZE; i++) {
                // if index is even...
                if (i % 2 == 0) {
                    // it is a white square
                    this.printedBoard.append(' ');
                }
                else {
                    // it is a black square
                    this.printedBoard.append('%');
                }
            }
        }

        // change all board squares with queens to the symbol 'Q'
        for (int i = 0; i < BOARD_WIDTH; i++) {
            this.printedBoard.setCharAt(i + (BOARD_WIDTH * this.qCoords[i].getY()), 'Q');
        }
    }

    /*
    INPUT: void
    OUTPUT: void, prints the QueenBoard to the console twice
            once with the queen values represented as 'Q'
            another with the queen values represented as # of collisions
    */
    public void printBoard() {
        // create the chess board with queens are corresponding positions
        this.createPrintedBoard();
        
        // create a top border for the board 
        char[] topBorder = new char[BOARD_WIDTH * 2 + 2];
        topBorder[BOARD_WIDTH * 2] = '\0';
        for (int i = 0; i < BOARD_WIDTH * 2 + 1; i++) {
            topBorder[i] = '_';
        }
        // print the top borders for each board representation, separated by spaces
        System.out.print(topBorder);
        System.out.print("   ");
        System.out.println(topBorder);
        
        // for every row...
        for (int i = 0; i < BOARD_SIZE; i += BOARD_WIDTH) {
            // print a wall creating the left border
            System.out.print("|");
            // for every column, print the values represented as '%',' ', and 'Q' 
            for (int j = 0; j < BOARD_WIDTH; j++) {
                System.out.print(this.printedBoard.charAt(i + j) + "|");
            }
            
            //separate the two boards with tildes and spaces
            System.out.print(" ~~ |");
            
            // for every column, print the values represented as '%',' ', and the
            // number of collisions for the queens
            for (int j = 0; j < BOARD_WIDTH; j++) {
                // if the current position to be printed is a queen
                if (this.printedBoard.charAt(i + j) == 'Q') {
                    // print the number of collisions that queen has
                    System.out.print(this.qCoords[j].getCollisions() + "|");
                }
                else {
                    // else just print the '%' or ' '
                    System.out.print(this.printedBoard.charAt(i + j) + "|");
                }
            }
            // crlf
            System.out.println();
        }
        // displace the total number of collisions
        System.out.println("Number of collisions: " + this.numCollisions);
    }
}
