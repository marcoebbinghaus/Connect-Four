package connectfour

class InvalidDimensionException(override val message: String) : RuntimeException(message) {
}