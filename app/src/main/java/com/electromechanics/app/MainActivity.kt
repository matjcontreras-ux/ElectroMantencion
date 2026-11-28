package com.electromechanics.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.electromechanics.app.data.BaseDatosLocal
import com.electromechanics.app.data.remote.LoginResponse
import com.electromechanics.app.data.remote.RetrofitClient
import com.electromechanics.app.data.remote.UsuarioRequest
import com.electromechanics.app.ui.screens.PantallaAsistencia
import com.electromechanics.app.ui.screens.PantallaDetalleServicio
import com.electromechanics.app.ui.screens.PantallaEvidencia
import com.electromechanics.app.ui.screens.PantallaPerfil
import com.electromechanics.app.ui.screens.PantallaPrincipal
import com.electromechanics.app.ui.screens.PantallaServicios
import com.electromechanics.app.ui.screens.PantallaUbicacion
import com.electromechanics.app.ui.theme.ElectroMantencionTheme
import com.electromechanics.app.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ElectroMantencionTheme {
                NavegacionElectroMantencion()
            }
        }
    }
}

@Composable
fun NavegacionElectroMantencion() {

    val loginViewModel: LoginViewModel = viewModel()

    var pantallaActual by rememberSaveable {
        mutableStateOf("login")
    }

    var usuario by rememberSaveable {
        mutableStateOf("")
    }

    var codigoServicioSeleccionado by rememberSaveable {
        mutableStateOf("OT-001")
    }

    when (pantallaActual) {

        "login" -> {
            PantallaLogin(
                loginViewModel = loginViewModel,
                alCambiarUsuario = { nuevoUsuario ->
                    usuario = nuevoUsuario
                },
                alIngresar = { respuesta ->
                    usuario = respuesta.nombre
                    pantallaActual = "principal"
                },
                alIrARegistro = {
                    pantallaActual = "registro"
                }
            )
        }

        "registro" -> {
            PantallaRegistro(
                alRegistroExitoso = { rutRegistrado ->
                    loginViewModel.cambiarUsuario(rutRegistrado)
                    loginViewModel.cambiarContrasena("")
                    pantallaActual = "login"
                },
                alVolverLogin = {
                    pantallaActual = "login"
                }
            )
        }

        "principal" -> {
            PantallaPrincipal(
                nombreTecnico = usuario,
                alVerServicios = {
                    pantallaActual = "servicios"
                },
                alRegistrarAsistencia = {
                    pantallaActual = "asistencia"
                },
                alVerPerfil = {
                    pantallaActual = "perfil"
                },
                alRegistrarUbicacion = {
                    pantallaActual = "ubicacion"
                },
                alCerrarSesion = {
                    usuario = ""
                    loginViewModel.cambiarUsuario("")
                    loginViewModel.cambiarContrasena("")
                    pantallaActual = "login"
                }
            )
        }

        "servicios" -> {
            PantallaServicios(
                alVolver = {
                    pantallaActual = "principal"
                },
                alVerDetalle = { codigoServicio ->
                    codigoServicioSeleccionado = codigoServicio
                    pantallaActual = "detalle"
                }
            )
        }

        "detalle" -> {
            PantallaDetalleServicio(
                codigoServicio = codigoServicioSeleccionado,
                alVolver = {
                    pantallaActual = "servicios"
                },
                alRegistrarAsistencia = {
                    pantallaActual = "asistencia"
                }
            )
        }

        "asistencia" -> {
            PantallaAsistencia(
                alVolver = {
                    pantallaActual = "principal"
                },
                alIrEvidencia = {
                    pantallaActual = "evidencia"
                }
            )
        }

        "evidencia" -> {
            PantallaEvidencia(
                alVolver = {
                    pantallaActual = "principal"
                }
            )
        }

        "ubicacion" -> {
            PantallaUbicacion(
                alVolver = {
                    pantallaActual = "principal"
                }
            )
        }

        "perfil" -> {
            PantallaPerfil(
                nombreTecnico = usuario,
                alVolver = {
                    pantallaActual = "principal"
                }
            )
        }
    }
}

@Composable
fun PantallaLogin(
    alCambiarUsuario: (String) -> Unit,
    alIngresar: (LoginResponse) -> Unit,
    alIrARegistro: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {

    var mensajeInformativo by rememberSaveable {
        mutableStateOf("")
    }

    val mensaje = loginViewModel.mensaje.ifEmpty {
        mensajeInformativo
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { espacio ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "ElectroMantencion",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingeniería y mantenimiento industrial",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = loginViewModel.usuario,
                onValueChange = { nuevoUsuario ->
                    loginViewModel.cambiarUsuario(nuevoUsuario)
                    alCambiarUsuario(nuevoUsuario)
                    mensajeInformativo = ""
                },
                label = {
                    Text("Usuario o RUT")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = loginViewModel.mensaje.startsWith("Ingresa tu usuario") ||
                        loginViewModel.mensaje.startsWith("El usuario")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = loginViewModel.contrasena,
                onValueChange = { nuevaContrasena ->
                    loginViewModel.cambiarContrasena(nuevaContrasena)
                    mensajeInformativo = ""
                },
                label = {
                    Text("Contraseña")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = loginViewModel.mensaje.startsWith("Ingresa tu contraseña") ||
                        loginViewModel.mensaje.startsWith("La contraseña")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    mensajeInformativo = ""

                    loginViewModel.iniciarSesion { respuesta ->
                        alIngresar(respuesta)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loginViewModel.cargando
            ) {
                Text(
                    if (loginViewModel.cargando) {
                        "Ingresando..."
                    } else {
                        "Ingresar"
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = alIrARegistro) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            TextButton(
                onClick = {
                    mensajeInformativo =
                        "La recuperación de contraseña se implementará más adelante."
                }
            ) {
                Text("¿Olvidaste tu contraseña?")
            }
        }
    }
}

@Composable
fun PantallaRegistro(
    alRegistroExitoso: (String) -> Unit,
    alVolverLogin: () -> Unit
) {
    val context = LocalContext.current
    val baseDatos = remember { BaseDatosLocal(context) }
    val scope = rememberCoroutineScope()

    var rut by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf("TECNICO") }
    var mensajeError by rememberSaveable { mutableStateOf("") }
    var registrando by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { espacio ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = rut,
                onValueChange = {
                    rut = it
                    mensajeError = ""
                },
                label = { Text("RUT (ej: 12345678-9)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mensajeError = ""
                },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    mensajeError = ""
                },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = {
                    clave = it
                    mensajeError = ""
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selecciona tu rol:",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = rol == "TECNICO",
                    onClick = { rol = "TECNICO" },
                    label = { Text("Técnico") }
                )
                FilterChip(
                    selected = rol == "ADMINISTRADOR",
                    onClick = { rol = "ADMINISTRADOR" },
                    label = { Text("Administrador") }
                )
            }

            if (mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    when {
                        rut.isBlank() || nombre.isBlank() || correo.isBlank() || clave.isBlank() -> {
                            mensajeError = "Todos los campos son obligatorios."
                        }
                        !correo.contains("@") -> {
                            mensajeError = "Ingresa un correo válido."
                        }
                        clave.length < 4 -> {
                            mensajeError = "La contraseña debe tener al menos 4 caracteres."
                        }
                        else -> {
                            registrando = true
                            mensajeError = ""

                            scope.launch {
                                try {
                                    val request = UsuarioRequest(
                                        rut = rut.trim(),
                                        nombre = nombre.trim(),
                                        correo = correo.trim(),
                                        contrasena = clave,
                                        rol = rol,
                                        especialidad = if (rol == "TECNICO") "General" else null
                                    )

                                    val response = RetrofitClient.usuarios.registrarUsuario(request)

                                    if (response.isSuccessful) {
                                        baseDatos.registrarUsuarioLocal(
                                            rut = rut,
                                            nombre = nombre,
                                            clave = clave,
                                            rol = rol
                                        )
                                        registrando = false
                                        alRegistroExitoso(rut.trim())
                                    } else {
                                        registrando = false
                                        mensajeError = "No se pudo registrar (RUT o correo ya existe)."
                                    }
                                } catch (e: Exception) {
                                    val guardadoLocal = baseDatos.registrarUsuarioLocal(
                                        rut = rut,
                                        nombre = nombre,
                                        clave = clave,
                                        rol = rol
                                    )
                                    registrando = false
                                    if (guardadoLocal) {
                                        alRegistroExitoso(rut.trim())
                                    } else {
                                        mensajeError = "Ese RUT ya está registrado localmente."
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !registrando
            ) {
                Text(if (registrando) "Registrando..." else "Registrarme")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = alVolverLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}