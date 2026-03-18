package com.taller.parcial.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taller.parcial.data.GolfRepository
import com.taller.parcial.view.DashboardScreen
import com.taller.parcial.viewmodel.DashboardViewModel
import com.taller.parcial.viewmodel.NuevaReservaViewModel
import com.taller.parcial.viewmodel.ReservasViewModel

object Routes {
    const val DASHBOARD      = "dashboard"
    const val RESERVAS       = "reservas"
    const val NUEVA_RESERVA  = "nueva_reserva"
    const val EDITAR_RESERVA = "editar_reserva/{reservaId}"
    fun editarReserva(id: Long) = "editar_reserva/$id"
}

@Composable
fun GolfNavGraph(repository: GolfRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel      = DashboardViewModel(repository),
                onVerReservas  = { navController.navigate(Routes.RESERVAS) },
                onNuevaReserva = { navController.navigate(Routes.NUEVA_RESERVA) }
            )
        }

        composable(Routes.RESERVAS) {
            ReservasScreen(
                viewModel       = ReservasViewModel(repository),
                onBack          = { navController.popBackStack() },
                onNuevaReserva  = { navController.navigate(Routes.NUEVA_RESERVA) },
                onEditarReserva = { id -> navController.navigate(Routes.editarReserva(id)) }
            )
        }

        composable(Routes.NUEVA_RESERVA) {
            NuevaReservaScreen(
                viewModel  = NuevaReservaViewModel(repository),
                reservaId  = null,
                onBack     = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.EDITAR_RESERVA,
            arguments = listOf(navArgument("reservaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getLong("reservaId") ?: 0L
            NuevaReservaScreen(
                viewModel  = NuevaReservaViewModel(repository),
                reservaId  = reservaId,
                onBack     = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}