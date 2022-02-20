package connectfour

class Column(val column: Int) {
    var filledSlots = mutableListOf<Char>()

    fun addChip(char: Char) {
        filledSlots.add(char)
    }

    fun chipAtRow(row: Int): Char {
        return try {
            filledSlots[row]
        } catch (e: IndexOutOfBoundsException) {
            ' '
        }
    }

    fun reset() {
        filledSlots.clear()
    }
}