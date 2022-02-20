package connectfour

class Game(private val player1: Player, private val player2: Player) {
    private var maxRows: Int = 6
    private var maxColumns: Int = 7
    private var gameCount = 0
    private var currentGameCount = 1
    private var maxTurns = 0
    var currentTurn = player1
    var status: GameStatus = GameStatus.RUNNING
    var columns = mutableListOf<Column>()

    fun applyDimensions(dimensions: String) {
        if (dimensions.isNotBlank()) {
            if (!dimensions.contains('x', true)) {
                throw InvalidDimensionException("Invalid input")
            }
            val (rows, columns) = dimensions.replace('x', 'X').split('X')
            this.maxRows = try {
                rows.trim().toInt()
            } catch (e: NumberFormatException) {
                throw InvalidDimensionException("Invalid input")
            }
            this.maxColumns = try {
                columns.trim().toInt()
            } catch (e: NumberFormatException) {
                throw InvalidDimensionException("Invalid input")
            }
            if (this.maxRows < 5 || this.maxRows > 9) {
                throw InvalidDimensionException("Board rows should be from 5 to 9")
            }
            if (this.maxColumns < 5 || this.maxColumns > 9) {
                throw InvalidDimensionException("Board columns should be from 5 to 9")
            }
        }
        maxTurns = maxRows * maxColumns
        for (col in 1..maxColumns) {
            columns.add(Column(col))
        }
    }

    fun applyGameCount(gameCount: String) {
        if (gameCount.isNotBlank()) {
            this.gameCount = try {
                gameCount.toInt()
            } catch (e: NumberFormatException) {
                throw InvalidGameCountException("Invalid input")
            }
            if (this.gameCount == 0) {
                throw InvalidGameCountException("Invalid input")
            }
        } else {
            this.gameCount = 1
        }
    }

    fun printSetup() {
        println("${player1.name} VS ${player2.name}")
        println("$maxRows X $maxColumns board")
        if (gameCount > 1) {
            println("Total $gameCount games")
            println("Game #$currentGameCount")
        } else {
            println("Single game")
        }
        printBoard()
    }

    private fun printBoard() {
        val columnsRange = (1..maxColumns)
        println(columnsRange.joinToString(separator = "") { " $it" })
        for (row in maxRows - 1 downTo 0 step 1) {
            print("║")
            columnsRange.forEach { col -> print("${columns[col - 1].chipAtRow(row)}║") }
            println()
        }
        for (col in columnsRange) {
            if (col == 1) {
                print("╚═╩")
            } else if (col < maxColumns) {
                print("═╩")
            } else {
                println("═╝")
            }
        }
    }

    private fun boardFull(): Boolean {
        return columns.sumOf { it.filledSlots.size } >= maxRows * maxColumns
    }

    fun printNextTurn() {
        println("${currentTurn.name}'s turn:")
    }

    fun addChip(column: String) {
        try {
            if (column == "end") {
                status = GameStatus.CANCELLED
                return
            }
            val columnInt = column.toInt()
            if (columnInt < 1 || columnInt > maxColumns) {
                println("The column number is out of range (1 - $maxColumns)")
                return
            }
            val chosenColumn = columns[columnInt - 1]
            if (chosenColumn.filledSlots.size >= maxRows) {
                println("Column $column is full")
                return
            }
            chosenColumn.addChip(currentTurn.chip)
            printBoard()
            if (boardContainsFour()) {
                status = GameStatus.WON
                println("Player ${currentTurn.name} won")
                currentTurn.score += 2
            }
            if (boardFull()) {
                status = GameStatus.DRAW
                player1.score += 1
                player2.score += 1
                println("It is a draw")
            }
            if (status == GameStatus.WON || status == GameStatus.DRAW) {
                if (gameCount > 1) {
                    printScore()
                    currentGameCount++
                    if (currentGameCount > gameCount) {
                        status = GameStatus.ALL_ROUNDS_DONE
                    } else {
                        nextRound()
                    }
                }
            }
            currentTurn = if (currentTurn == player1) player2 else player1
        } catch (e: NumberFormatException) {
            println("Incorrect column number")
        }
    }

    private fun nextRound() {
        status = GameStatus.RUNNING
        println("Game #$currentGameCount")
        columns.forEach { it.reset() }
        printBoard()
    }

    private fun printScore() {
        println("Score")
        println("${player1.name}: ${player1.score} ${player2.name}: ${player2.score}")
    }

    private fun boardContainsFour(): Boolean {
        for (column in columns) {
            val x = column.column
            for (y in 0 until maxRows) {
                val winHorizontally = winHorizontally(x, y)
                val winVertically = winVertically(x, y)
                val winDiagonally = winDiagonally(x, y)
                if (winHorizontally || winVertically || winDiagonally) {
                    return true
                }
            }
        }
        return false
    }

    private fun winDiagonally(x: Int, y: Int): Boolean {
        val winLeftDownRightUp = winLeftDownRightUp(x, y)
        if (winLeftDownRightUp) {
            return true
        }
        val winLeftUpRightDown = winLeftUpRightDown(x, y)
        if (winLeftUpRightDown) {
            return true
        }
        return false
    }

    private fun winVertically(x: Int, y: Int): Boolean {
        val yMin = y - 3
        val yMax = y + 3
        val chips = mutableSetOf<Char>()
        for (potentialStart in yMin..yMax) {
            chips.clear()
            for (currentY in potentialStart..potentialStart + 3) {
                chips.add(getChipAt(x, currentY))
            }
            if (isFourInARow(chips)) return true
        }
        return false
    }

    private fun winLeftUpRightDown(x: Int, y: Int): Boolean {
        val xMax = x - 3
        val yMax = y + 3
        val chips = mutableSetOf<Char>()
        for (i in 0..3) {
            val currentX = xMax + i
            val currentY = yMax - i
            chips.clear()
            for (toAddOrSubtract in 0..3) {
                chips.add(getChipAt(currentX + toAddOrSubtract, currentY - toAddOrSubtract))
            }
            if (isFourInARow(chips)) return true
        }
        return false
    }

    private fun winLeftDownRightUp(x: Int, y: Int): Boolean {
        val xMin = x - 3
        val yMin = y - 3
        val chips = mutableSetOf<Char>()
        for (i in 0..3) {
            val currentX = xMin + i
            val currentY = yMin + i
            chips.clear()
            for (toAdd in 0..3) {
                chips.add(getChipAt(currentX + toAdd, currentY + toAdd))
            }
            if (isFourInARow(chips)) return true
        }
        return false
    }

    private fun winHorizontally(x: Int, y: Int): Boolean {
        val xMin = x - 3
        val xMax = x + 3
        val chips = mutableSetOf<Char>()
        for (potentialStart in xMin..xMax) {
            chips.clear()
            for (currentX in potentialStart..potentialStart + 3) {
                chips.add(getChipAt(currentX, y))
            }
            if (isFourInARow(chips)) return true
        }
        return false
    }

    private fun isFourInARow(chips: MutableSet<Char>): Boolean {
        if (chips.size == 1) {
            val chip = chips.iterator().next()
            if (chip == player1.chip || chip == player2.chip) {
                return true;
            }
        }
        return false
    }

    private fun getChipAt(x: Int, y: Int): Char {
        return try {
            val column = columns[x - 1]
            column.chipAtRow(y)
        } catch (e: IndexOutOfBoundsException) {
            ' '
        }
    }

    fun hasInvalidDimensions(): Boolean {
        return columns.isEmpty()
    }

    fun hasInvalidGameCount(): Boolean {
        return gameCount <= 0
    }

    fun isActive(): Boolean {
        return if (gameCount == 1) {
            status == GameStatus.RUNNING
        } else {
            status != GameStatus.ALL_ROUNDS_DONE
        }

    }
}