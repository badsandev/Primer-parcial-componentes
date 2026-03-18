package com.taller.parcial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.taller.parcial.data.GolfDatabase
import com.taller.parcial.data.GolfRepositoryImpl
import com.taller.parcial.ui.GolfNavGraph
import com.taller.parcial.ui.theme.GolfClubTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database   = GolfDatabase.getInstance(applicationContext)
        val repository = GolfRepositoryImpl(
            clienteDao = database.clienteDao(),
            reservaDao = database.reservaDao()
        )

        setContent {
            GolfClubTheme {
                GolfNavGraph(repository = repository)
            }
        }
    }
}