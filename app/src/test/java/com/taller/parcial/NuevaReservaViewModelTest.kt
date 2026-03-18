package com.taller.parcial

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.taller.parcial.data.GolfRepository
import com.taller.parcial.viewmodel.NuevaReservaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class NuevaReservaViewModelTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: FakeGolfRepository
    private lateinit var viewModel: NuevaReservaViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = FakeGolfRepository()
        viewModel = NuevaReservaViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Prueba 1 ──────────────────────────────────────────────────────
    // Verifica que el formulario no se guarda si los campos están vacíos
    @Test
    fun `guardar con campos vacíos muestra errores de validación`() = runTest {
        viewModel.onNombreChange("")
        viewModel.onTelefonoChange("")
        viewModel.onFechaChange("")

        viewModel.guardarReserva()

        val state = viewModel.state.value
        assertNotNull(state.errorNombre)
        assertNotNull(state.errorTelefono)
        assertNotNull(state.errorFecha)
        assertFalse(state.guardadoExitoso)
    }

    // ── Prueba 2 ──────────────────────────────────────────────────────
    // Verifica que una reserva válida se guarda correctamente
    @Test
    fun `guardar con datos válidos resulta en guardado exitoso`() = runTest {
        repo.simularConflicto = false
        viewModel.onNombreChange("Ana García")
        viewModel.onTelefonoChange("3109876543")
        viewModel.onFechaChange("17/03/2026")

        viewModel.guardarReserva()

        val state = viewModel.state.value
        assertTrue(state.guardadoExitoso)
        assertNull(state.error)
    }

    // ── Prueba 3 ──────────────────────────────────────────────────────
    // Verifica que no se puede reservar una cancha ya ocupada
    @Test
    fun `guardar con conflicto de cancha muestra error y no guarda`() = runTest {
        repo.simularConflicto = true
        viewModel.onNombreChange("Luis Torres")
        viewModel.onTelefonoChange("3001111111")
        viewModel.onFechaChange("17/03/2026")

        viewModel.guardarReserva()

        val state = viewModel.state.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("ya está reservada"))
        assertFalse(state.guardadoExitoso)
    }
}