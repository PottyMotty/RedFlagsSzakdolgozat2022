package spoti.config

import ch.qos.logback.classic.spi.PackagingDataCalculator
import spoti.other.Phase
import spoti.phase_strategies.PhaseBaseStrategy

private typealias PhaseAction = () -> Unit


data class PhaseConfig(
    val phase: Phase,
    val isFirst: Boolean = false
) {
    var phaseStrategy: PhaseBaseStrategy? = null
        set(value){
            value?.let{
                if(it.phase != phase) throw Exception("Phases don't match")
            }
            field = value
        }
    var timeLimit: TimeLimit = TimeLimit.NoLimit
    var prepPhase: PhaseAction? = null
    var nextPhase: (() -> Phase)? = null
}

sealed class TimeLimit {
    data class TimeLimitInMilliseconds(val timeCalculator: ()-> Long) : TimeLimit(){
        val time : Long get() = timeCalculator()
    }
    object NoLimit : TimeLimit()
}

fun PhaseConfig.prepPhase(prep: PhaseAction) {
    this.prepPhase = prep
}
fun PhaseConfig.timeLimit(ms : ()->Long) {
    timeLimit = TimeLimit.TimeLimitInMilliseconds(ms)
}
fun PhaseConfig.nextPhase(nextPhase:()-> Phase){
    this.nextPhase = nextPhase
}
