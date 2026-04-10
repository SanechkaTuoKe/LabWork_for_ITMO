package org.example.ui.instruments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.domain.Instrument
import org.example.domain.InstrumentType
import org.example.service.InstrumentService

class InstrumentController(private val service: InstrumentService) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val _instruments: MutableState<List<Instrument>> =
        mutableStateOf(emptyList())
    val instruments: State<List<Instrument>> = _instruments

    private val _selected: MutableState<Instrument?> =
        mutableStateOf(null)
    val selected: State<Instrument?> = _selected

    private val _error: MutableState<String?> =
        mutableStateOf(null)
    val error: State<String?> = _error

    fun load() {
        scope.launch {
            try {
                val data: List<Instrument> = service.getAll()
                _instruments.value = data
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun select(instrument: Instrument) {
        _selected.value = instrument
    }

    fun add(
        name: String,
        type: InstrumentType,
        inventory: String?,
        location: String
    ) {
        scope.launch {
            try {
                service.add(name, type, inventory, location)
                val data: List<Instrument> = service.getAll()
                _instruments.value = data
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun delete(id: Long) {
        scope.launch {
            try {
                service.delete(id)
                val data: List<Instrument> = service.getAll()
                _instruments.value = data
                _selected.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}