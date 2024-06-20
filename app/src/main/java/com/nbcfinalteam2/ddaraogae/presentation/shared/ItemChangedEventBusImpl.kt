package com.nbcfinalteam2.ddaraogae.presentation.shared

import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemChangedEventBusImpl @Inject constructor(
    dispatcher: CoroutineDispatcher
) : ItemChangedEventBus {
    private val _itemChangedEvent = MutableSharedFlow<Unit>(replay = 1)
    override val itemChangedEvent: SharedFlow<Unit> = _itemChangedEvent.asSharedFlow()

    private val _stampChangedEvent = MutableSharedFlow<Unit>(replay = 1)
    override val stampChangedEvent: SharedFlow<Unit> = _stampChangedEvent.asSharedFlow()

    private val scope = CoroutineScope(dispatcher)

    override fun notifyItemChanged() {
        scope.launch {
            _itemChangedEvent.emit(Unit)
        }
    }

    override fun notifyStampChanged() {
        scope.launch {
            _stampChangedEvent.emit(Unit)
        }
    }
}