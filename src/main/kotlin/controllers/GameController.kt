package controllers

import entities.*
import entities.Hopper.Companion.MAXIMUM_VELOCITY
import exceptions.HopperTestExceptions
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class GameController {

    /**
     * Find the optimal number of hops required to reach the finish square. Returns -1 that would prompt a No Solution output in case no solution could be found for the test case.*
     * @param grid : [Grid] used for the game.*
     * @param startSquare: [Square] that represents the origin square of the [Hopper]*
     * @param obstacles : list of [Square] that are considered occupied and could not be used to reach the finish square.*
     * @param finishSquare : the [Square] that represents the destination of the [Hopper]*
     * @return [Int] : number of hops required to reach the finish square. -1 in case no solution is found*
     */
    fun findOptimalNumberOfHops(grid: Grid, startSquare: Square, obstacles: List<Square>, finishSquare: Square):Int{
        // create the hopper where the initial position is the start square and have a velocity of (0,0)
        val hopper = Hopper(0,0,startSquare)
        val accelerationIndicator = AccelerationIndicator.hopperAcceleration
        // get all possible squares in grid
        val gridSquares = grid.grid
        var eligibleHopSquares = findEligibleHopSquares(hopper, accelerationIndicator,obstacles, gridSquares)
        var hopCount = 0
        println("Finding eligible hops with hopper starting position (${hopper.currentPosition.x}, ${hopper.currentPosition.y}) and starting velocity (${hopper.velocityX}, ${hopper.velocityY})")
        while (hopper.currentPosition != finishSquare) {
            // determine the best move that will get us closer to the finish square
            // There is case where either I didn't fully understand the logic behind how I should handle velocity or the logic in incomplete.
            // Since the hopper can get very close to the finish square (e.g: only one square to finish) but has a high velocity to the extent that reducing it by (-1,-1) wil get you outside the grid.
            // example would be being in a position (6, 2) with velocity (3, 1) and trying to reach (6,4) as i final destination. Reducing the velocity to (2,0) will still push the hopper outside the grid.
            // So I just flag this case as a No solution case. Another option would be to reset the velocity.
            val bestMove = determineBestNextMove(eligibleHopSquares,finishSquare) ?: return -1
            // if the best move is the current place itself it means that the hopper can not move and thus cannot reach the finish square. As such there is no solution.
            if (hopper.currentPosition == bestMove.square){
                return -1
            }
            hopper.velocityX = bestMove.potentialVelocityX
            hopper.velocityY = bestMove.potentialVelocityY
            hopper.currentPosition = bestMove.square
            println("Finding eligible hops with hopper current position (${hopper.currentPosition.x}, ${hopper.currentPosition.y}) with velocity (${hopper.velocityX}, ${hopper.velocityY})")
            hopCount += 1
            if (bestMove.square == finishSquare){
                break
            }
            eligibleHopSquares = findEligibleHopSquares(hopper, accelerationIndicator,obstacles, gridSquares)
        }
        println("Reached Solution !")
        return hopCount
    }

    /**
     * find potential squares where the hopper could jump, whilst avoiding obstacle squares.*
     *@param hopper : the [Hopper] in the test case.*
     * @param accelerationIndicator: list of a pair of possible acceleration instructions.*
     * @param obstacles: list of obstacle [Square]s*
     * @param gridSquares: list of [Square]s inside the [Grid]*
     */
    private fun findEligibleHopSquares(hopper: Hopper, accelerationIndicator: List<Pair<Int, Int>>, obstacles: List<Square>, gridSquares: List<Square>): MutableList<HoppingSquareToPotentialVelocity> {
        return accelerationIndicator.map{ acceleration ->
                val potentialVelocityX = if ( hopper.velocityX + acceleration.first > MAXIMUM_VELOCITY) MAXIMUM_VELOCITY else hopper.velocityX + acceleration.first
                val potentialVelocityY = if ( hopper.velocityY + acceleration.second > MAXIMUM_VELOCITY) MAXIMUM_VELOCITY else hopper.velocityY + acceleration.second
                HoppingSquareToPotentialVelocity(
                Square(hopper.currentPosition.x + potentialVelocityX,
                    hopper.currentPosition.y + potentialVelocityY),
                    potentialVelocityX, potentialVelocityY)

        }.filter { it.square in gridSquares }.filterNot { it.square in obstacles }.toMutableList()
    }

    /**
     * Determines what is the best next move for the [Hopper] in their way to reach the finish [Square].*
     * @param eligibleHopSquares: list of potential [Square]s that the hopper is allowed to jump to.*
     * @param finishSquare: destination [Square].*
     * @return [HoppingSquareToPotentialVelocity]: [Square] that the [Hopper] should jump to and the used velocity to reach it.*
     */
    private fun determineBestNextMove(
        eligibleHopSquares: MutableList<HoppingSquareToPotentialVelocity>,
        finishSquare: Square,
    ): HoppingSquareToPotentialVelocity? {
        var closestDistance = Double.MAX_VALUE
        var closestSquare: HoppingSquareToPotentialVelocity? = null
        eligibleHopSquares.forEach { eligibleSquare ->
            val distance = determineClosestSquareToFinishSquare(eligibleSquare.square, finishSquare)
            if (distance < closestDistance) {
                closestDistance = distance
                closestSquare = eligibleSquare
            }
        }
        eligibleHopSquares.clear()
        return closestSquare
    }


    private fun determineClosestSquareToFinishSquare(currentSquare: Square, finishSquare: Square): Double {
        return sqrt(
            abs(currentSquare.x - finishSquare.x).toDouble().pow(2) + abs(currentSquare.y - finishSquare.y).toDouble().pow(2)
        )
    }


    /**
     * Create the [Grid] with the user input dimensions*
     * @param dimensions : which is a list of input strings that (should) contain(s) the dimensions of the grid*
     * @return [Grid] : the game grid*
     */
    fun createGrid(dimensions : List<String>): Grid {
        if (dimensions.size != 2){
            throw HopperTestExceptions.InvalidUserInput(
                "Please make sure to enter 2 elements as the dimension of the grid!",
                dimensions.joinToString(" "),
                dimensions.size,
                2
            )
        }

        return try {
            val grid = Grid(dimensions[0].toInt(), dimensions[1].toInt())
            grid.isGridValid()
            grid
        } catch (numberFormatException: NumberFormatException) {
            throw NumberFormatException("Please make sure that the dimensions are integers.")
        }
    }

    /**
     * Create the start [Square] where the hopper is initially positioned and the end [Square] the hopper should reach.*
     * @param startAndFinishSquaresCoordinates : which is a list of input strings that (should) contain(s) the coordinates of the squares in question.*
     * @param grid: the [Grid] on which the game is played and where the start and finish [Square] should belong.*
     * @return Pair<[Square],[Square]> : a pair of start and end [Square]*
     */
    fun createStartAndFinishSquares(startAndFinishSquaresCoordinates: List<String>, grid: Grid): Pair<Square,Square>{
        if (startAndFinishSquaresCoordinates.size != 4) {
            throw HopperTestExceptions.InvalidUserInput(
                "Please make sure that the start and finish squares are entered in the requested format.",
                startAndFinishSquaresCoordinates.joinToString(" "),
                startAndFinishSquaresCoordinates.size,
                4
            )
        }
        return try {
            val startSquare = Square(startAndFinishSquaresCoordinates[0].toInt(), startAndFinishSquaresCoordinates[1].toInt())
            startSquare.checkIfWithinGrid(grid)
            val endSquare = Square(startAndFinishSquaresCoordinates[2].toInt(), startAndFinishSquaresCoordinates[3].toInt())
            endSquare.checkIfWithinGrid(grid)
            Pair(startSquare,endSquare)
        }catch (numberFormatException: NumberFormatException) {
            throw NumberFormatException("Please make sure that the coordinates are integers.")
        }
    }

    /**
     * Create an obstacle wall from two obstacle boundaries' coordinates.*
     * Logic: Given obstacleSquare(x1,y1) and obstacleSquare(x2,y2), all the [Square]s where x between x1 and x2 and y between y1 and y2, are considered a part of the obstacle wall.*
     * @param obstaclesCoordinates : which is a list of input strings that (should) contain(s) the coordinates of the obstacle extremities.*
     * @return List<[Square] : list of the obstacle [Square]s*
     */
    fun createObstacleSquares(obstaclesCoordinates: List<String>): List<Square> {
        if (obstaclesCoordinates.size != 4) {
            throw HopperTestExceptions.InvalidUserInput(
                "Please make sure that the range of the obstacle contain 4 coordinates as aforementioned.",
                obstaclesCoordinates.joinToString(" "),
                obstaclesCoordinates.size,
                4
            )
        }
        val obstacleBound1 = Square(obstaclesCoordinates[0].toInt(), obstaclesCoordinates[2].toInt())
        val obstacleBound2 = Square(obstaclesCoordinates[1].toInt(), obstaclesCoordinates[3].toInt())
        verifyObstacleRanges(obstacleBound1, obstacleBound2)
        return (obstacleBound1.x..obstacleBound2.x).toList().flatMap { i ->
            (obstacleBound1.y..obstacleBound2.y).toList().map {j ->
                Square(i, j, true)
            }
        }
    }

    private fun verifyObstacleRanges(obstacleBound1:Square, obstacleBound2: Square){
        if (obstacleBound1.x > obstacleBound2.x || obstacleBound1.y > obstacleBound2.y){
            throw HopperTestExceptions.InvalidObstacleExtremitiesException(obstacleBound1,obstacleBound2)
        }
    }
}
