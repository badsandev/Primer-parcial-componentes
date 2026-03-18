package com.taller.parcial.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun getAllClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getClienteById(id: Long): ClienteEntity?

    @Query("SELECT * FROM clientes WHERE nombre LIKE '%' || :query || '%'")
    fun buscarClientes(query: String): Flow<List<ClienteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity): Long

    @Update
    suspend fun updateCliente(cliente: ClienteEntity)

    @Delete
    suspend fun deleteCliente(cliente: ClienteEntity)

    @Query("SELECT COUNT(*) FROM clientes")
    suspend fun countClientes(): Int
}
