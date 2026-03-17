package com.taller.parcial.model.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [ClienteEntity::class, ReservaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GolfDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile
        private var INSTANCE: GolfDatabase? = null

        fun getInstance(context: Context): GolfDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GolfDatabase::class.java,
                    "golf_club_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para sembrar datos de ejemplo (mocks) al crear la base de datos por primera vez.
     */
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Sembrar datos mock en un hilo de IO
            CoroutineScope(Dispatchers.IO).launch {
                sembrarDatosMock(db)
            }
        }

        private fun sembrarDatosMock(db: SupportSQLiteDatabase) {
            val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            // ── Clientes mock ──────────────────────────────────────────
            db.execSQL("INSERT INTO clientes (nombre, telefono) VALUES ('Carlos Mendoza', '310-555-0101')")
            db.execSQL("INSERT INTO clientes (nombre, telefono) VALUES ('Ana García', '320-555-0202')")
            db.execSQL("INSERT INTO clientes (nombre, telefono) VALUES ('Luis Ramírez', '315-555-0303')")
            db.execSQL("INSERT INTO clientes (nombre, telefono) VALUES ('María Torres', '300-555-0404')")
            db.execSQL("INSERT INTO clientes (nombre, telefono) VALUES ('Pedro Sánchez', '301-555-0505')")

            // ── Reservas mock (usando IDs 1–5 de los clientes) ─────────
            // Carlos - Cancha 2 - Activa
            db.execSQL("""
                INSERT INTO reservas (clienteId, fecha, hora, numeroCancha, cantidadJugadores, estado)
                VALUES (1, '$hoy', '10:00 AM', 2, 4, 'ACTIVA')
            """.trimIndent())

            // Ana - Cancha 3 - Activa
            db.execSQL("""
                INSERT INTO reservas (clienteId, fecha, hora, numeroCancha, cantidadJugadores, estado)
                VALUES (2, '$hoy', '11:30 AM', 3, 2, 'ACTIVA')
            """.trimIndent())

            // Luis - Cancha 1 - Finalizada
            db.execSQL("""
                INSERT INTO reservas (clienteId, fecha, hora, numeroCancha, cantidadJugadores, estado)
                VALUES (3, '$hoy', '1:00 PM', 1, 3, 'FINALIZADA')
            """.trimIndent())

            // María - Cancha 4 - Activa (día siguiente)
            db.execSQL("""
                INSERT INTO reservas (clienteId, fecha, hora, numeroCancha, cantidadJugadores, estado)
                VALUES (4, '$hoy', '3:00 PM', 4, 4, 'ACTIVA')
            """.trimIndent())

            // Pedro - Cancha 2 - Finalizada
            db.execSQL("""
                INSERT INTO reservas (clienteId, fecha, hora, numeroCancha, cantidadJugadores, estado)
                VALUES (5, '$hoy', '8:00 AM', 2, 2, 'FINALIZADA')
            """.trimIndent())
        }
    }
}