package com.taller.parcial.model.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/** Resultado del JOIN entre reservas y clientes */
data class ReservaConClienteResult(
    @Embedded val reserva: ReservaEntity,
    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: ClienteEntity
)

@Dao
interface ReservaDao {

    /** Todas las reservas con datos del cliente (JOIN) */
    @Transaction
    @Query("SELECT * FROM reservas ORDER BY fecha DESC, hora ASC")
    fun getAllReservasConCliente(): Flow<List<ReservaConClienteResult>>

    /** Reservas filtradas por fecha */
    @Transaction
    @Query("SELECT * FROM reservas WHERE fecha = :fecha ORDER BY hora ASC")
    fun getReservasByFecha(fecha: String): Flow<List<ReservaConClienteResult>>

    /** Reservas activas hoy */
    @Transaction
    @Query("SELECT * FROM reservas WHERE fecha = :hoy AND estado = 'ACTIVA'")
    fun getReservasActivasHoy(hoy: String): Flow<List<ReservaConClienteResult>>

    /** Conteo de reservas de hoy */
    @Query("SELECT COUNT(*) FROM reservas WHERE fecha = :hoy")
    suspend fun countReservasHoy(hoy: String): Int

    /** Canchas ocupadas hoy (con reservas activas) */
    @Query("SELECT COUNT(DISTINCT numeroCancha) FROM reservas WHERE fecha = :hoy AND estado = 'ACTIVA'")
    suspend fun countCanchasOcupadasHoy(hoy: String): Int

    /** Total de reservas activas */
    @Query("SELECT COUNT(*) FROM reservas WHERE estado = 'ACTIVA'")
    suspend fun countReservasActivas(): Int

    /** Total de reservas finalizadas */
    @Query("SELECT COUNT(*) FROM reservas WHERE estado = 'FINALIZADA'")
    suspend fun countReservasFinalizadas(): Int

    /** Buscar reserva por ID */
    @Transaction
    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun getReservaById(id: Long): ReservaConClienteResult?

    /** Insertar nueva reserva, retorna el ID generado */
    /**
     * Verifica si ya existe una reserva ACTIVA para la misma cancha, fecha y hora.
     * Al editar, excluimos el ID actual para no bloquearse a sí misma.
     */
    @Query("""
        SELECT COUNT(*) FROM reservas
        WHERE numeroCancha = :cancha
          AND fecha        = :fecha
          AND hora         = :hora
          AND estado       = 'ACTIVA'
          AND id          != :excluirId
    """)
    suspend fun contarConflictos(
        cancha: Int,
        fecha: String,
        hora: String,
        excluirId: Long = 0L
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReserva(reserva: ReservaEntity): Long

    /** Actualizar reserva existente */
    @Update
    suspend fun updateReserva(reserva: ReservaEntity)

    /** Eliminar reserva */
    @Delete
    suspend fun deleteReserva(reserva: ReservaEntity)

    /** Buscar reservas por nombre de cliente */
    @Transaction
    @Query("""
        SELECT r.* FROM reservas r
        INNER JOIN clientes c ON r.clienteId = c.id
        WHERE c.nombre LIKE '%' || :query || '%'
        ORDER BY r.fecha DESC
    """)
    fun buscarReservasPorCliente(query: String): Flow<List<ReservaConClienteResult>>
}