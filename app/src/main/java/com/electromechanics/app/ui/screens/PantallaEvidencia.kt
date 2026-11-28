package com.electromechanics.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun obtenerCarpetaEvidencia(context: Context): File {
    val carpeta = File(context.filesDir, "evidencia")
    if (!carpeta.exists()) {
        carpeta.mkdirs()
    }
    return carpeta
}

private fun guardarFotoLocal(context: Context, bitmap: Bitmap): String? {
    return try {
        val carpeta = obtenerCarpetaEvidencia(context)
        val marcaTiempo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val archivo = File(carpeta, "evidencia_$marcaTiempo.jpg")
        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
        }
        archivo.name
    } catch (e: Exception) {
        null
    }
}

private fun cargarUltimaFotoLocal(context: Context): Pair<Bitmap, String>? {
    return try {
        val carpeta = obtenerCarpetaEvidencia(context)
        val ultimaFoto = carpeta.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".jpg") }
            ?.maxByOrNull { it.lastModified() }

        if (ultimaFoto != null) {
            val bitmap = BitmapFactory.decodeFile(ultimaFoto.absolutePath)
            if (bitmap != null) Pair(bitmap, ultimaFoto.name) else null
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

private fun contarFotosGuardadas(context: Context): Int {
    val carpeta = obtenerCarpetaEvidencia(context)
    return carpeta.listFiles()?.count { it.isFile && it.name.endsWith(".jpg") } ?: 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEvidencia(
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val prefs = remember {
        contexto.getSharedPreferences("evidencia_prefs", Context.MODE_PRIVATE)
    }

    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var mensaje by rememberSaveable {
        mutableStateOf("")
    }

    var nombreArchivoActual by rememberSaveable {
        mutableStateOf("")
    }

    var totalFotos by rememberSaveable {
        mutableStateOf(0)
    }

    var evidenciaRegistrada by rememberSaveable {
        mutableStateOf(false)
    }

    var fotografia by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // Al abrir la pantalla, recupera la última foto de la carpeta /evidencia
    LaunchedEffect(Unit) {
        val resultado = cargarUltimaFotoLocal(contexto)
        if (resultado != null) {
            fotografia = resultado.first
            nombreArchivoActual = resultado.second
        }
        totalFotos = contarFotosGuardadas(contexto)

        val descGuardada = prefs.getString("descripcion_evidencia", "") ?: ""
        val registradaGuardada = prefs.getBoolean("evidencia_registrada", false)
        if (descGuardada.isNotEmpty()) {
            descripcion = descGuardada
        }
        evidenciaRegistrada = registradaGuardada
    }

    val lanzadorCamara =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { imagen ->
            if (imagen != null) {
                fotografia = imagen
                val nombreGuardado = guardarFotoLocal(contexto, imagen)
                if (nombreGuardado != null) {
                    nombreArchivoActual = nombreGuardado
                    totalFotos = contarFotosGuardadas(contexto)
                }
                evidenciaRegistrada = false
                prefs.edit().putBoolean("evidencia_registrada", false).apply()
                mensaje = "Fotografía guardada en carpeta /evidencia ($nombreArchivoActual)."
            } else {
                mensaje = "No se tomó ninguna fotografía."
            }
        }

    val lanzadorPermisoCamara =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (concedido) {
                lanzadorCamara.launch(null)
            } else {
                mensaje = "Error: debes conceder permiso para usar la cámara."
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
                text = "Toma una fotografía y describe el trabajo realizado. Fotos guardadas en /evidencia: $totalFotos"
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
                        if (nombreArchivoActual.isNotEmpty()) {
                            Text(
                                text = "Archivo: evidencia/$nombreArchivoActual",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Text(
                            text = "Todavía no has tomado una fotografía."
                        )
                    }

                    Button(
                        onClick = {
                            val permisoOtorgado = ContextCompat.checkSelfPermission(
                                contexto,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (permisoOtorgado) {
                                lanzadorCamara.launch(null)
                            } else {
                                lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
                            }
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
                            prefs.edit()
                                .putString("descripcion_evidencia", descripcion.trim())
                                .putBoolean("evidencia_registrada", true)
                                .apply()
                            evidenciaRegistrada = true
                            mensaje =
                                "Evidencia registrada correctamente en carpeta /evidencia."
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
                        Text("Ruta: /evidencia/$nombreArchivoActual ($totalFotos fotos guardadas)")
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