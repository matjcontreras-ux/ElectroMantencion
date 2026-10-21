
package com.electromechanics.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsistenciaViewModel : ViewModel() {

    var jornadaIniciada by mutableStateOf(false)
        private set

    var horaEntrada by mutableStateOf("")
        private set

    var horaSalida by mutableStateOf("")
        private set

    var mensaje by mutableStateOf("")
        private set

    private val formatoVisible =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private val formatoBaseDatos =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun restaurarEstado(entrada: String?, salida: String?) {
        val entradaFecha = convertirFecha(entrada)
        val salidaFecha = convertirFecha(salida)

        horaEntrada = entradaFecha?.format(formatoVisible) ?: ""
        horaSalida = salidaFecha?.format(formatoVisible) ?: ""

        jornadaIniciada = entradaFecha != null &&
                (salidaFecha == null || entradaFecha.isAfter(salidaFecha))

        mensaje = if (entradaFecha != null) {
            "Se recuperaron los registros guardados."
        } else {
            ""
        }
    }

    fun prepararFechaVisible(): String {
        return LocalDateTime.now().format(formatoVisible)
    }

    fun prepararFechaBaseDatos(): String {
        return LocalDateTime.now().format(formatoBaseDatos)
    }

    fun puedeRegistrar(): Boolean {
        return jornadaIniciada || horaSalida.isEmpty()
    }

    fun actualizarEntrada(fecha: String) {
        val fechaConvertida = convertirFecha(fecha)
        horaEntrada = fechaConvertida?.format(formatoVisible) ?: fecha
        horaSalida = ""
        jornadaIniciada = true
        mensaje = "Entrada registrada y guardada correctamente."
    }

    fun actualizarSalida(fecha: String) {
        val fechaConvertida = convertirFecha(fecha)
        horaSalida = fechaConvertida?.format(formatoVisible) ?: fecha
        jornadaIniciada = false
        mensaje = "Salida registrada y guardada correctamente."
    }

    fun mostrarMensaje(texto: String) {
        mensaje = texto
    }

    private fun convertirFecha(fecha: String?): LocalDateTime? {
        if (fecha.isNullOrBlank()) return null

        val formatos = listOf(
            formatoBaseDatos,
            formatoVisible
        )

        for (formato in formatos) {
            try {
                return LocalDateTime.parse(fecha, formato)
            } catch (_: Exception) {
                // Probar el siguiente formato.
            }
        }

        return null
    }
}
