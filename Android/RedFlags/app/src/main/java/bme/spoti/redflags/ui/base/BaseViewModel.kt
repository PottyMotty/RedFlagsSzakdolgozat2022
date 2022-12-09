package bme.spoti.redflags.ui.base

import androidx.lifecycle.ViewModel
import bme.spoti.redflags.events.UIEvent
import bme.spoti.redflags.events.UiFragmentEvent
import bme.spoti.redflags.events.UiViewModelEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

open class BaseViewModel : ViewModel() {
    val uiEventsInput = MutableSharedFlow<UIEvent>(0, 10)

    val uiFragmentEventsOutput = uiEventsInput.filterIsInstance<UiFragmentEvent>()
    val uiViewModelEventsOutput = uiEventsInput.filterIsInstance<UiViewModelEvent>()
}