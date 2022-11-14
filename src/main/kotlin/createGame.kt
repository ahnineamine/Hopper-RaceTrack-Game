import controllers.GameController
import entities.Hopper
import java.util.*


fun main() {
    val gameController = GameController()

    val reader = Scanner(System.`in`)

    println("Please enter the number of cases you want to test:")
    val numberOfCases = reader.nextLine().toInt()
    var caseCount = 1
    while (caseCount <= numberOfCases) {
        println("Test case number $caseCount")

        // Create Grid
        println("Please enter the grid's width(x) and height (y) with a space between them (i.e: x y):")
        val dimensions = reader.nextLine().split(" ")
        val grid = gameController.createGrid(dimensions)

        // Create start and finish squares
        println("Please enter the start and end squares in this format : x1 y1 x2 y2")
        val startAndFinishSquares = reader.nextLine().split(" ")
        if (startAndFinishSquares.size != 4) {
            throw Exception("Please make sure that the start and end squares are entered in the requested format.")
        }
        val (startSquare, finishSquare) = gameController.createStartAndFinishSquares(startAndFinishSquares,grid)

        // create the obstacles
        println("Please chose the number of obstacles (>0):")
        val numberOfObstaclesLine = reader.nextLine()
        val numberOfObstacles = numberOfObstaclesLine.toInt()
        if (numberOfObstacles == 0){
            throw Exception("The game should have at least on obstacle!")
        }
        val obstacleWalls = (1..numberOfObstacles).toList().map {
            println("Please enter the obstacle's start and end range as follow: x1 x2 y1 y2. (obstacle $it out of $numberOfObstacles)")
            val obstacleString = reader.nextLine()
            val obstacles = obstacleString.split(" ")
            gameController.createObstacleSquares(obstacles)
        }

        // Get number of hops to reach the finish square
        println("calculating number of hops to reach final square ...")
        val numberOfHops = gameController.findOptimalNumberOfHops(grid, startSquare, obstacleWalls.flatten(), finishSquare)
        if (numberOfHops == -1) {
            println("No solution")
        } else {
            println("Optimal solution takes $numberOfHops hops")
        }

        caseCount ++
    }
}
