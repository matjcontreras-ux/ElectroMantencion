
package com.electromechanics.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.electromechanics.app.data.BaseDatosLocal
import com.electromechanics.app.viewmodel.AsistenciaViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAsistencia(
    alVolver: () -> Unit,
    alIrEvidencia: () -> Unit,
    asistenciaViewModel: AsistenciaViewModel = viewModel()
) {
    val contexto = LocalContext.current

    val baseDatos = remember(contexto) {
        BaseDatosLocal(contexto.applicationContext)
    }

    val jornadaIniciada = asistenciaViewModel.jornadaIniciada
    val horaEntrada = asistenciaViewModel.horaEntrada
    val horaSalida = asistenciaViewModel.horaSalida
    val mensaje = asistenciaViewModel.mensaje

    val formatoOrdenable =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    LaunchedEffect(Unit) {
        try {
            val entradaGuardada =
                baseDatos.obtenerUltimoRegistro("ENTRADA")

            val salidaGuardada =
                baseDatos.obtenerUltimoRegistro("SALIDA")

            asistenciaViewModel.restaurarEstado(
                entrada = entradaGuardada,
                salida = salidaGuardada
            )
        } catch (e: Exception) {
            asistenciaViewModel.mostrarMensaje(
                "No se pudieron recuperar los registros guardados."
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registro de asistencia")
                }
            )
        }
    ) { espacio ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Mi jornada laboral",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = if (jornadaIniciada) {
                            "Estado: jornada iniciada"
                        } else if (horaSalida.isNotEmpty()) {
                            "Estado: jornada finalizada"
                        } else {
                            "Estado: jornada no iniciada"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (horaEntrada.isEmpty()) {
                            "Hora de entrada: sin registrar"
                        } else {
                            "Hora de entrada: $horaEntrada"
                        }
                    )

                    Text(
                        text = if (horaSalida.isEmpty()) {
                            "Hora de salida: sin registrar"
                        } else {
                            "Hora de salida: $horaSalida"
                        }
                    )
                }
            }

            Button(
                onClick = {
                    val ahora = LocalDateTime.now()
                    val fechaHora = ahora.format(formatoOrdenable)
                    val fechaVisible =
                        asistenciaViewModel.prepararFechaVisible()

                    try {
                        if (!jornadaIniciada && horaSalida.isEmpty()) {

                            val guardado = baseDatos.guardarRegistro(
                                tipo = "ENTRADA",
                                fechaHora = fechaHora
                            )

                            if (guardado) {
                                asistenciaViewModel.actualizarEntrada(
                                    fechaVisible
                                )
                            } else {
                                asistenciaViewModel.mostrarMensaje(
                                    "No se pudo guardar la entrada."
                                )
                            }

                        } else if (jornadaIniciada) {

                            val guardado = baseDatos.guardarRegistro(
                                tipo = "SALIDA",
                                fechaHora = fechaHora
                            )

                            if (guardado) {
                                asistenciaViewModel.actualizarSalida(
                                    fechaVisible
                                )
                            } else {
                                asistenciaViewModel.mostrarMensaje(
                                    "No se pudo guardar la salida."
                                )
                            }

                        } else {
                            asistenciaViewModel.mostrarMensaje(
                                "La jornada ya finalizó. No puedes registrar otra entrada aquí."
                            )
                        }
                    } catch (e: Exception) {
                        asistenciaViewModel.mostrarMensaje(
                            "Ocurrió un error al guardar el registro."
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = asistenciaViewModel.puedeRegistrar()
            ) {
                Text(
                    text = if (jornadaIniciada) {
                        "Registrar salida"
                    } else {
                        "Registrar entrada"
                    }
                )
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedButton(
                onClick = alIrEvidencia,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar a evidencia fotográfica")
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al menú principal")
            }
        }
    }
}
