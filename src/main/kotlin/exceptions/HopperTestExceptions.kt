package exceptions

import entities.Grid
import entities.Grid.Companion.MAXIMUM_GRID_DIMENSION
import entities.Square

class HopperTestExceptions {
    class InvalidGridDimensionsException(width: Int, height: Int): HopperTestException(
        "The grid dimensions are not valid, please make sure that the maximum range for both dimensions are $MAXIMUM_GRID_DIMENSION",
        mapOf("inputWidth" to width, "inputHeight" to height)
    )

    class SquareNotWithinGridException(x:Int, y:Int, grid: Grid): HopperTestException(
        "Square is not within cell",
        mapOf("squareLongitude" to x, "squareLatitude" to y, "gridWidth" to grid.width, "gridHeight" to grid.height)
    )

    class InvalidObstacleExtremitiesException(obstacleBound1: Square, obstacleBound2: Square): HopperTestException(
        "The obstacle range is not coherent (x1 < x2 and y1 < y2)",
        mapOf("obstacleBound1X" to obstacleBound1.x, "obstacleBound2X" to obstacleBound2.x, "obstacleBound1Y" to obstacleBound1.y, "obstacleBound2Y" to obstacleBound2.y)
    )

    class InvalidUserInput(message: String, input: String, size: Int, expectedSize:Int): HopperTestException(
        message,
        mapOf("inputString" to input, "numberOfElementsInString" to size, "expectedElementsInString" to expectedSize)
    )
}