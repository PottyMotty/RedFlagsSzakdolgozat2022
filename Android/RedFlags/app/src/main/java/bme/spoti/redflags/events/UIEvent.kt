package bme.spoti.redflags.events

import androidx.navigation.NavDirections
import androidx.navigation.Navigator

interface UIEvent

interface UiFragmentEvent: UIEvent
interface UiViewModelEvent: UIEvent

data class NavigationEvent(
    val navDirections: NavDirections,
    val extra: Navigator.Extras? = null
) : UiFragmentEvent