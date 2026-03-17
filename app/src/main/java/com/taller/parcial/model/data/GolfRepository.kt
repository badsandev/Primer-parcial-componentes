package com.taller.parcial.model.data


import com.taller.parcial.model.Cliente
import com.taller.parcial.model.Reserva
import com.taller.parcial.model.ReservaConCliente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio único de acceso a datos para la app Golf Club.
 * Expone la lógica de negocio hacia los ViewModels, abstrayendo ROOM.
 */
class GolfRepository(
    private val clienteDao: ClienteDao,
    private val reservaDao: ReservaDao
) {
    private val hoy: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    // ── Clientes ──────────────────────────────────────────────────────

    fun getAllClientes(): Flow<List<Cliente>> =
        clienteDao.getAllClientes().map { list -> list.map { it.toDomain() } }

    suspend fun getClienteById(id: Long): Cliente? =
        clienteDao.getClienteById(id)?.toDomain()

    suspend fun insertCliente(cliente: Cliente): Long =
        clienteDao.insertCliente(ClienteEntity.fromDomain(cliente))

    suspend fun updateCliente(cliente: Cliente) =
        clienteDao.updateCliente(ClienteEntity.fromDomain(cliente))

    // ── Reservas ──────────────────────────────────────────────────────

    fun getAllReservas(): Flow<List<ReservaConCliente>> =
        reservaDao.getAllReservasConCliente().map { list -> list.toReservaConClienteList() }

    fun getReservasHoy(): Flow<List<ReservaConCliente>> =
        reservaDao.getReservasActivasHoy(hoy).map { list -> list.toReservaConClienteList() }

    fun buscarReservas(query: String): Flow<List<ReservaConCliente>> =
        reservaDao.buscarReservasPorCliente(query).map { list -> list.toReservaConClienteList() }

    suspend fun getReservaById(id: Long): ReservaConCliente? =
        reservaDao.getReservaById(id)?.toReservaConCliente()

    /**
     * Retorna true si ya existe una reserva ACTIVA en la misma cancha, fecha y hora.
     * Pasar [excluirId] al editar para no comparar la reserva consigo misma.
     */
    suspend fun existeConflicto(
        cancha: Int,
        fecha: String,
        hora: String,
        excluirId: Long = 0L
    ): Boolean = reservaDao.contarConflictos(cancha, fecha, hora, excluirId) > 0

    suspend fun insertReserva(reserva: Reserva) =
        reservaDao.insertReserva(ReservaEntity.fromDomain(reserva))

    suspend fun updateReserva(reserva: Reserva) =
        reservaDao.updateReserva(ReservaEntity.fromDomain(reserva))

    suspend fun deleteReserva(reserva: Reserva) =
        reservaDao.deleteReserva(ReservaEntity.fromDomain(reserva))

    // ── Métricas Dashboard ────────────────────────────────────────────

    suspend fun getReservasHoyCount(): Int = reservaDao.countReservasHoy(hoy)
    suspend fun getCanchasOcupadasCount(): Int = reservaDao.countCanchasOcupadasHoy(hoy)
    suspend fun getReservasActivasCount(): Int = reservaDao.countReservasActivas()
    suspend fun getReservasFinalizadasCount(): Int = reservaDao.countReservasFinalizadas()

    // ── Extensiones de mapeo ──────────────────────────────────────────

    private fun List<ReservaConClienteResult>.toReservaConClienteList() =
        map { it.toReservaConCliente() }

    private fun ReservaConClienteResult.toReservaConCliente() = ReservaConCliente(
        reserva = reserva.toDomain(),
        cliente = cliente.toDomain()
    )
}