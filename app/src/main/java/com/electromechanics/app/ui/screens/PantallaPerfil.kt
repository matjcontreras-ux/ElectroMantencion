
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    nombreTecnico: String,
    alVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") }
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
                text = "Información del técnico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Nombre de usuario",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = nombreTecnico.ifBlank { "Técnico" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Cargo: Técnico de mantenimiento"
                    )

                    Text(
                        text = "Empresa: ElectroMantencion"
                    )

                    Text(
                        text = "Cuenta: Sesión de demostración"
                    )
                }
            }

            Text(
                text = "Mi actividad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Servicios asignados: 3")
                    Text("Servicios completados: por registrar")
                    Text("Última asistencia: por registrar")
                }
            }

            Text(
                text = "Los datos de actividad son informativos y de demostración."
            )

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
