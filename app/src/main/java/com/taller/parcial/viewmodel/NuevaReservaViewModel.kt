package com.taller.parcial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.taller.parcial.model.Cliente
import com.taller.parcial.model.EstadoReserva
import com.taller.parcial.model.Reserva
import com.taller.parcial.data.GolfRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FormularioReservaState(
    // Campos del formulario
    val nombreCliente: String = "",
    val telefono: String = "",
    val fecha: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    val hora: String = "10:00 AM",
    val numeroCancha: Int = 1,
    val cantidadJugadores: Int = 4,
    val estado: EstadoReserva = EstadoReserva.ACTIVA,

    // Control de flujo
    val isLoading: Boolean = false,
    val isEdicion: Boolean = false,
    val reservaId: Long = 0,
    val clienteId: Long = 0,
    val guardadoExitoso: Boolean = false,
    val error: String? = null,

    // Errores de validación por campo
    val errorNombre: String? = null,
    val errorTelefono: String? = null,
    val errorFecha: String? = null
)

class NuevaReservaViewModel(private val repository: GolfRepository) : ViewModel() {

    private val _state = MutableStateFlow(FormularioReservaState())
    val state: StateFlow<FormularioReservaState> = _state.asStateFlow()

    /** Cargar una reserva existente para edición */
    fun cargarReservaParaEdicion(reservaId: Long) {
        if (reservaId == 0L) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val reservaConCliente = repository.getReservaById(reservaId)
            reservaConCliente?.let { rc ->
                _state.update {
                    it.copy(
                        isEdicion = true,
                        reservaId = rc.reserva.id,
                        clienteId = rc.cliente.id,
                        nombreCliente = rc.cliente.nombre,
                        telefono = rc.cliente.telefono,
                        fecha = rc.reserva.fecha,
                        hora = rc.reserva.hora,
                        numeroCancha = rc.reserva.numeroCancha,
                        cantidadJugadores = rc.reserva.cantidadJugadores,
                        estado = rc.reserva.estado,
                        isLoading = false
                    )
                }
            } ?: _state.update { it.copy(isLoading = false, error = "Reserva no encontrada") }
        }
    }

    // ── Actualización de campos ───────────────────────────────────────

    fun onNombreChange(value: String) =
        _state.update { it.copy(nombreCliente = value, errorNombre = null) }

    fun onTelefonoChange(value: String) =
        _state.update { it.copy(telefono = value, errorTelefono = null) }

    fun onFechaChange(value: String) =
        _state.update { it.copy(fecha = value, errorFecha = null) }

    fun onHoraChange(value: String) =
        _state.update { it.copy(hora = value) }

    fun onCanchaChange(value: Int) =
        _state.update { it.copy(numeroCancha = value) }

    fun onJugadoresChange(value: Int) =
        _state.update { it.copy(cantidadJugadores = value) }

    fun onEstadoChange(value: EstadoReserva) =
        _state.update { it.copy(estado = value) }

    // ── Guardar ───────────────────────────────────────────────────────

    fun guardarReserva() {
        if (!validar()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val st = _state.value

                // ── Validar conflicto de cancha ───────────────────────
                val hayConflicto = repository.existeConflicto(
                    cancha     = st.numeroCancha,
                    fecha      = st.fecha.trim(),
                    hora       = st.hora.trim(),
                    excluirId  = if (st.isEdicion) st.reservaId else 0L
                )
                if (hayConflicto) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "La Cancha ${st.numeroCancha} ya está reservada el ${st.fecha} a las ${st.hora}. " +
                                    "Elige otra cancha u otro horario."
                        )
                    }
                    return@launch
                }

                // 1. Insertar o actualizar cliente
                val cliente = Cliente(
                    id = st.clienteId,
                    nombre = st.nombreCliente.trim(),
                    telefono = st.telefono.trim()
                )
                val clienteId = if (st.isEdicion && st.clienteId != 0L) {
                    repository.updateCliente(cliente)
                    st.clienteId
                } else {
                    repository.insertCliente(cliente)
                }

                // 2. Insertar o actualizar reserva
                val reserva = Reserva(
                    id = if (st.isEdicion) st.reservaId else 0,
                    clienteId = clienteId,
                    fecha = st.fecha.trim(),
                    hora = st.hora.trim(),
                    numeroCancha = st.numeroCancha,
                    cantidadJugadores = st.cantidadJugadores,
                    estado = st.estado
                )
                if (st.isEdicion) repository.updateReserva(reserva)
                else repository.insertReserva(reserva)

                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Error al guardar: ${e.message}")
                }
            }
        }
    }

    private fun validar(): Boolean {
        val st = _state.value
        var valido = true
        if (st.nombreCliente.isBlank()) {
            _state.update { it.copy(errorNombre = "El nombre es requerido") }
            valido = false
        }
        if (st.telefono.isBlank()) {
            _state.update { it.copy(errorTelefono = "El teléfono es requerido") }
            valido = false
        }
        if (st.fecha.isBlank()) {
            _state.update { it.copy(errorFecha = "La fecha es requerida") }
            valido = false
        }
        return valido
    }

    fun limpiarError() = _state.update { it.copy(error = null) }
}
