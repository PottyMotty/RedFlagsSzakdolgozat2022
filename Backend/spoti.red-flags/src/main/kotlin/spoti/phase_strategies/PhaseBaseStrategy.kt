package spoti.phase_strategies

import spoti.game.GameManager
import spoti.other.Phase

abstract class PhaseBaseStrategy(val phase: Phase) {
    abstract suspend operator fun invoke(gameManager: GameManager)
}