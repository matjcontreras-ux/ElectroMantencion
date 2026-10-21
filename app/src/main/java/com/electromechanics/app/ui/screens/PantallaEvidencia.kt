
package com.electromechanics.app.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEvidencia(
    alVolver: () -> Unit
) {
    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var mensaje by rememberSaveable {
        mutableStateOf("")
    }

    var evidenciaRegistrada by rememberSaveable {
        mutableStateOf(false)
    }

    var fotografia by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val lanzadorCamara =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { imagen ->
            if (imagen != null) {
                fotografia = imagen
                evidenciaRegistrada = false
                mensaje = "Fotografía tomada correctamente."
            } else {
                mensaje = "No se tomó ninguna fotografía."
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Evidencia fotográfica")
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
                text = "Documentar trabajo realizado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Toma una fotografía y describe el trabajo realizado."
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Fotografía de evidencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (fotografia != null) {
                        Image(
                            bitmap = fotografia!!.asImageBitmap(),
                            contentDescription =
                                "Fotografía del trabajo realizado",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    } else {
                        Text(
                            text = "Todavía no has tomado una fotografía."
                        )
                    }

                    Button(
                        onClick = {
                            lanzadorCamara.launch(null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Abrir cámara")
                    }
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    evidenciaRegistrada = false
                    mensaje = ""
                },
                label = {
                    Text("Descripción del trabajo")
                },
                placeholder = {
                    Text("Describe las tareas realizadas")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = mensaje.startsWith("Error:")
            )

            Button(
                onClick = {
                    when {
                        fotografia == null -> {
                            mensaje =
                                "Error: primero debes tomar una fotografía."
                        }

                        descripcion.isBlank() -> {
                            mensaje =
                                "Error: escribe una descripción del trabajo."
                        }

                        else -> {
                            evidenciaRegistrada = true
                            mensaje =
                                "Evidencia registrada correctamente en pantalla."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar evidencia")
            }

            if (evidenciaRegistrada) {
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
                            text = "Evidencia completada",
                            fontWeight = FontWeight.Bold
                        )

                        Text("Descripción: $descripcion")
                        Text("Fotografía: adjunta en esta sesión")
                    }
                }
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = if (mensaje.startsWith("Error:")) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
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
