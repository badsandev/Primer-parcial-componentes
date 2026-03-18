package com.taller.parcial.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.parcial.model.EstadoReserva
import com.taller.parcial.view.theme.GolfGreen
import com.taller.parcial.view.theme.GolfGreenDark
import com.taller.parcial.view.theme.GolfOrange
import com.taller.parcial.view.theme.GolfRed
import com.taller.parcial.view.theme.GolfWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GolfTopBar(
    titulo: String,
    mostrarBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(text = titulo, fontWeight = FontWeight.Bold, color = GolfWhite)
        },
        navigationIcon = {
            if (mostrarBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint               = GolfWhite
                    )
                }
            }
        },
        actions = acciones,
        colors  = TopAppBarDefaults.topAppBarColors(containerColor = GolfGreenDark)
    )
}

@Composable
fun StatCard(
    label: String,
    valor: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = label,
                color      = GolfWhite,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )
            Text(
                text       = valor.toString(),
                color      = GolfWhite,
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EstadoBadge(estado: EstadoReserva) {
    val (color, texto) = when (estado) {
        EstadoReserva.ACTIVA     -> Pair(GolfGreen,  "Activa")
        EstadoReserva.FINALIZADA -> Pair(GolfRed,    "Finalizada")
        EstadoReserva.CANCELADA  -> Pair(GolfOrange, "Cancelada")
    }
    Box(
        modifier         = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, color = GolfWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun GolfButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier    = Modifier,
    habilitado: Boolean   = true,
    esSecundario: Boolean = false
) {
    if (esSecundario) {
        OutlinedButton(
            onClick  = onClick,
            modifier = modifier.height(50.dp),
            enabled  = habilitado,
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = GolfGreen)
        ) { Text(texto, fontWeight = FontWeight.SemiBold) }
    } else {
        Button(
            onClick  = onClick,
            modifier = modifier.height(50.dp),
            enabled  = habilitado,
            colors   = ButtonDefaults.buttonColors(containerColor = GolfGreenDark)
        ) { Text(texto, color = GolfWhite, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
fun GolfTextField(
    valor: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier    = Modifier,
    errorMensaje: String? = null,
    soloLectura: Boolean  = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value          = valor,
        onValueChange  = onValueChange,
        label          = { Text(label) },
        modifier       = modifier.fillMaxWidth(),
        isError        = errorMensaje != null,
        readOnly       = soloLectura,
        supportingText = errorMensaje?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        trailingIcon   = trailingIcon,
        singleLine     = true,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GolfGreen,
            focusedLabelColor  = GolfGreen,
            cursorColor        = GolfGreen
        )
    )
}