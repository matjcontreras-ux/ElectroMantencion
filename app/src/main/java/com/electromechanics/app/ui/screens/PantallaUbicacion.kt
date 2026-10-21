
package com.electromechanics.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.electromechanics.app.data.BaseDatosLocal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUbicacion(
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current

    val baseDatos = remember(contexto) {
        BaseDatosLocal(contexto.applicationContext)
    }

    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var buscando by remember { mutableStateOf(false) }
    var solicitudPendiente by remember { mutableStateOf(false) }

    val administradorUbicacion = remember(contexto) {
        contexto.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    val obtenerUbicacion: () -> Unit = obtener@{
        val permisoPreciso =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val permisoAproximado =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!permisoPreciso && !permisoAproximado) {
            mensaje = "Debes autorizar el permiso de ubicación."
            buscando = false
            return@obtener
        }

        try {
            val proveedor = when {
                permisoPreciso &&
                        administradorUbicacion.isProviderEnabled(
                            LocationManager.GPS_PROVIDER
                        ) -> LocationManager.GPS_PROVIDER

                administradorUbicacion.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                ) -> LocationManager.NETWORK_PROVIDER

                permisoPreciso -> LocationManager.GPS_PROVIDER

                else -> {
                    mensaje =
                        "Activa la ubicación del dispositivo e inténtalo nuevamente."
                    buscando = false
                    return@obtener
                }
            }

            if (!administradorUbicacion.isProviderEnabled(proveedor)) {
                mensaje =
                    "Activa la ubicación del dispositivo e inténtalo nuevamente."
                buscando = false
                return@obtener
            }

            buscando = true
            mensaje = "Buscando ubicación..."

            val receptorUbicacion = object : LocationListener {

                override fun onLocationChanged(ubicacion: Location) {
                    latitud = ubicacion.latitude.toString()
                    longitud = ubicacion.longitude.toString()

                    try {
                        val fechaHora = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm:ss"
                            )
                        )

                        val guardado = baseDatos.guardarRegistro(
                            tipo = "UBICACION",
                            fechaHora = fechaHora,
                            latitud = ubicacion.latitude,
                            longitud = ubicacion.longitude
                        )

                        mensaje = if (guardado) {
                            "Ubicación obtenida y guardada correctamente."
                        } else {
                            "No se pudo guardar la ubicación en la base de datos."
                        }
                    } catch (e: Exception) {
                        mensaje = "Error al guardar la ubicación."
                    }

                    buscando = false
                    administradorUbicacion.removeUpdates(this)
                }

                @Deprecated("Método antiguo de LocationListener")
                override fun onStatusChanged(
                    proveedor: String?,
                    estado: Int,
                    extras: android.os.Bundle?
                ) {
                }

                override fun onProviderEnabled(proveedor: String) {
                }

                override fun onProviderDisabled(proveedor: String) {
                    mensaje = "La ubicación se desactivó."
                    buscando = false
                }
            }

            administradorUbicacion.requestSingleUpdate(
                proveedor,
                receptorUbicacion,
                Looper.getMainLooper()
            )

        } catch (e: SecurityException) {
            mensaje = "No hay permiso para consultar la ubicación."
            buscando = false
        } catch (e: Exception) {
            mensaje =
                "No se pudo obtener la ubicación. Revisa la configuración del dispositivo."
            buscando = false
        }
    }

    val lanzadorPermisos =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->

            val autorizado =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (autorizado && solicitudPendiente) {
                solicitudPendiente = false
                obtenerUbicacion()
            } else {
                solicitudPendiente = false
                buscando = false
                mensaje = "No se autorizó el permiso de ubicación."
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ubicación del técnico")
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
                text = "Registrar ubicación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Obtén las coordenadas del lugar donde te encuentras."
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Coordenadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (latitud.isEmpty()) {
                            "Latitud: sin obtener"
                        } else {
                            "Latitud: $latitud"
                        }
                    )

                    Text(
                        text = if (longitud.isEmpty()) {
                            "Longitud: sin obtener"
                        } else {
                            "Longitud: $longitud"
                        }
                    )
                }
            }

            Button(
                onClick = {
                    val tienePermiso =
                        ContextCompat.checkSelfPermission(
                            contexto,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(
                                    contexto,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                    if (tienePermiso) {
                        obtenerUbicacion()
                    } else {
                        solicitudPendiente = true

                        lanzadorPermisos.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = !buscando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (buscando) {
                        "Buscando ubicación..."
                    } else {
                        "Obtener mi ubicación"
                    }
                )
            }

            if (mensaje.isNotEmpty()) {
                val esError =
                    mensaje.startsWith("No se") ||
                            mensaje.startsWith("No hay") ||
                            mensaje.startsWith("No se autorizó") ||
                            mensaje.startsWith("Debes") ||
                            mensaje.startsWith("Activa") ||
                            mensaje.startsWith("La ubicación") ||
                            mensaje.startsWith("Error:")

                Text(
                    text = mensaje,
                    color = if (esError) {
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
