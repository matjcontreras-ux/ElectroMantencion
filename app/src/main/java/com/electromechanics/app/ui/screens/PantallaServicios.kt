
package com.electromechanics.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaServicios(
    alVolver: () -> Unit = {},
    alVerDetalle: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Servicios asignados")
                },
                navigationIcon = {
                    TextButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                        Text(" Volver")
                    }
                }
            )
        }
    ) { espacio ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Servicios",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Mis órdenes de trabajo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Revisa los servicios pendientes y selecciona uno para consultar sus detalles."
            )

            TarjetaServicio(
                codigo = "OT-001",
                titulo = "Mantención de tablero eléctrico",
                ubicacion = "Planta industrial, sector A",
                estado = "Pendiente",
                alSeleccionar = {
                    alVerDetalle("OT-001")
                }
            )

            TarjetaServicio(
                codigo = "OT-002",
                titulo = "Inspección de motores",
                ubicacion = "Planta industrial, sector B",
                estado = "En proceso",
                alSeleccionar = {
                    alVerDetalle("OT-002")
                }
            )

            TarjetaServicio(
                codigo = "OT-003",
                titulo = "Revisión de sistema de iluminación",
                ubicacion = "Bodega principal",
                estado = "Pendiente",
                alSeleccionar = {
                    alVerDetalle("OT-003")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
                Text(" Volver al menú principal")
            }
        }
    }
}

@Composable
fun TarjetaServicio(
    codigo: String,
    titulo: String,
    ubicacion: String,
    estado: String,
    alSeleccionar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = codigo,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación"
                )
                Text(text = ubicacion)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (estado == "En proceso") {
                        Icons.Default.Build
                    } else {
                        Icons.Default.PendingActions
                    },
                    contentDescription = "Estado del servicio",
                    tint = if (estado == "En proceso") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.tertiary
                    }
                )

                Text(
                    text = "Estado: $estado",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = alSeleccionar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null
                )
                Text(" Ver detalle")
            }
        }
    }
}
