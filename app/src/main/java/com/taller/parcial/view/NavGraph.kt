package com.taller.parcial.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taller.parcial.data.GolfRepository
import com.taller.parcial.data.GolfRepositoryImpl
import com.taller.parcial.view.DashboardScreen
import com.taller.parcial.viewmodel.DashboardViewModel
import com.taller.parcial.viewmodel.NuevaReservaViewModel
import com.taller.parcial.viewmodel.ReservasViewModel

// ───────── FACTORIES ─────────

class NuevaReservaViewModelFactory(
    private val repository: GolfRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NuevaReservaViewModel(repository) as T
    }
}

class ReservasViewModelFactory(
    private val repository: GolfRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReservasViewModel(repository as GolfRepositoryImpl) as T
    }
}

class DashboardViewModelFactory(
    private val repository: GolfRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository as GolfRepositoryImpl) as T
    }
}

// ───────── ROUTES ─────────

object Routes {
    const val DASHBOARD      = "dashboard"
    const val RESERVAS       = "reservas"
    const val NUEVA_RESERVA  = "nueva_reserva"
    const val EDITAR_RESERVA = "editar_reserva/{reservaId}"

    fun editarReserva(id: Long) = "editar_reserva/$id"
}

// ───────── NAVGRAPH ─────────

@Composable
fun GolfNavGraph(repository: GolfRepository) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {

        // DASHBOARD
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(repository)
            )

            DashboardScreen(
                viewModel      = viewModel,
                onVerReservas  = { navController.navigate(Routes.RESERVAS) },
                onNuevaReserva = { navController.navigate(Routes.NUEVA_RESERVA) }
            )
        }

        // LISTADO RESERVAS
        composable(Routes.RESERVAS) {
            val viewModel: ReservasViewModel = viewModel(
                factory = ReservasViewModelFactory(repository)
            )

            ReservasScreen(
                viewModel       = viewModel,
                onBack          = { navController.popBackStack() },
                onNuevaReserva  = { navController.navigate(Routes.NUEVA_RESERVA) },
                onEditarReserva = { id ->
                    navController.navigate(Routes.editarReserva(id))
                }
            )
        }

        // NUEVA RESERVA
        composable(Routes.NUEVA_RESERVA) {
            val viewModel: NuevaReservaViewModel = viewModel(
                factory = NuevaReservaViewModelFactory(repository)
            )

            NuevaReservaScreen(
                viewModel  = viewModel,
                reservaId  = null,
                onBack     = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }

        // EDITAR RESERVA
        composable(
            route = Routes.EDITAR_RESERVA,
            arguments = listOf(navArgument("reservaId") { type = NavType.LongType })
        ) { backStackEntry ->

            val reservaId = backStackEntry.arguments?.getLong("reservaId") ?: 0L

            val viewModel: NuevaReservaViewModel = viewModel(
                factory = NuevaReservaViewModelFactory(repository)
            )

            NuevaReservaScreen(
                viewModel  = viewModel,
                reservaId  = reservaId,
                onBack     = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}