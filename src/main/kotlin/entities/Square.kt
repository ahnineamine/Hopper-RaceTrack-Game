package entities

import exceptions.HopperTestExceptions

data class Square (
    // Horizontal position of the square in the grid
    val x: Int,
    // Vertical position of the square in the grid
    val y: Int,
    // Whether the square is occupied / is an obstacle.
    val isObstacle: Boolean = false,
){
    /**
     * Check if the [Square] is Within a given [Grid]*
     */
    fun checkIfWithinGrid(grid: Grid) {
        if (x > grid.width || y > grid.height){
            throw HopperTestExceptions.SquareNotWithinGridException(x,y,grid)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Square

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }


}