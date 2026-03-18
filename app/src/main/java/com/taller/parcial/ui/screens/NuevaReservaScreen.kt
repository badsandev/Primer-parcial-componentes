// ui/screens/NuevaReservaScreen.kt
package com.golfclub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golfclub.model.EstadoReserva
import com.golfclub.ui.components.*
import com.golfclub.ui.theme.*
import com.golfclub.viewmodel.NuevaReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaScreen(
    viewModel: NuevaReservaViewModel,
    reservaId: Long?,
    onBack: () -> Unit,
    onGuardado: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Cargar datos si es edición
    LaunchedEffect(reservaId) {
        if (reservaId != null && reservaId != 0L) {
            viewModel.cargarReservaParaEdicion(reservaId)
        }
    }

    // Navegar atrás al guardar exitosamente
    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onGuardado()
    }

    Scaffold(
        topBar = {
            GolfTopBar(
                titulo      = if (state.isEdicion) "Editar Reserva" else "Nueva Reserva",
                mostrarBack = true,
                onBack      = onBack
            )
        },
        containerColor = GolfBackground
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { CircularProgressIndicator(color = GolfGreen) }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Sección: Datos del cliente ──────────────────────────
            SeccionLabel("Datos del Cliente")

            GolfTextField(
                valor         = state.nombreCliente,
                onValueChange = viewModel::onNombreChange,
                label         = "Nombre del Cliente",
                errorMensaje  = state.errorNombre
            )

            GolfTextField(
                valor         = state.telefono,
                onValueChange = viewModel::onTelefonoChange,
                label         = "Teléfono",
                errorMensaje  = state.errorTelefono
            )

            // ── Sección: Detalles de la reserva ─────────────────────
            SeccionLabel("Detalles de la Reserva")

            GolfTextField(
                valor         = state.fecha,
                onValueChange = viewModel::onFechaChange,
                label         = "Fecha (dd/MM/yyyy)",
                errorMensaje  = state.errorFecha,
                trailingIcon  = {
                    Icon(
                        imageVector        = Icons.Default.CalendarMonth,
                        contentDescription = "Fecha",
                        tint               = GolfGreen
                    )
                }
            )

            GolfTextField(
                valor         = state.hora,
                onValueChange = viewModel::onHoraChange,
                label         = "Hora (Ej: 10:00 AM)",
                trailingIcon  = {
                    Icon(
                        imageVector        = Icons.Default.Schedule,
                        contentDescription = "Hora",
                        tint               = GolfGreen
                    )
                }
            )

            // ── Número de Cancha (Dropdown) ──────────────────────────
            SpinnerField(
                label      = "Número de Cancha",
                opciones    = (1..9).map { "Cancha $it" },
                seleccionado = "Cancha ${state.numeroCancha}",
                onSeleccion  = { index -> viewModel.onCanchaChange(index + 1) }
            )

            // ── Cantidad de Jugadores (Dropdown) ─────────────────────
            SpinnerField(
                label        = "Cantidad de Jugadores",
                opciones      = (1..8).map { "$it jugador${if (it > 1) "es" else ""}" },
                seleccionado  = "${state.cantidadJugadores} jugador${if (state.cantidadJugadores > 1) "es" else ""}",
                onSeleccion   = { index -> viewModel.onJugadoresChange(index + 1) }
            )

            // ── Estado (Dropdown) ─────────────────────────────────────
            SpinnerField(
                label        = "Estado",
                opciones      = EstadoReserva.values().map { it.label },
                seleccionado  = state.estado.label,
                onSeleccion   = { index -> viewModel.onEstadoChange(EstadoReserva.values()[index]) }
            )

            // ── Mostrar error general ─────────────────────────────────
            state.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text     = error,
                        color    = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Botones ────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GolfButton(
                    texto         = "Cancelar",
                    onClick       = onBack,
                    modifier      = Modifier.weight(1f),
                    esSecundario  = true
                )
                GolfButton(
                    texto    = if (state.isEdicion) "Actualizar" else "Guardar",
                    onClick  = viewModel::guardarReserva,
                    modifier = Modifier.weight(1f),
                    habilitado = !state.isLoading
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Componentes locales ───────────────────────────────────────────────────────

@Composable
private fun SeccionLabel(texto: String) {
    Text(
        text       = texto,
        fontWeight = FontWeight.Bold,
        fontSize   = 14.sp,
        color      = GolfGreenDark
    )
    HorizontalDivider(color = GolfGreenAccent, thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpinnerField(
    label: String,
    opciones: List<String>,
    seleccionado: String,
    onSeleccion: (Int) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded         = expandido,
        onExpandedChange = { expandido = !expandido }
    ) {
        OutlinedTextField(
            value         = seleccionado,
            onValueChange = {},
            readOnly      = true,
            label         = { Text(label) },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier      = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GolfGreen,
                focusedLabelColor  = GolfGreen
            )
        )
        ExposedDropdownMenu(
            expanded         = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEachIndexed { index, opcion ->
                DropdownMenuItem(
                    text    = { Text(opcion) },
                    onClick = {
                        onSeleccion(index)
                        expandido = false
                    }
                )
            }
        }
    }
}
