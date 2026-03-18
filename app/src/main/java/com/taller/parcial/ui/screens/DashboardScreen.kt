// ui/screens/DashboardScreen.kt
package com.golfclub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golfclub.model.ReservaConCliente
import com.golfclub.ui.components.GolfButton
import com.golfclub.ui.components.GolfTopBar
import com.golfclub.ui.components.StatCard
import com.golfclub.ui.theme.*
import com.golfclub.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onVerReservas: () -> Unit,
    onNuevaReserva: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            GolfTopBar(
                titulo = "Golf Club",
                acciones = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector        = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint               = GolfWhite
                        )
                    }
                }
            )
        },
        containerColor = GolfBackground
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = GolfGreen) }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Grid de estadísticas ────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(
                            label    = "Reservas\nHoy",
                            valor    = state.reservasHoy,
                            color    = GolfGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label    = "Canchas\nOcupadas",
                            valor    = state.canchasOcupadas,
                            color    = GolfGreenDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(
                            label    = "Reservas\nActivas",
                            valor    = state.reservasActivas,
                            color    = GolfGreenLight,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label    = "Reservas\nFinalizadas",
                            valor    = state.reservasFinalizadas,
                            color    = GolfGray,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Próximas reservas ───────────────────────────────────
            item {
                Text(
                    text       = "Próximas Reservas",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = GolfGrayDark
                )
            }

            if (state.proximasReservas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = CardDefaults.cardColors(containerColor = GolfWhite)
                    ) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay reservas para hoy", color = GolfGray)
                        }
                    }
                }
            } else {
                items(state.proximasReservas) { reserva ->
                    ProximaReservaRow(reserva)
                }
            }

            // ── Botones de acción ────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(4.dp))
                GolfButton(
                    texto    = "Ver Todas las Reservas",
                    onClick  = onVerReservas,
                    modifier = Modifier.fillMaxWidth(),
                    esSecundario = true
                )
            }

            item {
                GolfButton(
                    texto    = "Nueva Reserva",
                    onClick  = onNuevaReserva,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProximaReservaRow(reserva: ReservaConCliente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(8.dp),
        colors   = CardDefaults.cardColors(containerColor = GolfWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = reserva.nombreCliente,
                fontWeight = FontWeight.SemiBold,
                color      = GolfGrayDark,
                fontSize   = 14.sp
            )
            Text(
                text     = "${reserva.reserva.hora}  –  Cancha ${reserva.reserva.numeroCancha}",
                color    = GolfGray,
                fontSize = 13.sp
            )
        }
    }
}
