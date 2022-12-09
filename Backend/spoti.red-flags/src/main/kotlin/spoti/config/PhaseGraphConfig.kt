package spoti.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import spoti.exceptions.NoNextPhaseDeclared
import spoti.game.GameManager
import spoti.other.Phase

class PhaseGraphConfig {
    var phases : List<PhaseConfig> = emptyList()
    var currentPhaseConfig : MutableStateFlow<PhaseConfig?> = MutableStateFlow(null)
    fun addPhase(phaseConfig: PhaseConfig){
        if(phases.any{it.phase == phaseConfig.phase || (it.isFirst && phaseConfig.isFirst)})
            throw Exception("Invalid phase, error in config")
        if(phaseConfig.timeLimit is TimeLimit.TimeLimitInMilliseconds && phaseConfig.nextPhase == null){
            throw Exception("Time limit provided, but no next stage description")
        }
        phases = phases.plus(phaseConfig)
        if(phaseConfig.isFirst){
            currentPhaseConfig.update { phaseConfig }
        }
    }

    operator fun get(phase: Phase) : PhaseConfig{
        return phases.find { it.phase == phase } ?: throw Exception("Phase not found")
    }
    fun getFirstPhase() : PhaseConfig{
        return phases.single{it.isFirst}
    }
    fun nextPhase(){
        currentPhaseConfig.value?.nextPhase?.let { nextPhase ->
            currentPhaseConfig.update { get(nextPhase()) }
        } ?: throw NoNextPhaseDeclared()
    }
    fun setPhase(phase: Phase){
        currentPhaseConfig.update { get(phase) }
    }
}

fun graph(game: GameManager, configuration: PhaseGraphConfig.(GameManager) -> Unit) : PhaseGraphConfig{
    val graph = PhaseGraphConfig()
    graph.configuration(game)
    return graph
}

fun PhaseGraphConfig.phase(phaseType: Phase,isFirst: Boolean =false,configuration: PhaseConfig.() -> Unit){
    val newPhase = PhaseConfig(phaseType, isFirst)
    newPhase.configuration()
    addPhase(newPhase)
}