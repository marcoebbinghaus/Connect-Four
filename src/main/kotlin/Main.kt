package connectfour

fun main() {
    println("Connect Four")
    val game = setupGame()
    game.printSetup()
    do {
        game.printNextTurn()
        val chosenColumn = readln()
        game.addChip(chosenColumn)
    } while (game.isActive())
    println("Game over!")
}

private fun setupGame(): Game {
    println("First player's name:")
    val player1 = readln()
    println("Second player's name:")
    val player2 = readln()
    val game = Game(Player(player1, 'o'), Player(player2, '*'))
    do {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val dimensions = readln()
        try {
            game.applyDimensions(dimensions)
        } catch (e: InvalidDimensionException) {
            println(e.message)
        }
    } while (game.hasInvalidDimensions())
    do {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        val gameCount = readln()
        try {
            game.applyGameCount(gameCount)
        } catch (e: InvalidGameCountException) {
            println(e.message)
        }
    } while (game.hasInvalidGameCount())

    return game
}
