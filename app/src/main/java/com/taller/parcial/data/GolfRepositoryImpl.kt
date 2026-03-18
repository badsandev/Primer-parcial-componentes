package com.taller.parcial.data


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
class GolfRepositoryImpl(
    private val clienteDao: ClienteDao,
    private val reservaDao: ReservaDao
) : GolfRepository  {
    private val hoy: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    // ── Clientes ──────────────────────────────────────────────────────

    override  fun getAllClientes(): Flow<List<Cliente>> =
        clienteDao.getAllClientes().map { list -> list.map { it.toDomain() } }

    override suspend fun getClienteById(id: Long): Cliente? =
        clienteDao.getClienteById(id)?.toDomain()

    override suspend fun insertCliente(cliente: Cliente): Long =
        clienteDao.insertCliente(ClienteEntity.fromDomain(cliente))

    override suspend fun updateCliente(cliente: Cliente) =
        clienteDao.updateCliente(ClienteEntity.fromDomain(cliente))

    // ── Reservas ──────────────────────────────────────────────────────

    override fun getAllReservas(): Flow<List<ReservaConCliente>> =
        reservaDao.getAllReservasConCliente().map { list -> list.toReservaConClienteList() }

    override fun getReservasHoy(): Flow<List<ReservaConCliente>> =
        reservaDao.getReservasActivasHoy(hoy).map { list -> list.toReservaConClienteList() }

    override fun buscarReservas(query: String): Flow<List<ReservaConCliente>> =
        reservaDao.buscarReservasPorCliente(query).map { list -> list.toReservaConClienteList() }

    override suspend fun getReservaById(id: Long): ReservaConCliente? =
        reservaDao.getReservaById(id)?.toReservaConCliente()

    /**
     * Retorna true si ya existe una reserva ACTIVA en la misma cancha, fecha y hora.
     * Pasar [excluirId] al editar para no comparar la reserva consigo misma.
     */
    override suspend fun existeConflicto(
        cancha: Int,
        fecha: String,
        hora: String,
        excluirId: Long
    ): Boolean = reservaDao.contarConflictos(cancha, fecha, hora, excluirId) > 0

    override suspend fun insertReserva(reserva: Reserva) {
        reservaDao.insertReserva(ReservaEntity.fromDomain(reserva))
    }
    override suspend fun updateReserva(reserva: Reserva) =
        reservaDao.updateReserva(ReservaEntity.fromDomain(reserva))

    override suspend fun deleteReserva(reserva: Reserva) =
        reservaDao.deleteReserva(ReservaEntity.fromDomain(reserva))

    // ── Métricas Dashboard ────────────────────────────────────────────

    override suspend fun getReservasHoyCount(): Int = reservaDao.countReservasHoy(hoy)
    override suspend fun getCanchasOcupadasCount(): Int = reservaDao.countCanchasOcupadasHoy(hoy)
    override suspend fun getReservasActivasCount(): Int = reservaDao.countReservasActivas()
    override suspend fun getReservasFinalizadasCount(): Int = reservaDao.countReservasFinalizadas()

    // ── Extensiones de mapeo ──────────────────────────────────────────

    private fun List<ReservaConClienteResult>.toReservaConClienteList() =
        map { it.toReservaConCliente() }

    private fun ReservaConClienteResult.toReservaConCliente() = ReservaConCliente(
        reserva = reserva.toDomain(),
        cliente = cliente.toDomain()
    )
}