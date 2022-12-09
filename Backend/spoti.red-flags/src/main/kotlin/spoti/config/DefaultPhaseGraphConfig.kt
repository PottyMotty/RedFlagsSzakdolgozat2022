package spoti.config

import kotlinx.coroutines.flow.update
import spoti.game.GameManager
import spoti.other.Phase
import spoti.other.extensions.seconds
import spoti.phase_strategies.date_crafting.DateCraftingStrategy
import spoti.phase_strategies.leaderboard.LeaderboardDefaultStrategy
import spoti.phase_strategies.sabotage.SabotageWithRandomizedPairsStrategy
import spoti.phase_strategies.showcase_one_by_one.ShowcaseOneByOneSendAllStrategy
import spoti.phase_strategies.single_reveal.SingleRevealRandomSelectStrategy
import spoti.phase_strategies.winner_showcase.DefaultWinnerShowcaseStrategy
import java.util.concurrent.atomic.AtomicInteger

fun defaultPhaseGraphConfig(game: GameManager): PhaseGraphConfig {
    val POSITIVE_CARDS_NUM = 5
    val NEGATIVE_CARDS_NUM = 3
    return graph(game) { game ->
        phase(Phase.WAITING_FOR_PLAYERS, isFirst = true) {
            nextPhase {
                Phase.PLAYERS_GATHERED
            }
        }
        phase(Phase.PLAYERS_GATHERED) {
            nextPhase {
                Phase.SINGLE_REVEAL
            }
        }
        phase(Phase.SINGLE_REVEAL) {
            phaseStrategy = SingleRevealRandomSelectStrategy()
            prepPhase {
                game.setupNextRound()
            }
            timeLimit{10.seconds}
            nextPhase {
                Phase.DATE_CRAFTING
            }
        }
        phase(Phase.DATE_CRAFTING) {
            phaseStrategy = DateCraftingStrategy(POSITIVE_CARDS_NUM)
            prepPhase {
                game.doneWorking.update { AtomicInteger(0) }
            }
            timeLimit{30.seconds}
            nextPhase {
                Phase.SHOWCASE_ONE_BY_ONE
            }
        }
        phase(Phase.SHOWCASE_ONE_BY_ONE) {
            phaseStrategy = ShowcaseOneByOneSendAllStrategy()
            prepPhase {
                if(game.isSabotageStage){
                    game.fillUpDateOptionsAfterSabotage()
                }else{
                    game.fillUpDateOptionsPreSabotage()
                }

            }
            timeLimit{15.seconds * game.room.players.size.minus(1)}
            nextPhase {
                Phase.SHOWCASE_ALL
            }
        }
        phase(Phase.SHOWCASE_ALL) {
            nextPhase {
                Phase.WINNER_SHOWCASE
            }
        }
        phase(Phase.SABOTAGE) {
            phaseStrategy = SabotageWithRandomizedPairsStrategy(NEGATIVE_CARDS_NUM)
            prepPhase {
                game.isSabotageStage = true
                game.doneWorking.update { AtomicInteger(0) }
            }
            timeLimit{30.seconds}
            nextPhase {
                Phase.SHOWCASE_ONE_BY_ONE
            }
        }
        phase(Phase.LEADERBOARD) {
            phaseStrategy = LeaderboardDefaultStrategy()
            prepPhase {
                game.handOutPoints()
            }
            nextPhase {
                println("NUMCOUNT: ${game.currentRoundNumber} + ${game.pastSinglesIDs} + ${game.numRounds}")
                if (game.currentRoundNumber > game.numRounds) {
                    Phase.END
                } else {
                    if(game.pastSinglesIDs.size == game.room.playerCount){
                        game.currentRoundNumber++
                        game.pastSinglesIDs.clear()
                    }
                    Phase.SINGLE_REVEAL
                }
            }
            timeLimit{15.seconds}
        }
        phase(Phase.WINNER_SHOWCASE) {
            phaseStrategy = DefaultWinnerShowcaseStrategy()
            timeLimit{10.seconds}
            nextPhase {
                if (game.isSabotageStage) Phase.LEADERBOARD else Phase.SABOTAGE
            }
        }
        phase(Phase.END) {

        }
    }
}