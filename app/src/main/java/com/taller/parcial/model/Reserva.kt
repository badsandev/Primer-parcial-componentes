package com.taller.parcial.model

data class Reserva(
    val id: Long = 0,
    val clienteId: Long,
    val fecha: String,
    val hora: String,
    val numeroCancha: Int,
    val cantidadJugadores: Int,
    val estado: EstadoReserva
)

enum class EstadoReserva(val label: String) {
    ACTIVA("Activa"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada")
}