package com.taller.parcial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taller.parcial.model.ReservaConCliente
import com.taller.parcial.data.GolfRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReservasUiState(
    val reservas: List<ReservaConCliente> = emptyList(),
    val filtroBusqueda: String = "",
    val isLoading: Boolean = true,
    val mensajeExito: String? = null,
    val error: String? = null
)

class ReservasViewModel(private val repository: GolfRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservasUiState())
    val uiState: StateFlow<ReservasUiState> = _uiState.asStateFlow()

    private val _busqueda = MutableStateFlow("")

    init {
        // Reaccionar al cambio de búsqueda con debounce
        viewModelScope.launch {
            _busqueda
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) repository.getAllReservas()
                    else repository.buscarReservas(query)
                }
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { reservas ->
                    _uiState.update { it.copy(reservas = reservas, isLoading = false) }
                }
        }
    }

    fun onBusquedaChange(query: String) {
        _busqueda.value = query
        _uiState.update { it.copy(filtroBusqueda = query) }
    }

    fun eliminarReserva(reservaConCliente: ReservaConCliente) {
        viewModelScope.launch {
            try {
                repository.deleteReserva(reservaConCliente.reserva)
                _uiState.update { it.copy(mensajeExito = "Reserva eliminada correctamente") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeExito = null, error = null) }
    }
}