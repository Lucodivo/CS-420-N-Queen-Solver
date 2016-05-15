/*
Connor Haskins
Bronco ID# 010215400
May 14th, 2016
CS 420.01 w/ Dr. Fang Tang
 */

// random used for both the crossovers and mutations of the genetic algorithm
import java.util.Random;

/*
GeneticQueenBoards solves the N-Queen problem by using a genetic algorithm.
A random population is created. The two fittest of the population are chosen.
Their "genes" (Queen coordinates in this case) are randomly spliced to produce 
new "chromosomes" (Queen coordinates) for the next generation.
The next generation is used as the new population and the cycle continues until
and optimal solution is found.
User may specify the rate of mutations, 
the POPULATION_SIZE of each generation can be changed in this file as well.
*/
class GeneticQueenBoards {

    // an array of QueenBoards representing the current population of the
    // genetic populations
    private QueenBoard[] qBoards;

    // queen boards that are the best and second best in terms of collisions
    // they will be used to produce the offspring of the next generation(population)
    private QueenBoard bestBoard;
    private QueenBoard secondBestBoard;

    // a 2D array to form the tournament tree
    // the tournament tree is used to get bestBoard and secondBestBoard
    // in N+log N time
    private QueenBoard[][] tournamentTree;
    // keep track of the depth of the tournament tree
    private final int treeDepth;

    // a value that should be between 0.000 to 1.000,
    // ex: .652 means mutations will happen for 652 of 1000 offspring
    private final float mutationFrequency;

    // the population size is used for the random population that seeds the
    // genetic algorithm
    // POPULATION_SIZE MUST BE EQUAL TO 2^i, WHERE i IS ANY INTEGER
    public static final int POPULATION_SIZE = 16;

    // value to allow the user to see all tournamentTrees of every generation
    // before the optimal solution is found
    private boolean printPopulationTrees;
    // keep track of the number of generations to reach goal state
    private int numGens;

    // Random to be used for crossover and mutations
    private Random rand;

    /*
    Frequency constructor
    INPUT: float mFreq - represents the rate of mutation. Values can vary between
        .0000 and 1.000, the precision is only up to the thousandths place (10^-3)
    OUTPUT: a GeneticQueenBoards object that will initially hold a random population
            of QueenBoards. Calling geneticSearch() on the object will give an 
            optimal solution for the N-Queen problem
    */
    public GeneticQueenBoards(float mFreq) {
        // save the designated frequency of mutations
        this.mutationFrequency = mFreq;

        // initialize the Random object for mutation and crossover
        this.rand = new Random();
        
        // initialize number of generations to 0
        this.numGens = 0;

        // depth = log2(# of elements) = loge(# of elements) / loge(2)
        double dTreeDepth = Math.log(POPULATION_SIZE) / Math.log(2);
        
        // actual tree depth will be logn + 1
        this.treeDepth = (int)dTreeDepth + 1;
        
        // create a new tournament tree with the tree depth specified before
        this.tournamentTree = new QueenBoard[this.treeDepth][];

        // initialize the QueenBoards to an array of POPULATION_SIZE
        qBoards = new QueenBoard[POPULATION_SIZE];
        // create each QueenBoard as a random QueenBoard
        for (int i = 0; i < POPULATION_SIZE; i++) {
            qBoards[i] = new QueenBoard();
        }

        // set the printing of the population trees to false
        // user can set to true, and the tournamentTree of each generation
        // will print [IT'S USUALLY A LOT OF TREES!]
        this.printPopulationTrees = false;
    }

    /*
    There is where the most of the genetic algorithm starts, we grab the two best
    QueenBoards (ranked by # of collisions) and create a new population until we find
    a QueenBoard with the goal state of 0 collisions
    INPUT: void
    OUTPUT: void, creates an optimal solution stored in this.bestBoard
    */
    public void geneticSearch() {
        // while the best board isn't optimal...
        do {
            // use tournament select to get the two best boards
            this.tournamentSelectTwo();
            // create the next generation
            this.newPopulation();
            this.numGens++;
        } while (!this.bestBoard.isOptimal()); // bestBoard is null until first call
    }

    /*
    Uses a tournament style algorithm to find the first and second best QueenBoard
    in N+logN time
    INPUT: void
    OUTPUT: void, points bestBoard and secondBestBoard to the best and second best
            Queen Boards of the current generation
    */
    private void tournamentSelectTwo() {
        // create the tournament tree used for finding 1st and 2nd best QueenBoards
        this.createTournamentTree();
        
        // if user specified they want to see each tournament tree, print current tree
        if (this.printPopulationTrees) {
            this.printTournamentTree();
        }
        
        // the best board will be the root of the tree
        this.bestBoard = this.tournamentTree[this.treeDepth - 1][0];
        // aquire the second best board from the tournament tree
        this.setSecondBest();
    }

    /* 
    INPUT: void
    OUTPUT: creates n new QueenBoards from the best and second best board
    */
    private void newPopulation() {
        // gather the "chromosomes" (queen positions) from the two best Queen boards
        int[] parentChromosome1 = this.bestBoard.getInitIndex();
        int[] parentChromosome2 = this.secondBestBoard.getInitIndex();

        // uncomment if you want the best parent for the next generation
        // YOU MUST ALSO START THE NEXT FOR LOOP AT i = 1
        //this.qBoards[0] = this.bestBoard;
        // create a new Queen board from the parents
        boolean spliceToggle = true;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            // initialize a new chromosome
            int[] newChromosome = new int[QueenBoard.BOARD_WIDTH];
            // create a random crossover point to splice the 
            int crossoverPoint = rand.nextInt(QueenBoard.BOARD_WIDTH);
            
            // toggle between using the first part of parentChromosome1 + second part of parentChromosome2...
            if(spliceToggle){
                // copy parent one's chorosome onto newChromosome from the beginning to the crossover point
                System.arraycopy(parentChromosome1, 0, newChromosome, 0, crossoverPoint);
                // copy parent two's chorosome onto newChromosome from the crossover point to end of array
                System.arraycopy(parentChromosome2, crossoverPoint, newChromosome, crossoverPoint, QueenBoard.BOARD_WIDTH - crossoverPoint);
            }
            // ...and using the first part of parentChromosome2 + second part of parentChromosome1
            else {
                // copy parent two's chorosome onto newChromosome from the beginning to the crossover point
                System.arraycopy(parentChromosome2, 0, newChromosome, 0, crossoverPoint);
                // copy parent one's chorosome onto newChromosome from the crossover point to end of array
                System.arraycopy(parentChromosome1, crossoverPoint, newChromosome, crossoverPoint, QueenBoard.BOARD_WIDTH - crossoverPoint);
            }
            
            // pick a number in range [0,999] and multiply mutationFrequency (should be range
            // of [0.000 to 1.000)) by 1000 to convert the decimal to XX.X
            // this gives the result of mutations happening XX.X% of the time
            if (rand.nextInt(1000) < (this.mutationFrequency * 1000)) {
                // mutate a random row with a random value
                newChromosome[rand.nextInt(QueenBoard.BOARD_WIDTH)] = rand.nextInt(QueenBoard.BOARD_WIDTH);
            }
            
            // add the new board to the next generation
            this.qBoards[i] = new QueenBoard(newChromosome);
            
            // toggle the splice toggle
            spliceToggle = !spliceToggle;
        }
    }

    /*
    INPUT: void
    OUTPUT: void, creates the tournament tree by having the QueenBoards compete
            using their # of collisions as the comparison
            **ONLY CREATES A TOURNAMENT TREE FOR A POPULATION SIZE N WHERE N = 2^i
                WHERE i IS ANY INTEGER**
    */
    private void createTournamentTree() {
        // base of tree is simply our population
        this.tournamentTree[0] = this.qBoards;
        
        // set the current row to the base of the tournament tree
        QueenBoard[] currentRow = this.qBoards;
        
        // proceed through the tournament for each level of depth 
        for (int i = 1; i < this.treeDepth; i++) {
            // the next row is going to be half the size of the current
            int nextRowSize = currentRow.length / 2;
            
            // initialize the next row array
            QueenBoard[] nextRow = new QueenBoard[nextRowSize];
            
            // for each element in the new row
            for (int j = 0; j < nextRowSize; j++) {
                // assign the current slot in the new row to the winner of the 
                // neighboring pairs in the previous row
                nextRow[j] = this.compareFitness(currentRow[j * 2], currentRow[j * 2 + 1]);
            }
            // set the current row to the row we just created
            currentRow = nextRow;
            // add the row we created to the tournament tree
            this.tournamentTree[i] = nextRow;
        }
    }

    /*
    INPUT: void
    OUTPUT: void, assigns the second best Queen Board to this.secondBestBoard
        The main idea is that the 2nd best board could have ONLY lost to the best
        board. So we must back track through all boards that the best board played
        against.
    */
    private void setSecondBest() {
        // we want to track the best index
        int bestIndex;
        // discover if the index of the best board is at position 0 or 1 of the second level
        if (this.tournamentTree[this.treeDepth - 2][0] == this.bestBoard) {
            // assign secondBestBoard to the last board the best lost to
            this.secondBestBoard = this.tournamentTree[this.treeDepth - 2][1];
            // set index of bestBoard at the second level to 0
            bestIndex = 0;
        }
        else {
            // assign secondBestBoard to the last board the best lost to
            this.secondBestBoard = this.tournamentTree[this.treeDepth - 2][0];
            // set index of bestBoard at the second level to 1
            bestIndex = 1;
        }
        // for every level below level 2
        for (int i = this.treeDepth - 3; i > -1; i--) {
            /* current bestBoard has two potential positions
            for a visualization of why index is either bestIndex*2 of (bestIndex*2 + 1)...
            
            B = bestBoard
                              0(B)                   [0 * 2 = 0 => +1 = 1] 
                      0                   1(B)       [1 * 2 = 2 => +1 = 3] 
                  0       1        2(B)         3    [2 * 2 = 4 => +1 = 5]
                0   1   2   3   4    5(B)    6     7 [5 * 2 = 10 => +1 = 11]
               0 1 2 3 4 5 6 7 8 9 10(B)11 12 13 14 15
            */
            // if bestBoard is at bestIndex * 2
            if (this.tournamentTree[i][bestIndex * 2] == this.bestBoard) {
                // compare the current secondBestBoard with a previous board that the bestBoard had beat
                this.secondBestBoard = this.compareFitness(this.secondBestBoard, this.tournamentTree[i][bestIndex * 2 + 1]);
                // set the bestIndex to bestIndex * 2
                bestIndex *= 2;
            }
            // else bestBoard is at bestIndex * 2 + 1
            else {
                // compare the current secondBestBoard with a previous board that the bestBoard had beat
                this.secondBestBoard = this.compareFitness(this.secondBestBoard, this.tournamentTree[i][bestIndex * 2]);
                // set the bestIndex to bestIndex * 2 + 1
                bestIndex = (bestIndex * 2) + 1;
            }
        }
    }

    /*
    helper function
    INPUT: QueenBoard q1,q2 - two QueenBoards
    OUTPUT: Of the two QeenBoards passed, this function returns the
        QueenBoard with less # of collisions
    */
    private QueenBoard compareFitness(QueenBoard q1, QueenBoard q2) {
        // if q1 has less collisions, return q1
        if (q1.getNumCollisions() < q2.getNumCollisions()) {
            return q1;
        }
        // else return q2
        else {
            return q2;
        }
    }

    /*
    INPUT: void
    OUTPUT: Prints the current population of boards to the console
    */
    public void printBoards() {
        // for all boards in the current population
        for (int i = 0; i < qBoards.length; i++) {
            // print the board
            qBoards[i].printBoard();
        }
    }

    /*
    INPUT: void
    OUTPUT: prints the solution board, if one exists
    */
    public void printSolutionBoard() {
        // if a solution board exists...
        if (this.bestBoard != null) {
            // print that board
            this.bestBoard.printBoard();
            // print to console the number of generations it took to get to the solution
            System.out.println("Number of generations: " + this.numGens);
        }
        // else...
        else {
            // inform user that the solution was not found
            System.out.println("Solution was not found. :(");
        }
    }

    /*
    INPUT: void
    OUTPUT: prints the current generation's tournament tree
    */
    private void printTournamentTree() {
        System.out.println("Tournament Tree: ");
        // for every level of the tree
        for (int i = 0; i < this.treeDepth; i++) {
            // print each element separated by a space
            for (int j = 0; j < this.tournamentTree[i].length; j++) {
                System.out.print(this.tournamentTree[i][j].numCollisions + " ");
            }
            // crlf
            System.out.println();
        }
        //crlf
        System.out.println();
    }

    /*
    This function allows the user to toggle if they want to view all generations
    or not
    INPUT: boolean b - sets this.printPopulationTrees to b 
    OUTPUT: void
    */
    public void setPrintPopulationTrees(boolean b) {
        this.printPopulationTrees = b;
    }
}
