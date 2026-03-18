package com.taller.parcial

import com.taller.parcial.data.GolfRepositoryImpl
import com.taller.parcial.data.GolfRepository
import com.taller.parcial.model.Cliente
import com.taller.parcial.model.Reserva
import com.taller.parcial.model.ReservaConCliente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGolfRepository : GolfRepository {

    val reservas = mutableListOf<ReservaConCliente>()
    val clientes = mutableListOf<Cliente>()
    var simularConflicto = false
    var simularError = false

    override fun getAllReservas(): Flow<List<ReservaConCliente>> = flowOf(reservas)
    override fun getReservasHoy(): Flow<List<ReservaConCliente>> = flowOf(reservas)
    override fun buscarReservas(query: String): Flow<List<ReservaConCliente>> =
        flowOf(reservas.filter { it.cliente.nombre.contains(query, ignoreCase = true) })

    override suspend fun getReservaById(id: Long): ReservaConCliente? =
        reservas.find { it.reserva.id == id }

    override fun getAllClientes(): Flow<List<Cliente>> {
        TODO("Not yet implemented")
    }

    override suspend fun getClienteById(id: Long): Cliente? {
        TODO("Not yet implemented")
    }

    override suspend fun insertCliente(cliente: Cliente): Long {
        clientes.add(cliente.copy(id = clientes.size + 1L))
        return clientes.size.toLong()
    }

    override suspend fun updateCliente(cliente: Cliente) {
        val idx = clientes.indexOfFirst { it.id == cliente.id }
        if (idx >= 0) clientes[idx] = cliente
    }

    override suspend fun insertReserva(reserva: Reserva) { /* guardado simulado */ }
    override suspend fun updateReserva(reserva: Reserva) { /* actualización simulada */ }
    override suspend fun deleteReserva(reserva: Reserva) {
        reservas.removeIf { it.reserva.id == reserva.id }
    }

    override suspend fun existeConflicto(cancha: Int, fecha: String, hora: String, excluirId: Long) = simularConflicto

    override suspend fun getReservasHoyCount() = reservas.size
    override suspend fun getCanchasOcupadasCount() = 2
    override suspend fun getReservasActivasCount() = reservas.size
    override suspend fun getReservasFinalizadasCount() = 0
}