/*
Connor Haskins
Bronco ID# 010215400
May 14th, 2016
CS 420.01 w/ Dr. Fang Tang
 */

/*
Program that demonstrates two separate solutions to the N-Queen Puzzle
1) A initial board state is randomly generated and displayed on the console
2) A steepest hill climb is performed finding a local maximum and that state
is displayed to the console
3) A genetic algorithm is used that allows for the solution of a goal state
with 0 collisions between queens. The initial population is shown and the
discovered goal state is displayed on the console.
*/
public class NQueen {
    
    public static void main(String[] args) {
        // create a new queen board for the Steepest Ascent Hill Climb
        QueenBoard qBoard = new QueenBoard();

        System.out.println("==========================");
        System.out.println("Steepest Ascent Hill Climb ");
        System.out.println("==========================");
        // print initial board
        qBoard.printBoard();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("VVVVVVVVVVVVVVVVVVVVVVV\n");
        // perform the steepest ascent hill climb
        qBoard.steepestHillClimb();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("VVVVVVVVVVVVVVVVVVVVVVV");
        // print the local maximum
        qBoard.printBoard();
        // crlf
        System.out.println();

        // create a new population of GeneticQueenBoards
        // with mutation frequency set for 0.5f or 50.0%
        GeneticQueenBoards gQBoards = new GeneticQueenBoards(0.5f);

        System.out.println("====================");
        System.out.println("Genetic Local Search");
        System.out.println("====================");
        System.out.println("Population size: " + GeneticQueenBoards.POPULATION_SIZE);
        // print the first population of boards
        gQBoards.printBoards();
        // only turn on if you want to see every generations tournament tree
        // *IT IS A LOT 200~8000*
        //gQBoards.setPrintPopulationTrees(true);
        // perform a genetic search
        gQBoards.geneticSearch();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("VVVVVVVVVVVVVVVVVVVVVVV\n");
        // print the optimal solution found
        gQBoards.printSolutionBoard();
    }

}
