package exceptions

open class HopperTestException(
    message: String,
    val additionalProperties: Map<String, Any?> = emptyMap(),
) : RuntimeException(message) {
}