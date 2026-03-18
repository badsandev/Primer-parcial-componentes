package com.taller.parcial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taller.parcial.model.ReservaConCliente
import com.taller.parcial.data.GolfRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val reservasHoy: Int = 0,
    val canchasOcupadas: Int = 0,
    val reservasActivas: Int = 0,
    val reservasFinalizadas: Int = 0,
    val proximasReservas: List<ReservaConCliente> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class DashboardViewModel(private val repository: GolfRepositoryImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    private fun cargarDashboard() {
        viewModelScope.launch {
            try {
                repository.getReservasHoy().collect { reservasHoy ->
                    _uiState.update { state ->
                        state.copy(
                            reservasHoy         = repository.getReservasHoyCount(),
                            canchasOcupadas     = repository.getCanchasOcupadasCount(),
                            reservasActivas     = repository.getReservasActivasCount(),
                            reservasFinalizadas = repository.getReservasFinalizadasCount(),
                            proximasReservas    = reservasHoy.take(5),
                            isLoading           = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        cargarDashboard()
    }
}