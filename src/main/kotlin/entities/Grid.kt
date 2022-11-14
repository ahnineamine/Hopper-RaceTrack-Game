package entities

import exceptions.HopperTestExceptions

data class Grid (
    // the width of the rectangular grid e.g: 5
    val width: Int,
    // the width of the rectangular grid e.g: 6
    val height: Int,
) {
    /**
     * Create a rectangular grid made of a set of [Square] where the width and height consists the boundaries of the grid*
     * @return list of [Square] within the [Grid]*
     */
    val grid: List<Square>
        get() = (0..width).toList().flatMap { widthElem -> (0..height).toList().map { heightElem -> widthElem to heightElem } }
            .map { pair -> Square(pair.first, pair.second) }

    /**
     * Check if the dimensions of the [Grid] are valid (height and width are smaller than [MAXIMUM_GRID_DIMENSION]*
     * @throws *
     */
    fun isGridValid(){
        if (width > MAXIMUM_GRID_DIMENSION || height > MAXIMUM_GRID_DIMENSION){
            throw HopperTestExceptions.InvalidGridDimensionsException(width,height)
        }
    }

    companion object{
        const val MAXIMUM_GRID_DIMENSION = 30
    }
}