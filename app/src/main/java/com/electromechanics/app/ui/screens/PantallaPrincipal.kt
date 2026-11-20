package com.electromechanics.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    nombreTecnico: String = "Técnico",
    alVerServicios: () -> Unit = {},
    alRegistrarAsistencia: () -> Unit = {},
    alVerPerfil: () -> Unit = {},
    alRegistrarUbicacion: () -> Unit = {},
    alCerrarSesion: () -> Unit = {}
) {
    // 1. Variables de Ubicación
    val contexto = LocalContext.current
    val clienteUbicacion = remember { LocationServices.getFusedLocationProviderClient(contexto) }
    var textoUbicacion by remember { mutableStateOf("Obtén las coordenadas del lugar donde realizas el servicio.") }

    // 2. Lanzador de permisos de GPS
    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val permisoConcedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permisoConcedido) {
            try {
                clienteUbicacion.lastLocation.addOnSuccessListener { ubicacion ->
                    if (ubicacion != null) {
                        textoUbicacion = "Lat: ${ubicacion.latitude}, Lon: ${ubicacion.longitude}"
                    } else {
                        textoUbicacion = "GPS sin señal. Abre Google Maps y reintenta."
                    }
                }
            } catch (e: SecurityException) {
                textoUbicacion = "Error de seguridad con el GPS"
            }
        } else {
            textoUbicacion = "Permiso denegado. No se puede obtener ubicación."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("ElectroMantencion")
                }
            )
        }
    ) { espacio ->

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 700)
            ) + slideInVertically(
                animationSpec = tween(durationMillis = 700),
                initialOffsetY = { altura -> altura / 8 }
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "¡Bienvenido, $nombreTecnico!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Panel principal del técnico",
                    style = MaterialTheme.typography.bodyLarge
                )

                ResumenJornada()

                Text(
                    text = "Mis funciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TarjetaFuncion(
                    titulo = "Servicios asignados",
                    descripcion = "Consulta tus órdenes de trabajo y revisa su estado.",
                    textoBoton = "Ver servicios",
                    alPresionar = alVerServicios
                )

                TarjetaFuncion(
                    titulo = "Registro de asistencia",
                    descripcion = "Registra tu entrada y salida de la jornada.",
                    textoBoton = "Registrar asistencia",
                    alPresionar = alRegistrarAsistencia
                )

                // 3. Tarjeta de Ubicación actualizada con el texto dinámico y el lanzador
                TarjetaFuncion(
                    titulo = "Ubicación del técnico",
                    descripcion = textoUbicacion,
                    textoBoton = "Registrar ubicación",
                    alPresionar = {
                        lanzadorPermisos.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                        alRegistrarUbicacion()
                    }
                )

                TarjetaFuncion(
                    titulo = "Mi perfil",
                    descripcion = "Consulta tu información como técnico.",
                    textoBoton = "Ver perfil",
                    alPresionar = alVerPerfil
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = alCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
fun ResumenJornada() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen de jornada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Estado: jornada no iniciada")

            Text("Servicios pendientes: por consultar")
        }
    }
}

@Composable
fun TarjetaFuncion(
    titulo: String,
    descripcion: String,
    textoBoton: String,
    alPresionar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = alPresionar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(textoBoton)
            }
        }
    }
}