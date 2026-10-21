
package com.electromechanics.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleServicio(
    codigoServicio: String,
    alVolver: () -> Unit,
    alRegistrarAsistencia: () -> Unit
) {

    val titulo: String
    val ubicacion: String
    val descripcion: String
    val tareas: List<String>

    when (codigoServicio) {

        "OT-001" -> {
            titulo = "Mantención de tablero eléctrico"
            ubicacion = "Planta industrial, sector A"
            descripcion =
                "Revisión preventiva del tablero eléctrico y sus componentes."
            tareas = listOf(
                "Inspeccionar visualmente el tablero.",
                "Revisar el estado de los componentes.",
                "Comprobar las conexiones según el procedimiento autorizado.",
                "Registrar los resultados de la inspección."
            )
        }

        "OT-002" -> {
            titulo = "Inspección de motores"
            ubicacion = "Planta industrial, sector B"
            descripcion =
                "Inspección general de motores eléctricos de la instalación."
            tareas = listOf(
                "Verificar el estado exterior del motor.",
                "Revisar posibles ruidos o vibraciones.",
                "Registrar las observaciones encontradas.",
                "Informar cualquier anomalía al supervisor."
            )
        }

        "OT-003" -> {
            titulo = "Revisión de sistema de iluminación"
            ubicacion = "Bodega principal"
            descripcion =
                "Inspección del sistema de iluminación de la bodega."
            tareas = listOf(
                "Revisar el funcionamiento de las luminarias.",
                "Identificar luces que no funcionen.",
                "Registrar los puntos que requieren mantenimiento.",
                "Informar los resultados de la revisión."
            )
        }

        else -> {
            titulo = "Servicio no identificado"
            ubicacion = "Sin ubicación registrada"
            descripcion = "No se encontró información para esta orden."
            tareas = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle del servicio")
                }
            )
        }
    ) { espacio ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = codigoServicio,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = titulo,
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
                        text = "Información de la orden",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Ubicación: $ubicacion")

                    Text("Estado: asignado")
                }
            }

            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(text = descripcion)

            Text(
                text = "Tareas del servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            tareas.forEachIndexed { indice, tarea ->
                Text(
                    text = "${indice + 1}. $tarea",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = alRegistrarAsistencia,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar al registro de asistencia")
            }

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a servicios")
            }
        }
    }
}
