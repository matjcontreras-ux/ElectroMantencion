
package com.electromechanics.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.electromechanics.app.validation.Validaciones

class PerfilViewModel : ViewModel() {

    var nombreTecnico by mutableStateOf("")
        private set

    var correo by mutableStateOf("")
        private set

    var mensaje by mutableStateOf("")
        private set

    fun actualizarNombre(nombre: String) {
        nombreTecnico = nombre
        mensaje = ""
    }

    fun actualizarCorreo(nuevoCorreo: String) {
        correo = nuevoCorreo
        mensaje = ""
    }

    fun guardarPerfil() {
        val errorNombre = Validaciones.validarCampoObligatorio(
            nombreTecnico,
            "nombre"
        )

        if (errorNombre != null) {
            mensaje = errorNombre
            return
        }

        val errorCorreo = Validaciones.validarCampoObligatorio(
            correo,
            "correo"
        )

        if (errorCorreo != null) {
            mensaje = errorCorreo
            return
        }

        if (!correo.contains("@") || !correo.contains(".")) {
            mensaje = "Ingresa un correo electrónico válido."
            return
        }

        mensaje = "Perfil validado correctamente."
    }
}
