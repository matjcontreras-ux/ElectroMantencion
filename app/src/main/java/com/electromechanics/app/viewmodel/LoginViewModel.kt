package com.electromechanics.app.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.electromechanics.app.data.BaseDatosLocal
import com.electromechanics.app.data.remote.LoginRequest
import com.electromechanics.app.data.remote.LoginResponse
import com.electromechanics.app.data.remote.RetrofitClient
import com.electromechanics.app.validation.Validaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatosLocal = BaseDatosLocal(application)

    var usuario by mutableStateOf("")
        private set

    var contrasena by mutableStateOf("")
        private set

    var mensaje by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    var usuarioAutenticado by mutableStateOf<LoginResponse?>(null)
        private set

    fun cambiarUsuario(nuevoUsuario: String) {
        usuario = nuevoUsuario
        mensaje = ""
    }

    fun cambiarContrasena(nuevaContrasena: String) {
        contrasena = nuevaContrasena
        mensaje = ""
    }

    fun validarIngreso(): Boolean {
        val errorUsuario = Validaciones.validarUsuario(usuario)
        if (errorUsuario != null) {
            mensaje = errorUsuario
            return false
        }

        val errorContrasena = Validaciones.validarContrasena(contrasena)
        if (errorContrasena != null) {
            mensaje = errorContrasena
            return false
        }

        mensaje = ""
        return true
    }

    fun registrarUsuarioLocal(
        rut: String,
        nombre: String,
        clave: String,
        rol: String = "TECNICO"
    ): Boolean {
        return baseDatosLocal.registrarUsuarioLocal(rut, nombre, clave, rol)
    }

    fun iniciarSesion(onSuccess: (LoginResponse) -> Unit) {
        if (cargando || !validarIngreso()) return

        viewModelScope.launch {
            cargando = true
            mensaje = ""

            try {
                val respuesta = withContext(Dispatchers.IO) {
                    RetrofitClient.usuarios.iniciarSesion(
                        LoginRequest(
                            rut = usuario.trim(),
                            contrasena = contrasena
                        )
                    )
                }

                if (respuesta.isSuccessful) {
                    val datosUsuario = respuesta.body()

                    if (datosUsuario != null && datosUsuario.activo) {
                        // Sincronizar credenciales en SQLite para acceso offline
                        withContext(Dispatchers.IO) {
                            baseDatosLocal.registrarUsuarioLocal(
                                rut = datosUsuario.rut,
                                nombre = datosUsuario.nombre,
                                clave = contrasena,
                                rol = datosUsuario.rol
                            )
                        }
                        usuarioAutenticado = datosUsuario
                        mensaje = ""
                        onSuccess(datosUsuario)
                    } else {
                        mensaje = "El servidor respondió, pero no entregó un usuario activo."
                    }
                } else {
                    mensaje = when (respuesta.code()) {
                        400 -> "Solicitud incorrecta. Revisa los datos."
                        401 -> "RUT o contraseña incorrectos."
                        403 -> "El usuario no tiene autorización."
                        404 -> "No se encontró el servicio de inicio de sesión."
                        500 -> "Error interno del servidor."
                        else -> "Error del servidor. Código: ${respuesta.code()}"
                    }
                }
            } catch (e: IOException) {
                // Respaldo offline usando SQLite local si el servidor no responde
                val usuarioLocal = withContext(Dispatchers.IO) {
                    baseDatosLocal.validarLoginLocal(usuario.trim(), contrasena)
                }

                if (usuarioLocal != null) {
                    val (nombreLocal, rolLocal) = usuarioLocal
                    val datosOffline = LoginResponse(
                        id = "local-${usuario.trim()}",
                        rut = usuario.trim(),
                        nombre = nombreLocal,
                        correo = "${usuario.trim()}@local.cl",
                        rol = rolLocal,
                        especialidad = null,
                        activo = true
                    )
                    usuarioAutenticado = datosOffline
                    mensaje = ""
                    onSuccess(datosOffline)
                } else {
                    mensaje = "Sin conexión al servidor y credenciales no encontradas en SQLite local."
                }
            } catch (e: Exception) {
                mensaje = "Error al procesar la respuesta: ${e.localizedMessage}"
            } finally {
                cargando = false
            }
        }
    }
}