package connectfour

class InvalidGameCountException(override val message: String) : RuntimeException(message) {
}