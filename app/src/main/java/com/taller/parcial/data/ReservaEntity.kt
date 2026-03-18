package com.taller.parcial.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.taller.parcial.model.EstadoReserva
import com.taller.parcial.model.Reserva

@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clienteId: Long,
    val fecha: String,
    val hora: String,
    val numeroCancha: Int,
    val cantidadJugadores: Int,
    val estado: String   // Guardamos el name() del enum como String
) {
    fun toDomain() = Reserva(
        id = id,
        clienteId = clienteId,
        fecha = fecha,
        hora = hora,
        numeroCancha = numeroCancha,
        cantidadJugadores = cantidadJugadores,
        estado = EstadoReserva.valueOf(estado)
    )

    companion object {
        fun fromDomain(reserva: Reserva) = ReservaEntity(
            id = reserva.id,
            clienteId = reserva.clienteId,
            fecha = reserva.fecha,
            hora = reserva.hora,
            numeroCancha = reserva.numeroCancha,
            cantidadJugadores = reserva.cantidadJugadores,
            estado = reserva.estado.name
        )
    }
}