package com.taller.parcial.model

data class ReservaConCliente(
    val reserva: Reserva,
    val cliente: Cliente
) {
    val nombreCliente get() = cliente.nombre
    val telefonoCliente get() = cliente.telefono
    val esActiva get() = reserva.estado == EstadoReserva.ACTIVA
}