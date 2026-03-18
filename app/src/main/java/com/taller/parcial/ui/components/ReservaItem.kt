// ui/components/ReservaItem.kt
package com.golfclub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golfclub.model.ReservaConCliente
import com.golfclub.ui.theme.*

/**
 * Card que muestra una reserva en el listado.
 * Incluye botones de editar y eliminar.
 */
@Composable
fun ReservaItem(
    reservaConCliente: ReservaConCliente,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape  = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GolfWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Información principal ──────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = reservaConCliente.nombreCliente,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = GolfGrayDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(label = reservaConCliente.reserva.fecha)
                    InfoChip(label = reservaConCliente.reserva.hora)
                    InfoChip(label = "Cancha ${reservaConCliente.reserva.numeroCancha}")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "👥 ${reservaConCliente.reserva.cantidadJugadores} jugadores · 📞 ${reservaConCliente.telefonoCliente}",
                    fontSize = 12.sp,
                    color    = GolfGray
                )
            }

            // ── Estado + acciones ─────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EstadoBadge(estado = reservaConCliente.reserva.estado)
                Row {
                    IconButton(onClick = onEditar, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector        = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint               = GolfGreen,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick  = { mostrarDialogoEliminar = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint               = GolfRed,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // ── Diálogo de confirmación de eliminación ─────────────────────────
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title            = { Text("Eliminar Reserva") },
            text             = {
                Text("¿Deseas eliminar la reserva de ${reservaConCliente.nombreCliente}? Esta acción no se puede deshacer.")
            },
            confirmButton    = {
                TextButton(onClick = {
                    mostrarDialogoEliminar = false
                    onEliminar()
                }) {
                    Text("Eliminar", color = GolfRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton    = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InfoChip(label: String) {
    Surface(
        shape  = RoundedCornerShape(4.dp),
        color  = GolfBackground
    ) {
        Text(
            text     = label,
            fontSize = 11.sp,
            color    = GolfGrayDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
