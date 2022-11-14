package controllers

import entities.Square
import exceptions.HopperTestExceptions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class GameControllerTest {

    @Test
    fun findOptimalNumberOfHops() {
        val gameController = GameController()
        val successfulScenarioGrid = gameController.createGrid(listOf("2", "2"))
        val successfulScenarioStartSquare = Square(0,0)
        val successfulScenarioFinishSquare = Square(2,2)
        val successfulScenarioObstacles = listOf(Square(1,1))
        val successfulScenario = gameController.findOptimalNumberOfHops(successfulScenarioGrid,successfulScenarioStartSquare,successfulScenarioObstacles,successfulScenarioFinishSquare)
        // it should take 3 jumps to reach the finish square in this case with the locations being 0,0 -> 0,1 -> 2,1 -> 2,2
        assert(successfulScenario == 3)

        val failedScenarioGrid = gameController.createGrid(listOf("3", "3"))
        val failedScenarioStartSquare = Square(0,0)
        val failedScenarioFinishSquare = Square(2,2)
        val failedScenarioObstacles = gameController.createObstacleSquares(listOf("1","1","0","2")) + gameController.createObstacleSquares(listOf("0","2","1","1"))
        val failedScenario = gameController.findOptimalNumberOfHops(failedScenarioGrid,failedScenarioStartSquare,failedScenarioObstacles,failedScenarioFinishSquare)
        // The obstacles should bloc the jumper from reaching the desired destination
        assert(failedScenario == -1)
    }

    @Test
    fun createGrid() {
        val dimensions = listOf("4","4")
        val gameController = GameController()
        val successfulGrid = gameController.createGrid(dimensions)
        assert(successfulGrid.width == 4)
        assert(successfulGrid.height == 4)
        assert(successfulGrid.grid.size == 25)

        val failedGridException = assertThrows(HopperTestExceptions.InvalidUserInput::class.java) {
            gameController.createGrid(dimensions.subList(0,1))
        }
        assert(failedGridException.message!!.contains("Please make sure to enter 2 elements as the dimension of the grid!"))
        assert(failedGridException.additionalProperties["inputString"] == "4")
        assert(failedGridException.additionalProperties["numberOfElementsInString"] == 1)
        assert(failedGridException.additionalProperties["expectedElementsInString"] == 2)
    }

    @Test
    fun createObstacleSquares() {
        val obstaclesCoordinates = mutableListOf("2","3","2","3")
        val gameController = GameController()
        val successfulObstacles = gameController.createObstacleSquares(obstaclesCoordinates)
        assert(successfulObstacles.size == 4)
        assert(successfulObstacles[0].x ==2)
        assert(successfulObstacles[0].y ==2)
        assert(successfulObstacles[1].x ==2)
        assert(successfulObstacles[1].y ==3)
        assert(successfulObstacles[2].x ==3)
        assert(successfulObstacles[2].y ==2)
        assert(successfulObstacles[3].x ==3)
        assert(successfulObstacles[3].y ==3)

        val failedObstaclesException = assertThrows(HopperTestExceptions.InvalidObstacleExtremitiesException::class.java) {
            obstaclesCoordinates[1]="1"
            gameController.createObstacleSquares(obstaclesCoordinates)
        }
        assert(failedObstaclesException.message!!.contains("The obstacle range is not coherent (x1 < x2 and y1 < y2)"))
        assert(failedObstaclesException.additionalProperties["obstacleBound1X"] == 2)
        assert(failedObstaclesException.additionalProperties["obstacleBound2X"] == 1)
        assert(failedObstaclesException.additionalProperties["obstacleBound1Y"] == 2)
        assert(failedObstaclesException.additionalProperties["obstacleBound2Y"] == 3)


    }
}