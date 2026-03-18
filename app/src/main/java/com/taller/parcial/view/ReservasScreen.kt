package com.taller.parcial.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taller.parcial.view.theme.GolfBackground
import com.taller.parcial.view.theme.GolfGray
import com.taller.parcial.view.theme.GolfGreen
import com.taller.parcial.view.theme.GolfGreenDark
import com.taller.parcial.view.theme.GolfWhite
import com.taller.parcial.viewmodel.ReservasViewModel

@Composable
fun ReservasScreen(
    viewModel: ReservasViewModel,
    onBack: () -> Unit,
    onNuevaReserva: () -> Unit,
    onEditarReserva: (Long) -> Unit
) {
    val state             by viewModel.uiState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }

    LaunchedEffect(state.mensajeExito, state.error) {
        state.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            GolfTopBar(
                titulo      = "Listado de Reservas",
                mostrarBack = true,
                onBack      = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onNuevaReserva,
                containerColor = GolfGreenDark
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Nueva Reserva",
                    tint               = GolfWhite
                )
            }
        },
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = GolfBackground
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value         = state.filtroBusqueda,
                    onValueChange = viewModel::onBusquedaChange,
                    placeholder   = { Text("Buscar por cliente…") },
                    leadingIcon   = {
                        Icon(
                            imageVector        = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint               = GolfGray
                        )
                    },
                    modifier   = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GolfGreen,
                        focusedLabelColor  = GolfGreen
                    )
                )
            }

            Text(
                text     = "${state.reservas.size} reservas encontradas",
                color    = GolfGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (state.isLoading) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = GolfGreen) }
            } else if (state.reservas.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron reservas", color = GolfGray)
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = state.reservas,
                        key   = { it.reserva.id }
                    ) { reservaConCliente ->
                        ReservaItem(
                            reservaConCliente = reservaConCliente,
                            onEditar          = { onEditarReserva(reservaConCliente.reserva.id) },
                            onEliminar        = { viewModel.eliminarReserva(reservaConCliente) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}