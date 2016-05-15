/*
Connor Haskins
Bronco ID# 010215400
May 14th, 2016
CS 420.01 w/ Dr. Fang Tang
 */

/*
this class is used to store information about coordinates on an N-Queen Board
It contains information about the horizontal, vertical, and diagonal positions,
as well as calculating collisions and potential improvements when given the set
of the current N-Queen positions

The main purpose of this class is to view all potential views from a current
N-Queen board state
*/
class BoardCoord {

    // x and y positions of the Board Coordinate
    // the upper left corner of the board is treated as the origin
    // with y increasing downward and x increasing rightward
    private int x;
    private int y;
    
    // there are 2N-1 different diagonal positions for both lower left to upper right
    // and lower right to upper left
    private int posDiagonal; // LL to UR
    private int negDiagonal; // LR to UL

    // number of collisions and potential improvement when compared to the current
    // queens position in the corresponding column.
    private int collisions;
    private int improvement;

    /*
    Constructor:
    INPUT: int x, y - the coordinates of a queen board
    OUTPUT: BoardCoord with calculated slopes and unknown collisions/improvements
    */
    BoardCoord(int x, int y) {
        // set the x and y coordinates to arguments passed
        this.x = x;
        this.y = y;
        // calculate the slopes of the coordinates
        this.createSlopes();
        // set the collisions to zero, in case they are accessed
        collisions = 0;
    }
    
    /*
    INPUT: int ypos - the y position that one wants to change
        *X POS CAN'T BE CHANGED DUE TO RULES OF N-QUEEN*
    OUTPUT: void, it simply changes the y position, updates the slopes,
            and sets the collisions back to unknown
    */
    public void setYPos(int yPos) {
        // change y position
        this.y = yPos;
        // recalculate the slopes
        this.createSlopes();
        // initialize the collisions to 0
        collisions = 0;
    }

    /*
    INPUT: void
    OUTPUT: void, updates the diagonal values of the BoardCoord using the current
            x and y positions
    */
    private void createSlopes() {
        /*
        assigning the posDiagonal value
        posDiagonal can be demonstrated using N = 4 and using the function x + y
            1 2 3 4
           ---------
        1 | 2 3 4 5
        2 | 3 4 5 6
        3 | 4 5 6 7
        4 | 5 6 7 8
        */
        posDiagonal = this.x + this.y;
        /*
        assigning the negDiagonal value
        negDiagonal can be demonstrated using N = 4 and using the function x - y
             1  2  3  4
           ---------
        1 |  0  1  2  3
        2 | -1  0  1  2
        3 | -2 -1  0  1
        4 | -3 -2 -1  0
        */
        negDiagonal = this.x - this.y;
    }

    /*
    INPUT: BoardCoord[] qCoords - this is the list of BoardCoords that represent
            the current positions of the queens on the QueenBoard
    OUTPUT: void, updates the collisions of the BoardCoord using the queen coordinates
            passed. Updates the improvements, meaning how much better the current
            coordinate is when compared to the Queen of the same column.
    */
    public void calcCollisions(BoardCoord[] qCoords) {
        // start the collisions at 0
        this.collisions = 0;
        // for every queen coordinate
        for (int i = 0; i < QueenBoard.BOARD_WIDTH; i++) {
            // if the this BoardCoord is on the same...
            if ((this.y == qCoords[i].y // horizontal line
                    || this.posDiagonal == qCoords[i].posDiagonal // positive diagonal line
                    || this.negDiagonal == qCoords[i].negDiagonal) // or negative diagonal line
                    && this.x != i) { // and it is not actually the same Coord of the queen
                // increase the number of collisions
                this.collisions++;
            }
        }
        // calculate the potential improvement of the move by comparing it to the
        // number of collisions of the queen in the same column
        this.improvement = qCoords[this.x].getCollisions() - this.collisions;
    }

    //GETTERS
    // get private x value
    public int getX() {
        return this.x;
    }

    // get private y value
    public int getY() {
        return this.y;
    }

    // get collisions
    public int getCollisions() {
        return this.collisions;
    }

    // get improvements
    public int getImprovement() {
        return this.improvement;
    }
}
