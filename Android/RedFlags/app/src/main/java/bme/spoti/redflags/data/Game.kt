package bme.spoti.redflags.data

class Game {
    enum class Phase() {
        WAITING_FOR_PLAYERS,
        PLAYERS_GATHERED,
        SINGLE_REVEAL,
        DATE_CRAFTING,
        SHOWCASE_ONE_BY_ONE,
        SHOWCASE_ALL,
        SABOTAGE,
        LEADERBOARD,
        WINNER_SHOWCASE,
        END,
        WAITING_FOR_RECONNECT,
        RECONNECT,
    }
}