package com.taller.parcial.data
import com.taller.parcial.model.Cliente
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val telefono: String
) {

    fun toDomain() = Cliente(id = id, nombre = nombre, telefono = telefono)

    companion object {
        /** Convierte un modelo de dominio a entidad para guardar en DB */
        fun fromDomain(cliente: Cliente) = ClienteEntity(
            id = cliente.id,
            nombre = cliente.nombre,
            telefono = cliente.telefono
        )
    }
}