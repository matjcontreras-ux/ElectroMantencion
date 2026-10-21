
package com.electromechanics.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.electromechanics.app.validation.Validaciones

class LoginViewModel : ViewModel() {

    var usuario by mutableStateOf("")
        private set

    var contrasena by mutableStateOf("")
        private set

    var mensaje by mutableStateOf("")
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

        val errorContrasena =
            Validaciones.validarContrasena(contrasena)

        if (errorContrasena != null) {
            mensaje = errorContrasena
            return false
        }

        mensaje = ""
        return true
    }
}
