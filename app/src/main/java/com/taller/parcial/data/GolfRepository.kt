package com.taller.parcial.data

import com.taller.parcial.model.Cliente
import com.taller.parcial.model.Reserva
import com.taller.parcial.model.ReservaConCliente
import kotlinx.coroutines.flow.Flow

interface GolfRepository {
    fun getAllClientes(): Flow<List<Cliente>>
    suspend fun getClienteById(id: Long): Cliente?
    suspend fun insertCliente(cliente: Cliente): Long
    suspend fun updateCliente(cliente: Cliente)

    fun getAllReservas(): Flow<List<ReservaConCliente>>
    fun getReservasHoy(): Flow<List<ReservaConCliente>>
    fun buscarReservas(query: String): Flow<List<ReservaConCliente>>
    suspend fun getReservaById(id: Long): ReservaConCliente?
    suspend fun insertReserva(reserva: Reserva)
    suspend fun updateReserva(reserva: Reserva)
    suspend fun deleteReserva(reserva: Reserva)
    suspend fun existeConflicto(cancha: Int, fecha: String, hora: String, excluirId: Long = 0L): Boolean

    suspend fun getReservasHoyCount(): Int
    suspend fun getCanchasOcupadasCount(): Int
    suspend fun getReservasActivasCount(): Int
    suspend fun getReservasFinalizadasCount(): Int
}