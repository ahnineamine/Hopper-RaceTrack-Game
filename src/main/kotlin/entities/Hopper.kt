package entities

data class Hopper (
    // Horizontal Velocity
    var velocityX: Int,
    // Vertical Velocity
    var velocityY: Int,
    // the Square occupied by the hopper
    var currentPosition: Square
    ){
    companion object {
        const val MAXIMUM_VELOCITY = 3

    }

}