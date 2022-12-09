package spoti.exceptions

import spoti.other.Phase

sealed class RedFlagsException(override val message: String? = null) : Exception(message)
data class RoomNotFoundException(override val message: String? = null) : RedFlagsException(message)
data class ValueNotYetAvalaible(override val message: String? = null) : RedFlagsException(message)
data class PlayerNotFound(override val message: String? = null) : RedFlagsException(message)
data class NoNextPhaseDeclared(override val message: String? = null) : RedFlagsException(message)